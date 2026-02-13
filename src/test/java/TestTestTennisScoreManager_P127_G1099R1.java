/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "Senior"
Cognome: "QA Engineer"
Username: ange.dalia@studenti.unina.it
UserID: 127
Date: 21/11/2025
*/

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Field;

public class TestTestTennisScoreManager_P127_G1099R1 {

    private TennisScoreManager manager;

    @BeforeClass
    public static void setUpClass() {
        // Setup statico se necessario
    }

    @AfterClass
    public static void tearDownClass() {
        // Teardown statico se necessario
    }

    @Before
    public void setUp() {
        manager = new TennisScoreManager();
    }

    @After
    public void tearDown() {
        manager = null;
    }

    // --- METODI DI UTILITÀ ---
    private void scorePoints(int player, int times) {
        for (int i = 0; i < times; i++) {
            manager.pointScored(player);
        }
    }

    // --- TEST DI COPERTURA E MUTAZIONE ---

    @Test
    public void testInvalidPlayerInput() {
        // Copre il ramo 'else' finale di pointScored
        manager.pointScored(3); 
        manager.pointScored(-1);
        assertEquals("Love-Love", manager.getGameScore());
    }

    @Test
    public void testStandardGameWinP1() {
        // Mutation: verifica < 4 e incrementi
        assertEquals("Love-Love", manager.getGameScore());
        manager.pointScored(1);
        assertEquals("15-Love", manager.getGameScore());
        manager.pointScored(1);
        assertEquals("30-Love", manager.getGameScore());
        manager.pointScored(1);
        assertEquals("40-Love", manager.getGameScore());
        manager.pointScored(1);
        
        // MUTATION KILLER: Verifica esplicita del reset
        assertEquals("Love-Love", manager.getGameScore());
        assertTrue(manager.getMatchScore().contains("Game: 1-0"));
    }

    @Test
    public void testStandardGameWinP2() {
        // Mutation: verifica l'altro ramo dell'if principale
        scorePoints(2, 3);
        assertEquals("Love-40", manager.getGameScore());
        manager.pointScored(2);
        assertTrue(manager.getMatchScore().contains("Game: 0-1"));
    }

    @Test
    public void testDeuceLogic() {
        // MUTATION KILLER: Testiamo il RIENTRO nel Deuce (4-4)
        // Questo è cruciale per battere la mutation dell'anno scorso
        scorePoints(1, 3); // 40
        scorePoints(2, 3); // 40
        assertEquals("Deuce", manager.getGameScore());
        
        manager.pointScored(1); // Vantaggio P1
        assertEquals("Vantaggio P1", manager.getGameScore());
        
        manager.pointScored(2); // Ritorno a Deuce
        assertEquals("Deuce", manager.getGameScore());
    }

    @Test
    public void testAdvantageP1() {
        // Copre il ramo "Vantaggio P1"
        scorePoints(1, 3);
        scorePoints(2, 3); 
        manager.pointScored(1);
        assertEquals("Vantaggio P1", manager.getGameScore());
        
        // MUTATION KILLER: Verifica che NON sia vinto il game
        assertFalse(manager.getMatchScore().contains("Game: 1-0"));
    }

    @Test
    public void testP2BugPathExecution() {
        // Copertura Bug P2
        scorePoints(1, 3);
        scorePoints(2, 3); // Deuce
        manager.pointScored(2); // P2 Vantaggio (teorico)
        assertEquals("Errore Game", manager.getGameScore());
    }

    @Test
    public void testSetEvolution() {
        // Copre l'incremento dei game fino a 6
        for (int i = 0; i < 5; i++) {
            scorePoints(1, 4); // P1 vince game a zero
        }
        assertTrue(manager.getMatchScore().contains("Game: 5-0"));
        
        scorePoints(1, 4); // 6-0
        assertTrue(manager.getMatchScore().startsWith("1-0"));
    }
    
    @Test
    public void testSetWinByTwoGames() {
        // Scenario 7-5
        for(int i=0; i<5; i++) scorePoints(1, 4);
        for(int i=0; i<5; i++) scorePoints(2, 4);
        
        scorePoints(1, 4); // 6-5
        scorePoints(1, 4); // 7-5 -> P1 Vince Set
        
        assertTrue(manager.getMatchScore().startsWith("1-0"));
    }
    
    @Test
    public void testSetWinP2() {
        // Scenario opposto per coprire i rami di P2 in checkSetPoint
        for(int i=0; i<6; i++) scorePoints(2, 4);
        assertTrue(manager.getMatchScore().startsWith("0-1"));
    }

