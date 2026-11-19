package com.qa.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Puente entre TestNG y Cucumber (ruta BDD).
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - Registrada en testng.xml (bloque "BDD - Cucumber"), que ejecuta Surefire con mvn test.
 *  - Hereda de AbstractTestNGCucumberTests: TestNG ve cada escenario como un caso de prueba.
 *  - features: lee los .feature; glue: busca LoginSteps (com.qa.steps) y Hooks (com.qa.hooks).
 *  - plugin: genera los reportes en target/.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.qa.steps", "com.qa.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-report.html",
                "json:target/cucumber.json"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}