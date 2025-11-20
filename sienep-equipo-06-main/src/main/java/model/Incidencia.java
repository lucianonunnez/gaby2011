package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.Canal;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Incidencia extends Instancia {
    private String lugar;
    private List<String> personasInvolucradas;
    private Funcionario reportadoPor;

    // Constructores
    public Incidencia() {
        super();
        this.setTipo("INCIDENCIA");
    }
    public Incidencia(String titulo, String codigo, LocalDateTime fechaHora,
                      Canal canal, String comentario, boolean confidencial,
                      Categoria categoria, Estudiante estudianteAsociado,
                      Funcionario creador, String lugar, List<String> personasInvolucradas,
                      Funcionario reportadoPor, String tipo) {
        super(titulo, codigo, fechaHora, canal, comentario, confidencial, categoria, estudianteAsociado, creador, tipo);
        this.lugar = lugar;
        this.personasInvolucradas = personasInvolucradas;
        this.reportadoPor = reportadoPor;
    }
    public Incidencia(int id, String titulo, String codigo, LocalDateTime fechaHora,
                      Canal canal, String comentario, boolean confidencial,
                      Categoria categoria, Estudiante estudianteAsociado,
                      Funcionario creador, String lugar, List<String> personasInvolucradas,
                      Funcionario reportadoPor, String tipo) {
        super(id, titulo, codigo, fechaHora, canal, comentario, confidencial, categoria, estudianteAsociado, creador, tipo);
        this.lugar = lugar;
        this.personasInvolucradas = personasInvolucradas;
        this.reportadoPor = reportadoPor;
    }
}

