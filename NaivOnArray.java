public class NaivOnArray {

    public static long[][] multiply(int[][] a, int[][] b) {
        int n = a.length;
        long[][] c = new long[n][n];

        // Recorre filas de A y columnas de B para calcular cada posicion de C.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long suma = 0;
                for (int k = 0; k < n; k++) {
                    suma += (long) a[i][k] * b[k][j];
                }
                c[i][j] = suma;
            }
        }
        return c;
    }
}

