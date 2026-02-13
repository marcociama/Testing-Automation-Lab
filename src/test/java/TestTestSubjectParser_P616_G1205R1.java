import org.junit.Test;
import static org.junit.Assert.*;

public class TestTestSubjectParser_P616_G1205R1 {

    private SubjectParser parser;

    // ==================== Test Costruttore ====================

    @Test
    public void constructor_subjectValido_inizializzaRangeDefault() {
        parser = new SubjectParser("12345 Test Subject");
        assertEquals(1, parser.getThisRange());
        assertEquals(1, parser.getUpperRange());
    }

    // ==================== Test getId() ====================

    @Test
    public void getId_subjectConIdNumerico_restituisceId() {
        parser = new SubjectParser("12345 Test Subject");
        assertEquals(12345L, parser.getId());
    }

    @Test
    public void getId_subjectConIdZero_restituisceZero() {
        // BVA: Boundary value - zero
        parser = new SubjectParser("0 Test Subject");
        assertEquals(0L, parser.getId());
    }

    @Test
    public void getId_subjectConIdNegativo_restituisceIdNegativo() {
        // BVA: Boundary value - numero negativo
        parser = new SubjectParser("-1 Test Subject");
        assertEquals(-1L, parser.getId());
    }

    @Test
    public void getId_subjectSenzaNumeroIniziale_restituisceMenoUno() {
        parser = new SubjectParser("NotANumber Test");
        assertEquals(-1L, parser.getId());
    }

    @Test
    public void getId_subjectVuoto_restituisceMenoUno() {
        parser = new SubjectParser("");
        assertEquals(-1L, parser.getId());
    }

    @Test
    public void getId_subjectNull_restituisceMenoUno() {
        parser = new SubjectParser(null);
        assertEquals(-1L, parser.getId());
    }

    @Test
    public void getId_subjectSoloNumero_restituisceNumero() {
        parser = new SubjectParser("999");
        assertEquals(999L, parser.getId());
    }

    // ==================== Test getTitle() ====================

    @Test
    public void getTitle_subjectConRangeTonde_rimuoveRange() {
        parser = new SubjectParser("123 Test Title (1/5)");
        String title = parser.getTitle();
        assertEquals("Test Title ", title);
    }

    @Test
    public void getTitle_subjectConRangeQuadre_rimuoveRange() {
        parser = new SubjectParser("123 Another Test [3/10]");
        String title = parser.getTitle();
        assertEquals("Another Test ", title);
    }

    @Test
    public void getTitle_subjectSenzaRange_restituisceTitoloCompleto() {
        parser = new SubjectParser("123 Simple Message");
        String title = parser.getTitle();
        assertEquals("Simple Message", title);
    }

    @Test
    public void getTitle_rangeConCaratteriNonNumerici_includeRangeNelTitolo() {
        // NOTA: L'asserzione verifica il comportamento attuale che include il range
        // non valido nel titolo, dato che contiene caratteri non numerici
        parser = new SubjectParser("123 Message (invalid) test");
        String title = parser.getTitle();
        assertTrue(title.contains("(invalid)"));
    }

    @Test
    public void getTitle_rangeMultipli_rimuoveSoloUltimoRangeValido() {
        parser = new SubjectParser("123 Multiple (1/2) ranges (3/4)");
        String title = parser.getTitle();
        // Verifica che il primo range sia ancora presente
        assertTrue(title.contains("(1/2)"));
        // Verifica che l'ultimo range valido sia stato rimosso
        assertFalse(title.contains("(3/4)"));
    }

    @Test
    public void getTitle_subjectMoltoCorto_gestisceCorrettamente() {
        // BVA: Boundary - subject minimo
        parser = new SubjectParser("1 T");
        String title = parser.getTitle();
        assertEquals("T", title);
    }

    @Test
    public void getTitle_caratteriSpeciali_preservaCaratteri() {
        parser = new SubjectParser("123 Ti#tle$ with @special! (2/5)");
        String title = parser.getTitle();
        assertTrue(title.contains("#"));
        assertTrue(title.contains("$"));
        assertTrue(title.contains("@"));
    }

    // ==================== Test getRangeString() ====================

    @Test
    public void getRangeString_rangeConParentesiTonde_restituisceRangeCompleto() {
        parser = new SubjectParser("123 Test (1/5)");
        parser.getTitle(); // Popola RangeString
        String rangeString = parser.getRangeString();
        assertEquals("(1/5)", rangeString);
    }

