package service.impl;

import dao.GenericDAO;
import dao.impl.CategoriaDAOImpl;
import model.Categoria;
import org.apache.log4j.Logger;
import service.CategoriaService;

import java.util.List;

public class CategoriaServiceImpl implements CategoriaService {
    private static final Logger logger = Logger.getLogger(CategoriaServiceImpl.class);
    private final GenericDAO<Categoria> categoriaDAO;

    public CategoriaServiceImpl() throws Exception {
        this.categoriaDAO = new CategoriaDAOImpl();
    }


    @Override
    public void guardarCategoria(Categoria categoria) {
        try {
            categoriaDAO.save(categoria);
            logger.info("Categoria guardada exitosamente: " + categoria.getNombre());
        } catch (Exception e) {
            logger.error("Error guardando categoria: " + categoria.getNombre(), e);
        }
    }

    @Override
    public Categoria buscarCategoriaPorId(int id) {
        try {
            return categoriaDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error buscando categoria por ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<Categoria> listarCategorias() {
        try {
            return categoriaDAO.findAll();
        } catch (Exception e) {
            logger.error("Error listando categorias", e);
            return null;
        }
    }

    @Override
    public void actualizarCategoria(Categoria categoria) {
        try {
            categoriaDAO.update(categoria);
            logger.info("Categoria actualizada exitosamente: ID " + categoria.getId());
        } catch (Exception e) {
            logger.error("Error actualizando categoria: ID " + categoria.getId(), e);
        }
    }

    @Override
    public void eliminarCategoria(int id) {
        try {
            categoriaDAO.delete(id);
            logger.info("Categoria eliminada exitosamente: ID " + id);
        } catch (Exception e) {
            logger.error("Error eliminando categoria: ID " + id, e);
        }
    }
}