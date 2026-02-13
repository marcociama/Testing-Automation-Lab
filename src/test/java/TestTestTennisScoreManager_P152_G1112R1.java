import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTestTennisScoreManager_P152_G1112R1 {

    private TennisScoreManager manager;

    @Before
    public void setUp() {
        manager = new TennisScoreManager();
    }

    // --- HELPER METHODS ---

    private void playerWinsGame(int player) {
        for (int i = 0; i < 4; i++) {
            manager.pointScored(player);
        }
    }

    private void playerWinsSet(int player) {
        for (int i = 0; i < 6; i++) {
            playerWinsGame(player);
        }
    }

    private void setupTieBreak() {
        for(int i=0; i<5; i++) { playerWinsGame(1); playerWinsGame(2); }
        playerWinsGame(1); // 6-5
        playerWinsGame(2); // 6-6
    }

    // --- TEST pointScored (Copertura Branch Completa) ---

    @Test
    public void testPointScored_Branches() {
        // 1. Branch: isGameOver = True
        playerWinsSet(1); playerWinsSet(1); playerWinsSet(1); // Match Over
        manager.pointScored(1);
        assertTrue(manager.isGameOver());

        // Reset per altri branch
        setUp(); 

        // 2. Branch: Invalid Player (else finale)
        String scoreBefore = manager.getGameScore();
        manager.pointScored(3);
        assertEquals(scoreBefore, manager.getGameScore());

        // 3. Branch: Player 1 (checkGamePoint -> False)
        manager.pointScored(1);
        assertEquals("15-Love", manager.getGameScore());

        // 4. Branch: Player 2 (checkGamePoint -> False)
        manager.pointScored(2);
        assertEquals("15-15", manager.getGameScore());

        // 5. Branch: isTieBreak = True (Player 1)
        setupTieBreak();
        manager.pointScored(1); // TieBreak Logic P1
        assertTrue(manager.getMatchScore().contains("TIE-BREAK: 1-0"));

        // 6. Branch: isTieBreak = True (Player 2)
        manager.pointScored(2); // TieBreak Logic P2
        assertTrue(manager.getMatchScore().contains("TIE-BREAK: 1-1"));
    }

    // --- TEST getGameScore (Copertura Branch Booleani) ---

    @Test
    public void testGameScore_Branches() {
        // 1. Branch: isGameOver
        playerWinsSet(1); playerWinsSet(1); playerWinsSet(1);
        assertEquals("PARTITA FINITA", manager.getGameScore());
        
        setUp(); // Reset

        // 2. Branch: Standard Score (scoreP1 < 4 && scoreP2 < 4 && (scoreP1 != 3 || scoreP2 != 3))
        // Caso A: 40-0 (3-0). (3!=3 False || 0!=3 True) -> True. 
        manager.pointScored(1); manager.pointScored(1); manager.pointScored(1);
        assertEquals("40-Love", manager.getGameScore());

        // Caso B: 0-40 (0-3). (0!=3 True || 3!=3 False) -> True.
        setUp();
        manager.pointScored(2); manager.pointScored(2); manager.pointScored(2);
        assertEquals("Love-40", manager.getGameScore());

        // 3. Branch: Deuce (scoreP1 == scoreP2 && scoreP1 >= 3)
        // Caso: 40-40. Standard check fallisce (F || F). Deuce check passa.
        manager.pointScored(1); manager.pointScored(1); manager.pointScored(1);
        assertEquals("Deuce", manager.getGameScore());

        // 4. Branch: Vantaggio P1
        manager.pointScored(1);
        assertEquals("Vantaggio P1", manager.getGameScore());

        // 5. Branch: Vantaggio P2 (BUG - CODICE MORTO)
        // Tentiamo di raggiungerlo per 'coprire' il path il più possibile
        manager.pointScored(2); // Torna Deuce
        manager.pointScored(2); // P2 segna -> Dovrebbe essere Adv P2
        // Asseriamo il BUG (Errore Game) invece di Adv P2, perché il ramo True if(AdvP2) è impossibile
        assertEquals("Errore Game", manager.getGameScore());
    }

    // --- TEST checkTieBreakPoint (Copertura Branch) ---

    @Test
    public void testTieBreak_Branches() {
        setupTieBreak();

        // 1. P1 vince (True) - Raggiungibile ma con bug reset
        for(int i=0; i<7; i++) manager.pointScored(1); 
        assertTrue(manager.getMatchScore().contains("Game: 0-0")); // Bug reset confermato

        setUp(); setupTieBreak();

        // 2. P1 in testa ma non vince (False)
        // 6-5: P1 >= 6 (False condition initial check logic inside method is >=7)
        // Facciamo 7-6 per P1: (7>=7 True && 7 >= 8 False)
        for(int i=0; i<6; i++) { manager.pointScored(1); manager.pointScored(2); } // 6-6
        manager.pointScored(1); // 7-6
        assertEquals("TIE-BREAK: 7-6", manager.getTieBreakScore());

        // 3. P2 in testa ma non vince (False second IF)
        // Facciamo 6-7 per P2: (7>=7 True && 7 >= 8 False)
        manager.pointScored(2); // 7-7
        manager.pointScored(2); // 7-8 (P2 leads by 1)
        // scoreP2 (8) >= 7 (True) AND 8 >= 9 (False)
        assertEquals("TIE-BREAK: 7-8", manager.getTieBreakScore());

        // 4. P2 vince (True second IF)
        manager.pointScored(2); // 7-9 (Win)
        assertTrue(manager.getMatchScore().contains("Game: 0-0")); // Bug reset
    }

    // --- TEST checkSetPoint (Copertura Branch) ---

    @Test
    public void testSetPoint_Branches() {
        // 1. P1 Standard Win (Branch 1 OR condition 1)
        playerWinsSet(1);
        assertTrue(manager.getMatchScore().startsWith("1-0"));

        setUp();
        
        // 2. P1 Extended Win 7-5 (Branch 1 OR condition 2)
        for(int i=0; i<5; i++) { playerWinsGame(1); playerWinsGame(2); }
        playerWinsGame(1); playerWinsGame(1);
        assertTrue(manager.getMatchScore().startsWith("1-0"));

        setUp();

        // 3. P2 Standard Win (Branch 2 OR condition 1)
        playerWinsSet(2);
        assertTrue(manager.getMatchScore().startsWith("0-1"));

        setUp();

        // 4. P2 Extended Win 7-5 (Branch 2 OR condition 2)
        for(int i=0; i<5; i++) { playerWinsGame(1); playerWinsGame(2); }
        playerWinsGame(2); playerWinsGame(2);
        assertTrue(manager.getMatchScore().startsWith("0-1"));

        // NOTA: I branch per (games == 7 && games == 6) [Tie Break Win] sono IRRAGGIUNGIBILI
        // a causa del bug in checkTieBreakPoint che resetta i games a 0.
    }

    // --- TEST FINALI DI INTEGRAZIONE E COPERTURA ---

    @Test
    public void testMatchFullSimulation() {
        // Copertura isGameOver branch e getMatchScore loop
        playerWinsSet(1); // 1-0
        playerWinsSet(2); // 1-1
        playerWinsSet(1); // 2-1
        playerWinsSet(2); // 2-2
        playerWinsSet(1); // 3-2 (Win)
        
        assertTrue(manager.isGameOver());
        assertEquals("P1: 3 Set | P2: 2 Set", manager.getMatchScore());
    }
    
    @Test
    public void testPrintScoreCoverage() {
        // Esegue il metodo per coprire le righe di System.out
        manager.printScore();
        setupTieBreak();
        manager.printScore();
    }
}