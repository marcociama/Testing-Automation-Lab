import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalcolatriceTest {
    @Test
    void testSomma() {
        Calcolatrice calc = new Calcolatrice();
        assertEquals(5, calc.somma(2, 3));
    }
}