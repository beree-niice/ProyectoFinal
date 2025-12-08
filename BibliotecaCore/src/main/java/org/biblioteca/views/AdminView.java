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

public class AdminView {

    private Usuario usuario;
    private UsuarioDAO usuarioDAO;
    private LibroDAO libroDAO;
    private PrestamoDAO prestamoDAO;
    private BorderPane layout;

    public AdminView() {
        this.usuarioDAO = new UsuarioDAO();
        this.libroDAO = new LibroDAO();
        this.prestamoDAO = new PrestamoDAO();
    }

    public BorderPane getAdminView(Usuario usuario, String color) {
        this.usuario = usuario;

        layout = new BorderPane();
        layout.setPadding(new Insets(10));

        HBox topBar = createAdminTopBar(usuario, color);
        VBox sideMenu = createAdminSideMenu(color);
        StackPane content = createAdminContent();

        layout.setTop(topBar);
        layout.setLeft(sideMenu);
        layout.setCenter(content);

        return layout;
    }

    private HBox createAdminTopBar(Usuario usuario, String color) {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: " + color + ";");

        Label title = new Label("BIBLIOTECA DIGITAL - ADMINISTRADOR");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label(usuario.getNombre() + " | Admin ID: " + usuario.getIdUsuario());
        userInfo.setTextFill(Color.WHITE);

        Button systemBtn = new Button("Configuracion Sistema");
        systemBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");
        systemBtn.setOnAction(e -> showSystemConfig());

        Button backupBtn = new Button("Backup BD");
        backupBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white;");
        backupBtn.setOnAction(e -> showBackupDialog());

        Button logoutBtn = new Button("Cerrar Sesion");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        topBar.getChildren().addAll(title, spacer, systemBtn, backupBtn, userInfo, logoutBtn);
        return topBar;
    }

    private VBox createAdminSideMenu(String color) {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(250);
        menu.setStyle("-fx-background-color: #2c3e50;");

        Label menuTitle = new Label("PANEL DE ADMINISTRACION");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        menuTitle.setTextFill(Color.WHITE);

        // Sección: Gestión de Usuarios
        Label section1 = new Label("GESTION DE USUARIOS");
        section1.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        section1.setTextFill(Color.web(color));

        Button[] userButtons = {
                createAdminButton("Gestionar Lectores", "Gestionar Lectores"),
                createAdminButton("Gestionar Bibliotecarios", "Gestionar Bibliotecarios"),
                createAdminButton("Crear Nuevo Usuario", "Crear Nuevo Usuario"),
                createAdminButton("Reporte Usuarios", "Reporte Usuarios")
        };

        // Sección: Sistema
        Label section2 = new Label("SISTEMA");
        section2.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        section2.setTextFill(Color.web(color));

        Button[] systemButtons = {
                createAdminButton("Configuracion", "Configuracion"),
                createAdminButton("Auditoria y Logs", "Auditoria"),
                createAdminButton("Estadisticas BD", "Estadisticas BD"),
                createAdminButton("Exportar Datos", "Exportar Datos")
        };

        // Sección: Reportes Avanzados
        Label section3 = new Label("REPORTES AVANZADOS");
        section3.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        section3.setTextFill(Color.web(color));


        /*pendiente de consulta
        Button[] reportButtons = {
                createAdminButton("Reporte Financiero", "financialReport"),
                createAdminButton("Analisis de Uso", "usageAnalysis"),
                createAdminButton("Historial Completo", "fullHistory")
        };+*/
        menu.getChildren().addAll(
                menuTitle, new Separator(),
                section1
        );
        menu.getChildren().addAll(userButtons);
        menu.getChildren().addAll(new Separator(), section2);
        menu.getChildren().addAll(systemButtons);
        menu.getChildren().addAll(new Separator(), section3);
        menu.getChildren().addAll(reportButtons);

        return menu;
    }

