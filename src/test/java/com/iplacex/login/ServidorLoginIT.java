package com.iplacex.login;

import com.sun.net.httpserver.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * Pruebas de integración: levantan el servidor HTTP real y verifican, mediante solicitudes HTTP,
 * que el servidor y ServicioAutenticacion funcionan correctamente en conjunto.
 */
public class ServidorLoginIT {

    private static HttpServer servidor;
    private static String urlLogin;
    private static final HttpClient cliente = HttpClient.newHttpClient();

    // Levanta el servidor una vez en un puerto libre antes de todas las pruebas
    @BeforeClass
    public static void levantarServidor() throws Exception {
        servidor = ServidorLogin.iniciar(0);
        urlLogin = "http://localhost:" + servidor.getAddress().getPort() + "/login";
    }

    // Detiene el servidor al terminar, dejando el entorno limpio
    @AfterClass
    public static void detenerServidor() {
        servidor.stop(0);
    }

    /** Envía una solicitud POST /login con las credenciales como formulario. */
    private HttpResponse<String> enviarLogin(String correo, String contrasena) throws Exception {
        String cuerpo = "correo=" + URLEncoder.encode(correo, StandardCharsets.UTF_8)
                + "&contrasena=" + URLEncoder.encode(contrasena, StandardCharsets.UTF_8);
        HttpRequest solicitud = HttpRequest.newBuilder(URI.create(urlLogin))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build();
        return cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void loginValidoResponde200ConBienvenida() throws Exception {
        HttpResponse<String> respuesta = enviarLogin("ana@iplacex.cl", "Clave123");
        assertEquals(200, respuesta.statusCode());
        assertEquals("Bienvenido, Ana", respuesta.body());
    }

    @Test
    public void credencialesInvalidasResponde401() throws Exception {
        HttpResponse<String> respuesta = enviarLogin("ana@iplacex.cl", "ClaveMala");
        assertEquals(401, respuesta.statusCode());
    }

    @Test
    public void camposVaciosResponde400() throws Exception {
        HttpResponse<String> respuesta = enviarLogin("", "Clave123");
        assertEquals(400, respuesta.statusCode());
    }

    @Test
    public void metodoNoPermitidoResponde405() throws Exception {
        HttpRequest solicitud = HttpRequest.newBuilder(URI.create(urlLogin)).GET().build();
        HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, respuesta.statusCode());
    }
}