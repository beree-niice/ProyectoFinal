package org.biblioteca.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginScreen extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        // Configurar ventana principal
        primaryStage.setTitle("Biblioteca Digital - Inicio de Sesión");

        // Layout principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        // Panel superior con logo/título
        VBox headerBox = createHeader();

        // Panel central con formulario
        GridPane loginForm = createLoginForm();

        // Panel inferior con botón y estado
        VBox footerBox = createFooter();

        // Ensamblar layout
        mainLayout.setTop(headerBox);
        mainLayout.setCenter(loginForm);
        mainLayout.setBottom(footerBox);

        // Estilo de fondo
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");

        Scene scene = new Scene(mainLayout, 500, 550);

        // Manejar evento del botón login
        loginButton.setOnAction(e -> handleLogin());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(5); // Reducí el espaciado
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10, 0, 20, 0));

        try {
            Image logoImage = new Image("/imagenes/EmcabezadoLogin.jpg");

            ImageView logoImageView = new ImageView(logoImage);

            logoImageView.setFitHeight(120);
            logoImageView.setFitWidth(400);
            logoImageView.setPreserveRatio(true);


            header.getChildren().add(logoImageView);

        } catch (Exception e) {
            System.err.println("Error al cargar la imagen del encabezado: " + e.getMessage());

            ImageView placeholder = new ImageView();
            placeholder.setFitHeight(120);
            placeholder.setFitWidth(400);
            placeholder.setStyle("-fx-background-color: rgba(255,255,255,0.1); " +
                    "-fx-border-color: white; " +
                    "-fx-border-width: 1; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

            Label errorLabel = new Label("Logo Biblioteca Digital");
            errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            errorLabel.setTextFill(Color.WHITE);
            StackPane placeholderPane = new StackPane(errorLabel);
            placeholderPane.setPrefSize(400, 120);
            placeholder.setImage(null);

            header.getChildren().add(placeholderPane);
        }

        Label subtitleLabel = new Label("Sistema de Gestión");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        header.getChildren().add(subtitleLabel);

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

        Label formTitle = new Label("INICIAR SESIÓN");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        formTitle.setTextFill(Color.web("#2c3e50"));
        GridPane.setColumnSpan(formTitle, 2);
        GridPane.setHalignment(formTitle, javafx.geometry.HPos.CENTER);
        grid.add(formTitle, 0, 0);

        // Usuario
        Label userLabel = new Label("Usuario:");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(userLabel, 0, 1);

        usernameField = new TextField();
        usernameField.setPromptText("Ingrese su nombre de usuario");
        usernameField.setPrefWidth(200);
        usernameField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
        grid.add(usernameField, 1, 1);

        // Contraseña
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

        // Botón de login
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
                loginButton.setStyle(loginButton.getStyle() + "-fx-background-color: #2980b9;"));
        loginButton.setOnMouseExited(e ->
                loginButton.setStyle(loginButton.getStyle() + "-fx-background-color: #3498db;"));

        // Label de estado
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setTextFill(Color.WHITE);

        footer.getChildren().addAll(loginButton, statusLabel);
        return footer;
    }

    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Por favor complete todos los campos");
            statusLabel.setTextFill(Color.YELLOW);
        } else {
            statusLabel.setText("Validando credenciales...");
            statusLabel.setTextFill(Color.LIGHTGREEN);
        }
    }


    private void openMainDashboard() {
        System.out.println("Abriendo dashboard principal...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
