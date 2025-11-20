package facade;

import auth.AuthManager;
import model.*;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;
import service.*;
import service.impl.PermisoServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminFacade implements MenuFacade {
    private final AuthManager authManager;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final CategoriaService categoriaService;
    private final InstanciaService instanciaService;
    private final EstudianteService estudianteService;
    private final FuncionarioService funcionarioService;
    private final PermisoService permisoService;
    private final Scanner scanner = new Scanner(System.in);

    public AdminFacade(AuthManager authManager, UsuarioService usuarioService, RolService rolService,
                       CategoriaService categoriaService, InstanciaService instanciaService,
                       EstudianteService estudianteService, FuncionarioService funcionarioService,
                       PermisoService permisoService) {
        this.authManager = authManager;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.categoriaService = categoriaService;
        this.instanciaService = instanciaService;
        this.estudianteService = estudianteService;
        this.funcionarioService = funcionarioService;
        this.permisoService = permisoService;
    }

    @Override
    public void mostrarMenu() {
        System.out.println("=== Menú Administrador ===");
        System.out.println("1. Gestionar usuarios");
        System.out.println("2. Gestionar roles");
        System.out.println("3. Gestionar categorías");
        System.out.println("4. Ver instancias");
        System.out.println("0. Cerrar sesión");
        System.out.print("Ingrese una opción: ");
    }

    @Override
    public void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> gestionarUsuarios();
                case 2 -> gestionarRoles();
                case 3 -> gestionarCategorias();
                case 4 -> {
                    List<Instancia> instancias = instanciaService.listarTodasLasInstancias();
                    for(Instancia i : instancias) {
                        System.out.println("ID: " + i.getId() + ", Titulo: " + i.getTitulo() + ", Estudiante: " + i.getEstudianteAsociado().getNombre() + " " + i.getEstudianteAsociado().getApellido());
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

    private void gestionarUsuarios() throws Exception {
        System.out.println("=== Gestión de Usuarios ===");
        System.out.println("1. Ver usuarios");
        System.out.println("2. Crear usuario");
        System.out.println("3. Editar usuario");
        System.out.println("4. Eliminar usuario");
        System.out.println("5. Cambiar contraseña");
        System.out.println("Ingresar opción: ");
        int subOpcion = Integer.parseInt(scanner.nextLine());
        switch (subOpcion) {
            case 1 -> {
                List<Usuario> usuarios = usuarioService.listarUsuarios();
                for (Usuario u : usuarios) {
                    System.out.println("ID: " + u.getId() + ", Nombre: " + u.getNombre() + ", Email: " + u.getEmail() + " , Tipo: " + u.getTipo());
                }
            }
            case 2 -> {
                System.out.println("=== Crear Usuario ===");
                System.out.println("1. Estudiante");
                System.out.println("2. Funcionario");
                System.out.println("Ingrese el tipo de usuario a crear: ");
                int tipoUsuario = Integer.parseInt(scanner.nextLine());

                if(tipoUsuario == 1) {
                    Estudiante estudiante = new Estudiante();
                    estudiante.setTipo(TipoUsuario.ESTUDIANTE);

                    System.out.print("Nombre: ");
                    estudiante.setNombre(scanner.nextLine());
                    System.out.print("Apellido: ");
                    estudiante.setApellido(scanner.nextLine());
                    System.out.print("Email: ");
                    estudiante.setEmail(scanner.nextLine());
                    System.out.print("Documento: ");
                    estudiante.setDocumento(scanner.nextLine());
                    System.out.print("Contraseña: ");
                    estudiante.setContrasenia(scanner.nextLine());
                    System.out.print("Carrera: ");
                    estudiante.setCarrera(scanner.nextLine());
                    System.out.print("Grupo: ");
                    estudiante.setGrupo(scanner.nextLine());
                    System.out.print("Teléfono: ");
                    estudiante.setTelefono(scanner.nextLine());
                    System.out.print("Calle: ");
                    estudiante.setCalle(scanner.nextLine());
                    System.out.print("Número Puerta: ");
                    estudiante.setNumeroPuerta(scanner.nextLine());
                    System.out.print("Fecha Nacimiento (YYYY-MM-DD): ");
                    estudiante.setFechaNacimiento(LocalDate.parse(scanner.nextLine()));
                    System.out.print("Sistema Salud: ");
                    estudiante.setSistemaSalud(scanner.nextLine());
                    System.out.print("Comentarios Generales: ");
                    estudiante.setComentariosGenerales(scanner.nextLine());
                    System.out.print("Estado Salud: ");
                    estudiante.setEstadoSalud(scanner.nextLine());
                    System.out.print("Observaciones Confidenciales (separadas por coma): ");
                    estudiante.setObservacionesConfidenciales(List.of(scanner.nextLine().split(",")));

                    estudiante.setEstado(EstadoUsuario.ACTIVO);

                    estudianteService.guardarEstudiante(estudiante);
                    System.out.println("Estudiante creado exitosamente.");
                } else if (tipoUsuario == 2) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setTipo(TipoUsuario.FUNCIONARIO);

                    System.out.print("Nombre: ");
                    funcionario.setNombre(scanner.nextLine());
                    System.out.print("Apellido: ");
                    funcionario.setApellido(scanner.nextLine());
                    System.out.print("Email: ");
                    funcionario.setEmail(scanner.nextLine());
                    System.out.print("Documento: ");
                    funcionario.setDocumento(scanner.nextLine());
                    System.out.print("Contraseña: ");
                    funcionario.setContrasenia(scanner.nextLine());

                    // MOSTRAR ROLES DISPONIBLES
                    System.out.println("\n=== Roles Disponibles ===");
                    List<Rol> roles = rolService.obtenerTodosLosRoles();
                    if (roles == null || roles.isEmpty()) {
                        System.out.println("No hay roles disponibles. Debe crear roles primero.");
                        return;
                    }

                    for (Rol r : roles) {
                        System.out.println(r.getId() + ". " + r.getNombre());
                    }

                    System.out.print("ID Rol: ");
                    int idRol = Integer.parseInt(scanner.nextLine());

                    // VALIDAR QUE EL ROL EXISTE
                    Rol rol = rolService.buscarRolPorId(idRol);
                    if (rol == null) {
                        System.out.println("Error: El rol con ID " + idRol + " no existe.");
                        return;
                    }


                    funcionario.setRol(rol);
                    funcionario.setEstado(EstadoUsuario.ACTIVO);

                    funcionarioService.guardarFuncionario(funcionario);
                    System.out.println("Funcionario creado exitosamente con rol: " + rol.getNombre());
                } else {
                    System.out.println("Tipo de usuario no válido.");
                }
            } case 3 -> {
                System.out.println("=== Editar Usuario ===");
                System.out.println("Nota: Solo se pueden editar teléfono y dirección de Estudiantes");
                System.out.println("1. Editar teléfono");
                System.out.println("2. Editar dirección");
                System.out.print("Ingrese una opción: ");
                int option = Integer.parseInt(scanner.nextLine());

                if(option == 1 || option == 2) {
                    // listar estudiantes
                    List<Estudiante> estudiantes = estudianteService.listarTodosEstudiantes();
                    if(estudiantes == null || estudiantes.isEmpty()) {
                        System.out.println("No hay estudiantes registrados.");
                        return;
                    }

                    System.out.println("=== Estudiantes Registrados ===");
                    for(Estudiante e : estudiantes) {
                        System.out.println("ID: " + e.getId() + ", Nombre: " + e.getNombre() + " " + e.getApellido() + "(" + e.getDocumento() +")");
                    }

                    System.out.println("ID del estudiante a editar: ");
                    int idEstudiante = Integer.parseInt(scanner.nextLine());

                    Estudiante estudiante = estudianteService.buscarEstudiantePorId(idEstudiante);
                    if(estudiante == null) {
                        System.out.println("Estudiante no encontrado.");
                        return;
                    }

                    if(option == 1) {
                        // editar telefono
                        System.out.println("Teléfono actual: " + estudiante.getTelefono());
                        System.out.println("Nuevo teléfono: ");
                        String nuevoTelefono = scanner.nextLine();
                        estudiante.setTelefono(nuevoTelefono);
                    } else if (option == 2) {
                        // editar direccion (calle + numero de puerta)
                        System.out.println("Dirección actual: " + estudiante.getCalle() + " " + estudiante.getNumeroPuerta());
                        System.out.println("Nueva calle: ");
                        String nuevaCalle = scanner.nextLine();
                        System.out.println("Nuevo número de puerta: ");
                        String nuevoNumeroPuerta = scanner.nextLine();
                        estudiante.setCalle(nuevaCalle);
                        estudiante.setNumeroPuerta(nuevoNumeroPuerta);
                    }

                    try {
                        estudianteService.actualizarEstudiante(estudiante);
                        System.out.println("Estudiante actualizado exitosamente.");
                    } catch (Exception e) {
                        System.out.println("Error al actualizar el estudiante: " + e.getMessage());
                    }
                } else {
                    System.out.println("Opción no válida.");
                }
            }
            case 4 -> {
                System.out.println("=== Eliminar Usuario ===");
                System.out.print("ID del usuario: ");
                int id = Integer.parseInt(scanner.nextLine());

                usuarioService.eliminarUsuario(id);
                System.out.println("Usuario eliminado.");
            }
            case 5 -> {
                System.out.println("=== Cambiar Contraseña ===");
                // mostrar ID de usuarios nombre y apellido
                List<Usuario> usuarios = usuarioService.listarUsuarios();
                for (Usuario u : usuarios) {
                    System.out.println("ID: " + u.getId() + ", Nombre: " + u.getNombre() + " " + u.getApellido());
                }
                System.out.println("ID del usuario: ");
                int id = Integer.parseInt(scanner.nextLine());

                Usuario usuario = usuarioService.buscarUsuarioPorId(id);
                if(usuario == null) {
                    System.out.println("Usuario no encontrado.");
                    return;
                }

                System.out.println("Usuario: " + usuario.getNombre() + " " + usuario.getApellido());
                System.out.println("Nueva contraseña: ");
                String nuevaContrasenia = scanner.nextLine();
                System.out.println("Confirmar nueva contraseña: ");
                String confirmarContrasenia = scanner.nextLine();

                if(!nuevaContrasenia.equals(confirmarContrasenia)) {
                    System.out.println("Las contraseñas no coinciden.");
                    return;
                }

                if(nuevaContrasenia.length() < 6) {
                    System.out.println("La contraseña debe tener al menos 6 caracteres.");
                    return;
                }

                usuarioService.cambiarContrasenia(id, nuevaContrasenia);
                System.out.println("Contraseña actualizada exitosamente.");
            }
        }
    }

    private void gestionarRoles() throws Exception {
        System.out.println("=== Gestionar Roles ===");
        System.out.println("1. Ver roles");
        System.out.println("2. Crear rol");
        System.out.println("3. Editar rol");
        System.out.println("4. Eliminar rol");
        System.out.println("5. Agregar permisos a un rol");
        System.out.println("6. Quitar permisos de un rol");
        System.out.print("Ingrese una opción: ");
        int subOpcion = Integer.parseInt(scanner.nextLine());

        switch (subOpcion) {
            case 1 -> {
                List<Rol> roles = rolService.obtenerTodosLosRoles();
                if(roles == null || roles.isEmpty()) {
                    System.out.println("No hay roles registrados.");
                    return;
                }
                System.out.println("=== Roles ===");
                for (Rol r : roles) {
                    System.out.println("ID: " + r.getId() + ", Nombre: " + r.getNombre());
                    if (r.getPermisos() != null && !r.getPermisos().isEmpty()) {
                        System.out.print("  Permisos: ");
                        for (Permiso p : r.getPermisos()) {
                            System.out.print(p.getNombre() + " ");
                        }
                        System.out.println();
                    } else {
                        System.out.println("  Sin permisos asignados.");
                    }
                }
            }
            case 2 -> {
                System.out.println("=== Crear Rol ===");
                System.out.print("Nombre: ");
                String nombre = scanner.nextLine().trim();
                if (nombre.isEmpty()) {
                    System.out.println("El nombre no puede estar vacío.");
                    return;
                }
                List<Permiso> permisosDisponibles = permisoService.obtenerTodosLosPermisos();
                if (permisosDisponibles == null || permisosDisponibles.isEmpty()) {
                    System.out.println("No hay permisos disponibles. Debe crear permisos primero.");
                    return;
                }
                System.out.println("=== Permisos Disponibles ===");
                for (Permiso p : permisosDisponibles) {
                    System.out.println(p.getId() + ". " + p.getNombre());
                }
                System.out.print("Ingrese IDs de permisos a asignar (separados por coma, o vacío para ninguno): ");
                String inputPermisos = scanner.nextLine().trim();
                List<Permiso> permisosSeleccionados = new ArrayList<>();
                if (!inputPermisos.isEmpty()) {
                    String[] ids = inputPermisos.split(",");
                    for (String idStr : ids) {
                        try {
                            int id = Integer.parseInt(idStr.trim());
                            Permiso permiso = permisosDisponibles.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
                            if (permiso != null) {
                                permisosSeleccionados.add(permiso);
                            } else {
                                System.out.println("Permiso con ID " + id + " no encontrado. Ignorado.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("ID inválido: " + idStr.trim() + ". Ignorado.");
                        }
                    }
                }

                if (permisosSeleccionados.isEmpty()) {
                    System.out.println("Error: Un rol debe tener al menos un permiso asignado. Intente de nuevo.");
                    return;  // Sale del caso sin guardar
                }
                Rol rol = new Rol();
                rol.setNombre(nombre);
                rol.setPermisos(permisosSeleccionados);
                try {
                    rolService.guardarRol(rol);
                    System.out.println("Rol creado exitosamente.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error al crear rol: " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.println("=== Editar Rol ===");
                // mostrar roles existentes
                List<Rol> roles = rolService.obtenerTodosLosRoles();
                for (Rol r : roles) {
                    System.out.println("ID: " + r.getId() + ", Nombre: " + r.getNombre());
                }
                System.out.print("ID del rol: ");
                int id = Integer.parseInt(scanner.nextLine());
                Rol rol = rolService.buscarRolPorId(id);
                if (rol == null) {
                    System.out.println("Rol no encontrado.");
                    return;
                }
                System.out.print("Nuevo nombre (actual: " + rol.getNombre() + "): ");
                String nuevoNombre = scanner.nextLine().trim();
                if (!nuevoNombre.isEmpty()) {
                    rol.setNombre(nuevoNombre);
                }

                // Editar permisos
                List<Permiso> permisosDisponibles = permisoService.obtenerTodosLosPermisos();
                if (permisosDisponibles == null || permisosDisponibles.isEmpty()) {
                    System.out.println("No hay permisos disponibles para editar.");
                } else {
                    System.out.println("=== Permisos Disponibles ===");
                    for (Permiso p : permisosDisponibles) {
                        System.out.println(p.getId() + ". " + p.getNombre());
                    }
                    System.out.print("Ingrese IDs de permisos a asignar (separados por coma, reemplazará los actuales): ");
                    String inputPermisos = scanner.nextLine().trim();
                    List<Permiso> nuevosPermisos = new ArrayList<>();
                    if (!inputPermisos.isEmpty()) {
                        String[] ids = inputPermisos.split(",");
                        for (String idStr : ids) {
                            try {
                                int permId = Integer.parseInt(idStr.trim());
                                Permiso permiso = permisosDisponibles.stream().filter(p -> p.getId() == permId).findFirst().orElse(null);
                                if (permiso != null) {
                                    nuevosPermisos.add(permiso);
                                } else {
                                    System.out.println("Permiso con ID " + permId + " no encontrado. Ignorado.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("ID inválido: " + idStr.trim() + ". Ignorado.");
                            }
                        }
                    }
                    rol.setPermisos(nuevosPermisos);
                }
                try {
                    rolService.actualizarRol(rol);
                    System.out.println("Rol actualizado exitosamente.");
                } catch (Exception e) {
                    System.out.println("Error al actualizar rol: " + e.getMessage());
                }
            }
            case 4 -> {
                System.out.println("=== Eliminar Rol ===");
                // mostrar roles existentes
                List<Rol> roles = rolService.obtenerTodosLosRoles();
                for (Rol r : roles) {
                    System.out.println("ID: " + r.getId() + ", Nombre: " + r.getNombre());
                }
                System.out.print("ID del rol: ");
                int id = Integer.parseInt(scanner.nextLine());
                try {
                    rolService.eliminarRol(id);
                    System.out.println("Rol eliminado exitosamente.");
                } catch (IllegalStateException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error al eliminar rol: " + e.getMessage());
                }
            }
            case 5 -> agregarPermisosARol();
            case 6 -> quitarPermisosARol();
            default -> System.out.println("Opción inválida.");
        }
    }

    private void agregarPermisosARol() throws Exception {
        System.out.println("=== Agregar Permisos a un Rol ===");
        // Listar roles existentes
        List<Rol> roles = rolService.obtenerTodosLosRoles();
        if (roles == null || roles.isEmpty()) {
            System.out.println("No hay roles registrados.");
            return;
        }
        System.out.println("=== Roles Disponibles ===");
        for (Rol r : roles) {
            System.out.println("ID: " + r.getId() + ", Nombre: " + r.getNombre());
        }
        System.out.print("ID del rol a modificar: ");
        int idRol = Integer.parseInt(scanner.nextLine());
        Rol rol = rolService.buscarRolPorId(idRol);
        if (rol == null) {
            System.out.println("Rol no encontrado.");
            return;
        }
        // Listar permisos disponibles (sin mostrar los que ya tiene)
        List<Permiso> permisosDisponibles = permisoService.obtenerTodosLosPermisos();
        if (permisosDisponibles == null || permisosDisponibles.isEmpty()) {
            System.out.println("No hay permisos disponibles.");
            return;
        }
        List<Permiso> permisosActuales = rol.getPermisos() != null ? rol.getPermisos() : new ArrayList<>();
        List<Permiso> permisosParaAgregar = permisosDisponibles.stream()
                .filter(p -> !permisosActuales.contains(p))
                .toList();
        if (permisosParaAgregar.isEmpty()) {
            System.out.println("El rol ya tiene todos los permisos disponibles.");
            return;
        }
        System.out.println("=== Permisos Disponibles para Agregar ===");
        for (Permiso p : permisosParaAgregar) {
            System.out.println(p.getId() + ". " + p.getNombre());
        }
        System.out.print("Ingrese IDs de permisos a agregar (separados por coma): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("No se agregaron permisos.");
            return;
        }
        String[] ids = input.split(",");
        boolean agregado = false;
        for (String idStr : ids) {
            try {
                int idPermiso = Integer.parseInt(idStr.trim());
                Permiso permiso = permisosParaAgregar.stream().filter(p -> p.getId() == idPermiso).findFirst().orElse(null);
                if (permiso != null) {
                    rolService.agregarPermisoARol(idRol, idPermiso);
                    System.out.println("Permiso '" + permiso.getNombre() + "' agregado al rol.");
                    agregado = true;
                } else {
                    System.out.println("Permiso con ID " + idPermiso + " no válido o ya asignado. Ignorado.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ID inválido: " + idStr.trim() + ". Ignorado.");
            }
        }
        if (agregado) {
            System.out.println("Permisos agregados exitosamente.");
        }
    }


    private void quitarPermisosARol() throws Exception {
        System.out.println("=== Quitar Permisos a un Rol ===");
        // Listar roles existentes
        List<Rol> roles = rolService.obtenerTodosLosRoles();
        if (roles == null || roles.isEmpty()) {
            System.out.println("No hay roles registrados.");
            return;
        }
        System.out.println("=== Roles Disponibles ===");
        for (Rol r : roles) {
            System.out.println("ID: " + r.getId() + ", Nombre: " + r.getNombre());
        }
        System.out.print("ID del rol a modificar: ");
        int idRol = Integer.parseInt(scanner.nextLine());
        Rol rol = rolService.buscarRolPorId(idRol);
        if (rol == null) {
            System.out.println("Rol no encontrado.");
            return;
        }
        // Listar permisos actuales del rol
        List<Permiso> permisosActuales = rol.getPermisos();
        if (permisosActuales == null || permisosActuales.isEmpty()) {
            System.out.println("El rol no tiene permisos asignados.");
            return;
        }
        System.out.println("=== Permisos Actuales del Rol ===");
        for (Permiso p : permisosActuales) {
            System.out.println(p.getId() + ". " + p.getNombre());
        }
        System.out.print("Ingrese IDs de permisos a quitar (separados por coma): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("No se quitaron permisos.");
            return;
        }
        String[] ids = input.split(",");
        boolean quitado = false;
        for (String idStr : ids) {
            try {
                int idPermiso = Integer.parseInt(idStr.trim());
                Permiso permiso = permisosActuales.stream().filter(p -> p.getId() == idPermiso).findFirst().orElse(null);
                if (permiso != null) {
                    rolService.quitarPermisoARol(idRol, idPermiso);
                    System.out.println("Permiso '" + permiso.getNombre() + "' quitado del rol.");
                    quitado = true;
                } else {
                    System.out.println("Permiso con ID " + idPermiso + " no asignado al rol. Ignorado.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ID inválido: " + idStr.trim() + ". Ignorado.");
            }
        }
        if (quitado) {
            System.out.println("Permisos quitados exitosamente.");
        }
    }

    private void gestionarCategorias() throws Exception {
        System.out.println("=== Gestionar Categorías ===");
        System.out.println("1. Ver categorías");
        System.out.println("2. Crear categoría");
        System.out.println("3. Editar categoría");
        System.out.println("4. Eliminar categoría");
        System.out.print("Ingrese una opción: ");
        int subOpcion = Integer.parseInt(scanner.nextLine());

        switch (subOpcion) {
            case 1 -> {
                List<Categoria> categorias = categoriaService.listarCategorias();
                System.out.println("=== Categorías ===");
                for (Categoria c : categorias) {
                    System.out.println("ID: " + c.getId() + ", Nombre: " + c.getNombre() + ", Descripción: " + c.getDescripcion());
                }
            }
            case 2 -> {
                System.out.println("=== Crear Categoría ===");
                System.out.print("Nombre: ");
                String nombre = scanner.nextLine();
                System.out.print("Descripción: ");
                String descripcion = scanner.nextLine();

                Categoria categoria = new Categoria();
                categoria.setNombre(nombre);
                categoria.setDescripcion(descripcion);

                categoriaService.guardarCategoria(categoria);
                System.out.println("Categoría creada.");
            }
            case 3 -> {
                System.out.println("=== Editar Categoría ===");
                // listar categorias existentes
                List<Categoria> categorias = categoriaService.listarCategorias();
                for (Categoria c : categorias) {
                    System.out.println("ID: " + c.getId() + ", Nombre: " + c.getNombre());
                }
                System.out.print("ID de la categoría: ");
                int id = Integer.parseInt(scanner.nextLine());
                Categoria categoria = categoriaService.buscarCategoriaPorId(id);
                if (categoria == null) {
                    System.out.println("Categoría no encontrada.");
                    return;
                }
                System.out.print("Nuevo nombre: ");
                categoria.setNombre(scanner.nextLine());
                System.out.print("Nueva descripción: ");
                categoria.setDescripcion(scanner.nextLine());

                categoriaService.actualizarCategoria(categoria);
                System.out.println("Categoría actualizada.");
            }
            case 4 -> {
                System.out.println("=== Eliminar Categoría ===");
                List<Categoria> categorias = categoriaService.listarCategorias();
                for (Categoria c : categorias) {
                    System.out.println("ID: " + c.getId() + ", Nombre: " + c.getNombre());
                }
                System.out.print("ID de la categoría: ");
                int id = Integer.parseInt(scanner.nextLine());

                categoriaService.eliminarCategoria(id);
                System.out.println("Categoría eliminada.");
            }
            default -> System.out.println("Opción inválida.");
        }
    }
}
