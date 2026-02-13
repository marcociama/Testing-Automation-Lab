/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "inserire il proprio nome"
Cognome: "inserire il proprio cognome"
Username: dom.mennillo@studenti.unina.it
UserID: 332
Date: 24/11/2025
*/

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestTestSubjectParser_P332_G1202R1 {


    /**
     * Test case 1: Normal scenario where the subject string starts with a valid positive long ID followed by text.
     * Path covered: StringTokenizer finds token -> Long.parseLong succeeds -> returns ID.
     */
    @Test
    public void testGetId_ValidPositiveIdWithTitle() {
        SubjectParser parser = new SubjectParser("12345 Test Subject Title");
        assertEquals(12345L, parser.getId());
    }

    /**
     * Test case 2: Scenario where the subject string contains only the number.
     * Path covered: StringTokenizer finds token -> Long.parseLong succeeds -> returns ID.
     */
    @Test
    public void testGetId_ValidIdOnly() {
        SubjectParser parser = new SubjectParser("987654321");
        assertEquals(987654321L, parser.getId());
    }

    /**
     * Test case 3: Scenario where the ID is a negative number.
     * Path covered: StringTokenizer finds token ("-100") -> Long.parseLong succeeds -> returns negative ID.
     */
    @Test
    public void testGetId_ValidNegativeId() {
        SubjectParser parser = new SubjectParser("-100 Negative Case");
        assertEquals(-100L, parser.getId());
    }

    /**
     * Test case 4: Scenario with leading whitespace.
     * Path covered: StringTokenizer ignores leading delimiters (spaces) -> finds "555" -> returns 555.
     */
    @Test
    public void testGetId_LeadingWhitespace() {
        SubjectParser parser = new SubjectParser("   555 Whitespace");
        assertEquals(555L, parser.getId());
    }

    /**
     * Test case 5: Scenario where the first token is not a number.
     * Path covered: StringTokenizer finds token "Title" -> Long.parseLong throws NumberFormatException -> Catch -> returns -1.
     */
    @Test
    public void testGetId_NonNumericStart() {
        SubjectParser parser = new SubjectParser("Title 123");
        assertEquals(-1L, parser.getId());
    }

    /**
     * Test case 6: Scenario where the number is mixed with letters (invalid format).
     * Path covered: StringTokenizer finds token "123A" -> Long.parseLong throws NumberFormatException -> Catch -> returns -1.
     */
    @Test
    public void testGetId_InvalidFormatMixed() {
        SubjectParser parser = new SubjectParser("123A Mixed");
        assertEquals(-1L, parser.getId());
    }

    /**
     * Test case 7: Scenario where the subject string is empty.
     * Path covered: StringTokenizer created -> nextToken throws NoSuchElementException -> Catch -> returns -1.
     */
    @Test
    public void testGetId_EmptyString() {
        SubjectParser parser = new SubjectParser("");
        assertEquals(-1L, parser.getId());
    }

    /**
     * Test case 8: Scenario where the subject string is null.
     * Path covered: new StringTokenizer(null) throws NullPointerException -> Catch -> returns -1.
     */
    @Test
    public void testGetId_NullString() {
        SubjectParser parser = new SubjectParser(null);
        assertEquals(-1L, parser.getId());
    }

    /**
     * Test case 9: Scenario where the number is too large for a long (Overflow).
     * Path covered: StringTokenizer finds token -> Long.parseLong throws NumberFormatException -> Catch -> returns -1.
     */
    @Test
    public void testGetId_Overflow() {
        // 9223372036854775808 is Long.MAX_VALUE + 1
        SubjectParser parser = new SubjectParser("9223372036854775808 Overflow");
        assertEquals(-1L, parser.getId());
    }

  /**
     * Test Case 1: Standard title with an ID and plain text.
     * Logic Path: No brackets found, full string traversal.
     * Input: "123 Simple Title"
     * Expected: "Simple Title"
     */
    @Test
    public void testGetTitle_SimpleText() {
        SubjectParser parser = new SubjectParser("123 Simple Title");
        assertEquals("Simple Title", parser.getTitle());
    }

    /**
     * Test Case 2: Title containing a valid range with round brackets.
     * Logic Path: Detects ')', scans back to '(', finds '/', sets RangeString, removes from title.
     * Input: "123 Story Part One (1/5)"
     * Expected: "Story Part One " (Note: trailing space remains as range is removed from end)
     */
    @Test
    public void testGetTitle_ValidRoundRange() {
        SubjectParser parser = new SubjectParser("123 Story Part One (1/5)");
        assertEquals("Story Part One ", parser.getTitle());
    }

    /**
     * Test Case 3: Title containing a valid range with square brackets.
     * Logic Path: Detects ']', scans back to '[', finds '/', sets RangeString, removes from title.
     * Input: "456 Story Part Two [2/5]"
     * Expected: "Story Part Two "
     */
    @Test
    public void testGetTitle_ValidSquareRange() {
        SubjectParser parser = new SubjectParser("456 Story Part Two [2/5]");
        assertEquals("Story Part Two ", parser.getTitle());
    }

    /**
     * Test Case 4: Title with brackets containing non-numeric/non-slash characters.
     * Logic Path: Detects ')', inner loop finds non-digit 'a', aborts range check, appends chars to title.
     * Input: "789 Invalid Range (1a/5)"
     * Expected: "Invalid Range (1a/5)"
     */
    @Test
    public void testGetTitle_InvalidCharInRange() {
        SubjectParser parser = new SubjectParser("789 Invalid Range (1a/5)");
        assertEquals("Invalid Range (1a/5)", parser.getTitle());
    }

    /**
     * Test Case 5: Title with numeric brackets but no slash.
     * Logic Path: Detects ')', scans back to '(', no '/' found in buffer. 
     * Current Logic Behavior: The content inside brackets is swallowed/ignored in the 'else' (implicit fall-through), not appended to title.
     * Input: "101 Year (2020)"
     * Expected: "Year " (2020 is removed but not set as RangeString)
     */
    @Test
    public void testGetTitle_NumericNoSlash() {
        SubjectParser parser = new SubjectParser("101 Year (2020)");
        assertEquals("Year ", parser.getTitle());
    }

    /**
     * Test Case 6: Title with multiple ranges.
     * Logic Path: Loop runs backwards. First range (at end) is detected and set. 'FoundRange' becomes true.
     * Second range (earlier in string) is encountered, 'FoundRange' is true, so treated as normal text.
     * Input: "999 Double (1/2) (3/4)"
     * Expected: "Double (1/2) " (Only (3/4) is stripped as the valid range)
     */
    @Test
    public void testGetTitle_MultipleRanges() {
        SubjectParser parser = new SubjectParser("999 Double (1/2) (3/4)");
        assertEquals("Double (1/2) ", parser.getTitle());
    }

    /**
     * Test Case 7: Malformed range - Closing bracket without opening bracket (Crash Scenario).
     * Logic Path: Detects ')', scans backwards looking for '('. If only digits/slash exist until start of string,
     * index goes out of bounds. Exception caught.
     * Input: "123 5/10)" (Note: Space is at index 3, substring starts after space: "5/10)")
     * Expected: null
     */
    @Test
    public void testGetTitle_MissingOpenBracket_Exception() {
        // "5/10)" contains only digits/slash before ')'. 
        // Inner loop runs until index -1 trying to find '('.
        SubjectParser parser = new SubjectParser("123 5/10)");
        assertNull(parser.getTitle());
    }

    /**
     * Test Case 8: Malformed range - Closing bracket without opening, but hits non-digit safety.
     * Logic Path: Detects ')', scans backwards, hits space (non-digit) before start of string. Aborts range check.
     * Input: "123 Title 5/10)"
     * Expected: "Title 5/10)"
     */
    @Test
    public void testGetTitle_MissingOpenBracket_Safe() {
        SubjectParser parser = new SubjectParser("123 Title 5/10)");
        assertEquals("Title 5/10)", parser.getTitle());
    }

    /**
     * Test Case 9: Null Subject.
     * Logic Path: Subject.substring throws NullPointerException. Caught.
     * Expected: null
     */
    @Test
    public void testGetTitle_NullSubject() {
        SubjectParser parser = new SubjectParser(null);
        assertNull(parser.getTitle());
    }

    /**
     * Test Case 10: Subject with no spaces.
     * Logic Path: Subject.indexOf(" ") returns -1. Substring starts at 0. Logic processes whole string.
     * Input: "NoSpacesHere(1/2)"
     * Expected: "NoSpacesHere"
     */
    @Test
    public void testGetTitle_NoSpaces() {
        SubjectParser parser = new SubjectParser("NoSpacesHere(1/2)");
        assertEquals("NoSpacesHere", parser.getTitle());
    }

    /**
     * Test Case 11: Nested or mixed brackets (Invalid Structure).
     * Logic Path: Scans ')', inner loop finds ')', which is not digit or slash. Aborts.
     * Input: "123 Complex (1/(2))"
     * Expected: "Complex (1/(2))"
     */
    @Test
    public void testGetTitle_NestedBrackets() {
        SubjectParser parser = new SubjectParser("123 Complex (1/(2))");
        assertEquals("Complex (1/(2))", parser.getTitle());
    }
   /**
     * Test Case 1: Range String extracted from Round Brackets.
     * Logic Path: RangeString is null -> getTitle() parsed -> RangeString set -> returned.
     */
    @Test
    public void testGetRangeString_RoundBrackets() {
        SubjectParser parser = new SubjectParser("123 Title (1/5)");
        assertEquals("(1/5)", parser.getRangeString());
    }

    /**
     * Test Case 2: Range String extracted from Square Brackets.
     * Logic Path: RangeString is null -> getTitle() parsed -> RangeString set -> returned.
     */
    @Test
    public void testGetRangeString_SquareBrackets() {
        SubjectParser parser = new SubjectParser("123 Title [2/10]");
        assertEquals("[2/10]", parser.getRangeString());
    }

    /**
     * Test Case 3: No Range String present in Subject.
     * Logic Path: RangeString is null -> getTitle() parsed -> no range found -> RangeString remains null -> returns null.
     */
    @Test
    public void testGetRangeString_NoRange() {
        SubjectParser parser = new SubjectParser("123 Title Without Range");
        assertNull(parser.getRangeString());
    }

    /**
     * Test Case 4: Range String is already cached (Pre-calculated).
     * Logic Path: RangeString is not null -> getTitle() is SKIPPED -> returns existing RangeString.
     * verification: We inject a value that does NOT match the subject to prove parsing didn't happen.
     */
    @Test
    public void testGetRangeString_CachedValue() throws Exception {
        SubjectParser parser = new SubjectParser("123 Title (1/5)");
        
        // Use reflection to set private field RangeString
        Field rangeField = SubjectParser.class.getDeclaredField("RangeString");
        rangeField.setAccessible(true);
        rangeField.set(parser, "InjectedValue");
        
        // Should return injected value, ignoring the actual subject "(1/5)"
        assertEquals("InjectedValue", parser.getRangeString());
    }

    /**
     * Test Case 5: Subject is null.
     * Logic Path: RangeString is null -> getTitle() called -> getTitle() throws/catches Exception (NPE) -> returns null -> getRangeString returns null.
     */
    @Test
    public void testGetRangeString_NullSubject() {
        SubjectParser parser = new SubjectParser(null);
        assertNull(parser.getRangeString());
    }

    /**
     * Test Case 6: Mixed/Invalid format where getTitle logic fails to set RangeString.
     * Logic Path: RangeString is null -> getTitle() called -> parses but finds valid tokens are not a range -> returns null.
     */
    @Test
    public void testGetRangeString_InvalidFormat() {
        // "1/5)" misses opening bracket, getTitle logic handles this safely but sets no range.
        SubjectParser parser = new SubjectParser("123 Title 1/5)");
        assertNull(parser.getRangeString());
    }
    /**
     * Helper method to use reflection to invoke the private method messageParts().
     */
    private int[] invokeMessageParts(SubjectParser parser) throws Exception {
        Method method = SubjectParser.class.getDeclaredMethod("messageParts");
        method.setAccessible(true);
        return (int[]) method.invoke(parser);
    }

    /**
     * Helper method to use reflection to set the private field RangeString.
     * This allows us to test messageParts() in isolation without relying on getTitle() parsing logic.
     */
    private void setRangeString(SubjectParser parser, String value) throws Exception {
        Field field = SubjectParser.class.getDeclaredField("RangeString");
        field.setAccessible(true);
        field.set(parser, value);
    }

    /**
     * Test Case 1: Valid Round Brackets logic.
     * Logic Path: 
     * 1. getRangeString returns "(1/5)".
     * 2. lastIndexOf("(") finds index.
     * 3. First parsing block succeeds.
     * 4. Returns [1, 5].
     */
    @Test
    public void testMessageParts_ValidRoundBrackets() throws Exception {
        SubjectParser parser = new SubjectParser("dummy");
        setRangeString(parser, "(1/5)");
        
        int[] result = invokeMessageParts(parser);
        assertNotNull(result);
        assertArrayEquals(new int[]{1, 5}, result);
    }

    /**
     * Test Case 2: Valid Square Brackets logic.
     * Logic Path: 
     * 1. getRangeString returns "[10/20]".
     * 2. lastIndexOf("(") returns -1 (or fails to find valid structure).
     * 3. Exception caught (inte).
     * 4. Enter nested Try block.
     * 5. lastIndexOf("[") finds index.
     * 6. Second parsing block succeeds.
     * 7. Returns [10, 20].
     */
    @Test
    public void testMessageParts_ValidSquareBrackets() throws Exception {
        SubjectParser parser = new SubjectParser("dummy");
        setRangeString(parser, "[10/20]");
        
        int[] result = invokeMessageParts(parser);
        assertNotNull(result);
        assertArrayEquals(new int[]{10, 20}, result);
    }

    /**
     * Test Case 3: Round Brackets with non-numeric data.
     * Logic Path: 
     * 1. getRangeString returns "(a/b)".
     * 2. First block tries to parse Integer.parseInt("a") -> throws NumberFormatException.
     * 3. Catch (inte).
     * 4. Enter nested Try block (Square).
     * 5. lastIndexOf("[") returns -1 -> throws Exception.
     * 6. Catch (subE).
     * 7. Returns null.
     */
    @Test
    public void testMessageParts_RoundBracketsNonNumeric() throws Exception {
        SubjectParser parser = new SubjectParser("dummy");
        setRangeString(parser, "(a/b)");
        
        int[] result = invokeMessageParts(parser);
        assertNull(result);
    }

    /**
     * Test Case 4: Square Brackets with non-numeric data.
     * Logic Path: 
     * 1. getRangeString returns "[a/b]".
     * 2. First block fails (no round brackets).
     * 3. Nested block tries to parse Integer.parseInt("a") -> throws NumberFormatException.
     * 4. Catch (subE).
     * 5. Returns null.
     */
    @Test
    public void testMessageParts_SquareBracketsNonNumeric() throws Exception {
        SubjectParser parser = new SubjectParser("dummy");
        setRangeString(parser, "[a/b]");
        
        int[] result = invokeMessageParts(parser);
        assertNull(result);
    }

    /**
     * Test Case 5: Null RangeString (Outer Exception).
     * Logic Path: 
     * 1. getRangeString returns null.
     * 2. mainrange.lastIndexOf causes NullPointerException.
     * 3. Caught by Outer Catch block.
     * 4. Returns null.
     */
    @Test
    public void testMessageParts_NullRangeString() throws Exception {
        SubjectParser parser = new SubjectParser("dummy");
        setRangeString(parser, null); 
        
        int[] result = invokeMessageParts(parser);
        assertNull(result);
    }

    /**
     * Test Case 6: Malformed Round Brackets (Missing closing).
     * Logic Path: 
     * 1. getRangeString returns "(1/5".
     * 2. lastIndexOf("(") works.
     * 3. indexOf(")") returns -1.
     * 4. substring throws IndexOutOfBoundsException.
     * 5. Catch (inte).
     * 6. Nested block fails (no square brackets).
     * 7. Returns null.
     */
    @Test
    public void testMessageParts_MalformedRoundBrackets() throws Exception {
        SubjectParser parser = new SubjectParser("dummy");
        setRangeString(parser, "(1/5");
        
        int[] result = invokeMessageParts(parser);
        assertNull(result);
    }

    /**
     * Test Case 7: Malformed/Empty Range String (No brackets).
     * Logic Path: 
     * 1. getRangeString returns "InvalidString".
     * 2. lastIndexOf("(") returns -1.
     * 3. substring throws IndexOutOfBoundsException.
     * 4. Catch (inte).
     * 5. Nested block: lastIndexOf("[") returns -1.
     * 6. Exception.
     * 7. Catch (subE).
     * 8. Returns null.
     */
    @Test
    public void testMessageParts_NoBrackets() throws Exception {
        SubjectParser parser = new SubjectParser("dummy");
        setRangeString(parser, "InvalidString");
        
        int[] result = invokeMessageParts(parser);
        assertNull(result);
    }

    /**
     * Test Case 8: Mixed format - Round brackets exist but logic fails, fallback to Square fails.
     * Input: "(1/2] " - Mismatched.
     * Logic Path: 
     * 1. lastIndexOf("(") finds index.
     * 2. indexOf(")") fails (returns -1).
     * 3. Exception -> Catch (inte).
     * 4. Nested try: lastIndexOf("[") fails.
     * 5. Returns null.
     */
    @Test
    public void testMessageParts_MismatchedBrackets() throws Exception {
        SubjectParser parser = new SubjectParser("dummy");
        setRangeString(parser, "(1/2]");
        
        int[] result = invokeMessageParts(parser);
        assertNull(result);
    }
  /**
     * Test Case 1: Valid Range with Round Brackets.
     * Logic Path: messageParts() returns [1, 5] -> parts != null -> UpperRange updated to 5 -> returns 5.
     */
    @Test
    public void testGetUpperRange_ValidRound() {
        SubjectParser parser = new SubjectParser("Test Subject (1/5)");
        assertEquals(5, parser.getUpperRange());
    }

    /**
     * Test Case 2: Valid Range with Square Brackets.
     * Logic Path: messageParts() returns [10, 20] -> parts != null -> UpperRange updated to 20 -> returns 20.
     */
    @Test
    public void testGetUpperRange_ValidSquare() {
        SubjectParser parser = new SubjectParser("Test Subject [10/20]");
        assertEquals(20, parser.getUpperRange());
    }

    /**
     * Test Case 3: No Range in Subject.
     * Logic Path: messageParts() returns null -> parts != null is false -> UpperRange remains default (1) -> returns 1.
     */
    @Test
    public void testGetUpperRange_NoRange() {
        SubjectParser parser = new SubjectParser("Test Subject Only");
        assertEquals(1, parser.getUpperRange());
    }

    /**
     * Test Case 4: Invalid Syntax (Non-numeric).
     * Logic Path: messageParts() encounters parsing error -> returns null -> UpperRange remains default (1) -> returns 1.
     */
    @Test
    public void testGetUpperRange_InvalidSyntax() {
        SubjectParser parser = new SubjectParser("Test Subject (a/b)");
        assertEquals(1, parser.getUpperRange());
    }

    /**
     * Test Case 5: Null Subject.
     * Logic Path: messageParts() handles null subject -> returns null -> UpperRange remains default (1) -> returns 1.
     */
    @Test
    public void testGetUpperRange_NullSubject() {
        SubjectParser parser = new SubjectParser(null);
        assertEquals(1, parser.getUpperRange());
    }

    /**
     * Test Case 6: Verify State Persistence.
     * Logic Path: 
     * 1. Constructor sets UpperRange = 1.
     * 2. First call detects range (1/99) and updates UpperRange to 99.
     * 3. Subsequent calls return the stored UpperRange (99).
     */
    @Test
    public void testGetUpperRange_StatePersistence() {
        SubjectParser parser = new SubjectParser("Title (1/99)");
        // First call triggers parsing and update
        assertEquals(99, parser.getUpperRange());
        // Second call returns current field value
        assertEquals(99, parser.getUpperRange());
    }

    /**
     * Test Case 7: Manual Field Injection (White Box Testing).
     * Logic Path: 
     * If messageParts fails (simulated by having no range in string), 
     * the method returns the current value of UpperRange variable.
     * We use reflection to set UpperRange to a custom value to ensure it returns the field value, not just a hardcoded 1.
     */
    @Test
    public void testGetUpperRange_ReturnsCurrentField_WhenPartsNull() throws Exception {
        SubjectParser parser = new SubjectParser("No Range Here");
        
        // Inject custom UpperRange value
        Field upperRangeField = SubjectParser.class.getDeclaredField("UpperRange");
        upperRangeField.setAccessible(true);
        upperRangeField.setInt(parser, 100);

        // messageParts() returns null, so it shouldn't overwrite 100.
        // It should return the injected 100.
        assertEquals(100, parser.getUpperRange());
    }


    /**
     * Test Case 1: Valid Range with Round Brackets.
     * Logic Path: messageParts() returns [1, 5] -> parts != null -> LowerRange updated to 1 -> returns 1.
     * (Note: messageParts parses "1" from "(1/5)").
     */
    @Test
    public void testGetThisRange_ValidRound() {
        SubjectParser parser = new SubjectParser("Test Subject (1/5)");
        assertEquals(1, parser.getThisRange());
    }

    /**
     * Test Case 2: Valid Range with different numbers.
     * Logic Path: messageParts() returns [10, 20] -> parts != null -> LowerRange updated to 10 -> returns 10.
     */
    @Test
    public void testGetThisRange_ValidRoundHighValue() {
        SubjectParser parser = new SubjectParser("Test Subject (10/20)");
        assertEquals(10, parser.getThisRange());
    }

    /**
     * Test Case 3: Valid Range with Square Brackets.
     * Logic Path: messageParts() returns [5, 15] -> parts != null -> LowerRange updated to 5 -> returns 5.
     */
    @Test
    public void testGetThisRange_ValidSquare() {
        SubjectParser parser = new SubjectParser("Test Subject [5/15]");
        assertEquals(5, parser.getThisRange());
    }

    /**
     * Test Case 4: No Range in Subject.
     * Logic Path: messageParts() returns null -> parts != null is false -> LowerRange remains default (1) -> returns 1.
     */
    @Test
    public void testGetThisRange_NoRange() {
        SubjectParser parser = new SubjectParser("Test Subject Only");
        assertEquals(1, parser.getThisRange());
    }

    /**
     * Test Case 5: Invalid Syntax (Non-numeric).
     * Logic Path: messageParts() encounters parsing error -> returns null -> LowerRange remains default (1) -> returns 1.
     */
    @Test
    public void testGetThisRange_InvalidSyntax() {
        SubjectParser parser = new SubjectParser("Test Subject (a/b)");
        assertEquals(1, parser.getThisRange());
    }

    /**
     * Test Case 6: Null Subject.
     * Logic Path: messageParts() handles null subject -> returns null -> LowerRange remains default (1) -> returns 1.
     */
    @Test
    public void testGetThisRange_NullSubject() {
        SubjectParser parser = new SubjectParser(null);
        assertEquals(1, parser.getThisRange());
    }

    /**
     * Test Case 7: Verify State Persistence (White Box / Branch Coverage).
     * Logic Path: 
     * 1. Check that default is 1.
     * 2. Parse a valid range (sets LowerRange to 100).
     * 3. Subsequent call returns 100.
     */
    @Test
    public void testGetThisRange_StateUpdate() {
        SubjectParser parser = new SubjectParser("Title (100/200)");
        assertEquals(100, parser.getThisRange());
        // Verify field holds the value
        assertEquals(100, parser.getThisRange());
    }

    /**
     * Test Case 8: Manual Field Injection (White Box Testing).
     * Logic Path: 
     * Ensure that if parsing fails (parts == null), the method returns the *current* state of LowerRange,
     * not just a hardcoded 1.
     */
    @Test
    public void testGetThisRange_ReturnsCurrentField_WhenPartsNull() throws Exception {
        SubjectParser parser = new SubjectParser("No Range Here");
        
        // Inject custom LowerRange value using reflection
        Field lowerRangeField = SubjectParser.class.getDeclaredField("LowerRange");
        lowerRangeField.setAccessible(true);
        lowerRangeField.setInt(parser, 999);

        // messageParts() returns null because "No Range Here" has no brackets.
        // The code enters the 'else' (implicit) or skips the 'if', preserving 999.
        assertEquals(999, parser.getThisRange());
    }


}
				

						