    @Test
    public void testTieBreakEntry() {
        // Forza l'entrata nel TieBreak (6-6)
        for(int i=0; i<5; i++) scorePoints(1, 4);
        for(int i=0; i<5; i++) scorePoints(2, 4);
        
        scorePoints(1, 4); // 6-5
        scorePoints(2, 4); // 6-6 -> TieBreak
        
        assertTrue(manager.getMatchScore().contains("TIE-BREAK"));
        assertEquals("TIE-BREAK: 0-0", manager.getTieBreakScore());
    }
    
    @Test
    public void testTieBreakBugExecution() {
        // Copertura bug Tie-Break
        for(int i=0; i<5; i++) scorePoints(1, 4);
        for(int i=0; i<5; i++) scorePoints(2, 4);
        scorePoints(1, 4); 
        scorePoints(2, 4); 
        
        scorePoints(1, 7); // P1 vince TB
        
        String finalScore = manager.getMatchScore();
        assertTrue(finalScore.startsWith("0-0"));
        assertTrue(finalScore.contains("Game: 0-0"));
    }
    
    @Test
    public void testTieBreakWinP2_Coverage() {
        // COPERTURA: gamesP2++; resetGameAndPoints(); checkSetPoint();
        for(int i=0; i<5; i++) scorePoints(1, 4);
        for(int i=0; i<5; i++) scorePoints(2, 4);
        scorePoints(1, 4); // 6-5
        scorePoints(2, 4); // 6-6
        
        scorePoints(2, 7); 
        
        String matchScore = manager.getMatchScore();
        assertTrue(matchScore.contains("Game: 0-0"));
    }
    
    @Test
    public void testSetWinP2_7to5_Coverage() {
        // COPERTURA: else if (gamesP2 >= 6 ... || (gamesP2 == 7 && gamesP1 == 5) ...)
        for(int i=0; i<5; i++) scorePoints(1, 4);
        for(int i=0; i<5; i++) scorePoints(2, 4);
        
        scorePoints(2, 4); // 6-5
        scorePoints(2, 4); // 7-5 -> Vince il Set
        
        assertTrue(manager.getMatchScore().startsWith("0-1"));
    }

    @Test
    public void testGameOverSequence() {
        // P1 vince il match
        for(int g=0; g<6; g++) scorePoints(1, 4);
        for(int g=0; g<6; g++) scorePoints(1, 4);
        for(int g=0; g<6; g++) scorePoints(1, 4);
        
        assertTrue(manager.isGameOver());
        assertEquals("PARTITA FINITA", manager.getGameScore());
        
        manager.pointScored(1); 
        assertTrue(manager.isGameOver());
        assertTrue(manager.getMatchScore().contains("P1: 3 Set"));
    }

    @Test
    public void testMatchWinP2_Coverage() {
        // COPERTURA FONDAMENTALE (che mancava nella versione 97%): if (setsWonP2 == 3)
        for(int g=0; g<6; g++) scorePoints(2, 4);
        for(int g=0; g<6; g++) scorePoints(2, 4);
        for(int g=0; g<6; g++) scorePoints(2, 4);
        
        assertTrue(manager.isGameOver());
        assertTrue(manager.getMatchScore().contains("P2: 3 Set"));
    }
    
    @Test
    public void testResetMethodsDirectly() {
        manager.pointScored(1);
        manager.resetPoints();
        assertEquals("Love-Love", manager.getGameScore());
        
        scorePoints(1, 4); 
        manager.resetGameAndPoints();
        assertTrue(manager.getMatchScore().contains("Game: 0-0"));
    }

    @Test
    public void testForceSetWinWithReflection() throws Exception {
        // Reflection per P2 (essenziale per il 99%)
        Field gamesP1Field = TennisScoreManager.class.getDeclaredField("gamesP1");
        Field gamesP2Field = TennisScoreManager.class.getDeclaredField("gamesP2");
        gamesP1Field.setAccessible(true);
        gamesP2Field.setAccessible(true);
        
        gamesP1Field.setInt(manager, 6);
        gamesP2Field.setInt(manager, 7);
        
        manager.checkSetPoint();
        
        assertTrue(manager.getMatchScore().startsWith("0-1"));
    }
    
    @Test
    public void testDeepMatchPrinting_Coverage() throws Exception {
        // Copre la formattazione delle virgole
        Field currentSetField = TennisScoreManager.class.getDeclaredField("currentSet");
        currentSetField.setAccessible(true);
        currentSetField.setInt(manager, 4); 
        
        Field setsP1Field = TennisScoreManager.class.getDeclaredField("setsP1");
        setsP1Field.setAccessible(true);
        int[] p1Scores = (int[]) setsP1Field.get(manager);
        p1Scores[0] = 6; p1Scores[1] = 4; p1Scores[2] = 7; 
        
        manager.printScore();
    }
}