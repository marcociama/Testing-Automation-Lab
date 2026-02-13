/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "Gemini"
Cognome: "AI"
Username: gemini_ai
UserID: 001
Date: 2023-10-27
*/

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
import static org.junit.Assert.*;

public class TestTestTennisScoreManager_P183_G1242R1 {
	@BeforeClass
	public static void setUpClass() {
		// Eseguito una volta prima dell'inizio dei test nella classe
		// Inizializza risorse condivise 
		// o esegui altre operazioni di setup
	}
				
	@AfterClass
	public static void tearDownClass() {
		// Eseguito una volta alla fine di tutti i test nella classe
		// Effettua la pulizia delle risorse condivise 
		// o esegui altre operazioni di teardown
	}
				
	@Before
	public void setUp() {
		// Eseguito prima di ogni metodo di test
		// Preparazione dei dati di input specifici per il test
	}
				
	@After
	public void tearDown() {
		// Eseguito dopo ogni metodo di test
		// Pulizia delle risorse o ripristino dello stato iniziale
	}
				
	@Test
    public void testInitialScore() {
        TennisScoreManager manager = new TennisScoreManager();
        assertEquals("Love-Love", manager.getGameScore());
    }

    @Test
    public void testP1ScoredOnce() {
        TennisScoreManager manager = new TennisScoreManager();
        manager.pointScored(1);
        assertEquals("15-Love", manager.getGameScore());
    }

    @Test
    public void testP2ScoredOnce() {
        TennisScoreManager manager = new TennisScoreManager();
        manager.pointScored(2);
        assertEquals("Love-15", manager.getGameScore());
    }

    @Test
    public void testInvalidPlayerInput() {
        TennisScoreManager manager = new TennisScoreManager();
        manager.pointScored(3); // Invalid player
        assertEquals("Love-Love", manager.getGameScore());
    }

