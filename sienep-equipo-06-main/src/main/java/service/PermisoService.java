package service;

import model.Permiso;

import java.util.List;

public interface PermisoService {
    void guardarPermiso(Permiso permiso);
    Permiso buscarPermisoPorId(int id);
    List<Permiso> obtenerTodosLosPermisos();
    void actualizarPermiso(Permiso permiso);
    void eliminarPermiso(int id);
}