/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;
import org.zabalburu.seriesjpa.tmdb.servicio.AuthService;
import org.zabalburu.seriesjpa.tmdb.util.JPAUtil;


public class LoginFrame extends JFrame {

    private final AuthService authService;

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblMensaje;

    public LoginFrame(AuthService authService) {
        this.authService = authService;

        setTitle("SeriesJPA - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("SeriesJPA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel lblSubtitulo = new JLabel("Iniciar sesión", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel panelTitulo = new JPanel(new GridLayout(2, 1));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblSubtitulo);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsuario = new JLabel("Usuario:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(lblUsuario, gbc);

        txtUsuario = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        int alto = txtUsuario.getPreferredSize().height;
        txtUsuario.setPreferredSize(new Dimension(200,alto));
        txtUsuario.setMinimumSize(new Dimension(200,alto));
        panelFormulario.add(txtUsuario, gbc);

        JLabel lblPassword = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(txtPassword, gbc);

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelFormulario.add(lblMensaje, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnLogin = new JButton("Entrar");
        panelBotones.add(btnLogin);

        add(panelTitulo, BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> realizarLogin());
        txtPassword.addActionListener(e -> realizarLogin());
        //this.getRootPane().setDefaultButton(btnLogin);
    }

    private void realizarLogin() {
        String nombreUsuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (nombreUsuario.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Debes introducir usuario y contraseña");
            return;
        }

        Usuario usuario = authService.login(nombreUsuario, password);

        if (usuario == null) {
            lblMensaje.setText("Usuario o contraseña incorrectos");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(usuario, JPAUtil.getEntityManagerFactory());
            mainFrame.setVisible(true);
        
        });

        dispose();
    }
}
