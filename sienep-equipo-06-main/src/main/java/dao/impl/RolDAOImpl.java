package dao.impl;

import dao.GenericDAO;
import db.ConfiguracionSistema;
import model.Permiso;
import model.Rol;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolDAOImpl implements GenericDAO<Rol> {
    private static final Logger logger = Logger.getLogger(RolDAOImpl.class);
    private final Connection connection;

    public RolDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
    }
    // constructor para inyección de dependencias para las pruebas unitarias
    public RolDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Rol rol) throws Exception {
        String sql = "INSERT INTO proyecto.roles (nombre) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rol.getNombre());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                logger.warn("No se insertó el rol (posible conflicto de nombre): " + rol.getNombre());
                return;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    rol.setId(rs.getInt(1));
                }
            }
            if (rol.getPermisos() != null && !rol.getPermisos().isEmpty()) {
                savePermisos(rol);
            }
            logger.info("Rol guardado: " + rol.getNombre());
        }
    }

    @Override
    public Rol findById(int id) throws Exception {
        String sql = "SELECT id, nombre FROM proyecto.roles WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    logger.warn("Rol no encontrado ID=" + id);
                    return null;
                }

                return new Rol(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        loadPermisos(id)
                );
            }
        }
    }

    public Rol findByNombre(String nombre) throws Exception {
        String sql = "SELECT id, nombre FROM proyecto.roles WHERE nombre = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    logger.warn("Rol no encontrado: " + nombre);
                    return null;
                }
                int id = rs.getInt("id");
                return new Rol(id, nombre, loadPermisos(id));
            }
        }
    }

    // método para verificar si un rol tiene funcionarios asociados
    public boolean rolTieneFuncionarios(int idRol) throws Exception {
        String sql = "SELECT 1 FROM proyecto.funcionarios WHERE id_rol = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<Rol> findAll() throws Exception {
        String sql = "SELECT id, nombre FROM proyecto.roles";

        List<Rol> roles = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        new ArrayList<>()
                );
                roles.add(rol);
            }
        }
        if (roles.isEmpty()) return roles;
        loadPermisosMasivo(roles);
        logger.info("Roles cargados: " + roles.size());
        return roles;
    }

    @Override
    public void update(Rol rol) throws Exception {
        String sql = "UPDATE proyecto.roles SET nombre = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, rol.getNombre());
            ps.setInt(2, rol.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                logger.warn("No existe rol para actualizar ID=" + rol.getId());
                return;
            }

            // Primero borramos permisos del rol, luego agregamos nuevos
            deletePermisos(rol.getId());

            if (rol.getPermisos() != null && !rol.getPermisos().isEmpty()) {
                savePermisos(rol);
            }

            logger.info("Rol actualizado ID=" + rol.getId());
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM proyecto.roles WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                logger.warn("No existe rol para eliminar ID=" + id);
                return;
            }

            logger.info("Rol eliminado ID=" + id);
        }
    }

    // metodos auxiliares
    // Este método asocia los permisos al rol en la tabla rol_permisos
    private void savePermisos(Rol rol) throws SQLException {
        String query = "INSERT INTO proyecto.rol_permisos (id_rol, id_permiso) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            for (Permiso permiso : rol.getPermisos()) {
                ps.setInt(1, rol.getId());
                ps.setInt(2, permiso.getId());
                ps.addBatch();
            }
            ps.executeBatch();
            logger.info("Permisos asociados al rol ID " + rol.getId());
        }
    }

    // Este método elimina todas las asociaciones de permisos para un rol dado
    private void deletePermisos(int idRol) throws SQLException {
        String query = "DELETE FROM proyecto.rol_permisos WHERE id_rol = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idRol);
            ps.executeUpdate();
            logger.info("Permisos eliminados para rol ID " + idRol);
        }
    }

    // Este método carga los permisos asociados a un rol
        protected List<Permiso> loadPermisos(int idRol) throws Exception {
        String query = "SELECT p.id, p.nombre FROM proyecto.permisos p " +
                "JOIN proyecto.rol_permisos rp ON p.id = rp.id_permiso " +
                "WHERE rp.id_rol = ?";
        List<Permiso> permisos = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    permisos.add(new Permiso(rs.getInt("id"), rs.getString("nombre")));
                }
            }
        }
        return permisos;
    }

    private void loadPermisosMasivo(List<Rol> roles) throws Exception {
        Map<Integer, Rol> mapa = new HashMap<>();
        for (Rol r : roles) mapa.put(r.getId(), r);

        String sql = """
            SELECT rp.id_rol, p.id, p.nombre
            FROM proyecto.rol_permisos rp
            JOIN proyecto.permisos p ON p.id = rp.id_permiso
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idRol = rs.getInt("id_rol");

                Rol rol = mapa.get(idRol);
                if (rol != null) {
                    rol.getPermisos().add(new Permiso(
                            rs.getInt("id"),
                            rs.getString("nombre")
                    ));
                }
            }
        }
    }

    public void addPermisoToRol(int idRol, int idPermiso) throws Exception {
        // Verificar que el rol existe
        if (findById(idRol) == null) {
            throw new Exception("Rol no encontrado.");
        }


        String checkPermisoSql = "SELECT 1 FROM proyecto.permisos WHERE id = ?";
        try (PreparedStatement psCheck = connection.prepareStatement(checkPermisoSql)) {
            psCheck.setInt(1, idPermiso);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("Permiso no encontrado.");
                }
            }
        }

        // Verificar si ya está asignado
        String checkAsignadoSql = "SELECT 1 FROM proyecto.rol_permisos WHERE id_rol = ? AND id_permiso = ?";
        try (PreparedStatement psCheckAsignado = connection.prepareStatement(checkAsignadoSql)) {
            psCheckAsignado.setInt(1, idRol);
            psCheckAsignado.setInt(2, idPermiso);
            try (ResultSet rs = psCheckAsignado.executeQuery()) {
                if (rs.next()) {
                    throw new Exception("El permiso ya está asignado al rol.");
                }
            }
        }

        // Insertar la asociación
        String insertSql = "INSERT INTO proyecto.rol_permisos (id_rol, id_permiso) VALUES (?, ?)";
        try (PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
            psInsert.setInt(1, idRol);
            psInsert.setInt(2, idPermiso);
            psInsert.executeUpdate();
            logger.info("Permiso " + idPermiso + " agregado al rol " + idRol);
        }
    }

    public void removePermisoFromRol(int idRol, int idPermiso) throws Exception {
        // Verificar que el rol existe
        if (findById(idRol) == null) {
            throw new Exception("Rol no encontrado.");
        }

        // Verificar que el permiso existe
        String checkPermisoSql = "SELECT 1 FROM proyecto.permisos WHERE id = ?";
        try (PreparedStatement psCheck = connection.prepareStatement(checkPermisoSql)) {
            psCheck.setInt(1, idPermiso);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("Permiso no encontrado.");
                }
            }
        }

        // Verificar si está asignado
        String checkAsignadoSql = "SELECT 1 FROM proyecto.rol_permisos WHERE id_rol = ? AND id_permiso = ?";
        try (PreparedStatement psCheckAsignado = connection.prepareStatement(checkAsignadoSql)) {
            psCheckAsignado.setInt(1, idRol);
            psCheckAsignado.setInt(2, idPermiso);
            try (ResultSet rs = psCheckAsignado.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("El permiso no está asignado al rol.");
                }
            }
        }

        // Eliminar la asociación
        String deleteSql = "DELETE FROM proyecto.rol_permisos WHERE id_rol = ? AND id_permiso = ?";
        try (PreparedStatement psDelete = connection.prepareStatement(deleteSql)) {
            psDelete.setInt(1, idRol);
            psDelete.setInt(2, idPermiso);
            psDelete.executeUpdate();
            logger.info("Permiso " + idPermiso + " quitado del rol " + idRol);
        }
    }

}
