/*
Nome: Matteo 
Cognome: De Luca 
Username: matteo.deluca3@studenti.unina.it
UserID: 621
Date: 28/10/2025
*/

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTestSubjectParser_P621_G366R1 {

    private static SubjectParser sharedParser;
    private SubjectParser parser;

    @BeforeClass
    public static void setUpClass() {
        sharedParser = new SubjectParser("0 Test (1/1)");
    }

    @AfterClass
    public static void tearDownClass() {
        sharedParser = null;
    }

    @Before
    public void setUp() {
        parser = new SubjectParser("12345 Messaggio di prova (3/10)");
    }

    @After
    public void tearDown() {
        parser = null;
    }

    @Test
    public void testGetId_valido() {
        long id = parser.getId();
        assertEquals(12345L, id);
    }

    @Test
    public void testGetId_nonValido() {
        SubjectParser p = new SubjectParser("abc nessun id valido");
        long id = p.getId();
        assertEquals(-1L, id);
    }

    @Test
    public void testGetTitle_eRangeString_parentesi() {
        String title = parser.getTitle();
        String range = parser.getRangeString();

        assertEquals("Messaggio di prova ", title);
        assertEquals("(3/10)", range);
    }

    @Test
    public void testGetThisRange_eGetUpperRange_parentesi() {
        int lower = parser.getThisRange();
        int upper = parser.getUpperRange();

        assertEquals(3, lower);
        assertEquals(10, upper);
    }

    @Test
    public void testGetTitle_eRangeString_brackets() {
        SubjectParser p = new SubjectParser("999 Titolo alternativo [4/7]");

        String title = p.getTitle();
        String range = p.getRangeString();

        assertEquals("Titolo alternativo ", title);
        assertEquals("[4/7]", range);

        assertEquals(4, p.getThisRange());
        assertEquals(7, p.getUpperRange());
    }

    @Test
    public void testGetRangeSenzaRange() {
        SubjectParser p = new SubjectParser("42 Titolo senza range");
        String title = p.getTitle();
        String range = p.getRangeString();

        assertEquals("Titolo senza range", title);
        assertNull(range);

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());
    }

    @Test
    public void testGetRangeString_lazyInit() {
        SubjectParser p = new SubjectParser("888 Lazy test (5/9)");

        String rangeBeforeTitle = p.getRangeString();
        assertEquals("(5/9)", rangeBeforeTitle);

        String titleAfter = p.getTitle();
        assertEquals("Lazy test ", titleAfter);

        assertEquals(5, p.getThisRange());
        assertEquals(9, p.getUpperRange());
    }

    @Test
    public void testGetTitle_rangeNonNumerico() {
        SubjectParser p = new SubjectParser("77 Titolo con testo (a/b)");

        String title = p.getTitle();
        String range = p.getRangeString();

        assertNotNull(title);
        assertTrue(title.contains("Titolo con testo"));
        assertNull(range);
    }

    @Test
    public void testGetRange_malformedMantieneDefault() {
        SubjectParser p = new SubjectParser("111 Oggetto rotto (3/x)");

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());

        assertNull(p.getRangeString());
    }

    @Test
    public void testGetThisRange_eGetUpperRange_cachePersistente() {
        SubjectParser p = new SubjectParser("555 Titolo cache (2/4)");

        assertEquals(2, p.getThisRange());
        assertEquals(4, p.getUpperRange());

        assertEquals(2, p.getThisRange());
        assertEquals(4, p.getUpperRange());
    }

    @Test
    public void testGetRangeString_quadreLazyInit() {
        SubjectParser p = new SubjectParser("321 Oggetto quadre [8/12]");

        String rangeDirect = p.getRangeString();
        assertEquals("[8/12]", rangeDirect);

        String titleAfter = p.getTitle();
        assertEquals("Oggetto quadre ", titleAfter);

        assertEquals(8, p.getThisRange());
        assertEquals(12, p.getUpperRange());
    }

    @Test
    public void testSoloIdSenzaSpazio() {
        SubjectParser p = new SubjectParser("99999");

        long id = p.getId();
        assertEquals(99999L, id);

        String title = p.getTitle();
        assertEquals("99999", title);

        assertNull(p.getRangeString());

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());
    }

    @Test
    public void testSubjectVuoto() {
        SubjectParser p = new SubjectParser("");

        long id = p.getId();
        assertEquals(-1L, id);

        String title = p.getTitle();
        assertEquals("", title);

        assertNull(p.getRangeString());

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());
    }

    @Test
    public void testRangeParzialeSoloInizio_parentesi() {
        SubjectParser p = new SubjectParser("10 Prova incompleta (4/");

        assertNull(p.getRangeString());

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());

        String title = p.getTitle();
        assertTrue(title.contains("Prova incompleta"));
    }

    @Test
    public void testRangeParzialeSoloInizio_quadre() {
        SubjectParser p = new SubjectParser("11 Prova quadre incomplete [7/");

        assertNull(p.getRangeString());

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());

        String title = p.getTitle();
        assertTrue(title.contains("Prova quadre incomplete"));
    }

    @Test
    public void testRangeInMezzoAlTitolo() {
        SubjectParser p = new SubjectParser("12 Titolo (3/5) extra testo");

        String title = p.getTitle();
        String range = p.getRangeString();

        assertEquals("(3/5)", range);

        assertTrue(title.contains("Titolo "));
        assertTrue(title.contains(" extra testo"));

        assertEquals(3, p.getThisRange());
        assertEquals(5, p.getUpperRange());
    }

    @Test
    public void testGetTitle_caratteriNonNumericiDentroParentesi() {
        SubjectParser p = new SubjectParser("13 Tizio (/) altro");

        String title = p.getTitle();
        String range = p.getRangeString();

        assertEquals("(/)", range);

        assertTrue(title.contains("Tizio"));
        assertFalse(title.contains("(/)"));
        assertTrue(title.contains("altro"));

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());
    }

    @Test
    public void testGetRangeString_dopoGetTitle_modificaStatoInterno() {
        SubjectParser p = new SubjectParser("14 Sequenza (6/9)");

        String t1 = p.getTitle();
        assertEquals("Sequenza ", t1);

        String r1 = p.getRangeString();
        assertEquals("(6/9)", r1);

        String t2 = p.getTitle();
        assertEquals("Sequenza ", t2);

        assertEquals(6, p.getThisRange());
        assertEquals(9, p.getUpperRange());
    }

    // -----------------------
    // nuovi test per eccezioni
    // -----------------------

    @Test
    public void testGetId_subjectNull() {
        SubjectParser p = new SubjectParser(null);
        long id = p.getId();
        assertEquals(-1L, id);
    }

    @Test
    public void testGetTitle_subjectNull() {
        SubjectParser p = new SubjectParser(null);

        String title = p.getTitle();
        assertNull(title);

        String range = p.getRangeString();
        assertNull(range);

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());
    }

    @Test
    public void testGetRangeString_subjectNull() {
        SubjectParser p = new SubjectParser(null);

        String range = p.getRangeString();
        assertNull(range);

        assertEquals(1, p.getThisRange());
        assertEquals(1, p.getUpperRange());
    }
}
