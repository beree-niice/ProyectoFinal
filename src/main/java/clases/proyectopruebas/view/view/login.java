package clases.proyectopruebas.view.view;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import clases.proyectopruebas.view.AdminView;
import clases.proyectopruebas.view.BibliotecarioView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.security.MessageDigest;

public class login {

    // Mapa de colores para cada tipo de usuario
    private static java.util.Map<TipoUsuario, String> roleColors = new java.util.HashMap<>();
    static {
        roleColors.put(TipoUsuario.LECTOR, "#27ae60");
        roleColors.put(TipoUsuario.BIBLIOTECARIO, "#3498db");
        roleColors.put(TipoUsuario.ADMIN, "#9b59b6");
    }

    // Login Screen
    public static class Login extends Application {

        private TextField usernameField;
        private PasswordField passwordField;
        private Button loginButton;
        private Label statusLabel;

        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Biblioteca Digital - Inicio de Sesion");

            BorderPane mainLayout = new BorderPane();
            mainLayout.setPadding(new Insets(20));

            VBox headerBox = createHeader();
            GridPane loginForm = createLoginForm();
            VBox footerBox = createFooter();

            mainLayout.setTop(headerBox);
            mainLayout.setCenter(loginForm);
            mainLayout.setBottom(footerBox);

            mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #adcbe8, #2b3d4e);");

            Scene scene = new Scene(mainLayout, 550, 550); // Aumentado para la imagen

            loginButton.setOnAction(e -> handleLogin(primaryStage));

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }

        private VBox createHeader() {
            VBox header = new VBox(15);
            header.setAlignment(Pos.CENTER);
            header.setPadding(new Insets(0, 0, 20, 0));

            try {
                // Cargar imagen desde resources
                InputStream is = getClass().getResourceAsStream("/imagenes/EncabezadoLogin.jpg");
                if (is != null) {
                    Image logoImage = new Image(is);
                    ImageView logoView = new ImageView(logoImage);

                    // Ajustar tamaño de la imagen
                    logoView.setFitHeight(300);
                    logoView.setFitWidth(300);
                    logoView.setPreserveRatio(true);
                    logoView.setSmooth(true);
                    logoView.setCache(true);

                    // Efectos visuales
                    logoView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

                    header.getChildren().add(logoView);
                } else {
                    // Si no hay imagen, mostrar icono de texto
                    Label iconLabel = new Label("");
                    iconLabel.setStyle("-fx-font-size: 60px;");
                    header.getChildren().add(iconLabel);
                }
            } catch (Exception e) {
                // En caso de error, mostrar icono de texto
                Label iconLabel = new Label("");
                iconLabel.setStyle("-fx-font-size: 60px;");
                header.getChildren().add(iconLabel);
            }

            // Títulos
           /* Label titleLabel = new Label("BIBLIOTECA DIGITAL");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            titleLabel.setTextFill(Color.WHITE);
            titleLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 1);");
*/
            Label subtitleLabel = new Label("Sistema de Gestion");
            subtitleLabel.setFont(Font.font("Arial", 16));
            subtitleLabel.setTextFill(Color.BLACK);

            header.getChildren().addAll(/*titleLabel,*/ subtitleLabel);
            return header;
        }

        private GridPane createLoginForm() {
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(15);
            grid.setVgap(20);
            grid.setPadding(new Insets(25));
            grid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-border-color: #3498db;" +
                    "-fx-border-width: 2;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 0);");

            Label formTitle = new Label("INICIAR SESION");
            formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            formTitle.setTextFill(Color.web("#2c3e50"));
            GridPane.setColumnSpan(formTitle, 2);
            GridPane.setHalignment(formTitle, javafx.geometry.HPos.CENTER);
            grid.add(formTitle, 0, 0);

            Label userLabel = new Label("Email o DNI:");
            userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            grid.add(userLabel, 0, 1);

            usernameField = new TextField();
            usernameField.setPromptText("Ingrese su email o DNI");
            usernameField.setPrefWidth(200);
            usernameField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
            grid.add(usernameField, 1, 1);

            Label passLabel = new Label("Contraseña:");
            passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            grid.add(passLabel, 0, 2);

            passwordField = new PasswordField();
            passwordField.setPromptText("Ingrese su contraseña");
            passwordField.setPrefWidth(200);
            passwordField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
            grid.add(passwordField, 1, 2);

