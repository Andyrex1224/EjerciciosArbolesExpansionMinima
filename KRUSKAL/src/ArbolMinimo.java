import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class ArbolMinimo extends JFrame {

    // Se almacenaran las aristas
    static class Arista {
        int origen;
        int destino;
        int peso;

        Arista(int origen, int destino, int peso) {
            this.origen = origen;
            this.destino = destino;
            this.peso = peso;
        }

        @Override
        public String toString() {
            return origen + " - " + destino + " (peso " + peso + ")";
        }
    }

    // Estructura para evitar ciclos (Kruskal)
    static class UnionFind {
        int[] padre;

        UnionFind(int n) {
            padre = new int[n];
            for (int i = 0; i < n; i++) padre[i] = i;
        }

        int find(int x) {
            while (padre[x] != x) {
                x = padre[x];
            }
            return x;
        }

        boolean union(int a, int b) {
            int raizA = find(a);
            int raizB = find(b);

            if (raizA == raizB) return false; // ya están conectados -> forma ciclo
            padre[raizB] = raizA;             // unir conjuntos
            return true;
        }
    }

    // ======= nodos en los cuales se va a visualizar =======
    static class Nodo {
        int x;
        int y;

        Nodo(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Ventana emergente que se mostrara
    static class PanelGrafo extends JPanel {
        int cantidadNodos;
        ArrayList<Arista> listaAristas;
        int[][] aristaEnArbolMinimo; // 0 = no, 1 = sí
        Nodo[] posicionesNodos;

        PanelGrafo() {
            setBackground(Color.WHITE);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (cantidadNodos > 0) {
                        calcularPosiciones();
                        repaint();
                    }
                }
            });
        }

        void cargarGrafo(int cantidadNodos, ArrayList<Arista> listaAristas, int[][] aristaEnArbolMinimo) {
            this.cantidadNodos = cantidadNodos;
            this.listaAristas = listaAristas;
            this.aristaEnArbolMinimo = aristaEnArbolMinimo;
            calcularPosiciones();
            repaint();
        }

        void calcularPosiciones() {
            posicionesNodos = new Nodo[cantidadNodos];

            int w = Math.max(getWidth(), 800);
            int h = Math.max(getHeight(), 600);
            int cx = w / 2;
            int cy = h / 2;
            int r = Math.min(w, h) / 2 - 80;

            for (int i = 0; i < cantidadNodos; i++) {
                double ang = 2 * Math.PI * i / cantidadNodos;
                int x = cx + (int) (r * Math.cos(ang));
                int y = cy + (int) (r * Math.sin(ang));
                posicionesNodos[i] = new Nodo(x, y);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (posicionesNodos == null) return;

            Graphics2D g2 = (Graphics2D) g;

            // Aristas
            for (Arista a : listaAristas) {
                int u = a.origen - 1;
                int v = a.destino - 1;

                Nodo p1 = posicionesNodos[u];
                Nodo p2 = posicionesNodos[v];

                if (aristaEnArbolMinimo[u][v] == 1) {
                    g2.setColor(Color.GREEN.darker());
                    g2.setStroke(new BasicStroke(4));
                } else {
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.setStroke(new BasicStroke(2));
                }

                g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                int mx = (p1.x + p2.x) / 2;
                int my = (p1.y + p2.y) / 2;
                g2.setColor(Color.BLACK);
                g2.drawString("" + a.peso, mx + 6, my - 6);
            }

            // Nodos
            for (int i = 0; i < cantidadNodos; i++) {
                Nodo p = posicionesNodos[i];

                g2.setColor(new Color(30, 120, 220));
                g2.fillOval(p.x - 18, p.y - 18, 36, 36);

                g2.setColor(Color.WHITE);
                g2.drawOval(p.x - 18, p.y - 18, 36, 36);

                g2.drawString("" + (i + 1), p.x - 4, p.y + 5);
            }
        }
    }

    // Desarollo del grafo y su visualización
    private final PanelGrafo panel = new PanelGrafo();

    private int cantidadNodos;
    private ArrayList<Arista> listaAristas = new ArrayList<>();
    private int[][] aristaEnArbolMinimo; // 0/1 (matriz que marca el árbol mínimo)

    public ArbolMinimo() {
        super("Árbol de Expansión Mínima - Kruskal (simple)");

        // Parte del grafo
        cantidadNodos = 8;
        listaAristas.add(new Arista(1, 2, 10));
        listaAristas.add(new Arista(1, 3, 8));
        listaAristas.add(new Arista(1, 4, 12));
        listaAristas.add(new Arista(2, 5, 12));
        listaAristas.add(new Arista(2, 6, 18));
        listaAristas.add(new Arista(3, 6, 15));
        listaAristas.add(new Arista(4, 6, 12));
        listaAristas.add(new Arista(4, 7, 8));
        listaAristas.add(new Arista(5, 6, 10));
        listaAristas.add(new Arista(6, 7, 10));
        listaAristas.add(new Arista(6, 8, 9));
        listaAristas.add(new Arista(5, 8, 13));
        listaAristas.add(new Arista(7, 8, 14));

        // Matriz del árbol mínimo
        aristaEnArbolMinimo = new int[cantidadNodos][cantidadNodos];

        // Ejecutar Kruskal y marcar aristas
        ejecutarKruskalSimple();

        // Se asigna la ventana donde se mostrara el grafo
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.cargarGrafo(cantidadNodos, listaAristas, aristaEnArbolMinimo);

        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Si la arista pertenece al conjunto se le asigna un uno
    private void marcarAristaComoParteDelArbolMinimo(int u, int v) {
        aristaEnArbolMinimo[u][v] = 1;
        aristaEnArbolMinimo[v][u] = 1;
    }

    // Ordenar por peso con burbuja
    private void ordenarPorPesoBurbuja(ArrayList<Arista> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            for (int j = 0; j < lista.size() - 1 - i; j++) {
                if (lista.get(j).peso > lista.get(j + 1).peso) {
                    Arista temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
    }

    // empezamos con el metodo KRUSKAL
    private void ejecutarKruskalSimple() {

        // Copia para ordenar sin cambiar la lista original
        ArrayList<Arista> aristasOrdenadas = new ArrayList<>();
        for (Arista a : listaAristas) aristasOrdenadas.add(a);

        ordenarPorPesoBurbuja(aristasOrdenadas);

        UnionFind uf = new UnionFind(cantidadNodos);

        int sumaTotal = 0;
        int aristasAgregadas = 0;

        System.out.println("\n=== KRUSKAL ===");
        System.out.println("Aristas ordenadas por peso:");
        for (Arista a : aristasOrdenadas) System.out.println("  " + a);

        System.out.println("\nSelección:");

        for (Arista a : aristasOrdenadas) {
            int u = a.origen - 1;
            int v = a.destino - 1;

            // Si no forma ciclo, se agrega
            if (uf.union(u, v)) {
                marcarAristaComoParteDelArbolMinimo(u, v);
                sumaTotal += a.peso;
                aristasAgregadas++;
                System.out.println("Escogido: " + a);
            } else {
                System.out.println("Descartado: " + a);
            }

            if (aristasAgregadas == cantidadNodos - 1) break;
        }

        if (aristasAgregadas == cantidadNodos - 1) {
            System.out.println("\nPeso total del Árbol Mínimo = " + sumaTotal);
        } else {
            System.out.println("\nEl grafo no es conexo (no hay AEM).");
        }

        System.out.println("====!!=====!!=====!!======!!===\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArbolMinimo().setVisible(true));
    }
}
