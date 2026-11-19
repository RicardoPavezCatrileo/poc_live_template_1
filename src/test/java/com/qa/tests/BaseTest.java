package com.qa.tests;

import com.qa.core.DriverFactory;
import com.qa.reportes.ExtentManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Clase madre de los tests TestNG (ruta TestNG).
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - Usa DriverFactory: getDriver() en @BeforeMethod y quitDriver() en @AfterMethod.
 *  - Registra cada @Test en ExtentManager (target/reportes/extent-report.html),
 *    con screenshot si falla, y escribe logs (consola y target/logs/ejecucion.log).
 *  - La heredan los tests (LoginTest), que reciben el driver ya abierto.
 *  - Es el equivalente de Hooks en la ruta BDD.
 */
public abstract class BaseTest {

    // getClass() = la clase hija real (ej: LoginTest), así el log muestra su nombre
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected WebDriver driver;

    /** TestNG entrega el método que se va a ejecutar: se usa su nombre y description. */
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method metodo) {
        String descripcion = metodo.getAnnotation(Test.class).description();
        String nombre = descripcion.isEmpty() ? metodo.getName() : descripcion;
        log.info("===== Inicio test: {}", nombre);
        ExtentManager.iniciarTest(nombre, "TestNG", getClass().getSimpleName());
        driver = DriverFactory.getDriver();
    }

    /** TestNG entrega el resultado del test: se marca en el reporte ANTES de cerrar el navegador. */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult resultado) {
        switch (resultado.getStatus()) {
            case ITestResult.FAILURE -> {
                log.error("Test FALLIDO: {}", resultado.getName(), resultado.getThrowable());
                ExtentManager.marcarResultado(true, resultado.getThrowable());
            }
            case ITestResult.SKIP -> {
                log.warn("Test OMITIDO: {}", resultado.getName());
                ExtentManager.marcarOmitido("Test omitido");
            }
            default -> {
                log.info("Test OK: {}", resultado.getName());
                ExtentManager.marcarResultado(false, null);
            }
        }
        DriverFactory.quitDriver();
    }
}
