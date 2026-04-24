/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.em;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.zabalburu.seriesjpa.tmdb.modelo.Genero;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;
import org.zabalburu.seriesjpa.tmdb.util.JPAUtil;

public class PersistenciaBasicaTest {

    @Test
    public void deberiaPersistirSerieConGeneros() {
        EntityManagerFactory emfTest = JPAUtil.getEntityManagerFactory("seriesjpa_test");

        EntityManager em = emfTest.createEntityManager();
        Long serieId;

        try {
            em.getTransaction().begin();

            Genero drama = new Genero("Drama");
            Genero fantasia = new Genero("Fantasía");

            Serie serie = new Serie(1001, "Prueba Serie");
            serie.addGenero(drama);
            serie.addGenero(fantasia);

            em.persist(drama);
            em.persist(fantasia);
            em.persist(serie);

            em.getTransaction().commit();
            serieId = serie.getId();

        } finally {
            em.close();
        }

        em = emfTest.createEntityManager();
        try {
            Serie recuperada = em.find(Serie.class, serieId);

            assertNotNull(recuperada);
            assertEquals("Prueba Serie", recuperada.getTitulo());
            assertEquals(2, recuperada.getGeneros().size());
            Serie noExiste = em.find(Serie.class, -1L);
            assertNull(noExiste);
        } finally {
            em.close();
        }
    }
}

