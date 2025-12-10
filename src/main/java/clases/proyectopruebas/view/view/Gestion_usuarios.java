package clases.proyectopruebas.view.view;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class Gestion_usuarios {
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private TableView<Usuario> userTable;
    private ObservableList<Usuario> userData;
    
    public BorderPane getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        Label title = new Label("GESTION DE USUARIOS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));
        title.setPadding(new Insets(0, 0, 20, 0));
        
        HBox filterPanel = createFilterPanel();
        VBox tableContainer = createUserTable();
        HBox actionButtons = createActionButtons();
        
        VBox topSection = new VBox(15, title, filterPanel);
        layout.setTop(topSection);
        layout.setCenter(tableContainer);
        layout.setBottom(actionButtons);
        
        loadUsers();
        
        return layout;
    }
    
    private HBox createFilterPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f8f9fa;" +
                      "-fx-border-color: #dee2e6;" +
                      "-fx-border-radius: 5;" +
                      "-fx-border-width: 1;");
        
        ComboBox<TipoUsuario> tipoFilter = new ComboBox<>();
        tipoFilter.getItems().addAll(TipoUsuario.values());
        tipoFilter.getItems().add(0, null);
        tipoFilter.setPromptText("Todos los tipos");
        
        ComboBox<EstadoUsuario> estadoFilter = new ComboBox<>();
        estadoFilter.getItems().addAll(EstadoUsuario.values());
        estadoFilter.getItems().add(0, null);
        estadoFilter.setPromptText("Todos los estados");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por nombre o DNI...");
        searchField.setPrefWidth(200);
        
        Button filterBtn = new Button("Filtrar");
        filterBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        filterBtn.setOnAction(e -> filterUsers(tipoFilter.getValue(), estadoFilter.getValue(), searchField.getText()));
        
        Button clearBtn = new Button("Limpiar");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> {
            tipoFilter.setValue(null);
            estadoFilter.setValue(null);
            searchField.clear();
            loadUsers();
        });
        
        panel.getChildren().addAll(
            new Label("Tipo:"), tipoFilter,
            new Label("Estado:"), estadoFilter,
            new Label("Buscar:"), searchField, filterBtn, clearBtn
        );
        
        return panel;
    }
    
    private VBox createUserTable() {
        VBox container = new VBox();
        
        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userTable.setStyle("-fx-font-size: 13px;");
        
        TableColumn<Usuario, String> dniCol = new TableColumn<>("DNI");
        dniCol.setCellValueFactory(new PropertyValueFactory<>("dni"));
        dniCol.setPrefWidth(100);
        
        TableColumn<Usuario, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nombreCol.setPrefWidth(150);
        
        TableColumn<Usuario, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);
        
        TableColumn<Usuario, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoUsuario().getNombre()));
        
        TableColumn<Usuario, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado().getNombre()));
        estadoCol.setCellFactory(col -> new TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (usuario.getEstado() == EstadoUsuario.MOROSO) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        userTable.getColumns().addAll(dniCol, nombreCol, emailCol, tipoCol, estadoCol);
        
        Label resultLabel = new Label();
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        resultLabel.setTextFill(Color.web("#7f8c8d"));
        
        container.getChildren().addAll(userTable, resultLabel);
        VBox.setVgrow(userTable, Priority.ALWAYS);
        
        return container;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);
        
        Button editBtn = createActionButton("Editar Usuario", "#3498db");
        Button changeEstadoBtn = createActionButton("Cambiar Estado", "#f39c12");
        Button changeTipoBtn = createActionButton("Cambiar Tipo", "#9b59b6");
        Button deleteBtn = createActionButton("Eliminar Usuario", "#e74c3c");
        
        editBtn.setOnAction(e -> editUser());
        changeEstadoBtn.setOnAction(e -> changeUserEstado());
        changeTipoBtn.setOnAction(e -> changeUserTipo());
        deleteBtn.setOnAction(e -> deleteUser());
        
        buttonBox.getChildren().addAll(editBtn, changeEstadoBtn, changeTipoBtn, deleteBtn);
        return buttonBox;
    }
    
    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 10 20;" +
                    "-fx-background-radius: 5;" +
                    "-fx-cursor: hand;");
        btn.setMinWidth(120);
        return btn;
    }
    
    private void loadUsers() {
        List<Usuario> usuarios = usuarioDAO.findAll();
        userData = FXCollections.observableArrayList(usuarios);
        userTable.setItems(userData);
    }
    
    private void filterUsers(TipoUsuario tipo, EstadoUsuario estado, String search) {
        // Implementar filtrado
        loadUsers(); // Por ahora solo recarga
    }
    
    private void editUser() {
        Usuario selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showUserDialog(selected);
        } else {
            showAlert("Seleccione un usuario para editar");
        }
    }
    
    private void changeUserEstado() {
        Usuario selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ChoiceDialog<EstadoUsuario> dialog = new ChoiceDialog<>(selected.getEstado(), EstadoUsuario.values());
            dialog.setTitle("Cambiar Estado");
            dialog.setHeaderText("Cambiar estado de " + selected.getNombre());
            dialog.setContentText("Seleccione el nuevo estado:");
            
            dialog.showAndWait().ifPresent(nuevoEstado -> {
                try {
                    usuarioDAO.actualizarEstado(selected.getIdUsuario(), nuevoEstado);
                    selected.setEstado(nuevoEstado);
                    userTable.refresh();
                    showAlert("Estado actualizado exitosamente");
                } catch (Exception e) {
                    showAlert("Error al actualizar estado: " + e.getMessage());
                }
            });
        } else {
            showAlert("Seleccione un usuario");
        }
    }
    
    private void changeUserTipo() {
        Usuario selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ChoiceDialog<TipoUsuario> dialog = new ChoiceDialog<>(selected.getTipoUsuario(), TipoUsuario.values());
            dialog.setTitle("Cambiar Tipo");
            dialog.setHeaderText("Cambiar tipo de " + selected.getNombre());
            dialog.setContentText("Seleccione el nuevo tipo:");
            
            dialog.showAndWait().ifPresent(nuevoTipo -> {
                try {
                    selected.setTipoUsuario(nuevoTipo);
                    usuarioDAO.update(selected);
                    userTable.refresh();
                    showAlert("Tipo de usuario actualizado exitosamente");
                } catch (Exception e) {
                    showAlert("Error al actualizar tipo: " + e.getMessage());
                }
            });
        } else {
            showAlert("Seleccione un usuario");
        }
    }
    
    private void deleteUser() {
        Usuario selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Eliminación");
            confirm.setHeaderText("¿Eliminar usuario?");
            confirm.setContentText("¿Está seguro de eliminar a \"" + selected.getNombre() + "\"?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        usuarioDAO.delete(selected.getIdUsuario());
                        userData.remove(selected);
                        showAlert("Usuario eliminado exitosamente");
                    } catch (Exception e) {
                        showAlert("Error al eliminar: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert("Seleccione un usuario para eliminar");
        }
    }
    
    private void showUserDialog(Usuario usuario) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Editar Usuario");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField dniField = new TextField(usuario.getDni());
        dniField.setDisable(true); // DNI no se puede cambiar
        
        TextField nombreField = new TextField(usuario.getNombre());
        TextField emailField = new TextField(usuario.getEmail());
        TextField telefonoField = new TextField(usuario.getTelefono());
        TextArea direccionArea = new TextArea(usuario.getDireccion());
        direccionArea.setPrefRowCount(3);
        
        ComboBox<TipoUsuario> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll(TipoUsuario.values());
        tipoCombo.setValue(usuario.getTipoUsuario());
        
        ComboBox<EstadoUsuario> estadoCombo = new ComboBox<>();
        estadoCombo.getItems().addAll(EstadoUsuario.values());
        estadoCombo.setValue(usuario.getEstado());
        
        TextField limiteField = new TextField(String.valueOf(usuario.getLimitePrestamos()));
        
        grid.add(new Label("DNI:"), 0, 0);
        grid.add(dniField, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(nombreField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Teléfono:"), 0, 3);
        grid.add(telefonoField, 1, 3);
        grid.add(new Label("Dirección:"), 0, 4);
        grid.add(direccionArea, 1, 4);
        grid.add(new Label("Tipo:"), 0, 5);
        grid.add(tipoCombo, 1, 5);
        grid.add(new Label("Estado:"), 0, 6);
        grid.add(estadoCombo, 1, 6);
        grid.add(new Label("Límite préstamos:"), 0, 7);
        grid.add(limiteField, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    usuario.setNombre(nombreField.getText());
                    usuario.setEmail(emailField.getText());
                    usuario.setTelefono(telefonoField.getText());
                    usuario.setDireccion(direccionArea.getText());
                    usuario.setTipoUsuario(tipoCombo.getValue());
                    usuario.setEstado(estadoCombo.getValue());
                    usuario.setLimitePrestamos(Integer.parseInt(limiteField.getText()));
                    return usuario;
                } catch (NumberFormatException e) {
                    showAlert("Error en formato numérico: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            try {
                usuarioDAO.update(result);
                userTable.refresh();
                showAlert("Usuario actualizado exitosamente");
            } catch (Exception e) {
                showAlert("Error al actualizar: " + e.getMessage());
            }
        });
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}