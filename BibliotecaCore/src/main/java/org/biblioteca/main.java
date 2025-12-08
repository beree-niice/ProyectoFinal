package org.biblioteca;

import org.biblioteca.views.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación Biblioteca Digital
 * Punto de entrada del sistema
 */
public class Main extends Application {

    private static final String APP_TITLE = "Sistema de Gestión de Biblioteca Digital";
    private static final String APP_VERSION = "1.0.0";

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Configurar la ventana principal
            primaryStage.setTitle(APP_TITLE);

            // 2. Configurar ícono de la aplicación
            try {
                primaryStage.getIcons().add(new Image(
                        getClass().getResourceAsStream("/images/library-icon.png")
                ));
            } catch (Exception e) {
                System.out.println("No se pudo cargar el ícono de la aplicación");
            }

            // 3. Verificar conexión a la base de datos
            if (!testDatabaseConnection()) {
                showDatabaseError();
                return;
            }

            // 4. Mostrar splash screen inicial
            showSplashScreen(primaryStage);

            // 5. Configurar cierre seguro
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                exitApplication(primaryStage);
            });

        } catch (Exception e) {
            showStartupError(e);
        }
    }

    /**
     * Muestra una pantalla de carga inicial
     */
    private void showSplashScreen(Stage primaryStage) {
        SplashScreen splash = new SplashScreen();
        Scene splashScene = splash.createScene();

        primaryStage.setScene(splashScene);
        primaryStage.setWidth(600);
        primaryStage.setHeight(400);
        primaryStage.centerOnScreen();
        primaryStage.show();

        // Esperar 2 segundos y luego mostrar login
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> showLoginScreen(primaryStage));
            } catch (InterruptedException e) {
                Platform.runLater(() -> showLoginScreen(primaryStage));
            }
        }).start();
    }

    /**
     * Muestra la pantalla de login
     */
    private void showLoginScreen(Stage primaryStage) {
        AuthenticationSystem.LoginScreen loginScreen = new AuthenticationSystem.LoginScreen();
        try {
            // Usamos reflection para llamar al método start interno
            loginScreen.start(primaryStage);
        } catch (Exception e) {
            // Si hay error, mostramos pantalla de login básica
            showBasicLogin(primaryStage);
        }
    }

    /**
     * Pantalla de login alternativa en caso de error
     */
    private void showBasicLogin(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Inicio");
        alert.setHeaderText("No se pudo cargar la interfaz de login");
        alert.setContentText("Por favor reinicie la aplicación.");
        alert.showAndWait();
        Platform.exit();
    }

    /**
     * Verifica la conexión a la base de datos
     */
    private boolean testDatabaseConnection() {
        try {
            DatabaseConnection connection = DatabaseConnection.getInstance();
            return connection.testConnection();
        } catch (Exception e) {
            System.err.println("Error de conexión a BD: " + e.getMessage());
            return false;
        }
    }

    /**
     * Muestra error de conexión a base de datos
     */
    private void showDatabaseError() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Conexión");
            alert.setHeaderText("No se pudo conectar a la base de datos");
            alert.setContentText("Verifique:\n" +
                    "1. Que MySQL esté ejecutándose\n" +
                    "2. Las credenciales en DatabaseConnection.java\n" +
                    "3. Que la base de datos 'biblioteca_digital' exista");

            alert.showAndWait();
            Platform.exit();
        });
    }

    /**
     * Muestra error de inicio de aplicación
     */
    private void showStartupError(Exception e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Inicio");
            alert.setHeaderText("No se pudo iniciar la aplicación");
            alert.setContentText("Error: " + e.getMessage() +
                    "\n\nPor favor contacte al administrador del sistema.");
            alert.showAndWait();
            Platform.exit();
        });
    }

    /**
     * Maneja el cierre seguro de la aplicación
     */
    private void exitApplication(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Salir del Sistema");
        alert.setHeaderText("¿Está seguro de salir del sistema?");
        alert.setContentText("Se cerrarán todas las sesiones activas.");

        alert.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                try {
                    // Cerrar conexión a BD
                    DatabaseConnection.getInstance().closeConnection();

                    // Cerrar aplicación
                    Platform.exit();
                    System.exit(0);
                } catch (Exception e) {
                    Platform.exit();
                }
            }
        });
    }

    /**
     * Método main - punto de entrada de la aplicación
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  SISTEMA DE BIBLIOTECA DIGITAL v" + APP_VERSION);
        System.out.println("========================================");
        System.out.println("Iniciando aplicación...");

        // Configurar propiedades de JavaFX
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "false");

        try {
            // Iniciar aplicación JavaFX
            launch(args);
        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la aplicación:");
            e.printStackTrace();

            // Mostrar mensaje de error en consola
            System.err.println("\nPOSIBLES SOLUCIONES:");
            System.err.println("1. Verificar que JavaFX esté configurado correctamente");
            System.err.println("2. Verificar las dependencias en pom.xml o build.gradle");
            System.err.println("3. Ejecutar con: --module-path \"path/to/javafx/lib\" --add-modules javafx.controls,javafx.fxml");
        }
    }
}

/**
 * Clase para mostrar splash screen inicial
 */
class SplashScreen {

    public Scene createScene() {
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(20);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);" +
                "-fx-alignment: center; -fx-padding: 40;");

        // Título
        javafx.scene.text.Text title = new javafx.scene.text.Text("📚 Biblioteca Digital");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-fill: white;");

        // Subtítulo
        javafx.scene.text.Text subtitle = new javafx.scene.text.Text("Sistema de Gestión Integral");
        subtitle.setStyle("-fx-font-size: 18px; -fx-fill: #bdc3c7;");

        // Spinner de carga
        javafx.scene.control.ProgressIndicator progress = new javafx.scene.control.ProgressIndicator();
        progress.setStyle("-fx-progress-color: #3498db;");

        // Versión
        javafx.scene.text.Text version = new javafx.scene.text.Text("Versión 1.0.0");
        version.setStyle("-fx-font-size: 12px; -fx-fill: #7f8c8d;");

        // Cargando...
        javafx.scene.text.Text loading = new javafx.scene.text.Text("Inicializando sistema...");
        loading.setStyle("-fx-font-size: 14px; -fx-fill: #ecf0f1;");

        root.getChildren().addAll(title, subtitle, progress, loading, version);

        return new Scene(root, 600, 400);
    }
}