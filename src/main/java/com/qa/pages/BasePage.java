package com.qa.pages;

import com.qa.core.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.TIMEOUT));
    }

    /** Ruta relativa de la página, ej: "/login". Vacío = home. */
    protected abstract String ruta();

    public void abrir() {
        driver.get(Config.BASE_URL + ruta());
        esperarCarga();
    }

    protected void esperarCarga() {
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    protected WebElement esperarVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void escribir(By locator, String texto) {
        WebElement e = esperarVisible(locator);
        e.clear();
        e.sendKeys(texto);
    }

    protected String obtenerTexto(By locator) {
        return esperarVisible(locator).getText();
    }

    protected boolean estaVisible(By locator) {
        try {
            return esperarVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getTitulo() {
        return driver.getTitle();
    }

    public String getUrlActual() {
        return driver.getCurrentUrl();
    }
}