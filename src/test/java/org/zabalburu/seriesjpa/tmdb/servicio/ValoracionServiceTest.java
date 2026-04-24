/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.servicio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zabalburu.seriesjpa.tmdb.dao.SerieDAO;
import org.zabalburu.seriesjpa.tmdb.dao.UsuarioDAO;
import org.zabalburu.seriesjpa.tmdb.dao.ValoracionDAO;
import org.zabalburu.seriesjpa.tmdb.modelo.Genero;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;
import org.zabalburu.seriesjpa.tmdb.modelo.Valoracion;
import org.zabalburu.seriesjpa.tmdb.util.JPAUtil;



public class ValoracionServiceTest {

    private static EntityManagerFactory emf;

    private ValoracionService valoracionService;

    @BeforeAll
    public static void initEntityManagerFactory() {
        emf = JPAUtil.getEntityManagerFactory("seriesjpa_test");
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

        valoracionService = 
            new ValoracionService(
                emf,
                new UsuarioDAO(),
                new SerieDAO(),
                new ValoracionDAO()
        );
    }

    private void limpiarBaseDeDatos() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        em.createQuery("DELETE FROM Valoracion").executeUpdate();
        em.createQuery("DELETE FROM Serie").executeUpdate();
        em.createQuery("DELETE FROM Usuario").executeUpdate();

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void guardarValoracion_deberiaCrearNuevaValoracion() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Usuario usuario = new Usuario("ana", "1234", "ana@test.com");
        Serie serie = new Serie(500, "Dark");
        em.persist(usuario);
        em.persist(serie);

        em.getTransaction().commit();
        em.close();

        valoracionService.anadirValoracion(usuario.getId(), serie.getId(), 5, "Excelente serie");

        EntityManager emComprobacion = emf.createEntityManager();

        List<Valoracion> valoraciones = emComprobacion.createQuery("""
                SELECT v
                FROM Valoracion v                                                                                                
                """, Valoracion.class).getResultList();

        emComprobacion.close();

        Assertions.assertEquals(1, valoraciones.size());
        Assertions.assertEquals(5, valoraciones.get(0).getEstrellas());
        Assertions.assertEquals("Excelente serie", valoraciones.get(0).getComentario());
        
    }

    @Test
    public void guardarValoracion_deberiaActualizarSiYaExiste() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Usuario usuario = new Usuario("ana", "1234", "ana@test.com");
        Serie serie = new Serie(500, "Dark");
        Valoracion valoracion = new Valoracion(usuario, serie, 3, "Bien", java.time.LocalDate.now());

        em.persist(usuario);
        em.persist(serie);
        em.persist(valoracion);

        em.getTransaction().commit();
        em.close();

        valoracionService.anadirValoracion(usuario.getId(), serie.getId(), 5, "Mucho mejor de lo que recordaba");

        EntityManager emComprobacion = emf.createEntityManager();

        List<Valoracion> valoraciones = emComprobacion.createQuery("""
                SELECT v
                FROM Valoracion v
                """, Valoracion.class).getResultList();

        emComprobacion.close();

        Assertions.assertEquals(1, valoraciones.size());
        Assertions.assertEquals(5, valoraciones.get(0).getEstrellas());
        Assertions.assertEquals("Mucho mejor de lo que recordaba", valoraciones.get(0).getComentario());
    }

    @Test
    public void guardarValoracion_deberiaLanzarExcepcionSiEstrellasNoValidas() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Usuario usuario = new Usuario("ana", "1234", "ana@test.com");
        Serie serie = new Serie(500, "Dark");

        em.persist(usuario);
        em.persist(serie);

        em.getTransaction().commit();
        em.close();

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                valoracionService.anadirValoracion(usuario.getId(), serie.getId(), 0, "Comentario")
        );
    }
}
