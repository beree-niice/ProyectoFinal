package org.biblioteca.views;

import org.biblioteca.dao.UsuarioDAO;
import org.biblioteca.models.Usuario;
import org.biblioteca.models.enums.TipoUsuario;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import java.security.MessageDigest;

public class Registro extends Dialog<Usuario> {

    private UsuarioDAO usuarioDAO;
    private TextField nombreField;
    private TextField emailField;
    private TextField telefonoField;
    private TextArea direccionArea;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    public RegistrationDialog() {
        this.usuarioDAO = new UsuarioDAO();
        initDialog();
    }

    private void initDialog() {
        setTitle("Registro de Nuevo Lector");
        setHeaderText("Complete sus datos para registrarse");

        // Crear botones
        ButtonType registerButtonType = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        // Crear contenido
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Campos del formulario
        nombreField = new TextField();
        nombreField.setPromptText("Nombre completo");

        emailField = new TextField();
        emailField.setPromptText("correo@ejemplo.com");

        telefonoField = new TextField();
        telefonoField.setPromptText("Teléfono de contacto");

        direccionArea = new TextArea();
        direccionArea.setPromptText("Dirección completa");
        direccionArea.setPrefRowCount(3);

        passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña (mínimo 6 caracteres)");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmar contraseña");

        // Agregar campos al grid
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(nombreField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Teléfono:"), 0, 3);
        grid.add(telefonoField, 1, 3);
        grid.add(new Label("Dirección:"), 0, 4);
        grid.add(direccionArea, 1, 4);
        grid.add(new Label("Contraseña:"), 0, 5);
        grid.add(passwordField, 1, 5);
        grid.add(new Label("Confirmar:"), 0, 6);
        grid.add(confirmPasswordField, 1, 6);

        getDialogPane().setContent(grid);

        // Configurar validación
        setResultConverter(buttonType -> {
            if (buttonType == registerButtonType) {
                return validateAndCreateUser();
            }
            return null;
        });

        // Deshabilitar botón registrar hasta que los campos estén válidos
        Button registerButton = (Button) getDialogPane().lookupButton(registerButtonType);
        registerButton.setDisable(true);

        // Validar en tiempo real
        nombreField.textProperty().addListener((obs, old, newValue) -> validateFields(registerButton));
        emailField.textProperty().addListener((obs, old, newValue) -> validateFields(registerButton));
        passwordField.textProperty().addListener((obs, old, newValue) -> validateFields(registerButton));
        confirmPasswordField.textProperty().addListener((obs, old, newValue) -> validateFields(registerButton));
    }

    private void validateFields(Button registerButton) {
        boolean valid = !nombreField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                !passwordField.getText().isEmpty() &&
                passwordField.getText().length() >= 6 &&
                passwordField.getText().equals(confirmPasswordField.getText());

        registerButton.setDisable(!valid);
    }

    private Usuario validateAndCreateUser() {

        // Validar email único
        if (usuarioDAO.existeEmail(emailField.getText())) {
            showError("Email ya registrado");
            return null;
        }

        // Validar contraseñas coinciden
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Las contraseñas no coinciden");
            return null;
        }

        try {
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombreField.getText());
            usuario.setEmail(emailField.getText());
            usuario.setTelefono(telefonoField.getText());
            usuario.setDireccion(direccionArea.getText());
            usuario.setTipoUsuario(TipoUsuario.LECTOR);
            usuario.setPasswordHash(encryptSHA1(passwordField.getText()));

            // Guardar en BD
            Usuario saved = usuarioDAO.save(usuario);

            if (saved != null) {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Registro Exitoso");
                success.setHeaderText("Usuario registrado correctamente");
                success.setContentText("Ahora puede iniciar sesión con sus credenciales.");
                success.showAndWait();

                return saved;
            }

        } catch (Exception e) {
            showError("Error al registrar: " + e.getMessage());
        }

        return null;
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Registro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}