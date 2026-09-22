package it.unibo.oop.reactivegui03;

/**
 * Third reactive GUI.
 *
 */
public final class Test {

    private Test() {
    }

    /*
     * Realizzare una classe AnotherConcurrentGUI con costruttore privo di argomenti,
     * che aggiunga all'esercizio precedente la seguente funzionalità:
     * - dopo 10 secondi dalla partenza dell'applicazione, i pulsanti si disabilitino
     *   e il conteggio si fermi comunque.
     *   - fare attenzione a non creare corse critiche
     *
     * Suggerimenti:
     * - si usi un ulteriore agente...
     * - si rifattorizzi se necessario/utile la struttura della soluzione
     */

    /**
     * Main method to start the GUI.
     */
    public static void main() {
        new AnotherConcurrentGUI();
    }
}
