# HU-11 · Criterios de aceptación

**Criterio:** Solo quien registró el reporte puede retirarlo, y solo mientras está activo. Al retirarlo, el reporte sale del mapa y sus reclamaciones pendientes quedan rechazadas.

## Escenario 1: Reporte activo

- **Dado** que Bruno Salas registró la «Mochila negra con cuadernos de Cálculo» y el reporte está «Activo»
- **Cuando** pulsa «Retirar» en «Mis reportes» y confirma
- **Entonces** el reporte queda «Retirado» y ya no aparece en el mapa

## Escenario 2: Reporte reservado

- **Dado** que la «Calculadora científica Casio» de Bruno está «Reservada» porque él aceptó una reclamación
- **Cuando** Bruno abre «Mis reportes»
- **Entonces** ese reporte no muestra la opción «Retirar»

## Escenario 3: Reclamaciones pendientes

- **Dado** que Andrea Paz reclamó la mochila de Bruno y su reclamación está «Pendiente»
- **Cuando** Bruno retira el reporte de la mochila
- **Entonces** la reclamación de Andrea queda «Rechazada» con el motivo «El reporte fue retirado por su autor.»
