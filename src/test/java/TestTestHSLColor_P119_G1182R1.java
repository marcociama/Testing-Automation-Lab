import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class TestTestHSLColor_P119_G1182R1 {

    private static final int HSLMAX = 255;
    private static final int RGBMAX = 255;
    private static final int UNDEFINED = 170;

    // --- Helper Methods for Reflection ---

    private Object invokePrivateMethod(Object instance, String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Method method = HSLColor.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(instance, args);
    }

    private void invokePrivateVoidMethod(Object instance, String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Method method = HSLColor.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        method.invoke(instance, args);
    }

    private int getPrivateIntField(Object instance, String fieldName) throws Exception {
        Field field = HSLColor.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getInt(instance);
    }

    // --- initHSLbyRGB Tests ---

    @Test
    public void initHSLbyRGBGreyscaleTest() {
        HSLColor color = new HSLColor();
        // R=G=B => cMax == cMin
        color.initHSLbyRGB(100, 100, 100);
        
        assertEquals(0, color.getSaturation());
        assertEquals(UNDEFINED, color.getHue());
        // pLum calculation: ((100*255)+255)/(2*255) => 100
        assertEquals(100, color.getLuminence());
    }

    @Test
    public void initHSLbyRGBColorLowLuminanceRedMaxTest() {
        HSLColor color = new HSLColor();
        // Low Lum (<= 127). R is Max.
        // R=100, G=0, B=0.
        color.initHSLbyRGB(100, 0, 0);

        assertTrue(color.getLuminence() <= (HSLMAX / 2));
        // Saturation logic for low lum should be used
        // Hue logic: cMax == R branch
        assertTrue(color.getSaturation() > 0);
    }

    @Test
    public void initHSLbyRGBColorHighLuminanceGreenMaxTest() {
        HSLColor color = new HSLColor();
        // High Lum (> 127). G is Max.
        // R=200, G=255, B=200.
        color.initHSLbyRGB(200, 255, 200);

        assertTrue(color.getLuminence() > (HSLMAX / 2));
        // Hue logic: cMax == G branch
        // Hue should be around 85 (approx 1/3 of 255)
        int h = color.getHue();
        assertTrue(h > 80 && h < 90);
    }

    @Test
    public void initHSLbyRGBColorBlueMaxTest() {
        HSLColor color = new HSLColor();
        // B is Max.
        color.initHSLbyRGB(0, 0, 255);

        // Hue logic: cMax == B branch
        // Hue should be around 170 (approx 2/3 of 255)
        int h = color.getHue();
        assertTrue(h > 165 && h < 175);
    }
    
    // Test Specifico per il Branch 1: else if (cMax == B)
    // Usiamo valori misti dove B è strettamente il maggiore per evitare ambiguità con R o G.
    @Test
    public void initHSLbyRGB_BlueMax_Strict_Test() {
        HSLColor color = new HSLColor();
        // R=50, G=100, B=200. 
        // cMax=200 (B). cMax != R (200!=50), cMax != G (200!=100).
        // Questo forza l'ingresso nell'ultimo else if (cMax == B).
        color.initHSLbyRGB(50, 100, 200);
        
        // Formula: Hue = ((2 * HSLMAX) / 3) + GDelta - RDelta;
        // (2*255)/3 = 170. 
        // Hue atteso intorno a 170 (Blu puro) +/- delta.
        int h = color.getHue();
        assertTrue("Hue should be dominated by Blue (around 170)", h > 150 && h < 190);
    }

    @Test
    public void initHSLbyRGBNegativeHueCorrectionTest() {
        HSLColor color = new HSLColor();
        // Questa combinazione (Magenta) con R=Max triggera spesso pHue < 0 prima della correzione
        // cMax=255 (R), cMin=0 (G). 
        // cMax == R -> pHue = BDelta - GDelta. 
        // BDelta sarà 0, GDelta sarà alto. Result negativo.
        color.initHSLbyRGB(255, 0, 255);

        // Verifica che sia stato corretto (aggiunto HSLMAX)
        assertTrue(color.getHue() > 0);
    }

    // --- initRGBbyHSL Tests ---

    @Test
    public void initRGBbyHSLGreyscaleTest() {
        HSLColor color = new HSLColor();
        // S = 0
        color.initRGBbyHSL(100, 0, 128);

        assertEquals(128, color.getRed());
        assertEquals(128, color.getGreen());
        assertEquals(128, color.getBlue());
    }

    @Test
    public void initRGBbyHSLLowLumTest() {
        HSLColor color = new HSLColor();
        // L <= 127
        color.initRGBbyHSL(0, 255, 100);
        
        // Verifica che R, G, B siano settati (non greyscale)
        assertNotEquals(color.getRed(), color.getGreen());
    }

    @Test
    public void initRGBbyHSLHighLumTest() {
        HSLColor color = new HSLColor();
        // L > 127
        color.initRGBbyHSL(0, 255, 200);

        assertNotEquals(color.getRed(), color.getGreen());
    }

    @Test
    public void initRGBbyHSLClampCheckTest() {
        HSLColor color = new HSLColor();
        // Questo test esistente copre pRed > RGBMAX
        color.initRGBbyHSL(0, 300, 128); 

        assertEquals(255, color.getRed()); // Deve essere clippato a 255
        assertTrue(color.getGreen() <= 255);
        assertTrue(color.getBlue() <= 255);
    }
    
    // Test Specifico per il Branch 2: if (pGreen > RGBMAX)
    @Test
    public void initRGBbyHSL_GreenClamp_Test() {
        HSLColor color = new HSLColor();
        // Per forzare Green > 255:
        // 1. Usiamo S > 255 (es. 300) per generare un Magic2 molto alto.
        // 2. Usiamo Hue = 85 (HSLMAX/3, ovvero Verde Puro).
        //    A questo Hue, hueToRGB restituisce direttamente 'mag2' (il valore alto).
        //    pGreen = (mag2 * RGBMAX + ...) / HSLMAX. Se mag2 > HSLMAX, pGreen > RGBMAX.
        color.initRGBbyHSL(85, 300, 128);
        
        assertEquals("Green should be clamped to 255", 255, color.getGreen());
    }

    // Test Specifico per il Branch 3: if (pBlue > RGBMAX)
    @Test
    public void initRGBbyHSL_BlueClamp_Test() {
        HSLColor color = new HSLColor();
        // Per forzare Blue > 255:
        // 1. Usiamo S > 255 (es. 300) per Magic2 alto.
        // 2. Il componente Blue chiama hueToRGB con (H - HSLMAX/3).
        //    Affinché (H - 85) colpisca il plateau di 'mag2' (range 42.5 -> 127.5),
        //    H deve essere circa 85 + 85 = 170 (Blu Puro).
        color.initRGBbyHSL(170, 300, 128);
        
        assertEquals("Blue should be clamped to 255", 255, color.getBlue());
    }

    // --- hueToRGB Private Method Tests ---

    @Test
    public void hueToRGBBranchCoverageTest() throws Exception {
        HSLColor color = new HSLColor();
        // Accesso al metodo privato hueToRGB(int mag1, int mag2, int Hue)
        Class<?>[] paramTypes = {int.class, int.class, int.class};
        
        // Case 1: Hue < 0 (Correzione range)
        int res1 = (int) invokePrivateMethod(color, "hueToRGB", paramTypes, new Object[]{10, 20, -50});
        assertEquals(10, res1);

        // Case 2: Hue > HSLMAX (Correzione range)
        int res2 = (int) invokePrivateMethod(color, "hueToRGB", paramTypes, new Object[]{10, 20, 300});
        assertEquals(20, res2);

        // Case 3: Hue < HSLMAX/6
        invokePrivateMethod(color, "hueToRGB", paramTypes, new Object[]{0, 255, 20});
        
        // Case 4: Hue < HSLMAX/2
        int res4 = (int) invokePrivateMethod(color, "hueToRGB", paramTypes, new Object[]{10, 20, 100});
        assertEquals(20, res4);

        // Case 5: Hue < HSLMAX*2/3
        invokePrivateMethod(color, "hueToRGB", paramTypes, new Object[]{0, 255, 150});
    }

    // --- Helper iMax / iMin Private Tests ---

    @Test
    public void iMaxTest() throws Exception {
        HSLColor color = new HSLColor();
        Class<?>[] paramTypes = {int.class, int.class};

        // Branch a > b
        int res1 = (int) invokePrivateMethod(color, "iMax", paramTypes, new Object[]{10, 5});
        assertEquals(10, res1);

        // Branch else (b >= a)
        int res2 = (int) invokePrivateMethod(color, "iMax", paramTypes, new Object[]{5, 10});
        assertEquals(10, res2);
    }

    @Test
    public void iMinTest() throws Exception {
        HSLColor color = new HSLColor();
        Class<?>[] paramTypes = {int.class, int.class};

        // Branch a < b
        int res1 = (int) invokePrivateMethod(color, "iMin", paramTypes, new Object[]{5, 10});
        assertEquals(5, res1);

        // Branch else (b <= a)
        int res2 = (int) invokePrivateMethod(color, "iMin", paramTypes, new Object[]{10, 5});
        assertEquals(5, res2);
    }

    // --- Setters / Getters / Private Setters ---

    @Test
    public void setHueLoopsTest() {
        HSLColor color = new HSLColor();
        
        // Case 1: Loop while (iToValue < 0)
        color.setHue(-200);
        assertEquals(55, color.getHue());

        // Case 2: Loop while (iToValue > HSLMAX)
        color.setHue(500);
        assertEquals(245, color.getHue());
    }

    @Test
    public void setSaturationClampingTest() {
        HSLColor color = new HSLColor();

        // Case < 0
        color.setSaturation(-10);
        assertEquals(0, color.getSaturation());

        // Case > HSLMAX
        color.setSaturation(300);
        assertEquals(HSLMAX, color.getSaturation());
        
        // Valid
        color.setSaturation(100);
        assertEquals(100, color.getSaturation());
    }

    @Test
    public void setLuminenceClampingTest() {
        HSLColor color = new HSLColor();

        // Case < 0
        color.setLuminence(-10);
        assertEquals(0, color.getLuminence());

        // Case > HSLMAX
        color.setLuminence(300);
        assertEquals(HSLMAX, color.getLuminence());

        // Valid
        color.setLuminence(100);
        assertEquals(100, color.getLuminence());
    }

    @Test
    public void privateSettersTest() throws Exception {
        HSLColor color = new HSLColor();
        Class<?>[] paramTypes = {int.class};

        // setRed
        invokePrivateVoidMethod(color, "setRed", paramTypes, new Object[]{255});
        assertEquals(255, color.getRed());

        // setGreen
        invokePrivateVoidMethod(color, "setGreen", paramTypes, new Object[]{255});
        assertEquals(255, color.getGreen());

        // setBlue
        invokePrivateVoidMethod(color, "setBlue", paramTypes, new Object[]{255});
        assertEquals(255, color.getBlue());
    }

    // --- Other Methods Tests ---

    @Test
    public void reverseColorTest() {
        HSLColor color = new HSLColor();
        color.setHue(0);
        color.reverseColor();
        // 0 + 127 = 127
        assertEquals(127, color.getHue());
    }

    @Test
    public void reverseLightTest() throws Exception {
        HSLColor color = new HSLColor();
        color.setLuminence(50);
        
        invokePrivateVoidMethod(color, "reverseLight", null, null);
        
        // 255 - 50 = 205
        assertEquals(205, color.getLuminence());
    }

    @Test
    public void greyscaleMethodTest() throws Exception {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(255, 0, 0); // Colorato
        assertTrue(color.getSaturation() > 0);

        invokePrivateVoidMethod(color, "greyscale", null, null);

        // greyscale chiama initRGBbyHSL(UNDEFINED, 0, pLum)
        // quindi RGB diventano pLum
        assertEquals(color.getRed(), color.getLuminence());
        assertEquals(color.getGreen(), color.getLuminence());
        assertEquals(color.getBlue(), color.getLuminence());
    }

    @Test
    public void brightenTest() {
        HSLColor color = new HSLColor();
        color.setLuminence(100);

        // Branch 0: fPercent == 0
        color.brighten(0.0f);
        assertEquals(100, color.getLuminence());

        // Branch: L < 0 clamp (passando valore negativo grande)
        color.brighten(-5.0f);
        assertEquals(0, color.getLuminence());

        // Reset
        color.setLuminence(100);
        // Branch: L > HSLMAX clamp
        color.brighten(5.0f);
        assertEquals(HSLMAX, color.getLuminence());

        // Normal calc
        color.setLuminence(100);
        color.brighten(1.5f);
        assertEquals(150, color.getLuminence());
    }

    @Test
    public void blendTest() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(0, 0, 0); // Start Black

        // Branch 1: fPercent >= 1
        color.blend(255, 255, 255, 1.5f);
        assertEquals(255, color.getRed()); // Diventa bianco

        // Reset
        color.initHSLbyRGB(0, 0, 0);
        
        // Branch 2: fPercent <= 0
        color.blend(255, 255, 255, -0.5f);
        assertEquals(0, color.getRed()); // Rimane nero

        // Branch 3: Calculation
        color.blend(255, 255, 255, 0.5f);
        int r = color.getRed();
        assertTrue(r >= 127 && r <= 128);
    }
}