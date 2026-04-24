/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.view;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.zabalburu.seriesjpa.tmdb.dao.GeneroDAO;
import org.zabalburu.seriesjpa.tmdb.dao.SerieDAO;
import org.zabalburu.seriesjpa.tmdb.dao.TmdbClient;
import org.zabalburu.seriesjpa.tmdb.dao.UsuarioDAO;
import org.zabalburu.seriesjpa.tmdb.dao.ValoracionDAO;
import org.zabalburu.seriesjpa.tmdb.modelo.Genero;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;
import org.zabalburu.seriesjpa.tmdb.modelo.Valoracion;
import org.zabalburu.seriesjpa.tmdb.servicio.SerieImportService;
import org.zabalburu.seriesjpa.tmdb.servicio.ValoracionService;

public class MainFrame extends JFrame {

    private final Usuario usuario;
    private final EntityManagerFactory emf;
    private final SerieDAO serieDAO = new SerieDAO();
    private final ValoracionService valoracionService;

    private JLabel lblBienvenida;
    private JLabel lblCorreo;

    private JLabel lblTotalValoraciones;
    private JLabel lblMediaValoraciones;
    private JLabel lblSerieDestacada;

    private JTable tblSeries;
    private DefaultTableModel modelSeries;

    private JTable tblValoraciones;
    private DefaultTableModel modelValoraciones;

    private JButton btnBuscarSeries;
    private JButton btnActualizar;
    private JButton btnCerrarSesion;
    private JButton btnVerDetalle;

    private List<Serie> seriesImportadas = new ArrayList<>();

    public MainFrame(Usuario usuario, EntityManagerFactory emf) {
        this.usuario = usuario;
        this.emf = emf;
        this.valoracionService = new ValoracionService(
                emf,
                new UsuarioDAO(),
                new SerieDAO(),
                new ValoracionDAO()
                
        );
        
        setTitle("SeriesJPA - Panel principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        initComponents();
        cargarResumen();
        cargarSeriesImportadas();
        cargarTablaValoraciones();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        root.add(crearCabecera(), BorderLayout.NORTH);
        root.add(crearCentro(), BorderLayout.CENTER);
    }

    private JPanel crearCabecera() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel panelTextos = new JPanel();
        panelTextos.setLayout(new BoxLayout(panelTextos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("SeriesJPA");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 30));

        lblBienvenida = new JLabel("Bienvenido, " + usuario.getNombreUsuario());
        lblBienvenida.setFont(new Font("SansSerif", Font.PLAIN, 20));

        lblCorreo = new JLabel(usuario.getEmail());
        lblCorreo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblCorreo.setForeground(new Color(110, 110, 110));

        panelTextos.add(lblTitulo);
        panelTextos.add(Box.createVerticalStrut(8));
        panelTextos.add(lblBienvenida);
        panelTextos.add(Box.createVerticalStrut(4));
        panelTextos.add(lblCorreo);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        btnBuscarSeries = new JButton("Buscar series");
        btnActualizar = new JButton("Actualizar");
        btnCerrarSesion = new JButton("Cerrar sesión");

        btnBuscarSeries.addActionListener(e -> abrirBuscadorSeries());
        btnActualizar.addActionListener(e -> refrescarPantalla());
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        panelBotones.add(btnBuscarSeries);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrarSesion);

