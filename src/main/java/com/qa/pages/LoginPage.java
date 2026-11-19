package com.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object de la página /login.
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - Hereda de BasePage: abrir(), escribir(), click(), obtenerTexto(), getUrlActual().
 *  - Define sus locators y acciones de negocio: login(usuario, clave) y obtenerMensaje().
 *  - La usan las dos rutas de prueba: LoginTest (TestNG) y LoginSteps (Cucumber).
 *  - Recibe el driver por constructor (viene de DriverFactory).
 */
public class LoginPage extends BasePage {

    // Locators
    private final By txtUsuario = By.id("username");
    private final By txtClave = By.id("password");
    private final By btnLogin = By.cssSelector("button[type='submit']");
    private final By lblMensaje = By.id("flash");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String ruta() {
        return "/login";
    }

    // Acciones
    public void login(String usuario, String clave) {
        log.info("Login con usuario: {}", usuario); // la clave nunca se registra
        escribir(txtUsuario, usuario);
        escribir(txtClave, clave);
        click(btnLogin);
    }

    public String obtenerMensaje() {
        return obtenerTexto(lblMensaje);
    }
}