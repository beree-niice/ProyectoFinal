package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.PrestamoDAO;
import clases.proyectopruebas.models.Prestamo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PrestamosViewController {

    @FXML private TableView<Prestamo> tablaPrestamos;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final ObservableList<Prestamo> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tablaPrestamos.setItems(data);
        cargarPrestamos();
    }

    private void cargarPrestamos() {
        try {
            data.setAll(prestamoDAO.findAll());
        } catch (Exception e) {
            mostrarError("No se pudieron cargar los préstamos: " + e.getMessage());
        }
    }

    public void onNuevo() { mostrarInfo("Funcionalidad de creación pendiente"); }
    public void onDevolver() { mostrarInfo("Funcionalidad de devolución pendiente"); }
    public void onRenovar() { mostrarInfo("Funcionalidad de renovación pendiente"); }

    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
    private void mostrarInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
}
