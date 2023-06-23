package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {}

    /**
     * Función de dispersión XOR.
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int dispersion = 0;
        for (int i = 0; i < llave.length; i+=4)
            dispersion ^= enInt(i, llave, false);
        return dispersion;
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int a, b, c;
        a = b = 0x9E37_79B9;
        c = -1;

        for (int i = 0; llave.length - i >= 0; i += 12) {
            boolean ultima = llave.length - i < 12;
            if (ultima)
                c += llave.length;
            a += enInt(i, llave, true);
            b += enInt(i+4, llave, true);
            c += enInt(i+8, llave, true) << (ultima ? 8 : 0);

            // Mezcla
            a -= b; a -= c; a ^= c >>> 13;
            b -= c; b -= a; b ^= a << 8;
            c -= a; c -= b; c ^= b >>> 13;

            a -= b; a -= c; a ^= c >>> 12;
            b -= c; b -= a; b ^= a << 16;
            c -= a; c -= b; c ^= b >>> 5;

            a -= b; a -= c; a ^= c >>> 3;
            b -= c; b -= a; b ^= a << 10;
            c -= a; c -= b; c ^= b >>> 15;
        }

        return c;
    }

    /**
     * Función de dispersión Daniel J. Bernstein.
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
        int h = 5381;
        for (int i = 0; i < llave.length; i++)
            h += (h<<5)+((int)llave[i] & 0xFF);
        return h;
    }

    /* Convierte una llave y un índice a un int en big o little endian. */
    private static int enInt(int i, byte[] llave, boolean littleEndian) {
        int resultado = 0;
        for (int j = 0; i+j < llave.length && j < 4; j++)
            resultado |= (((int)llave[i+j]) & 0xFF) << (8*(littleEndian?j:3-j));
        return resultado;
    }
}
