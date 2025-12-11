package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.security.MessageDigest;
import java.util.Optional;

public class LoginController {

    private UsuarioDAO usuarioDAO;

    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean authenticate(String credential, String password) {
        try {
            String hashedPassword = encryptSHA1(password);
            Optional<Usuario> usuarioOpt = usuarioDAO.autenticar(credential, hashedPassword);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                MainController.getInstance().setCurrentUser(usuario);
                redirectToDashboard(usuario);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            return false;
        }
    }

    private void redirectToDashboard(Usuario usuario) {
        Stage stage = MainController.getInstance().getPrimaryStage();

        switch (usuario.getTipoUsuario()) {
            case LECTOR:
                // placeholder: no dashboard especializado implementado
                stage.setScene(new Scene(new javafx.scene.control.Label("Lector"), 800, 600));
                break;
            case BIBLIOTECARIO:
                stage.setScene(new Scene(new javafx.scene.control.Label("Bibliotecario"), 800, 600));
                break;
            case ADMIN:
                stage.setScene(new Scene(new javafx.scene.control.Label("Admin"), 800, 600));
                break;
        }
        stage.setTitle("Biblioteca Digital - " + usuario.getTipoUsuario().getNombre());
        stage.setMaximized(true);
    }

    private String encryptSHA1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean registerUser(String dni, String nombre, String email, String telefono,
                                String direccion, String password) {
        try {
            // Verificar si el DNI ya existe
            if (usuarioDAO.existeDni(dni)) {
                return false;
            }

            // Verificar si el email ya existe
            if (usuarioDAO.existeEmail(email)) {
                return false;
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setDni(dni);
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setTelefono(telefono);
            nuevoUsuario.setDireccion(direccion);
            nuevoUsuario.setTipoUsuario(TipoUsuario.LECTOR);
            nuevoUsuario.setEstado(EstadoUsuario.ACTIVO);
            nuevoUsuario.setPasswordHash(encryptSHA1(password));
            nuevoUsuario.setLimitePrestamos(3);

            // Guardar en la base de datos
            usuarioDAO.save(nuevoUsuario);
            return true;

        } catch (Exception e) {
            System.err.println("Error en registro: " + e.getMessage());
            return false;
        }
    }
}