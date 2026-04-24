/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.util;

import jakarta.persistence.EntityManager;
import org.zabalburu.seriesjpa.tmdb.modelo.Genero;
import org.zabalburu.seriesjpa.tmdb.modelo.Serie;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;
import org.zabalburu.seriesjpa.tmdb.modelo.Valoracion;


public class DataInitializer {

    public static void initialize() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            Long totalUsuarios = em.createQuery("""
                    SELECT COUNT(u)
                    FROM Usuario u
                    """, Long.class).getSingleResult();

            if (totalUsuarios == 0) {
                em.getTransaction().begin();

                Usuario admin = new Usuario("admin", "admin@seriesjpa.com", PasswordUtils.getHash("admin"));
                Usuario ana = new Usuario("ana", "ana@seriesjpa.com", PasswordUtils.getHash("ana"));
                Usuario luis = new Usuario("luis", "luis@seriesjpa.com", PasswordUtils.getHash("luis"));

                Genero drama = new Genero("Drama");
                Genero cienciaFiccion = new Genero("Ciencia ficción");
                Genero thriller = new Genero("Thriller");

                Serie dark = new Serie(1001, "Dark");
                dark.setSinopsis("Una serie alemana de misterio y viajes en el tiempo.");
                dark.addGenero(drama);
                dark.addGenero(cienciaFiccion);

                Serie lost = new Serie(1002, "Lost");
                lost.setSinopsis("Un grupo de supervivientes en una isla llena de misterios.");
                lost.addGenero(drama);
                lost.addGenero(thriller);

                Serie severance = new Serie(1003, "Severance");
                severance.setSinopsis("Una empresa separa quirúrgicamente recuerdos laborales y personales.");
                severance.addGenero(cienciaFiccion);
                severance.addGenero(thriller);

                em.persist(admin);
                em.persist(ana);
                em.persist(luis);

                em.persist(drama);
                em.persist(cienciaFiccion);
                em.persist(thriller);

                em.persist(dark);
                em.persist(lost);
                em.persist(severance);

                Valoracion v1 = new Valoracion(5, "Una obra maestra.");
                admin.addValoracion(v1);
                dark.addValoracion(v1);
                em.persist(v1);

                Valoracion v2 = new Valoracion(4, "Muy intrigante y original.");
                admin.addValoracion(v2);
                severance.addValoracion(v2);
                em.persist(v2);

                Valoracion v3 = new Valoracion(5, "De mis series favoritas.");
                ana.addValoracion(v3);
                lost.addValoracion(v3);
                em.persist(v3);

                Valoracion v4 = new Valoracion(4, "Muy buena atmósfera.");
                ana.addValoracion(v4);
                dark.addValoracion(v4);
                em.persist(v4);

                Valoracion v5 = new Valoracion(3, "Interesante, pero algo lenta.");
                luis.addValoracion(v5);
                severance.addValoracion(v5);
                em.persist(v5);

                Valoracion v6 = new Valoracion(4, "Muy entretenida.");
                luis.addValoracion(v6);
                lost.addValoracion(v6);
                em.persist(v6);

                em.getTransaction().commit();
            }

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
