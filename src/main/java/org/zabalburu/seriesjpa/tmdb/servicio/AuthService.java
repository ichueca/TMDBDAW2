/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.servicio;
    
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.zabalburu.seriesjpa.tmdb.dao.UsuarioDAO;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;
import org.zabalburu.seriesjpa.tmdb.util.PasswordUtils;


public class AuthService {

    private final EntityManagerFactory emf;
    private final UsuarioDAO usuarioDAO;

    public AuthService(EntityManagerFactory emf, UsuarioDAO usuarioDAO) {
        this.emf = emf;
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario login(String nombreUsuario, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            Usuario usuario = usuarioDAO.findByNombreUsuarioConValoraciones(em, nombreUsuario);

            if (usuario == null) {
                return null;
            }

            if (!PasswordUtils.checkPw(password, usuario.getPassword())) {
                return null;
            }

            return usuario;
        } finally {
            em.close();
        }
    }
}

