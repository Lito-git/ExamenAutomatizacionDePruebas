@acceptance
Feature: Aceptación del inicio de sesión en ambiente de prueba
  Para decidir si la versión desplegada puede promoverse
  Como equipo de QA
  Quiero verificar los flujos críticos del inicio de sesión sobre la aplicación desplegada

  Scenario: El servicio desplegado está disponible
    When consulto el estado del servicio
    Then el servicio responde con el código 200

  Scenario: Inicio de sesión exitoso
    When inicio sesión con el correo "ana@iplacex.cl" y la contraseña "Clave123"
    Then recibo el código 200 y el mensaje "Bienvenido, Ana"

  Scenario Outline: Inicio de sesión rechazado
    When inicio sesión con el correo "<correo>" y la contraseña "<contrasena>"
    Then recibo el código <codigo> y el mensaje "<mensaje>"

    Examples:
      | correo         | contrasena | codigo | mensaje                           |
      | ana@iplacex.cl | ClaveMala  | 401    | Credenciales inválidas            |
      |                | Clave123   | 400    | Debe ingresar correo y contraseña |