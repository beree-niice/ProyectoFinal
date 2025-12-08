package org.biblioteca.views;

import org.biblioteca.dao.LibroDAO;
import org.biblioteca.dao.PrestamoDAO;
import org.biblioteca.dao.UsuarioDAO;
import org.biblioteca.models.Libro;
import org.biblioteca.models.Prestamo;
import org.biblioteca.models.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class LectorView {

    private Usuario usuario;
    private UsuarioDAO usuarioDAO;
    private LibroDAO libroDAO;
    private PrestamoDAO prestamoDAO;
    private BorderPane layout;

    public UserDashboard() {
        this.usuarioDAO = new UsuarioDAO();
        this.libroDAO = new LibroDAO();
        this.prestamoDAO = new PrestamoDAO();
    }

    public BorderPane getUserView(Usuario usuario, String color) {
        this.usuario = usuario;

        layout = new BorderPane();
        layout.setPadding(new Insets(10));

        HBox topBar = createUserTopBar(usuario, color);
        VBox sideMenu = createUserSideMenu(color);
        StackPane content = createUserContent();

        layout.setTop(topBar);
        layout.setLeft(sideMenu);
        layout.setCenter(content);

        return layout;
    }

    private HBox createUserTopBar(Usuario usuario, String color) {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: " + color + ";");

        Label title = new Label("BIBLIOTECA DIGITAL - LECTOR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label(usuario.getNombre());
        userInfo.setTextFill(Color.WHITE);

        Button logoutBtn = new Button("Cerrar Sesion");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> returnToLogin());

        topBar.getChildren().addAll(title, spacer, userInfo, logoutBtn);
        return topBar;
    }

    private VBox createUserSideMenu(String color) {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(200);
        menu.setStyle("-fx-background-color: #ecf0f1;");

        Label menuTitle = new Label("MENU LECTOR");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        menuTitle.setTextFill(Color.web(color));

        Button btnBuscar = createMenuButton("Buscar Libros", "Buscar", color);
        Button btnMisLibros = createMenuButton("Mis Prestamos", "Mis prestamos", color);
        Button btnPerfil = createMenuButton("Mi Perfil", "perfil", color);
        Button btnHistorial = createMenuButton("Historial", "Historial", color);

        menu.getChildren().addAll(menuTitle, btnBuscar, btnMisLibros, btnPerfil, btnHistorial);
        return menu;
    }

    private Button createMenuButton(String text, String action, String color) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;" +
                "-fx-text-alignment: left;" +
                "-fx-padding: 12 15;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;");
        btn.setTextFill(Color.web("#2c3e50"));

        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: " + darkenColor(color) + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;"));
        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: transparent;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;"));

        btn.setOnAction(e -> handleMenuAction(action));
        return btn;
    }

    private String darkenColor(String color) {
        if (color.equals("#B0C4DE")) return "#00008B";
        if (color.equals("#7FFFD4")) return "#5FBF98";
        if (color.equals("#DDA0DD")) return "#8b4d8b";
        return color;
    }

    private StackPane createUserContent() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));

        // Vista inicial: Dashboard con estadísticas
        VBox dashboard = new VBox(20);

        // Bienvenida
        Label welcome = new Label("Bienvenido, " + usuario.getNombre());
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcome.setTextFill(Color.web("#27ae60"));

        // Estadísticas del usuario
        Map<String, Object> stats = prestamoDAO.getEstadisticasUsuario(usuario.getIdUsuario());

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(15);
        statsGrid.setPadding(new Insets(20));

        int totalPrestamos = (int) stats.getOrDefault("total_prestamos", 0);
        int prestamosActivos = (int) stats.getOrDefault("prestamos_activos", 0);

        VBox stat1 = createStatCard("Total Prestamos", String.valueOf(totalPrestamos), "#3498db");
        VBox stat2 = createStatCard("Prestamos Activos", String.valueOf(prestamosActivos), "#2ecc71");
        VBox stat3 = createStatCard("Limite Prestamos", String.valueOf(usuario.getLimitePrestamos()), "#f39c12");
        VBox stat4 = createStatCard("Estado", usuario.getEstado().getNombre(),
                usuario.getEstado().name().equals("ACTIVO") ? "#27ae60" : "#e74c3c");

        statsGrid.add(stat1, 0, 0);
        statsGrid.add(stat2, 1, 0);
        statsGrid.add(stat3, 0, 1);
        statsGrid.add(stat4, 1, 1);

        // Préstamos activos recientes
        Label loansTitle = new Label("Mis Prestamos Activos");
        loansTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox activeLoansList = createActiveLoansList();

        dashboard.getChildren().addAll(welcome, statsGrid, loansTitle, activeLoansList);
        content.getChildren().add(dashboard);

        return content;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + color + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefSize(150, 100);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.WHITE);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(titleLabel, valueLabel);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    private VBox createActiveLoansList() {
        VBox container = new VBox(10);

        List<Prestamo> prestamos = prestamoDAO.findPrestamosByUsuarioCompletos(usuario.getIdUsuario());

        if (prestamos.isEmpty()) {
            Label emptyLabel = new Label("No tiene prestamos activos");
            emptyLabel.setFont(Font.font("Arial", 14));
            emptyLabel.setTextFill(Color.GRAY);
            container.getChildren().add(emptyLabel);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Prestamo prestamo : prestamos) {
                if (prestamo.getEstado().name().equals("ACTIVO")) {
                    HBox loanItem = createLoanItem(prestamo, formatter);
                    container.getChildren().add(loanItem);
                }
            }
        }

        return container;
    }

    private HBox createLoanItem(Prestamo prestamo, DateTimeFormatter formatter) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(10));
        item.setStyle("-fx-background-color: #f8f9fa;" +
                "-fx-border-color: #dee2e6;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;");

        VBox info = new VBox(5);
        Label title = new Label(prestamo.getLibro().getTitulo());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label dates = new Label("Prestado: " + prestamo.getFechaPrestamo().format(formatter) +
                " | Devolucion: " + prestamo.getFechaDevolucionEsperada().format(formatter));
        dates.setFont(Font.font("Arial", 12));

        info.getChildren().addAll(title, dates);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button renewBtn = new Button("Renovar");
        renewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        renewBtn.setDisable(!prestamo.puedeRenovarse());
        renewBtn.setOnAction(e -> renewLoan(prestamo));

        item.getChildren().addAll(info, spacer, renewBtn);
        return item;
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "Buscar":
                showBookSearch();
                break;
            case "Mis prestamos":
                showMyLoans();
                break;
            case "Perfil":
                showProfile();
                break;
            case "Historial":
                showHistory();
                break;
        }
    }

    private void showBookSearch() {
        BookSearchView searchView = new BookSearchView(usuario, libroDAO, prestamoDAO);
        layout.setCenter(searchView.getView());
    }

    private void showMyLoans() {
        MyLoansView loansView = new MyLoansView(usuario, prestamoDAO);
        layout.setCenter(loansView.getView());
    }

    private void showProfile() {
        ProfileView profileView = new ProfileView(usuario, usuarioDAO);
        layout.setCenter(profileView.getView());
    }

    private void showHistory() {
        HistoryView historyView = new HistoryView(usuario, prestamoDAO);
        layout.setCenter(historyView.getView());
    }

    private void renewLoan(Prestamo prestamo) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Renovar Prestamo");
        confirm.setHeaderText("Confirmar Renovacion");
        confirm.setContentText("¿Desea renovar el prestamo por 14 dias adicionales?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = prestamoDAO.renovarPrestamo(prestamo.getIdPrestamo(), 14);
                if (success) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Renovacion Exitosa");
                    info.setHeaderText("Prestamo renovado");
                    info.setContentText("La fecha de devolucion ha sido extendida.");
                    info.showAndWait();
                    showMyLoans();
                }
            }
        });
    }

    private void returnToLogin() {
        // Cerrar sesión y volver al login
        System.out.println("Cerrando sesion del usuario: " + usuario.getNombre());
    }
}