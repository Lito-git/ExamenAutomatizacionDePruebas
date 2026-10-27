package com.iplacex.aceptacion;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.Assert.assertEquals;

/**
 * Step definitions de aceptación que envían solicitudes HTTP reales a la aplicación desplegada.
 * La URL del ambiente se toma de la variable BASE_URL (por defecto, el servidor local).
 */
public class AceptacionSteps {

    private static final String URL_BASE =
            System.getenv().getOrDefault("BASE_URL", "http://localhost:8080");

    private final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private HttpResponse<String> respuesta;

    @When("consulto el estado del servicio")
    public void consultoElEstadoDelServicio() throws Exception {
        HttpRequest solicitud = HttpRequest.newBuilder(URI.create(URL_BASE + "/salud")).GET().build();
        respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
    }

    @Then("el servicio responde con el código {int}")
    public void elServicioRespondeConElCodigo(int codigo) {
        assertEquals(codigo, respuesta.statusCode());
    }

    @When("inicio sesión con el correo {string} y la contraseña {string}")
    public void inicioSesion(String correo, String contrasena) throws Exception {
        String cuerpo = "correo=" + URLEncoder.encode(correo, StandardCharsets.UTF_8)
                + "&contrasena=" + URLEncoder.encode(contrasena, StandardCharsets.UTF_8);
        HttpRequest solicitud = HttpRequest.newBuilder(URI.create(URL_BASE + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build();
        respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
    }

    @Then("recibo el código {int} y el mensaje {string}")
    public void reciboElCodigoYElMensaje(int codigo, String mensaje) {
        assertEquals(codigo, respuesta.statusCode());
        assertEquals(mensaje, respuesta.body());
    }
}