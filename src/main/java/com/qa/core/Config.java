package com.qa.core;

public final class Config {
    private Config() {}

    // Se pueden cambiar por consola: mvn test -Dbrowser=firefox -DbaseUrl=https://...
    public static final String BASE_URL = System.getProperty("baseUrl", "https://the-internet.herokuapp.com");
    public static final String BROWSER = System.getProperty("browser", "chrome");
    public static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("headless", "false"));
    public static final int TIMEOUT = Integer.parseInt(System.getProperty("timeout", "10"));
}