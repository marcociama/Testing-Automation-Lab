import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTestHSLColor_P152_G1111R1 {

    private HSLColor hslColor;
    private static final int HSLMAX = 255;
    private static final int RGBMAX = 255;
    private static final int UNDEFINED = 170;

    @Before
    public void setUp() {
        hslColor = new HSLColor();
    }

    // --- Test initHSLbyRGB (RGB -> HSL) ---

    @Test
    public void testInitHSLbyRGB_Red() {
        // Copre il branch: cMax == R
        hslColor.initHSLbyRGB(255, 0, 0);
        assertEquals(255, hslColor.getRed());
        assertEquals(0, hslColor.getHue());
        assertEquals(255, hslColor.getSaturation());
        // Calcolo con arrotondamento: ((510*255)+255)/510 = 128
        assertEquals(128, hslColor.getLuminence()); 
    }

    @Test
    public void testInitHSLbyRGB_Green() {
        // Copre il branch: cMax == G
        hslColor.initHSLbyRGB(0, 255, 0);
        assertEquals(85, hslColor.getHue());
        assertEquals(255, hslColor.getSaturation());
        assertEquals(128, hslColor.getLuminence());
    }

    @Test
    public void testInitHSLbyRGB_Blue() {
        // Copre il branch: cMax == B
        hslColor.initHSLbyRGB(0, 0, 255);
        assertEquals(170, hslColor.getHue());
        assertEquals(255, hslColor.getSaturation());
        assertEquals(128, hslColor.getLuminence());
    }

    @Test
    public void testInitHSLbyRGB_Greyscale() {
        // Copre il branch: if (cMax == cMin)
        hslColor.initHSLbyRGB(128, 128, 128);
        assertEquals(0, hslColor.getSaturation());
        assertEquals(UNDEFINED, hslColor.getHue());
        assertEquals(128, hslColor.getLuminence());
    }

    @Test
    public void testInitHSLbyRGB_HighLuminance() {
        // Copre il branch: else di (pLum <= HSLMAX/2) per Saturation
        hslColor.initHSLbyRGB(200, 255, 200); // Lum > 127
        assertTrue(hslColor.getLuminence() > 127);
        assertEquals(255, hslColor.getSaturation());
    }

    @Test
    public void testInitHSLbyRGB_NegativeHueCorrection() {
        // Copre il branch: if (pHue < 0)
        // Caso in cui BDelta è piccolo e GDelta è grande (G=0) con R Max
        hslColor.initHSLbyRGB(255, 0, 100);
        assertTrue(hslColor.getHue() >= 0);
        assertTrue(hslColor.getHue() <= HSLMAX);
    }

    // --- Test initRGBbyHSL (HSL -> RGB) ---

    @Test
    public void testInitRGBbyHSL_Greyscale() {
        // Copre il branch: if (S == 0)
        hslColor.initRGBbyHSL(50, 0, 100);
        assertEquals(100, hslColor.getRed());
        assertEquals(100, hslColor.getGreen());
        assertEquals(100, hslColor.getBlue());
    }

    @Test
    public void testInitRGBbyHSL_ColorLowLum() {
        // Copre il branch: if (L <= HSLMAX / 2)
        hslColor.initRGBbyHSL(0, 255, 64);
        assertEquals(128, hslColor.getRed()); 
        assertEquals(0, hslColor.getGreen());
    }

    @Test
    public void testInitRGBbyHSL_ColorHighLum() {
        // Copre il branch: else di if (L <= HSLMAX / 2)
        hslColor.initRGBbyHSL(0, 255, 192);
        // Verifica valori generali per non-saturazione
        assertTrue(hslColor.getRed() >= 255);
        assertTrue(hslColor.getGreen() > 0);
    }

    @Test
    public void testInitRGBbyHSL_OverflowChecks() {
        // NUOVO TEST: Copre i branch if (pRed > RGBMAX), if (pGreen > RGBMAX), if (pBlue > RGBMAX)
        // Utilizziamo S=300 (fuori range) per forzare matematicamente l'overflow
        
        // Case 1: Red Overflow (H=0)
        hslColor.initRGBbyHSL(0, 300, 128);
        assertEquals("Red should be clamped to 255", 255, hslColor.getRed());
        
        // Case 2: Green Overflow (H=85)
        hslColor.initRGBbyHSL(85, 300, 128);
        assertEquals("Green should be clamped to 255", 255, hslColor.getGreen());

        // Case 3: Blue Overflow (H=170)
        hslColor.initRGBbyHSL(170, 300, 128);
        assertEquals("Blue should be clamped to 255", 255, hslColor.getBlue());
    }

    @Test
    public void testHueToRGB_Branches() {
        // Test indiretto dei branch di hueToRGB
        // Hue < 1/2 (Giallo/Verde)
        hslColor.initRGBbyHSL(80, 255, 127);
        assertEquals(80, hslColor.getHue());
        // Hue < 2/3 (Ciano/Blu)
        hslColor.initRGBbyHSL(150, 255, 127);
        assertEquals(150, hslColor.getHue());
        // Hue > 2/3 (Viola)
        hslColor.initRGBbyHSL(220, 255, 127);
        assertEquals(220, hslColor.getHue());
    }

    // --- Test Setters & Utilities ---

    @Test
    public void testSetHue_WrapAround() {
        // Copre i cicli while in setHue
        hslColor.initHSLbyRGB(255, 0, 0);
        hslColor.setHue(-50);
        assertEquals(205, hslColor.getHue());
        hslColor.setHue(300);
        assertEquals(45, hslColor.getHue());
    }

    @Test
    public void testSetSaturation_Bounds() {
        hslColor.initHSLbyRGB(100, 100, 100);
        hslColor.setSaturation(300);
        assertEquals(255, hslColor.getSaturation());
        hslColor.setSaturation(-50);
        assertEquals(0, hslColor.getSaturation());
    }

    @Test
    public void testSetLuminence_Bounds() {
        hslColor.initHSLbyRGB(100, 100, 100);
        hslColor.setLuminence(500);
        assertEquals(255, hslColor.getLuminence());
        hslColor.setLuminence(-1);
        assertEquals(0, hslColor.getLuminence());
    }

    @Test
    public void testReverseColor() {
        hslColor.initHSLbyRGB(255, 0, 0);
        hslColor.reverseColor();
        assertEquals(127, hslColor.getHue());
    }

    @Test
    public void testBrighten() {
        // Copre branch fPercent == 0 e calcolo normale
        hslColor.initHSLbyRGB(128, 128, 128);
        
        hslColor.brighten(1.5f);
        assertEquals(192, hslColor.getLuminence());
        
        hslColor.setLuminence(100);
        hslColor.brighten(0.0f);
        assertEquals(100, hslColor.getLuminence());
        
        // Copre L > HSLMAX
        hslColor.brighten(10.0f);
        assertEquals(255, hslColor.getLuminence());
    }

    @Test
    public void testBrighten_Negative() {
        // NUOVO TEST: Copre il branch if (L < 0)
        hslColor.initHSLbyRGB(128, 128, 128);
        hslColor.brighten(-0.5f); // 128 * -0.5 = -64
        assertEquals("Luminence should be clamped to 0", 0, hslColor.getLuminence());
    }

    @Test
    public void testBlend() {
        hslColor.initHSLbyRGB(255, 0, 0);
        // Blend 0%
        hslColor.blend(0, 0, 255, 0.0f);
        assertEquals(255, hslColor.getRed());
        // Blend 100%
        hslColor.blend(0, 0, 255, 1.0f);
        assertEquals(255, hslColor.getBlue());
        // Blend 50%
        hslColor.blend(0, 255, 0, 0.5f);
        assertEquals(127, hslColor.getGreen());
    }
    
    @Test
    public void testGettersConsistency() {
        hslColor.initHSLbyRGB(10, 20, 30);
        assertEquals(10, hslColor.getRed());
        assertEquals(20, hslColor.getGreen());
        assertEquals(30, hslColor.getBlue());
    }
}