package org.biblioteca.views;

import org.biblioteca.dao.*;
import org.biblioteca.models.*;
import org.biblioteca.models.enums.TipoUsuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;

public class BibliotecarioView {

    private Usuario usuario;
    private UsuarioDAO usuarioDAO;
    private LibroDAO libroDAO;
    private PrestamoDAO prestamoDAO;
    private BorderPane layout;

    public BibliotecarioView() {
        this.usuarioDAO = new UsuarioDAO();
        this.libroDAO = new LibroDAO();
        this.prestamoDAO = new PrestamoDAO();
    }

    public BorderPane getLibrarianView(Usuario usuario, String color) {
        this.usuario = usuario;

        layout = new BorderPane();
        layout.setPadding(new Insets(10));

        HBox topBar = createLibrarianTopBar(usuario, color);
        VBox sideMenu = createLibrarianSideMenu(color);
        StackPane content = createLibrarianContent();

        layout.setTop(topBar);
        layout.setLeft(sideMenu);
        layout.setCenter(content);

        return layout;
    }

    private HBox createLibrarianTopBar(Usuario usuario, String color) {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: " + color + ";");

        Label title = new Label("BIBLIOTECA DIGITAL - BIBLIOTECARIO");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label(usuario.getNombre() + " | ID: " + usuario.getIdUsuario());
        userInfo.setTextFill(Color.WHITE);

        Button quickLoanBtn = new Button("Nuevo Prestamo");
        quickLoanBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        quickLoanBtn.setOnAction(e -> showQuickLoanDialog());

        Button quickReturnBtn = new Button("Registrar Devolucion");
        quickReturnBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        quickReturnBtn.setOnAction(e -> showQuickReturnDialog());

        Button logoutBtn = new Button("Cerrar Sesion");
        logoutBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");

        topBar.getChildren().addAll(title, spacer, quickLoanBtn, quickReturnBtn, userInfo, logoutBtn);
        return topBar;
    }

    private VBox createLibrarianSideMenu(String color) {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(220);
        menu.setStyle("-fx-background-color: #ecf0f1;");

        Label menuTitle = new Label("MENU BIBLIOTECARIO");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        menuTitle.setTextFill(Color.web(color));

        // Solo funciones permitidas para bibliotecario
        Button btnGestionLibros = createMenuButton("Gestionar Libros", "Gestionar libros", color);
        Button btnBuscarUsuarios = createMenuButton("Buscar Usuarios", "Buscaar usuarios", color);
        Button btnPrestamosActivos = createMenuButton("Prestamos Activos", "prestamos activos", color);
        Button btnDevoluciones = createMenuButton("Devoluciones", "devoluciones", color);
        Button btnMultas = createMenuButton("Multas Pendientes", "multas pendientes", color);
        Button btnReportes = createMenuButton("Reportes", "Reportes", color);
        Button btnPerfil = createMenuButton("Mi Perfil", "mi perfil", color);

        menu.getChildren().addAll(menuTitle, btnGestionLibros, btnBuscarUsuarios,
                btnPrestamosActivos, btnDevoluciones, btnMultas,
                btnReportes, btnPerfil);
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
        if (color.equals("#3498db")) return "#2980b9";
        return color;
    }

