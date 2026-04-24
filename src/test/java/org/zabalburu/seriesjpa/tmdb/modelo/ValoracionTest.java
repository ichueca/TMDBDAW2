/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.modelo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author ichueca
 */
public class ValoracionTest {
    
    public ValoracionTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of setEstrellas method, of class Valoracion.
     */
    @org.junit.jupiter.api.Test
    public void testSetEstrellas() {
        Valoracion valoracion = new Valoracion(4, "Muy buena");

        assertEquals(4, valoracion.getEstrellas());
        assertEquals("Muy buena", valoracion.getComentario());
        assertNotNull(valoracion.getFecha());
        
    }
    
    @Test()
    public void noDeberiaPermitirEstrellasMenoresQueUno() {
        assertThrows(IllegalArgumentException.class, 
                () -> new Valoracion(0, "Mal"));
        
        
    }

    @Test()
    public void noDeberiaPermitirEstrellasMayoresQueCinco() {
        assertThrows(IllegalArgumentException.class, 
                () -> new Valoracion(6, "Demasiado"));
    }

    @Test
    public void deberiaActualizarValoracion() {
        Valoracion valoracion = new Valoracion(3, "Normal");
        valoracion.actualizar(5, "Excelente");
        assertEquals(5, valoracion.getEstrellas());
        assertEquals("Excelente", valoracion.getComentario());
    }
}
