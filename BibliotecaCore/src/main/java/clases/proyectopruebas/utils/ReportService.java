package clases.proyectopruebas.utils;

import clases.proyectopruebas.models.Prestamo;
import clases.proyectopruebas.models.Usuario;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void generarReporteLibrosPrestados(List<Prestamo> prestamos, Path path) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.beginText();
                cs.newLineAtOffset(50, 780);
                cs.showText("Reporte - Libros Prestados");
                cs.endText();

                cs.setFont(PDType1Font.HELVETICA, 12);
                float y = 750;
                for (Prestamo p : prestamos) {
                    if (y < 80) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        y = 780;
                    }
                    cs.beginText();
                    cs.newLineAtOffset(50, y);
                    String linea = String.format("Usuario: %s | Libro: %s | Desde: %s | Hasta: %s | Devuelto: %s",
                            p.getUsuario() != null ? p.getUsuario().getNombre() : "",
                            p.getLibro() != null ? p.getLibro().getTitulo() : "",
                            p.getFechaPrestamo() != null ? DF.format(p.getFechaPrestamo()) : "",
                            p.getFechaDevolucionEsperada() != null ? DF.format(p.getFechaDevolucionEsperada()) : "",
                            p.getFechaDevolucionReal() != null ? DF.format(p.getFechaDevolucionReal()) : "-");
                    cs.showText(linea);
                    cs.endText();
                    y -= 18;
                }
            }
            doc.save(path.toFile());
        }
    }

    public void generarReporteUsuariosMorosos(List<Usuario> morosos, Path path) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.beginText();
                cs.newLineAtOffset(50, 780);
                cs.showText("Reporte - Usuarios Morosos");
                cs.endText();

                cs.setFont(PDType1Font.HELVETICA, 12);
                float y = 750;
                for (Usuario u : morosos) {
                    if (y < 80) {
                        cs.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        y = 780;
                    }
                    cs.beginText();
                    cs.newLineAtOffset(50, y);
                    String linea = String.format("%s | Email: %s | DNI: %s",
                            u.getNombre(), u.getEmail(), u.getDni());
                    cs.showText(linea);
                    cs.endText();
                    y -= 18;
                }
            }
            doc.save(path.toFile());
        }
    }
}