    private StackPane createLibrarianContent() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));

        // Dashboard con estadísticas
        VBox dashboard = new VBox(20);

        Label welcome = new Label("Panel de Control - Bibliotecario");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcome.setTextFill(Color.web("#3498db"));

        // Estadísticas rápidas
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(15);
        statsGrid.setPadding(new Insets(20));

        List<Map<String, Object>> prestamosActivos = prestamoDAO.getPrestamosActivos();
        List<Map<String, Object>> multasPendientes = prestamoDAO.getMultasPendientes();
        List<Libro> librosDisponibles = libroDAO.findDisponibles();

        VBox card1 = createDashboardCard("Prestamos Activos",
                String.valueOf(prestamosActivos.size()), "#3498db");
        VBox card2 = createDashboardCard("Multas Pendientes",
                String.valueOf(multasPendientes.size()), "#e74c3c");
        VBox card3 = createDashboardCard("Libros Disponibles",
                String.valueOf(librosDisponibles.size()), "#2ecc71");
        VBox card4 = createDashboardCard("Usuarios Activos",
                String.valueOf(usuarioDAO.findByEstado(
                        org.biblioteca.models.enums.EstadoUsuario.ACTIVO).size()),
                "#f39c12");

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 0, 1);
        statsGrid.add(card4, 1, 1);

        // Lista rápida de préstamos vencidos
        VBox vencidosSection = createVencidosSection();

        dashboard.getChildren().addAll(welcome, statsGrid, vencidosSection);
        content.getChildren().add(dashboard);

        return content;
    }

    private VBox createDashboardCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);");
        card.setPrefSize(180, 120);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.WHITE);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(titleLabel, valueLabel);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    private VBox createVencidosSection() {
        VBox section = new VBox(10);

        Label title = new Label("Prestamos Vencidos");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        List<Prestamo> vencidos = prestamoDAO.findPrestamosVencidos();

        if (vencidos.isEmpty()) {
            Label empty = new Label("No hay prestamos vencidos");
            empty.setFont(Font.font("Arial", 14));
            empty.setTextFill(Color.GRAY);
            section.getChildren().addAll(title, empty);
        } else {
            for (Prestamo prestamo : vencidos) {
                HBox item = createVencidoItem(prestamo);
                section.getChildren().add(item);
            }
        }

        return section;
    }

    private HBox createVencidoItem(Prestamo prestamo) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(10));
        item.setStyle("-fx-background-color: #ffeaa7;" +
                "-fx-border-color: #fdcb6e;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;");

        VBox info = new VBox(5);
        Label libroLabel = new Label("Libro: " + prestamo.getLibro().getTitulo());
        libroLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label usuarioLabel = new Label("Usuario ID: " + prestamo.getUsuario().getIdUsuario());
        usuarioLabel.setFont(Font.font("Arial", 11));

        info.getChildren().addAll(libroLabel, usuarioLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button actionBtn = new Button("Registrar Devolucion");
        actionBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        actionBtn.setOnAction(e -> registerReturn(prestamo.getIdPrestamo()));

        item.getChildren().addAll(info, spacer, actionBtn);
        return item;
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "Gestionar Libros":
                showBookManagement();
                break;
            case "Buscar usuarios":
                showUserSearch();
                break;
            case "Prestamos activos":
                showActiveLoans();
                break;
            case "Devoluciones":
                showReturns();
                break;
            case "Multas pendientes":
                showFines();
                break;
            case "Reportes":
                showReports();
                break;
            case "Mi Perfil":
                showProfile();
                break;
        }
    }

    private void showQuickLoanDialog() {
        NewLoanDialog dialog = new NewLoanDialog(usuario, libroDAO, prestamoDAO, usuarioDAO);
        dialog.showAndWait();
    }

    private void showQuickReturnDialog() {
        ReturnDialog dialog = new ReturnDialog(prestamoDAO);
        dialog.showAndWait();
    }

    private void showBookManagement() {
        BookManagementView bookView = new BookManagementView(libroDAO);
        layout.setCenter(bookView.getView());
    }

    private void showUserSearch() {
        UserSearchView userView = new UserSearchView(usuarioDAO);
        layout.setCenter(userView.getView());
    }

    private void showActiveLoans() {
        ActiveLoansView loansView = new ActiveLoansView(prestamoDAO);
        layout.setCenter(loansView.getView());
    }

    private void showReturns() {
        ReturnsView returnsView = new ReturnsView(prestamoDAO);
        layout.setCenter(returnsView.getView());
    }

    private void showFines() {
        FinesView finesView = new FinesView(prestamoDAO);
        layout.setCenter(finesView.getView());
    }

    private void showReports() {
        ReportsView reportsView = new ReportsView(prestamoDAO, libroDAO);
        layout.setCenter(reportsView.getView());
    }

    private void showProfile() {
        ProfileView profileView = new ProfileView(usuario, usuarioDAO);
        layout.setCenter(profileView.getView());
    }

    private void registerReturn(int idPrestamo) {
        ReturnDialog dialog = new ReturnDialog(prestamoDAO, idPrestamo);
        dialog.showAndWait();
    }
}