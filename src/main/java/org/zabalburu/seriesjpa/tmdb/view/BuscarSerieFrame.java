/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.view;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.zabalburu.seriesjpa.tmdb.dto.TmdbSerieDTO;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;
import org.zabalburu.seriesjpa.tmdb.servicio.SerieImportService;

public class BuscarSerieFrame extends JFrame {

    private final SerieImportService serieImportService;

    private JTextField txtBusqueda;
    private JButton btnBuscar;
    private JButton btnImportar;
    private JTable tblResultados;
    private DefaultTableModel tableModel;

    private List<TmdbSerieDTO> resultados = new ArrayList<>();

    public BuscarSerieFrame(SerieImportService serieImportService) {
        this.serieImportService = serieImportService;

        setTitle("Buscar series en TMDb");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        root.add(crearPanelBusqueda(), BorderLayout.NORTH);
        root.add(crearPanelTabla(), BorderLayout.CENTER);
        root.add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel lblBusqueda = new JLabel("Buscar serie:");
        txtBusqueda = new JTextField();
        btnBuscar = new JButton("Buscar");

        btnBuscar.addActionListener(e -> buscarSeries());

        panel.add(lblBusqueda, BorderLayout.WEST);
        panel.add(txtBusqueda, BorderLayout.CENTER);
        panel.add(btnBuscar, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane crearPanelTabla() {
        tableModel = new DefaultTableModel(
                new String[]{"TMDb ID", "Título", "Fecha primer aire", "Sinopsis"},
                0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Integer.class;
                    case 1 -> String.class;
                    case 2 -> String.class;
                    case 3 -> String.class;
                    default -> Object.class;
                };
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblResultados = new JTable(tableModel);
        tblResultados.setRowHeight(28);
        tblResultados.getTableHeader().setReorderingAllowed(false);
        tblResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(tblResultados);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnImportar = new JButton("Importar serie");
        btnImportar.addActionListener(e -> importarSerieSeleccionada());

        panel.add(btnImportar);

        return panel;
    }

    private void buscarSeries() {
        String texto = txtBusqueda.getText().trim();

        if (texto.isBlank()) {
            JOptionPane.showMessageDialog(this, "Introduce un texto para buscar.");
            return;
        }

        try {
            resultados = serieImportService.buscarSeries(texto);
            cargarTabla();

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se han encontrado resultados.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar series en TMDb:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        tableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (TmdbSerieDTO dto : resultados) {
            String fecha = dto.fechaPrimerAire() != null
                    ? dto.fechaPrimerAire().format(formatter)
                    : "";

            tableModel.addRow(new Object[]{
                    dto.tmdbId(),
                    dto.titulo(),
                    fecha,
                    dto.sinopsis()
            });
        }
    }

    private void importarSerieSeleccionada() {
        int filaSeleccionada = tblResultados.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar una serie.");
            return;
        }

        int filaModelo = tblResultados.convertRowIndexToModel(filaSeleccionada);
        TmdbSerieDTO dto = resultados.get(filaModelo);

        try {
            Serie serie = serieImportService.importarSerie(dto.tmdbId());

            JOptionPane.showMessageDialog(this,
                    "Serie importada correctamente:\n" + serie.getTitulo());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al importar la serie:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
