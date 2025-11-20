package model;

import lombok.Getter;
import lombok.Setter;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;

@Getter
@Setter
public abstract class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasenia;
    private String documento;
    private EstadoUsuario estado;
    private TipoUsuario tipo; // Para distinguir entre instancias de Funcionarios y Estudiantes

    // Constructores
    public Usuario() {
        this.estado = EstadoUsuario.ACTIVO; // Default
    }
    public Usuario(String nombre, String apellido, String email, String contrasenia, String documento, EstadoUsuario estado, TipoUsuario tipo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = contrasenia;
        this.documento = documento;
        this.estado = EstadoUsuario.ACTIVO;
        this.tipo = tipo;
    }
    public Usuario(int id, String nombre, String apellido, String email, String contrasenia, String documento, EstadoUsuario estado, TipoUsuario tipo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = contrasenia;
        this.documento = documento;
        this.estado = EstadoUsuario.ACTIVO;
        this.tipo = tipo;
    }
}