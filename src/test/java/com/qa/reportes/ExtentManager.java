package com.qa.reportes;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.qa.core.Config;
import com.qa.core.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * Administra el reporte ExtentReports: target/reportes/extent-report.html
 *
 * Relación en la arquitectura (ver arquitectura.html):
 *  - La usan BaseTest (ruta TestNG) y Hooks (ruta BDD): ambas rutas escriben
 *    en el MISMO reporte, cada test o escenario es una entrada.
 *  - Usa DriverFactory para tomar el screenshot cuando algo falla.
 *  - Usa Config para mostrar navegador, URL y headless en el resumen del reporte.
 *
 * Flujo por cada test/escenario:
 *   iniciarTest(nombre, tags...) -> marcarResultado(...) -> se guarda el HTML (flush).
 *
 * ThreadLocal: igual que en DriverFactory, cada hilo tiene su propio test actual.
 */
public final class ExtentManager {

    private static final String RUTA_REPORTE = "target/reportes/extent-report.html";

    private static final ExtentReports EXTENT = crearReporte();
    private static final ThreadLocal<ExtentTest> TEST_ACTUAL = new ThreadLocal<>();

    private ExtentManager() {}

    private static ExtentReports crearReporte() {
        ExtentSparkReporter spark = new ExtentSparkReporter(RUTA_REPORTE);
        spark.config().setDocumentTitle("Reporte de pruebas QA");
        spark.config().setReportName("Ejecución automatizada - Selenium");
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setEncoding("UTF-8");

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Navegador", Config.BROWSER);
        extent.setSystemInfo("Headless", String.valueOf(Config.HEADLESS));
        extent.setSystemInfo("URL base", Config.BASE_URL);
        extent.setSystemInfo("Java", System.getProperty("java.version"));
        return extent;
    }

    /**
     * Crea la entrada del test/escenario en el reporte.
     * @param categorias agrupan en la pestaña "Category" (ej: tags @login, @smoke o "TestNG")
     */
    public static void iniciarTest(String nombre, String... categorias) {
        ExtentTest test = EXTENT.createTest(nombre);
        if (categorias.length > 0) test.assignCategory(categorias);
        TEST_ACTUAL.set(test);
    }

    /** Agrega una línea informativa al test actual (aparece en su detalle). */
    public static void info(String mensaje) {
        if (TEST_ACTUAL.get() != null) TEST_ACTUAL.get().info(mensaje);
    }

    /**
     * Marca el resultado del test actual y guarda el HTML.
     * Si falló, adjunta screenshot y el error. Debe llamarse ANTES de cerrar el navegador.
     */
    public static void marcarResultado(boolean fallo, Throwable error) {
        ExtentTest test = TEST_ACTUAL.get();
        if (test == null) return;

        if (fallo) {
            String captura = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BASE64);
            test.fail("El test falló",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(captura, "Screenshot").build());
            if (error != null) test.fail(error);
        } else {
            test.pass("El test pasó");
        }
        TEST_ACTUAL.remove();
        // flush escribe el HTML. Se hace tras cada test para que el reporte quede
        // completo aunque se ejecute solo una clase (mvn test -Dtest=...).
        EXTENT.flush();
    }

    /** Marca el test actual como omitido (skipped) y guarda el HTML. */
    public static void marcarOmitido(String motivo) {
        ExtentTest test = TEST_ACTUAL.get();
        if (test == null) return;
        test.skip(motivo);
        TEST_ACTUAL.remove();
        EXTENT.flush();
    }
}
