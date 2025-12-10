package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import clases.proyectopruebas.view.AdminView;
import clases.proyectopruebas.view.view.Gestion_usuarios;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.List;

public class AdminController {
    private AdminView adminView;
    private Usuario usuario;
    private UsuarioDAO usuarioDAO;

    public AdminController(Usuario usuario) {
        this.usuario = usuario;
        this.usuarioDAO = new UsuarioDAO();
        this.adminView = new AdminView();
    }

    public void initialize() {
        // Obtener la vista del administrador
        var view = adminView.getView(usuario);

        // Crear la escena y mostrar la ventana
        Scene scene = new Scene(view, 1200, 800);
        Stage stage = new Stage();
        stage.setTitle("Panel de Administración");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // Métodos para manejar las acciones del menú
    public void handleMenuAction(String action) {
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
            case "permissions":
                showPermissions();
                break;
            case "audit":
                showAuditLogs();
                break;
            case "backup":
                performBackup();
                break;
            case "restore":
                restoreBackup();
                break;
            case "cleanup":
                performCleanup();
                break;
            case "dbStats":
                showDatabaseStats();
                break;
            case "financialReport":
                generateFinancialReport();
                break;
            case "usageAnalysis":
                showUsageAnalysis();
                break;
            case "userReport":
                generateUserReport();
                break;
            case "fullHistory":
                showFullHistory();
                break;
            default:
                showAlert("Acción no implementada", "Esta funcionalidad está en desarrollo");
                break;
        }
    }

    // ========== MÉTODOS DE BÚSQUEDA Y FILTRADO ==========

