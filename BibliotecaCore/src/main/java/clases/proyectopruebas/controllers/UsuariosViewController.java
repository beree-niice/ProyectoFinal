package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class UsuariosViewController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colDni;
    @FXML private TableColumn<Usuario, String> colEstado;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ObservableList<Usuario> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tablaUsuarios.setItems(data);
        cargarUsuarios();
    }

    private void cargarUsuarios() { 
        try {
            data.setAll(usuarioDAO.findAll());
        } catch (Exception e) {
            mostrarError("No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    public void onBuscar() {
        String q = txtBuscar.getText();
        try {
            List<Usuario> result = usuarioDAO.buscar(q); // asumir método buscar implementado
            data.setAll(result);
        } catch (Exception e) {
            mostrarError("Error en búsqueda: " + e.getMessage());
        }
    }

    public void onNuevo() { mostrarInfo("Funcionalidad de creación pendiente"); }
    public void onEditar() { mostrarInfo("Funcionalidad de edición pendiente"); }
    public void onEliminar() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarInfo("Seleccione un usuario"); return; }
        try {
            usuarioDAO.delete(sel.getIdUsuario());
            cargarUsuarios();
        } catch (Exception e) {
            mostrarError("No se pudo eliminar: " + e.getMessage());
        }
    }

    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
    private void mostrarInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
}
