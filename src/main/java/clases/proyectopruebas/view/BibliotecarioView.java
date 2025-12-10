package clases.proyectopruebas.view;

import clases.proyectopruebas.dao.PrestamoDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.view.view.Bienvenida;
import clases.proyectopruebas.view.view.Gestion_libros;
import clases.proyectopruebas.view.view.Gestion_usuarios;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class BibliotecarioView {

    private Usuario usuario;
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
        topBar.setStyle("-fx-background-color: #3498db;");

        Label title = new Label("BIBLIOTECA DIGITAL - BIBLIOTECARIO");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label(usuario.getNombre() + " | " + usuario.getTipoUsuario().getNombre());
        userInfo.setTextFill(Color.WHITE);

        logoutBtn = new Button("Cerrar Sesion");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> returnToBienvenida());

        topBar.getChildren().addAll(title, spacer, userInfo, logoutBtn);
        return topBar;
    }

    private VBox createSideMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(220);
        menu.setStyle("-fx-background-color: #ecf0f1;");

        Label menuTitle = new Label("MENU BIBLIOTECARIO");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        menuTitle.setTextFill(Color.web("#3498db"));

        Button[] buttons = {
                createMenuButton("Gestionar Libros", "manageBooks"),
                createMenuButton("Registrar Prestamo", "registerLoan"),
                createMenuButton("Registrar Devolucion", "registerReturn"),
                createMenuButton("Gestionar Usuarios", "manageUsers"),
                createMenuButton("Ver Prestamos Activos", "viewActiveLoans"),
                createMenuButton("Calcular Multas", "calculateFines"),
                createMenuButton("Reportes", "reports"),
                createMenuButton("Busqueda Avanzada", "advancedSearch")
        };

        menu.getChildren().add(menuTitle);
        menu.getChildren().addAll(buttons);

        return menu;
    }

    private Button createMenuButton(String text, String action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: #3498db;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;" +
                "-fx-text-alignment: left;" +
                "-fx-padding: 12 15;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;");
        btn.setTextFill(Color.web("#2c3e50"));

        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #2980b9;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;"));
        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: transparent;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-border-color: #3498db;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;"));

        btn.setOnAction(e -> handleMenuAction(action));
        return btn;
    }

    private StackPane createContent() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));

        // Dashboard con estadísticas
        GridPane dashboard = createDashboardStats();
        content.getChildren().add(dashboard);

        return content;
    }

    private GridPane createDashboardStats() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        try {
            List<Map<String, Object>> prestamosActivos = prestamoDAO.getPrestamosActivos();
            List<Map<String, Object>> multasPendientes = prestamoDAO.getMultasPendientes();

            VBox card1 = createStatCard("Prestamos Activos", String.valueOf(prestamosActivos.size()), "#3498db");
            VBox card2 = createStatCard("Multas Pendientes", String.valueOf(multasPendientes.size()), "#e74c3c");
            VBox card3 = createStatCard("Libros Disponibles", "0", "#2ecc71"); // Necesitarías un método para esto
            VBox card4 = createStatCard("Usuarios Activos", "0", "#f39c12"); // Necesitarías un método para esto

            grid.add(card1, 0, 0);
            grid.add(card2, 1, 0);
            grid.add(card3, 0, 1);
            grid.add(card4, 1, 1);

        } catch (Exception e) {
            Label errorLabel = new Label("Error al cargar estadísticas: " + e.getMessage());
            errorLabel.setTextFill(Color.RED);
            grid.add(errorLabel, 0, 0);
        }

        return grid;
    }

    private VBox createStatCard(String title, String value, String color) {
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

    private void handleMenuAction(String action) {
        // Implementar acciones del menú
        switch (action) {
            case "registerLoan":
                showLoanRegistration();
                break;
            case "registerReturn":
                showReturnRegistration();
                break;
            case "manageBooks":
                showBookManagement();
                break;
            case "manageUsers":
                showUserManagement();
                break;
        }
    }

    private void showLoanRegistration() {
        RegistroPrestamos loanView = new RegistroPrestamos(usuario);
        Stage stage = new Stage();
        stage.setScene(new Scene(loanView.getView(), 800, 600));
        stage.setTitle("Registrar Préstamo");
        stage.show();
    }

    private void showReturnRegistration() {
        RegistroDevoluciones returnView = new RegistroDevoluciones(usuario);
        Stage stage = new Stage();
        stage.setScene(new Scene(returnView.getView(), 800, 600));
        stage.setTitle("Registrar Devolución");
        stage.show();
    }

    private void showBookManagement() {
        Gestion_libros bookView = new Gestion_libros();
        Stage stage = new Stage();
        stage.setScene(new Scene(bookView.getView(), 1000, 700));
        stage.setTitle("Gestión de Libros");
        stage.show();
    }

    private void showUserManagement() {
        Gestion_usuarios userView = new Gestion_usuarios();
        Stage stage = new Stage();
        stage.setScene(new Scene(userView.getView(), 1000, 700));
        stage.setTitle("Gestión de Usuarios");
        stage.show();
    }

    private void returnToBienvenida() {
        // Cerrar la ventana actual
        Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
        currentStage.close();

        try {
            Stage bienvenidaStage = new Stage();
            Bienvenida bienvenida = new Bienvenida();
            bienvenida.start(bienvenidaStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
