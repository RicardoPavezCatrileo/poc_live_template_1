package com.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        escribir(txtUsuario, usuario);
        escribir(txtClave, clave);
        click(btnLogin);
    }

    public String obtenerMensaje() {
        return obtenerTexto(lblMensaje);
    }
}