            return grid;
        }

        private VBox createFooter() {
            VBox footer = new VBox(15);
            footer.setAlignment(Pos.CENTER);
            footer.setPadding(new Insets(20, 0, 0, 0));

            // 1. Inicializar loginButton
            loginButton = new Button("ACCEDER");
            loginButton.setStyle("-fx-background-color: #3498db;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 10 30;" +
                    "-fx-background-radius: 25;" +
                    "-fx-border-radius: 25;" +
                    "-fx-cursor: hand;");
            loginButton.setOnMouseEntered(e ->
                    loginButton.setStyle("-fx-background-color: #2980b9;"));
            loginButton.setOnMouseExited(e ->
                    loginButton.setStyle("-fx-background-color: #3498db;"));

            // 2. Inicializar statusLabel (solo UNA vez)
            statusLabel = new Label("");
            statusLabel.setFont(Font.font("Arial", 12));
            statusLabel.setTextFill(Color.WHITE);

            // 3. Crear Hyperlinks
            Hyperlink registerLink = new Hyperlink("Registrarse como lector");
            registerLink.setTextFill(Color.LIGHTBLUE);
            registerLink.setOnAction(e -> showRegistrationScreen());

            Hyperlink recoverLink = new Hyperlink("¿Olvidó su contraseña?");
            recoverLink.setTextFill(Color.LIGHTBLUE);
            recoverLink.setOnAction(e -> showRecoveryDialog());

            Hyperlink inicioLink = new Hyperlink("Volver al inicio");
            inicioLink.setTextFill(Color.web("#3498db"));
            inicioLink.setOnAction(e -> {
                Bienvenida bienvenida = new Bienvenida();
                Stage stage = (Stage) inicioLink.getScene().getWindow();
                bienvenida.start(stage);
            });

            // 4. Añadir componentes al VBox - SIN DUPLICADOS
            // Asegúrate de que cada componente se añade solo UNA vez
            footer.getChildren().addAll(
                    loginButton,    // ← Button
                    statusLabel,    // ← Label (solo una vez)
                    inicioLink,     // ← Hyperlink
                    registerLink,   // ← Hyperlink
                    recoverLink     // ← Hyperlink
            );

            return footer;
        }

        private void handleLogin(Stage stage) {
            String credential = usernameField.getText();
            String pass = passwordField.getText();

            if (credential.isEmpty() || pass.isEmpty()) {
                statusLabel.setText("Por favor complete todos los campos");
                statusLabel.setTextFill(Color.YELLOW);
                return;
            }

            statusLabel.setText("Validando credenciales...");
            statusLabel.setTextFill(Color.LIGHTGREEN);

            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                String hashedPassword = encryptSHA1(pass);
                java.util.Optional<Usuario> usuarioOpt = usuarioDAO.autenticar(credential, hashedPassword);

                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    statusLabel.setText("Acceso concedido");
                    redirectBasedOnRole(stage, usuario);
                } else {
                    statusLabel.setText("Credenciales incorrectas o usuario inactivo");
                    statusLabel.setTextFill(Color.ORANGE);
                }
            } catch (Exception e) {
                statusLabel.setText("Error de conexion: " + e.getMessage());
                statusLabel.setTextFill(Color.RED);
            }
        }

        private void redirectBasedOnRole(Stage stage, Usuario usuario) {
            String color = roleColors.get(usuario.getTipoUsuario());

            switch (usuario.getTipoUsuario()) {
                case LECTOR:
                    LectorView lectorDashboard = new LectorView();
                    stage.setScene(new Scene(lectorDashboard.getView(usuario), 1200, 700));
                    break;
                case BIBLIOTECARIO:
                    BibliotecarioView bibliotecarioDashboard = new BibliotecarioView();
                    stage.setScene(new Scene(bibliotecarioDashboard.getView(usuario), 1200, 700));
                    break;
                case ADMIN:
                    AdminView adminDashboard = new AdminView();
                    stage.setScene(new Scene(adminDashboard.getView(usuario), 1200, 700));
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

        private void showRegistrationScreen() {
            Stage registerStage = new Stage();
            Registro registration = new Registro();
            registration.start(registerStage);
        }

        private void showRecoveryDialog() {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Recuperar Contrasena");
            alert.setHeaderText("Funcionalidad en desarrollo");
            alert.setContentText("Por favor contacte al administrador del sistema.");
            alert.showAndWait();
        }
    }
}