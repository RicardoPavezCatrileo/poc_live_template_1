@login @exitoso
Feature: Login exitoso
  Como usuario registrado
  Quiero iniciar sesión con mis credenciales
  Para acceder a mi área segura

  @smoke
  Scenario: CP001 - Login exitoso con credenciales válidas
    Given que el usuario está en la página de login
    When ingresa el usuario "tomsmith" y la clave "SuperSecretPassword!"
    Then ve el mensaje "You logged into a secure area!"
    And es redirigido al área segura