    @Test
    public void getRangeString_rangeConParentesiQuadre_restituisceRangeCompleto() {
        parser = new SubjectParser("123 Test [3/10]");
        parser.getTitle(); // Popola RangeString
        String rangeString = parser.getRangeString();
        assertEquals("[3/10]", rangeString);
    }

    @Test
    public void getRangeString_senzaRange_restituisceNull() {
        parser = new SubjectParser("123 Simple Message");
        String rangeString = parser.getRangeString();
        assertNull(rangeString);
    }

    @Test
    public void getRangeString_primaDiGetTitle_invocaGetTitleAutomaticamente() {
        parser = new SubjectParser("123 Test (2/8)");
        // Non chiamiamo getTitle() esplicitamente
        String rangeString = parser.getRangeString();
        assertNotNull(rangeString);
        assertEquals("(2/8)", rangeString);
    }

    // ==================== Test getThisRange() ====================

    @Test
    public void getThisRange_rangeValidoTonde_restituisceLowerBound() {
        parser = new SubjectParser("123 Test (3/5)");
        assertEquals(3, parser.getThisRange());
    }

    @Test
    public void getThisRange_rangeValidoQuadre_restituisceLowerBound() {
        parser = new SubjectParser("123 Test [7/10]");
        assertEquals(7, parser.getThisRange());
    }

    @Test
    public void getThisRange_rangeZero_restituisceZero() {
        // BVA: Boundary - valore zero
        parser = new SubjectParser("123 Test (0/5)");
        assertEquals(0, parser.getThisRange());
    }

    @Test
    public void getThisRange_senzaRange_restituisceDefaultUno() {
        parser = new SubjectParser("123 Simple Message");
        assertEquals(1, parser.getThisRange());
    }

    @Test
    public void getThisRange_rangeNonValido_restituisceDefaultUno() {
        parser = new SubjectParser("123 Test (abc/def)");
        assertEquals(1, parser.getThisRange());
    }

    @Test
    public void getThisRange_numeroMultipleCifre_gestisceCorrettamente() {
        parser = new SubjectParser("123 Test (123/456)");
        assertEquals(123, parser.getThisRange());
    }

    // ==================== Test getUpperRange() ====================

    @Test
    public void getUpperRange_rangeValidoTonde_restituisceUpperBound() {
        parser = new SubjectParser("123 Test (1/5)");
        assertEquals(5, parser.getUpperRange());
    }

    @Test
    public void getUpperRange_rangeValidoQuadre_restituisceUpperBound() {
        parser = new SubjectParser("123 Test [3/10]");
        assertEquals(10, parser.getUpperRange());
    }

    @Test
    public void getUpperRange_rangeZero_restituisceZero() {
        // BVA: Boundary - valore zero
        parser = new SubjectParser("123 Test (5/0)");
        assertEquals(0, parser.getUpperRange());
    }

    @Test
    public void getUpperRange_senzaRange_restituisceDefaultUno() {
        parser = new SubjectParser("123 Simple Message");
        assertEquals(1, parser.getUpperRange());
    }

    @Test
    public void getUpperRange_numeroGrande_gestisceCorrettamente() {
        // BVA: Boundary - numero grande
        parser = new SubjectParser("123 Test (1/999)");
        assertEquals(999, parser.getUpperRange());
    }

    // ==================== Test di Integrazione ====================

    @Test
    public void integrazione_parsingCompletoConTuttiComponenti_verificaCoerenza() {
        parser = new SubjectParser("12345 Complete Message (2/8)");
        
        // Verifica ID
        assertEquals(12345L, parser.getId());
        
        // Verifica Title
        String title = parser.getTitle();
        assertEquals("Complete Message ", title);
        
        // Verifica Range
        assertEquals(2, parser.getThisRange());
        assertEquals(8, parser.getUpperRange());
        assertEquals("(2/8)", parser.getRangeString());
    }

    @Test
    public void integrazione_subjectSenzaRange_verificaValoriDefault() {
        parser = new SubjectParser("54321 Simple Subject");
        
        assertEquals(54321L, parser.getId());
        assertEquals("Simple Subject", parser.getTitle());
        assertEquals(1, parser.getThisRange());
        assertEquals(1, parser.getUpperRange());
        assertNull(parser.getRangeString());
    }

    @Test
    public void integrazione_rangeQuadreCompleto_verificaTuttiMetodi() {
        parser = new SubjectParser("99999 Square Test [15/30]");
        
        assertEquals(99999L, parser.getId());
        assertEquals("Square Test ", parser.getTitle());
        assertEquals(15, parser.getThisRange());
        assertEquals(30, parser.getUpperRange());
        assertEquals("[15/30]", parser.getRangeString());
    }

    
}