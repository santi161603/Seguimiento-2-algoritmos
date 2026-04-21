# Sistema de multiplicacion de matrices grandes (Java)

Proyecto didactico con 15 algoritmos de multiplicacion en archivos `.java` separados y una clase principal `GestorMatrices` para:

- Generar matrices cuadradas `n x n` (con `n` potencia de 2)
- Llenarlas con numeros aleatorios de 6 digitos
- Ejecutar y medir tiempos de 15 algoritmos
- Guardar matrices y resultados en archivos `.csv`

## Archivos principales

- `GestorMatrices.java`: coordinador principal.
- `MatrizUtils.java`: utilidades de generacion, validacion y guardado.

### Algoritmos implementados (15)

1. `NaivOnArray`
2. `NaivLoopUnrollingTwo`
3. `NaivLoopUnrollingFour`
4. `WinogradOriginal`
5. `WinogradScaled`
6. `StrassenNaiv`
7. `StrassenWinograd`
8. `III3SequentialBlock` (Row by Column)
9. `III4ParallelBlock` (Row by Column - Parallel)
10. `III5EnhancedParallelBlock` (Row by Column - Parallel)
11. `IV3SequentialBlock` (Row by Row)
12. `IV4ParallelBlock` (Row by Row - Parallel)
13. `IV5EnhancedParallelBlock` (Row by Row - Parallel)
14. `V3SequentialBlock` (Column by Column)
15. `V4ParallelBlock` (Column by Column - Parallel)

## Compilar y ejecutar

```powershell
Set-Location "C:\Users\Santiago\Documents\GitHub\Seguimiento 2 algoritmos"
javac *.java
java GestorMatrices 8
```

Tambien puedes ejecutar sin argumento y el programa pedira `n` por consola:

```powershell
java GestorMatrices
```

## Salidas generadas

Se crean en la carpeta `salida/`:

- `MatrizA_n.csv`
- `MatrizB_n.csv`
- `Resultado_<Algoritmo>_n.csv` (uno por algoritmo)
- `Tiempos_n.csv` (resumen de tiempos y verificacion)

## Nota didactica

- Para facilitar sustentacion academica, el paralelismo usa `Thread` y `Runnable`.
- El codigo evita librerias avanzadas y prioriza estructuras simples: `for`, `if` y arreglos bidimensionales.

