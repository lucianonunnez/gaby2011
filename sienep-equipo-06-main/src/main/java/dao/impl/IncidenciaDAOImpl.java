package dao.impl;

import dao.GenericDAO;
import dao.InstanciaDAO;
import dao.UsuarioDAO;
import db.ConfiguracionSistema;
import model.Funcionario;
import model.Incidencia;
import model.Instancia;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDAOImpl implements GenericDAO<Incidencia> {
    private static final Logger logger = Logger.getLogger(IncidenciaDAOImpl.class);
    private final Connection connection;
    private final InstanciaDAO instanciaDAO;  // delega los campos comunes
    private final UsuarioDAO usuarioDAO;  // para cargar reportadoPor

    public IncidenciaDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
        this.instanciaDAO = new InstanciaDAOImpl();
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    @Override
    public void save(Incidencia incidencia) throws Exception {
        connection.setAutoCommit(false);
        try {
            // delega save de campos comunes
            instanciaDAO.save(incidencia);
            // guarda campos específicos en incidencias
            String query = "INSERT INTO proyecto.incidencias (id, lugar, personas_involucradas, id_reportado_por) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, incidencia.getId());
                ps.setString(2, incidencia.getLugar());
                ps.setString(3, String.join(",", incidencia.getPersonasInvolucradas()));
                ps.setInt(4, incidencia.getReportadoPor().getId());
                ps.executeUpdate();
            }
            connection.commit();
            logger.info("Incidencia guardada: ID " + incidencia.getId());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error guardando Incidencia: " + incidencia.getTitulo(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public Incidencia findById(int id) throws Exception {
        // delega findById de campos comunes
        Instancia instancia = instanciaDAO.findById(id);
        if (instancia instanceof Incidencia) {
            Incidencia incidencia = (Incidencia) instancia;
            // carga campos específicos
            String query = "SELECT lugar, personas_involucradas, id_reportado_por FROM proyecto.incidencias WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        incidencia.setLugar(rs.getString("lugar"));
                        incidencia.setPersonasInvolucradas(List.of(rs.getString("personas_involucradas").split(",")));
                        incidencia.setReportadoPor((Funcionario) usuarioDAO.findById(rs.getInt("id_reportado_por")));
                    }
                }
            }
            logger.info("Incidencia encontrada: ID " + id);
            return incidencia;
        }
        return null;
    }

    @Override
    public List<Incidencia> findAll() throws Exception {
        // delega findAll y filtra por tipo
        List<Instancia> instancias = instanciaDAO.findAll();
        List<Incidencia> incidencias = new ArrayList<>();
        for (Instancia i : instancias) {
            if (i instanceof Incidencia) {
                incidencias.add((Incidencia) i);
            }
        }
        logger.info("Incidencias obtenidas: " + incidencias.size());
        return incidencias;
    }

    @Override
    public void update(Incidencia incidencia) throws Exception {
        connection.setAutoCommit(false);
        try {
            // delega update de campos comunes
            instanciaDAO.update(incidencia);
            // actualización de campos específicos
            String query = "UPDATE proyecto.incidencias SET lugar = ?, personas_involucradas = ?, id_reportado_por = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, incidencia.getLugar());
                ps.setString(2, String.join(",", incidencia.getPersonasInvolucradas()));
                ps.setInt(3, incidencia.getReportadoPor().getId());
                ps.setInt(4, incidencia.getId());
                ps.executeUpdate();
            }
            connection.commit();
            logger.info("Incidencia actualizada: ID " + incidencia.getId());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error actualizando Incidencia: ID " + incidencia.getId(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        // delega el delete (DELETE físico)
        instanciaDAO.delete(id);
        logger.info("Incidencia eliminada: ID " + id);
    }
}
