/*
 * Nome: [Tuo Nome]
 * Cognome: [Tuo Cognome]
 * Matricola: [Tua Matricola]
 */

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTestTennisScoreManager_P249_G1130R1 {

    private TennisScoreManager manager;

    @Before
    public void setUp() {
        manager = new TennisScoreManager();
    }

    // --- TEST BASE & PUNTEGGIO ---

    @Test
    public void testInitialScore() {
        // Verifica lo stato iniziale
        assertEquals("Love-Love", manager.getGameScore());
        assertTrue(manager.getMatchScore().contains("0-0"));
    }

    @Test
    public void testStandardScoringSequence() {
        // P1: 15
        manager.pointScored(1);
        assertEquals("15-Love", manager.getGameScore());
        
        // P2: 15
        manager.pointScored(2);
        assertEquals("15-15", manager.getGameScore());
        
        // P1: 30
        manager.pointScored(1);
        assertEquals("30-15", manager.getGameScore());
        
        // P1: 40
        manager.pointScored(1);
        assertEquals("40-15", manager.getGameScore());
    }

    @Test
    public void testDeuce() {
        // Porta a 40-40 (3 punti a testa)
        generatePoints(3, 3);
        assertEquals("Deuce", manager.getGameScore());
    }

    @Test
    public void testAdvantagePlayer1() {
        // Deuce
        generatePoints(3, 3);
        // P1 segna -> Vantaggio P1
        manager.pointScored(1);
        assertEquals("Vantaggio P1", manager.getGameScore());
    }

    // --- TEST CODICE MORTO / IMPOSSIBILE ---

    @Test
    public void testDeadCodeAdvantagePlayer2() {
        /*
         * OBIETTIVO: Coprire il ramo 'if (scoreP2 >= 3 && scoreP2 == scoreP2 + 1)'
         * * ANALISI BUG: La condizione `scoreP2 == scoreP2 + 1` è matematicamente impossibile.
         * Nel codice, questo blocco dovrebbe gestire "Vantaggio P2".
         * Poiché la condizione è falsa, il codice cade nel return finale "Errore Game".
         * Questo test porta il gioco in stato di Vantaggio P2 e asserisce che il metodo
         * restituisca "Errore Game" invece di "Vantaggio P2".
         */
        generatePoints(3, 4); // P1: 40, P2: Vantaggio (tecnicamente)
        
        // Assertiamo il fallback del bug
        assertEquals("Errore Game", manager.getGameScore());
    }

    @Test
    public void testInvalidPlayerInput() {
        // Copertura ramo else in pointScored
        String scoreBefore = manager.getGameScore();
        manager.pointScored(99); // Input non valido
        assertEquals(scoreBefore, manager.getGameScore()); // Il punteggio non deve cambiare
    }

    // --- TEST LOGICA GAME E SET (STANDARD) ---

    @Test
    public void testWinStandardGameP1() {
        // P1 vince a zero
        winGame(1);
        // Verifica dal Match Score che P1 ha 1 game
        assertTrue(manager.getMatchScore().contains("1-0"));
        // I punti devono essere resettati
        assertEquals("Love-Love", manager.getGameScore());
    }
    
    @Test
    public void testWinStandardGameP2() {
        winGame(2);
        assertTrue(manager.getMatchScore().contains("0-1"));
    }

    @Test
    public void testWinSetStandard() {
        // P1 vince 6 game di fila (6-0)
        for (int i = 0; i < 6; i++) {
            winGame(1);
        }
        
        // Verifica che il set sia stato assegnato
        // getMatchScore mostrerà "1-0 (Game: 0-0 ...)" perché siamo nel set 2
        String score = manager.getMatchScore();
        // Regex semplice o controllo stringa per vedere se i set vinti da P1 sono 1
        // Formato atteso: "1-0 (Game: 0-0 Love-Love)"
        assertTrue("Il set dovrebbe essere vinto da P1", score.startsWith("1-0"));
    }

    // --- TEST TIE-BREAK E BUG CRITICO ---

    @Test
    public void testTieBreakEntry() {
        playToSixAll(); // Helper per arrivare a 6-6
        
        // Verifica attivazione flag TieBreak e output
        String matchScore = manager.getMatchScore();
        assertTrue(matchScore.contains("TIE-BREAK"));
        assertEquals("TIE-BREAK: 0-0", manager.getTieBreakScore());
    }

    @Test
    public void testBuggyTieBreakWinDoesNotAwardSet() {
        /*
         * OBIETTIVO: Documentare il bug nel metodo checkTieBreakPoint.
         * * ANALISI BUG: Quando un giocatore vince il TieBreak, viene chiamato:
         * 1. resetGameAndPoints() -> azzera gamesP1 e gamesP2 a 0.
         * 2. checkSetPoint() -> controlla se gamesP1 == 6 o 7.
         * Poiché i game sono stati azzerati PRIMA del controllo, il set non viene mai assegnato.
         */
        
        playToSixAll(); // Siamo 6-6
        
        // P1 vince il tie break (7 punti a 0)
        for (int i = 0; i < 7; i++) {
            manager.pointScored(1);
        }

        String finalScore = manager.getMatchScore();
        
        // COMPORTAMENTO ATTESO (BUGGATO): 
        // Il set non è incrementato. Siamo ancora nel set 1 (o il contatore set vinti è 0-0).
        // I game sono resettati a 0-0.
        // Se funzionasse, inizierebbe con "1-0". Siccome è buggato, inizia con "0-0".
        assertTrue("BUG: Il set non dovrebbe essere assegnato a causa del reset prematuro", 
                   finalScore.startsWith("0-0"));
                   
        // Verifica ulteriore: siamo tornati a Love-Love ma il set non è avanzato correttamente nel punteggio totale
        assertEquals("Love-Love", manager.getGameScore());
    }

    // --- TEST GAME OVER & TRANSIZIONI ---

    @Test
    public void testIsGameOverTransition() {
        // Verifica iniziale
        assertFalse(manager.isGameOver());

        // P1 vince 3 set. 
        // NOTA: Usiamo la vittoria standard (6-0) perché il TieBreak è buggato e non assegna set.
        winSetStandard(1); // Set 1
        assertFalse(manager.isGameOver());
        
        winSetStandard(1); // Set 2
        assertFalse(manager.isGameOver());
        
        winSetStandard(1); // Set 3 -> Vittoria
        
        assertTrue("La partita dovrebbe essere finita dopo 3 set vinti", manager.isGameOver());
        assertEquals("PARTITA FINITA", manager.getGameScore());
        assertEquals("P1: 3 Set | P2: 0 Set", manager.getMatchScore());
    }
    
    @Test
    public void testMatchEndMessageP2() {
        // P2 vince 3 set
        winSetStandard(2);
        winSetStandard(2);
        winSetStandard(2);
        
        assertTrue(manager.isGameOver());
        assertTrue(manager.getMatchScore().contains("P2: 3 Set"));
    }

    @Test
    public void testPointScoredAfterGameOver() {
        // Vinci la partita
        winSetStandard(1);
        winSetStandard(1);
        winSetStandard(1);
        
        // Prova a segnare un altro punto
        String finalScore = manager.getMatchScore();
        manager.pointScored(1); // Dovrebbe stampare a video e fare return
        
        // Il punteggio non deve cambiare
        assertEquals(finalScore, manager.getMatchScore());
    }
    
    // --- TEST LOGICA SET POINT (Rami complessi) ---
    
    @Test
    public void testWinSet7_5() {
        // Simula 5-5
        for(int i=0; i<5; i++) { winGame(1); winGame(2); }
        
        // P1 va a 6-5
        winGame(1);
        // P1 va a 7-5 -> Vince il set
        winGame(1);
        
        // Verifica assegnazione set
        assertTrue(manager.getMatchScore().startsWith("1-0"));
    }
    
    @Test
    public void testWinSet7_5_Player2() {
        // Simula 5-5
        for(int i=0; i<5; i++) { winGame(1); winGame(2); }
        
        // P2 va a 5-6
        winGame(2);
        // P2 va a 5-7 -> Vince il set
        winGame(2);
        
        assertTrue(manager.getMatchScore().startsWith("0-1"));
    }
    
    // Test copertura ramo (gamesP2 == 7 && gamesP2 == 6) in checkSetPoint
    // Questa condizione è impossibile in Java per un intero, ma fa parte dell'OR
    // Copriamo l'OR raggiungendo la condizione di vittoria P2 standard.
    
    // --- HELPER METHODS ---

    private void generatePoints(int p1, int p2) {
        for (int i = 0; i < p1; i++) manager.pointScored(1);
        for (int i = 0; i < p2; i++) manager.pointScored(2);
    }

    private void winGame(int player) {
        // Vince un game a zero (4 punti consecutivi)
        for (int i = 0; i < 4; i++) {
            manager.pointScored(player);
        }
    }

    private void winSetStandard(int player) {
        // Vince 6 game a 0
        for (int i = 0; i < 6; i++) {
            winGame(player);
        }
    }

    private void playToSixAll() {
        // P1 vince 5 game
        for (int i = 0; i < 5; i++) winGame(1);
        // P2 vince 6 game (5-6)
        for (int i = 0; i < 6; i++) winGame(2);
        // P1 vince 1 game (6-6) -> Tie Break
        winGame(1);
    }
}