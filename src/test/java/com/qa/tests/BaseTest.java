package com.qa.tests;

import com.qa.core.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Clase madre de los tests TestNG (ruta TestNG).
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - Usa DriverFactory: getDriver() en @BeforeMethod y quitDriver() en @AfterMethod.
 *  - La heredan los tests (LoginTest), que reciben el driver ya abierto.
 *  - Es el equivalente de Hooks en la ruta BDD.
 */
public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}