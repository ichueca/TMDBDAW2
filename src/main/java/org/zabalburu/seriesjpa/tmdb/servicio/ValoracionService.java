/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.servicio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.List;

import org.zabalburu.seriesjpa.tmdb.dao.SerieDAO;
import org.zabalburu.seriesjpa.tmdb.dao.UsuarioDAO;
import org.zabalburu.seriesjpa.tmdb.dao.ValoracionDAO;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;
import org.zabalburu.seriesjpa.tmdb.modelo.Valoracion;


public class ValoracionService {

    private final EntityManagerFactory emf;
    private final UsuarioDAO usuarioDAO;
    private final SerieDAO serieDAO;
    private final ValoracionDAO valoracionDAO;

    public ValoracionService(EntityManagerFactory emf, 
            UsuarioDAO usuarioDAO, 
            SerieDAO serieDAO, 
            ValoracionDAO valoracionDAO) {
        this.emf = emf;
        this.usuarioDAO = usuarioDAO;
        this.serieDAO = serieDAO;
        this.valoracionDAO = valoracionDAO;
    }

   public void anadirValoracion(
           Long usuarioId, 
           Long serieId, 
           int estrellas, 
           String comentario) {
        if (estrellas < 1 || estrellas > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Usuario usuario = usuarioDAO.findById(em, usuarioId);
            Serie serie = serieDAO.findById(em, serieId);

            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }
            if (serie == null) {
                throw new IllegalArgumentException("Serie no encontrada");
            }

            Valoracion existente = valoracionDAO.findByUsuarioAndSerie(em, usuarioId, serieId);
            if (existente != null){
                valoracionDAO.delete(em, existente);
                usuario.removeValoracion(existente);
                serie.removeValoracion(existente);
            }
            Valoracion nueva = new Valoracion(usuario, serie, estrellas, comentario, LocalDate.now());
            valoracionDAO.insert(em, nueva);
            usuario.addValoracion(nueva);
            serie.addValoracion(nueva);
            em.getTransaction().commit();

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void borrarValoracion(Long valoracionId) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Valoracion valoracion = valoracionDAO.findById(em, valoracionId);
            if (valoracion == null) {
                //throw new IllegalArgumentException("Valoración no encontrada");
                return;
            }

            valoracion.getUsuario().removeValoracion(valoracion);
            valoracion.getSerie().removeValoracion(valoracion);

            valoracionDAO.delete(em, valoracion);

            em.getTransaction().commit();

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Valoracion> getValoracionesSerie(Long serieId) {
        EntityManager em = emf.createEntityManager();
        try {
            return valoracionDAO.findBySerieId(em, serieId);
        } finally {
            em.close();
        }  
    }

    public List<Valoracion> getValoracionesUsuario(Long usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            return valoracionDAO.findByUsuarioId(em, usuarioId);
        } finally {
            em.close();
        }
    }
}

