package com.qa.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Fábrica de navegadores: el ÚNICO lugar donde se crea y se cierra el WebDriver (capa core).
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - Usa Config: BROWSER (chrome/firefox/edge) y HEADLESS.
 *  - Crea el WebDriver de Selenium, que controla el navegador real.
 *  - La usan BaseTest (ruta TestNG) y Hooks (ruta BDD) para abrir y cerrar
 *    el navegador en cada test o escenario, y LoginSteps para entregarle
 *    el driver a las páginas.
 *  - ThreadLocal: un navegador por hilo, preparado para ejecución en paralelo.
 */
public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(crearDriver());
        }
        return DRIVER.get();
    }

    private static WebDriver crearDriver() {
        WebDriver driver = switch (Config.BROWSER.toLowerCase()) {
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (Config.HEADLESS) o.addArguments("-headless");
                yield new FirefoxDriver(o);
            }
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (Config.HEADLESS) o.addArguments("--headless=new");
                yield new EdgeDriver(o);
            }
            default -> {
                ChromeOptions o = new ChromeOptions();
                if (Config.HEADLESS) o.addArguments("--headless=new");
                yield new ChromeDriver(o);
            }
        };
        driver.manage().window().maximize();
        return driver;
    }

    public static void quitDriver() {
        if (DRIVER.get() != null) {
            DRIVER.get().quit();
            DRIVER.remove();
        }
    }
}