package facade;

import auth.AuthManager;
import model.*;
import model.enums.Canal;
import service.CategoriaService;
import service.EstudianteService;
import service.InstanciaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class PsicopedagogoFacade implements MenuFacade {
    private final AuthManager authManager;
    private final InstanciaService instanciaService;
    private final EstudianteService estudianteService;
    private final Scanner scanner = new Scanner(System.in);
    private final CategoriaService categoriaService;

    public PsicopedagogoFacade(AuthManager authManager, InstanciaService instanciaService,
                               EstudianteService estudianteService, CategoriaService categoriaService) {
        this.authManager = authManager;
        this.instanciaService = instanciaService;
        this.estudianteService = estudianteService;
        this.categoriaService = categoriaService;
    }

    @Override
    public void mostrarMenu() {
        System.out.println("=== Menú Psicopedagogo ===");
        System.out.println("1. Gestionar Instancias Comunes");
        System.out.println("2. Gestionar Incidencias");
        System.out.println("3. Listar estudiantes (DATOS SENSIBLES)");
        System.out.println("4. Ver instancias asociadas a un Estudiante");
        System.out.println("0. Cerrar sesión");
        System.out.print("Ingrese una opción: ");
    }

    @Override
    public void ejecutarOpcion(int opcion) {
        Funcionario psicopedagogo = (Funcionario) authManager.getUsuarioActual();
        try {
            switch (opcion) {
                case 1 -> gestionarInstanciasComunes(psicopedagogo);
                case 2 -> gestionarIncidencias(psicopedagogo);
                case 3 -> {
                    // Lista de estudiantes con datos sensibles
                    List<Estudiante> estudiantes = estudianteService.listarTodosEstudiantes();
                    System.out.println("=== Lista de Estudiantes ===");
                    for (Estudiante e : estudiantes) {
                        System.out.println("ID: " + e.getId() + ", Nombre: " + e.getNombre() + ", Salud: " + e.getEstadoSalud() + ", Observaciones: " + e.getObservacionesConfidenciales());
                    }
                }
                case 4 -> {
                    // mostrar estudiantes del sistema
                    List<Estudiante> estudiantes = estudianteService.listarTodosEstudiantes();
                    for (Estudiante e : estudiantes) {
                        System.out.println("ID: " + e.getId() + ", Nombre: " + e.getNombre() + " " + e.getApellido());
                    }
                    System.out.print("Ingrese el ID del Estudiante: ");
                    int estudianteId = Integer.parseInt(scanner.nextLine());
                    List<Instancia> instancias = instanciaService.getInstanciasByEstudiante(estudianteId);
                    System.out.println("=== Instancias asociadas al Estudiante ID " + estudianteId + " ===");
                    for (Instancia instancia : instancias) {
                        if(instancias.isEmpty()) {
                            System.out.println("No hay instancias asociadas a este estudiante.");
                            return;
                        }
                        System.out.println("ID: " + instancia.getId() + ", Título: " + instancia.getTitulo() + ", Tipo: " + instancia.getTipo() + ", Fecha: " + instancia.getFechaHora());
                    }
                }
                case 0 -> {
                    authManager.logout();
                    System.out.println("Sesión cerrada.");
                }
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar la opción: " + e.getMessage());
        }
    }

    private void gestionarInstanciasComunes(Funcionario psicopedagogo) {
        while (true) {
            System.out.println("=== Gestionar Instancias Comunes ===");
            System.out.println("1. Crear instancia común");
            System.out.println("2. Listar instancias comunes");
            System.out.println("3. Cambiar fecha de una instancia común");
            System.out.println("4. Editar comentario de una instancia común");
            System.out.println("5. Eliminar instancia común");
            System.out.println("6. Clonar instancia común");
            System.out.println("0. Volver al menú principal");
            System.out.print("Ingrese una opción: ");
            int subOpcion = Integer.parseInt(scanner.nextLine());

            try {
                switch (subOpcion) {
                    case 1 -> crearInstanciaComun(psicopedagogo);
                    case 2 -> {
                        List<InstanciaComun> instancias = instanciaService.listarInstanciasComunes();
                        System.out.println("=== Instancias Comunes ===");
                        for (InstanciaComun ic : instancias) {
                            System.out.println("ID: " + ic.getId() + ", Título: " + ic.getTitulo() + ", Fecha: " + ic.getFechaHora());
                        }
                    }
                    case 3 -> {
                        // Cambiar fecha de una instancia común
                        // se lista las instancias comunes existentes
                        List<InstanciaComun> instancias = instanciaService.listarInstanciasComunes();
                        for (InstanciaComun ic : instancias) {
                            System.out.println("ID: " + ic.getId() + ", Título: " + ic.getTitulo() + ", Fecha: " + ic.getFechaHora());
                        }

                        System.out.print("ID de la instancia común: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.print("Nueva fecha y hora (YYYY-MM-DD HH:MM): ");
                        String nuevaFechaStr = scanner.nextLine();
                        LocalDateTime nuevaFecha = LocalDateTime.parse(nuevaFechaStr.replace(" ", "T"));
                        instanciaService.actualizarFechaInstanciaComun(id, nuevaFecha);
                        System.out.println("Fecha actualizada exitosamente.");
                    }
                    case 4 -> {
                        // Editar comentario de una instancia común
                        // mostrar las instancias comunes
                        List<InstanciaComun> instancias = instanciaService.listarInstanciasComunes();
                        for (InstanciaComun ic : instancias) {
                            System.out.println("ID: " + ic.getId() + ", Título: " + ic.getTitulo() + ", Comentario: " + ic.getComentario());
                        }
                        System.out.print("ID de la instancia común: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.print("Nuevo comentario: ");
                        String nuevoComentario = scanner.nextLine();
                        instanciaService.actualizarComentarioInstanciaComun(id, nuevoComentario);
                        System.out.println("Comentario actualizado exitosamente.");
                    }
                    case 5 -> eliminarInstanciaComun();
                    case 6 -> {
                        // mostrar las instancias existentes
                        List<InstanciaComun> instancias = instanciaService.listarInstanciasComunes();
                        for (InstanciaComun ic : instancias) {
                            System.out.println("ID: " + ic.getId() + ", Título: " + ic.getTitulo() + ", Fecha: " + ic.getFechaHora());
                        }
                        System.out.print("ID de la instancia común a clonar: ");
                        int idOriginal = Integer.parseInt(scanner.nextLine());
                        try {
                            instanciaService.clonarInstanciaComun(idOriginal);
                            System.out.println("Instancia clonada exitosamente.");
                        } catch (Exception e) {
                            System.out.println("Error al clonar instancia: " + e.getMessage());
                        }
                    }
                    case 0 -> {
                        return;  // Volver al menú principal
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void gestionarIncidencias(Funcionario psicopedagogo) {
        while (true) {
            System.out.println("=== Gestionar Incidencias ===");
            System.out.println("1. Crear incidencia");
            System.out.println("2. Listar incidencias");
            System.out.println("3. Cambiar fecha de una incidencia");
            System.out.println("4. Editar comentario de una incidencia");
            System.out.println("5. Eliminar incidencia");
            System.out.println("6. Editar canal de notificación de una incidencia");
            System.out.println("7. Ver incidencias de un estudiante");
            System.out.println("0. Volver al menú principal");
            System.out.print("Ingrese una opción: ");
            int subOpcion = Integer.parseInt(scanner.nextLine());

            try {
                switch (subOpcion) {
                    case 1 -> crearIncidencia(psicopedagogo);
                    case 2 -> {
                        List<Incidencia> incidencias = instanciaService.listarIncidencias();
                        System.out.println("=== Incidencias ===");
                        for (Incidencia i : incidencias) {
                            System.out.println("ID: " + i.getId() + ", Título: " + i.getTitulo() + ", Fecha: " + i.getFechaHora());
                        }
                    }
                    case 3 -> {
                        // Cambiar fecha de una incidencia
                        // se lista las incidencias existentes
                        List<Incidencia> incidencias = instanciaService.listarIncidencias();
                        for (Incidencia i : incidencias) {
                            System.out.println("ID: " + i.getId() + ", Título: " + i.getTitulo() + ", Fecha: " + i.getFechaHora());
                        }
                        System.out.print("ID de la incidencia: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.print("Nueva fecha y hora (YYYY-MM-DD HH:MM): ");
                        String nuevaFechaStr = scanner.nextLine();
                        LocalDateTime nuevaFecha = LocalDateTime.parse(nuevaFechaStr.replace(" ", "T"));
                        instanciaService.actualizarFechaIncidencia(id, nuevaFecha);
                        System.out.println("Fecha actualizada exitosamente.");
                    }
                    case 4 -> {
                        // Editar comentario de una incidencia
                        // mostrar las incidencias
                        List<Incidencia> incidencias = instanciaService.listarIncidencias();
                        for (Incidencia i : incidencias) {
                            System.out.println("ID: " + i.getId() + ", Título: " + i.getTitulo() + ", Comentario: " + i.getComentario());
                        }
                        System.out.print("ID de la incidencia: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.print("Nuevo comentario: ");
                        String nuevoComentario = scanner.nextLine();
                        instanciaService.actualizarComentarioIncidencia(id, nuevoComentario);
                        System.out.println("Comentario actualizado exitosamente.");
                    }
                    case 5 -> {
                        // Eliminar incidencia
                        List<Incidencia> incidencias = instanciaService.listarIncidencias();
                        for (Incidencia i : incidencias) {
                            System.out.println("ID: " + i.getId() + ", Título: " + i.getTitulo() + ", Fecha: " + i.getFechaHora());
                        }
                        System.out.print("ID de la incidencia a eliminar: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        instanciaService.eliminarInstanciaPorId(id);
                        System.out.println("Incidencia eliminada exitosamente.");
                    } case 6 -> {
                        // Editar canal de notificación de una incidencia
                        List<Incidencia> incidencias = instanciaService.listarIncidencias();
                        for (Incidencia i : incidencias) {
                            System.out.println("ID: " + i.getId() + ", Título: " + i.getTitulo() + ", Canal: " + i.getCanal());
                        }
                        System.out.print("ID de la incidencia: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.println("Canales de notificación...");
                        System.out.println("1. PRESENCIAL");
                        System.out.println("2. TELEFÓNICO");
                        System.out.println("3. EMAIL");
                        System.out.println("4. OTRO");
                        System.out.print("Ingrese el ID del nuevo canal: ");
                        int canalId = Integer.parseInt(scanner.nextLine());
                        Incidencia incidencia = incidencias.stream()
                                .filter(i -> i.getId() == id)
                                .findFirst()
                                .orElse(null);
                        if (incidencia == null) {
                            System.out.println("Incidencia no encontrada.");
                            return;
                        }

                        if (canalId == 1) {
                            incidencia.setCanal(Canal.PRESENCIAL);
                        } else if (canalId == 2) {
                            incidencia.setCanal(Canal.TELEFONICO);
                        } else if (canalId == 3) {
                            incidencia.setCanal(Canal.EMAIL);
                        } else {
                            incidencia.setCanal(Canal.OTRO);
                        }
                        instanciaService.actualizarIncidencia(incidencia);
                        System.out.println("Canal de notificación actualizado exitosamente.");
                    }
                    case 7 -> {
                        // Ver incidencias de un estudiante
                        // mostrar estudiantes del sistema
                        List<Estudiante> estudiantes = estudianteService.listarTodosEstudiantes();
                        for (Estudiante e : estudiantes) {
                            System.out.println("ID: " + e.getId() + ", Nombre: " + e.getNombre() + " " + e.getApellido());
                        }
                        System.out.print("Ingrese el ID del Estudiante: ");
                        int estudianteId = Integer.parseInt(scanner.nextLine());
                        List<Incidencia> incidenciasEstudiante = instanciaService.listarIncidencias().stream()
                                .filter(incidencia -> incidencia.getEstudianteAsociado().getId() == estudianteId)
                                .toList();
                        System.out.println("=== Incidencias asociadas al Estudiante ID " + estudianteId + " ===");
                        for (Incidencia incidencia : incidenciasEstudiante) {
                            if(incidenciasEstudiante.isEmpty()) {
                                System.out.println("No hay incidencias asociadas a este estudiante.");
                                return;
                            }
                            System.out.println("ID: " + incidencia.getId() + ", Título: " + incidencia.getTitulo() + ", Fecha: " + incidencia.getFechaHora());
                        }
                    }
                    case 0 -> {
                        return;  // Volver al menú principal
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void crearInstanciaComun(Funcionario psicopedagogo) throws Exception {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Comentario: ");
        String comentario = scanner.nextLine();

        // mostrar ID estudiantes + nombre  + apellidp
        List<Estudiante> estudiantes = estudianteService.listarTodosEstudiantes();
        for (Estudiante e : estudiantes) {
            System.out.println("ID: " + e.getId() + ", Nombre: " + e.getNombre() + " " + e.getApellido());
        }


        System.out.print("ID Estudiante: ");
        int estudianteId = Integer.parseInt(scanner.nextLine());
        System.out.print("Motivación: ");
        String motivacion = scanner.nextLine();
        System.out.println("Canales de notificación...");
        System.out.println("1. PRESENCIAL");
        System.out.println("2. TELEFÓNICO");
        System.out.println("3. EMAIL");
        System.out.println("4. OTRO");
        System.out.print("Ingrese el ID del canal: ");
        int canalId = Integer.parseInt(scanner.nextLine());

        System.out.print("Fecha y hora de la instancia (YYYY-MM-DD HH:MM): ");
        String fechaHoraStr = scanner.nextLine();
        LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr.replace(" ", "T"));

        List<Categoria> categorias = categoriaService.listarCategorias();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías disponibles.");
            return;
        }
        System.out.println("Categorías disponibles:");
        for (Categoria categoria : categorias) {
            System.out.println(categoria.getId() + ". " + categoria.getNombre());
        }
        System.out.print("Ingrese el ID de la categoría: ");
        int categoriaId = Integer.parseInt(scanner.nextLine());
        Categoria categoriaSeleccionada = categorias.stream()
                .filter(c -> c.getId() == categoriaId)
                .findFirst()
                .orElse(null);

        if (categoriaSeleccionada == null) {
            System.out.println("Categoría inválida.");
            return;
        }

        Estudiante estudiante = estudianteService.buscarEstudiantePorId(estudianteId);
        if (estudiante == null) {
            System.out.println("Estudiante no encontrado.");
            return;
        }

        // verificar que  ningun campo esté vacío
        if(titulo.isEmpty() || comentario.isEmpty() || motivacion.isEmpty()) {
            System.out.println("Todos los campos son obligatorios.");
            return;
        }

        InstanciaComun instanciaComun = new InstanciaComun();
        instanciaComun.setTitulo(titulo);
        instanciaComun.setComentario(comentario);
        instanciaComun.setEstudianteAsociado(estudiante);
        instanciaComun.setCreador(psicopedagogo);
        instanciaComun.setFechaHora(fechaHora);
        if (canalId == 1) {
            instanciaComun.setCanal(Canal.PRESENCIAL);
        } else if (canalId == 2) {
            instanciaComun.setCanal(Canal.TELEFONICO);
        } else if (canalId == 3) {
            instanciaComun.setCanal(Canal.EMAIL);
        } else {
            instanciaComun.setCanal(Canal.OTRO);
        }
        instanciaComun.setConfidencial(false);
        instanciaComun.setCategoria(categoriaSeleccionada);
        instanciaComun.setMotivacion(motivacion);
        instanciaComun.setTipo("COMUN");

        System.out.print("¿Desea crear un recordatorio en Google Calendar? (s/n): ");
        String respuesta = scanner.nextLine();
        boolean crearRecordatorio = respuesta.equalsIgnoreCase("s");

        if (crearRecordatorio) {
            System.out.println("El recordatorio se creará automáticamente en Google Calendar");
            System.out.println("Se enviará una notificación 30 minutos antes de la instancia");
        }

        instanciaService.createInstanciaComun(instanciaComun);

        if (crearRecordatorio && instanciaComun.getGoogleCalendarEventId() != null) {
            System.out.println("Recordatorio creado exitosamente en Google Calendar");
        }

        System.out.println("Instancia común creada exitosamente");
    }

    private void crearIncidencia(Funcionario psicopedagogo) throws Exception {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Comentario: ");
        String comentario = scanner.nextLine();
        // mostrar ID estudiantes + nombre  + apellidp
        List<Estudiante> estudiantes = estudianteService.listarTodosEstudiantes();
        for (Estudiante e : estudiantes) {
            System.out.println("ID: " + e.getId() + ", Nombre: " + e.getNombre() + " " + e.getApellido());
        }
        System.out.print("ID Estudiante: ");
        int estudianteId = Integer.parseInt(scanner.nextLine());
        System.out.print("Lugar: ");
        String lugar = scanner.nextLine();
        System.out.print("Personas involucradas (nombre y apellido separados por coma): ");
        String personas = scanner.nextLine();
        System.out.println("Fecha y hora de la incidencia (YYYY-MM-DD HH:MM): ");
        String fechaHoraStr = scanner.nextLine();

        // evitar fechas futuras al dia de hoy
        if(LocalDateTime.parse(fechaHoraStr.replace(" ", "T")).isAfter(LocalDateTime.now())) {
            System.out.println("La fecha y hora no puede ser futura.");
            return;
        }
        LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr.replace(" ", "T"));

        Estudiante estudiante = estudianteService.buscarEstudiantePorId(estudianteId);
        if (estudiante == null) {
            System.out.println("Estudiante no encontrado.");
            return;
        }

        List<Categoria> categorias = categoriaService.listarCategorias();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías disponibles.");
            return;
        }
        System.out.println("Categorías disponibles:");
        for (Categoria categoria : categorias) {
            System.out.println(categoria.getId() + ". " + categoria.getNombre());
        }
        System.out.print("Ingrese el ID de la categoría: ");
        int categoriaId = Integer.parseInt(scanner.nextLine());
        Categoria categoriaSeleccionada = categorias.stream()
                .filter(c -> c.getId() == categoriaId)
                .findFirst()
                .orElse(null);

        if (categoriaSeleccionada == null) {
            System.out.println("Categoría inválida.");
            return;
        }

        // verificar que no haya campos vacíos
        if(titulo.isEmpty() || comentario.isEmpty() || lugar.isEmpty() || personas.isEmpty() || fechaHoraStr.isEmpty() || estudianteId == 0) {
            System.out.println("Todos los campos son obligatorios.");
            return;
        }

        Incidencia incidencia = new Incidencia();
        incidencia.setTitulo(titulo);
        incidencia.setComentario(comentario);
        incidencia.setEstudianteAsociado(estudiante);
        incidencia.setCreador(psicopedagogo);
        incidencia.setFechaHora(fechaHora);
        incidencia.setCanal(Canal.PRESENCIAL);
        incidencia.setConfidencial(true);
        incidencia.setCategoria(categoriaSeleccionada);
        incidencia.setLugar(lugar);
        incidencia.setPersonasInvolucradas(List.of(personas.split(",")));
        incidencia.setReportadoPor(psicopedagogo);
        incidencia.setTipo("INCIDENCIA");

        instanciaService.createIncidencia(incidencia);
        System.out.println("✓ Incidencia creada exitosamente");
    }

    private void eliminarInstanciaComun() throws Exception {
        List<InstanciaComun> instancias = instanciaService.listarInstanciasComunes();
        for (InstanciaComun ic : instancias) {
            System.out.println("ID: " + ic.getId() + ", Título: " + ic.getTitulo() + ", Fecha: " + ic.getFechaHora());
        }
        System.out.print("ID de la instancia común a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());
        instanciaService.eliminarInstanciaPorId(id);
        System.out.println("Instancia común eliminada exitosamente.");
    }
}
