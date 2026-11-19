package com.qa.hooks;

import com.qa.core.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * Ciclo de vida de cada escenario Cucumber (ruta BDD).
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - Cucumber la encuentra porque com.qa.hooks está en el glue de TestRunner.
 *  - Usa DriverFactory: crea el navegador en @Before y lo cierra en @After.
 *  - Si el escenario falla, adjunta un screenshot a target/cucumber-report.html.
 *  - Es el equivalente de BaseTest en la ruta TestNG.
 */
public class Hooks {

    @Before
    public void setUp() {
        DriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] captura = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(captura, "image/png", scenario.getName());
        }
        DriverFactory.quitDriver();
    }
}