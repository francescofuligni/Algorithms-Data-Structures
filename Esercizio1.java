/**
 * AUTORE
 *  Francesco Maria Fuligni
 *  Matricola 0001068987 - Università di Bologna
 *  francesco.fuligni@studio.unibo.it
 */

/**
 * DESCRIZIONE DEL CODICE
 * L'algortimo legge da riga di comando i nomi dei due file contenenti gli alberi
 * e li confronta.
 * - buldPairsTree() costruisce l'albero dal file contenente le coppie padre figlio.
 * - buildNestedTree() costruisce l'albero dal file contenente le liste annidate.
 * - Tree memorizza i due alberi e Node il singolo nodo con l'insieme dei propri figli.
 * - Il confronto viene eseguito tramite i metodi equals() ridefiniti di Tree e Node.
 * - hashCode() ridefinito per Node permette il corretto funzionamento del metodo 
 *   equals() di HashSet, che sftutta l'hashing per trovare nodi con lo stesso valore
 *   nel confronto tra i set di figli.
 */

/**
 * ANALISI COSTO COMPUTAZIONALE
 * Il caso pessimo si ha quando i due alberi sono uguali: bisogna confrontare tutti i nodi.
 * - Il confronto tra i valori dei nodi ha costo costante.
 * - Il confronto tra i set di figli sfrutta il metodo .equals() di HashSet, che controlla
 *   prima se i due set hanno la stessa dimensione e poi se i due set contengono gli stessi
 *   elementi. La ricerca di un elemento in un HashSet avviene (in media) in tempo costante,
 *   grazie all'utilizzo della tabella Hash. Dunque, per nodi con k figli il metodo equals()
 *   ha costo k*cost = O(k).
 * - Ogni volta che il metodo equals() di HashSet trova un figlio e lo confronta con il
 *   corrispettivo dell'altro HashSet(), richiama l'equals() della classe Node creando il
 *   meccanismo ricorsivo.
 * In questo modo, l'algoritmo confronta ricorsivamente tutti i nodi, portando a un costo
 * computazionale lineare O(n), assumendo che la funzione di Hash efficiente.
 * Tuttavia, in caso di elevate collisioni nell'hashing, il costo può salire fino a O(n^2).
 * 
 * MASTER THEOREM
 * Nel caso medio, in cui si hanno due alberi bilanciati in modo che i nodi abbiano un
 * numero medio di k figli (costante), è possibile definire una relazione di ricorrenza
 * in cui tutti i sottoproblemi hanno la stessa dimensione.
 * La relazione di ricorrenza sarà quindi:
 *  T(n) = cost             se il valore dei nodi è diverso o se è l'ultimo conftonto
 *  T(n) = k*T(n/k) + cost  se bisogna confrontare i set di k filgi
 * 
 * Dal Master Theorem si ha: T(n) = a*T(n/b) + f(n)
 * a = k
 * b = k
 * f(n) = cost
 * L'algoritmo rientra nel caso 1 del Master Theorem, essendo:
 *  log_b(a) = log_k(k) = 1
 *  f(n) = O(n^(1-e)) con e>0
 * Dunque l'algoritmo ha costo computazionale Tetha(n^log_b(a)) = Tetha(n) anche nel
 * caso in cui si abbiano alberi con numero medio di figli k.
 */

