package model;

import lombok.Getter;
import lombok.Setter;
import model.enums.Canal;
import java.time.LocalDateTime;

@Getter
@Setter
public class InstanciaComun extends Instancia {
    private String motivacion; // Detalles de por qué el estudiante solicita una instancia
    // Constructores
    public InstanciaComun() {
        super();
        this.setTipo("COMUN");
    }
    public InstanciaComun(String titulo, String codigo, LocalDateTime fechaHora,
                          Canal canal, String comentario, boolean confidencial,
                          Categoria categoria, Estudiante estudianteAsociado,
                          Funcionario creador, String tipo, String motivacion) {
        super(titulo, codigo, fechaHora, canal, comentario, confidencial, categoria, estudianteAsociado, creador, tipo);
        this.motivacion = motivacion;
    }
    public InstanciaComun(int id, String titulo, String codigo, LocalDateTime fechaHora,
                          Canal canal, String comentario, boolean confidencial,
                          Categoria categoria, Estudiante estudianteAsociado,
                          Funcionario creador, String tipo, String motivacion) {
        super(id, titulo, codigo, fechaHora, canal, comentario, confidencial, categoria, estudianteAsociado, creador, tipo);
        this.motivacion = motivacion;
    }
}