    @Test
    public void testDeuce() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 40-40
        for(int i=0; i<3; i++) manager.pointScored(1);
        for(int i=0; i<3; i++) manager.pointScored(2);
        assertEquals("Deuce", manager.getGameScore());
    }

    @Test
    public void testAdvantageP1() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 40-40
        for(int i=0; i<3; i++) manager.pointScored(1);
        for(int i=0; i<3; i++) manager.pointScored(2);
        // P1 Scores for Advantage
        manager.pointScored(1);
        assertEquals("Vantaggio P1", manager.getGameScore());
    }

    /**
     * This test validates a specific bug in the source code where the condition
     * for Advantage P2 contains a logic error (scoreP2 == scoreP2 + 1),
     * causing the method to fall through to "Errore Game".
     */
    @Test
    public void testAdvantageP2_BugBehavior() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 40-40
        for(int i=0; i<3; i++) manager.pointScored(1);
        for(int i=0; i<3; i++) manager.pointScored(2);
        // P2 Scores for Advantage
        manager.pointScored(2);
        assertEquals("Errore Game", manager.getGameScore());
    }

    @Test
    public void testGameWinP1() {
        TennisScoreManager manager = new TennisScoreManager();
        // P1 scores 4 times (40 -> Win)
        for(int i=0; i<4; i++) manager.pointScored(1);
        // Check match score string for "Game: 1-0"
        assertTrue(manager.getMatchScore().contains("Game: 1-0"));
    }

    @Test
    public void testGameWinP2() {
        TennisScoreManager manager = new TennisScoreManager();
        // P2 scores 4 times
        for(int i=0; i<4; i++) manager.pointScored(2);
        assertTrue(manager.getMatchScore().contains("Game: 0-1"));
    }

    @Test
    public void testSetWinP1_Standard() {
        TennisScoreManager manager = new TennisScoreManager();
        // P1 wins 6 games straight
        for(int g=0; g<6; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
        }
        // Should be 1-0 in sets
        assertTrue(manager.getMatchScore().startsWith("1-0"));
    }

    @Test
    public void testSetWinP2_Standard() {
        TennisScoreManager manager = new TennisScoreManager();
        // P2 wins 6 games straight
        for(int g=0; g<6; g++) {
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        assertTrue(manager.getMatchScore().startsWith("0-1"));
    }

    @Test
    public void testSetWinP1_Extended_7_5() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 5-5
        for(int g=0; g<5; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        // P1 wins next 2 games -> 7-5
        for(int g=0; g<2; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
        }
        assertTrue(manager.getMatchScore().startsWith("1-0"));
    }

    @Test
    public void testSetWinP2_Extended_5_7() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 5-5
        for(int g=0; g<5; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        // P2 wins next 2 games -> 5-7
        for(int g=0; g<2; g++) {
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        assertTrue(manager.getMatchScore().startsWith("0-1"));
    }

    @Test
    public void testTieBreakTrigger() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 6-6
        for(int g=0; g<6; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        // Assert current score is TIE-BREAK
        assertTrue(manager.getMatchScore().contains("TIE-BREAK"));
    }

    @Test
    public void testTieBreakPointScoring() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 6-6
        for(int g=0; g<6; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        // P1 scores 1 point in tiebreak
        manager.pointScored(1);
        assertEquals("TIE-BREAK: 1-0", manager.getTieBreakScore());
    }

	@Test
    public void testTieBreakWinP2() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 6-6
        for(int g=0; g<6; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        // P2 Wins TieBreak (7 points)
        for(int p=0; p<7; p++) manager.pointScored(2);
        
        // Note: The source code has a bug where resetGameAndPoints() is called before checkSetPoint() in checkTieBreakPoint(),
        // causing the game count to be wiped to 0-0 before the set win can be registered.
        // Therefore, the match score remains "0-0" instead of the expected "0-1". 
        // The assertion below reflects the actual behavior of the provided class.
        assertTrue(manager.getMatchScore().startsWith("0-0"));
    }

    /**
     * This test addresses the bug in checkSetPoint where the condition for P1 winning 
     * a set via Tiebreak (7-6) is missing/malformed. 
     * The test asserts the actual behavior: the set is NOT awarded to P1, 
     * and games stay at 7-6 in the current set.
     */
  	@Test
public void testTieBreakWinP1_BuggedBehavior() {
    TennisScoreManager manager = new TennisScoreManager();
    // Reach 6-6
    for(int g=0; g<6; g++) {
        for(int p=0; p<4; p++) manager.pointScored(1);
        for(int p=0; p<4; p++) manager.pointScored(2);
    }
    // P1 Wins TieBreak (7 points)
    for(int p=0; p<7; p++) manager.pointScored(1);
    
    // The bug in checkTieBreakPoint calls resetGameAndPoints() (resetting games to 0) 
    // BEFORE calling checkSetPoint(). Consequently, the games become 0-0, the set is not awarded,
    // and the tie-break flag is cleared.
    // The actual state is: Sets 0-0, Games 0-0, Points Love-Love.
    assertTrue(manager.getMatchScore().startsWith("0-0 (Game: 0-0"));
}

    @Test
    public void testMatchWinP1_3Sets() {
        TennisScoreManager manager = new TennisScoreManager();
        // P1 wins 3 sets of 6-0
        for(int s=0; s<3; s++) {
            for(int g=0; g<6; g++) {
                for(int p=0; p<4; p++) manager.pointScored(1);
            }
        }
        assertEquals("P1: 3 Set | P2: 0 Set", manager.getMatchScore());
    }

    @Test
    public void testMatchWinP2_3Sets() {
        TennisScoreManager manager = new TennisScoreManager();
        // P2 wins 3 sets of 0-6
        for(int s=0; s<3; s++) {
            for(int g=0; g<6; g++) {
                for(int p=0; p<4; p++) manager.pointScored(2);
            }
        }
        assertEquals("P1: 0 Set | P2: 3 Set", manager.getMatchScore());
    }

    @Test
    public void testPlayAfterMatchOver() {
        TennisScoreManager manager = new TennisScoreManager();
        // P1 Wins Match
        for(int s=0; s<3; s++) {
            for(int g=0; g<6; g++) {
                for(int p=0; p<4; p++) manager.pointScored(1);
            }
        }
        // Try to score again
        manager.pointScored(1);
        // Result shouldn't change
        assertEquals("P1: 3 Set | P2: 0 Set", manager.getMatchScore());
    }

    @Test
    public void testResetPointsDirectly() {
        TennisScoreManager manager = new TennisScoreManager();
        manager.pointScored(1);
        manager.resetPoints();
        assertEquals("Love-Love", manager.getGameScore());
    }
    
    @Test
    public void testResetGameAndPoints() {
        TennisScoreManager manager = new TennisScoreManager();
        manager.pointScored(1); // 15-0
        manager.resetGameAndPoints();
        assertEquals("Love-Love", manager.getGameScore());
    }

    @Test
    public void testTieBreak_7_6_NoWin() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 6-6 in games to trigger TieBreak
        for(int g=0; g<6; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        
        // Reach 6-6 in TieBreak points
        for(int p=0; p<6; p++) {
            manager.pointScored(1);
            manager.pointScored(2);
        }
        
        // P1 scores to make it 7-6
        manager.pointScored(1);
        
        // Condition covered: scoreP1 >= 7 (True) AND scoreP1 >= scoreP2 + 2 (False)
        // Expected: Game does not reset, score reflects 7-6
        assertTrue(manager.getMatchScore().contains("TIE-BREAK: 7-6"));
        // Ensure set score is still 0-0 (games 6-6)
        assertTrue(manager.getMatchScore().startsWith("0-0"));
    }

    @Test
    public void testTieBreak_6_7_NoWin() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 6-6 in games
        for(int g=0; g<6; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        
        // Reach 6-6 in TieBreak points
        for(int p=0; p<6; p++) {
            manager.pointScored(1);
            manager.pointScored(2);
        }
        
        // P2 scores to make it 6-7
        manager.pointScored(2);
        
        // Condition covered: scoreP2 >= 7 (True) AND scoreP2 >= scoreP1 + 2 (False)
        assertTrue(manager.getMatchScore().contains("TIE-BREAK: 6-7"));
        assertTrue(manager.getMatchScore().startsWith("0-0"));
    }

    @Test
    public void testSetScore_6_5_NoWin() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 5-5
        for(int g=0; g<5; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        
        // P1 wins next game -> 6-5
        for(int p=0; p<4; p++) manager.pointScored(1);
        
        // Condition covered in checkSetPoint: 
        // gamesP1 >= 6 (True) AND gamesP1 >= gamesP2 + 2 (False)
        // Result: No set win, match continues
        assertTrue(manager.getMatchScore().contains("Game: 6-5"));
        assertTrue(manager.getMatchScore().startsWith("0-0"));
    }
    
    @Test
    public void testSetScore_5_6_NoWin() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach 5-5
        for(int g=0; g<5; g++) {
            for(int p=0; p<4; p++) manager.pointScored(1);
            for(int p=0; p<4; p++) manager.pointScored(2);
        }
        
        // P2 wins next game -> 5-6
        for(int p=0; p<4; p++) manager.pointScored(2);
        
        // Condition covered in checkSetPoint (else if): 
        // gamesP2 >= 6 (True) AND gamesP2 >= gamesP1 + 2 (False)
        assertTrue(manager.getMatchScore().contains("Game: 5-6"));
        assertTrue(manager.getMatchScore().startsWith("0-0"));
    }

    @Test
    public void testAdvantageP2_ConditionCombinations() {
        TennisScoreManager manager = new TennisScoreManager();
        // Reach Deuce (3-3)
        for(int i=0; i<3; i++) manager.pointScored(1);
        for(int i=0; i<3; i++) manager.pointScored(2);
        
        // P2 scores -> Advantage P2 (3-4)
        manager.pointScored(2);
        
        // Covers getGameScore conditions:
        // 1. (scoreP1 == scoreP2 && scoreP1 >= 3) -> False (3!=4) && True -> False
        // 2. (scoreP1 >= 3 && scoreP1 == scoreP2 + 1) -> True (3>=3) && False (3!=5) -> False
        // 3. (scoreP2 >= 3 && scoreP2 == scoreP2 + 1) -> True && False (Bug in code) -> False
        
        // Due to bug, returns "Errore Game"
        assertEquals("Errore Game", manager.getGameScore());
    }

}

						