package org.biblioteca.views;

import org.biblioteca.controllers.LibroController;
import org.biblioteca.models.Autor;
import org.biblioteca.models.Categoria;
import org.biblioteca.models.Editorial;
import org.biblioteca.models.Libro;
import org.biblioteca.models.enums.EstadoFisico;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LibroView extends JFrame {

    private final LibroController controller;
    private JTable tablaLibros;
    private DefaultTableModel tableModel;
    private JTextField txtBusqueda;
    private JLabel lblEstadisticas;

    // Colores del Tema
    private final Color COLOR_PRIMARY = new Color(41, 128, 185);
    private final Color COLOR_SECONDARY = new Color(52, 152, 219);
    private final Color COLOR_BG = new Color(245, 246, 250);
    private final Color COLOR_SUCCESS = new Color(46, 204, 113);
    private final Color COLOR_DANGER = new Color(231, 76, 60);

    public LibroView() {
        this.controller = new LibroController();
        initUI();
        cargarDatos();
    }

    private void initUI() {
        setTitle("Sistema de Gestión Bibliotecaria - Libros");
        setSize(1280, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);

        // --- HEADER ---
        mainPanel.add(crearPanelSuperior(), BorderLayout.NORTH);

        // --- TABLA ---
        mainPanel.add(crearPanelTabla(), BorderLayout.CENTER);

        // --- FOOTER (BOTONES) ---
        mainPanel.add(crearPanelInferior(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARY);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("📚 Catálogo de Libros");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        txtBusqueda = new JTextField(20);
        txtBusqueda.putClientProperty("JTextField.placeholderText", "Buscar por título...");
        txtBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) buscarLibros();
            }
        });

        JButton btnBuscar = new JButton("🔍 Buscar");
        estilizarBoton(btnBuscar, COLOR_SECONDARY);
        btnBuscar.addActionListener(e -> buscarLibros());

        JButton btnRecargar = new JButton("🔄");
        estilizarBoton(btnRecargar, new Color(39, 174, 96));
        btnRecargar.addActionListener(e -> cargarDatos());

        searchPanel.add(txtBusqueda);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnRecargar);

        panel.add(title, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(COLOR_BG);

        String[] columnas = {"ID", "ISBN", "Título", "Autor(es)", "Editorial", "Categoría", "Stock", "Precio", "Estado"};

        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 6) return Integer.class;
                return String.class;
            }
        };

        tablaLibros = new JTable(tableModel);
        tablaLibros.setRowHeight(30);
        tablaLibros.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibros.setShowGrid(false);
        tablaLibros.setIntercellSpacing(new Dimension(0, 0));

        // Estilo del Header
        JTableHeader header = tablaLibros.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARY));

        // Renderizado condicional (Colores)
        tablaLibros.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                // Columna Stock (Index 6)
                if (column == 6) {
                    setHorizontalAlignment(CENTER);
                    String stockInfo = value.toString(); // Formato: "5/10"
                    if (stockInfo.startsWith("0/")) {
                        c.setForeground(COLOR_DANGER);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(39, 174, 96));
                    }
                } else {
                    setHorizontalAlignment(LEFT);
                }

                setBorder(new EmptyBorder(0, 5, 0, 5));
                return c;
            }
        });

        // Ajustar anchos de columna
        tablaLibros.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        tablaLibros.getColumnModel().getColumn(2).setPreferredWidth(200); // Titulo
        tablaLibros.getColumnModel().getColumn(3).setPreferredWidth(150); // Autores

        JScrollPane scroll = new JScrollPane(tablaLibros);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        lblEstadisticas = new JLabel("Cargando datos...");
        lblEstadisticas.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton btnNuevo = new JButton("➕ Nuevo Libro");
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnDetalles = new JButton("📄 Detalles");

        estilizarBoton(btnNuevo, COLOR_SUCCESS);
        estilizarBoton(btnEditar, COLOR_PRIMARY);
        estilizarBoton(btnEliminar, COLOR_DANGER);
        estilizarBoton(btnDetalles, Color.GRAY);

        btnNuevo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnDetalles.addActionListener(e -> verDetalles());

        btnPanel.add(btnNuevo);
        btnPanel.add(btnEditar);
        btnPanel.add(btnDetalles);
        btnPanel.add(btnEliminar);

        panel.add(lblEstadisticas, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    // ================= LÓGICA DE DATOS =================

    private void cargarDatos() {
        new SwingWorker<List<Libro>, Void>() {
            @Override
            protected List<Libro> doInBackground() {
                return controller.obtenerTodosLibros();
            }

            @Override
            protected void done() {
                try {
                    actualizarTabla(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LibroView.this, "Error al cargar datos: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void buscarLibros() {
        String texto = txtBusqueda.getText().trim();
        if (texto.isEmpty()) {
            cargarDatos();
            return;
        }

        new SwingWorker<List<Libro>, Void>() {
            @Override
            protected List<Libro> doInBackground() {
                // Intenta buscar por ISBN primero, si no hay resultados, busca por título
                var porIsbn = controller.buscarPorISBN(texto);
                if (porIsbn.isPresent()) return List.of(porIsbn.get());

                return controller.buscarPorTitulo(texto);
            }

            @Override
            protected void done() {
                try {
                    actualizarTabla(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void actualizarTabla(List<Libro> libros) {
        tableModel.setRowCount(0);
        for (Libro l : libros) {
            tableModel.addRow(new Object[]{
                    l.getIdLibro(),
                    l.getIsbn(),
                    l.getTitulo(),
                    l.getNombresAutores(), // Usamos el helper del modelo
                    l.getEditorial() != null ? l.getEditorial().getNombre() : "Sin Editorial",
                    l.getCategoria() != null ? l.getCategoria().getNombre() : "Sin Categoría",
                    l.getDisponibilidadTexto(),
                    l.getPrecio() != null ? String.format("$%.2f", l.getPrecio()) : "N/A",
                    l.getEstadoFisico()
            });
        }
        lblEstadisticas.setText(controller.obtenerEstadisticas());
    }

    private void editarSeleccionado() {
        int row = tablaLibros.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro para editar.");
            return;
        }
        int id = (int) tablaLibros.getValueAt(row, 0);
        controller.obtenerLibro(id).ifPresent(this::abrirFormulario);
    }

    private void eliminarSeleccionado() {
        int row = tablaLibros.getSelectedRow();
        if (row == -1) return;

        int id = (int) tablaLibros.getValueAt(row, 0);
        String titulo = (String) tablaLibros.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar '" + titulo + "'?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.eliminarLibro(id)) {
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar. Verifique que no tenga préstamos activos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verDetalles() {
        int row = tablaLibros.getSelectedRow();
        if (row == -1) return;
        int id = (int) tablaLibros.getValueAt(row, 0);

        controller.obtenerLibro(id).ifPresent(libro -> {
            StringBuilder info = new StringBuilder();
            info.append("Titulo: ").append(libro.getTitulo()).append("\n");
            info.append("ISBN: ").append(libro.getIsbn()).append("\n");
            info.append("Autores: ").append(libro.getNombresAutores()).append("\n");
            info.append("Descripción: ").append(libro.getDescripcion()).append("\n");
            info.append("Ubicación: ").append(libro.getUbicacion()).append("\n");

            JOptionPane.showMessageDialog(this, new JTextArea(info.toString()) {{
                setEditable(false);
                setBackground(new Color(240,240,240));
            }}, "Detalles del Libro", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void abrirFormulario(Libro libro) {
        LibroDialog dialog = new LibroDialog(this, libro);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            cargarDatos();
        }
    }

    private void estilizarBoton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(new CompoundBorder(
                new LineBorder(color.darker(), 1),
                new EmptyBorder(8, 15, 8, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(color); }
        });
    }

    // ================= CLASE INTERNA: FORMULARIO =================

    private class LibroDialog extends JDialog {
        private boolean guardado = false;
        private final Libro libroActual;

        // Componentes
        private JTextField txtTitulo, txtIsbn, txtPaginas, txtAnio, txtCopiasTotal, txtCopiasDisp, txtUbicacion, txtPrecio;
        private JTextArea txtDescripcion;
        private JComboBox<Editorial> cmbEditorial;
        private JComboBox<Categoria> cmbCategoria;
        private JComboBox<String> cmbIdioma;
        private JComboBox<EstadoFisico> cmbEstado;

        // Componente especial para Autores (Lista de selección múltiple)
        private JList<Autor> listAutores;

        public LibroDialog(JFrame parent, Libro libro) {
            super(parent, libro == null ? "Nuevo Libro" : "Editar Libro: " + libro.getTitulo(), true);
            this.libroActual = libro == null ? new Libro() : libro;
            initDialogUI();
            cargarCombosYDatos();
        }

        private void initDialogUI() {
            setSize(800, 600);
            setLocationRelativeTo(getParent());
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            formPanel.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            // Inicializar componentes
            txtTitulo = new JTextField();
            txtIsbn = new JTextField();
            txtPaginas = new JTextField();
            txtAnio = new JTextField();
            txtCopiasTotal = new JTextField();
            txtCopiasDisp = new JTextField();
            txtUbicacion = new JTextField();
            txtPrecio = new JTextField();
            txtDescripcion = new JTextArea(3, 20);
            txtDescripcion.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            cmbEditorial = new JComboBox<>();
            cmbCategoria = new JComboBox<>();
            cmbEstado = new JComboBox<>(EstadoFisico.values());
            cmbIdioma = new JComboBox<>(new String[]{"Español", "Inglés", "Francés", "Alemán", "Portugués"});

            // Configurar JList de Autores
            listAutores = new JList<>();
            listAutores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            listAutores.setVisibleRowCount(4);
            JScrollPane scrollAutores = new JScrollPane(listAutores);

            // Construcción del Formulario (2 columnas)
            agregarCampo(formPanel, gbc, 0, 0, "Título:", txtTitulo, 3); // Span 3

            agregarCampo(formPanel, gbc, 0, 1, "ISBN:", txtIsbn, 1);
            agregarCampo(formPanel, gbc, 2, 1, "Precio:", txtPrecio, 1);

            agregarCampo(formPanel, gbc, 0, 2, "Editorial:", cmbEditorial, 1);
            agregarCampo(formPanel, gbc, 2, 2, "Categoría:", cmbCategoria, 1);

            agregarCampo(formPanel, gbc, 0, 3, "Año:", txtAnio, 1);
            agregarCampo(formPanel, gbc, 2, 3, "Páginas:", txtPaginas, 1);

            agregarCampo(formPanel, gbc, 0, 4, "Idioma:", cmbIdioma, 1);
            agregarCampo(formPanel, gbc, 2, 4, "Estado:", cmbEstado, 1);

            agregarCampo(formPanel, gbc, 0, 5, "Total Copias:", txtCopiasTotal, 1);
            agregarCampo(formPanel, gbc, 2, 5, "Disponibles:", txtCopiasDisp, 1);

            agregarCampo(formPanel, gbc, 0, 6, "Ubicación:", txtUbicacion, 3);

            // Autores
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 1;
            formPanel.add(new JLabel("Autores (Ctrl+Click):"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.ipady = 40;
            formPanel.add(scrollAutores, gbc);
            gbc.ipady = 0;

            // Descripción
            gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1;
            formPanel.add(new JLabel("Descripción:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.ipady = 40;
            formPanel.add(new JScrollPane(txtDescripcion), gbc);

            // Botones
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnGuardar = new JButton("💾 Guardar");
            JButton btnCancelar = new JButton("❌ Cancelar");

            estilizarBoton(btnGuardar, COLOR_SUCCESS);
            estilizarBoton(btnCancelar, COLOR_DANGER);

            btnGuardar.addActionListener(e -> guardar());
            btnCancelar.addActionListener(e -> dispose());

            btnPanel.add(btnGuardar);
            btnPanel.add(btnCancelar);

            add(new JScrollPane(formPanel), BorderLayout.CENTER);
            add(btnPanel, BorderLayout.SOUTH);
        }

        private void agregarCampo(JPanel panel, GridBagConstraints gbc, int x, int y, String label, JComponent comp, int width) {
            gbc.gridx = x; gbc.gridy = y;
            gbc.gridwidth = 1;
            gbc.weightx = 0.0;
            panel.add(new JLabel(label), gbc);

            gbc.gridx = x + 1;
            gbc.gridwidth = width;
            gbc.weightx = 1.0;
            panel.add(comp, gbc);
        }

        private void cargarCombosYDatos() {
            // Cargar datos auxiliares desde el Controller
            DefaultComboBoxModel<Editorial> modelEd = new DefaultComboBoxModel<>();
            controller.obtenerEditoriales().forEach(modelEd::addElement);
            cmbEditorial.setModel(modelEd);

            DefaultComboBoxModel<Categoria> modelCat = new DefaultComboBoxModel<>();
            controller.obtenerCategorias().forEach(modelCat::addElement);
            cmbCategoria.setModel(modelCat);

            DefaultListModel<Autor> modelAutor = new DefaultListModel<>();
            controller.obtenerTodosAutores().forEach(modelAutor::addElement);
            listAutores.setModel(modelAutor);

            // Si estamos editando, llenar los campos
            if (libroActual.getIdLibro() > 0) {
                txtTitulo.setText(libroActual.getTitulo());
                txtIsbn.setText(libroActual.getIsbn());
                txtPaginas.setText(String.valueOf(libroActual.getNumPaginas()));
                txtAnio.setText(String.valueOf(libroActual.getAñoPublicacion()));
                txtCopiasTotal.setText(String.valueOf(libroActual.getCopiasTotales()));
                txtCopiasDisp.setText(String.valueOf(libroActual.getCopiasDisponibles()));
                txtUbicacion.setText(libroActual.getUbicacion());
                txtPrecio.setText(libroActual.getPrecio() != null ? libroActual.getPrecio().toString() : "");
                txtDescripcion.setText(libroActual.getDescripcion());

                cmbIdioma.setSelectedItem(libroActual.getIdioma());
                cmbEstado.setSelectedItem(libroActual.getEstadoFisico());

                // Seleccionar Objetos en Combos (Gracias a equals() en los modelos)
                if (libroActual.getEditorial() != null) cmbEditorial.setSelectedItem(libroActual.getEditorial());
                if (libroActual.getCategoria() != null) cmbCategoria.setSelectedItem(libroActual.getCategoria());

                // Seleccionar Autores en la lista
                if (libroActual.getAutores() != null) {
                    int[] indices = new int[libroActual.getAutores().size()];
                    for (int i = 0; i < libroActual.getAutores().size(); i++) {
                        Autor autorLibro = libroActual.getAutores().get(i);
                        // Buscar índice en el modelo de la lista
                        for (int j = 0; j < modelAutor.size(); j++) {
                            if (modelAutor.get(j).getIdAutor() == autorLibro.getIdAutor()) {
                                indices[i] = j;
                                break;
                            }
                        }
                    }
                    listAutores.setSelectedIndices(indices);
                }
            }
        }

        private void guardar() {
            try {
                // Mapeo de datos (Vista -> Modelo)
                libroActual.setTitulo(txtTitulo.getText());
                libroActual.setIsbn(txtIsbn.getText());
                libroActual.setNumPaginas(Integer.parseInt(txtPaginas.getText()));
                libroActual.setAñoPublicacion(Integer.parseInt(txtAnio.getText()));
                libroActual.setCopiasTotales(Integer.parseInt(txtCopiasTotal.getText()));
                libroActual.setCopiasDisponibles(Integer.parseInt(txtCopiasDisp.getText()));
                libroActual.setUbicacion(txtUbicacion.getText());
                libroActual.setDescripcion(txtDescripcion.getText());

                if (!txtPrecio.getText().isEmpty())
                    libroActual.setPrecio(new BigDecimal(txtPrecio.getText()));

                libroActual.setEditorial((Editorial) cmbEditorial.getSelectedItem());
                libroActual.setCategoria((Categoria) cmbCategoria.getSelectedItem());
                libroActual.setIdioma((String) cmbIdioma.getSelectedItem());
                libroActual.setEstadoFisico((EstadoFisico) cmbEstado.getSelectedItem());

                // Obtener autores seleccionados
                libroActual.setAutores(listAutores.getSelectedValuesList());

                boolean exito;
                if (libroActual.getIdLibro() == 0) {
                    exito = controller.crearLibro(libroActual);
                } else {
                    exito = controller.actualizarLibro(libroActual);
                }

                if (exito) {
                    guardado = true;
                    dispose();
                    JOptionPane.showMessageDialog(this, "Libro guardado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor verifique los campos numéricos.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isGuardado() { return guardado; }
    }
}