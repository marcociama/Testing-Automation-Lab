import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class TestTestSubjectParser_P105_G1211R1 {

    // --- getId() Tests ---

    @Test
    public void GetIdValidTest() {
        String subject = "12345 Valid Title";
        SubjectParser parser = new SubjectParser(subject);
        assertEquals(12345L, parser.getId());
    }

    @Test
    public void GetIdInvalidTextTest() {
        String subject = "InvalidId Valid Title";
        SubjectParser parser = new SubjectParser(subject);
        // Expect -1 when Long.parseLong fails
        assertEquals(-1L, parser.getId());
    }

    @Test
    public void GetIdNullSubjectTest() {
        SubjectParser parser = new SubjectParser(null);
        // Expect -1 when StringTokenizer throws NullPointerException inside getId
        assertEquals(-1L, parser.getId());
    }

    // --- getTitle() and getRangeString() Tests ---

    @Test
    public void GetTitleParenthesesRangeTest() {
        String subject = "100 Title Check (1/5)";
        SubjectParser parser = new SubjectParser(subject);
        
        assertEquals("Title Check ", parser.getTitle());
        assertEquals("(1/5)", parser.getRangeString());
    }

    @Test
    public void GetTitleBracketsRangeTest() {
        String subject = "100 Title Check [1/5]";
        SubjectParser parser = new SubjectParser(subject);
        
        assertEquals("Title Check ", parser.getTitle());
        assertEquals("[1/5]", parser.getRangeString());
    }

    @Test
    public void GetTitleNoRangeTest() {
        String subject = "100 Title Check No Range";
        SubjectParser parser = new SubjectParser(subject);
        
        // Loop runs, finds no range, returns full string (minus ID part)
        assertEquals("Title Check No Range", parser.getTitle());
        // RangeString remains null
        assertNull(parser.getRangeString());
    }

    @Test
    public void GetTitleBrokenRangeNonDigitTest() {
        // Test loop logic: Character.isDigit(nextchar) == false check
        String subject = "100 Title (1a/5)";
        SubjectParser parser = new SubjectParser(subject);
        
        assertEquals("Title (1a/5)", parser.getTitle());
        assertNull(parser.getRangeString());
    }

    @Test
    public void GetTitleBrokenRangeNoSlashTest() {
        // Test logic: Missing slash swallows the parentheses content
        String subject = "100 Title (15)";
        SubjectParser parser = new SubjectParser(subject);
        
        assertEquals("Title ", parser.getTitle());
        assertNull(parser.getRangeString());
    }

    @Test
    public void GetTitleMultiplePossibleRangesTest() {
        // Logic starts from end (i--). Should pick the last one.
        String subject = "100 Title (1/2) real (3/4)";
        SubjectParser parser = new SubjectParser(subject);
        
        assertEquals("Title (1/2) real ", parser.getTitle());
        assertEquals("(3/4)", parser.getRangeString());
    }

    @Test
    public void GetTitleNullSubjectTest() {
        SubjectParser parser = new SubjectParser(null);
        // Subject.substring throws NPE, caught by catch(Exception parseE), returns null
        assertNull(parser.getTitle());
    }
    
    @Test
    public void GetTitleNoSpaceTest() {
        // Logic: Subject.substring(Subject.indexOf(" ") + 1...
        String subject = "OneWordSubject";
        SubjectParser parser = new SubjectParser(subject);
        
        assertEquals("OneWordSubject", parser.getTitle());
    }

    // --- getThisRange() and getUpperRange() / messageParts() Tests ---

    @Test
    public void GetThisRangeStandardTest() {
        String subject = "100 Title (2/5)";
        SubjectParser parser = new SubjectParser(subject);
        
        // Parses (2/5) successfully in first try block of messageParts
        assertEquals(2, parser.getThisRange());
        assertEquals(5, parser.getUpperRange());
    }

    @Test
    public void GetThisRangeBracketsTest() {
        String subject = "100 Title [3/6]";
        SubjectParser parser = new SubjectParser(subject);
        
        // Falls through to catch(inte) then tries brackets logic
        assertEquals(3, parser.getThisRange());
        assertEquals(6, parser.getUpperRange());
    }

    @Test
    public void GetThisRangeNullRangeTest() {
        String subject = "100 Title No Range";
        SubjectParser parser = new SubjectParser(subject);
        
        assertEquals(1, parser.getThisRange());
        assertEquals(1, parser.getUpperRange());
    }

    @Test
    public void GetThisRangeOverflowTest() {
        // Forces inner number format exception, then falls back to brackets, fails, returns null
        String subject = "100 Title (9999999999/1)";
        SubjectParser parser = new SubjectParser(subject);
        
        assertEquals(1, parser.getThisRange());
        assertEquals(1, parser.getUpperRange());
    }

    // --- Special Coverage Tests (Subclassing to force exceptions) ---

    @Test
    public void GetRangeStringExceptionTest() {
        // We force the catch block in getRangeString() to be executed.
        // We do this by overriding getTitle() to throw a RuntimeException.
        SubjectParser parser = new SubjectParser("Subject") {
            @Override
            public String getTitle() {
                throw new RuntimeException("Forced Error");
            }
        };

        // getRangeString calls getTitle -> throws Exception -> caught by getRangeString -> returns null
        assertNull(parser.getRangeString());
    }

    @Test
    public void MessagePartsOuterExceptionTest() {
        // We force the OUTER catch block in messageParts() to be executed.
        // We do this by overriding getRangeString() to throw a RuntimeException.
        SubjectParser parser = new SubjectParser("Subject") {
            @Override
            public String getRangeString() {
                throw new RuntimeException("Forced Error");
            }
        };

        // getThisRange calls messageParts -> calls getRangeString -> throws Exception
        // messageParts catches Exception -> prints stack trace -> returns null
        // getThisRange receives null -> does nothing -> returns default (1)
        assertEquals(1, parser.getThisRange());
    }
}