        panel.add(panelTextos, BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearCentro() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));

        JPanel panelCards = new JPanel(new GridLayout(1, 3, 20, 0));
        panelCards.add(crearCardTotalValoraciones());
        panelCards.add(crearCardMediaValoraciones());
        panelCards.add(crearCardSerieDestacada());

        panel.add(panelCards, BorderLayout.NORTH);
        panel.add(crearPestanas(), BorderLayout.CENTER);

        return panel;
    }

    private JTabbedPane crearPestanas() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Series importadas", crearPanelSeries());
        tabs.addTab("Mis valoraciones", crearPanelValoraciones());
        return tabs;
    }

    private JPanel crearPanelSeries() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblTitulo = new JLabel("Series disponibles en la base de datos");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        modelSeries = new DefaultTableModel(
                new String[]{"TMDb ID", "Título", "Fecha", "Géneros"},
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

        tblSeries = new JTable(modelSeries);
        tblSeries.setRowHeight(28);
        tblSeries.getTableHeader().setReorderingAllowed(false);
        tblSeries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSeries.setAutoCreateRowSorter(true);

        tblSeries.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDetalleSerieSeleccionada();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblSeries);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVerDetalle = new JButton("Ver detalle");
        btnVerDetalle.addActionListener(e -> abrirDetalleSerieSeleccionada());
        panelBotones.add(btnVerDetalle);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelValoraciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel lblTabla = new JLabel("Mis valoraciones");
        lblTabla.setFont(new Font("SansSerif", Font.BOLD, 20));

        modelValoraciones = new DefaultTableModel(
                new String[]{"Serie", "Estrellas", "Comentario", "Fecha"},
                0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> String.class;
                    case 1 -> Integer.class;
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

        tblValoraciones = new JTable(modelValoraciones);
        tblValoraciones.setRowHeight(28);
        tblValoraciones.getTableHeader().setReorderingAllowed(false);
        tblValoraciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblValoraciones.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(tblValoraciones);

        panel.add(lblTabla, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearCardTotalValoraciones() {
        JPanel card = crearCardBase(new Color(52, 152, 219));

        JLabel lblTitulo = new JLabel("Mis valoraciones");
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblTitulo.setForeground(Color.WHITE);

        lblTotalValoraciones = new JLabel("0");
        lblTotalValoraciones.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTotalValoraciones.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel("Total registradas");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblInfo.setForeground(Color.WHITE);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));
        card.add(lblTotalValoraciones);
        card.add(Box.createVerticalStrut(8));
        card.add(lblInfo);

        return card;
    }

    private JPanel crearCardMediaValoraciones() {
        JPanel card = crearCardBase(new Color(46, 204, 113));

        JLabel lblTitulo = new JLabel("Puntuación media");
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblTitulo.setForeground(Color.WHITE);

        lblMediaValoraciones = new JLabel("0.00");
        lblMediaValoraciones.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblMediaValoraciones.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel("Media de estrellas");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblInfo.setForeground(Color.WHITE);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));
        card.add(lblMediaValoraciones);
        card.add(Box.createVerticalStrut(8));
        card.add(lblInfo);

        return card;
    }

    private JPanel crearCardSerieDestacada() {
        JPanel card = crearCardBase(new Color(155, 89, 182));

        JLabel lblTitulo = new JLabel("Serie destacada");
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblTitulo.setForeground(Color.WHITE);

        lblSerieDestacada = new JLabel("-");
        lblSerieDestacada.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblSerieDestacada.setForeground(Color.WHITE);

        JLabel lblInfo = new JLabel("Mejor valorada por ti");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblInfo.setForeground(Color.WHITE);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(10));
        card.add(lblSerieDestacada);
        card.add(Box.createVerticalStrut(8));
        card.add(lblInfo);

        return card;
    }

    private JPanel crearCardBase(Color colorFondo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(colorFondo);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        return card;
    }

    private void cargarResumen() {
        List<Valoracion> valoraciones = usuario.getValoraciones();

        int total = valoraciones.size();
        double media = calcularMedia(valoraciones);
        String serieDestacada = calcularSerieDestacada(valoraciones);

        lblTotalValoraciones.setText(String.valueOf(total));
        lblMediaValoraciones.setText(String.format("%.2f", media));
        lblSerieDestacada.setText(serieDestacada);
    }

    private void cargarSeriesImportadas() {
        EntityManager em = emf.createEntityManager();

        try {
            seriesImportadas = serieDAO.findAllConGeneros(em);
        } finally {
            em.close();
        }

        modelSeries.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Serie serie : seriesImportadas) {
            String fecha = serie.getFechaPrimerAire() != null
                    ? serie.getFechaPrimerAire().format(formatter)
                    : "";

            String generos = serie.getGeneros().stream()
                    .map(Genero::getNombre)
                    .sorted()
                    .collect(Collectors.joining(", "));

            modelSeries.addRow(new Object[]{
                    serie.getTmdbId(),
                    serie.getTitulo(),
                    fecha,
                    generos
            });
        }
    }

    private void cargarTablaValoraciones() {
        modelValoraciones.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        usuario.getValoraciones().stream()
                .sorted(Comparator.comparing(Valoracion::getFecha).reversed())
                .forEach(v -> modelValoraciones.addRow(new Object[]{
                        v.getSerie().getTitulo(),
                        v.getEstrellas(),
                        v.getComentario(),
                        v.getFecha().format(formatter)
                }));
    }

    private double calcularMedia(List<Valoracion> valoraciones) {
        if (valoraciones.isEmpty()) {
            return 0.0;
        }

        return valoraciones.stream()
                .mapToInt(Valoracion::getEstrellas)
                .average()
                .orElse(0.0);
    }

    private String calcularSerieDestacada(List<Valoracion> valoraciones) {
        return valoraciones.stream()
                .max(Comparator.comparingInt(Valoracion::getEstrellas))
                .map(v -> v.getSerie().getTitulo())
                .orElse("-");
    }

    private void abrirBuscadorSeries() {
        BuscarSerieFrame frame = new BuscarSerieFrame(
                new SerieImportService(
                        emf,
                        new SerieDAO(),
                        new GeneroDAO(),
                        new TmdbClient()
                )
        );

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cargarSeriesImportadas();
            }
        });

        frame.setVisible(true);
    }

    private void abrirDetalleSerieSeleccionada() {
        int filaSeleccionada = tblSeries.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar una serie.");
            return;
        }

        int filaModelo = tblSeries.convertRowIndexToModel(filaSeleccionada);
        Serie serie = seriesImportadas.get(filaModelo);

        SerieDetalleDialog dialog = new SerieDetalleDialog(this, 
                usuario,
                serie,
                valoracionService);
        dialog.setVisible(true);
        cargarResumen();
        cargarSeriesImportadas();
        cargarTablaValoraciones();
    }

    private void refrescarPantalla() {
        cargarResumen();
        cargarSeriesImportadas();
        cargarTablaValoraciones();
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
    
    
}
