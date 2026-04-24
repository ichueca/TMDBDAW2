/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.servicio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


import java.util.List;
import org.zabalburu.seriesjpa.tmdb.dao.GeneroDAO;
import org.zabalburu.seriesjpa.tmdb.dao.SerieDAO;
import org.zabalburu.seriesjpa.tmdb.dao.TmdbClient;
import org.zabalburu.seriesjpa.tmdb.dto.TmdbSerieDTO;
import org.zabalburu.seriesjpa.tmdb.modelo.Genero;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;

public class SerieImportService {

    private final EntityManagerFactory emf;
    private final SerieDAO serieDAO;
    private final GeneroDAO generoDAO;
    private final TmdbClient tmdbClient;

    public SerieImportService(EntityManagerFactory emf, 
            SerieDAO serieDAO, 
            GeneroDAO generoDAO, 
            TmdbClient tmdbClient) {
        this.emf = emf;
        this.serieDAO = serieDAO;
        this.generoDAO = generoDAO;
        this.tmdbClient = tmdbClient;
    }

    public List<TmdbSerieDTO> buscarSeries(String texto) {
        return tmdbClient.buscarSeries(texto);
    }

    public Serie importarSerie(Integer tmdbId) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Serie existente = serieDAO.findByTmdbIdConGeneros(em, tmdbId);
            if (existente != null) {
                em.getTransaction().commit();
                return existente;
            }

            TmdbSerieDTO dto = tmdbClient.obtenerDetalleSerie(tmdbId);

            Serie serie = new Serie(dto.tmdbId(), dto.titulo());
            serie.setSinopsis(dto.sinopsis());
            serie.setFechaPrimerAire(dto.fechaPrimerAire());
            serie.setPosterUrl(dto.posterUrl());

            for (String nombreGenero : dto.generos()) {
                Genero genero = generoDAO.findByNombre(em, nombreGenero);
                if (genero == null) {
                    genero = new Genero(nombreGenero);
                    generoDAO.insert(em, genero);
                }
                serie.addGenero(genero);
            }

            serieDAO.insert(em, serie);

            em.getTransaction().commit();
            return serie;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}

