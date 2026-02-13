/*
Agisci come un esperto Software Tester Java. Ho bisogno che tu scriva una classe di test JUnit 4 completa per la classe HSLColor (codice fornito sotto).
Obiettivi Critici:

Massimizzare la Code Coverage: Devi raggiungere il 100% di copertura delle linee e dei branch.
Massimizzare la Mutation Score: I test devono "uccidere" i mutanti (es. cambiamenti negli operatori relazionali < vs <=, off-by-one errors).
Compatibilità: Il test deve essere una singola classe chiamata TestHSLColor.
Istruzioni Tecniche Specifiche (Fondamentali):

Metodi Privati e "Dead Code": La classe contiene metodi privati (es. greyscale, reverseLight, setRed, iMin) che non vengono mai chiamati dai metodi pubblici. Per coprire queste righe, DEVI utilizzare la Java Reflection (java.lang.reflect.Method) per renderli accessibili e invocarli direttamente nei test. Non ignorarli.
Precisione degli Arrotondamenti: La classe usa molta aritmetica intera (divisioni intere, + 0.5). Non usare tolleranze (delta) nelle assertEquals. Calcola mentalmente il risultato esatto dell'algoritmo (anche se matematicamente impreciso a causa del troncamento intero) e usa quel valore esatto per le asserzioni. Esempio: se il calcolo matematico è 254.9 ma il codice tronca a 254, il test deve aspettarsi 254.
Boundary Testing: Testa esplicitamente i limiti (0, 255, valori negativi che wrappano, valori oltre il massimo).
Intestazione Obbligatoria:
Inizia il codice sorgente con questo commento esatto, compilato come segue:
Java

Codice della classe da testare (HSLColor):
Java

public class TestTestHSLColor_P249_G1138R1 {
    private final static int HSLMAX = 255;
    private final static int RGBMAX = 255;
    private final static int UNDEFINED = 170;
    private int pHue;
    private int pSat;
    private int pLum;
    private int pRed;
    private int pGreen;
    private int pBlue;

    public void initHSLbyRGB(int R, int G, int B) {
        int cMax, cMin, RDelta, GDelta, BDelta, cMinus, cPlus;
        pRed = R; pGreen = G; pBlue = B;
        cMax = iMax(iMax(R, G), B);
        cMin = iMin(iMin(R, G), B);
        cMinus = cMax - cMin;
        cPlus = cMax + cMin;
        pLum = ((cPlus * HSLMAX) + RGBMAX) / (2 * RGBMAX);

        if (cMax == cMin) {
            pSat = 0;
            pHue = UNDEFINED;
        } else {
            if (pLum <= (HSLMAX / 2)) {
                pSat = (int) (((cMinus * HSLMAX) + 0.5) / cPlus);
            } else {
                pSat = (int) (((cMinus * HSLMAX) + 0.5) / (2 * RGBMAX - cPlus));
            }
            RDelta = (int) ((((cMax - R) * (HSLMAX / 6)) + 0.5) / cMinus);
            GDelta = (int) ((((cMax - G) * (HSLMAX / 6)) + 0.5) / cMinus);
            BDelta = (int) ((((cMax - B) * (HSLMAX / 6)) + 0.5) / cMinus);
            if (cMax == R) {
                pHue = BDelta - GDelta;
            } else if (cMax == G) {
                pHue = (HSLMAX / 3) + RDelta - BDelta;
            } else if (cMax == B) {
                pHue = ((2 * HSLMAX) / 3) + GDelta - RDelta;
            }
            if (pHue < 0) pHue = pHue + HSLMAX;
        }
    }

    public void initRGBbyHSL(int H, int S, int L) {
        int Magic1, Magic2;
        pHue = H; pLum = L; pSat = S;
        if (S == 0) {
            pRed = (L * RGBMAX) / HSLMAX;
            pGreen = pRed; pBlue = pRed;
        } else {
            if (L <= HSLMAX / 2) {
                Magic2 = (L * (HSLMAX + S) + (HSLMAX / 2)) / (HSLMAX);
            } else {
                Magic2 = L + S - ((L * S) + (HSLMAX / 2)) / HSLMAX;
            }
            Magic1 = 2 * L - Magic2;
            pRed = (hueToRGB(Magic1, Magic2, H + (HSLMAX / 3)) * RGBMAX + (HSLMAX / 2)) / HSLMAX;
            if (pRed > RGBMAX) pRed = RGBMAX;
            pGreen = (hueToRGB(Magic1, Magic2, H) * RGBMAX + (HSLMAX / 2)) / HSLMAX;
            if (pGreen > RGBMAX) pGreen = RGBMAX;
            pBlue = (hueToRGB(Magic1, Magic2, H - (HSLMAX / 3)) * RGBMAX + (HSLMAX / 2)) / HSLMAX;
            if (pBlue > RGBMAX) pBlue = RGBMAX;
        }
    }

    private int hueToRGB(int mag1, int mag2, int Hue) {
        if (Hue < 0) Hue = Hue + HSLMAX;
        else if (Hue > HSLMAX) Hue = Hue - HSLMAX;
        if (Hue < (HSLMAX / 6)) return (mag1 + (((mag2 - mag1) * Hue + (HSLMAX / 12)) / (HSLMAX / 6)));
        if (Hue < (HSLMAX / 2)) return mag2;
        if (Hue < (HSLMAX * 2 / 3)) return (mag1 + (((mag2 - mag1) * ((HSLMAX * 2 / 3) - Hue) + (HSLMAX / 12)) / (HSLMAX / 6)));
        return mag1;
    }

    private int iMax(int a, int b) { return (a > b) ? a : b; }
    private int iMin(int a, int b) { return (a < b) ? a : b; }
    private void greyscale() { initRGBbyHSL(UNDEFINED, 0, pLum); }
    public int getHue() { return pHue; }
    public void setHue(int iToValue) {
        while (iToValue < 0) iToValue = HSLMAX + iToValue;
        while (iToValue > HSLMAX) iToValue = iToValue - HSLMAX;
        initRGBbyHSL(iToValue, pSat, pLum);
    }
    public int getSaturation() { return pSat; }
    public void setSaturation(int iToValue) {
        if (iToValue < 0) iToValue = 0; else if (iToValue > HSLMAX) iToValue = HSLMAX;
        initRGBbyHSL(pHue, iToValue, pLum);
    }
    public int getLuminence() { return pLum; }
    public void setLuminence(int iToValue) {
        if (iToValue < 0) iToValue = 0; else if (iToValue > HSLMAX) iToValue = HSLMAX;
        initRGBbyHSL(pHue, pSat, iToValue);
    }
    public int getRed() { return pRed; }
    private void setRed(int iNewValue) { initHSLbyRGB(iNewValue, pGreen, pBlue); }
    public int getGreen() { return pGreen; }
    private void setGreen(int iNewValue) { initHSLbyRGB(pRed, iNewValue, pBlue); }
    public int getBlue() { return pBlue; }
    private void setBlue(int iNewValue) { initHSLbyRGB(pRed, pGreen, iNewValue); }
    public void reverseColor() { setHue(pHue + (HSLMAX / 2)); }
    private void reverseLight() { setLuminence(HSLMAX - pLum); }
    public void brighten(float fPercent) {
        int L;
        if (fPercent == 0) return;
        L = (int) (pLum * fPercent);
        if (L < 0) L = 0;
        if (L > HSLMAX) L = HSLMAX;
        setLuminence(L);
    }
    public void blend(int R, int G, int B, float fPercent) {
        if (fPercent >= 1) { initHSLbyRGB(R, G, B); return; }
        if (fPercent <= 0) return;
        int newR = (int) ((R * fPercent) + (pRed * (1.0 - fPercent)));
        int newG = (int) ((G * fPercent) + (pGreen * (1.0 - fPercent)));
        int newB = (int) ((B * fPercent) + (pBlue * (1.0 - fPercent)));
        initHSLbyRGB(newR, newG, newB);
    }
}
Nome: "Ange"
Cognome: "Ragozzino"
Username: ange.ragozzino@studenti.unina.it
UserID: 249
Date: 22/11/2025
*/

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.lang.reflect.Method;

