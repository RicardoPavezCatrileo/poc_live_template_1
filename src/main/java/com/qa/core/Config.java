package com.qa.core;

/**
 * Configuración central del framework (capa core).
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - No depende de ninguna clase del proyecto: es la base de todo.
 *  - La usa DriverFactory: BROWSER y HEADLESS deciden qué navegador crear.
 *  - La usa BasePage: BASE_URL arma la URL en abrir() y TIMEOUT define el WebDriverWait.
 *  - Los valores llegan desde la terminal con -D (mvn test -Dbrowser=firefox).
 */
public final class Config {
    private Config() {}

    // Se pueden cambiar por consola: mvn test -Dbrowser=firefox -DbaseUrl=https://...
    public static final String BASE_URL = System.getProperty("baseUrl", "https://the-internet.herokuapp.com");
    public static final String BROWSER = System.getProperty("browser", "chrome");
    public static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("headless", "false"));
    public static final int TIMEOUT = Integer.parseInt(System.getProperty("timeout", "10"));
}