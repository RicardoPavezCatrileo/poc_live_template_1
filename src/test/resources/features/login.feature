@login
Feature: Login de usuario
  Como usuario registrado
  Quiero iniciar sesión en el sitio
  Para acceder a mi área segura

  @smoke
  Scenario Outline: <caso> - Login con distintas credenciales
    Given que el usuario está en la página de login
    When ingresa el usuario "<usuario>" y la clave "<clave>"
    Then ve el mensaje "<mensaje>"
    

    Examples:
      | caso  | usuario  | clave                | mensaje                        |
      | CP001 | tomsmith | SuperSecretPassword! | You logged into a secure area! |
      | CP002 | malo     | mala                 | Your username is invalid!      |