class TestHSLColor {

    private HSLColor hsl;

    @Before
    public void setUp() {
        hsl = new HSLColor();
    }

    // --- UTILITY PER REFLECTION (Necessario per Dead Code e Metodi Privati) ---
    
    private Object invokePrivateMethod(String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
        Method method = HSLColor.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(hsl, params);
    }

    // --- TEST REFLECTION METODI PRIVATI/DEAD CODE ---

    @Test
    public void testPrivateIMax() throws Exception {
        // Copre iMax (dead code logicamente isolato)
        // Caso A > B
        int res1 = (int) invokePrivateMethod("iMax", new Class[]{int.class, int.class}, new Object[]{10, 5});
        assertEquals(10, res1);
        
        // Caso A <= B
        int res2 = (int) invokePrivateMethod("iMax", new Class[]{int.class, int.class}, new Object[]{5, 20});
        assertEquals(20, res2);
    }

    @Test
    public void testPrivateIMin() throws Exception {
        // Copre iMin (usato internamente ma necessario testarlo isolato per mutation score)
        // Caso A < B
        int res1 = (int) invokePrivateMethod("iMin", new Class[]{int.class, int.class}, new Object[]{5, 10});
        assertEquals(5, res1);
        
        // Caso A >= B
        int res2 = (int) invokePrivateMethod("iMin", new Class[]{int.class, int.class}, new Object[]{20, 5});
        assertEquals(5, res2);
    }

