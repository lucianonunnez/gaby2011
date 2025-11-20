package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Permiso {
    private int id;
    private String nombre;

    // Constructores
    public Permiso() {
    }
    public Permiso(String nombre) {
        this.nombre = nombre;
    }
    public Permiso(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}