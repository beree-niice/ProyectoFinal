package clases.proyectopruebas.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    private static MainController instance;

    public static synchronized MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    private javafx.stage.Stage primaryStage;
    private clases.proyectopruebas.models.Usuario currentUser;

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        if (instance == null) {
            instance = this;
        }
        setStatus("Listo");
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    public javafx.stage.Stage getPrimaryStage() { return primaryStage; }
    public void setPrimaryStage(javafx.stage.Stage s) { this.primaryStage = s; }
    public clases.proyectopruebas.models.Usuario getCurrentUser() { return currentUser; }
    public void setCurrentUser(clases.proyectopruebas.models.Usuario u) { this.currentUser = u; }

    private void loadView(String fxml) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            setStatus("Error cargando vista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void goLibros(ActionEvent e) {
        loadView("LibrosView.fxml");
        setStatus("Libros");
    }

    public void goUsuarios(ActionEvent e) {
        loadView("UsuariosView.fxml");
        setStatus("Usuarios");
    }

    public void goPrestamos(ActionEvent e) {
        loadView("PrestamosView.fxml");
        setStatus("Préstamos");
    }

    public void goExport(ActionEvent e) {
        loadView("ExportView.fxml");
        setStatus("Exportar/Importar");
    }

    public void goReportes(ActionEvent e) {
        loadView("ReportesView.fxml");
        setStatus("Reportes");
    }
}
