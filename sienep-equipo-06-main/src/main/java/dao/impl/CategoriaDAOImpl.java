package dao.impl;

import dao.GenericDAO;
import db.ConfiguracionSistema;
import model.Categoria;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements GenericDAO<Categoria> {
    private static final Logger logger = Logger.getLogger(CategoriaDAOImpl.class);
    private final Connection connection;

    public CategoriaDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
    }

    // constructor para inyección de dependencias en pruebas
    public CategoriaDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Categoria categoria) throws Exception {
        String query = "INSERT INTO proyecto.categorias (nombre, descripcion) VALUES (?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                    logger.info("Categoria guardada con ID: " + categoria.getId());
                }
            }
        } catch (SQLException e) {
            logger.error("Error guardando categoria: " + categoria.getNombre(), e);
            throw e;
        }
    }

    @Override
    public Categoria findById(int id) throws Exception {
        String query = "SELECT * FROM proyecto.categorias WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categoria categoria = new Categoria(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                    logger.info("Categoria encontrada: ID " + id);
                    return categoria;
                } else {
                    logger.warn("Categoria no encontrada: ID " + id);
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando categoria por ID: " + id, e);
            throw e;
        }
        return null;
    }

    @Override
    public List<Categoria> findAll() throws Exception {
        String query = "SELECT * FROM proyecto.categorias";
        List<Categoria> categorias = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                categorias.add(categoria);
            }
            logger.info("Categorias obtenidas: " + categorias.size());
        } catch (SQLException e) {
            logger.error("Error obteniendo todas las categorias", e);
            throw e;
        }
        return categorias;
    }

    @Override
    public void update(Categoria categoria) throws Exception {
        String query = "UPDATE proyecto.categorias SET nombre = ?, descripcion = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Categoria actualizada: ID " + categoria.getId());
            } else {
                logger.warn("No se encontró categoria para actualizar: ID " + categoria.getId());
            }
        } catch (SQLException e) {
            logger.error("Error actualizando categoria: ID " + categoria.getId(), e);
            throw e;
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM proyecto.categorias WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Categoria eliminada correctamente: ID " + id);
            } else {
                logger.warn("No se encontró categoria para eliminar: ID " + id);
            }
        } catch (SQLException e) {
            logger.error("Error eliminando categoria: ID " + id, e);
            throw e;
        }
    }
}
