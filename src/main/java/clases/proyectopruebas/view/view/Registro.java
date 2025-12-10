package clases.proyectopruebas.view.view;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.security.MessageDigest;

public class Registro extends Application {

    private TextField dniField, nombreField, emailField, telefonoField;
    private PasswordField passwordField, confirmPasswordField;
    private TextArea direccionArea;
    private Button registerButton;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Registro de Lector - Biblioteca Digital");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #fbe7e7;");

        VBox headerBox = createHeader();
        ScrollPane formContainer = createRegistrationForm();
        VBox footerBox = createFooter();

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(formContainer);
        mainLayout.setBottom(footerBox);

        Scene scene = new Scene(mainLayout, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 20, 0));

        Label titleLabel = new Label("REGISTRO DE NUEVO LECTOR");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        Label subtitleLabel = new Label("Complete el formulario para registrarse");
        subtitleLabel.setFont(Font.font("Arial", 14));
        subtitleLabel.setTextFill(Color.web("#7f8c8d"));

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private ScrollPane createRegistrationForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25));
        grid.setStyle("-fx-background-color: #74a9b3;" +
                "-fx-border-color: rgba(124,84,137,0.79);" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1;");

        // DNI
        Label dniLabel = new Label("DNI:");
        dniLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(dniLabel, 0, 0);

        dniField = new TextField();
        dniField.setPromptText("Ingrese su DNI");
        dniField.setPrefWidth(250);
        grid.add(dniField, 1, 0);

        // Nombre
        Label nombreLabel = new Label("Nombre Completo:");
        nombreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(nombreLabel, 0, 1);

        nombreField = new TextField();
        nombreField.setPromptText("Ingrese su nombre completo");
        nombreField.setPrefWidth(250);
        grid.add(nombreField, 1, 1);

        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(emailLabel, 0, 2);

        emailField = new TextField();
        emailField.setPromptText("ejemplo@email.com");
        emailField.setPrefWidth(250);
        grid.add(emailField, 1, 2);

        // Teléfono
        Label telefonoLabel = new Label("Teléfono:");
        telefonoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(telefonoLabel, 0, 3);

        telefonoField = new TextField();
        telefonoField.setPromptText("(Opcional)");
        telefonoField.setPrefWidth(250);
        grid.add(telefonoField, 1, 3);

        // Dirección
        Label direccionLabel = new Label("Dirección:");
        direccionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(direccionLabel, 0, 4);

        direccionArea = new TextArea();
        direccionArea.setPromptText("Ingrese su dirección completa");
        direccionArea.setPrefRowCount(3);
        direccionArea.setPrefWidth(250);
        grid.add(direccionArea, 1, 4);

        // Contraseña
        Label passLabel = new Label("Contraseña:");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(passLabel, 0, 5);

        passwordField = new PasswordField();
        passwordField.setPromptText("Mínimo 6 caracteres");
        passwordField.setPrefWidth(250);
        grid.add(passwordField, 1, 5);

        // Confirmar Contraseña
        Label confirmPassLabel = new Label("Confirmar Contraseña:");
        confirmPassLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(confirmPassLabel, 0, 6);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Repita la contraseña");
        confirmPasswordField.setPrefWidth(250);
        grid.add(confirmPasswordField, 1, 6);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        return scrollPane;
    }

    private VBox createFooter() {
        VBox footer = new VBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));

        registerButton = new Button("REGISTRARSE");
        registerButton.setStyle("-fx-background-color: #27ae60;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10 30;" +
                "-fx-background-radius: 25;" +
                "-fx-border-radius: 25;" +
                "-fx-cursor: hand;");
        registerButton.setOnMouseEntered(e ->
                registerButton.setStyle("-fx-background-color: #219653;"));
        registerButton.setOnMouseExited(e ->
                registerButton.setStyle("-fx-background-color: #27ae60;"));
        registerButton.setOnAction(e -> handleRegistration());

        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 12));

        Hyperlink backLink = new Hyperlink("Volver al login");
        backLink.setTextFill(Color.web("#3498db"));
        backLink.setOnAction(e -> {
            login.Login login = new login.Login();
            Stage stage = (Stage) backLink.getScene().getWindow();
            login.start(stage);
        });
// En el método createFooter():
        Hyperlink inicioLink = new Hyperlink("Volver al inicio");
        inicioLink.setTextFill(Color.web("#3498db"));
        inicioLink.setOnAction(e -> {
            Bienvenida bienvenida = new Bienvenida();
            Stage stage = (Stage) inicioLink.getScene().getWindow();
            bienvenida.start(stage);
        });

        footer.getChildren().addAll(registerButton, statusLabel, backLink,inicioLink);
        return footer;
    }

    private void handleRegistration() {
        // Validaciones
        if (dniField.getText().isEmpty() || nombreField.getText().isEmpty() ||
                emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statusLabel.setText("Complete los campos obligatorios");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            statusLabel.setText("Las contraseñas no coinciden");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        if (passwordField.getText().length() < 6) {
            statusLabel.setText("La contraseña debe tener al menos 6 caracteres");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // Verificar si el DNI ya existe
            if (usuarioDAO.existeDni(dniField.getText())) {
                statusLabel.setText("El DNI ya está registrado");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            // Verificar si el email ya existe
            if (usuarioDAO.existeEmail(emailField.getText())) {
                statusLabel.setText("El email ya está registrado");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setDni(dniField.getText());
            nuevoUsuario.setNombre(nombreField.getText());
            nuevoUsuario.setEmail(emailField.getText());
            nuevoUsuario.setTelefono(telefonoField.getText());
            nuevoUsuario.setDireccion(direccionArea.getText());
            nuevoUsuario.setTipoUsuario(TipoUsuario.LECTOR);
            nuevoUsuario.setEstado(EstadoUsuario.ACTIVO);
            nuevoUsuario.setPasswordHash(encryptSHA1(passwordField.getText()));
            nuevoUsuario.setLimitePrestamos(3);

            // Guardar en la base de datos
            usuarioDAO.save(nuevoUsuario);

            statusLabel.setText("¡Registro exitoso! Ahora puede iniciar sesión");
            statusLabel.setTextFill(Color.GREEN);

            // Limpiar campos
            clearFields();

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
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

    private void clearFields() {
        dniField.clear();
        nombreField.clear();
        emailField.clear();
        telefonoField.clear();
        direccionArea.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

}