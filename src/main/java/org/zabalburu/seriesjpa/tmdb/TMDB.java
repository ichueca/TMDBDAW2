/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.zabalburu.seriesjpa.tmdb;

import com.formdev.flatlaf.FlatLightLaf;
import jakarta.persistence.EntityManagerFactory;
import javax.swing.SwingUtilities;
import org.zabalburu.seriesjpa.tmdb.dao.UsuarioDAO;
import org.zabalburu.seriesjpa.tmdb.servicio.AuthService;
import org.zabalburu.seriesjpa.tmdb.util.DataInitializer;
import org.zabalburu.seriesjpa.tmdb.util.JPAUtil;
import org.zabalburu.seriesjpa.tmdb.util.PasswordUtils;
import org.zabalburu.seriesjpa.tmdb.view.LoginFrame;


/**
 *
 * @author ichueca
 */
public class TMDB {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();
        String env = System.getProperty("app.env", "dev");
        if ("dev".equals(env)) {
            DataInitializer.initialize();
        }
        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService(
                    JPAUtil.getEntityManagerFactory(),
                    new UsuarioDAO()
            );

            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }
}
