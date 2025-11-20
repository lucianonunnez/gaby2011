package facade;

import auth.AuthManager;
import model.Estudiante;
import model.Incidencia;
import model.Instancia;
import model.InstanciaComun;
import service.EstudianteService;
import service.InstanciaService;

import java.util.List;
import java.util.Scanner;

public class DocenteFacade implements MenuFacade {
    private final AuthManager authManager;
    private final InstanciaService instanciaService;
    private final EstudianteService estudianteService;
    private final Scanner scanner = new Scanner(System.in);

    public DocenteFacade(AuthManager authManager, InstanciaService instanciaService, EstudianteService estudianteService) {
        this.authManager = authManager;
        this.instanciaService = instanciaService;
        this.estudianteService = estudianteService;
    }

    @Override
    public void mostrarMenu() {
        System.out.println("=== Menú Docente ===");
        System.out.println("1. Ver reportes de instancias");
        System.out.println("2. Ver reportes de incidencias");
        System.out.println("3. Listas estudiantes");
        System.out.println("0. Cerrar sesión");
        System.out.print("Ingrese una opción: ");
    }

    @Override
    public void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> {
                    // Case 1: Ver reportes de instancias comunes
                    System.out.println("Ingrese el ID del estudiante: ");
                    int idEstudiante = Integer.parseInt(scanner.nextLine());

                    List<Instancia> todasInstancias = instanciaService.getInstanciasByEstudiante(idEstudiante);
                    if (todasInstancias == null || todasInstancias.isEmpty()) {
                        System.out.println("No hay instancias registradas para este estudiante.");
                        return;
                    }

                    System.out.println("=== Reportes de Instancias Comunes ===");
                    for (Instancia i : todasInstancias) {
                        if (i instanceof InstanciaComun ic && !ic.isConfidencial()) {
                            System.out.println("ID: " + ic.getId()
                                    + ", Título: " + ic.getTitulo()
                                    + ", Estudiante: " + ic.getEstudianteAsociado().getNombre()
                                    + " " + ic.getEstudianteAsociado().getApellido());
                        }
                    }
                }
                case 2 -> {
                    // Case 2: Ver reportes de incidencias
                    System.out.println("Ingrese el ID del estudiante: ");
                    int idEstudiante = Integer.parseInt(scanner.nextLine());

                    List<Instancia> todasInstancias = instanciaService.getInstanciasByEstudiante(idEstudiante);
                    if (todasInstancias == null || todasInstancias.isEmpty()) {
                        System.out.println("No hay incidencias registradas para este estudiante.");
                        return;
                    }

                    System.out.println("=== Reportes de Incidencias ===");
                    for (Instancia i : todasInstancias) {
                        if (i instanceof Incidencia inc && !inc.isConfidencial()) {
                            System.out.println("ID: " + inc.getId()
                                    + ", Título: " + inc.getTitulo()
                                    + ", Lugar: " + inc.getLugar());
                        }
                    }
                }
                case 3 -> {
                    List<Estudiante> estudiantes = estudianteService.listarTodosEstudiantes();
                    if (estudiantes == null || estudiantes.isEmpty()) {
                        System.out.println("No hay estudiantes registrados.");
                    } else {
                        System.out.println("=== Lista de Estudiantes ===");
                        for (Estudiante e : estudiantes) {
                            System.out.println("ID: " + e.getId()
                                    + ", Nombre: " + e.getNombre()
                                    + " " + e.getApellido());
                        }
                    }
                }
                case 0 -> {
                    authManager.logout();
                    System.out.println("Sesión cerrada.");
                }
                default -> System.out.println("Opción no válida.");
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar la opción: " + e.getMessage());
        }
    }
}