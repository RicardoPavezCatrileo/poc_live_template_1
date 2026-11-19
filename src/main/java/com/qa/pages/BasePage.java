package com.qa.pages;

import com.qa.core.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Clase madre de todas las páginas (patrón Page Object Model - POM).
 *
 * Idea del POM: cada página del sitio se representa con una clase Java que
 * guarda sus locators (cómo encontrar los elementos) y sus acciones
 * (login, buscar, etc.). Así los tests no tocan Selenium directamente y,
 * si cambia la página, solo se corrige en un lugar.
 *
 * Esta clase reúne lo que TODAS las páginas necesitan:
 *  - el driver (el navegador),
 *  - una espera explícita (WebDriverWait),
 *  - métodos reutilizables que esperan antes de interactuar
 *    (evitan errores de "elemento no encontrado" por cargas lentas).
 *
 * Es "abstract": no se puede crear un BasePage directamente,
 * solo se usa heredando (ej: LoginPage extends BasePage).
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - Usa Config: BASE_URL (en abrir()) y TIMEOUT (en el WebDriverWait).
 *  - Recibe el WebDriver que creó DriverFactory (no lo crea ella).
 *  - Usa WebDriverWait + ExpectedConditions para esperar antes de actuar.
 *  - La heredan las páginas (LoginPage): ellas dicen QUÉ hacer, BasePage sabe CÓMO.
 */
public abstract class BasePage {

    // Navegador que controla Selenium. "protected" = visible en las clases hijas.
    // "final" = se asigna una sola vez (en el constructor) y no cambia.
    protected final WebDriver driver;

    // Espera explícita: reintenta una condición hasta que se cumpla
    // o se acabe el tiempo (Config.TIMEOUT, por defecto 10 segundos).
    protected final WebDriverWait wait;

    // Logger de la página: getClass() = la página real (ej: LoginPage),
    // así cada línea del log indica en qué página ocurrió la acción.
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor: recibe el driver (normalmente desde DriverFactory.getDriver())
     * y prepara la espera con el timeout configurado.
     * Es "protected" porque solo lo llaman las clases hijas con super(driver).
     */
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.TIMEOUT));
    }

    /**
     * Ruta relativa de la página, ej: "/login". Vacío ("") = home.
     * Cada página hija DEBE implementarlo (por eso es abstract).
     * Se concatena con Config.BASE_URL en el método abrir().
     */
    protected abstract String ruta();

    /**
     * Navega a la página: BASE_URL + ruta()
     * Ej: "https://the-internet.herokuapp.com" + "/login"
     * y espera a que termine de cargar.
     */
    public void abrir() {
        log.info("Abriendo {}", Config.BASE_URL + ruta());
        driver.get(Config.BASE_URL + ruta());
        esperarCarga();
    }

    /**
     * Espera a que el navegador termine de cargar la página.
     * Ejecuta JavaScript en el navegador y pregunta por document.readyState;
     * cuando responde "complete", la página (HTML, imágenes, scripts) ya cargó.
     * La "d" de la lambda es el mismo driver que recibe el wait.
     */
    protected void esperarCarga() {
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    /**
     * Espera a que el elemento exista en la página Y sea visible, y lo devuelve.
     * Si no aparece dentro del timeout, lanza TimeoutException (el test falla).
     * Es la base de escribir(), obtenerTexto() y estaVisible().
     *
     * @param locator cómo encontrar el elemento, ej: By.id("username")
     */
    protected WebElement esperarVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Hace clic en un elemento, pero solo cuando es "clickeable"
     * (visible y habilitado). Evita fallar si el botón aún se está cargando.
     */
    protected void click(By locator) {
        log.debug("Click en {}", locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Escribe texto en un campo (input, textarea).
     * Pasos: espera que sea visible -> borra lo que tenga (clear) -> escribe (sendKeys).
     * El clear() evita que el texto nuevo se sume al que ya existía.
     */
    protected void escribir(By locator, String texto) {
        // No se registra el texto: podría ser una clave
        log.debug("Escribiendo en {}", locator);
        WebElement e = esperarVisible(locator);
        e.clear();
        e.sendKeys(texto);
    }

    /**
     * Devuelve el texto visible de un elemento (ej: un mensaje de error o éxito).
     * Se usa en los tests para validar con Assert.
     */
    protected String obtenerTexto(By locator) {
        String texto = esperarVisible(locator).getText();
        log.debug("Texto de {}: {}", locator, texto);
        return texto;
    }

    /**
     * Indica si un elemento está visible: true o false.
     * A diferencia de esperarVisible(), NO hace fallar el test si el elemento
     * no aparece: atrapa la TimeoutException y devuelve false.
     * Útil para validar que algo SÍ o NO se muestra.
     * Ojo: si el elemento no aparece, igual espera el timeout completo.
     */
    protected boolean estaVisible(By locator) {
        try {
            return esperarVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Título de la pestaña del navegador (etiqueta <title> del HTML). */
    public String getTitulo() {
        return driver.getTitle();
    }

    /** URL actual del navegador. Útil para validar redirecciones (ej: que llegó a "/secure"). */
    public String getUrlActual() {
        return driver.getCurrentUrl();
    }
}