    public List<Usuario> searchUsers(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return usuarioDAO.findAll();
            } else {
                return usuarioDAO.buscar(searchTerm);
            }
        } catch (Exception e) {
            showAlert("Error", "Error al buscar usuarios: " + e.getMessage());
            return List.of();
        }
    }

    public List<Usuario> filterUsersByType(TipoUsuario tipo) {
        try {
            return usuarioDAO.findByTipo(tipo);
        } catch (Exception e) {
            showAlert("Error", "Error al filtrar usuarios por tipo: " + e.getMessage());
            return List.of();
        }
    }

    public List<Usuario> searchUsersByType(String searchTerm, TipoUsuario tipo) {
        try {
            // Primero buscamos por término
            List<Usuario> resultados = usuarioDAO.buscar(searchTerm);

            // Luego filtramos por tipo si se especificó
            if (tipo != null) {
                resultados.removeIf(usuario -> !usuario.getTipoUsuario().equals(tipo));
            }

            return resultados;
        } catch (Exception e) {
            showAlert("Error", "Error en búsqueda avanzada: " + e.getMessage());
            return List.of();
        }
    }

    // ========== MÉTODOS ESTADÍSTICOS ==========

    public long getTotalUsers() {
        try {
            return usuarioDAO.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getUsersByTypeCount(TipoUsuario tipo) {
        try {
            return usuarioDAO.countByTipo(tipo);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getSystemStats() {
        try {
            long totalUsuarios = getTotalUsers();
            long lectores = getUsersByTypeCount(TipoUsuario.LECTOR);
            long bibliotecarios = getUsersByTypeCount(TipoUsuario.BIBLIOTECARIO);
            long admins = getUsersByTypeCount(TipoUsuario.ADMIN);

            return String.format(
                    "Estadísticas del Sistema:\n\n" +
                            "Total de Usuarios: %d\n" +
                            "Lectores: %d\n" +
                            "Bibliotecarios: %d\n" +
                            "Administradores: %d\n\n" +
                            "Última actualización: %s",
                    totalUsuarios, lectores, bibliotecarios, admins,
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        } catch (Exception e) {
            return "Error al cargar estadísticas: " + e.getMessage();
        }
    }

    // ========== MÉTODOS DE GESTIÓN ==========

    public boolean updateUserStatus(int userId, String newStatus) {
        try {
            // Aquí necesitarías implementar la lógica para cambiar el estado del usuario
            // Por ahora, solo mostramos un mensaje
            showAlert("Actualizar Estado", "Cambiando estado del usuario ID: " + userId + " a: " + newStatus);
            return true;
        } catch (Exception e) {
            showAlert("Error", "Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserLoanLimit(int userId, int newLimit) {
        try {
            return usuarioDAO.actualizarLimitePrestamos(userId, newLimit);
        } catch (Exception e) {
            showAlert("Error", "Error al actualizar límite de préstamos: " + e.getMessage());
            return false;
        }
    }

    public boolean canUserBorrow(int userId) {
        try {
            return usuarioDAO.puedeTomarPrestamo(userId);
        } catch (Exception e) {
            showAlert("Error", "Error al verificar préstamos: " + e.getMessage());
            return false;
        }
    }

    // ========== MÉTODOS DE VISTAS ==========

    private void showUserManagement() {
        try {
            Gestion_usuarios userView = new Gestion_usuarios();
            Stage stage = new Stage();
            stage.setScene(new Scene(userView.getView(), 1000, 700));
            stage.setTitle("Gestión de Usuarios");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "No se pudo abrir la gestión de usuarios: " + e.getMessage());
        }
    }

    private void showLibrarianManagement() {
        showAlert("Gestión de Bibliotecarios", "Funcionalidad en desarrollo");
    }

    private void showSystemConfig() {
        showAlert("Configuración del Sistema", "Funcionalidad en desarrollo");
    }

    private void showPermissions() {
        showAlert("Permisos y Roles", "Funcionalidad en desarrollo");
    }

    private void showAuditLogs() {
        showAlert("Auditoría y Logs", "Funcionalidad en desarrollo");
    }

    private void performBackup() {
        try {
            boolean success = usuarioDAO.performBackup();
            if (success) {
                showAlert("Backup", "Backup realizado exitosamente");
            } else {
                showAlert("Backup", "Error al realizar el backup");
            }
        } catch (Exception e) {
            showAlert("Error", "Error en backup: " + e.getMessage());
        }
    }

    private void restoreBackup() {
        showAlert("Restaurar Backup", "Funcionalidad en desarrollo");
    }

    private void performCleanup() {
        showAlert("Limpieza BD", "Funcionalidad en desarrollo");
    }

    private void showDatabaseStats() {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Estadísticas de la Base de Datos");
            alert.setHeaderText("Información del Sistema");
            alert.setContentText(getSystemStats());
            alert.showAndWait();
        } catch (Exception e) {
            showAlert("Error", "Error al obtener estadísticas: " + e.getMessage());
        }
    }

    private void generateFinancialReport() {
        showAlert("Reporte Financiero", "Funcionalidad en desarrollo");
    }

    private void showUsageAnalysis() {
        showAlert("Análisis de Uso", "Funcionalidad en desarrollo");
    }

    private void generateUserReport() {
        try {
            // Generar un reporte simple de usuarios
            StringBuilder report = new StringBuilder();
            report.append("REPORTE DE USUARIOS\n");
            report.append("===================\n\n");

            List<Usuario> todosUsuarios = usuarioDAO.findAll();
            report.append("Total de usuarios: ").append(todosUsuarios.size()).append("\n\n");

            for (TipoUsuario tipo : TipoUsuario.values()) {
                long count = getUsersByTypeCount(tipo);
                report.append(tipo.getNombre()).append(": ").append(count).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reporte de Usuarios");
            alert.setHeaderText("Resumen del Sistema");
            alert.setContentText(report.toString());
            alert.showAndWait();
        } catch (Exception e) {
            showAlert("Error", "Error al generar reporte: " + e.getMessage());
        }
    }

    private void showFullHistory() {
        showAlert("Historial Completo", "Funcionalidad en desarrollo");
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para cerrar sesión
    public void logout() {
        // Este método se llamará desde la vista
        // La lógica de cierre de ventana se maneja en la vista
    }

    // Método para obtener el DAO (útil para otras clases)
    public UsuarioDAO getUsuarioDAO() {
        return usuarioDAO;
    }
}