// java Esercizio1 parent_child_pairs.txt nested_list.txt


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class Esercizio1 {
    static Tree pairsTree;  // Albero costruito dal file con coppie padre-figlio
    static Tree nestedTree; // Albero costruito dal file con liste annidate


    /* Struttura dati per memorizzare i due alberi. */
    private static class Tree {
        private final Node root;
        

        /* Memorizza un nodo e i suoi figli come un insieme (non ordinato) di nodi. */
        private static class Node {
            private final int value;
            private Set<Node> children;
            
            public Node(int value) {
                this.value = value;
                this.children = new HashSet<>();
            }
            
            public int getValue() {
                return value;
            }
            
            public Set<Node> getChildren() {
                return children;
            }
            
            public void addChild(Node child) {
                this.children.add(child);
            }

            @Override
            public int hashCode() {
                return Integer.hashCode(value);
            }

            /**
             * Confronta ricorsivamente i due nodi
             * e gli insiemi dei figli dei due nodi.
             */
            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof Node)) {
                    return false;
                }

                Node other = (Node) obj;
                return this.value == other.getValue() && this.children.equals(other.getChildren());
            }

            public String toString() {
                return this.getValue()+"";
            }
        }
        

        public Tree(Node root) {
            this.root = root;
        }

        public Node getRoot() {
		    return root;
	    }

        @Override
        public int hashCode() {
            return root.hashCode();
        }

        /**
         * Confronta le radici dei due alberi,
         * richiamando il metodo .equals della classe Node.
         */
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Tree)) {
                return false;
            }
            Tree other = (Tree) obj;
            return this.root.equals(other.getRoot());
        }

        /* Visita in ampiezza (BFS) per stampare l'albero. */
        private String visit() {
            String s = "";
            Queue<Node> queue = new LinkedList<>();
            queue.add(this.root);

            while(!queue.isEmpty()) {
                int levelSize = queue.size();

                for (int i = 0; i < levelSize; i++) {
                    Node current = queue.poll();
                    s = s + current.getValue() + (" ");
                    queue.addAll(current.getChildren());
                }
                s+="\n";
            }
            return s;
        }

        public String toString() {
            return visit();
        }
    }
    
    

    /**
     * Costruisce il primo albero a partire dal file
     * contenente la rappresentazione in forma di coppie padre-figlio.
     */
    public static Tree.Node buildPairsTree(Scanner scan) {
        Map<Integer, Tree.Node> nodes = new HashMap<>();
        Tree.Node root = null;          // Tiene traccia del nodo radice
    
        while (scan.hasNextLine()) {
            String[] parts = scan.nextLine().split(",");
            int parentValue = Integer.parseInt(parts[0].trim());
            int childValue = Integer.parseInt(parts[1].trim());
    
            Tree.Node parent = nodes.computeIfAbsent(parentValue, Tree.Node::new);
            Tree.Node child = nodes.computeIfAbsent(childValue, Tree.Node::new);
    
            parent.addChild(child);
    
            if (root == null) {
                root = parent;
            } else if (root.equals(child)) {
                root = parent;
            }
        }
        return root;
    }

    

    /**
     * Costruisce il secondo albero a partire dal file
     * contenente la rappresentazione in forma di liste annidate.
     */
    public static Tree.Node buildNestedTree(Scanner scan) {
        if (scan.hasNext("\\[")) {
            scan.next("\\[");
        }

        String s = "";
        while (scan.hasNext("\\d")) {
            s += scan.next("\\d");
        }
        int value = Integer.parseInt(s);
        Tree.Node root = new Tree.Node(value);

        while (scan.hasNext()) {
            char c = scan.findWithinHorizon(".", 1).charAt(0);
            if (c == '[') {
                root.addChild(buildNestedTree(scan));    // Chiamata ricorsiva sul sottoalbero
            } else if (c == ']') {
                break;
            }
        }
        return root;
    }



    /**
     * Nella lettura dei file, si assume che il primo file contenga
     * l'albero in forma di coppie padre-figlio e che il secondo
     * contenga l'albero in forma di lista annidata.
     */
    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("Necessari come parametri i nomi dei due file contententi i due alberi.");
            return;
        }
        Locale.setDefault(Locale.US);
        
        try {
            // Legge il primo file (coppie padre-figlio).
            File file1 = new File(args[0]);
            Scanner scan1 = new Scanner(file1);
            
            pairsTree = new Tree(buildPairsTree(scan1));
            scan1.close();
        
            // Legge il secondo file (liste annidate).
            File file2 = new File(args[1]);
            Scanner scan2 = new Scanner(file2);
            String line = scan2.nextLine();
            scan2.close();

            line = line.replaceAll(" ", "");   // Rimozione spazi
            Scanner lineScan = new Scanner(line);
            lineScan.useDelimiter("");
            nestedTree = new Tree(buildNestedTree(lineScan));
            lineScan.close();

            // Confronto tra gli alberi.
            if(pairsTree.equals(nestedTree)) {
                System.out.println("I due alberi sono UGUALI.");
            } else {
                System.out.println("I due alberi sono DIVERSI.");
            }
        } catch(FileNotFoundException e) {
            System.err.println("FILE NOT FOUND: " + e);
            e.printStackTrace();
        }
    }
}