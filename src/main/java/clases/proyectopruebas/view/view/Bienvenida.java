package clases.proyectopruebas.view.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Bienvenida extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Biblioteca Digital - Bienvenida");

        // Layout principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #4a6491);");

        // Encabezado
        VBox header = crearEncabezado();

        // Contenido central
        VBox centerContent = crearContenidoCentral();

        // Pie de página
        VBox footer = crearPieDePagina();

        mainLayout.setTop(header);
        mainLayout.setCenter(centerContent);
        mainLayout.setBottom(footer);

        Scene scene = new Scene(mainLayout, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox crearEncabezado() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 0, 20, 0));

        try {
            // Intenta cargar la imagen del encabezado
            Image logoImage = new Image(getClass().getResourceAsStream("/imagenes/EncabezadoLogin.jpg"));
            ImageView logoView = new ImageView(logoImage);

            // Ajustar tamaño
            logoView.setFitHeight(150);
            logoView.setFitWidth(150);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
            logoView.setCache(true);

            // Efecto de sombra
            logoView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.5), 10, 0, 0, 0);");

            header.getChildren().add(logoView);
        } catch (Exception e) {
            // Si no hay imagen, muestra un icono de libro
            Label iconLabel = new Label("");
            iconLabel.setFont(Font.font("Arial", 60));
            iconLabel.setTextFill(Color.WHITE);
            header.getChildren().add(iconLabel);
        }

        Label titulo = new Label("BIBLIOTECA DIGITAL");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);
        titulo.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 5, 0, 0, 1);");

        Label subtitulo = new Label("Tu portal hacia el conocimiento");
        subtitulo.setFont(Font.font("Arial", 16));
        subtitulo.setTextFill(Color.LIGHTGRAY);

        header.getChildren().addAll(titulo, subtitulo);

        return header;
    }

    private VBox crearContenidoCentral() {
        VBox centerContent = new VBox(30);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20, 50, 20, 50));

        // Mensaje de bienvenida
        VBox mensajeBox = new VBox(15);
        mensajeBox.setAlignment(Pos.CENTER);

        Label bienvenidaLabel = new Label("¡Bienvenido!");
        bienvenidaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        bienvenidaLabel.setTextFill(Color.WHITE);

        // Botones de opciones
        HBox botonesBox = new HBox(20);
        botonesBox.setAlignment(Pos.CENTER);
        botonesBox.setPadding(new Insets(20, 0, 20, 0));

        // Botón Iniciar Sesión
        Button btnLogin = new Button("INICIAR SESIÓN");
        btnLogin.setStyle("-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 16px;" +
                "-fx-padding: 15 40;" +
                "-fx-background-radius: 30;" +
                "-fx-border-radius: 30;" +
                "-fx-cursor: hand;");
        btnLogin.setOnMouseEntered(e ->
                btnLogin.setStyle("-fx-background-color: #2980b9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 15 40;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-radius: 30;" +
                        "-fx-cursor: hand;"));
        btnLogin.setOnMouseExited(e ->
                btnLogin.setStyle("-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 15 40;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-radius: 30;" +
                        "-fx-cursor: hand;"));

        // Botón Registrarse
        Button btnRegistro = new Button("REGISTRARSE");
        btnRegistro.setStyle("-fx-background-color: #27ae60;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 16px;" +
                "-fx-padding: 15 40;" +
                "-fx-background-radius: 30;" +
                "-fx-border-radius: 30;" +
                "-fx-cursor: hand;");
        btnRegistro.setOnMouseEntered(e ->
                btnRegistro.setStyle("-fx-background-color: #219653;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 15 40;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-radius: 30;" +
                        "-fx-cursor: hand;"));
        btnRegistro.setOnMouseExited(e ->
                btnRegistro.setStyle("-fx-background-color: #27ae60;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 15 40;" +
                        "-fx-background-radius: 30;" +
                        "-fx-border-radius: 30;" +
                        "-fx-cursor: hand;"));

        // Acciones de los botones
        btnLogin.setOnAction(e -> {
            login.Login loginApp = new login.Login();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            loginApp.start(stage);
        });

        btnRegistro.setOnAction(e -> {
            Registro registroApp = new Registro();
            Stage stage = (Stage) btnRegistro.getScene().getWindow();
            registroApp.start(stage);
        });

        botonesBox.getChildren().addAll(btnLogin, btnRegistro);

        // Características destacadas
        VBox caracteristicasBox = new VBox(10);
        caracteristicasBox.setAlignment(Pos.CENTER);
        caracteristicasBox.setPadding(new Insets(20, 0, 0, 0));

        Label caracteristicasLabel = new Label("Características destacadas:");
        caracteristicasLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        caracteristicasLabel.setTextFill(Color.WHITE);

        HBox iconosBox = new HBox(30);
        iconosBox.setAlignment(Pos.CENTER);

        VBox[] caracteristicas = {
                crearCaracteristica("📚", "Catálogo variado", "Multiples libros disponibles")
        };

        iconosBox.getChildren().addAll(caracteristicas);

        caracteristicasBox.getChildren().addAll(caracteristicasLabel, iconosBox);

        centerContent.getChildren().addAll(mensajeBox, botonesBox, caracteristicasBox);

        return centerContent;
    }

    private VBox crearCaracteristica(String icono, String titulo, String descripcion) {
        VBox caracteristica = new VBox(5);
        caracteristica.setAlignment(Pos.CENTER);
        caracteristica.setPrefWidth(150);

        Label iconoLabel = new Label(icono);
        iconoLabel.setFont(Font.font("Arial", 24));
        iconoLabel.setTextFill(Color.WHITE);

        Label tituloLabel = new Label(titulo);
        tituloLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        tituloLabel.setTextFill(Color.WHITE);
        tituloLabel.setAlignment(Pos.CENTER);
        tituloLabel.setWrapText(true);

        Label descLabel = new Label(descripcion);
        descLabel.setFont(Font.font("Arial", 10));
        descLabel.setTextFill(Color.LIGHTGRAY);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setWrapText(true);

        caracteristica.getChildren().addAll(iconoLabel, tituloLabel, descLabel);

        return caracteristica;
    }

    private VBox crearPieDePagina() {
        VBox footer = new VBox(10);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 20, 0));
        footer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");

        Label versionLabel = new Label("Versión 2.0");
        versionLabel.setFont(Font.font("Arial", 10));
        versionLabel.setTextFill(Color.LIGHTGRAY);

        Label derechosLabel = new Label("© 2025 Biblioteca Digital - TOPICOS AVANZADOS DE PROGRAMACIÓN");
        derechosLabel.setFont(Font.font("Arial", 10));
        derechosLabel.setTextFill(Color.LIGHTGRAY);

        // Botón para salir
        Button btnSalir = new Button("Salir");
        btnSalir.setStyle("-fx-background-color: transparent;" +
                "-fx-text-fill: #e74c3c;" +
                "-fx-border-color: #e74c3c;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 15;" +
                "-fx-padding: 5 20;" +
                "-fx-cursor: hand;");
        btnSalir.setOnMouseEntered(e ->
                btnSalir.setStyle("-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #e74c3c;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 15;" +
                        "-fx-padding: 5 20;" +
                        "-fx-cursor: hand;"));
        btnSalir.setOnMouseExited(e ->
                btnSalir.setStyle("-fx-background-color: transparent;" +
                        "-fx-text-fill: #e74c3c;" +
                        "-fx-border-color: #e74c3c;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 15;" +
                        "-fx-padding: 5 20;" +
                        "-fx-cursor: hand;"));

        btnSalir.setOnAction(e -> {
            Stage stage = (Stage) btnSalir.getScene().getWindow();
            stage.close();
        });

        footer.getChildren().addAll(versionLabel, derechosLabel, btnSalir);

        return footer;
    }

    // Para ejecutar desde el Main
    public static void main(String[] args) {
        launch(args);
    }
}