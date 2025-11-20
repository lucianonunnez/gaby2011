package model;

import lombok.Getter;
import lombok.Setter;
import model.enums.Canal;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Instancia {
    private int id;
    private String titulo;
    private String codigo; // Generador (RDF14, ej. INST-2025-00123)
    private LocalDateTime fechaHora;
    private Canal canal;
    private String comentario;
    private boolean confidencial; // RF10 - para el control de acceso
    private Categoria categoria;
    private Estudiante estudianteAsociado;
    private Funcionario creador;
    private String tipo; // COMUN o INCIDENCIA
    private String googleCalendarEventId;

    // Constructores
    public Instancia() {
    }
    public Instancia(String titulo, String codigo, LocalDateTime fechaHora,
                     Canal canal, String comentario, boolean confidencial,
                     Categoria categoria, Estudiante estudianteAsociado,
                     Funcionario creador, String tipo) {
        this.titulo = titulo;
        this.codigo = codigo;
        this.fechaHora = fechaHora;
        this.canal = canal;
        this.comentario = comentario;
        this.confidencial = confidencial;
        this.categoria = categoria;
        this.estudianteAsociado = estudianteAsociado;
        this.creador = creador;
        this.tipo = tipo;
    }
    public Instancia(int id, String titulo, String codigo, LocalDateTime fechaHora,
                     Canal canal, String comentario, boolean confidencial,
                     Categoria categoria, Estudiante estudianteAsociado,
                     Funcionario creador, String tipo) {
        this.id = id;
        this.titulo = titulo;
        this.codigo = codigo;
        this.fechaHora = fechaHora;
        this.canal = canal;
        this.comentario = comentario;
        this.confidencial = confidencial;
        this.categoria = categoria;
        this.estudianteAsociado = estudianteAsociado;
        this.creador = creador;
        this.tipo = tipo;
    }
}