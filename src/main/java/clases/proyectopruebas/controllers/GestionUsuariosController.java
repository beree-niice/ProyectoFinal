package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

public class GestionUsuariosController {
    private UsuarioDAO usuarioDAO;
    private ObservableList<Usuario> userData;
    private TableView<Usuario> userTable;

    // Filtros actuales
    private TipoUsuario tipoFilter;
    private EstadoUsuario estadoFilter;
    private String searchText;

    public GestionUsuariosController() {
        this.usuarioDAO = new UsuarioDAO();
        this.userData = FXCollections.observableArrayList();
        this.tipoFilter = null;
        this.estadoFilter = null;
        this.searchText = "";
    }

    public void setTableView(TableView<Usuario> tableView) {
        this.userTable = tableView;
    }

    // ========== MÉTODOS DE CARGA Y FILTRADO ==========

    public void loadAllUsers() {
        try {
            List<Usuario> usuarios = usuarioDAO.findAll();
            userData.setAll(usuarios);
            if (userTable != null) {
                userTable.setItems(userData);
            }
        } catch (Exception e) {
            showError("Error al cargar usuarios", e.getMessage());
        }
    }

    public void filterUsers(TipoUsuario tipo, EstadoUsuario estado, String search) {
        this.tipoFilter = tipo;
        this.estadoFilter = estado;
        this.searchText = (search != null) ? search.trim().toLowerCase() : "";

        try {
            // Primero obtener todos los usuarios
            List<Usuario> allUsers = usuarioDAO.findAll();

            // Aplicar filtros localmente
            List<Usuario> filteredUsers = allUsers.stream()
                    .filter(createFilterPredicate())
                    .toList();

            userData.setAll(filteredUsers);
            if (userTable != null) {
                userTable.setItems(userData);
                userTable.refresh();
            }

        } catch (Exception e) {
            showError("Error al filtrar usuarios", e.getMessage());
        }
    }

    private Predicate<Usuario> createFilterPredicate() {
        return usuario -> {
            boolean matches = true;

            // Filtrar por tipo
            if (tipoFilter != null) {
                matches = matches && usuario.getTipoUsuario() == tipoFilter;
            }

            // Filtrar por estado
            if (estadoFilter != null) {
                matches = matches && usuario.getEstado() == estadoFilter;
            }

            // Filtrar por texto de búsqueda
            if (!searchText.isEmpty()) {
                matches = matches && (
                        usuario.getNombre().toLowerCase().contains(searchText) ||
                                usuario.getDni().toLowerCase().contains(searchText) ||
                                usuario.getEmail().toLowerCase().contains(searchText)
                );
            }

            return matches;
        };
    }

    public void clearFilters() {
        this.tipoFilter = null;
        this.estadoFilter = null;
        this.searchText = "";
        loadAllUsers();
    }

    // ========== MÉTODOS DE EXPORTACIÓN CSV ==========

    public boolean exportToCSV(List<Usuario> usuarios, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // Escribir encabezados
            writer.write("ID;DNI;Nombre;Email;Teléfono;Dirección;Tipo Usuario;Estado;Límite Préstamos;Fecha Registro;Última Actividad");
            writer.newLine();

            // Escribir datos de usuarios
            for (Usuario usuario : usuarios) {
                String ultimaActividad = (usuario.getFechaUltimaActividad() != null) ?
                        usuario.getFechaUltimaActividad().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";

                String line = String.format("%d;%s;%s;%s;%s;%s;%s;%s;%d;%s;%s",
                        usuario.getIdUsuario(),
                        escapeCSV(usuario.getDni()),
                        escapeCSV(usuario.getNombre()),
                        escapeCSV(usuario.getEmail()),
                        escapeCSV(usuario.getTelefono()),
                        escapeCSV(usuario.getDireccion()),
                        usuario.getTipoUsuario().getNombre(),
                        usuario.getEstado().getNombre(),
                        usuario.getLimitePrestamos(),
                        usuario.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        ultimaActividad
                );
                writer.write(line);
                writer.newLine();
            }

            // Escribir metadata
            writer.newLine();
            writer.write(";;;METADATA;;");
            writer.newLine();
            writer.write(String.format("Fecha de exportación:;%s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            writer.newLine();
            writer.write(String.format("Total de usuarios exportados:;%d", usuarios.size()));
            writer.newLine();
            writer.write(String.format("Filtros aplicados:;Tipo: %s | Estado: %s | Búsqueda: %s",
                    (tipoFilter != null) ? tipoFilter.getNombre() : "Todos",
                    (estadoFilter != null) ? estadoFilter.getNombre() : "Todos",
                    (!searchText.isEmpty()) ? searchText : "Ninguna"
            ));

            return true;
        } catch (IOException e) {
            showError("Error de escritura", "No se pudo escribir el archivo CSV: " + e.getMessage());
            return false;
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        // Escapar comillas y punto y coma
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    public File getDefaultExportFile() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = String.format("usuarios_exportados_%s.csv", timestamp);
        return new File(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + fileName);
    }

    public File chooseExportLocation(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte de usuarios como CSV");

        // Configurar filtros de extensión
        FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter(
                "Archivos CSV (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(csvFilter);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));
        fileChooser.setSelectedExtensionFilter(csvFilter);

        // Sugerir nombre por defecto
        fileChooser.setInitialFileName(getDefaultExportFile().getName());
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Downloads"));

        return fileChooser.showSaveDialog(stage);
    }

    public boolean exportCurrentFilteredUsers(Stage stage) {
        if (userData.isEmpty()) {
            showWarning("Exportación cancelada", "No hay usuarios para exportar.");
            return false;
        }

        File exportFile = chooseExportLocation(stage);
        if (exportFile == null) {
            return false; // Usuario canceló
        }

        // Asegurar extensión .csv
        if (!exportFile.getName().toLowerCase().endsWith(".csv")) {
            exportFile = new File(exportFile.getAbsolutePath() + ".csv");
        }

        // Confirmar sobrescritura si el archivo existe
        if (exportFile.exists()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar sobrescritura");
            confirm.setHeaderText("El archivo ya existe");
            confirm.setContentText("¿Desea sobrescribir " + exportFile.getName() + "?");

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return false;
            }
        }

        try {
            boolean success = exportToCSV(userData, exportFile);
            if (success) {
                showExportSuccess(exportFile);
                return true;
            }
            return false;
        } catch (Exception e) {
            showError("Error de exportación", "No se pudo exportar a CSV: " + e.getMessage());
            return false;
        }
    }

    public boolean exportAllUsers(Stage stage) {
        try {
            List<Usuario> allUsers = usuarioDAO.findAll();
            if (allUsers.isEmpty()) {
                showWarning("Exportación cancelada", "No hay usuarios en la base de datos.");
                return false;
            }

            File exportFile = chooseExportLocation(stage);
            if (exportFile == null) {
                return false;
            }

            if (!exportFile.getName().toLowerCase().endsWith(".csv")) {
                exportFile = new File(exportFile.getAbsolutePath() + ".csv");
            }

            if (exportFile.exists()) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmar sobrescritura");
                confirm.setHeaderText("El archivo ya existe");
                confirm.setContentText("¿Desea sobrescribir " + exportFile.getName() + "?");

                if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                    return false;
                }
            }

            boolean success = exportToCSV(allUsers, exportFile);
            if (success) {
                showExportSuccess(exportFile);
                return true;
            }
            return false;
        } catch (Exception e) {
            showError("Error de exportación", "No se pudo exportar todos los usuarios: " + e.getMessage());
            return false;
        }
    }

