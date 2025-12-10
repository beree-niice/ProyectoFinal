package clases.proyectopruebas.view;
import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import clases.proyectopruebas.view.view.Gestion_usuarios;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class AdminView {
        private Usuario usuario;

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
            topBar.setStyle("-fx-background-color: #9b59b6;");

            Label title = new Label("BIBLIOTECA DIGITAL - ADMINISTRADOR");
            title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            title.setTextFill(Color.WHITE);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label userInfo = new Label(usuario.getNombre() + " | " + usuario.getTipoUsuario().getNombre());
            userInfo.setTextFill(Color.WHITE);

            Button logoutBtn = new Button("Cerrar Sesion");
            logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

            topBar.getChildren().addAll(title, spacer, userInfo, logoutBtn);
            return topBar;
        }

        private VBox createSideMenu() {
            VBox menu = new VBox(10);
            menu.setPadding(new Insets(20));
            menu.setPrefWidth(250);
            menu.setStyle("-fx-background-color: #2c3e50;");

            Label menuTitle = new Label("PANEL DE ADMINISTRACION");
            menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            menuTitle.setTextFill(Color.WHITE);

            // Sección Sistema
            Label section1 = new Label("SISTEMA");
            section1.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            section1.setTextFill(Color.web("#3498db"));

            Button[] systemButtons = {
                    createAdminButton("Gestionar Usuarios", "manageUsers"),
                    createAdminButton("Gestionar Bibliotecarios", "manageLibrarians"),
                    createAdminButton("Configuracion Sistema", "systemConfig"),
                    createAdminButton("Permisos y Roles", "permissions"),
                    createAdminButton("Auditoria y Logs", "audit")
            };

            // Sección Base de Datos
            Label section2 = new Label("BASE DE DATOS");
            section2.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            section2.setTextFill(Color.web("#3498db"));

            Button[] dbButtons = {
                    createAdminButton("Backup Completo", "backup"),
                    createAdminButton("Restaurar Backup", "restore"),
                    createAdminButton("Limpieza BD", "cleanup"),
                    createAdminButton("Estadisticas BD", "dbStats")
            };

            // Sección Reportes
            Label section3 = new Label("REPORTES");
            section3.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            section3.setTextFill(Color.web("#3498db"));

            Button[] reportButtons = {
                    createAdminButton("Reporte Financiero", "financialReport"),
                    createAdminButton("Analisis de Uso", "usageAnalysis"),
                    createAdminButton("Reporte Usuarios", "userReport"),
                    createAdminButton("Historial Completo", "fullHistory")
            };

            menu.getChildren().addAll(
                    menuTitle, new Separator(),
                    section1
            );
            for (Button btn : systemButtons) menu.getChildren().add(btn);
            menu.getChildren().addAll(new Separator(), section2);
            for (Button btn : dbButtons) menu.getChildren().add(btn);
            menu.getChildren().addAll(new Separator(), section3);
            for (Button btn : reportButtons) menu.getChildren().add(btn);

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

        private StackPane createContent() {
            StackPane content = new StackPane();
            content.setPadding(new Insets(20));

            VBox controlPanel = new VBox(20);

            Label header = new Label("PANEL DE CONTROL DEL SISTEMA");
            header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            header.setTextFill(Color.web("#9b59b6"));

            // Estadísticas
            GridPane statsGrid = createSystemStats();

            controlPanel.getChildren().addAll(header, statsGrid);
            content.getChildren().add(controlPanel);

            return content;
        }

        private GridPane createSystemStats() {
            GridPane grid = new GridPane();
            grid.setHgap(20);
            grid.setVgap(15);
            grid.setPadding(new Insets(20));
            grid.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 10;");

            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();

                // Contar usuarios por tipo
                long totalUsuarios = usuarioDAO.count();
                long lectores = usuarioDAO.findByTipo(TipoUsuario.LECTOR).size();
                long bibliotecarios = usuarioDAO.findByTipo(TipoUsuario.BIBLIOTECARIO).size();
                long admins = usuarioDAO.findByTipo(TipoUsuario.ADMIN).size();

                addStat(grid, "Total Usuarios", String.valueOf(totalUsuarios), 0, 0);
                addStat(grid, "Lectores", String.valueOf(lectores), 1, 0);
                addStat(grid, "Bibliotecarios", String.valueOf(bibliotecarios), 0, 1);
                addStat(grid, "Administradores", String.valueOf(admins), 1, 1);

            } catch (Exception e) {
                Label error = new Label("Error al cargar estadísticas: " + e.getMessage());
                error.setTextFill(Color.RED);
                grid.add(error, 0, 0);
            }

            return grid;
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

        private void handleMenuAction(String action) {
            switch (action) {
                case "manageUsers":
                    showUserManagement();
                    break;
                case "manageLibrarians":
                    showLibrarianManagement();
                    break;
                case "systemConfig":
                    showSystemConfig();
                    break;
            }
        }

        private void showUserManagement() {
            Gestion_usuarios userView = new Gestion_usuarios();
            Stage stage = new Stage();
            stage.setScene(new Scene(userView.getView(), 1000, 700));
            stage.setTitle("Gestión de Usuarios");
            stage.show();
        }

        private void showLibrarianManagement() {
            // Implementar gestión de bibliotecarios
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Gestión de Bibliotecarios");
            alert.setHeaderText("Funcionalidad en desarrollo");
            alert.showAndWait();
        }

        private void showSystemConfig() {
            // Implementar configuración del sistema
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Configuración del Sistema");
            alert.setHeaderText("Funcionalidad en desarrollo");
            alert.showAndWait();
        }
    }