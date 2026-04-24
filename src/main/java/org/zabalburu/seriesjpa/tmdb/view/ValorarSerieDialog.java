/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.view;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;
import org.zabalburu.seriesjpa.tmdb.servicio.ValoracionService;

public class ValorarSerieDialog extends JDialog {

    private final Usuario usuario;
    private final Serie serie;
    private final ValoracionService valoracionService;
    //private final Runnable onValoracionGuardada;

    private JComboBox<Integer> cmbEstrellas;
    private JTextArea txtComentario;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public ValorarSerieDialog(JFrame owner,
                              Usuario usuario,
                              Serie serie,
                              ValoracionService valoracionService) {
        super(owner, "Valorar serie", true);
        this.usuario = usuario;
        this.serie = serie;
        this.valoracionService = valoracionService;
        //this.onValoracionGuardada = onValoracionGuardada;

        setSize(500, 350);
        setLocationRelativeTo(owner);

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JLabel lblTitulo = new JLabel("Valorar: " + serie.getTitulo());
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        root.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));

        JPanel panelEstrellas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblEstrellas = new JLabel("Estrellas:");
        cmbEstrellas = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        panelEstrellas.add(lblEstrellas);
        panelEstrellas.add(cmbEstrellas);

        JLabel lblComentario = new JLabel("Comentario:");
        txtComentario = new JTextArea(8, 30);
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);

        JScrollPane scrollComentario = new JScrollPane(txtComentario);

        panelCentro.add(panelEstrellas);
        panelCentro.add(Box.createVerticalStrut(10));
        panelCentro.add(lblComentario);
        panelCentro.add(Box.createVerticalStrut(5));
        panelCentro.add(scrollComentario);

        root.add(panelCentro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardarValoracion());
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        root.add(panelBotones, BorderLayout.SOUTH);
    }

    private void guardarValoracion() {
        try {
            int estrellas = (Integer) cmbEstrellas.getSelectedItem();
            String comentario = txtComentario.getText().trim();

            valoracionService.anadirValoracion(
                    usuario.getId(),
                    serie.getId(),
                    estrellas,
                    comentario
            );

            JOptionPane.showMessageDialog(this, "Valoración guardada correctamente.");

           /* if (onValoracionGuardada != null) {
                onValoracionGuardada.run();
            }*/

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar la valoración:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
