package model;

import lombok.Getter;
import lombok.Setter;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Estudiante extends Usuario {
    private String motivoDerivacion;
    private String carrera;
    private String grupo;
    private String telefono;
    private String calle;
    private String numeroPuerta;
    private LocalDate fechaNacimiento;
    private String foto;
    private String sistemaSalud;
    private String comentariosGenerales; // ej. Dificultad de concentraciÃ³n
    // Confidencial:
    private String estadoSalud;
    private List<String> observacionesConfidenciales;

    // Constructores
    public Estudiante() {
        super();
        this.setTipo(TipoUsuario.ESTUDIANTE);
    }
    public Estudiante(String nombre, String apellido, String email, String contrasenia,
                      String documento, EstadoUsuario estado, TipoUsuario tipo, String motivoDerivacion,
                      String carrera, String grupo, String telefono, String calle, String numeroPuerta,
                      LocalDate fechaNacimiento, String foto, String sistemaSalud, String comentariosGenerales,
                      String estadoSalud, List<String> observacionesConfidenciales) {
        super(nombre, apellido, email, contrasenia, documento, estado, tipo);
        this.motivoDerivacion = motivoDerivacion;
        this.carrera = carrera;
        this.grupo = grupo;
        this.telefono = telefono;
        this.calle = calle;
        this.numeroPuerta = numeroPuerta;
        this.fechaNacimiento = fechaNacimiento;
        this.foto = foto;
        this.sistemaSalud = sistemaSalud;
        this.comentariosGenerales = comentariosGenerales;
        this.estadoSalud = estadoSalud;
        this.observacionesConfidenciales = observacionesConfidenciales;
    }
    public Estudiante (int id, String nombre, String apellido, String email, String contrasenia,
                       String documento, EstadoUsuario estado, TipoUsuario tipo, String motivoDerivacion,
                       String carrera, String grupo, String telefono, String calle, String numeroPuerta,
                       LocalDate fechaNacimiento, String foto, String sistemaSalud, String comentariosGenerales,
                       String estadoSalud, List<String> observacionesConfidenciales) {
        super(id, nombre, apellido, email, contrasenia, documento, estado, tipo);
        this.motivoDerivacion = motivoDerivacion;
        this.carrera = carrera;
        this.grupo = grupo;
        this.telefono = telefono;
        this.calle = calle;
        this.numeroPuerta = numeroPuerta;
        this.fechaNacimiento = fechaNacimiento;
        this.foto = foto;
        this.sistemaSalud = sistemaSalud;
        this.comentariosGenerales = comentariosGenerales;
        this.estadoSalud = estadoSalud;
        this.observacionesConfidenciales = observacionesConfidenciales;
    }
}