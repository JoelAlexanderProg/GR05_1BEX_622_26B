# Cómo se aplicó TDD en la Práctica 2

Cada regla del rechazo de una reclamación se desarrolló con un ciclo Rojo-Verde-Refactor:

1. Rojo: se escribe una prueba que describe la regla y se ejecuta. Falla, porque el código todavía no la cumple.
2. Verde: se escribe el código mínimo para que la prueba pase. Todas las pruebas deben pasar.
3. Refactor: se mejora el código sin cambiar lo que hace y se vuelven a ejecutar todas las pruebas.

Cada fase quedó en un commit propio, con el formato «TDD número · fase: descripción». Por ejemplo, la prueba 3 está en los commits «TDD 3 · Rojo», «TDD 3 · Verde» y «TDD 3 · Refactor». Se pueden ver en la pestaña de commits del repositorio.
