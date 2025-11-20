package dao.impl;

import dao.FuncionarioDAO;
import dao.GenericDAO;
import dao.UsuarioDAO;
import db.ConfiguracionSistema;
import model.Funcionario;
import model.Rol;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAOImpl implements FuncionarioDAO {
    private static final Logger logger = Logger.getLogger(FuncionarioDAOImpl.class);
    private final Connection connection;
    private final UsuarioDAO usuarioDAO;
    private final GenericDAO<Rol> rolDAO;


    public FuncionarioDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
        this.usuarioDAO = new UsuarioDAOImpl();
        this.rolDAO = new RolDAOImpl();
    }

    // constructor para pruebas unitarias
    public FuncionarioDAOImpl(Connection connection, UsuarioDAO usuarioDAO, GenericDAO<Rol> rolDAO) {
        this.connection = connection;
        this.usuarioDAO = usuarioDAO;
        this.rolDAO = rolDAO;
    }

    @Override
    public void assignRol(int idFuncionario, Rol rol) throws Exception {
        String query = "UPDATE proyecto.funcionarios SET id_rol = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, rol.getId());
            ps.setInt(2, idFuncionario);
            ps.executeUpdate();
            logger.info("Rol asignado a funcionario ID " + idFuncionario + ": " + rol.getNombre());
        } catch (SQLException e) {
            logger.error("Error asignando rol a funcionario ID " + idFuncionario, e);
            throw e;
        }
    }

    @Override
    public List<Funcionario> findByRol(String nombreRol) throws Exception {
        String query = "SELECT u.id FROM proyecto.usuarios u " +
                "JOIN proyecto.funcionarios f ON u.id = f.id " +
                "JOIN proyecto.roles r ON f.id_rol = r.id " +
                "WHERE r.nombre = ? AND u.tipo = 'FUNCIONARIO' AND u.estado = 'ACTIVO'";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nombreRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Funcionario f = findById(rs.getInt("id"));
                    if (f != null) {
                        funcionarios.add(f);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando funcionarios por rol: " + nombreRol, e);
            throw e;
        }
        logger.info("Funcionarios encontrados por rol '" + nombreRol + "': " + funcionarios.size());
        return funcionarios;
    }

    @Override
    public void save(Funcionario funcionario) throws Exception {
        connection.setAutoCommit(false);
        try {
            // PRIMERO: inserción en usuarios
            usuarioDAO.save(funcionario);

            // SEGUNDO: verificar que el rol existe
            if (funcionario.getRol() == null) {
                throw new SQLException("El funcionario debe tener un rol asignado");
            }

            // TERCERO: Inserta en funcionarios CON EL ROL
            String query = "INSERT INTO proyecto.funcionarios (id, id_rol) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, funcionario.getId());
                ps.setInt(2, funcionario.getRol().getId());
                ps.executeUpdate();
            }

            connection.commit();
            logger.info("Funcionario guardado: " + funcionario.getNombre() + " con rol " + funcionario.getRol().getNombre());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error guardando funcionario: " + funcionario.getNombre(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public Funcionario findById(int id) throws Exception {
        String query = "SELECT u.id, u.nombre, u.apellido, u.email, u.contrasenia, u.documento, u.estado, u.tipo, " +
                "f.id_rol " +
                "FROM proyecto.usuarios u JOIN proyecto.funcionarios f ON u.id = f.id " +
                "WHERE u.id = ? AND u.estado = 'ACTIVO'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Funcionario funcionario = new Funcionario();
                    // Mapea campos de usuarios
                    funcionario.setId(rs.getInt("u.id"));
                    funcionario.setNombre(rs.getString("u.nombre"));
                    funcionario.setApellido(rs.getString("u.apellido"));
                    funcionario.setEmail(rs.getString("u.email"));
                    funcionario.setContrasenia(rs.getString("u.contrasenia"));
                    funcionario.setDocumento(rs.getString("u.documento"));
                    funcionario.setEstado(EstadoUsuario.valueOf(rs.getString("u.estado")));
                    funcionario.setTipo(TipoUsuario.valueOf(rs.getString("u.tipo")));
                    // Carga rol
                    int idRol = rs.getInt("f.id_rol");
                    if (!rs.wasNull()) {
                        Rol rol = rolDAO.findById(idRol);
                        funcionario.setRol(rol);
                    }
                    logger.info("Funcionario encontrado: ID " + id);
                    return funcionario;
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando funcionario por ID: " + id, e);
            throw e;
        }
        return null;
    }

    @Override
    public List<Funcionario> findAll() throws Exception {
        String query = "SELECT u.id FROM proyecto.usuarios u JOIN proyecto.funcionarios f ON u.id = f.id " +
                "WHERE u.tipo = 'FUNCIONARIO' AND u.estado = 'ACTIVO'";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Funcionario f = findById(rs.getInt("id"));
                    if (f != null) {
                        funcionarios.add(f);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error obteniendo todos los funcionarios", e);
            throw e;
        }
        logger.info("Funcionarios obtenidos: " + funcionarios.size());
        return funcionarios;
    }

    @Override
    public void update(Funcionario funcionario) throws Exception {
        connection.setAutoCommit(false);
        try {
            // Actualiza usuarios
            usuarioDAO.update(funcionario);
            // Actualiza funcionarios
            String query = "UPDATE proyecto.funcionarios SET id_rol = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                if(funcionario.getRol() != null) {
                    ps.setInt(1, funcionario.getRol().getId());
                } else {
                    ps.setNull(1, java.sql.Types.INTEGER);
                }
                ps.setInt(2, funcionario.getId());
                ps.executeUpdate();
            }
            connection.commit();
            logger.info("Funcionario actualizado: ID " + funcionario.getId());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error actualizando funcionario: ID " + funcionario.getId(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }


    @Override
    public void delete(int id) throws Exception {
        // Eliminación lógica: marca  como INACTIVO
        usuarioDAO.deleteLogical(id);
        logger.info("Funcionario marcado como inactivo: ID " + id);
    }
}
