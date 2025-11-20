package dao.impl;

import dao.EstudianteDAO;
import dao.UsuarioDAO;
import db.ConfiguracionSistema;
import model.Estudiante;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAOImpl implements EstudianteDAO {
    private static final Logger logger = Logger.getLogger(EstudianteDAOImpl.class);
    private final Connection connection;
    private final UsuarioDAO usuarioDAO;

    public EstudianteDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    // constructor para pruebas unitarias
    public EstudianteDAOImpl(Connection connection, UsuarioDAO usuarioDAO) {
        this.connection = connection;
        this.usuarioDAO = usuarioDAO;
    }


    @Override
    public List<Estudiante> findByCarrera(String carrera) throws Exception {
        String query = "SELECT u.id FROM proyecto.usuarios u JOIN proyecto.estudiantes e ON u.id = e.id " +
                "WHERE e.carrera = ? AND u.tipo = 'ESTUDIANTE' AND u.estado = 'ACTIVO'";
        List<Estudiante> estudiantes = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, carrera);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estudiante e = findById(rs.getInt("id"));
                    if (e != null) {
                        estudiantes.add(e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando estudiantes por carrera: " + carrera, e);
            throw e;
        }
        logger.info("Estudiantes encontrados por carrera '" + carrera + "': " + estudiantes.size());
        return estudiantes;
    }

    @Override
    public List<Estudiante> findByGrupo(String grupo) throws Exception {
        String query = "SELECT u.id FROM proyecto.usuarios u JOIN proyecto.estudiantes e ON u.id = e.id " +
                "WHERE e.grupo = ? AND u.tipo = 'ESTUDIANTE' AND u.estado = 'ACTIVO'";
        List<Estudiante> estudiantes = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, grupo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estudiante e = findById(rs.getInt("id"));
                    if (e != null) {
                        estudiantes.add(e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando estudiantes por grupo: " + grupo, e);
            throw e;
        }
        logger.info("Estudiantes encontrados por grupo '" + grupo + "': " + estudiantes.size());
        return estudiantes;
    }

    @Override
    public List<Estudiante> findBySistemaSalud(String sistemaSalud) throws Exception {
        String query = "SELECT u.id FROM proyecto.usuarios u JOIN proyecto.estudiantes e ON u.id = e.id " +
                "WHERE e.sistema_salud = ? AND u.tipo = 'ESTUDIANTE' AND u.estado = 'ACTIVO'";
        List<Estudiante> estudiantes = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, sistemaSalud);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estudiante e = findById(rs.getInt("id"));
                    if (e != null) {
                        estudiantes.add(e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando estudiantes por sistema de salud: " + sistemaSalud, e);
            throw e;
        }
        logger.info("Estudiantes encontrados por sistema de salud '" + sistemaSalud + "': " + estudiantes.size());
        return estudiantes;
    }

    @Override
    public void save(Estudiante estudiante) throws Exception {
        connection.setAutoCommit(false); // inicio de la transacción
        // primero insert como usuario
        try {
            usuarioDAO.save(estudiante);
            // campos especificos de estudiante
            String query = "INSERT INTO proyecto.estudiantes (id, motivo_derivacion, carrera, grupo, telefono, " +
                    "calle, numero_puerta, fecha_nacimiento, foto, sistema_salud, comentarios_generales, estado_salud, " +
                    "observaciones_confidenciales) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try(PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, estudiante.getId());
                ps.setString(2, estudiante.getMotivoDerivacion());
                ps.setString(3, estudiante.getCarrera());
                ps.setString(4, estudiante.getGrupo());
                ps.setString(5, estudiante.getTelefono());
                ps.setString(6, estudiante.getCalle());
                ps.setString(7, estudiante.getNumeroPuerta());
                ps.setDate(8, Date.valueOf(estudiante.getFechaNacimiento()));
                ps.setString(9, estudiante.getFoto());
                ps.setString(10, estudiante.getSistemaSalud());
                ps.setString(11, estudiante.getComentariosGenerales());
                ps.setString(12, estudiante.getEstadoSalud());
                ps.setString(13, String.join(",", estudiante.getObservacionesConfidenciales()));  // List a String
                ps.executeUpdate();
            }
            connection.commit();
            logger.info("Estudiante guardado: " + estudiante.getNombre() + " " + estudiante.getApellido());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error guardando estudiante: " + estudiante.getNombre() + " " + estudiante.getApellido(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public Estudiante findById(int id) throws Exception {
        String query = "SELECT u.id, u.nombre, u.apellido, u.email, u.contrasenia, u.documento, u.estado, u.tipo, " +
                "e.motivo_derivacion, e.carrera, e.grupo, e.telefono, e.calle, e.numero_puerta, e.fecha_nacimiento, e.foto, e.sistema_salud, e.comentarios_generales, e.estado_salud, e.observaciones_confidenciales " +
                "FROM proyecto.usuarios u JOIN proyecto.estudiantes e ON u.id = e.id " +
                "WHERE u.id = ? AND u.estado = 'ACTIVO'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Estudiante estudiante = new Estudiante();
                    // Mapea campos de usuarios
                    estudiante.setId(rs.getInt("id"));
                    estudiante.setNombre(rs.getString("nombre"));
                    estudiante.setApellido(rs.getString("apellido"));
                    estudiante.setEmail(rs.getString("email"));
                    estudiante.setContrasenia(rs.getString("contrasenia"));
                    estudiante.setDocumento(rs.getString("documento"));
                    estudiante.setEstado(EstadoUsuario.valueOf(rs.getString("estado")));
                    estudiante.setTipo(TipoUsuario.valueOf(rs.getString("tipo")));
                    // Mapea campos de estudiantes
                    estudiante.setMotivoDerivacion(rs.getString("motivo_derivacion"));
                    estudiante.setCarrera(rs.getString("carrera"));
                    estudiante.setGrupo(rs.getString("grupo"));
                    estudiante.setTelefono(rs.getString("telefono"));
                    estudiante.setCalle(rs.getString("calle"));
                    estudiante.setNumeroPuerta(rs.getString("numero_puerta"));
                    estudiante.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
                    estudiante.setFoto(rs.getString("foto"));
                    estudiante.setSistemaSalud(rs.getString("sistema_salud"));
                    estudiante.setComentariosGenerales(rs.getString("comentarios_generales"));
                    estudiante.setEstadoSalud(rs.getString("estado_salud"));
                    estudiante.setObservacionesConfidenciales(List.of(rs.getString("observaciones_confidenciales").split(",")));
                    logger.info("Estudiante encontrado: ID " + id);
                    return estudiante;
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando estudiante por ID: " + id, e);
            throw e;
        }
        return null;
    }

    @Override
    public List<Estudiante> findAll() throws Exception {
        String query = "SELECT u.id FROM proyecto.usuarios u JOIN proyecto.estudiantes e ON u.id = e.id " +
                "WHERE u.tipo = 'ESTUDIANTE' AND u.estado = 'ACTIVO'";
        List<Estudiante> estudiantes = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estudiante e = findById(rs.getInt("id"));
                    if (e != null) {
                        estudiantes.add(e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error obteniendo todos los estudiantes", e);
            throw e;
        }
        logger.info("Estudiantes obtenidos: " + estudiantes.size());
        return estudiantes;
    }

    @Override
    public void update(Estudiante estudiante) throws Exception {
        connection.setAutoCommit(false);
        try {
            // Actualiza usuarios
            usuarioDAO.update(estudiante);
            // Actualiza estudiantes
            String query = "UPDATE proyecto.estudiantes SET motivo_derivacion = ?, carrera = ?, grupo = ?, telefono = ?, calle = ?, numero_puerta = ?, fecha_nacimiento = ?, foto = ?, sistema_salud = ?, comentarios_generales = ?, estado_salud = ?, observaciones_confidenciales = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, estudiante.getMotivoDerivacion());
                ps.setString(2, estudiante.getCarrera());
                ps.setString(3, estudiante.getGrupo());
                ps.setString(4, estudiante.getTelefono());
                ps.setString(5, estudiante.getCalle());
                ps.setString(6, estudiante.getNumeroPuerta());
                ps.setDate(7, Date.valueOf(estudiante.getFechaNacimiento()));
                ps.setString(8, estudiante.getFoto());
                ps.setString(9, estudiante.getSistemaSalud());
                ps.setString(10, estudiante.getComentariosGenerales());
                ps.setString(11, estudiante.getEstadoSalud());
                ps.setString(12, String.join(",", estudiante.getObservacionesConfidenciales()));
                ps.setInt(13, estudiante.getId());
                ps.executeUpdate();
            }
            connection.commit();
            logger.info("Estudiante actualizado: ID " + estudiante.getId());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error actualizando estudiante: ID " + estudiante.getId(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        // Eliminación lógica: marca usuarios como INACTIVO
        usuarioDAO.deleteLogical(id);
        logger.info("Estudiante marcado como inactivo: ID " + id);
    }

    @Override
    public void updateTelefono(int id, String telefono) throws Exception {
        String query = "UPDATE proyecto.estudiantes SET telefono = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, telefono);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error actualizando teléfono del estudiante ID: " + id, e);
            throw e;
        }
    }
}
