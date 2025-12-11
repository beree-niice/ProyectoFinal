package clases.proyectopruebas.utils;

import clases.proyectopruebas.models.Libro;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExportService {

    private final ObjectMapper mapper = new ObjectMapper();

    public void exportLibrosToJson(List<Libro> libros, Path path) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), libros);
    }

    public List<Libro> importLibrosFromJson(Path path) throws IOException {
        return mapper.readValue(path.toFile(), new TypeReference<List<Libro>>(){});
    }

    public void exportLibrosToCsv(List<Libro> libros, Path path) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile()))) {
            writer.writeNext(new String[]{"id","titulo","autor","categoria","editorial","isbn","anio","cantidad"});
            for (Libro l : libros) {
                writer.writeNext(new String[]{
                        String.valueOf(l.getIdLibro()),
                        l.getTitulo(),
                        l.getAutores() != null ? l.getAutores().getFirst().getNombreCompleto() : "",
                        l.getCategoria() != null ? l.getCategoria().getNombre() : "",
                        l.getEditorial() != null ? l.getEditorial().getNombre() : "",
                        l.getIsbn(),
                        String.valueOf(l.getAnioPublicacion()),
                        String.valueOf(l.getCopiasTotales())
                });
            }
        }
    }

    public List<Libro> importLibrosFromCsv(Path path) throws IOException {
        List<Libro> libros = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(path.toFile()))) {
            String[] row;
            boolean header = true;
            while ((row = reader.readNext()) != null) {
                if (header) { header = false; continue; }
                Libro l = new Libro();
                l.setIdLibro(parseInt(row,0));
                l.setTitulo(row[1]);
                // Autor/Categoria/Editorial por nombre si se desea mapear después
                l.setIsbn(row[5]);
                l.setAnioPublicacion(parseInt(row,6));
                l.setCopiasTotales(parseInt(row,7));
                libros.add(l);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return libros;
    }

    private int parseInt(String[] row, int idx) {
        try { return Integer.parseInt(row[idx]); } catch (Exception e) { return 0; }
    }
}
