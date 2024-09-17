/**
 * AUTORE
 * Francesco Maria Fuligni
 * Matricola 0001068987 - Università di Bologna
 * francesco.fuligni@studio.unibo.it
 */

/**
 * DESCRIZIONE DEL CODICE
 * L'algoritmo legge in input il nome del file contenente la stringa
 * da analizzare e restituisce tutte le possibili decodifiche.
 * - setEncodings() inizializza il cifrario, utilizzando un dizionario che
 *   garantisce l'accesso agli elementi in tempo costante.
 * - findDecodings() calcola tutte le possibili decodifiche della stringa
 *   s data in input, con l'utilizzo di una lista di set per memorizzare
 *   i prefissi trovati durante le iterazioni e concatenarli. Le decodifiche
 *   finali si trovano nell'ultimo set, restituita dal metodo come output.
 *   L'utilizzo di Set è funzionale per evitare la memorizzazione di
 *   decodifiche duplicate.
 */

/**
 * ANALISI COSTO COMPUTAZIONALE
 * Nel metodo findDecodings():
 * - il ciclo for esterno itera su ognuno degli n caratteri della stringa s,
 *   quindi ha costo O(n);
 * - il primo ciclo for annidato itera al massimo maxLength volte, dove
 *   maxLength è il numero massimo di cifre che corrispondono a una decodifica
 *   nel cifrario. Questo valore è costante rispetto a n, per n molto grandi
 *   (per il cifrario dato, si ha maxLength = 4);
 * - il secondo ciclo for annidato si occupa di concatenare le decodifiche
 *   tra loro. Questa operazione, nel caso pessimo in cui tutti i prefissi
 *   generino decodifiche valide, costa O(n).
 * Dunque, il costo di questo algoritmo è 4*n*n = O(n^2) nel caso pessimo.
 * In aggiunta, viene effettuato un ordinamento sulle decodifiche trovate,
 * con il metodo Collections.sort(). Tale metodo ordina i k elementi della
 * lista con costo k*log(k), che rimane di ordine inferiore rispetto a O(n^2).
 */

// java Esercizio2 stringa.txt


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Esercizio2 {
    static Map<String, String> encodings;   // Cifrario contenente le codifiche dei caratteri
    static String s = "";                   // Stringa da decodificare        
    static int maxLength;                   // Lunghezza massima di una stringa di cifre decodificabile


    /* Inizializza le codifiche per ogni carattere. */
    public static void setEncodings() {
        encodings = new HashMap<>();
        encodings.put("0", "A");
        encodings.put("00", "B");
        encodings.put("001", "C");
        encodings.put("010", "D");
        encodings.put("0010", "E");
        encodings.put("0100", "F");
        encodings.put("0110", "G");
        encodings.put("0001", "H");

        // Calcola la lunghezza massima di una stringa decodificabile.
        for (String key : encodings.keySet()) {
            maxLength = Math.max(maxLength, key.length());
        }
    }



    /**
     * Trova tutte le possibili decodifiche per la stringa S. 
     * Iterando per tutte le possibili sottostringhe, decodings
     * viene riempita con tutte le decodifiche trovate.
     * L'utilizzo di Set permette di evitare decodifiche doppie.
     * L'utilizzo della variabile maxLength limita la ricerca
     * del prefisso al massimo di cifre decodificabili.
     */
    public static Set<String> findDecodings() {
        int length = s.length();
        List<Set<String>> decodings = new ArrayList<>();

        for(int i=0; i<=length; i++) {
            decodings.add(new HashSet<>());
        }

        // Caso base: la stringa vuota non ha nessuna decodifica.
        if(s=="") {
            return decodings.get(length);
        }

        // Caso generale: itera per tutte le possibili sottostringhe.
        decodings.get(0).add("");
        for (int i=1; i<=length; i++) {
            for (int j=1; j<=Math.min(i, maxLength); j++) {
                String substring = s.substring(i - j, i);

                if (encodings.containsKey(substring)) {
                    String character = encodings.get(substring);
                    
                    // Aggiunge il carattere alla fine delle decodifiche precedenti.
                    for (String prevDec : decodings.get(i-j)) {
                        decodings.get(i).add(prevDec + character);
                    }
                }
            }
        }
        
        // Le decodifiche finali si trovano nell'ultima lista.
        return decodings.get(length);
    }

    

    public static void main(String args[]) {
        if (args.length != 1) {
            System.err.println("Necessario come parametro il nome del file contenente la stringa.");
            return;
        }
        Locale.setDefault(Locale.US);
        
        // Legge la stringa da file.
        try {
            File file = new File(args[0]);
            Scanner scan = new Scanner(file);
            if(scan.hasNextLine()) {
                s = scan.nextLine();
                s = s.replaceAll(" ", "");
            }
            scan.close();

            setEncodings();
            List<String> decoding = new ArrayList<>();
            decoding.addAll(findDecodings());
            Collections.sort(decoding);     // Ordinamento per stampa in ordine alfabetico

            // Stampa tutte le decodifiche trovate.
            System.out.println(decoding.size());
            for(String d : decoding) {
                System.out.println(d);
            }
        } catch(FileNotFoundException e) {
            System.err.println("FILE NOT FOUND: " + e);
            e.printStackTrace();
        }
    }
}