package org.zabalburu.seriesjpa.tmdb.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

import org.zabalburu.seriesjpa.tmdb.modelo.Serie;



public class SerieDAO {

    public void insert(EntityManager em, Serie serie) {
        em.persist(serie);
    }

    public Serie findById(EntityManager em, Long id) {
        return em.find(Serie.class, id);
    }

    public Serie findByIdConGenerosYValoraciones(EntityManager em, Long id) {
        try {
            return em.createQuery("""
                SELECT DISTINCT s
                FROM Serie s
                   LEFT JOIN FETCH s.generos
                   LEFT JOIN FETCH s.valoraciones 
                WHERE s.id = :id
                """, Serie.class)
                .setParameter("id", id)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Serie> findAllConGeneros(EntityManager em) {
        return em.createQuery("""
                SELECT DISTINCT s
                FROM Serie s
                LEFT JOIN FETCH s.generos
                ORDER BY s.titulo
                """, Serie.class)
                .getResultList();
    }

    public Serie findByTmdbIdConGeneros(EntityManager em, Integer tmdbId) {
        try {
            return em.createQuery("""
                    SELECT DISTINCT s
                    FROM Serie s
                    LEFT JOIN FETCH s.generos
                    WHERE s.tmdbId = :tmdbId
                    """, Serie.class)
                    .setParameter("tmdbId", tmdbId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Serie> findAll(EntityManager em) {
        return em.createQuery("""
                SELECT s
                FROM Serie s
                ORDER BY s.titulo
                """, Serie.class)
                .getResultList();
    }
}

