/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "inserire il proprio nome"
Cognome: "inserire il proprio cognome"
Username: la.trani@studenti.unina.it
UserID: 1014
Date: 25/11/2025
*/
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTestTennisScoreManager_P1014_G1271R1 {

    private TennisScoreManager manager;

    @Before
    public void setUp() {
        manager = new TennisScoreManager();
    }

    // --- TEST DI BASE E STATO INIZIALE ---

    @Test
    public void testInitialState() {
        assertEquals("Love-Love", manager.getGameScore());
        assertTrue(manager.getMatchScore().contains("0-0"));
        assertFalse(manager.isGameOver());
    }

    @Test
    public void testStandardScoringP1() {
        manager.pointScored(1); // 15
        assertEquals("15-Love", manager.getGameScore());
        manager.pointScored(1); // 30
        assertEquals("30-Love", manager.getGameScore());
        manager.pointScored(1); // 40
        assertEquals("40-Love", manager.getGameScore());
    }

    // --- COPERTURA BOOLEANA AVANZATA: DEUCE E VANTAGGI ---

    /**
     * Copre parzialmente: if (scoreP1 == scoreP2 && scoreP1 >= 3)
     * Caso: Punteggi uguali ma < 3 (es. 15-15, 30-30).
     */
    @Test
    public void testPartialCondition_PreDeuceEquality() {
        // 15-15
        manager.pointScored(1);
        manager.pointScored(2);
        assertEquals("15-15", manager.getGameScore());
        
        // 30-30
        manager.pointScored(1);
        manager.pointScored(2);
        assertEquals("30-30", manager.getGameScore());
    }

    /**
     * Copre totalmente: if (scoreP1 == scoreP2 && scoreP1 >= 3)
     */
    @Test
    public void testDeuceLogic() {
        generateDeuce(); // 40-40 (3-3)
        assertEquals("Deuce", manager.getGameScore());
    }

    /**
     * Copre: if (scoreP1 >= 3 && scoreP1 == scoreP2 + 1)
     * Verifica entrata e rientro dal vantaggio.
     */
    @Test
    public void testAdvantageP1_Extended() {
        generateDeuce(); // 3-3
        manager.pointScored(1); // 4-3 -> Vantaggio P1
        assertEquals("Vantaggio P1", manager.getGameScore());

        // Torna a Deuce
        manager.pointScored(2); // 4-4
        assertEquals("Deuce", manager.getGameScore());

        // Di nuovo vantaggio P1 (5-4) -> Copre scoreP1 > 3
        manager.pointScored(1); 
        assertEquals("Vantaggio P1", manager.getGameScore());
    }

    /**
     * Copre: if (scoreP2 >= 3 && scoreP2 == scoreP2 + 1)
     * Questa condizione nel codice sorgente è BUGGATA (x == x + 1 è impossibile),
     * ma il test forza l'esecuzione a valutare l'IF passando un punteggio dove scoreP2 >= 3.
     */
    @Test
    public void testAdvantageP2_CoverageOfBuggyLine() {
        generateDeuce();
        manager.pointScored(2); // P2 va in vantaggio (Internamente scoreP2=4, scoreP1=3)
        // Poiché l'IF è errato, cade nel return finale
        assertEquals("Errore Game", manager.getGameScore());
    }

    // --- COPERTURA LOGICA VITTORIA GAME E SET ---

    @Test
    public void testWinGameP1() {
        winGame(1);
        assertEquals("Love-Love", manager.getGameScore());
        assertTrue(manager.getMatchScore().contains("Game: 1-0"));
    }

    /**
     * Copre logica set P1: (gamesP1 == 7 && gamesP2 == 5)
     */
    @Test
    public void testWinSetP1_7_5_Logic() {
        // 5-5
        winSet(1, 5);
        winSet(2, 5);
        
        // 6-5
        winGame(1);
        assertTrue(manager.getMatchScore().contains("Game: 6-5"));

        // 7-5 -> Vittoria Set
        winGame(1);
        
        String matchScore = manager.getMatchScore();
        assertTrue("Set deve essere 1-0", matchScore.startsWith("1-0"));
        assertTrue("Game resettati", matchScore.contains("Game: 0-0"));
    }

    /**
     * Copre logica set P2: else if (gamesP2 >= 6 && gamesP2 >= gamesP1 + 2 ...)
     * Scenario: 6-4.
     */
    @Test
    public void testWinSetP2_6_4_ComplexLogic() {
        winSet(1, 4);
        winSet(2, 4); 
        winGame(2); // 5-4
        winGame(2); // 6-4 -> Vince Set

        String matchScore = manager.getMatchScore();
        assertTrue("Set deve essere 0-1", matchScore.startsWith("0-1"));
    }

    /**
     * Copre logica set P2: ... || (gamesP2 == 7 && gamesP1 == 5) ...
     * Scenario: 7-5.
     */
    @Test
    public void testWinSetP2_7_5_Logic() {
        winSet(1, 5);
        winSet(2, 5);
        winGame(2); // 6-5
        winGame(2); // 7-5 -> Vince Set
        
        String matchScore = manager.getMatchScore();
        assertTrue("Set deve essere 0-1", matchScore.startsWith("0-1"));
    }

    // --- COPERTURA TIE-BREAK E BUG RELATIVI ---

    @Test
    public void testTieBreakTrigger() {
        forceTieBreak(); // 6-6
        assertTrue(manager.getMatchScore().contains("TIE-BREAK"));
    }

    /**
     * Copre: if (scoreP1 >= 7 && scoreP1 >= scoreP2 + 2) in checkTieBreakPoint.
     * Asserisce il BUG: set non assegnato e game resettati.
     */
    @Test
    public void testTieBreakWinP1_Coverage() {
        forceTieBreak();
        for(int i=0; i<7; i++) manager.pointScored(1);
        
        String score = manager.getMatchScore();
        assertTrue(score.startsWith("0-0"));
    }

    /**
     * Copre: else if (scoreP2 >= 7 && scoreP2 >= scoreP1 + 2)
     * Scenario: 8-6 per P2.
     */
    @Test
    public void testTieBreakWinP2_Extended_8_6() {
        forceTieBreak();
        
        // 6-6 nel tie break
        for(int i=0; i<6; i++) {
            manager.pointScored(1);
            manager.pointScored(2);
        }
        
        // P2 vince (8-6)
        manager.pointScored(2);
        manager.pointScored(2);
        
        // Verifica esecuzione ramo else if (anche se risultato buggato)
        String score = manager.getMatchScore();
        assertTrue("Game resettati (bug)", score.contains("Game: 0-0"));
    }

    // --- COPERTURA VITTORIA MATCH E GAME OVER ---

    /**
     * Copre: if (setsWonP1 == 3)
     */
    @Test
    public void testMatchWinP1_FullCoverage() {
        winSet(1, 6);
        winSet(1, 6);
        winSet(1, 6);
        
        assertTrue(manager.isGameOver());
        assertTrue(manager.getMatchScore().contains("P1: 3 Set"));
    }

    /**
     * Copre: if (setsWonP2 == 3)
     */
    @Test
    public void testMatchWinP2_FullCoverage() {
        // Usiamo 6-4 per P2 per evitare il bug del tie break
        winSet(1, 4); winSet(2, 6);
        winSet(1, 4); winSet(2, 6);
        winSet(1, 4); winSet(2, 6);
        
        assertTrue(manager.isGameOver());
        assertTrue(manager.getMatchScore().contains("P2: 3 Set"));
    }

    /**
     * Copre il blocco:
     * if (isGameOver()) { System.out.println(...); return; }
     * Verifica che chiamare pointScored DOPO la fine non cambi nulla.
     */
    @Test
    public void testBehaviorAfterGameOver() {
        // Vinciamo la partita con P1 (3 set a 0)
        winSet(1, 6);
        winSet(1, 6);
        winSet(1, 6);

        assertTrue("La partita deve essere finita", manager.isGameOver());
        
        // Memorizziamo il punteggio finale
        String finalScore = manager.getMatchScore();
        
        // Proviamo a segnare un altro punto
        manager.pointScored(1);
        
        // Verifichiamo che il punteggio sia identico (il metodo ha fatto return)
        assertEquals(finalScore, manager.getMatchScore());
        
        // Proviamo con l'altro giocatore
        manager.pointScored(2);
        assertEquals(finalScore, manager.getMatchScore());
    }
    
    // --- TEST INPUT INVALIDI ---
    
    @Test
    public void testInvalidInput() {
        manager.pointScored(99);
        assertEquals("Love-Love", manager.getGameScore());
    }

    // --- HELPER METHODS ---

    private void winGame(int player) {
        for (int i = 0; i < 4; i++) {
            manager.pointScored(player);
        }
    }

    private void winSet(int player, int games) {
        for (int i = 0; i < games; i++) {
            winGame(player);
        }
    }

    private void generateDeuce() {
        manager.pointScored(1); // 15
        manager.pointScored(2); // 15
        manager.pointScored(1); // 30
        manager.pointScored(2); // 30
        manager.pointScored(1); // 40
        manager.pointScored(2); // 40 - Deuce
    }

    private void forceTieBreak() {
        winSet(1, 5);
        winSet(2, 5);
        winGame(1); // 6-5
        winGame(2); // 6-6
    }
}