    private Button createAdminButton(String text, String action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent;" +
                "-fx-text-fill: #ecf0f1;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10 15;" +
                "-fx-cursor: hand;");

        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;"));
        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: transparent;" +
                        "-fx-text-fill: #ecf0f1;"));

        btn.setOnAction(e -> handleMenuAction(action));
        return btn;
    }

    private StackPane createAdminContent() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));

        VBox controlPanel = new VBox(20);

        Label header = new Label("PANEL DE CONTROL DEL SISTEMA");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#9b59b6"));

        // Estadísticas del sistema
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);
        statsGrid.setPadding(new Insets(20));
        statsGrid.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 10;");

        List<Usuario> totalUsuarios = usuarioDAO.findAll();
        List<Usuario> bibliotecarios = usuarioDAO.findByTipo(TipoUsuario.BIBLIOTECARIO);
        List<Libro> totalLibros = libroDAO.findAll();
        List<Map<String, Object>> prestamosActivos = prestamoDAO.getPrestamosActivos();
        List<Map<String, Object>> multasPendientes = prestamoDAO.getMultasPendientes();

        addStat(statsGrid, "Total Usuarios", String.valueOf(totalUsuarios.size()), 0, 0);
        addStat(statsGrid, "Bibliotecarios", String.valueOf(bibliotecarios.size()), 1, 0);
        addStat(statsGrid, "Total Libros", String.valueOf(totalLibros.size()), 0, 1);
        addStat(statsGrid, "Prestamos Activos", String.valueOf(prestamosActivos.size()), 1, 1);
        addStat(statsGrid, "Multas Pendientes", String.valueOf(multasPendientes.size()), 0, 2);
        addStat(statsGrid, "Estado Sistema", "ACTIVO", 1, 2);

        // Acciones rápidas
        HBox quickActions = new HBox(15);
        quickActions.setAlignment(Pos.CENTER);

        Button btnOptimize = createQuickActionButton("Optimizar BD", "#e74c3c");
        btnOptimize.setOnAction(e -> optimizeDatabase());

        Button btnGenerateReport = createQuickActionButton("Generar Reporte", "#3498db");
        btnGenerateReport.setOnAction(e -> generateQuickReport());

        Button btnCheckSecurity = createQuickActionButton("Verificar Seguridad", "#2ecc71");
        btnCheckSecurity.setOnAction(e -> checkSecurity());

        quickActions.getChildren().addAll(btnOptimize, btnGenerateReport, btnCheckSecurity);

        controlPanel.getChildren().addAll(header, statsGrid, quickActions);
        content.getChildren().add(controlPanel);

        return content;
    }

    private void addStat(GridPane grid, String label, String value, int col, int row) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(15));
        statBox.setStyle("-fx-background-color: white; -fx-border-radius: 8;");

        Label statLabel = new Label(label);
        statLabel.setFont(Font.font("Arial", 12));
        statLabel.setTextFill(Color.GRAY);

        Label statValue = new Label(value);
        statValue.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        statValue.setTextFill(Color.web("#2c3e50"));

        statBox.getChildren().addAll(statLabel, statValue);
        grid.add(statBox, col, row);
    }

    private Button createQuickActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12 25;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 14px;");
        btn.setMinWidth(120);
        return btn;
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "Gestionar lectores":
                showUserManagement(TipoUsuario.LECTOR);
                break;
            case "Gestionar blibiotecarios":
                showUserManagement(TipoUsuario.BIBLIOTECARIO);
                break;
            case "Crear nuevo usuario":
                showCreateUserDialog();
                break;
            case "Reporte de usuarios":
                showUserReport();
                break;
            case "Configuracion":
                showSystemConfig();
                break;
            case "Auditoria":
                showAuditLogs();
                break;
            case "BD Estado":
                showDatabaseStats();
                break;
            case "Exportar datos":
                showExportData();
                break;
                /*pendientes
            case "financialReport":
                showFinancialReport();
                break;
            case "usageAnalysis":
                showUsageAnalysis();
                break;
            case "fullHistory":
                showFullHistory();
                break;*/
        }
    }

    private void showUserManagement(TipoUsuario tipo) {
        UserManagementView userView = new UserManagementView(usuarioDAO, tipo);
        layout.setCenter(userView.getView());
    }

    private void showCreateUserDialog() {
        AdminCreateUserDialog dialog = new AdminCreateUserDialog(usuarioDAO);
        dialog.showAndWait();
    }

    private void showSystemConfig() {
        SystemConfigView configView = new SystemConfigView();
        layout.setCenter(configView.getView());
    }

    private void optimizeDatabase() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Optimizacion de BD");
        alert.setHeaderText("Funcionalidad en desarrollo");
        alert.setContentText("Esta funcion optimizara la base de datos para mejorar el rendimiento.");
        alert.showAndWait();
    }

    private void generateQuickReport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Generar Reporte");
        alert.setHeaderText("Funcionalidad en desarrollo");
        alert.setContentText("Se generara un reporte completo del sistema.");
        alert.showAndWait();
    }

    private void checkSecurity() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Verificar Seguridad");
        alert.setHeaderText("Funcionalidad en desarrollo");
        alert.setContentText("Se analizara la seguridad del sistema.");
        alert.showAndWait();
    }

    // Métodos placeholder para otras funciones
    private void showUserReport() {}
    private void showAuditLogs() {}
    private void showDatabaseStats() {}
    private void showExportData() {}
    private void showFinancialReport() {}
    private void showUsageAnalysis() {}
    private void showFullHistory() {}
    private void showBackupDialog() {}
}