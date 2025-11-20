package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Categoria {
    private int id;
    private String nombre;
    private String descripcion;

    // Constructores
    public Categoria() {
    }
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    public Categoria(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}

