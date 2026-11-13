package com.qa.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

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