package dao.impl;

import dao.EstudianteDAO;
import dao.GenericDAO;
import dao.InstanciaDAO;
import dao.UsuarioDAO;
import db.ConfiguracionSistema;
import model.*;
import model.enums.Canal;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstanciaDAOImpl implements InstanciaDAO {
    private static final Logger logger = Logger.getLogger(InstanciaDAOImpl.class);
    private final Connection connection;
    private final GenericDAO<Categoria> categoriaDAO;
    private final EstudianteDAO estudianteDAO;
    private final UsuarioDAO usuarioDAO;

    public InstanciaDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
        this.categoriaDAO = new CategoriaDAOImpl();
        this.estudianteDAO = new EstudianteDAOImpl();
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    // constructor para pruebas unitarias
    public InstanciaDAOImpl(Connection connection, GenericDAO<Categoria> categoriaDAO, EstudianteDAO estudianteDAO, UsuarioDAO usuarioDAO) {
        this.connection = connection;
        this.categoriaDAO = categoriaDAO;
        this.estudianteDAO = estudianteDAO;
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public void save(Instancia instancia) throws Exception {
        String query = "INSERT INTO proyecto.instancias (titulo, codigo, fecha_hora, canal, comentario, confidencial, id_categoria, id_estudiante, id_creador, tipo, google_calendar_event_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try(PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, instancia.getTitulo());
            ps.setString(2, instancia.getCodigo());
            ps.setTimestamp(3, Timestamp.valueOf(instancia.getFechaHora()));
            ps.setString(4, instancia.getCanal().toString());
            ps.setString(5, instancia.getComentario());
            ps.setBoolean(6, instancia.isConfidencial());
            ps.setInt(7, instancia.getCategoria().getId());
            ps.setInt(8, instancia.getEstudianteAsociado().getId());
            ps.setInt(9, instancia.getCreador().getId());
            ps.setString(10, instancia.getTipo());
            ps.setString(11, instancia.getGoogleCalendarEventId());
            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) {
                    instancia.setId(rs.getInt(1));
                    logger.info("Instancia guardada con ID: " + instancia.getId());
                }
            }
        } catch (SQLException e) {
            logger.error("Error guardando instancia: " + instancia.getTitulo(), e);
            throw e;
        }
    }

    @Override
    public Instancia findById(int id) throws Exception {
        String query = "SELECT * FROM proyecto.instancias WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapInstanciaFromRS(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando instancia por ID: " + id, e);
            throw e;
        }
        return null;
    }

    @Override
    public List<Instancia> findAll() throws Exception {
        String query = "SELECT * FROM proyecto.instancias";
        List<Instancia> instancias = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                instancias.add(mapInstanciaFromRS(rs));
            }
        } catch (SQLException e) {
            logger.error("Error obteniendo todas las instancias", e);
            throw e;
        }
        logger.info("Instancias obtenidas: " + instancias.size());
        return instancias;
    }

    @Override
    public void update(Instancia instancia) throws Exception {
        String query = "UPDATE proyecto.instancias SET titulo = ?, codigo = ?, fecha_hora = ?, canal = ?, comentario = ?, confidencial = ?, id_categoria = ?, id_estudiante = ?, id_creador = ?, tipo = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, instancia.getTitulo());
            ps.setString(2, instancia.getCodigo());
            ps.setTimestamp(3, Timestamp.valueOf(instancia.getFechaHora()));
            ps.setString(4, instancia.getCanal().toString());
            ps.setString(5, instancia.getComentario());
            ps.setBoolean(6, instancia.isConfidencial());
            ps.setInt(7, instancia.getCategoria().getId());
            ps.setInt(8, instancia.getEstudianteAsociado().getId());
            ps.setInt(9, instancia.getCreador().getId());
            ps.setString(10, instancia.getTipo());
            ps.setInt(11, instancia.getId());
            ps.executeUpdate();
            logger.info("Instancia actualizada: ID " + instancia.getId());
        } catch (SQLException e) {
            logger.error("Error actualizando instancia: ID " + instancia.getId(), e);
            throw e;
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM proyecto.instancias WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Instancia eliminada correctamente: ID " + id);
            } else {
                logger.warn("No se encontró instancia para eliminar: ID " + id);
            }
        } catch (SQLException e) {
            logger.error("Error eliminando instancia: ID " + id, e);
            throw e;
        }
    }

    @Override
    public List<Instancia> findByEstudiante(int idEstudiante) throws Exception {
        String query = "SELECT * FROM proyecto.instancias WHERE id_estudiante = ?";
        List<Instancia> instancias = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    instancias.add(mapInstanciaFromRS(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando instancias por estudiante: " + idEstudiante, e);
            throw e;
        }
        logger.info("Instancias encontradas para estudiante ID " + idEstudiante + ": " + instancias.size());
        return instancias;
    }

    public List<Instancia> getInstaciasByCategoria(int idCategoria) throws Exception {
        String query = "SELECT * FROM proyecto.instancias WHERE id_categoria = ?";
        List<Instancia> instancias = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    instancias.add(mapInstanciaFromRS(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando instancias por categoria: " + idCategoria, e);
            throw e;
        }
        logger.info("Instancias encontradas para categoria ID " + idCategoria + ": " + instancias.size());
        return instancias;
    }

    // Métodos auxiliares
    protected Instancia mapInstanciaFromRS(ResultSet rs) throws Exception {
        Instancia instancia;
        String tipo = rs.getString("tipo");
        if ("INCIDENCIA".equals(tipo)) {
            instancia = new Incidencia();
        } else {
            instancia = new InstanciaComun();
        }
        instancia.setId(rs.getInt("id"));
        instancia.setTitulo(rs.getString("titulo"));
        instancia.setCodigo(rs.getString("codigo"));
        instancia.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        instancia.setCanal(Canal.valueOf(rs.getString("canal")));
        instancia.setComentario(rs.getString("comentario"));
        instancia.setConfidencial(rs.getBoolean("confidencial"));
        instancia.setCategoria(categoriaDAO.findById(rs.getInt("id_categoria")));
        instancia.setEstudianteAsociado(estudianteDAO.findById(rs.getInt("id_estudiante")));
        instancia.setCreador((Funcionario) usuarioDAO.findById(rs.getInt("id_creador")));
        instancia.setTipo(tipo);
        instancia.setGoogleCalendarEventId(rs.getString("google_calendar_event_id"));
        return instancia;
    }
}
