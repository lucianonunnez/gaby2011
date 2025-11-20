package service.impl;

import dao.impl.RolDAOImpl;
import model.Rol;
import org.apache.log4j.Logger;
import service.RolService;

import java.util.List;

public class RolServiceImpl implements RolService {
    private static final Logger logger = Logger.getLogger(RolServiceImpl.class);
    private final RolDAOImpl rolDAO;

    public RolServiceImpl() throws Exception {
        this.rolDAO = new RolDAOImpl();
    }


    @Override
    public void guardarRol(Rol rol) {
        try {
            Rol existente = rolDAO.findByNombre(rol.getNombre());
            if (existente != null) {
                logger.warn("Ya existe un rol con ese nombre.");
                throw new IllegalArgumentException("Ya existe un rol con ese nombre.");
            }
            if (rol.getPermisos() == null || rol.getPermisos().isEmpty()) {
                throw new IllegalArgumentException("Un rol debe tener al menos un permiso asignado.");
            }
            rolDAO.save(rol);
        } catch (Exception e) {
            logger.error("Error al guardar rol", e);
        }
    }

    @Override
    public Rol buscarRolPorId(int id) {
        try {
            return rolDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar el rol con ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<Rol> obtenerTodosLosRoles() {
        try {
            return rolDAO.findAll();
        } catch (Exception e) {
            logger.error("Error al obtener todos los roles", e);
            return null;
        }
    }

    @Override
    public void actualizarRol(Rol rol) {
        try {
            rolDAO.update(rol);
            logger.info("Rol actualizado exitosamente: " + rol);
        } catch (Exception e) {
            logger.error("Error al actualizar el rol: " + rol, e);
        }
    }

    @Override
    public void eliminarRol(int id) {
        try {
            // primero toca verificar si no hay funcionarios asociados a este rol
            if (rolDAO.rolTieneFuncionarios(id)) {
                throw new IllegalStateException("No se puede eliminar un rol que está asignado a funcionarios.");
            }
            rolDAO.delete(id);
            logger.info("Rol eliminado: " + id);
        } catch (Exception e) {
            logger.error("Error al eliminar el rol: " + id, e);
            throw new RuntimeException("Error interno al eliminar el rol", e);
        }
    }

    @Override
    public void agregarPermisoARol(int idRol, int idPermiso) throws Exception {
        try {
            Rol rol = rolDAO.findById(idRol);
            if (rol == null) {
                throw new Exception("Rol no encontrado.");
            }
            rolDAO.addPermisoToRol(idRol, idPermiso);
            logger.info("Permiso " + idPermiso + " agregado al rol " + idRol);
        } catch (Exception e) {
            logger.error("Error al agregar permiso " + idPermiso + " al rol " + idRol, e);
            throw e;
        }
    }

    @Override
    public void quitarPermisoARol(int idRol, int idPermiso) throws Exception {
        try {
            Rol rol = rolDAO.findById(idRol);
            if (rol == null) {
                throw new Exception("Rol no encontrado.");
            }
            rolDAO.removePermisoFromRol(idRol, idPermiso);
            logger.info("Permiso " + idPermiso + " quitado del rol " + idRol);
        } catch (Exception e) {
            logger.error("Error al quitar permiso " + idPermiso + " del rol " + idRol, e);
            throw e;
        }
    }

}