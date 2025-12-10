package clases.proyectopruebas;

import clases.proyectopruebas.view.view.Bienvenida;
import javafx.application.Application;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Verificar drivers antes de iniciar
            if (!checkDatabaseDriver()) {
                showErrorDialog("Error de configuración",
                        "Driver de base de datos no encontrado",
                        "Agrega mysql-connector-java.jar al classpath");
                return;
            }

            // Iniciar con pantalla de bienvenida
            Bienvenida bienvenida = new Bienvenida();
            bienvenida.start(primaryStage);

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error crítico",
                    "No se pudo iniciar la aplicación",
                    e.getMessage());
        }
    }

    private boolean checkDatabaseDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL encontrado");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado");
            return false;
        }
    }

    private void showErrorDialog(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        // Configurar propiedades de JavaFX
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("javafx.animation.fullspeed", "true");

        System.out.println("Iniciando Biblioteca Digital version2.0");
        System.out.println("" + java.time.LocalDateTime.now());

        // Iniciar aplicación
        launch(args);
    }
}