package clases.proyectopruebas.view.view;

import clases.proyectopruebas.controllers.GestionUsuariosController;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class Gestion_usuarios {

    private GestionUsuariosController controller;
    private TableView<Usuario> userTable;
    private ObservableList<Usuario> userData;
    private Label resultLabel;
    private Stage currentStage;

    public Gestion_usuarios() {
        this.controller = new GestionUsuariosController();
        this.userData = FXCollections.observableArrayList();
    }

    public BorderPane getView() {
        return getView(null);
    }

    public BorderPane getView(Stage stage) {
        this.currentStage = stage;

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        Label title = new Label("GESTIÓN DE USUARIOS");
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

        // Cargar usuarios iniciales
        controller.loadAllUsers();
        userData.setAll(controller.getUserData());
        userTable.setItems(userData);
        updateResultLabel();

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
        tipoFilter.setPrefWidth(150);

        ComboBox<EstadoUsuario> estadoFilter = new ComboBox<>();
        estadoFilter.getItems().addAll(EstadoUsuario.values());
        estadoFilter.getItems().add(0, null);
        estadoFilter.setPromptText("Todos los estados");
        estadoFilter.setPrefWidth(150);

        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por nombre, DNI o email...");
        searchField.setPrefWidth(250);

        // Acción de búsqueda al presionar Enter
        searchField.setOnAction(e -> applyFilters(tipoFilter.getValue(), estadoFilter.getValue(), searchField.getText()));

        Button filterBtn = new Button("Aplicar Filtros");
        filterBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        filterBtn.setOnAction(e -> applyFilters(tipoFilter.getValue(), estadoFilter.getValue(), searchField.getText()));

        Button clearBtn = new Button("Limpiar Filtros");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> {
            tipoFilter.setValue(null);
            estadoFilter.setValue(null);
            searchField.clear();
            controller.clearFilters();
            userData.setAll(controller.getUserData());
            updateResultLabel();
        });

        Button refreshBtn = new Button("⟳");
        refreshBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshBtn.setTooltip(new Tooltip("Actualizar lista"));
        refreshBtn.setOnAction(e -> {
            controller.loadAllUsers();
            userData.setAll(controller.getUserData());
            updateResultLabel();
        });

        panel.getChildren().addAll(
                new Label("Tipo:"), tipoFilter,
                new Label("Estado:"), estadoFilter,
                new Label("Buscar:"), searchField,
                filterBtn, clearBtn, refreshBtn
        );

        return panel;
    }

    private void applyFilters(TipoUsuario tipo, EstadoUsuario estado, String search) {
        controller.filterUsers(tipo, estado, search);
        userData.setAll(controller.getUserData());
        updateResultLabel();
    }

    private VBox createUserTable() {
        VBox container = new VBox();

        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userTable.setStyle("-fx-font-size: 13px;");

        TableColumn<Usuario, String> dniCol = new TableColumn<>("DNI");
        dniCol.setCellValueFactory(new PropertyValueFactory<>("dni"));
        dniCol.setPrefWidth(120);

        TableColumn<Usuario, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nombreCol.setPrefWidth(180);

        TableColumn<Usuario, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Usuario, String> telefonoCol = new TableColumn<>("Teléfono");
        telefonoCol.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        telefonoCol.setPrefWidth(120);

        TableColumn<Usuario, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoUsuario().getNombre()));
        tipoCol.setPrefWidth(120);

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
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (usuario.getEstado() == EstadoUsuario.MOROSO) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
                    }
                }
            }
        });
        estadoCol.setPrefWidth(120);

        TableColumn<Usuario, Integer> limiteCol = new TableColumn<>("Límite Préstamos");
        limiteCol.setCellValueFactory(new PropertyValueFactory<>("limitePrestamos"));
        limiteCol.setPrefWidth(120);

        userTable.getColumns().addAll(dniCol, nombreCol, emailCol, telefonoCol, tipoCol, estadoCol, limiteCol);

        resultLabel = new Label();
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        resultLabel.setTextFill(Color.web("#2c3e50"));
        resultLabel.setPadding(new Insets(10, 0, 0, 0));

        container.getChildren().addAll(userTable, resultLabel);
        VBox.setVgrow(userTable, Priority.ALWAYS);

        return container;
    }

    private HBox createActionButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);

        Button viewBtn = createActionButton("Ver Detalles", "#3498db");
        Button editBtn = createActionButton("Editar Usuario", "#f39c12");
        Button changeEstadoBtn = createActionButton("Cambiar Estado", "#9b59b6");
        Button deleteBtn = createActionButton("Eliminar Usuario", "#e74c3c");

        // Botón de exportación mejorado
        Button exportBtn = new Button("📥 Exportar CSV");
        exportBtn.setStyle("-fx-background-color: #2ecc71;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;");
        exportBtn.setMinWidth(120);
        exportBtn.setTooltip(new Tooltip("Exportar usuarios a archivo CSV"));

        // Menú desplegable para opciones de exportación
        MenuButton exportMenu = new MenuButton("📥 Exportar CSV");
        exportMenu.setStyle("-fx-background-color: #2ecc71;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;");
        exportMenu.setMinWidth(120);

        MenuItem exportFilteredItem = new MenuItem("Exportar usuarios filtrados");
        exportFilteredItem.setOnAction(e -> exportFilteredUsers());

        MenuItem exportAllItem = new MenuItem("Exportar todos los usuarios");
        exportAllItem.setOnAction(e -> exportAllUsers());

        MenuItem previewCSVItem = new MenuItem("Vista previa CSV");
        previewCSVItem.setOnAction(e -> showCSVPreview());

        exportMenu.getItems().addAll(exportFilteredItem, exportAllItem, new SeparatorMenuItem(), previewCSVItem);

        viewBtn.setOnAction(e -> viewUserDetails());
        editBtn.setOnAction(e -> editUser());
        changeEstadoBtn.setOnAction(e -> changeUserEstado());
        deleteBtn.setOnAction(e -> deleteUser());

        buttonBox.getChildren().addAll(viewBtn, editBtn, changeEstadoBtn, deleteBtn, exportMenu);
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

    private void updateResultLabel() {
        String stats = controller.getFilterStats();
        resultLabel.setText(stats);
    }

    private void viewUserDetails() {
        Usuario selected = controller.getSelectedUser(userTable);
        if (!controller.validateUserSelection(selected)) return;

        showUserDetailsDialog(selected);
    }

    private void editUser() {
        Usuario selected = controller.getSelectedUser(userTable);
        if (!controller.validateUserSelection(selected)) return;

        showUserEditDialog(selected);
    }

    private void changeUserEstado() {
        Usuario selected = controller.getSelectedUser(userTable);
        if (!controller.validateUserSelection(selected)) return;

        ChoiceDialog<EstadoUsuario> dialog = new ChoiceDialog<>(selected.getEstado(), EstadoUsuario.values());
        dialog.setTitle("Cambiar Estado");
        dialog.setHeaderText("Cambiar estado de: " + selected.getNombre());
        dialog.setContentText("Seleccione el nuevo estado:");

        Optional<EstadoUsuario> result = dialog.showAndWait();
        result.ifPresent(nuevoEstado -> {
            controller.updateUserStatus(selected, nuevoEstado);
            userTable.refresh();
            updateResultLabel();
        });
    }

    private void deleteUser() {
        Usuario selected = controller.getSelectedUser(userTable);
        if (!controller.validateUserSelection(selected)) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminación");
        confirm.setHeaderText("¿Eliminar usuario?");
        confirm.setContentText("¿Está seguro de eliminar a \"" + selected.getNombre() + "\"?\n" +
                "Esta acción no se puede deshacer.");

        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success = controller.deleteUser(selected);
                if (success) {
                    userData.remove(selected);
                    updateResultLabel();
                }
            }
        });
    }

    // ========== MÉTODOS DE EXPORTACIÓN ==========

    private void exportFilteredUsers() {
        if (currentStage == null) {
            currentStage = (Stage) userTable.getScene().getWindow();
        }

        if (userData.isEmpty()) {
            showAlert("Exportación cancelada", "No hay usuarios para exportar con los filtros actuales.");
            return;
        }

        boolean success = controller.exportCurrentFilteredUsers(currentStage);
        if (success) {
            // Opcional: abrir carpeta de descargas
            openExportInfoDialog();
        }
    }

    private void exportAllUsers() {
        if (currentStage == null) {
            currentStage = (Stage) userTable.getScene().getWindow();
        }

        boolean success = controller.exportAllUsers(currentStage);
        if (success) {
            openExportInfoDialog();
        }
    }

    private void showCSVPreview() {
        if (userData.isEmpty()) {
            showAlert("Vista previa", "No hay usuarios para mostrar.");
            return;
        }

        StringBuilder csvPreview = new StringBuilder();
        csvPreview.append("ID;DNI;Nombre;Email;Teléfono;Tipo;Estado;Límite\n");

        // Mostrar solo los primeros 10 registros para la vista previa
        int limit = Math.min(10, userData.size());
        for (int i = 0; i < limit; i++) {
            Usuario usuario = userData.get(i);
            csvPreview.append(String.format("%d;%s;%s;%s;%s;%s;%s;%d\n",
                    usuario.getIdUsuario(),
                    usuario.getDni(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getTelefono(),
                    usuario.getTipoUsuario().getNombre(),
                    usuario.getEstado().getNombre(),
                    usuario.getLimitePrestamos()
            ));
        }

        if (userData.size() > limit) {
            csvPreview.append(String.format("\n... y %d usuarios más", userData.size() - limit));
        }

        TextArea previewArea = new TextArea(csvPreview.toString());
        previewArea.setEditable(false);
        previewArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px;");
        previewArea.setPrefSize(600, 400);

        Alert previewDialog = new Alert(Alert.AlertType.INFORMATION);
        previewDialog.setTitle("Vista previa CSV");
        previewDialog.setHeaderText("Formato del archivo CSV (primeras " + limit + " filas)");
        previewDialog.getDialogPane().setContent(previewArea);
        previewDialog.getDialogPane().setPrefSize(620, 450);
        previewDialog.showAndWait();
    }

    private void openExportInfoDialog() {
        Alert infoDialog = new Alert(Alert.AlertType.INFORMATION);
        infoDialog.setTitle("Información de exportación");
        infoDialog.setHeaderText("Archivo CSV exportado exitosamente");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label info1 = new Label("✓ El archivo CSV está listo para usar en:");
        info1.setStyle("-fx-font-weight: bold;");

        VBox programsList = new VBox(5);
        programsList.getChildren().addAll(
                new Label("• Microsoft Excel"),
                new Label("• Google Sheets"),
                new Label("• LibreOffice Calc"),
                new Label("• Cualquier editor de texto")
        );

        Label info2 = new Label("✓ Formato compatible:");
        info2.setStyle("-fx-font-weight: bold;");

        VBox formatList = new VBox(5);
        formatList.getChildren().addAll(
                new Label("• Separador: Punto y coma (;)"),
                new Label("• Codificación: UTF-8"),
                new Label("• Texto entre comillas dobles"),
                new Label("• Encabezados incluidos")
        );

        Button openHelpBtn = new Button("¿Cómo abrir en Excel?");
        openHelpBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        openHelpBtn.setOnAction(e -> showExcelHelp());

        content.getChildren().addAll(info1, programsList, new Separator(), info2, formatList, openHelpBtn);

        infoDialog.getDialogPane().setContent(content);
        infoDialog.getDialogPane().setPrefSize(400, 350);
        infoDialog.showAndWait();
    }

    private void showExcelHelp() {
        Alert helpDialog = new Alert(Alert.AlertType.INFORMATION);
        helpDialog.setTitle("Ayuda para abrir en Excel");
        helpDialog.setHeaderText("Pasos para abrir el CSV en Excel:");

        VBox steps = new VBox(10);
        steps.setPadding(new Insets(10));

        steps.getChildren().addAll(
                createStepLabel("1. Abra Microsoft Excel"),
                createStepLabel("2. Vaya a 'Datos' → 'Obtener datos externos' → 'Desde texto'"),
                createStepLabel("3. Seleccione el archivo CSV exportado"),
                createStepLabel("4. En el asistente de importación:"),
                createStepLabel("   • Paso 1: Seleccione 'Delimitado'"),
                createStepLabel("   • Paso 2: Marque 'Punto y coma' como delimitador"),
                createStepLabel("   • Paso 3: Formato de columna: 'General'"),
                createStepLabel("5. Haga clic en 'Finalizar' y luego en 'Aceptar'")
        );

        helpDialog.getDialogPane().setContent(steps);
        helpDialog.getDialogPane().setPrefSize(500, 300);
        helpDialog.showAndWait();
    }

    private Label createStepLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    // ========== MÉTODOS DE DIÁLOGO ==========

    private void showUserDetailsDialog(Usuario usuario) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalles del Usuario");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label dniLabel = createDetailLabel("DNI:", usuario.getDni());
        Label nombreLabel = createDetailLabel("Nombre:", usuario.getNombre());
        Label emailLabel = createDetailLabel("Email:", usuario.getEmail());
        Label telefonoLabel = createDetailLabel("Teléfono:", usuario.getTelefono());
        Label direccionLabel = createDetailLabel("Dirección:", usuario.getDireccion());
        Label tipoLabel = createDetailLabel("Tipo:", usuario.getTipoUsuario().getNombre());
        Label estadoLabel = createDetailLabel("Estado:", usuario.getEstado().getNombre());
        Label limiteLabel = createDetailLabel("Límite Préstamos:", String.valueOf(usuario.getLimitePrestamos()));
        Label fechaRegLabel = createDetailLabel("Fecha Registro:",
                usuario.getFechaRegistro().toString());

        int row = 0;
        grid.add(dniLabel, 0, row++);
        grid.add(nombreLabel, 0, row++);
        grid.add(emailLabel, 0, row++);
        grid.add(telefonoLabel, 0, row++);
        grid.add(direccionLabel, 0, row++);
        grid.add(tipoLabel, 0, row++);
        grid.add(estadoLabel, 0, row++);
        grid.add(limiteLabel, 0, row++);
        grid.add(fechaRegLabel, 0, row++);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private Label createDetailLabel(String label, String value) {
        Label detail = new Label(label + " " + value);
        detail.setFont(Font.font("Arial", 14));
        detail.setTextFill(Color.web("#2c3e50"));
        detail.setWrapText(true);
        return detail;
    }

    private void showUserEditDialog(Usuario usuario) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Editar Usuario");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label dniLabel = new Label("DNI:");
        TextField dniField = new TextField(usuario.getDni());
        dniField.setDisable(true);

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

        int row = 0;
        grid.add(new Label("DNI:"), 0, row);
        grid.add(dniField, 1, row++);
        grid.add(new Label("Nombre:"), 0, row);
        grid.add(nombreField, 1, row++);
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        grid.add(new Label("Teléfono:"), 0, row);
        grid.add(telefonoField, 1, row++);
        grid.add(new Label("Dirección:"), 0, row);
        grid.add(direccionArea, 1, row++);
        grid.add(new Label("Tipo:"), 0, row);
        grid.add(tipoCombo, 1, row++);
        grid.add(new Label("Estado:"), 0, row);
        grid.add(estadoCombo, 1, row++);
        grid.add(new Label("Límite préstamos:"), 0, row);
        grid.add(limiteField, 1, row++);

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
                    showAlert("Error", "El límite de préstamos debe ser un número válido");
                    return null;
                } catch (Exception e) {
                    showAlert("Error", "Error al guardar cambios: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            boolean success = controller.updateUser(result);
            if (success) {
                userTable.refresh();
                updateResultLabel();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}