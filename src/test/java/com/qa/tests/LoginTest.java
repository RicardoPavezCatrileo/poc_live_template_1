package com.qa.tests;

import com.qa.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "Login exitoso con credenciales válidas")
    public void loginExitoso() {
        LoginPage login = new LoginPage(driver);
        login.abrir();
        login.login("tomsmith", "SuperSecretPassword!");

        Assert.assertTrue(login.obtenerMensaje().contains("You logged into a secure area!"),
                "No apareció el mensaje de login exitoso");
        Assert.assertTrue(login.getUrlActual().contains("/secure"), "No redirigió a /secure");
    }

    @Test(description = "Login fallido con usuario inválido")
    public void loginFallido() {
        LoginPage login = new LoginPage(driver);
        login.abrir();
        login.login("usuarioMalo", "claveMala");

        Assert.assertTrue(login.obtenerMensaje().contains("Your username is invalid!"),
                "No apareció el mensaje de error");
    }
}