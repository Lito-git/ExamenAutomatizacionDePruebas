package com.iplacex.login;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Pruebas unitarias de ServicioAutenticacion: validan la lógica aislada, sin red ni servidor.
 * Cada prueba verifica un solo comportamiento.
 */
public class ServicioAutenticacionTest {

    private ServicioAutenticacion servicio;

    // Servicio nuevo antes de cada prueba: ninguna depende de otra
    @Before
    public void setUp() {
        servicio = new ServicioAutenticacion();
        servicio.registrarUsuario("ana@iplacex.cl", "Clave123", "Ana");
    }

    @Test
    public void credencialesValidasRetornaBienvenida() {
        assertEquals("Bienvenido, Ana", servicio.iniciarSesion("ana@iplacex.cl", "Clave123"));
    }

    @Test
    public void contrasenaIncorrectaRetornaCredencialesInvalidas() {
        assertEquals(ServicioAutenticacion.MSG_CREDENCIALES_INVALIDAS,
                servicio.iniciarSesion("ana@iplacex.cl", "ClaveMala"));
    }

    @Test
    public void correoNoRegistradoRetornaCredencialesInvalidas() {
        assertEquals(ServicioAutenticacion.MSG_CREDENCIALES_INVALIDAS,
                servicio.iniciarSesion("otro@iplacex.cl", "Clave123"));
    }

    @Test
    public void correoVacioRetornaMensajeCamposVacios() {
        assertEquals(ServicioAutenticacion.MSG_CAMPOS_VACIOS,
                servicio.iniciarSesion("", "Clave123"));
    }
}