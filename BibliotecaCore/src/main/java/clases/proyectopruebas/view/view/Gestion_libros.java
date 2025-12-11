package clases.proyectopruebas.view.view;


import clases.proyectopruebas.dao.LibroDAO;
import clases.proyectopruebas.models.Libro;
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

public class Gestion_libros {
    private LibroDAO libroDAO = new LibroDAO();
    private TableView<Libro> bookTable;
    private ObservableList<Libro> bookData;
    
    public BorderPane getView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));
        
        Label title = new Label("GESTION DE LIBROS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));
        title.setPadding(new Insets(0, 0, 20, 0));
        
        HBox searchPanel = createSearchPanel();
        VBox tableContainer = createBookTable();
        HBox actionButtons = createActionButtons();
        
        VBox topSection = new VBox(15, title, searchPanel);
        layout.setTop(topSection);
        layout.setCenter(tableContainer);
        layout.setBottom(actionButtons);
        
        loadBooks();
        
        return layout;
    }
    
    private HBox createSearchPanel() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f8f9fa;" +
                      "-fx-border-color: #dee2e6;" +
                      "-fx-border-radius: 5;" +
                      "-fx-border-width: 1;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por titulo, autor o ISBN...");
        searchField.setPrefWidth(300);
        
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("Todos", "Disponibles", "No disponibles");
        filterCombo.setValue("Todos");
        filterCombo.setPrefWidth(150);
        
        Button searchBtn = new Button("Buscar");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        searchBtn.setOnAction(e -> searchBooks(searchField.getText(), filterCombo.getValue()));
        
        Button clearBtn = new Button("Limpiar");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            filterCombo.setValue("Todos");
            loadBooks();
        });
        
        panel.getChildren().addAll(
            new Label("Filtrar:"), filterCombo,
            new Label("Buscar:"), searchField, searchBtn, clearBtn
        );
        
        return panel;
    }
    
    private VBox createBookTable() {
        VBox container = new VBox();
        
        bookTable = new TableView<>();
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bookTable.setStyle("-fx-font-size: 13px;");
        
        TableColumn<Libro, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idLibro"));
        idCol.setPrefWidth(60);
        
        TableColumn<Libro, String> tituloCol = new TableColumn<>("Titulo");
        tituloCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tituloCol.setPrefWidth(200);
        
        TableColumn<Libro, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(120);
        
        TableColumn<Libro, String> editorialCol = new TableColumn<>("Editorial");
        editorialCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEditorial() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEditorial().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        TableColumn<Libro, String> disponibilidadCol = new TableColumn<>("Disponible");
        disponibilidadCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().estaDisponible() ? "Sí" : "No"
            ));
        disponibilidadCol.setCellFactory(col -> new TableCell<Libro, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(item.equals("Sí") ? 
                            "-fx-text-fill: green; -fx-font-weight: bold;" : 
                            "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });
        
        bookTable.getColumns().addAll(idCol, tituloCol, isbnCol, editorialCol, disponibilidadCol);
        
        Label resultLabel = new Label();
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        resultLabel.setTextFill(Color.web("#7f8c8d"));
        
        container.getChildren().addAll(bookTable, resultLabel);
        VBox.setVgrow(bookTable, Priority.ALWAYS);
        
        return container;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);
        
        Button addBtn = createActionButton("Agregar Libro", "#27ae60");
        Button editBtn = createActionButton("Editar Libro", "#3498db");
        Button deleteBtn = createActionButton("Eliminar Libro", "#e74c3c");
        Button detailsBtn = createActionButton("Ver Detalles", "#9b59b6");
        
        addBtn.setOnAction(e -> addBook());
        editBtn.setOnAction(e -> editBook());
        deleteBtn.setOnAction(e -> deleteBook());
        detailsBtn.setOnAction(e -> showBookDetails());
        
        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn, detailsBtn);
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
    
    private void loadBooks() {
        List<Libro> libros = libroDAO.findAll();
        bookData = FXCollections.observableArrayList(libros);
        bookTable.setItems(bookData);
    }
    
    private void searchBooks(String query, String filter) {
        // Implementar búsqueda
        loadBooks(); // Por ahora solo recarga
    }
    
    private void addBook() {
        showBookDialog("Agregar Nuevo Libro", null);
    }
    
    private void editBook() {
        Libro selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showBookDialog("Editar Libro", selected);
        } else {
            showAlert("Seleccione un libro para editar");
        }
    }
    
    private void deleteBook() {
        Libro selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Eliminación");
            confirm.setHeaderText("¿Eliminar libro?");
            confirm.setContentText("¿Está seguro de eliminar \"" + selected.getTitulo() + "\"?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        libroDAO.delete(selected.getIdLibro());
                        bookData.remove(selected);
                        showAlert("Libro eliminado exitosamente");
                    } catch (Exception e) {
                        showAlert("Error al eliminar: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert("Seleccione un libro para eliminar");
        }
    }
    
    private void showBookDetails() {
        Libro selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Detalles del Libro");
            info.setHeaderText(selected.getTitulo());
            info.setContentText(
                "ISBN: " + selected.getIsbn() + "\n" +
                "Editorial: " + (selected.getEditorial() != null ? selected.getEditorial().getNombre() : "N/A") + "\n" +
                "Año: " + selected.getAnioPublicacion() + "\n" +
                "Páginas: " + selected.getNumPaginas() + "\n" +
                "Idioma: " + selected.getIdioma() + "\n" +
                "Disponibles: " + selected.getCopiasDisponibles() + "/" + selected.getCopiasTotales()
            );
            info.showAndWait();
        } else {
            showAlert("Seleccione un libro para ver detalles");
        }
    }
    
    private void showBookDialog(String title, Libro libro) {
        Dialog<Libro> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField tituloField = new TextField(libro != null ? libro.getTitulo() : "");
        TextField isbnField = new TextField(libro != null ? libro.getIsbn() : "");
        TextField añoField = new TextField(libro != null ? String.valueOf(libro.getAnioPublicacion()) : "");
        TextField paginasField = new TextField(libro != null ? String.valueOf(libro.getNumPaginas()) : "");
        TextField idiomaField = new TextField(libro != null ? libro.getIdioma() : "Español");
        TextField copiasField = new TextField(libro != null ? String.valueOf(libro.getCopiasTotales()) : "1");
        
        grid.add(new Label("Título:"), 0, 0);
        grid.add(tituloField, 1, 0);
        grid.add(new Label("ISBN:"), 0, 1);
        grid.add(isbnField, 1, 1);
        grid.add(new Label("Año:"), 0, 2);
        grid.add(añoField, 1, 2);
        grid.add(new Label("Páginas:"), 0, 3);
        grid.add(paginasField, 1, 3);
        grid.add(new Label("Idioma:"), 0, 4);
        grid.add(idiomaField, 1, 4);
        grid.add(new Label("Copias:"), 0, 5);
        grid.add(copiasField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    Libro result = libro != null ? libro : new Libro();
                    result.setTitulo(tituloField.getText());
                    result.setIsbn(isbnField.getText());
                    result.setAnioPublicacion(Integer.parseInt(añoField.getText()));
                    result.setNumPaginas(Integer.parseInt(paginasField.getText()));
                    result.setIdioma(idiomaField.getText());
                    result.setCopiasTotales(Integer.parseInt(copiasField.getText()));
                    result.setCopiasDisponibles(Integer.parseInt(copiasField.getText()));
                    
                    return result;
                } catch (NumberFormatException e) {
                    showAlert("Error en formato numérico: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            try {
                if (libro == null) {
                    libroDAO.save(result);
                    bookData.add(result);
                    showAlert("Libro agregado exitosamente");
                } else {
                    libroDAO.update(result);
                    int index = bookData.indexOf(libro);
                    bookData.set(index, result);
                    showAlert("Libro actualizado exitosamente");
                }
                bookTable.refresh();
            } catch (Exception e) {
                showAlert("Error al guardar: " + e.getMessage());
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