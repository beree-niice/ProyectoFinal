package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.LibroDAO;
import clases.proyectopruebas.models.Libro;
import clases.proyectopruebas.utils.ExportService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.nio.file.Path;
import java.util.List;

public class ExportViewController {

    @FXML private Label lblStatus;

    private final LibroDAO libroDAO = new LibroDAO();
    private final ExportService exportService = new ExportService();

    public void onExportJson() {
        try {
            List<Libro> libros = libroDAO.findAll();
            exportService.exportLibrosToJson(libros, Path.of("libros.json"));
            lblStatus.setText("Exportado a libros.json");
        } catch (Exception e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    public void onExportCsv() {
        try {
            List<Libro> libros = libroDAO.findAll();
            exportService.exportLibrosToCsv(libros, Path.of("libros.csv"));
            lblStatus.setText("Exportado a libros.csv");
        } catch (Exception e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    public void onImportJson() {
        try {
            List<Libro> libros = exportService.importLibrosFromJson(Path.of("libros.json"));
            // TODO: opcional persistir importados
            lblStatus.setText("Importados " + libros.size() + " libros desde JSON");
        } catch (Exception e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    public void onImportCsv() {
        try {
            List<Libro> libros = exportService.importLibrosFromCsv(Path.of("libros.csv"));
            // TODO: opcional persistir importados
            lblStatus.setText("Importados " + libros.size() + " libros desde CSV");
        } catch (Exception e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }
}
