public class StrassenNaiv {

    private static final int UMBRAL = 64;

    public static long[][] multiply(int[][] a, int[][] b) {
        return strassen(toLong(a), toLong(b));
    }

    public static long[][] multiplyLong(long[][] a, long[][] b) {
        return strassen(a, b);
    }

    private static long[][] strassen(long[][] a, long[][] b) {
        int n = a.length;
        long[][] c = new long[n][n];

        // Caso base claro: para tamanos pequenos usa el algoritmo clasico.
        if (n <= UMBRAL) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    long suma = 0;
                    for (int k = 0; k < n; k++) {
                        suma += a[i][k] * b[k][j];
                    }
                    c[i][j] = suma;
                }
            }
            return c;
        }

        int mitad = n / 2;

        long[][] a11 = new long[mitad][mitad];
        long[][] a12 = new long[mitad][mitad];
        long[][] a21 = new long[mitad][mitad];
        long[][] a22 = new long[mitad][mitad];
        long[][] b11 = new long[mitad][mitad];
        long[][] b12 = new long[mitad][mitad];
        long[][] b21 = new long[mitad][mitad];
        long[][] b22 = new long[mitad][mitad];

        dividir(a, a11, 0, 0);
        dividir(a, a12, 0, mitad);
        dividir(a, a21, mitad, 0);
        dividir(a, a22, mitad, mitad);
        dividir(b, b11, 0, 0);
        dividir(b, b12, 0, mitad);
        dividir(b, b21, mitad, 0);
        dividir(b, b22, mitad, mitad);

        long[][] m1 = strassen(sumar(a11, a22), sumar(b11, b22));
        long[][] m2 = strassen(sumar(a21, a22), b11);
        long[][] m3 = strassen(a11, restar(b12, b22));
        long[][] m4 = strassen(a22, restar(b21, b11));
        long[][] m5 = strassen(sumar(a11, a12), b22);
        long[][] m6 = strassen(restar(a21, a11), sumar(b11, b12));
        long[][] m7 = strassen(restar(a12, a22), sumar(b21, b22));

        long[][] c11 = sumar(restar(sumar(m1, m4), m5), m7);
        long[][] c12 = sumar(m3, m5);
        long[][] c21 = sumar(m2, m4);
        long[][] c22 = sumar(restar(sumar(m1, m3), m2), m6);

        unir(c11, c, 0, 0);
        unir(c12, c, 0, mitad);
        unir(c21, c, mitad, 0);
        unir(c22, c, mitad, mitad);

        return c;
    }

    private static long[][] toLong(int[][] m) {
        int n = m.length;
        long[][] res = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = m[i][j];
            }
        }
        return res;
    }

    private static void dividir(long[][] padre, long[][] hijo, int fila, int col) {
        for (int i = 0; i < hijo.length; i++) {
            for (int j = 0; j < hijo.length; j++) {
                hijo[i][j] = padre[i + fila][j + col];
            }
        }
    }

    private static void unir(long[][] hijo, long[][] padre, int fila, int col) {
        for (int i = 0; i < hijo.length; i++) {
            for (int j = 0; j < hijo.length; j++) {
                padre[i + fila][j + col] = hijo[i][j];
            }
        }
    }

    private static long[][] sumar(long[][] a, long[][] b) {
        int n = a.length;
        long[][] r = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                r[i][j] = a[i][j] + b[i][j];
            }
        }
        return r;
    }

    private static long[][] restar(long[][] a, long[][] b) {
        int n = a.length;
        long[][] r = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                r[i][j] = a[i][j] - b[i][j];
            }
        }
        return r;
    }
}

