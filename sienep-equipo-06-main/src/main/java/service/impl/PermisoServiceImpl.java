package service.impl;

import dao.GenericDAO;
import dao.impl.PermisoDAOImpl;
import model.Permiso;
import org.apache.log4j.Logger;
import service.PermisoService;

import java.util.List;

public class PermisoServiceImpl implements PermisoService {
    private static final Logger logger = Logger.getLogger(PermisoServiceImpl.class);
    private final GenericDAO<Permiso> permisoDAO;

    public PermisoServiceImpl() throws Exception {
        this.permisoDAO = new PermisoDAOImpl();
    }

    @Override
    public void guardarPermiso(Permiso permiso) {
        try {
            permisoDAO.save(permiso);
            logger.info("Permiso guardado exitosamente: " + permiso);
        } catch (Exception e) {
            logger.error("Error al guardar el permiso: " + permiso, e);
        }
    }

    @Override
    public Permiso buscarPermisoPorId(int id) {
        try {
            return permisoDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar el permiso con ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<Permiso> obtenerTodosLosPermisos() {
        try {
            return permisoDAO.findAll();
        } catch (Exception e) {
            logger.error("Error al obtener todos los permisos", e);
            return null;
        }
    }

    @Override
    public void actualizarPermiso(Permiso permiso) {
        try {
            permisoDAO.update(permiso);
            logger.info("Permiso actualizado exitosamente: " + permiso);
        } catch (Exception e) {
            logger.error("Error al actualizar el permiso: " + permiso, e);
        }
    }

    @Override
    public void eliminarPermiso(int id) {
        try {
            permisoDAO.delete(id);
            logger.info("Permiso eliminado exitosamente con ID: " + id);
        } catch (Exception e) {
            logger.error("Error al eliminar el permiso con ID: " + id, e);
        }
    }
}