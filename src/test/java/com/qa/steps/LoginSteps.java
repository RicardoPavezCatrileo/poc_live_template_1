package com.qa.steps;

import com.qa.core.DriverFactory;
import com.qa.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginSteps {

    private LoginPage loginPage;

    @Given("que el usuario está en la página de login")
    public void abrirLogin() {
        loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.abrir();
    }

    @When("ingresa el usuario {string} y la clave {string}")
    public void ingresarCredenciales(String usuario, String clave) {
        loginPage.login(usuario, clave);
    }

    @Then("ve el mensaje {string}")
    public void validarMensaje(String mensajeEsperado) {
        String obtenido = loginPage.obtenerMensaje();
        Assert.assertTrue(obtenido.contains(mensajeEsperado),
                "Esperado: " + mensajeEsperado + " | Obtenido: " + obtenido);
    }

    // "And" en el feature reutiliza cualquier anotación (@Given/@When/@Then):
    // Cucumber solo compara el texto del paso.
    @Then("es redirigido al área segura")
    public void validarAreaSegura() {
        String url = loginPage.getUrlActual();
        Assert.assertTrue(url.contains("/secure"), "No redirigió a /secure. URL actual: " + url);
    }

    @Then("permanece en la página de login")
    public void validarSigueEnLogin() {
        String url = loginPage.getUrlActual();
        Assert.assertTrue(url.contains("/login"), "Salió de /login. URL actual: " + url);
    }
}