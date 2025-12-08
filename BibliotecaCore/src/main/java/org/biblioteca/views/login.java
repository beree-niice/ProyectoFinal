package org.biblioteca.views;

import org.biblioteca.dao.UsuarioDAO;
import org.biblioteca.models.Usuario;
import org.biblioteca.models.enums.TipoUsuario;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.security.MessageDigest;

public class login{

    public static class Login extends Application {

        private TextField usernameField;
        private PasswordField passwordField;
        private Button loginButton;
        private Label statusLabel;
        private UsuarioDAO usuarioDAO;

        public Login() {
            this.usuarioDAO = new UsuarioDAO();
        }

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

            mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

            Scene scene = new Scene(mainLayout, 500, 500);

            loginButton.setOnAction(e -> handleLogin(primaryStage));

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }

        private VBox createHeader() {
            VBox header = new VBox(10);
            header.setAlignment(Pos.CENTER);
            header.setPadding(new Insets(0, 0, 30, 0));

            try {
                InputStream imageStream = getClass().getResourceAsStream("/imagenes/EncabezadoLogin.jpg");
                if (imageStream != null) {
                    Image logoImage = new Image(imageStream);
                    ImageView logoView = new ImageView(logoImage);


                    logoView.setFitWidth(300);
                    logoView.setFitHeight(80);
                    logoView.setPreserveRatio(true);
                    logoView.setSmooth(true);
                    logoView.setCache(true);

                    header.getChildren().add(logoView);

                    Label subtitleLabel = new Label("Sistema de Gestion");
                    subtitleLabel.setFont(Font.font("Arial", 16));
                    subtitleLabel.setTextFill(Color.LIGHTGRAY);

                    header.getChildren().add(subtitleLabel);
                } else {
                    System.err.println("No se pudo cargar la imagen: EncabezadoLogin.jpg");
                    mostrarTituloAlternativo(header);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar la imagen: " + e.getMessage());
                mostrarTituloAlternativo(header);
            }

            return header;
        }

        private void mostrarTituloAlternativo(VBox header) {
            Label titleLabel = new Label("BIBLIOTECA DIGITAL");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            titleLabel.setTextFill(Color.WHITE);
            titleLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 1);");

            Label subtitleLabel = new Label("Sistema de Gestion");
            subtitleLabel.setFont(Font.font("Arial", 16));
            subtitleLabel.setTextFill(Color.LIGHTGRAY);

            header.getChildren().addAll(titleLabel, subtitleLabel);
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

            Label userLabel = new Label("Usuario:");
            userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            grid.add(userLabel, 0, 1);

            usernameField = new TextField();
            usernameField.setPromptText("Email: ");
            usernameField.setPrefWidth(200);
            usernameField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
            grid.add(usernameField, 1, 1);

            Label passLabel = new Label("Contraseña:");
            passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            grid.add(passLabel, 0, 2);

            passwordField = new PasswordField();
            passwordField.setPromptText("Contraseña");
            passwordField.setPrefWidth(200);
            passwordField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
            grid.add(passwordField, 1, 2);

            return grid;
        }

        private VBox createFooter() {
            VBox footer = new VBox(15);
            footer.setAlignment(Pos.CENTER);
            footer.setPadding(new Insets(20, 0, 0, 0));

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

            statusLabel = new Label("");
            statusLabel.setFont(Font.font("Arial", 12));
            statusLabel.setTextFill(Color.WHITE);

            Hyperlink registerLink = new Hyperlink("Registrarse como Lector");
            registerLink.setTextFill(Color.LIGHTBLUE);
            registerLink.setOnAction(e -> showRegistrationDialog());

            Hyperlink recoverLink = new Hyperlink("¿Olvido su contraseña?");
            recoverLink.setTextFill(Color.LIGHTBLUE);
            recoverLink.setOnAction(e -> showRecoveryDialog());

            footer.getChildren().addAll(loginButton, statusLabel, registerLink, recoverLink);
            return footer;
        }

        private void handleLogin(Stage stage) {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                statusLabel.setText("Por favor complete todos los campos");
                statusLabel.setTextFill(Color.YELLOW);
            } else {
                statusLabel.setText("Validando credenciales...");
                statusLabel.setTextFill(Color.LIGHTGREEN);

                String hashedPassword = encryptSHA1(pass);
                Usuario usuario = usuarioDAO.autenticar(user, hashedPassword).orElse(null);

                if (usuario != null) {
                    statusLabel.setText("Acceso concedido");
                    redirectBasedOnRole(stage, usuario);
                } else {
                    statusLabel.setText("Credenciales incorrectas o usuario inactivo");
                    statusLabel.setTextFill(Color.ORANGE);
                }
            }
        }

        private void redirectBasedOnRole(Stage stage, Usuario usuario) {
            String color = getColorForRole(usuario.getTipoUsuario());

            switch (usuario.getTipoUsuario()) {
                case LECTOR:
                    UserDashboard userDashboard = new UserDashboard();
                    stage.setScene(new Scene(userDashboard.getUserView(usuario, color), 1200, 700));
                    break;
                case BIBLIOTECARIO:
                    LibrarianDashboard libDashboard = new LibrarianDashboard();
                    stage.setScene(new Scene(libDashboard.getLibrarianView(usuario, color), 1200, 700));
                    break;
                case ADMIN:
                    AdminDashboard adminDashboard = new AdminDashboard();
                    stage.setScene(new Scene(adminDashboard.getAdminView(usuario, color), 1200, 700));
                    break;
            }
            stage.setTitle("Biblioteca Digital - " + usuario.getTipoUsuario().getNombre());
            stage.setMaximized(true);
        }

        private String getColorForRole(TipoUsuario tipo) {
            switch (tipo) {
                case LECTOR: return "#B0C4DE";
                case BIBLIOTECARIO: return "#7FFFD4";
                case ADMIN: return "#DDA0DD";
                default: return "#2c3e50";
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

        private void showRegistrationDialog() {
            RegistrationDialog dialog = new RegistrationDialog();
            dialog.showAndWait();
        }

        private void showRecoveryDialog() {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Recuperar Contrasena");
            alert.setHeaderText("Funcionalidad en desarrollo");
            alert.setContentText("Por favor contacte al administrador del sistema.");
            alert.showAndWait();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}