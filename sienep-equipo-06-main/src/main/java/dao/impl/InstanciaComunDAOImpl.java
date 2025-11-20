package dao.impl;

import dao.GenericDAO;
import dao.InstanciaDAO;
import db.ConfiguracionSistema;
import model.Instancia;
import model.InstanciaComun;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstanciaComunDAOImpl implements GenericDAO<InstanciaComun> {
    private static final Logger logger = Logger.getLogger(InstanciaComunDAOImpl.class);
    private final Connection connection;
    private final InstanciaDAO instanciaDAO;

    public InstanciaComunDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
        this.instanciaDAO = new InstanciaDAOImpl();
    }

    @Override
    public void save(InstanciaComun instanciaComun) throws Exception {
        connection.setAutoCommit(false);
        try {
            // delega save de campos comunes a InstanciaDAO
            instanciaDAO.save(instanciaComun);
            // guarda motivacion en instancias_comunes
            String query = "INSERT INTO proyecto.instancias_comunes (id, motivacion) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, instanciaComun.getId());
                ps.setString(2, instanciaComun.getMotivacion());
                ps.executeUpdate();
            }
            connection.commit();
            logger.info("InstanciaComun guardada: ID " + instanciaComun.getId());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error guardando InstanciaComun: " + instanciaComun.getTitulo(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public InstanciaComun findById(int id) throws Exception {
        // delega findById de campos comunes
        Instancia instancia = instanciaDAO.findById(id);
        if (instancia instanceof InstanciaComun) {
            InstanciaComun instanciaComun = (InstanciaComun) instancia;
            // carga motivacion
            String query = "SELECT motivacion FROM proyecto.instancias_comunes WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        instanciaComun.setMotivacion(rs.getString("motivacion"));
                    }
                }
            }
            logger.info("InstanciaComun encontrada: ID " + id);
            return instanciaComun;
        }
        return null;
    }

    @Override
    public List<InstanciaComun> findAll() throws Exception {
        // delega findAll y filtra por tipo
        List<Instancia> instancias = instanciaDAO.findAll();
        List<InstanciaComun> comunes = new ArrayList<>();
        for (Instancia i : instancias) {
            if (i instanceof InstanciaComun) {
                comunes.add((InstanciaComun) i);
            }
        }
        logger.info("InstanciaComun obtenidas: " + comunes.size());
        return comunes;
    }

    @Override
    public void update(InstanciaComun instanciaComun) throws Exception {
        connection.setAutoCommit(false);
        try {
            // delega update de campos comunes
            instanciaDAO.update(instanciaComun);
            // actualización de motivacion
            String query = "UPDATE proyecto.instancias_comunes SET motivacion = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, instanciaComun.getMotivacion());
                ps.setInt(2, instanciaComun.getId());
                ps.executeUpdate();
            }
            connection.commit();
            logger.info("InstanciaComun actualizada: ID " + instanciaComun.getId());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error actualizando InstanciaComun: ID " + instanciaComun.getId(), e);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        // delega el delete (DELETE físico)
        instanciaDAO.delete(id);
        logger.info("InstanciaComun eliminada: ID " + id);
    }
}
