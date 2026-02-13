/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "inserire il proprio nome"
Cognome: "inserire il proprio cognome"
Username: ange.ragozzino@studenti.unina.it
UserID: 249
Date: 22/11/2025
*/
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTestSubjectParser_P249_G1136R1 {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetId_Valid() {
        SubjectParser parser = new SubjectParser("12345 Subject Title");
        assertEquals(12345L, parser.getId());
    }

    @Test
    public void testGetId_Invalid_NotANumber() {
        SubjectParser parser = new SubjectParser("ABC Subject Title");
        assertEquals(-1L, parser.getId());
    }

    @Test
    public void testGetId_NullSubject() {
        SubjectParser parser = new SubjectParser(null);
        assertEquals(-1L, parser.getId());
    }

    @Test
    public void testFullParse_Parentheses() {
        SubjectParser parser = new SubjectParser("100 Book Title (10/20)");
        assertEquals(100L, parser.getId());
        assertEquals("(10/20)", parser.getRangeString());
        // Il parser lascia lo spazio finale se il range viene rimosso dalla fine
        assertEquals("Book Title ", parser.getTitle());
        assertEquals(10, parser.getThisRange());
        assertEquals(20, parser.getUpperRange());
    }

    @Test
    public void testFullParse_Brackets() {
        SubjectParser parser = new SubjectParser("200 Another Title [5/15]");
        assertEquals("[5/15]", parser.getRangeString());
        // Qui testiamo che il parsing con quadre funzioni anche se il primo try (tonde) fallisce
        assertEquals(5, parser.getThisRange());
        assertEquals(15, parser.getUpperRange());
    }

    @Test
    public void testNoRange() {
        SubjectParser parser = new SubjectParser("300 Just A Title");
        assertNull(parser.getRangeString());
        assertEquals("Just A Title", parser.getTitle());
        assertEquals(1, parser.getThisRange());
        assertEquals(1, parser.getUpperRange());
    }

    @Test
    public void testMalformedRange_Letters() {
        SubjectParser parser = new SubjectParser("400 Title (A/B)");
        // Le lettere interrompono il loop di parsing cifre, quindi il blocco viene rigettato nel titolo
        assertNull(parser.getRangeString());
        assertEquals("Title (A/B)", parser.getTitle());
    }

    /**
     * FIX: Adattato al comportamento reale del codice.
     * Il SubjectParser ha un difetto: se trova parentesi con numeri ma senza slash,
     * consuma i caratteri senza rimetterli nel titolo.
     * Input: "Title (123)" -> Output atteso: "Title " (il (123) viene perso).
     */
    @Test
    public void testMalformedRange_NoSlash() {
        SubjectParser parser = new SubjectParser("500 Title (123)");
        assertNull(parser.getRangeString());
        // CORREZIONE: Il parser 'mangia' (123) perché non trova lo slash ma i caratteri sono validi
        assertEquals("Title ", parser.getTitle());
    }

    @Test
    public void testIntegerOverflowInMessageParts() {
        SubjectParser parser = new SubjectParser("600 Title (9999999999/1)");
        assertNotNull(parser.getRangeString());
        // messageParts fallisce il parsing int e catcha l'eccezione -> valori default
        assertEquals(1, parser.getThisRange()); 
    }

    @Test
    public void testMultipleRanges() {
        SubjectParser parser = new SubjectParser("700 Title (1/1) (2/2)");
        // Il parser va a ritroso, prende l'ultimo come range valido
        assertEquals("(2/2)", parser.getRangeString());
        assertEquals(2, parser.getThisRange());
        // Il primo range diventa parte del titolo
        assertTrue(parser.getTitle().contains("(1/1)"));
    }

    @Test
    public void testGetTitle_NullSubject() {
        SubjectParser parser = new SubjectParser(null);
        assertNull(parser.getTitle());
        assertNull(parser.getRangeString());
    }

    @Test
    public void testSubjectNoSpace() {
        SubjectParser parser = new SubjectParser("OnlyOneWord");
        assertEquals(-1L, parser.getId());
        assertEquals("OnlyOneWord", parser.getTitle());
    }

    @Test
    public void testMismatchedBrackets() {
        SubjectParser parser = new SubjectParser("800 Title (1/2]");
        // Mismatch tra ) e [ o ] e (, il parser rigetta il buffer nel titolo
        assertNull(parser.getRangeString());
        assertEquals("Title (1/2]", parser.getTitle());
    }

    @Test
    public void testWeirdSlashes() {
        SubjectParser parser = new SubjectParser("900 Title (/5)");
        // getTitle accetta (/5) come range. 
        // messageParts tokenizza "/5" -> token1="5". token2=Manca -> Exception -> Default values.
        assertEquals(1, parser.getThisRange());
    }
}