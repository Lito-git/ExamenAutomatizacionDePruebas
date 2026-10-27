package com.iplacex.aceptacion;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner del Acceptance Gate que ejecuta solo los escenarios @acceptance contra el ambiente desplegado.
 * Su nombre no termina en Test ni IT, por lo que no se ejecuta con las pruebas unitarias
 * ni de integración; se invoca explícitamente con -Dtest=AceptacionRunner.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.iplacex.aceptacion",
        tags = "@acceptance",
        plugin = { "pretty", "html:target/reporte-aceptacion.html" }
)
public class AceptacionRunner {
}