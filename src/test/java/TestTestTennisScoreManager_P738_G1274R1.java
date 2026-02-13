/*Compila i campi "Nome" e "Cognome" con le informazioni richieste Nome: "inserire il proprio nome" Cognome: "inserire il proprio cognome" Username: marco.canonico@studenti.unina.it UserID: 738 Date: 24/11/2025 */

import org.junit.Before; import org.junit.After; import org.junit.BeforeClass; import org.junit.AfterClass; import org.junit.Test; import static org.junit.Assert.*;

public class TestTestTennisScoreManager_P738_G1274R1 {

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
			
// --- TEST BASE FUNZIONALITA' E INIZIALIZZAZIONE ---

@Test
public void testInitialState() {
    String score = manager.getMatchScore();
    // 0-0 sets, 0-0 games, Love-Love
    assertTrue(score.contains("0-0"));
    assertTrue(score.contains("Game: 0-0"));
    assertTrue(score.contains("Love-Love"));
    assertFalse(manager.isGameOver());
}

@Test
public void testPointScoredP1() {
    manager.pointScored(1);
    String score = manager.getGameScore();
    assertEquals("15-Love", score);
}

@Test
public void testPointScoredP2() {
    manager.pointScored(2);
    String score = manager.getGameScore();
    assertEquals("Love-15", score);
}

@Test
public void testInvalidPlayer() {
    manager.pointScored(1); // 15-Love
    manager.pointScored(3); // Invalid
    manager.pointScored(-1); // Invalid
    assertEquals("15-Love", manager.getGameScore());
}

@Test
public void testResetPoints() {
    manager.pointScored(1);
    manager.resetPoints();
    assertEquals("Love-Love", manager.getGameScore());
}

// --- TEST LOGICA GAME STANDARD E VITTORIA GAME ---

@Test
public void testGameWinP1_Standard() {
    // P1 fa 4 punti di fila
    manager.pointScored(1); // 15
    manager.pointScored(1); // 30
    manager.pointScored(1); // 40
    manager.pointScored(1); // Win Game
    
    String matchScore = manager.getMatchScore();
    assertTrue(matchScore.contains("Game: 1-0"));
    assertEquals("Love-Love", manager.getGameScore());
}

@Test
public void testGameWinP2_Standard() {
    // P2 fa 4 punti di fila
    manager.pointScored(2); // 15
    manager.pointScored(2); // 30
    manager.pointScored(2); // 40
    manager.pointScored(2); // Win Game
    
    String matchScore = manager.getMatchScore();
    assertTrue(matchScore.contains("Game: 0-1"));
}

// --- TEST DEUCE, VANTAGGI E BUG DI GETGAMESCORE ---

@Test
public void testDeuce() {
    // 3-3
    manager.pointScored(1); // 15
    manager.pointScored(2); // 15
    manager.pointScored(1); // 30
    manager.pointScored(2); // 30
    manager.pointScored(1); // 40
    manager.pointScored(2); // 40 -> Deuce
    
    assertEquals("Deuce", manager.getGameScore());
}

@Test
public void testAdvantageP1() {
    testDeuce(); // Setup 40-40
    manager.pointScored(1); // Adv P1
    assertEquals("Vantaggio P1", manager.getGameScore());
}

@Test
public void testWinFromAdvantageP1() {
    testAdvantageP1(); // Adv P1
    manager.pointScored(1); // Win Game
    assertTrue(manager.getMatchScore().contains("Game: 1-0"));
}

@Test
public void testBackToDeuceFromP1() {
    testAdvantageP1(); // Adv P1
    manager.pointScored(2); // Back to Deuce
    assertEquals("Deuce", manager.getGameScore());
}

@Test
public void testAdvantageP2_BugCoverage() {
    /* * NOTA: Il codice sorgente contiene un bug in getGameScore:
     * if (scoreP2 >= 3 && scoreP2 == scoreP2 + 1)
     * Questa condizione è impossibile. Di conseguenza, quando P2 è in vantaggio,
     * il metodo ritorna "Errore Game". Testiamo questo comportamento specifico
     * per garantire la copertura del codice esistente.
     */
    testDeuce(); // 40-40 (3-3 internamente)
    manager.pointScored(2); // P2 va a 4 (P1 è 3). Dovrebbe essere Adv P2.
    
    // A causa del bug nel codice sorgente, ci aspettiamo "Errore Game"
    assertEquals("Errore Game", manager.getGameScore());
    
    // Se P2 segna ancora, dovrebbe comunque vincere il game
    manager.pointScored(2); 
    assertTrue(manager.getMatchScore().contains("Game: 0-1"));
}

// --- TEST SET E TIE-BREAK ---

private void winGameP1() {
    for(int i=0; i<4; i++) manager.pointScored(1);
}

private void winGameP2() {
    for(int i=0; i<4; i++) manager.pointScored(2);
}

@Test
public void testSetWinP1_Standard() {
    // Vinci 6 game
    for(int i=0; i<6; i++) {
        winGameP1();
    }
    // Dovrebbe aver vinto il set 1 e resettato i game
    String score = manager.getMatchScore();
    // 1-0 sets
    assertTrue(score.startsWith("1-0"));
    // Game resettati
    assertTrue(score.contains("Game: 0-0")); 
}

@Test
public void testSetWinP2_Standard() {
    for(int i=0; i<6; i++) {
        winGameP2();
    }
    String score = manager.getMatchScore();
    assertTrue(score.startsWith("0-1"));
    assertTrue(score.contains("Game: 0-0")); 
}

@Test
public void testSetWinP1_7_5() {
    // Porta a 5-5
    for(int i=0; i<5; i++) { winGameP1(); winGameP2(); }
    
    winGameP1(); // 6-5
    winGameP1(); // 7-5 -> Set Win
    
    String score = manager.getMatchScore();
    assertTrue(score.startsWith("1-0"));
}

@Test
public void testSetWinP2_5_7() {
    // Porta a 5-5
    for(int i=0; i<5; i++) { winGameP1(); winGameP2(); }
    
    winGameP2(); // 5-6
    winGameP2(); // 5-7 -> Set Win
    
    String score = manager.getMatchScore();
    assertTrue(score.startsWith("0-1"));
}

@Test
public void testTieBreakTrigger() {
    // Porta a 6-6
    for(int i=0; i<6; i++) { winGameP1(); winGameP2(); }
    
    // Verifica entrata in TieBreak
    String score = manager.getMatchScore();
    assertTrue(score.contains("TIE-BREAK: 0-0"));
    // Punti game devono essere 6-6
    assertTrue(score.contains("Game: 6-6"));
}

@Test
public void testTieBreakScoring() {
    testTieBreakTrigger(); // Siamo in TieBreak 6-6
    
    manager.pointScored(1);
    assertTrue(manager.getMatchScore().contains("TIE-BREAK: 1-0"));
    
    manager.pointScored(2);
    assertTrue(manager.getMatchScore().contains("TIE-BREAK: 1-1"));
}

@Test
public void testTieBreakWinP1_AndLogicBug() {
    testTieBreakTrigger(); // 6-6
    
    // Vinci tie break 7-0
    for(int i=0; i<6; i++) manager.pointScored(1); 
    // Score ora 6-0 tiebreak
    manager.pointScored(1); // 7-0 -> Win Game TieBreak
    
    /* * NOTA SUL CODICE SORGENTE:
     * C'è un difetto logico in checkTieBreakPoint(). Chiama resetGameAndPoints() 
     * che azzera i gamesP1/P2 PRIMA di chiamare checkSetPoint().
     * checkSetPoint() controlla se games >= 6. Poiché sono stati azzerati a 0,
     * il set non viene mai assegnato e si rimane nel Set 1 con 0 game.
     * Testiamo questo comportamento (anche se errato logicamente per il tennis reale)
     * per garantire la correttezza rispetto al codice fornito.
     */
    
    String score = manager.getMatchScore();
    // A causa del bug, games resetta a 0-0, set non vinto
    assertTrue(score.contains("Game: 0-0"));
    // Rimaniamo nel set corrente senza assegnazione (ancora 0-0 set score)
    assertTrue(score.startsWith("0-0")); 
}

@Test
public void testTieBreakWinP2_AndLogicBug() {
    testTieBreakTrigger();
    for(int i=0; i<7; i++) manager.pointScored(2);
    
    // Simile al test P1, il codice resetta i game prima di assegnare il set
    String score = manager.getMatchScore();
    assertTrue(score.contains("Game: 0-0"));
}

// --- TEST PARTITA COMPLETA E GAMEOVER ---

@Test
public void testMatchWinP1() {
    // Vinci 3 set. Usiamo 6-0 standard per evitare il bug del tiebreak
    // Set 1
    for(int i=0; i<6; i++) winGameP1();
    // Set 2
    for(int i=0; i<6; i++) winGameP1();
    // Set 3
    for(int i=0; i<6; i++) winGameP1();
    
    assertTrue(manager.isGameOver());
    String finalScore = manager.getMatchScore();
    assertTrue(finalScore.contains("P1: 3 Set"));
    
    // Test playing after game over
    manager.pointScored(1);
    // Il punteggio non deve cambiare o stampare output diverso, isGameOver rimane true
    assertTrue(manager.isGameOver());
    assertEquals("PARTITA FINITA", manager.getGameScore());
}

@Test
public void testMatchWinP2() {
    // Set 1
    for(int i=0; i<6; i++) winGameP2();
    // Set 2
    for(int i=0; i<6; i++) winGameP2();
    // Set 3
    for(int i=0; i<6; i++) winGameP2();
    
    assertTrue(manager.isGameOver());
    assertTrue(manager.getMatchScore().contains("P2: 3 Set"));
}

@Test
public void testMatchScoreInterim() {
    // Vinci 1 set per P1
    for(int i=0; i<6; i++) winGameP1();
    // Vinci 1 set per P2
    for(int i=0; i<6; i++) winGameP2();
    
    // Score dovrebbe essere 1-1 Sets
    String score = manager.getMatchScore();
    assertTrue(score.startsWith("1-1"));
}

@Test
public void testPrintScoreCoverage() {
    // Invocazione diretta per statement coverage delle System.out
    manager.printScore();
    manager.pointScored(1); // Triggera printScore interno
}
}