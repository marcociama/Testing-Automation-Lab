import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidatorePasswordTest {
    @Test
    public void testValidazione() {
        ValidatorePassword vp = new ValidatorePassword();

        // assertion roulette e magic numbers
        assertFalse(vp.eSicura("123"));
        assertFalse(vp.eSicura("password"));
        assertTrue(vp.eSicura("Password123!"));
        assertFalse(vp.eSicura(null));
    }

    @Test
    public void testVuoto() {

    }
}