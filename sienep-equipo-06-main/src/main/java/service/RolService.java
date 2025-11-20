package service;

import model.Rol;

import java.util.List;

public interface RolService {
    void guardarRol(Rol rol);
    Rol buscarRolPorId(int id);
    List<Rol> obtenerTodosLosRoles();
    void actualizarRol(Rol rol);
    void eliminarRol(int id);
    void agregarPermisoARol(int rolId, int permisoId) throws Exception;
    void quitarPermisoARol(int rolId, int permisoId) throws Exception;
}