    @Test
    public void testPrivateGreyscale() throws Exception {
        // Dead code assoluto: il metodo greyscale non è mai chiamato pubblicamente.
        // Setup: Rosso puro (R=255, G=0, B=0).
        // Calcolo Lum: ((255*255) + 255) / 510 = 65280 / 510 = 128.
        hsl.initHSLbyRGB(255, 0, 0);
        
        invokePrivateMethod("greyscale", null, null);
        
        assertEquals("Hue deve essere UNDEFINED (170)", 170, hsl.getHue());
        assertEquals("Saturation deve essere 0", 0, hsl.getSaturation());
        // Correto da 127 a 128 a causa dell'arrotondamento nel calcolo originale
        assertEquals("Luminance deve restare invariata", 128, hsl.getLuminence());
        
        // Verifica che i valori RGB siano aggiornati al grigio
        // Con L=128, S=0 -> R=128, G=128, B=128
        assertEquals(128, hsl.getRed());
        assertEquals(128, hsl.getGreen());
        assertEquals(128, hsl.getBlue());
    }

    @Test
    public void testPrivateReverseLight() throws Exception {
        // Dead code assoluto: reverseLight
        hsl.initHSLbyRGB(100, 100, 100); // Grigio scuro, L approx 100
        int originalLum = hsl.getLuminence(); // 100
        
        invokePrivateMethod("reverseLight", null, null);
        
        // Expected: 255 - 100 = 155
        assertEquals(255 - originalLum, hsl.getLuminence());
        // Verifica RGB ricalcolato (L=155, S=0) -> 155
        assertEquals(155, hsl.getRed());
    }

    @Test
    public void testPrivateSetRed() throws Exception {
        // Dead code: setRed è privato e mai chiamato internamente
        hsl.initHSLbyRGB(0, 0, 0);
        
        invokePrivateMethod("setRed", new Class[]{int.class}, new Object[]{255});
        
        assertEquals(255, hsl.getRed());
        assertEquals(0, hsl.getGreen()); // Invariato
        assertEquals(0, hsl.getBlue());  // Invariato
        
        // HSL ricalcolato: Rosso puro -> H=0, S=255.
        // Lum = ((255 + 0)*255 + 255) / 510 = 128.
        assertEquals(0, hsl.getHue());
        assertEquals(255, hsl.getSaturation());
        assertEquals(128, hsl.getLuminence());
    }

    @Test
    public void testPrivateSetGreen() throws Exception {
        hsl.initHSLbyRGB(0, 0, 0);
        invokePrivateMethod("setGreen", new Class[]{int.class}, new Object[]{255});
        assertEquals(255, hsl.getGreen());
        // Verde puro -> H=85 (255/3), S=255, L=128 (stesso calcolo del rosso)
        assertEquals(85, hsl.getHue());
    }

