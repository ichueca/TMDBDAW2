/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

import org.zabalburu.seriesjpa.tmdb.modelo.Valoracion;


public class ValoracionDAO {

    public void insert(EntityManager em, Valoracion valoracion) {
        em.persist(valoracion);
    }

    public Valoracion findById(EntityManager em, Long id) {
        return em.find(Valoracion.class, id);
    }

    public void delete(EntityManager em, Valoracion valoracion) {
        em.remove(valoracion);
    }

    public List<Valoracion> findBySerieId(EntityManager em, Long serieId) {
        return em.createQuery("""
                SELECT v
                FROM Valoracion v
                WHERE v.serie.id = :serieId
                ORDER BY v.fecha DESC
                """, Valoracion.class)
                .setParameter("serieId", serieId)
                .getResultList();
    }

    public List<Valoracion> findByUsuarioId(EntityManager em, Long usuarioId) {
        return em.createQuery("""
                SELECT v
                FROM Valoracion v
                WHERE v.usuario.id = :usuarioId
                ORDER BY v.fecha DESC
                """, Valoracion.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public Valoracion findByUsuarioAndSerie(EntityManager em, Long usuarioId, Long serieId) {
        try {
            return em.createQuery("""
                    SELECT v
                    FROM Valoracion v
                    WHERE v.usuario.id = :usuarioId
                      AND v.serie.id = :serieId
                    """, Valoracion.class)
                    .setParameter("usuarioId", usuarioId)
                    .setParameter("serieId", serieId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}

