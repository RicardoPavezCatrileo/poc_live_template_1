# CLAUDE.md

Este archivo entrega orientación a Claude Code (claude.ai/code) para trabajar con el código de este repositorio.

## Contexto

Proyecto de práctica para aprender automatización de pruebas QA con un framework basado en **Selenium 4 + Java 21 + TestNG + Maven**, al que luego se agregó **Cucumber (BDD)**. Funciona como *template* reutilizable para proyectos nuevos. El sitio de ejemplo es `https://the-internet.herokuapp.com` (página de login).

El proyecto se fue construyendo paso a paso conversando en el chat web de Claude, y los comandos se ejecutaron **manualmente en PowerShell (Windows)**. Por eso:

- Todo va en **español**: código, nombres de métodos (`abrir`, `escribir`, `obtenerTexto`, `ruta`), comentarios, pasos Gherkin y documentación.
- Al proponer comandos, usar sintaxis de PowerShell y explicar brevemente qué hace cada uno, ya que el objetivo es aprender.
- `PASOS_PROYECTO.txt` es la receta paso a paso de cómo se armó el proyecto (herramientas, pom, core, pages, tests, snippets, GitHub, flujo de ramas/PR). Si se agrega una etapa nueva al proyecto, documentarla ahí como un PASO más.

## Comandos

```powershell
mvn compile                                  # compilar
mvn test                                     # suite completa (testng.xml: tests TestNG + runner Cucumber)
mvn test -Dbrowser=firefox                   # chrome (por defecto) | firefox | edge
mvn test -Dheadless=true                     # sin abrir ventana del navegador
mvn test -DbaseUrl=https://... -Dtimeout=15  # cambiar URL base / timeout de esperas
mvn test -Dtest=LoginTest                    # una sola clase TestNG
mvn test -Dtest=LoginTest#loginExitoso       # un solo método TestNG
mvn test -Dtest=TestRunner "-Dcucumber.filter.tags=@smoke"   # solo Cucumber, filtrado por tag
mvn clean                                    # limpiar compilados (target/)
```

En PowerShell, las propiedades `-D` que contienen puntos deben ir entre comillas (como en el ejemplo de `cucumber.filter.tags`).

Reportes de Cucumber: `target/cucumber-report.html` y `target/cucumber.json`.

## Arquitectura

- `src/main/java/com/qa/core`
  - `Config`: toda la configuración se lee de propiedades `-D` con valores por defecto (`baseUrl`, `browser`, `headless`, `timeout`).
  - `DriverFactory`: `ThreadLocal<WebDriver>`, se crea de forma perezosa con `getDriver()` y se cierra con `quitDriver()`. Todas las capas obtienen el driver desde aquí; no crear drivers en otro lugar.
- `src/main/java/com/qa/pages`: Page Object Model. `BasePage` contiene un `WebDriverWait` y helpers con espera explícita (`click`, `escribir`, `obtenerTexto`, `estaVisible`, `esperarCarga`). Cada página extiende `BasePage`, declara sus locators `By` como campos privados e implementa `ruta()` (ruta relativa a `BASE_URL`, usada por `abrir()`).
- Dos estilos de prueba en paralelo que comparten las mismas páginas y el mismo `DriverFactory`:
  - **TestNG**: `com.qa.tests`, los tests extienden `BaseTest` (abre y cierra el driver por cada método).
  - **Cucumber BDD**: features en `src/test/resources/features`, step definitions en `com.qa.steps`, ciclo de vida en `com.qa.hooks.Hooks` (crea el driver en `@Before`; en `@After` adjunta captura si el escenario falla y cierra el driver). `com.qa.runners.TestRunner` define `glue = {"com.qa.steps", "com.qa.hooks"}`: si se crean paquetes nuevos de steps o hooks, hay que agregarlos ahí.
- `src/test/resources/testng.xml` es la suite que ejecuta Surefire: las nuevas clases de test TestNG (o runners) deben registrarse ahí para que corran con `mvn test`.

## Snippets del editor

`.vscode/qa.code-snippets` tiene atajos (equivalentes a los *Live Templates* de IntelliJ): `qpage` (clase Page Object), `qtest` (clase de test), `loc` (locator), `pctor` (constructor de Page), `stest` (método `@Test`), `atrue`, `aeq` (asserts). Actualizarlos si cambian las convenciones de Pages o Tests.

## Flujo Git

Trabajar en ramas `feature/<nombre>` y unir a `main` mediante Pull Request (detalle en el PASO 9 de `PASOS_PROYECTO.txt`).
