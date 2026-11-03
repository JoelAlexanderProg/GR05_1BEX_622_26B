# HU-12 · Criterios de aceptación

**Criterio:** El mapa muestra solo los reportes activos cuya descripción contiene la palabra buscada, sin distinguir mayúsculas, tildes ni espacios sobrantes. La búsqueda se combina con los demás filtros.

## Escenario 1: Palabra con coincidencias

- **Dado** que en el mapa están la «Mochila negra con cuadernos de Cálculo» y las «Llaves con llavero azul»
- **Cuando** el miembro busca «mochila»
- **Entonces** el mapa muestra solo la mochila

## Escenario 2: Sin coincidencias

- **Dado** que ningún reporte activo menciona un paraguas
- **Cuando** el miembro busca «paraguas»
- **Entonces** el sistema muestra «No hay reportes activos que cumplan los filtros.»

## Escenario 3: Mayúsculas y tildes

- **Dado** que en el mapa está la «Cámara Canon en estuche gris»
- **Cuando** el miembro busca «CAMARA», en mayúsculas y sin tilde
- **Entonces** el mapa muestra la cámara
