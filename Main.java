import java.util.List;

/* ============================================================
 * PRINCIPIO DE SUSTITUCIÓN DE LISKOV (LSP) - la "L" de SOLID
 * BASE: una subclase debe poder reemplazar a su clase base sin
 * romper el comportamiento que el código cliente espera.
 * Este archivo tiene 2 partes: 1) la VIOLACIÓN y 2) la SOLUCIÓN.
 * ============================================================ */

// ---------- PARTE 1: EJEMPLO QUE VIOLA EL LSP ----------

// Clase base. Su "contrato" implícito es: ancho y alto son
// independientes, cambiar uno NO modifica el otro.
class RectanguloMalo {
    protected int ancho, alto;
    void setAncho(int a) { ancho = a; }
    void setAlto(int a)  { alto = a; }
    int area() { return ancho * alto; }
}

// AQUÍ SE ENCUENTRA LA VIOLACIÓN: matemáticamente un cuadrado "es un"
// rectángulo, pero en código NO se comporta igual. Al forzar ancho == alto,
// esta subclase rompe la postcondición de la base (ancho y alto
// independientes). Se prometió MENOS que la clase padre: eso viola el LSP.
class CuadradoMalo extends RectanguloMalo {
    @Override void setAncho(int a) { ancho = a; alto = a; } // cambia también el alto
    @Override void setAlto(int a)  { ancho = a; alto = a; } // cambia también el ancho
}

// ---------- PARTE 2: SOLUCIÓN QUE RESPETA EL LSP ----------

// CÓMO SE APLICA: en lugar de heredar una relación que no es sustituible,
// se crea una abstracción común (Forma) con el contrato mínimo que TODAS
// las figuras cumplen: poder calcular su área.
interface Forma { int area(); }

// Cada figura implementa Forma de manera independiente, sin sobrescribir
// el comportamiento de la otra. Ninguna promete más ni menos que el contrato.
class Rectangulo implements Forma {
    private final int ancho, alto;
    Rectangulo(int ancho, int alto) { this.ancho = ancho; this.alto = alto; }
    public int area() { return ancho * alto; }
}

class Cuadrado implements Forma {
    private final int lado;
    Cuadrado(int lado) { this.lado = lado; }
    public int area() { return lado * lado; }
}

public class Main {
    // CÓDIGO CLIENTE: usa la clase base y ASUME que ancho y alto son
    // independientes. Si una subclase no es sustituible, este método falla.
    static void probar(RectanguloMalo r) {
        r.setAncho(5);
        r.setAlto(4);
        System.out.println(r.getClass().getSimpleName() + " -> esperado: 20, obtenido: " + r.area());
    }

    public static void main(String[] args) {
        // Con RectanguloMalo funciona (20). Con CuadradoMalo da 16: el
        // reemplazo cambió el comportamiento. ESA es la señal de violación.
        probar(new RectanguloMalo());
        probar(new CuadradoMalo());

        // Con la solución, el cliente trabaja con la abstracción Forma y
        // cualquier implementación se puede sustituir sin sorpresas.
        List<Forma> formas = List.of(new Rectangulo(5, 4), new Cuadrado(4));
        for (Forma f : formas) {
            System.out.println(f.getClass().getSimpleName() + " -> area: " + f.area());
        }
    }
}