    @Test
    public void testPrivateSetBlue() throws Exception {
        hsl.initHSLbyRGB(0, 0, 0);
        invokePrivateMethod("setBlue", new Class[]{int.class}, new Object[]{255});
        assertEquals(255, hsl.getBlue());
        // Blu puro -> H=170 (2*255/3), S=255, L=128 (stesso calcolo del rosso)
        assertEquals(170, hsl.getHue());
    }

    @Test
    public void testPrivateHueToRGBLogic() throws Exception {
        // Copertura intensiva e Branch Coverage per hueToRGB tramite Reflection.
        // Questo metodo è complesso e ha 4 rami principali + check iniziali di wrapping.
        
        Class<?>[] types = {int.class, int.class, int.class};
        
        int mag1 = 10;
        int mag2 = 200;
        
        // 1. Test Wrap negativo (Hue < 0) -> Hue = Hue + 255
        // Passiamo -10. Diventa 245. 245 > 170 (2/3 max), quindi cade nell'ultimo "return mag1"
        int resNeg = (int) invokePrivateMethod("hueToRGB", types, new Object[]{mag1, mag2, -10});
        assertEquals(mag1, resNeg);

        // 2. Test Wrap positivo (Hue > HSLMAX) -> Hue = Hue - 255
        // Passiamo 265. Diventa 10. 10 < 42 (1/6 max), primo if.
        // Calc: 10 + (((200-10)*10 + 21) / 42) -> 10 + (1900+21)/42 -> 10 + 45 = 55
        int resPos = (int) invokePrivateMethod("hueToRGB", types, new Object[]{mag1, mag2, 265});
        assertEquals(55, resPos);

        // 3. Branch: Hue < HSLMAX/6 (42). Input 10. Già testato sopra (wrap), rifacciamo diretto.
        int resBranch1 = (int) invokePrivateMethod("hueToRGB", types, new Object[]{mag1, mag2, 10});
        assertEquals(55, resBranch1);

        // 4. Branch: Hue < HSLMAX/2 (127). Input 100.
        // Return mag2
        int resBranch2 = (int) invokePrivateMethod("hueToRGB", types, new Object[]{mag1, mag2, 100});
        assertEquals(mag2, resBranch2);

        // 5. Branch: Hue < HSLMAX*2/3 (170). Input 150.
        // Calc: mag1 + (((mag2-mag1) * ((170)-150) + 21) / 42)
        // 10 + ((190 * 20 + 21) / 42) -> 10 + (3821 / 42) -> 10 + 90 = 100
        int resBranch3 = (int) invokePrivateMethod("hueToRGB", types, new Object[]{mag1, mag2, 150});
        assertEquals(100, resBranch3);

        // 6. Branch: Else. Input 200. Return mag1.
        int resBranch4 = (int) invokePrivateMethod("hueToRGB", types, new Object[]{mag1, mag2, 200});
        assertEquals(mag1, resBranch4);
    }

    // --- TEST METODI PUBBLICI (Logica Core) ---

    @Test
    public void testInitHSLbyRGB_Grayscale() {
        // Branch: cMax == cMin
        hsl.initHSLbyRGB(100, 100, 100);
        assertEquals(170, hsl.getHue()); // UNDEFINED
        assertEquals(0, hsl.getSaturation());
        // Lum = ((200*255)+255)/510 = 100
        assertEquals(100, hsl.getLuminence());
    }

    @Test
    public void testInitHSLbyRGB_RedMax_LowLum() {
        // Branch: Else -> pLum <= HSLMAX/2 (127)
        // R=50, G=0, B=0
        hsl.initHSLbyRGB(50, 0, 0);
        
        // Calcolo atteso:
        // cMax=50, cMin=0. Plus=50, Minus=50.
        // Lum = ((50*255)+255)/510 = 25. (<= 127)
        assertEquals(25, hsl.getLuminence());
        
        // Sat = ((50*255)+0.5)/50 = 12750.5/50 = 255
        assertEquals(255, hsl.getSaturation());
        
        // Hue (cMax == R). DeltaG = ((50-0)*42+0.5)/50 = 42. DeltaB = 42.
        // Hue = 42 - 42 = 0.
        assertEquals(0, hsl.getHue());
    }

