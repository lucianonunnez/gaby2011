package model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Rol {
    private int id;
    private String nombre;
    private List<Permiso> permisos;

    // Constructores
    public Rol() {
    }
    public Rol(String nombre, List<Permiso> permisos) {
        this.nombre = nombre;
        this.permisos = permisos;
    }
    public Rol(int id, String nombre, List<Permiso> permisos) {
        this.id = id;
        this.nombre = nombre;
        this.permisos = permisos;
    }
}

