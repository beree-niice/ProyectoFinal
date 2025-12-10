package clases.proyectopruebas.view;

import clases.proyectopruebas.dao.LibroDAO;
import clases.proyectopruebas.dao.PrestamoDAO;
import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Libro;
import clases.proyectopruebas.models.Prestamo;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class RegistroPrestamos {
    
    private Usuario bibliotecario;
    private LibroDAO libroDAO = new LibroDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    
    private ComboBox<Libro> libroCombo;
    private ComboBox<Usuario> usuarioCombo;
    private DatePicker fechaDevolucionPicker;
    private TextArea observacionesArea;
    
    public RegistroPrestamos(Usuario bibliotecario) {
        this.bibliotecario = bibliotecario;
    }


    public BorderPane getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        Label title = new Label("REGISTRAR NUEVO PRESTAMO");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));
        title.setPadding(new Insets(0, 0, 20, 0));
        
        GridPane form = createLoanForm();
        VBox infoPanel = createInfoPanel();
        HBox buttonPanel = createButtonPanel();
        
        VBox centerContent = new VBox(20, form, infoPanel);
        layout.setTop(title);
        layout.setCenter(centerContent);
        layout.setBottom(buttonPanel);
        
        loadFormData();
        
        return layout;
    }
    
    private GridPane createLoanForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setStyle("-fx-background-color: #f8f9fa;" +
                     "-fx-border-color: #dee2e6;" +
                     "-fx-border-radius: 10;" +
                     "-fx-border-width: 1;");
        
        // Libro
        Label libroLabel = new Label("Libro a prestar:");
        libroLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(libroLabel, 0, 0);
        
        libroCombo = new ComboBox<>();
        libroCombo.setPrefWidth(300);
        libroCombo.setPromptText("Seleccione un libro...");
        grid.add(libroCombo, 1, 0);
        
        // Usuario
        Label usuarioLabel = new Label("Usuario:");
        usuarioLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(usuarioLabel, 0, 1);
        
        usuarioCombo = new ComboBox<>();
        usuarioCombo.setPrefWidth(300);
        usuarioCombo.setPromptText("Seleccione un usuario...");
        grid.add(usuarioCombo, 1, 1);
        
        // Fecha devolución
        Label fechaLabel = new Label("Fecha de devolución:");
        fechaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(fechaLabel, 0, 2);
        
        fechaDevolucionPicker = new DatePicker(LocalDate.now().plusDays(14));
        fechaDevolucionPicker.setPrefWidth(200);
        grid.add(fechaDevolucionPicker, 1, 2);
        
        // Observaciones
        Label obsLabel = new Label("Observaciones:");
        obsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(obsLabel, 0, 3);
        
        observacionesArea = new TextArea();
        observacionesArea.setPromptText("Observaciones adicionales...");
        observacionesArea.setPrefRowCount(3);
        observacionesArea.setPrefWidth(350);
        grid.add(observacionesArea, 1, 3);
        
        return grid;
    }
    
    private VBox createInfoPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #e8f4f8;" +
                      "-fx-border-color: #3498db;" +
                      "-fx-border-radius: 10;" +
                      "-fx-border-width: 2;");
        
        Label infoTitle = new Label("INFORMACION DEL PRESTAMO");
        infoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        infoTitle.setTextFill(Color.web("#2980b9"));
        
        // Info libro seleccionado
        VBox libroInfo = new VBox(5);
        libroInfo.setPadding(new Insets(10));
        libroInfo.setStyle("-fx-background-color: white; -fx-border-radius: 5;");
        libroInfo.setVisible(false);
        
        Label libroTitle = new Label();
        libroTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label libroDetails = new Label();
        libroDetails.setFont(Font.font("Arial", 12));
        
        libroInfo.getChildren().addAll(libroTitle, libroDetails);
        
        // Info usuario seleccionado
        VBox usuarioInfo = new VBox(5);
        usuarioInfo.setPadding(new Insets(10));
        usuarioInfo.setStyle("-fx-background-color: white; -fx-border-radius: 5;");
        usuarioInfo.setVisible(false);
        
        Label usuarioName = new Label();
        usuarioName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label usuarioDetails = new Label();
        usuarioDetails.setFont(Font.font("Arial", 12));
        
        usuarioInfo.getChildren().addAll(usuarioName, usuarioDetails);
        
        // Listeners para actualizar información
        libroCombo.setOnAction(e -> {
            Libro selected = libroCombo.getValue();
            if (selected != null) {
                libroTitle.setText("Libro: " + selected.getTitulo());
                libroDetails.setText(
                    "ISBN: " + selected.getIsbn() + " | " +
                    "Disponibles: " + selected.getCopiasDisponibles() + "/" + selected.getCopiasTotales()
                );
                libroInfo.setVisible(true);
            } else {
                libroInfo.setVisible(false);
            }
        });
        
        usuarioCombo.setOnAction(e -> {
            Usuario selected = usuarioCombo.getValue();
            if (selected != null) {
                usuarioName.setText("Usuario: " + selected.getNombre());
                usuarioDetails.setText(
                    "DNI: " + selected.getDni() + " | " +
                    "Email: " + selected.getEmail()
                );
                usuarioInfo.setVisible(true);
                
                // Obtener estadísticas del usuario
                try {
                    int prestamosActivos = prestamoDAO.contarPrestamosActivosUsuario(selected.getIdUsuario());
                    Label statsLabel = new Label("Préstamos activos: " + prestamosActivos + "/" + selected.getLimitePrestamos());
                    statsLabel.setFont(Font.font("Arial", 12));
                    if (prestamosActivos >= selected.getLimitePrestamos()) {
                        statsLabel.setTextFill(Color.RED);
                    } else {
                        statsLabel.setTextFill(Color.GREEN);
                    }
                    usuarioInfo.getChildren().removeIf(node -> node instanceof Label && node != usuarioName && node != usuarioDetails);
                    usuarioInfo.getChildren().add(statsLabel);
                } catch (Exception ex) {
                    // Ignorar errores al obtener estadísticas
                }
            } else {
                usuarioInfo.setVisible(false);
            }
        });
        
        panel.getChildren().addAll(infoTitle, libroInfo, usuarioInfo);
        return panel;
    }
    
    private HBox createButtonPanel() {
        HBox panel = new HBox(20);
        panel.setPadding(new Insets(20, 0, 0, 0));
        panel.setAlignment(Pos.CENTER);
        
        Button saveBtn = createStyledButton("Registrar Prestamo", "#27ae60");
        Button clearBtn = createStyledButton("Limpiar Formulario", "#95a5a6");
        Button cancelBtn = createStyledButton("Cancelar", "#e74c3c");
        
        saveBtn.setOnAction(e -> registerLoan());
        clearBtn.setOnAction(e -> clearForm());
        cancelBtn.setOnAction(e -> closeWindow());
        
        panel.getChildren().addAll(saveBtn, clearBtn, cancelBtn);
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
    
    private void loadFormData() {
        try {
            // Cargar libros disponibles
            List<Libro> librosDisponibles = libroDAO.findDisponibles();
            ObservableList<Libro> libroData = FXCollections.observableArrayList(librosDisponibles);
            libroCombo.setItems(libroData);
            
            // Cargar usuarios activos (solo lectores)
            List<Usuario> usuarios = usuarioDAO.findByTipo(TipoUsuario.LECTOR);
            ObservableList<Usuario> usuarioData = FXCollections.observableArrayList(usuarios);
            usuarioCombo.setItems(usuarioData);
            
        } catch (Exception e) {
            showAlert("Error al cargar datos: " + e.getMessage());
        }
    }
    
    private void registerLoan() {
        Libro libro = libroCombo.getValue();
        Usuario usuario = usuarioCombo.getValue();
        LocalDate fechaDevolucion = fechaDevolucionPicker.getValue();
        
        if (libro == null || usuario == null || fechaDevolucion == null) {
            showAlert("Complete todos los campos obligatorios");
            return;
        }
        
        if (fechaDevolucion.isBefore(LocalDate.now())) {
            showAlert("La fecha de devolución no puede ser anterior a hoy");
            return;
        }
        
        // Verificar si el libro está disponible
        if (!libro.estaDisponible()) {
            showAlert("El libro seleccionado no está disponible");
            return;
        }
        
        // Verificar límite de préstamos del usuario
        int prestamosActivos = prestamoDAO.contarPrestamosActivosUsuario(usuario.getIdUsuario());
        if (prestamosActivos >= usuario.getLimitePrestamos()) {
            showAlert("El usuario ha alcanzado su límite de préstamos activos (" + usuario.getLimitePrestamos() + ")");
            return;
        }
        
        try {
            // Crear préstamo
            Prestamo prestamo = new Prestamo();
            prestamo.setLibro(libro);
            prestamo.setUsuario(usuario);
            prestamo.setBibliotecario(bibliotecario);
            prestamo.setFechaDevolucionEsperada(fechaDevolucion);
            prestamo.setObservaciones(observacionesArea.getText());
            
            // Calcular días de préstamo
            long diasPrestamo = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaDevolucion);
            
            // Registrar usando stored procedure
            java.util.Map<String, Object> resultado = prestamoDAO.registrarPrestamo(
                libro.getIdLibro(),
                usuario.getIdUsuario(),
                bibliotecario.getIdUsuario(),
                (int) diasPrestamo
            );
            
            if ((boolean) resultado.get("exito")) {
                showAlert("Préstamo registrado exitosamente. ID: " + resultado.get("id_prestamo"));
                clearForm();
                loadFormData(); // Recargar datos
            } else {
                showAlert("Error: " + resultado.get("mensaje"));
            }
            
        } catch (Exception e) {
            showAlert("Error al registrar préstamo: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        libroCombo.setValue(null);
        usuarioCombo.setValue(null);
        fechaDevolucionPicker.setValue(LocalDate.now().plusDays(14));
        observacionesArea.clear();
    }
    
    private void closeWindow() {
        // Cerrar ventana
        Stage stage = (Stage) libroCombo.getScene().getWindow();
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