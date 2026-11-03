# HU-13 · Criterios de aceptación

**Criterio:** Las estadísticas cuentan todos los reportes registrados, agrupados por categoría y por zona del campus, de mayor a menor cantidad.

## Escenario 1: Cantidad por categoría

- **Dado** que hay dos reportes de la categoría «Electrónicos» y uno de «Llaves»
- **Cuando** el miembro abre «Estadísticas»
- **Entonces** la tabla por categoría muestra «Electrónicos: 2» antes que «Llaves: 1»

## Escenario 2: Cantidad por zona

- **Dado** que hay tres reportes en la zona Centro y uno en la zona Norte
- **Cuando** el miembro abre «Estadísticas»
- **Entonces** la tabla por zona muestra «Centro: 3» antes que «Norte: 1»

## Escenario 3: Sin reportes

- **Dado** que todavía no se ha registrado ningún reporte
- **Cuando** el miembro abre «Estadísticas»
- **Entonces** el sistema muestra «Todavía no hay reportes registrados.»