    @Test
    public void testInitHSLbyRGB_RedMax_HighLum() {
        // Branch: Else -> pLum > HSLMAX/2
        // R=255, G=200, B=200
        hsl.initHSLbyRGB(255, 200, 200);
        
        // cMax=255, cMin=200. Plus=455, Minus=55.
        // Lum = ((455*255)+255)/510 = 116280 / 510 = 228. (>127)
        // Nota: Corretto da 227 a 228.
        assertEquals(228, hsl.getLuminence());
        
        // Sat = ((55*255)+0.5) / (510 - 455) = 14025.5 / 55 = 255
        assertEquals(255, hsl.getSaturation());
        
        // Hue (cMax == R). D_G = ((55*42)+0.5)/55 = 42. D_B = 42. Hue = 0.
        assertEquals(0, hsl.getHue());
    }

    @Test
    public void testInitHSLbyRGB_GreenMax() {
        // Branch: cMax == G
        // R=0, G=50, B=0.
        hsl.initHSLbyRGB(0, 50, 0);
        
        // RDelta = 42, BDelta = 42.
        // Hue = (255/3) + 42 - 42 = 85.
        assertEquals(85, hsl.getHue());
    }

    @Test
    public void testInitHSLbyRGB_BlueMax() {
        // Branch: cMax == B
        // R=0, G=0, B=50.
        hsl.initHSLbyRGB(0, 0, 50);
        
        // Hue = (2*255/3) + 42 - 42 = 170.
        assertEquals(170, hsl.getHue());
    }

    @Test
    public void testInitHSLbyRGB_NegativeHueWrap() {
        // Branch: pHue < 0
        // Caso Max == R, ma BDelta < GDelta.
        // R=255, G=0, B=100.
        hsl.initHSLbyRGB(255, 0, 100);
        
        // cMax=255, cMin=0. Minus=255.
        // BDelta = ((155 * 42) + 0.5) / 255 = 6510.5 / 255 = 25.
        // GDelta = 42.
        // Hue = 25 - 42 = -17.
        // Wrap: -17 + 255 = 238.
        
        assertEquals(238, hsl.getHue());
    }

    @Test
    public void testInitRGBbyHSL_ZeroSaturation() {
        // Branch: S == 0
        hsl.initRGBbyHSL(100, 0, 100);
        assertEquals(100, hsl.getRed());
        assertEquals(100, hsl.getGreen());
        assertEquals(100, hsl.getBlue());
    }

    @Test
    public void testInitRGBbyHSL_Calculations_LowL() {
        // Branch: L <= HSLMAX/2
        // H=0, S=255, L=50 (approx 19%).
        hsl.initRGBbyHSL(0, 255, 50);
        // Magic2 = (50 * (510) + 127) / 255 = 25627 / 255 = 100.
        // Magic1 = 100 - 100 = 0.
        // HueToRGB calls... Red should be dominant.
        assertEquals(100, hsl.getRed()); // 100 * 255 / 255
        assertEquals(0, hsl.getGreen());
        assertEquals(0, hsl.getBlue());
    }
    
    @Test
    public void testInitRGBbyHSL_Calculations_HighL() {
        // Branch: L > HSLMAX/2
        // H=0, S=255, L=200.
        hsl.initRGBbyHSL(0, 255, 200);
        // Magic2 = 200 + 255 - ((200*255) + 127)/255 = 455 - (51127/255) = 455 - 200 = 255.
        // Magic1 = 400 - 255 = 145.
        // Red -> HueToRGB(145, 255, 85) -> Hue < 127 -> return 255.
        // pRed = (255*255 + 127)/255 = 255.
        assertEquals(255, hsl.getRed());
        
        // Green -> HueToRGB(145, 255, 0) -> Hue < 42 -> calc...
        // 145 + (((255-145)*0 + 21)/42) = 145.
        // pGreen = (145*255 + 127)/255 = 145.
        assertEquals(145, hsl.getGreen());
    }

