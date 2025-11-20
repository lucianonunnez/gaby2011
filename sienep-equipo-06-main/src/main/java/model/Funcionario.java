package model;

import lombok.Getter;
import lombok.Setter;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;

@Getter
@Setter
public class Funcionario extends Usuario {
    private Rol rol;

    // Constructores
    public Funcionario() {
        super();
        this.setTipo(TipoUsuario.FUNCIONARIO);
    }
    public Funcionario(String nombre, String apellido, String email, String contrasenia,
                       String documento, EstadoUsuario estado, TipoUsuario tipo, Rol rol) {
        super(nombre, apellido, email, contrasenia, documento, estado, tipo);
        this.rol = rol;
    }
    public Funcionario (int id, String nombre, String apellido, String email, String contrasenia,
                        String documento, EstadoUsuario estado, TipoUsuario tipo, Rol rol) {
        super(id, nombre, apellido, email, contrasenia, documento, estado, tipo);
        this.rol = rol;
    }
}