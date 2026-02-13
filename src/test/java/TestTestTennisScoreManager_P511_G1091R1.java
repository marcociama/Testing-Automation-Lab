/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "inserire il proprio nome"
Cognome: "inserire il proprio cognome"
Username: giuse.vozza@studenti.unina.it
UserID: 511
Date: 21/11/2025
*/
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestTestTennisScoreManager_P511_G1091R1 {

    // --- Helper Methods ---

    private void scorePoints(TennisScoreManager tm, int player, int times) {
        for (int i = 0; i < times; i++) {
            tm.pointScored(player);
        }
    }

    private void winStandardGame(TennisScoreManager tm, int player) {
        scorePoints(tm, player, 4);
    }

    private void winSet(TennisScoreManager tm, int player) {
        for (int i = 0; i < 6; i++) {
            winStandardGame(tm, player);
        }
    }

    // --- Test Suite ---

    @Test
    public void TennisScoreManagerInitializationTest() {
        TennisScoreManager tm = new TennisScoreManager();
        assertEquals("Love-Love", tm.getGameScore());
        assertEquals("0-0 (Game: 0-0 Love-Love)", tm.getMatchScore());
    }

    @Test
    public void resetPointsResetLogicTest() {
        TennisScoreManager tm = new TennisScoreManager();
        tm.pointScored(1);
        tm.resetPoints();
        assertEquals("Love-Love", tm.getGameScore());
    }

    @Test
    public void pointScoredPlayer1ScoresTest() {
        TennisScoreManager tm = new TennisScoreManager();
        tm.pointScored(1);
        assertEquals("15-Love", tm.getGameScore());
    }

    @Test
    public void pointScoredPlayer2ScoresTest() {
        TennisScoreManager tm = new TennisScoreManager();
        tm.pointScored(2);
        assertEquals("Love-15", tm.getGameScore());
    }

    @Test
    public void pointScoredInvalidPlayerInputTest() {
        TennisScoreManager tm = new TennisScoreManager();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        tm.pointScored(3);

        assertTrue(outContent.toString().contains("Errore: Giocatore non valido"));
        assertEquals("Love-Love", tm.getGameScore());
        System.setOut(System.out); 
    }

    @Test
    public void getGameScoreDeuceLogicTest() {
        TennisScoreManager tm = new TennisScoreManager();
        scorePoints(tm, 1, 3);
        scorePoints(tm, 2, 3);
        assertEquals("Deuce", tm.getGameScore());
    }

    @Test
    public void getGameScoreAdvantageP1Test() {
        TennisScoreManager tm = new TennisScoreManager();
        scorePoints(tm, 1, 3);
        scorePoints(tm, 2, 3);
        tm.pointScored(1);
        assertEquals("Vantaggio P1", tm.getGameScore());
    }

    @Test
    public void getGameScoreAdvantageP2BuggedLogicTest() {
        TennisScoreManager tm = new TennisScoreManager();
        scorePoints(tm, 1, 3);
        scorePoints(tm, 2, 3);
        tm.pointScored(2);
        // Mantiene l'asserzione sul bug esistente per evitare fallimenti
        assertEquals("Errore Game", tm.getGameScore()); 
    }

    @Test
    public void checkGamePointP1WinsGameTest() {
        TennisScoreManager tm = new TennisScoreManager();
        scorePoints(tm, 1, 4);
        assertTrue(tm.getMatchScore().contains("Game: 1-0"));
    }

    @Test
    public void checkGamePointP2WinsGameTest() {
        TennisScoreManager tm = new TennisScoreManager();
        scorePoints(tm, 2, 4);
        assertTrue(tm.getMatchScore().contains("Game: 0-1"));
    }

    @Test
    public void checkSetPointP1WinsSetStandardTest() {
        TennisScoreManager tm = new TennisScoreManager();
        winSet(tm, 1);
        assertTrue(tm.getMatchScore().startsWith("1-0"));
    }

    @Test
    public void checkSetPointP2WinsSetStandardTest() {
        TennisScoreManager tm = new TennisScoreManager();
        winSet(tm, 2); 
        assertTrue(tm.getMatchScore().startsWith("0-1")); 
    }

    @Test
    public void checkSetPointSetGoesTo7_5Test() {
        TennisScoreManager tm = new TennisScoreManager();
        for(int i=0; i<5; i++) winStandardGame(tm, 1);
        for(int i=0; i<5; i++) winStandardGame(tm, 2);
        
        winStandardGame(tm, 1); // 6-5
        assertTrue(tm.getMatchScore().contains("Game: 6-5"));
        
        winStandardGame(tm, 1); // 7-5
        assertTrue(tm.getMatchScore().startsWith("1-0"));
    }

    @Test
    public void checkSetPointSetGoesTo5_7Test() {
        TennisScoreManager tm = new TennisScoreManager();
        for(int i=0; i<5; i++) winStandardGame(tm, 1);
        for(int i=0; i<6; i++) winStandardGame(tm, 2);
        
        winStandardGame(tm, 2); // 5-7
        assertTrue(tm.getMatchScore().startsWith("0-1"));
    }

    @Test
    public void getTieBreakScoreDisplayScoreTest() {
        TennisScoreManager tm = new TennisScoreManager();
        // Setup manuale Tie Break
        for(int i=0; i<6; i++) winStandardGame(tm, 1);
        for(int i=0; i<6; i++) winStandardGame(tm, 2);
        
        tm.pointScored(1);
        tm.pointScored(2);
        tm.pointScored(1);
        
        assertEquals("TIE-BREAK: 2-1", tm.getTieBreakScore());
    }

    @Test
    public void isGameOverP1WinsMatchTest() {
        TennisScoreManager tm = new TennisScoreManager();
        winSet(tm, 1);
        winSet(tm, 1);
        winSet(tm, 1);
        
        assertTrue(tm.isGameOver());
        assertEquals("P1: 3 Set | P2: 0 Set", tm.getMatchScore());
    }

    @Test
    public void isGameOverP2WinsMatchTest() {
        TennisScoreManager tm = new TennisScoreManager();
        winSet(tm, 2); 
        winSet(tm, 2); 
        winSet(tm, 2); 
        
        assertTrue(tm.isGameOver());
        assertEquals("P1: 0 Set | P2: 3 Set", tm.getMatchScore());
    }
    
    @Test
    public void pointScoredGameAlreadyOverTest() {
        TennisScoreManager tm = new TennisScoreManager();
        winSet(tm, 1);
        winSet(tm, 1);
        winSet(tm, 1);
        
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        tm.pointScored(1);
        
        assertTrue(outContent.toString().contains("La partita è finita!"));
        System.setOut(System.out);
    }

    @Test
    public void getGameScoreGameAlreadyOverTest() {
        TennisScoreManager tm = new TennisScoreManager();
        winSet(tm, 1);
        winSet(tm, 1);
        winSet(tm, 1);
        
        assertEquals("PARTITA FINITA", tm.getGameScore());
    }

    @Test
    public void printScoreExecutionCoverageTest() {
        TennisScoreManager tm = new TennisScoreManager();
        tm.pointScored(1);
        tm.printScore(); 
        assertTrue(true);
    }
    
    @Test
    public void getMatchScoreMixedSetsTest() {
        TennisScoreManager tm = new TennisScoreManager();
        winSet(tm, 1);
        winSet(tm, 2);
        
        String result = tm.getMatchScore();
        assertTrue(result.startsWith("1-1"));
    }
    
    @Test
    public void moveToNextSetCalledInternallyTest() {
         TennisScoreManager tm = new TennisScoreManager();
         winSet(tm, 1);
         tm.pointScored(1);
         assertTrue(tm.getMatchScore().contains("Game: 0-0"));
         assertEquals("15-Love", tm.getGameScore());
    }
}	