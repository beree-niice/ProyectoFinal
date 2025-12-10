package clases.proyectopruebas.view.view;

import clases.proyectopruebas.dao.LibroDAO;
import clases.proyectopruebas.dao.PrestamoDAO;
import clases.proyectopruebas.models.Libro;
import clases.proyectopruebas.models.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class LectorView {

    private Usuario usuario;
    private LibroDAO libroDAO = new LibroDAO();
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    private Button logoutBtn;

    public BorderPane getView(Usuario usuario) {
        this.usuario = usuario;
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        HBox topBar = createTopBar();
        VBox sideMenu = createSideMenu();
        StackPane content = createContent();

        layout.setTop(topBar);
        layout.setLeft(sideMenu);
        layout.setCenter(content);

        return layout;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #27ae60;");

        Label title = new Label("BIBLIOTECA DIGITAL - LECTOR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label(usuario.getNombre() + " | DNI: " + usuario.getDni());
        userInfo.setTextFill(Color.WHITE);

        logoutBtn = new Button("Cerrar Sesión");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> returnToBienvenida());

        topBar.getChildren().addAll(title, spacer, userInfo, logoutBtn);
        return topBar;
    }

    private VBox createSideMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(200);
        menu.setStyle("-fx-background-color: #ecf0f1;");

        Label menuTitle = new Label("MENÚ LECTOR");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        menuTitle.setTextFill(Color.web("#27ae60"));

        Button[] buttons = {
                createMenuButton("Buscar Libros", "search"),
                createMenuButton("Mi Perfil", "profile"),
                createMenuButton("Mis Préstamos", "myLoans"),
                createMenuButton("Préstamos Activos", "activeLoans"),
                createMenuButton("Historial", "history"),
                createMenuButton("Libros Favoritos", "favorites")
        };

        menu.getChildren().add(menuTitle);
        menu.getChildren().addAll(buttons);

        return menu;
    }

    private Button createMenuButton(String text, String action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: #27ae60;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;" +
                "-fx-text-alignment: left;" +
                "-fx-padding: 12 15;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;");
        btn.setTextFill(Color.web("#2c3e50"));

        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: #27ae60;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #219653;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;"));
        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: transparent;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-border-color: #27ae60;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;"));

        btn.setOnAction(e -> handleMenuAction(action));
        return btn;
    }

    private StackPane createContent() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));

        // Mostrar vista de búsqueda por defecto
        content.getChildren().add(createSearchView());

        return content;
    }

    private void handleMenuAction(String action) {
        // Implementar cambio de vistas según la acción
        System.out.println("Acción seleccionada: " + action);
    }

    private VBox createSearchView() {
        VBox searchView = new VBox(20);
        searchView.setPadding(new Insets(20));

        Label title = new Label("BUSCAR LIBROS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#2c3e50"));

        HBox searchBar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por título, autor o ISBN...");
        searchField.setPrefWidth(300);

        Button searchButton = new Button("Buscar");
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));

        searchBar.getChildren().addAll(searchField, searchButton);

        TableView<Libro> bookTable = createBookTable();

        searchView.getChildren().addAll(title, searchBar, bookTable);
        return searchView;
    }

    private TableView<Libro> createBookTable() {
        TableView<Libro> table = new TableView<>();

        TableColumn<Libro, String> tituloCol = new TableColumn<>("Título");
        tituloCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tituloCol.setPrefWidth(200);

        TableColumn<Libro, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(120);

        TableColumn<Libro, Integer> añoCol = new TableColumn<>("Año");
        añoCol.setCellValueFactory(new PropertyValueFactory<>("añoPublicacion"));
        añoCol.setPrefWidth(80);

        // COLUMNA CORREGIDA - Usando Integer en lugar de String
        TableColumn<Libro, Integer> disponibilidadCol = new TableColumn<>("Disponible");
        disponibilidadCol.setCellValueFactory(new PropertyValueFactory<>("copiasDisponibles"));
        disponibilidadCol.setCellFactory(col -> new TableCell<Libro, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Usar el método estaDisponible() si existe en la clase Libro
                    // o verificar directamente con item > 0
                    boolean disponible = item > 0;
                    setText(disponible ? "Sí" : "No");
                    setStyle(disponible ?
                            "-fx-text-fill: green; -fx-font-weight: bold;" :
                            "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });
        disponibilidadCol.setPrefWidth(100);

        TableColumn<Libro, Void> actionCol = new TableColumn<>("Acción");
        actionCol.setCellFactory(col -> new TableCell<Libro, Void>() {
            private final Button prestarBtn = new Button("Solicitar");

            {
                prestarBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                prestarBtn.setOnAction(e -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    solicitarPrestamo(libro);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Libro libro = getTableView().getItems().get(getIndex());
                    // Verificar si el libro está disponible
                    boolean disponible = libro.getCopiasDisponibles() > 0;
                    prestarBtn.setDisable(!disponible);
                    setGraphic(prestarBtn);
                }
            }
        });

        table.getColumns().addAll(tituloCol, isbnCol, añoCol, disponibilidadCol, actionCol);

        // Cargar libros disponibles
        List<Libro> libros = libroDAO.findDisponibles();
        ObservableList<Libro> data = FXCollections.observableArrayList(libros);
        table.setItems(data);

        return table;
    }

    private void performSearch(String query) {
        // Implementar búsqueda
        if (query != null && !query.trim().isEmpty()) {
            System.out.println("Buscando: " + query);
            // Aquí puedes implementar la lógica de búsqueda
            // List<Libro> resultados = libroDAO.buscarPorTermino(query);
            // Actualizar la tabla con los resultados
        }
    }

    private void solicitarPrestamo(Libro libro) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Solicitar Préstamo");
        alert.setHeaderText("Funcionalidad en desarrollo");
        alert.setContentText("Para solicitar préstamo, contacte a un bibliotecario.\n\n" +
                "Libro seleccionado: " + libro.getTitulo() + "\n" +
                "ISBN: " + libro.getIsbn());
        alert.showAndWait();
    }

    private void returnToBienvenida() {
        // Cerrar la ventana actual
        Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
        currentStage.close();

        // Mostrar la ventana de Bienvenida
        try {
            Stage bienvenidaStage = new Stage();
            Bienvenida bienvenida = new Bienvenida();
           // bienvenidaStage.setScene(new Scene(bienvenida.getView(), 600, 400));
            bienvenidaStage.setTitle("Biblioteca Digital - Bienvenida");
            bienvenidaStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}