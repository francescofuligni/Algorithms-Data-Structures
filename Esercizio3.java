/**
 * AUTORE
 * Francesco Maria Fuligni
 * Matricola 0001068987 - Università di Bologna
 * francesco.fuligni@studio.unibo.it
 */

/**
 * DESCRIZIONE DEL CODICE
 * L'algoritmo legge da riga di comando il nome del file contenente
 * la rete stradale e restituisce il percorso più breve per raggiungere
 * la destinazione (nodo n-1) dalla sorgente (nodo 0).
 * - Graph memorizza il grafo che rappresenta la rete stradale.
 * - Edge memorizza il singolo arco orientato e pesato.
 * - Dijkstra memorizza una sorgente e una destinazione, calcola
 *   il percorso più breve con il metodo findPath() e lo stampa con printPath().
 * - findPath() sfrutta l'algoritmo di Dijkstra, troncato al raggiungimento
 *   della destinazione, per trovare il percorso più breve dalla sorgente alla
 *   destinazione. Se tale percorso non viene trovato (cioè t[dst]=infinito),
 *   printPath() stampa "non raggiungibile".
 * - PriorityElement costituisce il singolo elemento della PriorityQueue
 *   utilizzata nell'algorimto di Dijkstra.
 */

/**
 * ANALISI COSTO COMPUTAZIONALE
 * Nel caso pessimo, l'algoritmo in findPath() deve visitare tutti i nodi del grafo
 * per trovare il nodo destinazione, oppure la destinazione non appartiene alla
 * stessa componente connessa della sorgente (e quindi non è raggiungibile).
 * Data una componente connessa, cui appartiene la sorgente, composta da n nodi, 
 * il costo dell'algoritmo equivale al costo dell'algoritmo di Dijkstra tradizionale
 * (poiché deve esplorare tutto il grafo, o comunque tutta la componente, per terminare).
 * -> O(m*log(n)) per un grafo con n nodi e m archi.
 */

// java Esercizio3 rete_stradale.txt


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

public class Esercizio3 {
    static Graph graph;                 // Grafo contenente la rete stradale
    static int n;                       // Numero di nodi (incroci)
    static int m;                       // Numero di archi (strade)

    /**
     * Struttura dati per memorizzare la rete stradale.
     * Il grafo viene memorizzato come lista di adiacenza.
     */
    private static class Graph {
        private Vector<LinkedList<Edge>> adjList;   // Lista di adiacenza


        /* Memorizza gli archi pesati orientati del grafo (rete stradale). */
        private class Edge {
            private int first;
            private int second;
            private double weight;

            public Edge(int first, int second, double weight) {
                this.first = first;
                this.second = second;
                this.weight = weight;
            }
            
            public double getWeight() {
                return this.weight;
            }
            
            @SuppressWarnings("unused")
            public int getFirstVertex() {
                return this.first;
            }
            
            public int getSecondVertex() {
                return this.second;
            }
            
            @Override
            public String toString() {
                return this.first + " --> " + this.second + " [" + this.weight + "]";
            }
        }


        public Graph() {
            this.adjList = new Vector<>(n);
            for (int i=0; i<n; i++) {
                this.adjList.add(i, new LinkedList<Edge>() );
            }
        }
        
        public void addEdge(int first, int second, double d) {
            Edge edge = new Edge(first, second, d);
            adjList.get(first).add(edge);
        }

        public List<Edge> getEdges(int first) {
            return this.adjList.get(first);
        }
        
        @Override
        public String toString() {
            String s = "{ ";
            Set<Edge> allEdges = new HashSet<>();
            for(List<Edge> edges : this.adjList) {
                allEdges.addAll(edges);
            }
            String sep = "";
            for(Edge edge : allEdges) {
                s += sep + edge;
                sep = ", ";
            }
            return s + " }";
        }
    }



