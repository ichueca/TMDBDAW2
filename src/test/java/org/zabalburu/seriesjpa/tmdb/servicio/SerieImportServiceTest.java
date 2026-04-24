/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.servicio;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import org.zabalburu.seriesjpa.tmdb.dao.GeneroDAO;
import org.zabalburu.seriesjpa.tmdb.dao.SerieDAO;
import org.zabalburu.seriesjpa.tmdb.dao.TmdbClient;
import org.zabalburu.seriesjpa.tmdb.dto.TmdbSerieDTO;
import org.zabalburu.seriesjpa.tmdb.modelo.Genero;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;

public class SerieImportServiceTest {

    private static EntityManagerFactory emf;

    private EntityManager em;
    private SerieDAO serieDAO;
    private GeneroDAO generoDAO;
    private TmdbClient tmdbClientMock;
    private SerieImportService serieImportService;

    @BeforeAll
    public static void initEntityManagerFactory() {
        emf = Persistence.createEntityManagerFactory("seriesjpa_test");
    }

    @AfterAll
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        limpiarBaseDeDatos();

        em = emf.createEntityManager();
        serieDAO = new SerieDAO();
        generoDAO = new GeneroDAO();
        tmdbClientMock = mock(TmdbClient.class);

        serieImportService = new SerieImportService(
                emf,
                serieDAO,
                generoDAO,
                tmdbClientMock
        );
    }

    private void limpiarBaseDeDatos() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        em.createQuery("DELETE FROM Valoracion").executeUpdate();
        em.createQuery("DELETE FROM Serie").executeUpdate();
        em.createQuery("DELETE FROM Genero").executeUpdate();

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void buscarSeries_deberiaDelegarEnTmdbClient() {
        TmdbSerieDTO dto1 = new TmdbSerieDTO(100,"Dark","",null,"",null);
        TmdbSerieDTO dto2 = new TmdbSerieDTO(200,"Lost","",null,"",null);

        when(tmdbClientMock.buscarSeries("da")).thenReturn(List.of(dto1));

        List<TmdbSerieDTO> resultado = serieImportService.buscarSeries("da");

        Assertions.assertEquals(1, resultado.size());
        Assertions.assertEquals("Dark", resultado.get(0).titulo());
        verify(tmdbClientMock, times(1)).buscarSeries("da");
    }

    @Test
    public void importarSerie_deberiaCrearSerieYGenerosSiNoExiste() {
        TmdbSerieDTO dto = new TmdbSerieDTO(999,
                "Severance",
                "Una empresa separa los recuerdos laborales y personales.",
                LocalDate.of(2022, 2, 18),
                "https://image.tmdb.org/t/p/w500/poster.jpg",
                List.of("Drama", "Ciencia ficción"));

        when(tmdbClientMock.obtenerDetalleSerie(999)).thenReturn(dto);

        Serie serieImportada = serieImportService.importarSerie(999);

        Assertions.assertNotNull(serieImportada);
        Assertions.assertNotNull(serieImportada.getId());
        Assertions.assertEquals(999, serieImportada.getTmdbId());
        Assertions.assertEquals("Severance", serieImportada.getTitulo());
        Assertions.assertEquals(2, serieImportada.getGeneros().size());

        EntityManager emComprobacion = emf.createEntityManager();

        Serie serieBD = serieDAO.findByTmdbIdConGeneros(emComprobacion, 999);
        Assertions.assertNotNull(serieBD);
        Assertions.assertEquals("Severance", serieBD.getTitulo());

        Genero drama = generoDAO.findByNombre(emComprobacion, "Drama");
        Genero cienciaFiccion = generoDAO.findByNombre(emComprobacion, "Ciencia ficción");

        Assertions.assertNotNull(drama);
        Assertions.assertNotNull(cienciaFiccion);

        emComprobacion.close();

        verify(tmdbClientMock, times(1)).obtenerDetalleSerie(999);
    }

    @Test
    public void importarSerie_noDeberiaDuplicarSiYaExiste() {
        EntityManager emPreparacion = emf.createEntityManager();
        emPreparacion.getTransaction().begin();

        Serie serieExistente = new Serie(1234, "Dark");
        serieExistente.setSinopsis("Serie ya guardada en la base de datos.");
        emPreparacion.persist(serieExistente);

        emPreparacion.getTransaction().commit();
        emPreparacion.close();

        Serie resultado = serieImportService.importarSerie(1234);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Dark", resultado.getTitulo());

        EntityManager emComprobacion = emf.createEntityManager();

        Long totalSeries = emComprobacion.createQuery("""
                SELECT COUNT(s)
                FROM Serie s
                WHERE s.tmdbId = :tmdbId
                """, Long.class)
                .setParameter("tmdbId", 1234)
                .getSingleResult();

        emComprobacion.close();

        Assertions.assertEquals(1L, totalSeries);
        verify(tmdbClientMock, never()).obtenerDetalleSerie(1234);
    }
}

