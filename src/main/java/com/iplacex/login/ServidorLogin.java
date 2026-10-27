package com.iplacex.login;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Servidor HTTP que expone el inicio de sesión en POST /login.
 */
public class ServidorLogin {

    static final int PUERTO_POR_DEFECTO = 8080;

    public static void main(String[] args) throws IOException {
        // El puerto se configura con la variable de entorno PORT (8080 por defecto)
        int puerto = Integer.parseInt(System.getenv().getOrDefault("PORT", String.valueOf(PUERTO_POR_DEFECTO)));
        HttpServer servidor = iniciar(puerto);
        System.out.println("Servidor de login escuchando en http://localhost:"
                + servidor.getAddress().getPort() + "/login");
    }

    /**
     * Crea y arranca el servidor con el usuario de prueba registrado.
     * Con puerto 0 el sistema asigna un puerto libre (usado por las pruebas de integración).
     */
    public static HttpServer iniciar(int puerto) throws IOException {
        ServicioAutenticacion servicio = new ServicioAutenticacion();
        servicio.registrarUsuario("ana@iplacex.cl", "Clave123", "Ana");

        HttpServer servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
        servidor.createContext("/login", exchange -> atenderLogin(exchange, servicio));
        servidor.start();
        return servidor;
    }

    /** Recibe las credenciales del formulario y responde con el código HTTP según el resultado. */
    private static void atenderLogin(HttpExchange exchange, ServicioAutenticacion servicio) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            responder(exchange, 405, "Método no permitido");
            return;
        }
        String cuerpo = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> datos = leerFormulario(cuerpo);
        String mensaje = servicio.iniciarSesion(datos.get("correo"), datos.get("contrasena"));

        // 400: campos vacíos | 401: credenciales inválidas | 200: acceso concedido
        int estado;
        if (mensaje.equals(ServicioAutenticacion.MSG_CAMPOS_VACIOS)) {
            estado = 400;
        } else if (mensaje.equals(ServicioAutenticacion.MSG_CREDENCIALES_INVALIDAS)) {
            estado = 401;
        } else {
            estado = 200;
        }
        responder(exchange, estado, mensaje);
    }

    /** Convierte un cuerpo "clave=valor&clave=valor" en un mapa. */
    private static Map<String, String> leerFormulario(String cuerpo) {
        Map<String, String> datos = new HashMap<>();
        for (String par : cuerpo.split("&")) {
            String[] claveValor = par.split("=", 2);
            if (claveValor.length == 2) {
                datos.put(URLDecoder.decode(claveValor[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(claveValor[1], StandardCharsets.UTF_8));
            }
        }
        return datos;
    }

    private static void responder(HttpExchange exchange, int estado, String texto) throws IOException {
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(estado, bytes.length);
        try (OutputStream salida = exchange.getResponseBody()) {
            salida.write(bytes);
        }
    }
}