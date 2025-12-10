package clases.proyectopruebas.view;

import clases.proyectopruebas.dao.PrestamoDAO;
import clases.proyectopruebas.models.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class RegistroDevoluciones {
    
    private Usuario bibliotecario;
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    private TableView<Map<String, Object>> loanTable;
    
    public RegistroDevoluciones(Usuario bibliotecario) {
        this.bibliotecario = bibliotecario;
    }
    public BorderPane getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        Label title = new Label("REGISTRAR DEVOLUCION");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));
        title.setPadding(new Insets(0, 0, 20, 0));
        
        VBox tableContainer = createLoanTable();
        HBox buttonPanel = createButtonPanel();
        
        layout.setTop(title);
        layout.setCenter(tableContainer);
        layout.setBottom(buttonPanel);
        
        loadActiveLoans();
        
        return layout;
    }
    
    private VBox createLoanTable() {
        VBox container = new VBox();
        
        loanTable = new TableView<>();
        loanTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loanTable.setStyle("-fx-font-size: 13px;");
        
        TableColumn<Map<String, Object>, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().get("id_prestamo").toString()));
        idCol.setPrefWidth(60);
        
        TableColumn<Map<String, Object>, String> libroCol = new TableColumn<>("Libro");
        libroCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().get("libro").toString()));
        libroCol.setPrefWidth(200);
        
        TableColumn<Map<String, Object>, String> usuarioCol = new TableColumn<>("Usuario");
        usuarioCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().get("usuario").toString()));
        usuarioCol.setPrefWidth(150);
        
        TableColumn<Map<String, Object>, String> fechaCol = new TableColumn<>("Fecha Devolución");
        fechaCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().get("fecha_devolucion_esperada").toString()));
        fechaCol.setPrefWidth(120);
        
        TableColumn<Map<String, Object>, String> vencidoCol = new TableColumn<>("Vencido");
        vencidoCol.setCellValueFactory(data -> {
            int diasVencido = (int) data.getValue().get("dias_vencido");
            return new javafx.beans.property.SimpleStringProperty(diasVencido > 0 ? "Sí (" + diasVencido + " días)" : "No");
        });
        vencidoCol.setCellFactory(col -> new TableCell<Map<String, Object>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("Sí")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        loanTable.getColumns().addAll(idCol, libroCol, usuarioCol, fechaCol, vencidoCol);
        
        Label infoLabel = new Label("Seleccione un préstamo para registrar su devolución");
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        infoLabel.setTextFill(Color.web("#7f8c8d"));
        
        container.getChildren().addAll(infoLabel, loanTable);
        VBox.setVgrow(loanTable, Priority.ALWAYS);
        
        return container;
    }
    
    private HBox createButtonPanel() {
        HBox panel = new HBox(20);
        panel.setPadding(new Insets(20, 0, 0, 0));
        panel.setAlignment(Pos.CENTER);
        
        Button returnBtn = createStyledButton("Registrar Devolución", "#27ae60");
        Button refreshBtn = createStyledButton("Actualizar Lista", "#3498db");
        Button cancelBtn = createStyledButton("Cancelar", "#e74c3c");
        
        returnBtn.setOnAction(e -> registerReturn());
        refreshBtn.setOnAction(e -> loadActiveLoans());
        cancelBtn.setOnAction(e -> closeWindow());
        
        panel.getChildren().addAll(returnBtn, refreshBtn, cancelBtn);
        return panel;
    }
    
    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 12 25;" +
                    "-fx-background-radius: 5;" +
                    "-fx-cursor: hand;" +
                    "-fx-font-size: 14px;");
        btn.setMinWidth(150);
        return btn;
    }
    
    private void loadActiveLoans() {
        try {
            List<Map<String, Object>> prestamosActivos = prestamoDAO.getPrestamosActivos();
            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList(prestamosActivos);
            loanTable.setItems(data);
        } catch (Exception e) {
            showAlert("Error al cargar préstamos activos: " + e.getMessage());
        }
    }
    
    private void registerReturn() {
        Map<String, Object> selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        
        if (selectedLoan == null) {
            showAlert("Seleccione un préstamo de la lista");
            return;
        }
        
        // Mostrar diálogo para observaciones
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Registrar Devolución");
        dialog.setHeaderText("Devolución del préstamo #" + selectedLoan.get("id_prestamo"));
        dialog.setContentText("Observaciones (opcional):");
        
        dialog.showAndWait().ifPresent(observaciones -> {
            try {
                int idPrestamo = (int) selectedLoan.get("id_prestamo");
                Map<String, Object> resultado = prestamoDAO.registrarDevolucion(idPrestamo, observaciones);
                
                if ((boolean) resultado.get("exito")) {
                    String mensaje = "Devolución registrada exitosamente.\n";
                    int diasRetraso = (int) resultado.get("dias_retraso");
                    if (diasRetraso > 0) {
                        mensaje += "Días de retraso: " + diasRetraso + "\n";
                        mensaje += "Multa generada: $" + resultado.get("multa_generada");
                    }
                    showAlert(mensaje);
                    loadActiveLoans(); // Recargar lista
                } else {
                    showAlert("Error: " + resultado.get("mensaje"));
                }
            } catch (Exception e) {
                showAlert("Error al registrar devolución: " + e.getMessage());
            }
        });
    }
    
    private void closeWindow() {
        Stage stage = (Stage) loanTable.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}