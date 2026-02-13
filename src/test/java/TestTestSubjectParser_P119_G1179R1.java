import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Arrays;

public class TestTestSubjectParser_P119_G1179R1 {

    // --- Helper per la Reflection ---
    // Necessari per testare lo stato interno e metodi privati garantendo copertura atomica.

    private Object getField(Object instance, String fieldName) throws Exception {
        Field field = SubjectParser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }
    
    private void setField(Object instance, String fieldName, Object value) throws Exception {
        Field field = SubjectParser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    private int[] invokeMessageParts(SubjectParser parser) throws Exception {
        Method method = SubjectParser.class.getDeclaredMethod("messageParts");
        method.setAccessible(true);
        return (int[]) method.invoke(parser);
    }

    // --- Costruttore ---

    @Test
    public void SubjectParserConstructorInitializationTest() throws Exception {
        String s = "Test Subject";
        SubjectParser parser = new SubjectParser(s);
        // Verifica inizializzazione corretta dei campi
        assertEquals("Subject field mismatch", s, getField(parser, "Subject"));
        assertEquals("UpperRange default mismatch", 1, getField(parser, "UpperRange"));
        assertEquals("LowerRange default mismatch", 1, getField(parser, "LowerRange"));
    }

    // --- getId Tests ---

    @Test
    public void getIdValidLongTest() {
        SubjectParser parser = new SubjectParser("123456 Subject");
        assertEquals(123456L, parser.getId());
    }

    @Test
    public void getIdInvalidNumberTest() {
        // Copre il catch(Exception e) in getId quando Long.parseLong fallisce
        SubjectParser parser = new SubjectParser("ABC Subject");
        assertEquals(-1L, parser.getId());
    }

    @Test
    public void getIdEmptyStringTest() {
        // Copre il caso in cui StringTokenizer non ha token (NoSuchElementException)
        SubjectParser parser = new SubjectParser("");
        assertEquals(-1L, parser.getId());
    }

    // --- getTitle Tests ---
    
    // Scenario: Range standard con parentesi tonde (X/Y) alla fine
    @Test
    public void getTitleParenthesesRangeTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 Title (5/10)");
        String title = parser.getTitle();
        // Verifica che il range sia stato rimosso dal titolo e salvato in RangeString
        assertEquals("Title ", title);
        assertEquals("(5/10)", getField(parser, "RangeString"));
    }

