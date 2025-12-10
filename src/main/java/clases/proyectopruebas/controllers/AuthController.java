package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import clases.proyectopruebas.utils.PasswordHasher;

import java.time.LocalDate;
import java.util.Optional;

public class AuthController {
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;

    public AuthController() {
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioActual = null;
    }

    /**
     * Autentica un usuario con email/DNI y contraseña
     */
    public boolean login(String emailODni, String password) {
        try {
            // Hashear la contraseña para comparar
            String passwordHash = PasswordHasher.hashPassword(password);

            Optional<Usuario> usuarioOpt = usuarioDAO.autenticar(emailODni, passwordHash);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();

                // Verificar que el usuario esté activo
                if (!usuario.estaActivo()) {
                    System.err.println("Usuario no activo. Estado: " + usuario.getEstado());
                    return false;
                }

                this.usuarioActual = usuario;
                return true;
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean logout() {
        this.usuarioActual = null;
        return true;
    }

    /**
     * Registra un nuevo usuario (siempre como LECTOR)
     */
    public boolean register(Usuario nuevoUsuario, String password) {
        // Validaciones básicas
        if (nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().trim().isEmpty()) {
            System.err.println("Error: Nombre es requerido");
            return false;
        }

        if (nuevoUsuario.getEmail() == null || nuevoUsuario.getEmail().trim().isEmpty()) {
            System.err.println("Error: Email es requerido");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            System.err.println("Error: Contraseña es requerida");
            return false;
        }

        if (nuevoUsuario.getDni() == null || nuevoUsuario.getDni().trim().isEmpty()) {
            System.err.println("Error: DNI es requerido");
            return false;
        }

        // Verificar si el email ya existe
        if (usuarioDAO.existeEmail(nuevoUsuario.getEmail())) {
            System.err.println("Error: El email ya está registrado");
            return false;
        }

        // Verificar si el DNI ya existe
        if (usuarioDAO.existeDni(nuevoUsuario.getDni())) {
            System.err.println("Error: El DNI ya está registrado");
            return false;
        }

        // Forzar tipo LECTOR y estado ACTIVO para registros nuevos
        nuevoUsuario.setTipoUsuario(TipoUsuario.LECTOR);
        nuevoUsuario.setEstado(EstadoUsuario.ACTIVO);
        nuevoUsuario.setLimitePrestamos(3);
        nuevoUsuario.setFechaRegistro(LocalDate.now());

        // Hashear la contraseña
        String passwordHash = PasswordHasher.hashPassword(password);
        nuevoUsuario.setPasswordHash(passwordHash);

        try {
            return usuarioDAO.save(nuevoUsuario) != null;
        } catch (Exception e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean isLoggedIn() {
        return usuarioActual != null;
    }

    // Métodos para verificar tipo de usuario
    public boolean isAdmin() {
        return isLoggedIn() && usuarioActual.esAdmin();
    }

    public boolean isBibliotecario() {
        return isLoggedIn() && usuarioActual.esBibliotecario();
    }

    public boolean isLector() {
        return isLoggedIn() && usuarioActual.esLector();
    }

    // Método genérico para verificar cualquier tipo
    public boolean hasRole(TipoUsuario tipoUsuario) {
        return isLoggedIn() && usuarioActual.getTipoUsuario() == tipoUsuario;
    }

    /**
     * Cambia la contraseña del usuario actual
     */
    public boolean cambiarPassword(String nuevaPassword) {
        if (!isLoggedIn()) return false;

        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            System.err.println("Error: La nueva contraseña no puede estar vacía");
            return false;
        }

        String passwordHash = PasswordHasher.hashPassword(nuevaPassword);
        usuarioActual.setPasswordHash(passwordHash);

        try {
            return usuarioDAO.update(usuarioActual) != null;
        } catch (Exception e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el perfil del usuario actual
     */
    public boolean actualizarPerfil(Usuario usuarioActualizado) {
        if (!isLoggedIn()) return false;

        // Validaciones
        if (usuarioActualizado.getNombre() == null || usuarioActualizado.getNombre().trim().isEmpty()) {
            System.err.println("Error: El nombre es requerido");
            return false;
        }

        if (usuarioActualizado.getEmail() == null || usuarioActualizado.getEmail().trim().isEmpty()) {
            System.err.println("Error: El email es requerido");
            return false;
        }

        if (usuarioActualizado.getDni() == null || usuarioActualizado.getDni().trim().isEmpty()) {
            System.err.println("Error: El DNI es requerido");
            return false;
        }

        // Mantener datos que no deben cambiar
        usuarioActualizado.setIdUsuario(usuarioActual.getIdUsuario());
        usuarioActualizado.setTipoUsuario(usuarioActual.getTipoUsuario()); // No cambiar tipo
        usuarioActualizado.setPasswordHash(usuarioActual.getPasswordHash()); // Mantener contraseña
        usuarioActualizado.setEstado(usuarioActual.getEstado()); // Mantener estado
        usuarioActualizado.setLimitePrestamos(usuarioActual.getLimitePrestamos());
        usuarioActualizado.setFechaRegistro(usuarioActual.getFechaRegistro());

        // Verificar si el email fue cambiado y si ya existe
        if (!usuarioActual.getEmail().equalsIgnoreCase(usuarioActualizado.getEmail())) {
            if (usuarioDAO.existeEmail(usuarioActualizado.getEmail())) {
                System.err.println("Error: El nuevo email ya está registrado");
                return false;
            }
        }

        // Verificar si el DNI fue cambiado y si ya existe
        if (!usuarioActual.getDni().equals(usuarioActualizado.getDni())) {
            if (usuarioDAO.existeDni(usuarioActualizado.getDni())) {
                System.err.println("Error: El nuevo DNI ya está registrado");
                return false;
            }
        }

        try {
            boolean resultado = usuarioDAO.update(usuarioActualizado) != null;
            if (resultado) {
                this.usuarioActual = usuarioActualizado;
            }
            return resultado;

        } catch (Exception e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un email está disponible
     */
    public boolean isEmailAvailable(String email) {
        try {
            return !usuarioDAO.existeEmail(email);
        } catch (Exception e) {
            System.err.println("Error al verificar email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un DNI está disponible
     */
    public boolean isDniAvailable(String dni) {
        try {
            return !usuarioDAO.existeDni(dni);
        } catch (Exception e) {
            System.err.println("Error al verificar DNI: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida credenciales sin iniciar sesión
     */
    public boolean validarCredenciales(String emailODni, String password) {
        try {
            String passwordHash = PasswordHasher.hashPassword(password);
            return usuarioDAO.autenticar(emailODni, passwordHash).isPresent();
        } catch (Exception e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene información del usuario actual para mostrar
     */
    public String getInfoUsuarioActual() {
        if (!isLoggedIn()) {
            return "No hay usuario autenticado";
        }

        return String.format("%s - %s (%s)",
                usuarioActual.getNombre(),
                usuarioActual.getEmail(),
                usuarioActual.getTipoUsuario().getNombre()
        );
    }

    /**
     * Reinicia el controlador
     */
    public void reset() {
        this.usuarioActual = null;
    }
}