    @Test
    public void testInitRGBbyHSL_Clamping() {
        // Test per assicurare che i rami "if (pRed > RGBMAX)" vengano eseguiti.
        // Forziamo un caso limite matematico: L=255, S=255 (White)
        hsl.initRGBbyHSL(0, 255, 255);
        // Magic2 = 255 + 255 - ((255*255)+127)/255 = 510 - 255 = 255.
        // Magic1 = 255.
        // returns 255 always.
        assertEquals(255, hsl.getRed());
        assertEquals(255, hsl.getGreen());
        assertEquals(255, hsl.getBlue());
    }

    // --- TEST SETTER E WRAPPING ---

    @Test
    public void testSetHue_Wrap() {
        // setHue chiama initRGBbyHSL
        // Test loop negativo
        hsl.setLuminence(127);
        hsl.setSaturation(255);
        hsl.setHue(-10); // Deve diventare 245
        assertEquals(245, hsl.getHue());

        // Test loop positivo
        hsl.setHue(300); // 300 - 255 = 45
        assertEquals(45, hsl.getHue());
        
        // Test loop multiplo (while loop coverage)
        hsl.setHue(600); // 600 - 255 - 255 = 90
        assertEquals(90, hsl.getHue());
    }

    @Test
    public void testSetSaturation_Clamp() {
        hsl.setSaturation(500);
        assertEquals(255, hsl.getSaturation());
        
        hsl.setSaturation(-50);
        assertEquals(0, hsl.getSaturation());
    }

    @Test
    public void testSetLuminence_Clamp() {
        hsl.setLuminence(500);
        assertEquals(255, hsl.getLuminence());
        
        hsl.setLuminence(-50);
        assertEquals(0, hsl.getLuminence());
    }
    
    @Test
    public void testReverseColor() {
        // setHue(pHue + 127)
        hsl.setLuminence(127);
        hsl.setSaturation(255);
        hsl.setHue(0);
        
        hsl.reverseColor();
        assertEquals(127, hsl.getHue()); // 0 + 127
        
        hsl.setHue(200);
        hsl.reverseColor();
        // 200 + 127 = 327. 327 - 255 = 72.
        assertEquals(72, hsl.getHue());
    }

    @Test
    public void testBrighten() {
        hsl.initHSLbyRGB(50, 50, 50); // Lum = 50
        
        // Case: 0 percent -> return immediately
        hsl.brighten(0.0f);
        assertEquals(50, hsl.getLuminence());
        
        // Case: Normal scale
        hsl.brighten(2.0f); // 50 * 2 = 100
        assertEquals(100, hsl.getLuminence());
        
        // Case: Clamp Max
        hsl.brighten(10.0f); // 1000 -> 255
        assertEquals(255, hsl.getLuminence());
        
        // Case: Clamp Min (negative percent? code allows float multiply leading to < 0 check)
        hsl.brighten(-1.0f); 
        assertEquals(0, hsl.getLuminence());
    }

    @Test
    public void testBlend() {
        // Setup: Red
        hsl.initHSLbyRGB(255, 0, 0); 
        
        // 1. fPercent >= 1 -> Diventa il colore target (Blue)
        hsl.blend(0, 0, 255, 1.5f);
        assertEquals(0, hsl.getRed());
        assertEquals(255, hsl.getBlue());
        
        // 2. fPercent <= 0 -> Resta com'è
        hsl.blend(0, 255, 0, -0.5f);
        assertEquals(0, hsl.getRed()); // Ancora blue (dallo step precedente)
        assertEquals(255, hsl.getBlue());
        
        // 3. Blend al 50%
        // Current: 0, 0, 255. Target: 200, 0, 0.
        // NewR = (200*0.5) + (0*0.5) = 100.
        // NewB = (0*0.5) + (255*0.5) = 127 (troncamento di 127.5).
        hsl.blend(200, 0, 0, 0.5f);
        assertEquals(100, hsl.getRed());
        assertEquals(127, hsl.getBlue());
    }
}