    private void showExportSuccess(File file) {
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Exportación exitosa");
        success.setHeaderText("Usuarios exportados correctamente");
        success.setContentText(String.format(
                "Se exportaron %d usuarios a:\n%s\n\n" +
                        "El archivo está listo para usar en Excel, Google Sheets u otros programas de hoja de cálculo.",
                userData.size(),
                file.getAbsolutePath()
        ));
        success.showAndWait();
    }

    // ========== MÉTODOS DE ESTADÍSTICAS ==========

    public String getFilterStats() {
        int total = userData.size();
        long lectores = userData.stream()
                .filter(u -> u.getTipoUsuario() == TipoUsuario.LECTOR)
                .count();
        long bibliotecarios = userData.stream()
                .filter(u -> u.getTipoUsuario() == TipoUsuario.BIBLIOTECARIO)
                .count();
        long admins = userData.stream()
                .filter(u -> u.getTipoUsuario() == TipoUsuario.ADMIN)
                .count();

        return String.format(
                "Mostrando %d usuarios | Lectores: %d | Bibliotecarios: %d | Administradores: %d",
                total, lectores, bibliotecarios, admins
        );
    }

    // ========== MÉTODOS DE GESTIÓN ==========

    public boolean updateUserStatus(Usuario usuario, EstadoUsuario nuevoEstado) {
        try {
            boolean success = usuarioDAO.actualizarEstado(usuario.getIdUsuario(), nuevoEstado);
            if (success) {
                usuario.setEstado(nuevoEstado);
                if (userTable != null) {
                    userTable.refresh();
                }
                showInfo("Estado actualizado",
                        String.format("El estado de %s ha sido cambiado a %s",
                                usuario.getNombre(), nuevoEstado.getNombre()));
                return true;
            }
            return false;
        } catch (Exception e) {
            showError("Error al actualizar estado", e.getMessage());
            return false;
        }
    }

    public boolean updateUserType(Usuario usuario, TipoUsuario nuevoTipo) {
        try {
            usuario.setTipoUsuario(nuevoTipo);
            usuarioDAO.update(usuario);

            if (userTable != null) {
                userTable.refresh();
            }

            showInfo("Tipo actualizado",
                    String.format("El tipo de %s ha sido cambiado a %s",
                            usuario.getNombre(), nuevoTipo.getNombre()));
            return true;
        } catch (Exception e) {
            showError("Error al actualizar tipo", e.getMessage());
            return false;
        }
    }

    public boolean updateUser(Usuario usuario) {
        try {
            usuarioDAO.update(usuario);

            if (userTable != null) {
                userTable.refresh();
            }

            showInfo("Usuario actualizado",
                    String.format("Los datos de %s han sido actualizados", usuario.getNombre()));
            return true;
        } catch (Exception e) {
            showError("Error al actualizar usuario", e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(Usuario usuario) {
        try {
            usuarioDAO.delete(usuario.getIdUsuario());
            userData.remove(usuario);

            showInfo("Usuario eliminado",
                    String.format("%s ha sido eliminado del sistema", usuario.getNombre()));
            return true;
        } catch (Exception e) {
            showError("Error al eliminar usuario", e.getMessage());
            return false;
        }
    }

    public Usuario getSelectedUser(TableView<Usuario> table) {
        return table.getSelectionModel().getSelectedItem();
    }

    public boolean validateUserSelection(Usuario usuario) {
        if (usuario == null) {
            showWarning("Selección requerida", "Por favor seleccione un usuario de la tabla");
            return false;
        }
        return true;
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ========== GETTERS Y SETTERS ==========

    public ObservableList<Usuario> getUserData() {
        return userData;
    }

    public UsuarioDAO getUsuarioDAO() {
        return usuarioDAO;
    }

    public TipoUsuario getTipoFilter() {
        return tipoFilter;
    }

    public EstadoUsuario getEstadoFilter() {
        return estadoFilter;
    }

    public String getSearchText() {
        return searchText;
    }
}