    // Scenario: Range standard con parentesi quadre [X/Y] alla fine
    @Test
    public void getTitleBracketRangeTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 Title [5/10]");
        String title = parser.getTitle();
        assertEquals("Title ", title);
        assertEquals("[5/10]", getField(parser, "RangeString"));
    }

    // Scenario: Nessun range presente
    @Test
    public void getTitleNoRangeTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 Just A Title");
        String title = parser.getTitle();
        assertEquals("Just A Title", title);
        assertNull(getField(parser, "RangeString"));
    }

    // Scenario: Multipli range.
    // La logica parsa all'indietro. Il primo trovato (l'ultimo nella stringa) viene preso.
    // I successivi (precedenti nella stringa) vengono ignorati perché FoundRange diventa true.
    @Test
    public void getTitleMultipleRangesTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 Title (1/2) suffix (3/4)");
        String title = parser.getTitle();
        // (3/4) viene processato come range. (1/2) viene trattato come testo normale nel ramo 'else'.
        assertEquals("Title (1/2) suffix ", title);
        assertEquals("(3/4)", getField(parser, "RangeString"));
    }

    // Scenario: Caratteri invalidi all'interno dei delimitatori
    @Test
    public void getTitleInvalidCharInRangeTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 Title (1/A)");
        String title = parser.getTitle();
        // Il loop interno trova 'A', che non è digit e non è '/'.
        // Esegue 'continue MAINLOOP', reinserendo il buffer nel titolo.
        assertEquals("Title (1/A)", title);
        assertNull(getField(parser, "RangeString"));
    }

    // Scenario: Delimitatori validi e numeri, ma manca lo slash '/'
    // La logica verifica `if (tmpbuf.toString().indexOf("/") != -1)`. Se falso, non imposta RangeString.
    // Nota: I caratteri consumati dal while interno non vengono reinseriti in sb in questo specifico path,
    // risultando in una rimozione della stringa "(123)" dal titolo ma senza impostare RangeString.
    @Test
    public void getTitleRangeWithoutSlashTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 Title (123)");
        String title = parser.getTitle();
        assertEquals("Title ", title); // (123) rimosso ma non è range valido
        assertNull(getField(parser, "RangeString"));
    }

    // Scenario: Parsing fallisce con eccezione (es. Subject null -> NullPointerException)
    @Test
    public void getTitleExceptionNoSpaceTest() {
        SubjectParser parser = new SubjectParser(null); // Subject null causa NPE in indexOf, catch ritorna null
        assertNull(parser.getTitle());
    }

    // Scenario: Parsing fallisce con eccezione nel loop while (parentesi aperta mancante)
    // Modificato: Rimosso "Title" e spazio per evitare abort safe del loop
    @Test
    public void getTitleMissingOpeningParenTest() {
        SubjectParser parser = new SubjectParser("123 5/10)"); 
        // Trova ')', entra nel while, decrementa i fino a -1 -> StringIndexOutOfBoundsException
        assertNull(parser.getTitle());
    }

    // --- getRangeString Tests ---
    
    @Test
    public void getRangeStringCachedTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 Title");
        setField(parser, "RangeString", "(1/1)"); // Simula cache già popolata
        assertEquals("(1/1)", parser.getRangeString());
    }

    @Test
    public void getRangeStringNotCachedTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 Title (2/2)");
        // RangeString è null, chiama getTitle internamente
        assertEquals("(2/2)", parser.getRangeString());
    }

    @Test
    public void getRangeStringExceptionTest() throws Exception {
        SubjectParser parser = new SubjectParser("1"); // Forza getTitle a ritornare null
        assertNull(parser.getRangeString());
    }

    // --- messageParts (Private logic) Tests ---

    // 1. Valid Parentheses
    @Test
    public void messagePartsValidParenTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 T (10/20)");
        parser.getTitle(); // Popola RangeString
        int[] parts = invokeMessageParts(parser);
        assertNotNull(parts);
        assertEquals(10, parts[0]);
        assertEquals(20, parts[1]);
    }

    // 2. Valid Brackets (Primo try fallisce, secondo try ha successo)
    @Test
    public void messagePartsValidBracketTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 T [30/40]");
        parser.getTitle();
        int[] parts = invokeMessageParts(parser);
        assertNotNull(parts);
        assertEquals(30, parts[0]);
        assertEquals(40, parts[1]);
    }

    // 3. Entrambi i formati invalidi (Catch inner subE)
    @Test
    public void messagePartsBothInvalidTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 T");
        setField(parser, "RangeString", "Invalid");
        // Try 1: lastIndexOf("(") fallisce -> Exception
        // Try 2: lastIndexOf("[") fallisce -> Exception -> return null
        int[] parts = invokeMessageParts(parser);
        assertNull(parts);
    }

    // 4. Tokenizer fallisce nel primo try (Parentesi trovata, contenuto invalido)
    @Test
    public void messagePartsParenTokenizerExceptionTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 T");
        setField(parser, "RangeString", "(1)"); 
        // Tokenizer("1", "/") -> nextToken="1". Secondo nextToken -> NoSuchElementException.
        // Catch 'inte'. Poi prova bracket, fallisce. Return null.
        assertNull(invokeMessageParts(parser));
    }

    // 5. Tokenizer fallisce nel secondo try (Quadra trovata, contenuto invalido)
    @Test
    public void messagePartsBracketTokenizerExceptionTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 T");
        setField(parser, "RangeString", "[1]");
        // Try 1 fallisce (manca '(').
        // Try 2 trova '[', ma Tokenizer fallisce su secondo nextToken.
        // Catch 'subE'. Return null.
        assertNull(invokeMessageParts(parser));
    }

    // 6. Eccezione Esterna (Outer Catch)
    @Test
    public void messagePartsOuterExceptionTest() throws Exception {
        SubjectParser parser = new SubjectParser("123 T");
        // getRangeString ritorna null (perché getTitle non è stato chiamato o ha fallito parzialmente in setup manuale)
        // mainrange è null. mainrange.substring -> NullPointerException.
        // Catch outer (Exception e). Return null.
        int[] parts = invokeMessageParts(parser);
        assertNull(parts);
    }

    // --- getThisRange Tests ---

    @Test
    public void getThisRangeValidTest() {
        SubjectParser parser = new SubjectParser("123 T (5/10)");
        assertEquals(5, parser.getThisRange());
    }

    @Test
    public void getThisRangeInvalidTest() {
        SubjectParser parser = new SubjectParser("123 T"); // messageParts ritorna null
        assertEquals(1, parser.getThisRange()); // Ritorna default LowerRange
    }

    @Test
    public void getThisRangeExceptionTest() throws Exception {
        // Test per coprire il catch(Exception e) vuoto in getThisRange
        SubjectParser parser = new SubjectParser("123 T");
        // Forziamo messageParts a lanciare un'eccezione non gestita internamente? 
        // Difficile dato che messageParts ha un catch-all.
        // Tuttavia, possiamo settare messageParts return a null e verificare che if(parts!=null) sia saltato.
        assertEquals(1, parser.getThisRange());
    }

    // --- getUpperRange Tests ---

    @Test
    public void getUpperRangeValidTest() {
        SubjectParser parser = new SubjectParser("123 T (5/10)");
        assertEquals(10, parser.getUpperRange());
    }

    @Test
    public void getUpperRangeInvalidTest() {
        SubjectParser parser = new SubjectParser("123 T");
        assertEquals(1, parser.getUpperRange()); // Ritorna default UpperRange
    }
}