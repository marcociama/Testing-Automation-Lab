import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalcolatriceTest {
    @Test
    public void testSomma() {
        Calcolatrice calc = new Calcolatrice();
        assertEquals(5, calc.somma(2, 3));
    }

    @Test
    public void vuoto(){

    }
}