    /**
     * Dati un grafo, una sorgente e una destinazione,
     * stampa il percorso più breve per raggiungere
     * la destinazione dalla sorgente (se possibile).
     */
    private static class Dijkstra {
        private final int src, dst, MATR;           // Sorgente, destinazione, numero di matricola
        private Random rand;                        // Generatore di numeri (pseudo)casuali per il metodo attesa
        private PriorityQueue<PriorityItem> queue;  // Coda con priorità
        private boolean[] added;                    // Array di nodi aggiunti al percorso
	    private double[] t;                         // Array di distanze (in tempo) dalla sorgente
        private int[] pred;                         // Array di nodi predecessori


        /* Elemento della coda con priorità. */
        private class PriorityItem implements Comparable<PriorityItem> {
            int value;
            double priority;

            PriorityItem(int value, double priority) {
                this.value = value;
                this.priority = priority;
            }

            public int compareTo(PriorityItem other) {
                return Double.compare(this.priority, other.priority);
            }
        }


        public Dijkstra(int src, int dst) {
            this.src = src;
            this.dst = dst;
            this.MATR = 1068987;

            this.rand = new Random(MATR);        // Seed fissato numero di matricola
            //this.rand = new Random(10000);     // Seed fissato 10_000

            this.queue = new PriorityQueue<>();
            this.added = new boolean[n];
	        this.t = new double[n];
            this.pred = new int[n];

            findPath();     // Calcola il cammino di costo minimo
        }

        /**
         * Sfrutta l'algoritmo di Dijkstra per trovare
         * il cammino più breve da un dato nodo sorgente
         * (src) a un dato nodo destinazione (dst).
         */
        private void findPath() {
            Arrays.fill(t, Double.POSITIVE_INFINITY);
            Arrays.fill(pred, -1);
            Arrays.fill(added, false);
            t[src] = 0.0;
    
            for (int v=0; v<n; v++) {
                queue.add(new PriorityItem(v, t[v]));   // insert
            }
            
            while (!queue.isEmpty()) {
                int u = queue.poll().value;     // find e deleteMin
                if(u == dst) {
                    break;
                }
                added[u] = true;
                for (Graph.Edge edge : graph.getEdges(u)) {
                    int v = edge.getSecondVertex();
                    double newTime = t[u] + edge.getWeight() + attesa(v, t[u] + edge.getWeight());
                    if (!added[v] && (newTime < t[v])) {
                        t[v] = newTime;
                        queue.add(new PriorityItem(v, t[v]));   // decreaseKey
                        pred[v] = u;
                    }
                }
            }
        }

        /**
         * Ritorna il tempo di attesa in corrispondenza di
         * un nodo i raggiunto all'istante di tempo t.
         * I commenti contengono varianti del metodo.
         */
        private double attesa(int i, double t) {
            //return rand.nextDouble();
            //return rand.nextDouble()*10;
            return 5.0;
        }

        /* Stampa il percorso più breve e il tempo necessario. */
        public void printPath() {
            // Controlla se la destinazione è raggiungibile.
            if (t[dst] == Double.POSITIVE_INFINITY) {
                System.out.println("NON RAGGIUNGIBILE.");
            } 
            else {
                // Ricostruisce il percorso da src a dst e stampa.
                String path = "";
                for (int node=dst; node!=-1; node=pred[node]) {
                    path = node + " " + path;
                }
                System.out.printf("%.2f\n",t[dst]);
                System.out.println(path);
            }
        }
    }



    public static void main(String args[]) {
        if (args.length != 1) {
            System.err.println("Necessario come parametro il nome del file contenente la rete stradale.");
            return;
        }
        Locale.setDefault(Locale.US);

        try {
            // Legge il file contenente la rete stradale.
            File file = new File(args[0]);
            Scanner scan = new Scanner(file);

            n = Integer.parseInt(scan.nextLine());
            m = Integer.parseInt(scan.nextLine());
            graph = new Graph();

            while(scan.hasNextLine()) {
                String[] tokens = scan.nextLine().split(" ");
                graph.addEdge(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Double.parseDouble(tokens[2]));
            }
            scan.close();

            Dijkstra d = new Dijkstra(0, n-1);
            d.printPath();
        } catch(FileNotFoundException e) {
            System.err.println("FILE NOT FOUND: " + e);
            e.printStackTrace();
        }
    }
}