package com.qa.hooks;

import com.qa.core.DriverFactory;
import com.qa.reportes.ExtentManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ciclo de vida de cada escenario Cucumber (ruta BDD).
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - Cucumber la encuentra porque com.qa.hooks está en el glue de TestRunner.
 *  - Usa DriverFactory: crea el navegador en @Before y lo cierra en @After.
 *  - Si el escenario falla, adjunta un screenshot a target/cucumber-report.html.
 *  - Registra cada escenario en ExtentManager (target/reportes/extent-report.html)
 *    y escribe logs (consola y target/logs/ejecucion.log).
 *  - Es el equivalente de BaseTest en la ruta TestNG.
 */
public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    @Before
    public void setUp(Scenario scenario) {
        log.info("===== Inicio escenario: {} {}", scenario.getName(), scenario.getSourceTagNames());
        // Los tags del feature (@login, @smoke...) quedan como categorías en Extent
        ExtentManager.iniciarTest(scenario.getName(), scenario.getSourceTagNames().toArray(new String[0]));
        DriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            log.error("Escenario FALLIDO: {}", scenario.getName());
            byte[] captura = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(captura, "image/png", scenario.getName());
        } else {
            log.info("Escenario OK: {}", scenario.getName());
        }
        // Cucumber no entrega la excepción al hook: el detalle del error está
        // en la consola y en target/cucumber-report.html
        ExtentManager.marcarResultado(scenario.isFailed(), null);
        DriverFactory.quitDriver();
        log.info("===== Fin escenario: {} -> {}", scenario.getName(), scenario.getStatus());
    }
}
