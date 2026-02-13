/* 
Nome: inserire il proprio nome
Cognome: inserire il proprio cognome
Username: lidiapisaniello1@gmail.com
UserID: 442
Date: 25/11/2025
*/

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestTestHSLColor_P442_G1268R1 {

    private HSLColor color;

    @BeforeClass
    public static void setUpClass() {
        // Setup globale se necessario
    }

    @AfterClass
    public static void tearDownClass() {
        // Teardown globale se necessario
    }

    @Before
    public void setUp() {
        color = new HSLColor();
    }

    @After
    public void tearDown() {
        color = null;
    }

    // -------------------------------------------------------
    // initHSLbyRGB: vari rami (grigio, R/G/B max, pHue<0)
    // -------------------------------------------------------

    @Test
    public void testInitHSLbyRGB_Greyscale() {
        color.initHSLbyRGB(100, 100, 100);
        assertEquals(0, color.getSaturation());
        assertEquals(170, color.getHue());   // UNDEFINED
        assertTrue(color.getLuminence() > 0);
    }

    @Test
    public void testInitHSLbyRGB_RMaxAndNegativeHueFix() {
        // Esempio che porta pHue < 0 prima della correzione
        color.initHSLbyRGB(16, 0, 16);
        int hue = color.getHue();
        assertTrue(hue >= 0 && hue <= 255);
    }

    @Test
    public void testInitHSLbyRGB_GMax_LowLight() {
        // G massima, somma bassa → pLum <= HSLMAX/2
        color.initHSLbyRGB(0, 32, 0);
        assertTrue(color.getSaturation() > 0);
        assertTrue(color.getLuminence() <= 128);
    }

   @Test
public void testInitHSLbyRGB_BMax_HighLight() {
    // cMax = 255, cMin = 200 → luminosità alta
    color.initHSLbyRGB(200, 200, 255);

    assertTrue(color.getSaturation() > 0);
    assertTrue(color.getLuminence() > 128);
}


    // -------------------------------------------------------
    // initRGBbyHSL: S=0, L<=127 con S>0, L>127
    // -------------------------------------------------------

    @Test
    public void testInitRGBbyHSL_Greyscale() {
        color.initRGBbyHSL(170, 0, 128); // saturazione 0 → grigio
        assertEquals(color.getRed(), color.getGreen());
        assertEquals(color.getGreen(), color.getBlue());
    }

    @Test
    public void testInitRGBbyHSL_LowLightColor() {
        // L <= 127 con S>0 → ramo Magic2 "basso"
        color.initRGBbyHSL(50, 100, 100);
        assertTrue(color.getRed() >= 0 && color.getRed() <= 255);
        assertTrue(color.getGreen() >= 0 && color.getGreen() <= 255);
        assertTrue(color.getBlue() >= 0 && color.getBlue() <= 255);
    }

    @Test
    public void testInitRGBbyHSL_HighLightColor() {
        // L > 127 con S>0 → ramo Magic2 "alto"
        color.initRGBbyHSL(100, 200, 150);
        assertTrue(color.getRed() >= 0 && color.getRed() <= 255);
        assertTrue(color.getGreen() >= 0 && color.getGreen() <= 255);
        assertTrue(color.getBlue() >= 0 && color.getBlue() <= 255);
    }

    // -------------------------------------------------------
    // Getter/Setter H, S, L
    // -------------------------------------------------------

    @Test
    public void testHueSetterWrap() {
        color.setHue(300); // > 255
        assertTrue(color.getHue() >= 0 && color.getHue() <= 255);

        color.setHue(-40); // < 0
        assertTrue(color.getHue() >= 0 && color.getHue() <= 255);
    }

    @Test
    public void testSaturationSetterBounds() {
        color.setSaturation(-1);
        assertEquals(0, color.getSaturation());

        color.setSaturation(300);
        assertEquals(255, color.getSaturation());
    }

    @Test
    public void testLuminenceSetterBounds() {
        color.setLuminence(-10);
        assertEquals(0, color.getLuminence());

        color.setLuminence(500);
        assertEquals(255, color.getLuminence());
    }

    // -------------------------------------------------------
    // reverseColor
    // -------------------------------------------------------

    @Test
    public void testReverseColor() {
        color.initHSLbyRGB(255, 100, 50);
        int initialHue = color.getHue();

        color.reverseColor();
        int newHue = color.getHue();

        // Non assumiamo una formula precisa, ma deve cambiare
        assertNotEquals(initialHue, newHue);
        assertTrue(newHue >= 0 && newHue <= 255);
    }

    // -------------------------------------------------------
    // brighten: f=0, f<0, f>1 (clamp)
    // -------------------------------------------------------

    @Test
    public void testBrightenZeroPercent() {
        color.initRGBbyHSL(100, 200, 120);
        int oldLum = color.getLuminence();

        color.brighten(0f);
        assertEquals(oldLum, color.getLuminence());
    }

    @Test
    public void testBrightenNegativePercent() {
        color.initRGBbyHSL(100, 200, 120);
        color.brighten(-1.0f);
        // Deve portare la luminanza a 0 per via del clamp
        assertEquals(0, color.getLuminence());
    }

    @Test
    public void testBrightenOverMax() {
        color.initRGBbyHSL(100, 200, 200);
        color.brighten(2.0f);
        // L viene clippato a HSLMAX = 255
        assertEquals(255, color.getLuminence());
    }

    // -------------------------------------------------------
    // blend: f<=0, 0<f<1, f>=1
    // -------------------------------------------------------

    @Test
    public void testBlendFull() {
        color.initHSLbyRGB(0, 0, 0);
        color.blend(255, 0, 0, 1f);
        assertEquals(255, color.getRed());
    }

    @Test
    public void testBlendZero() {
        color.initHSLbyRGB(100, 150, 200);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        color.blend(255, 0, 0, 0f);
        assertEquals(r, color.getRed());
        assertEquals(g, color.getGreen());
        assertEquals(b, color.getBlue());
    }

    @Test
    public void testBlendHalfway() {
        color.initHSLbyRGB(0, 0, 0);
        color.blend(255, 0, 0, 0.5f);
        assertTrue(color.getRed() > 100 && color.getRed() < 255);
    }

    // -------------------------------------------------------
    // hueToRGB: copriamo tutti i rami via reflection
    // -------------------------------------------------------

    @Test
    public void testHueToRGB_AllBranches() throws Exception {
        Method m = HSLColor.class.getDeclaredMethod("hueToRGB", int.class, int.class, int.class);
        m.setAccessible(true);

        int mag1 = 10;
        int mag2 = 200;

        // Hue < 0 -> normalizzato
        int rNeg = (Integer) m.invoke(color, mag1, mag2, -10);
        assertTrue(rNeg >= mag1 && rNeg <= mag2);

        // Hue > HSLMAX -> normalizzato
        int rOver = (Integer) m.invoke(color, mag1, mag2, 300);
        assertTrue(rOver >= mag1 && rOver <= mag2);

        // Hue in [0, HSLMAX/6)
        int rLow = (Integer) m.invoke(color, mag1, mag2, 10);
        assertTrue(rLow >= mag1 && rLow <= mag2);

        // Hue in [HSLMAX/6, HSLMAX/2)
        int rMid1 = (Integer) m.invoke(color, mag1, mag2, 60);
        assertEquals(mag2, rMid1);

        // Hue in [HSLMAX/2, 2*HSLMAX/3)
        int rMid2 = (Integer) m.invoke(color, mag1, mag2, 140);
        assertTrue(rMid2 >= mag1 && rMid2 <= mag2);

        // Hue >= 2*HSLMAX/3
        int rHigh = (Integer) m.invoke(color, mag1, mag2, 220);
        assertEquals(mag1, rHigh);
    }

    // -------------------------------------------------------
    // greyscale() private
    // -------------------------------------------------------

    @Test
    public void testGreyscale_Private() throws Exception {
        Method initRGB = HSLColor.class.getDeclaredMethod("initRGBbyHSL", int.class, int.class, int.class);
        initRGB.setAccessible(true);
        initRGB.invoke(color, 0, 255, 100); // colore qualsiasi

        Method m = HSLColor.class.getDeclaredMethod("greyscale");
        m.setAccessible(true);
        m.invoke(color);

        assertEquals(color.getRed(), color.getGreen());
        assertEquals(color.getGreen(), color.getBlue());
    }

    // -------------------------------------------------------
    // reverseLight() private
    // -------------------------------------------------------

    @Test
    public void testReverseLight_Private() throws Exception {
        color.initRGBbyHSL(100, 200, 50);
        int oldL = color.getLuminence();

        Method m = HSLColor.class.getDeclaredMethod("reverseLight");
        m.setAccessible(true);
        m.invoke(color);

        assertEquals(255 - oldL, color.getLuminence());
    }

    // -------------------------------------------------------
    // iMax / iMin private
    // -------------------------------------------------------

    @Test
    public void testIMax_Private() throws Exception {
        Method m = HSLColor.class.getDeclaredMethod("iMax", int.class, int.class);
        m.setAccessible(true);

        assertEquals(10, ((Integer) m.invoke(color, 10, 5)).intValue());
        assertEquals(20, ((Integer) m.invoke(color, 10, 20)).intValue());
    }

    @Test
    public void testIMin_Private() throws Exception {
        Method m = HSLColor.class.getDeclaredMethod("iMin", int.class, int.class);
        m.setAccessible(true);

        assertEquals(5, ((Integer) m.invoke(color, 10, 5)).intValue());
        assertEquals(10, ((Integer) m.invoke(color, 10, 20)).intValue());
    }

    // -------------------------------------------------------
    // setRed / setGreen / setBlue private
    // -------------------------------------------------------

    @Test
    public void testSetRedGreenBlue_Private() throws Exception {
        color.initHSLbyRGB(10, 20, 30);

        Method setRed = HSLColor.class.getDeclaredMethod("setRed", int.class);
        setRed.setAccessible(true);
        setRed.invoke(color, 100);
        assertEquals(100, color.getRed());

        Method setGreen = HSLColor.class.getDeclaredMethod("setGreen", int.class);
        setGreen.setAccessible(true);
        setGreen.invoke(color, 150);
        assertEquals(150, color.getGreen());

        Method setBlue = HSLColor.class.getDeclaredMethod("setBlue", int.class);
        setBlue.setAccessible(true);
        setBlue.invoke(color, 200);
        assertEquals(200, color.getBlue());
    }
}
