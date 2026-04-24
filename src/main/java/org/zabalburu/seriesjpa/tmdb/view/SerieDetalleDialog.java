/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.view;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.zabalburu.seriesjpa.tmdb.modelo.Genero;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;
import org.zabalburu.seriesjpa.tmdb.servicio.ValoracionService;

public class SerieDetalleDialog extends JDialog {

    private final Usuario usuario;
    private final Serie serie;
    private final ValoracionService valoracionService;

    public SerieDetalleDialog(JFrame owner,
                              Usuario usuario,
                              Serie serie,
                              ValoracionService valoracionService) {
        super(owner, "Detalle de serie", true);
        this.usuario = usuario;
        this.serie = serie;
        this.valoracionService = valoracionService;

        setSize(800, 550);
        setLocationRelativeTo(owner);

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JLabel lblTitulo = new JLabel(serie.getTitulo());
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        root.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(20, 20));
        root.add(panelCentro, BorderLayout.CENTER);

        panelCentro.add(crearPanelPoster(), BorderLayout.WEST);
        panelCentro.add(crearPanelInformacion(), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);

        JButton btnValorar = new JButton("Valorar");
        btnValorar.addActionListener(e -> abrirDialogoValorar());


        panelBotones.add(btnValorar);
        panelBotones.add(btnCerrar);

        root.add(panelBotones, BorderLayout.SOUTH);
        
        root.add(panelBotones, BorderLayout.SOUTH);
    }

    private void abrirDialogoValorar() {
        ValorarSerieDialog dialog = new ValorarSerieDialog(
                (JFrame) getOwner(),
                usuario,
                serie,
                valoracionService
        );
        dialog.setVisible(true);
        
    }
    
    private JPanel crearPanelPoster() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(240, 360));

        JLabel lblPoster = new JLabel("Cargando imagen...", SwingConstants.CENTER);
        lblPoster.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        lblPoster.setPreferredSize(new Dimension(220, 330));

        cargarPoster(lblPoster);

        panel.add(lblPoster, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblFecha = new JLabel("Fecha primer aire: " + formatearFecha());
        lblFecha.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JLabel lblGeneros = new JLabel("Géneros: " + formatearGeneros());
        lblGeneros.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JLabel lblSinopsisTitulo = new JLabel("Sinopsis");
        lblSinopsisTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        JTextArea txtSinopsis = new JTextArea(serie.getSinopsis() != null ? serie.getSinopsis() : "");
        txtSinopsis.setLineWrap(true);
        txtSinopsis.setWrapStyleWord(true);
        txtSinopsis.setEditable(false);
        txtSinopsis.setOpaque(false);
        txtSinopsis.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JScrollPane scrollSinopsis = new JScrollPane(txtSinopsis);
        scrollSinopsis.setBorder(null);

        panel.add(lblFecha);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblGeneros);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblSinopsisTitulo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scrollSinopsis);

        return panel;
    }

    private void cargarPoster(JLabel lblPoster) {
        if (serie.getPosterUrl() == null || serie.getPosterUrl().isBlank()) {
            lblPoster.setText("Sin imagen");
            return;
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(new URI(serie.getPosterUrl()).toURL());

            if (bufferedImage == null) {
                lblPoster.setText("No se pudo cargar la imagen");
                return;
            }

            Image imageEscalada = bufferedImage.getScaledInstance(220, 330, Image.SCALE_SMOOTH);
            lblPoster.setText("");
            lblPoster.setIcon(new ImageIcon(imageEscalada));

        } catch (Exception e) {
            lblPoster.setText("No se pudo cargar la imagen");
        }
    }

    private String formatearFecha() {
        if (serie.getFechaPrimerAire() == null) {
            return "-";
        }
        return serie.getFechaPrimerAire().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatearGeneros() {
        if (serie.getGeneros().isEmpty()) {
            return "-";
        }

        return serie.getGeneros().stream()
                .map(Genero::getNombre)
                .sorted()
                .collect(Collectors.joining(", "));
    }
} 

