/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "Alessandra"
Cognome: "Zotti"
Username: ales.zotti@studenti.unina.it
UserID: 245
Date: 22/11/2025
*/

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTestTennisScoreManager_P245_G1131R1 {
    
    private TennisScoreManager manager;

    @BeforeClass
    public static void setUpClass() {
        // Eseguito una volta prima dell'inizio dei test nella classe
        System.out.println("Inizio Test Suite TennisScoreManager");
    }
                
    @AfterClass
    public static void tearDownClass() {
        // Eseguito una volta alla fine di tutti i test nella classe
        System.out.println("Fine Test Suite TennisScoreManager");
    }
                
    @Before
    public void setUp() {
        // Eseguito prima di ogni metodo di test
        manager = new TennisScoreManager();
    }
                
    @After
    public void tearDown() {
        // Eseguito dopo ogni metodo di test
        manager = null;
    }
                
    @Test
    public void testInitialState() {
        // Verifica lo stato iniziale (0-0, Love-Love)
        assertEquals("Love-Love", manager.getGameScore());
        assertEquals("0-0 (Game: 0-0 Love-Love)", manager.getMatchScore());
    }

    @Test
    public void testSimplePointsP1() {
        // P1 segna punti: 15, 30, 40
        manager.pointScored(1);
        assertEquals("15-Love", manager.getGameScore());
        
        manager.pointScored(1);
        assertEquals("30-Love", manager.getGameScore());
        
        manager.pointScored(1);
        assertEquals("40-Love", manager.getGameScore());
    }

    @Test
    public void testSimplePointsP2() {
        // P2 segna punti
        manager.pointScored(2);
        assertEquals("Love-15", manager.getGameScore());
        
        manager.pointScored(2); 
        manager.pointScored(2);
        assertEquals("Love-40", manager.getGameScore());
    }
    
    @Test
    public void testWinGameP1() {
        // P1 vince un game a 0
        manager.pointScored(1); // 15
        manager.pointScored(1); // 30
        manager.pointScored(1); // 40
        manager.pointScored(1); // Vince Game
        
        // Verifica che il punteggio punti sia resettato e gamesP1 sia 1
        assertEquals("Love-Love", manager.getGameScore());
        assertTrue(manager.getMatchScore().contains("Game: 1-0"));
    }

    @Test
    public void testWinGameP2() {
        // P2 vince un game a 0
        for(int i=0; i<4; i++) manager.pointScored(2);
        
        assertEquals("Love-Love", manager.getGameScore());
        assertTrue(manager.getMatchScore().contains("Game: 0-1"));
    }

    @Test
    public void testDeuce() {
        // Arriviamo a 40-40 (3 punti a testa)
        for(int i=0; i<3; i++) {
            manager.pointScored(1);
            manager.pointScored(2);
        }
        assertEquals("Deuce", manager.getGameScore());
    }

    @Test
    public void testAdvantageP1() {
        // Arriviamo a Deuce
        for(int i=0; i<3; i++) {
            manager.pointScored(1);
            manager.pointScored(2);
        }
        // P1 fa punto -> Vantaggio
        manager.pointScored(1);
        assertEquals("Vantaggio P1", manager.getGameScore());
        
        // P2 pareggia -> Deuce
        manager.pointScored(2);
        assertEquals("Deuce", manager.getGameScore());
        
        // P1 vince dal vantaggio
        manager.pointScored(1); // Adv P1
        manager.pointScored(1); // Game P1
        assertTrue(manager.getMatchScore().contains("Game: 1-0"));
    }

    @Test
    public void testAdvantageP2Bug() {
        // TEST DEL BUG: Il codice ha "if (scoreP2 >= 3 && scoreP2 == scoreP2 + 1)"
        // Questa condizione è sempre falsa. Quindi "Vantaggio P2" non viene mai restituito.
        // Testiamo che NON ritorni Vantaggio P2 ma cada nel caso di default "Errore Game" o rimanga Deuce logico
        
        for(int i=0; i<3; i++) {
            manager.pointScored(1);
            manager.pointScored(2);
        }
        // Siamo a Deuce (3-3).
        // P2 segna un punto -> Punteggi interni (3-4).
        // getGameScore controlla:
        // 1. scoreP1 < 4... (falso)
        // 2. scoreP1 == scoreP2... (falso)
        // 3. Vantaggio P1... (falso)
        // 4. Vantaggio P2... (BUG: scoreP2 == scoreP2 + 1 è falso)
        // 5. Ritorna "Errore Game"
        
        manager.pointScored(2); 
        assertEquals("Errore Game", manager.getGameScore()); 
    }
    
    @Test
    public void testInvalidPlayer() {
        // Test ramo else invalid player
        manager.pointScored(99);
        assertEquals("Love-Love", manager.getGameScore());
    }

    @Test
    public void testSetWinP1Standard() {
        // P1 vince 6 game di fila
        for (int g = 0; g < 6; g++) {
            for (int p = 0; p < 4; p++) manager.pointScored(1);
        }
        // Dovrebbe aver vinto il set 1 e resettato per il set 2
        // Output atteso: setsWonP1=1, currentSet incrementato
        // Nota: moveToNextSet resetta games, quindi vedremo Game: 0-0 nel nuovo set
        String score = manager.getMatchScore();
        // Controlliamo che P1 abbia vinto un set guardando la stringa formattata o lo stato
        // Purtroppo non ci sono getter per i set vinti, dobbiamo dedurlo da getMatchScore
        // "1-0 (Game: 0-0 Love-Love)"
        assertTrue(score.startsWith("1-0"));
    }
    
    @Test
    public void testSetWinP2Standard() {
        // P2 vince 6 game di fila
        for (int g = 0; g < 6; g++) {
            for (int p = 0; p < 4; p++) manager.pointScored(2);
        }
        assertTrue(manager.getMatchScore().startsWith("0-1"));
    }

    @Test
    public void testSetWinP1Extended() {
        // Simuliamo 5-5
        for(int i=0; i<5; i++) {
            // P1 vince game
            for(int p=0; p<4; p++) manager.pointScored(1);
            // P2 vince game
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        
        // P1 vince il 6° game -> 6-5
        for(int p=0; p<4; p++) manager.pointScored(1);
        
        // P1 vince il 7° game -> 7-5 (Vittoria set)
        for(int p=0; p<4; p++) manager.pointScored(1);
        
        assertTrue(manager.getMatchScore().startsWith("1-0"));
    }

    @Test
    public void testTieBreakTrigger() {
        // Simuliamo 6-6 per attivare il tiebreak
        for(int i=0; i<6; i++) {
            for(int p=0; p<4; p++) manager.pointScored(1); // P1 game
            for(int p=0; p<4; p++) manager.pointScored(2); // P2 game
        }
        
        // Ora dovremmo essere in TieBreak
        // Stringa attesa contiene "TIE-BREAK: 0-0"
        assertTrue(manager.getMatchScore().contains("TIE-BREAK: 0-0"));
        
        // Segniamo un punto nel tie break
        manager.pointScored(1);
        assertTrue(manager.getMatchScore().contains("TIE-BREAK: 1-0"));
    }

    @Test
    public void testTieBreakWinBugP1() {
        // Raggiungiamo il TieBreak (6-6)
        for(int i=0; i<6; i++) {
            for(int p=0; p<4; p++) manager.pointScored(1); 
            for(int p=0; p<4; p++) manager.pointScored(2); 
        }
        
        // P1 vince il TieBreak (7-0)
        for(int i=0; i<6; i++) manager.pointScored(1); // 6-0
        
        // Al prossimo punto P1 vince il game del tiebreak.
        // QUI C'È IL BUG CRITICO:
        // checkTieBreakPoint -> gamesP1++ (diventa 7) -> resetGameAndPoints() (gamesP1 torna a 0) -> checkSetPoint() (gamesP1 è 0, non entra negli if di vittoria set).
        manager.pointScored(1); 
        
        // A causa del bug, il set NON viene assegnato a P1. 
        // I game sono stati resettati a 0-0 ma siamo ancora nel Set 1 (currentSet non è incrementato).
        // Quindi il match score sembrerà "0-0 (Game: 0-0 Love-Love)" invece di "1-0..."
        // Testiamo che il comportamento sia quello (errato) attuale.
        
        String score = manager.getMatchScore();
        // Se il bug è presente, non ha assegnato il set
        assertFalse(score.startsWith("1-0")); 
        // Verifica che i game siano tornati a 0-0
        assertTrue(score.contains("Game: 0-0"));
    }
    
    @Test
    public void testTieBreakWinBugP2() {
        // Raggiungiamo il TieBreak (6-6)
        for(int i=0; i<6; i++) {
            for(int p=0; p<4; p++) manager.pointScored(1); 
            for(int p=0; p<4; p++) manager.pointScored(2); 
        }
        
        // P2 vince il TieBreak (0-7)
        for(int i=0; i<7; i++) manager.pointScored(2);
        
        // Anche qui, resetta i game a 0 prima di controllare il set, quindi fallisce l'assegnazione del set.
        String score = manager.getMatchScore();
        assertFalse(score.startsWith("0-1"));
        assertTrue(score.contains("Game: 0-0"));
    }
    
    @Test
    public void testMatchWinP1() {
        // P1 deve vincere 3 set.
        // Poiché il tie-break è buggato, vinciamo i set in modo pulito (6-0).
        
        // Set 1
        for(int i=0; i<6; i++) { for(int p=0; p<4; p++) manager.pointScored(1); }
        // Set 2
        for(int i=0; i<6; i++) { for(int p=0; p<4; p++) manager.pointScored(1); }
        // Set 3
        for(int i=0; i<6; i++) { for(int p=0; p<4; p++) manager.pointScored(1); }
        
        assertTrue(manager.isGameOver());
        assertTrue(manager.getMatchScore().contains("P1: 3 Set"));
        
        // Testiamo comportamento post-game over
        manager.pointScored(1); // Non dovrebbe cambiare nulla
        assertTrue(manager.getGameScore().equals("PARTITA FINITA"));
    }
    
    @Test
    public void testMatchWinP2() {
        // P2 vince 3 set (6-0 ogni volta)
        for(int s=0; s<3; s++) {
             for(int i=0; i<6; i++) { 
                 for(int p=0; p<4; p++) manager.pointScored(2); 
             }
        }
        
        assertTrue(manager.isGameOver());
        assertTrue(manager.getMatchScore().contains("P2: 3 Set"));
    }

    @Test
    public void testSpecialConditionsSetPoint() {
        // Copertura rami specifici in checkSetPoint
        // (gamesP2 == 7 && gamesP1 == 6)
        // Per arrivarci senza passare dal TieBreak buggato (che scatta a 6-6), 
        // dovremmo avere una sequenza strana o manipolare lo stato, ma con l'interfaccia pubblica:
        // Se arrivo a 5-5, poi 5-6, poi 6-6 -> SCATTA TIE BREAK.
        // L'unico modo per entrare in (7-6) senza tie break nel codice è se isTieBreak non scatta.
        // Ma scatta sempre a 6-6.
        // Tuttavia, c'è un caso: checkSetPoint viene chiamato PRIMA di azzerare i punti o incrementare game in certi flussi? 
        // No. Quindi quelle condizioni (gamesP1 == 7 && gamesP2 == 6) sono codice morto (unreachable code) 
        // logicamente, a meno che non si salti il check 6-6.
        // Tuttavia, possiamo testare il ramo (gamesP1 == 7 && gamesP2 == 5) che è raggiungibile.
        
        // Set 1: P1 va 5-0
        for(int i=0; i<5; i++) { for(int p=0; p<4; p++) manager.pointScored(1); }
        // P2 va 5-5
        for(int i=0; i<5; i++) { for(int p=0; p<4; p++) manager.pointScored(2); }
        // P1 va 6-5
        for(int p=0; p<4; p++) manager.pointScored(1);
        // P1 va 7-5
        for(int p=0; p<4; p++) manager.pointScored(1);
        
        // Verifica vittoria set
        assertTrue(manager.getMatchScore().startsWith("1-0"));
    }
    
    @Test
    public void testResetPointsDirectly() {
        manager.pointScored(1);
        manager.resetPoints();
        assertEquals("Love-Love", manager.getGameScore());
    }
    
    @Test
    public void testResetGameAndPointsDirectly() {
        manager.pointScored(1);
        manager.resetGameAndPoints();
        assertEquals("0-0 (Game: 0-0 Love-Love)", manager.getMatchScore());
    }

    @Test
    public void testPartialScoreScenarios() {
        // Copertura per getGameScore() - casi misti
        // 15-15
        manager.pointScored(1); manager.pointScored(2);
        assertEquals("15-15", manager.getGameScore());
        
        // 30-15
        manager.pointScored(1);
        assertEquals("30-15", manager.getGameScore());
        
        // 30-30
        manager.pointScored(2);
        assertEquals("30-30", manager.getGameScore());
        
        // 40-30 (P1 != 3 || P2 != 3 check)
        manager.pointScored(1);
        assertEquals("40-30", manager.getGameScore());
    }
}