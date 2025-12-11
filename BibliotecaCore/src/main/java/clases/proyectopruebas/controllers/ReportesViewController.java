package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.PrestamoDAO;
import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Prestamo;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.utils.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ReportesViewController {

    @FXML private Label lblStatus;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ReportService reportService = new ReportService();

    public void onLibrosPrestados() {
        try {
            List<Prestamo> prestamos = prestamoDAO.findAll();
            reportService.generarReporteLibrosPrestados(prestamos, Path.of("reporte_libros_prestados.pdf"));
            lblStatus.setText("Reporte generado: reporte_libros_prestados.pdf");
        } catch (Exception e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    public void onUsuariosMorosos() {
        try {
            List<Usuario> usuarios = usuarioDAO.findAll();
            List<Usuario> morosos = usuarios.stream()
                    .filter(u -> u.getEstado() == EstadoUsuario.MOROSO)
                    .collect(Collectors.toList());
            reportService.generarReporteUsuariosMorosos(morosos, Path.of("reporte_usuarios_morosos.pdf"));
            lblStatus.setText("Reporte generado: reporte_usuarios_morosos.pdf");
        } catch (Exception e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }
}
