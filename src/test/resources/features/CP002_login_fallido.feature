@login @fallido
Feature: Login fallido
  Como sitio web
  Quiero rechazar credenciales inválidas
  Para proteger el área segura

  Scenario: CP002 - Login fallido con usuario inválido
    Given que el usuario está en la página de login
    When ingresa el usuario "malo" y la clave "mala"
    Then ve el mensaje "Your username is invalid!"
    And permanece en la página de login
