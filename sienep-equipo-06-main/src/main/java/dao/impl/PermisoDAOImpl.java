package dao.impl;

import dao.GenericDAO;
import db.ConfiguracionSistema;
import model.Permiso;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermisoDAOImpl implements GenericDAO<Permiso> {
    private static final Logger logger = Logger.getLogger(PermisoDAOImpl.class);
    private final Connection connection;

    // constructor default
    public PermisoDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
    }

    // constructor para tests, para crear el mock de Connection
    public PermisoDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Permiso permiso) throws Exception {
        String query = "INSERT INTO proyecto.permisos (nombre) VALUES (?);";

        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, permiso.getNombre());
            ps.executeUpdate();
            logger.info("Permiso guardado correctamente: " + permiso.getNombre());
        } catch (SQLException e) {
            logger.error("Error guardando permiso: " + permiso.getNombre(), e);
            throw e;
        }
    }

    @Override
    public Permiso findById(int id) throws Exception {
        String query = "SELECT * FROM proyecto.permisos WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    Permiso permiso = new Permiso(
                            rs.getInt("id"),
                            rs.getString("nombre")
                    );
                    logger.info("Permiso encontrado: ID " + id);
                    return permiso;
                } else  {
                    logger.warn("Permiso no encontrado: ID " + id);
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando permiso por ID: " + id, e);
            throw e;
        }
        return null;
    }

    @Override
    public List<Permiso> findAll() throws Exception {
        String query = "SELECT * FROM proyecto.permisos;";
        List<Permiso> permisos = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Permiso permiso = new Permiso(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
                permisos.add(permiso);
            }
            logger.info("Permisos obtenidos: " + permisos.size());
        } catch (SQLException e) {
            logger.error("Error obteniendo todos los permisos", e);
            throw e;
        }
        return permisos;
    }

    @Override
    public void update(Permiso permiso) throws Exception {
        String query = "UPDATE proyecto.permisos SET nombre = ? WHERE id = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, permiso.getNombre());
            ps.setInt(2, permiso.getId());
            ps.executeUpdate();
            logger.info("Permiso actualizado correctamente: ID " + permiso.getId());
        } catch (SQLException e) {
            logger.error("Error actualizando permiso: ID " + permiso.getId(), e);
            throw e;
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM proyecto.permisos WHERE id = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Permiso eliminado correctamente: ID " + id);
        } catch (SQLException e) {
            logger.error("Error eliminando permiso: ID " + id, e);
            throw e;
        }
    }
}
