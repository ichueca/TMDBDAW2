/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.dao;



import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.zabalburu.seriesjpa.tmdb.modelo.Usuario;

public class UsuarioDAO {

    public void insert(EntityManager em, Usuario usuario) {
        em.persist(usuario);
    }

    public Usuario findById(EntityManager em, Long id) {
        return em.find(Usuario.class, id);
    }

    public Usuario findByNombreUsuario(EntityManager em, String nombreUsuario) {
        try {
            return em.createQuery("""
                    SELECT u
                    FROM Usuario u
                    WHERE u.nombreUsuario = :nombreUsuario
                    """, Usuario.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Usuario findByIdConValoraciones(EntityManager em, Long id) {
        try {
            return em.createQuery("""
                    SELECT DISTINCT u
                    FROM Usuario u
                    LEFT JOIN FETCH u.valoraciones v
                    WHERE u.id = :id
                    """, Usuario.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Usuario findByNombreUsuarioConValoraciones(EntityManager em, String nombreUsuario) {
        try {
            return em.createQuery("""
                    SELECT DISTINCT u
                    FROM Usuario u
                    LEFT JOIN FETCH u.valoraciones v
                    LEFT JOIN FETCH v.serie s
                    WHERE u.nombreUsuario = :nombreUsuario
                    """, Usuario.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}

