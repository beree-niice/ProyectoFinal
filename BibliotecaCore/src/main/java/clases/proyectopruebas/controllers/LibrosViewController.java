package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.LibroDAO;
import clases.proyectopruebas.models.Libro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class LibrosViewController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, Integer> colCantidad;

    private final LibroDAO libroDAO = new LibroDAO();
    private final ObservableList<Libro> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Asignar cell value factories si el modelo soporta properties, aquí se usa toString básico
        tablaLibros.setItems(data);
        cargarLibros();
    }

    private void cargarLibros() {
        try {
            data.setAll(libroDAO.findAll());
        } catch (Exception e) {
            mostrarError("No se pudieron cargar los libros: " + e.getMessage());
        }
    }

    public void onBuscar() {
        String q = txtBuscar.getText();
        try {
            List<Libro> result = libroDAO.findDisponibles();
            data.setAll(result);
        } catch (Exception e) {
            mostrarError("Error en búsqueda: " + e.getMessage());
        }
    }

    public void onNuevo() {
        // TODO: diálogo de creación
        mostrarInfo("Funcionalidad de creación pendiente");
    }

    public void onEditar() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarInfo("Seleccione un libro"); return; }
        // TODO: diálogo de edición
        mostrarInfo("Funcionalidad de edición pendiente");
    }

    public void onEliminar() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarInfo("Seleccione un libro"); return; }
        try {
            libroDAO.delete(sel.getIdLibro());
            cargarLibros();
        } catch (Exception e) {
            mostrarError("No se pudo eliminar: " + e.getMessage());
        }
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
