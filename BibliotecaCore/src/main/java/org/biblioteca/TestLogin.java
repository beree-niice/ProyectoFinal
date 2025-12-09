package org.biblioteca;

import org.biblioteca.controllers.AuthController;
import org.biblioteca.dao.ConexionBD;
import org.biblioteca.dao.UsuarioDAO;
import org.biblioteca.models.Usuario;
import org.biblioteca.models.enums.TipoUsuario;
import org.biblioteca.utils.PasswordHasher;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Scanner;

public class TestLogin {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   SISTEMA DE LOGIN - BIBLIOTECA");
        System.out.println("==========================================");

        // Inicializar conexión a la base de datos
        try {
            Connection connection = ConexionBD.getConnection();
            System.out.println("✓ Conexión a la base de datos establecida");
            System.out.println();
        } catch (Exception e) {
            System.err.println("✗ Error al conectar con la base de datos: " + e.getMessage());
            return;
        }

        // Crear controlador de autenticación
        AuthController authController = new AuthController();
        Scanner scanner = new Scanner(System.in);

        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Iniciar Sesión");
            System.out.println("2. Registrar Nuevo Usuario (LECTOR)");
            System.out.println("3. Ver Usuario Actual");
            System.out.println("4. Salir");
            System.out.print("\nSeleccione una opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                switch (opcion) {
                    case 1:
                        iniciarSesion(authController, scanner);
                        break;
                    case 2:
                        registrarUsuario(authController, scanner);
                        break;
                    case 3:
                        mostrarUsuarioActual(authController);
                        break;
                    case 4:
                        salir = true;
                        System.out.println("\n¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer
            }
        }

