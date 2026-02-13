import org.junit.Before;
import org.junit.Test;

public class TestTestTennisScoreManager_P387_G1028R1 {

    private TennisScoreManager manager;

    // --- Utility per simulare lo scorrimento del punteggio ---

    private void scorePoint(int player) {
        manager.pointScored(player);
    }

    // Simula la vittoria di N games (esegue i path logici di game win/reset)
    private void scoreGames(int player, int gamesToWin) {
        for (int i = 0; i < gamesToWin; i++) {
            // Punti minimi per vincere un game (4-0)
            for (int j = 0; j < 4; j++) {
                scorePoint(player);
            }
        }
    }

    // Simula la vittoria di N set (esegue i path logici di set win/reset)
    private void scoreSets(int player, int setsToWin) {
        for (int i = 0; i < setsToWin; i++) {
            // Vinto 6-0 (path più veloce per la vittoria del set)
            scoreGames(player, 6);
        }
    }

    @Before // JUNIT 4 SETUP
    public void setUp() {
        manager = new TennisScoreManager();
    }

    // ====================================================================
    // 1. PATH DI STATO E UTILITY
    // ====================================================================

    @Test
    public void testPathInitialStateAndAccessors() {
        // Esegue il path di accesso allo stato iniziale (anche se non verificato)
        manager.getGameScore();
        manager.getMatchScore();
        manager.isGameOver();
    }

    @Test
    public void testPathResetPoints() {
        scorePoint(1); scorePoint(2); // 15-15
        manager.resetPoints(); // Esegue il path di reset
        manager.getGameScore();
    }

    @Test
    public void testPathInvalidInputAndGameOverBlock() {
        // 1. Path input non valido (dovrebbe essere ignorato)
        manager.pointScored(99); 
        
        // 2. Path Match Win
        scoreSets(1, 3); 
        
        // 3. Path pointScored dopo Game Over (dovrebbe essere bloccato)
        manager.pointScored(2);
        manager.isGameOver();
    }

    // ====================================================================
    // 2. PATH LOGICA GAME
    // ====================================================================

    @Test
    public void testPathStandardGameScores() {
        // Love-15, 30-15, 40-30, Deuce
        scorePoint(1); manager.getGameScore();
        scorePoint(2); manager.getGameScore();
        scorePoint(1); manager.getGameScore();
        scorePoint(2); manager.getGameScore();
        scorePoint(1); manager.getGameScore(); // 40-30
        scorePoint(2); manager.getGameScore(); // Deuce
    }

    @Test
    public void testPathDeuceAdvantageWin() {
        // Simula Deuce (raggiunto in testPathStandardGameScores, ma rifacciamolo per indipendenza)
        scorePoint(1); scorePoint(2); scorePoint(1); scorePoint(2); scorePoint(1); scorePoint(2); // Deuce
        
        // Path Vantaggio P1, Deuce, Vantaggio P2, Win P2
        scorePoint(1); manager.getGameScore(); // Vantaggio P1
        scorePoint(2); manager.getGameScore(); // Deuce
        scorePoint(2); manager.getGameScore(); // Vantaggio P2
        scorePoint(2); manager.getMatchScore(); // Game Win P2 (esegue path game win)
    }

    @Test
    public void testPathGameWinStandard() {
        // Path game vinto 4-0 (esegue path game win e reset dei punti)
        scoreGames(1, 1); 
    }

    // ====================================================================
    // 3. PATH LOGICA SET E PARTITA
    // ====================================================================

    @Test
    public void testPathSetWinStandard() {
        // Punteggio 5-4
        scoreGames(1, 5);
        scoreGames(2, 4);
        
        // P1 vince un game (6-4). Esegue path set win standard.
        scoreGames(1, 1); 
        manager.getMatchScore(); 
    }

    @Test
    public void testPathTieBreakActivationAndWin() {
        // Punteggio 6-6
        scoreGames(1, 6);
        scoreGames(2, 6);
        
        // L'ultima chiamata a scoreGames attiva il Tie-Break (esegue path TB activation)
        manager.getMatchScore(); 

        // P1 vince il Tie-Break (Punti 7-6 TB, totale 8-6)
        for (int i = 0; i < 6; i++) { scorePoint(1); scorePoint(2); } // 6-6 TB
        scorePoint(1); scorePoint(1); // 8-6 TB. Esegue path TB win e set win.
        
        manager.getMatchScore(); 
    }

    @Test
    public void testPathMatchWin() {
        // Path P2 vince 3 set di fila (esegue path match win)
        scoreSets(2, 3);
        manager.isGameOver(); 
        manager.getMatchScore(); 
    }
}