        scanner.close();
    }

    private static void iniciarSesion(AuthController authController, Scanner scanner) {
        System.out.println("\n=== INICIAR SESIÓN ===");

        System.out.print("Email o DNI: ");
        String emailODni = scanner.nextLine().trim();

        System.out.print("Contraseña: ");
        String password = scanner.nextLine().trim();

        System.out.println("\nVerificando credenciales...");

        if (authController.login(emailODni, password)) {
            System.out.println("✓ ¡Login exitoso!");

            Usuario usuario = authController.getUsuarioActual();
            mostrarInfoUsuario(usuario);

            // Mostrar permisos según tipo de usuario
            System.out.println("\n--- PERMISOS Y ACCESOS ---");
            System.out.println("- Puede ver catálogo: " + (authController.isLoggedIn() ? "SÍ" : "NO"));
            System.out.println("- Puede hacer préstamos: " + (authController.isBibliotecario() || authController.isAdmin() ? "SÍ" : "NO"));
            System.out.println("- Puede administrar usuarios: " + (authController.isAdmin() ? "SÍ" : "NO"));
            System.out.println("- Puede ver todos los préstamos: " + (authController.isBibliotecario() || authController.isAdmin() ? "SÍ" : "NO"));

        } else {
            System.out.println("✗ Error: Credenciales incorrectas o usuario inactivo");
        }
    }

    private static void mostrarInfoUsuario(Usuario usuario) {
        System.out.println("\n--- INFORMACIÓN DEL USUARIO ---");
        System.out.println("ID: " + usuario.getIdUsuario());
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("DNI: " + usuario.getDni());
        System.out.println("Teléfono: " + (usuario.getTelefono() != null ? usuario.getTelefono() : "No registrado"));

        // Mostrar tipo de usuario con descripción
        TipoUsuario tipo = usuario.getTipoUsuario();
        System.out.println("Tipo de Usuario: " + tipo.name() + " (" + tipo.getNombre() + ")");

        System.out.println("Estado: " + usuario.getEstado().getNombre());
        System.out.println("Fecha Registro: " + usuario.getFechaRegistro());
        System.out.println("Límite de Préstamos: " + usuario.getLimitePrestamos());

        // Mostrar rol específico
        System.out.print("Rol: ");
        if (usuario.esAdmin()) {
            System.out.println("ADMINISTRADOR - Acceso total al sistema");
        } else if (usuario.esBibliotecario()) {
            System.out.println("BIBLIOTECARIO - Gestión de préstamos y libros");
        } else if (usuario.esLector()) {
            System.out.println("LECTOR - Consulta y préstamo de libros");
        }
    }

    private static void registrarUsuario(AuthController authController, Scanner scanner) {
        System.out.println("\n=== REGISTRAR NUEVO USUARIO (LECTOR) ===");

        Usuario nuevoUsuario = new Usuario();

        System.out.print("DNI: ");
        String dni = scanner.nextLine().trim();
        nuevoUsuario.setDni(dni);

        System.out.print("Nombre completo: ");
        nuevoUsuario.setNombre(scanner.nextLine().trim());

        System.out.print("Email: ");
        nuevoUsuario.setEmail(scanner.nextLine().trim());

        System.out.print("Teléfono (opcional): ");
        String telefono = scanner.nextLine().trim();
        nuevoUsuario.setTelefono(telefono.isEmpty() ? null : telefono);

        System.out.print("Dirección (opcional): ");
        String direccion = scanner.nextLine().trim();
        nuevoUsuario.setDireccion(direccion.isEmpty() ? null : direccion);

        System.out.print("Contraseña: ");
        String password = scanner.nextLine().trim();

        System.out.println("\nRegistrando usuario...");

        if (authController.register(nuevoUsuario, password)) {
            System.out.println("✓ ¡Usuario registrado exitosamente!");
            System.out.println("   Tipo de usuario asignado: LECTOR");
            System.out.println("   Límite de préstamos: 3");

            // Iniciar sesión automáticamente
            if (authController.login(nuevoUsuario.getEmail(), password)) {
                System.out.println("✓ Sesión iniciada automáticamente");
                mostrarInfoUsuario(authController.getUsuarioActual());
            }
        } else {
            System.out.println("✗ Error al registrar usuario. Verifique los datos.");
        }
    }

    private static void mostrarUsuarioActual(AuthController authController) {
        if (authController.isLoggedIn()) {
            Usuario usuario = authController.getUsuarioActual();
            mostrarInfoUsuario(usuario);
        } else {
            System.out.println("\nNo hay usuario autenticado");
        }
    }

    // Método auxiliar para crear usuarios de prueba (si necesitas)
    public static void crearUsuariosDePrueba() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // Crear usuario ADMIN de prueba si no existe
            if (!usuarioDAO.existeEmail("admin@biblioteca.com")) {
                Usuario admin = new Usuario();
                admin.setDni("12345678A");
                admin.setNombre("Administrador Principal");
                admin.setEmail("admin@biblioteca.com");
                admin.setTipoUsuario(TipoUsuario.ADMIN);
                admin.setLimitePrestamos(10);
                admin.setFechaRegistro(LocalDate.now());

                String passwordHash = PasswordHasher.hashPassword("admin123");
                admin.setPasswordHash(passwordHash);

                usuarioDAO.save(admin);
                System.out.println("Usuario ADMIN creado: admin@biblioteca.com / admin123");
            }

            // Crear usuario BIBLIOTECARIO de prueba si no existe
            if (!usuarioDAO.existeEmail("biblio@biblioteca.com")) {
                Usuario biblio = new Usuario();
                biblio.setDni("87654321B");
                biblio.setNombre("Bibliotecario Principal");
                biblio.setEmail("biblio@biblioteca.com");
                biblio.setTipoUsuario(TipoUsuario.BIBLIOTECARIO);
                biblio.setLimitePrestamos(5);
                biblio.setFechaRegistro(LocalDate.now());

                String passwordHash = PasswordHasher.hashPassword("biblio123");
                biblio.setPasswordHash(passwordHash);

                usuarioDAO.save(biblio);
                System.out.println("Usuario BIBLIOTECARIO creado: biblio@biblioteca.com / biblio123");
            }

            // Crear usuario LECTOR de prueba si no existe
            if (!usuarioDAO.existeEmail("lector@biblioteca.com")) {
                Usuario lector = new Usuario();
                lector.setDni("11223344C");
                lector.setNombre("Lector de Prueba");
                lector.setEmail("lector@biblioteca.com");
                lector.setTipoUsuario(TipoUsuario.LECTOR);
                lector.setLimitePrestamos(3);
                lector.setFechaRegistro(LocalDate.now());

                String passwordHash = PasswordHasher.hashPassword("lector123");
                lector.setPasswordHash(passwordHash);

                usuarioDAO.save(lector);
                System.out.println("Usuario LECTOR creado: lector@biblioteca.com / lector123");
            }

        } catch (Exception e) {
            System.err.println("Error al crear usuarios de prueba: " + e.getMessage());
        }
    }
}