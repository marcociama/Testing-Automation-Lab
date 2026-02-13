import org.junit.Test;
import static org.junit.Assert.*;

public class TestTestHSLColor_P616_G1225R1 {

    // =================================================================
    // 1. Initialization and Constructor Tests
    // =================================================================

    @Test
    public void defaultConstructor_createsBlack_hasCorrectValues() {
        HSLColor color = new HSLColor();
        assertEquals("Hue should be 0 for default black", 0, color.getHue());
        assertEquals("Saturation should be 0 for default black", 0, color.getSaturation());
        assertEquals("Luminence should be 0 for default black", 0, color.getLuminence());
        assertEquals("Red should be 0 for default black", 0, color.getRed());
        assertEquals("Green should be 0 for default black", 0, color.getGreen());
        assertEquals("Blue should be 0 for default black", 0, color.getBlue());
    }

    @Test
    public void initHslByRgb_withPureRed_calculatesHslCorrectly() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(255, 0, 0);
        assertEquals("Hue for pure red", 0, color.getHue());
        assertEquals("Saturation for pure red", 255, color.getSaturation());
        assertEquals("Luminence for pure red", 128, color.getLuminence());
        assertEquals(255, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());
    }

    @Test
    public void initHslByRgb_withMediumGray_calculatesHslCorrectly() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(128, 128, 128);
        assertEquals("Hue for gray should be 170 (UNDEFINED)", 170, color.getHue());
        assertEquals("Saturation for gray should be 0", 0, color.getSaturation());
        assertEquals("Luminence for gray", 128, color.getLuminence());
        assertEquals(128, color.getRed());
        assertEquals(128, color.getGreen());
        assertEquals(128, color.getBlue());
    }

    @Test
    public void initRgbByHsl_withCyan_calculatesRgbCorrectly() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(128, 255, 128);
        assertEquals(128, color.getHue());
        assertEquals(255, color.getSaturation());
        assertEquals(128, color.getLuminence());
        assertEquals("Red for cyan", 1, color.getRed());
        assertEquals("Green for cyan", 255, color.getGreen());
        assertEquals("Blue for cyan", 255, color.getBlue());
    }

    // =================================================================
    // 2. Round-Trip Tests (RGB -> HSL -> RGB)
    // =================================================================

    @Test
    public void roundTrip_pureRed_maintainsRgb() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(255, 0, 0);
        color.initRGBbyHSL(color.getHue(), color.getSaturation(), color.getLuminence());
        assertTrue("Red component should be close after round-trip", Math.abs(255 - color.getRed()) <= 1);
        assertTrue("Green component should be close after round-trip", Math.abs(0 - color.getGreen()) <= 1);
        assertTrue("Blue component should be close after round-trip", Math.abs(0 - color.getBlue()) <= 1);
    }

    @Test
    public void roundTrip_pureGreen_maintainsRgb() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(0, 255, 0);
        color.initRGBbyHSL(color.getHue(), color.getSaturation(), color.getLuminence());
        assertTrue("Red component should be close after round-trip", Math.abs(0 - color.getRed()) <= 1);
        assertTrue("Green component should be close after round-trip", Math.abs(255 - color.getGreen()) <= 1);
        assertTrue("Blue component should be close after round-trip", Math.abs(0 - color.getBlue()) <= 1);
    }

    @Test
    public void roundTrip_pureBlue_maintainsRgb() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(0, 0, 255);
        color.initRGBbyHSL(color.getHue(), color.getSaturation(), color.getLuminence());
        assertTrue("Red component should be close after round-trip", Math.abs(0 - color.getRed()) <= 1);
        assertTrue("Green component should be close after round-trip", Math.abs(0 - color.getGreen()) <= 1);
        assertTrue("Blue component should be close after round-trip", Math.abs(255 - color.getBlue()) <= 1);
    }

    @Test
    public void roundTrip_gray_maintainsRgb() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(100, 100, 100);
        color.initRGBbyHSL(color.getHue(), color.getSaturation(), color.getLuminence());
        assertEquals(100, color.getRed());
        assertEquals(100, color.getGreen());
        assertEquals(100, color.getBlue());
    }

    @Test
    public void roundTrip_randomColor_maintainsRgbWithTolerance() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(42, 123, 200);
        color.initRGBbyHSL(color.getHue(), color.getSaturation(), color.getLuminence());
        assertTrue("Red component should be close after round-trip", Math.abs(42 - color.getRed()) <= 2);
        assertTrue("Green component should be close after round-trip", Math.abs(123 - color.getGreen()) <= 2);
        assertTrue("Blue component should be close after round-trip", Math.abs(200 - color.getBlue()) <= 2);
    }

    // =================================================================
    // 3. Setter Tests
    // =================================================================

    @Test
    public void setHue_withValidValue_updatesCorrectly() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(0, 255, 128); // Initialize with Red
        color.setHue(85); // Change to Green
        assertEquals(85, color.getHue());
        assertEquals(1, color.getRed());
        assertEquals(255, color.getGreen());
        assertEquals(1, color.getBlue());
    }

    @Test
    public void setHue_withNegativeValue_wrapsAround() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(0, 255, 128);
        color.setHue(-1); // -1 becomes 254
        assertEquals(254, color.getHue());
    }

    @Test
    public void setHue_withValue256_wrapsTo1() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(0, 255, 128);
        color.setHue(256); // 256 becomes 1
        assertEquals(1, color.getHue());
    }

    @Test
    public void setHue_withLargeValue_wrapsAround() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(0, 255, 128);
        color.setHue(257); // 257 becomes 2
        assertEquals(2, color.getHue());
    }

    @Test
    public void setSaturation_withNegativeValue_clampsToZero() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(128, 100, 100);
        color.setSaturation(-1);
        assertEquals(0, color.getSaturation());
    }

    @Test
    public void setSaturation_withValueOver255_clampsTo255() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(128, 100, 100);
        color.setSaturation(256);
        assertEquals(255, color.getSaturation());
    }

    @Test
    public void setLuminence_withNegativeValue_clampsToZero() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(128, 100, 100);
        color.setLuminence(-1);
        assertEquals(0, color.getLuminence());
    }

    @Test
    public void setLuminence_withValueOver255_clampsTo255() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(128, 100, 100);
        color.setLuminence(256);
        assertEquals(255, color.getLuminence());
    }

    // =================================================================
    // 4. reverseColor() Tests
    // =================================================================

    @Test
    public void reverseColor_withLowHue_addsOffset() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(10, 255, 128);
        color.reverseColor();
        assertEquals(137, color.getHue()); // 10 + 127
    }

    @Test
    public void reverseColor_withHighHue_wrapsAround() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(200, 255, 128);
        color.reverseColor();
        assertEquals(72, color.getHue()); // (200 + 127) % 255 logic
    }

    @Test
    public void reverseColor_withMidHue_reversesCorrectly() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(128, 255, 128);
        color.reverseColor();
        assertEquals(255, color.getHue()); // 128 + 127
    }

    // =================================================================
    // 5. brighten() Tests
    // =================================================================

    @Test
    public void brighten_withZeroPercent_doesNotChangeLuminence() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(100, 100, 100);
        int originalLuminence = color.getLuminence();
        color.brighten(0.0f);
        assertEquals(originalLuminence, color.getLuminence());
    }

    @Test
    public void brighten_withOneHundredPercent_doesNotChangeLuminence() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(100, 100, 100);
        int originalLuminence = color.getLuminence();
        color.brighten(1.0f);
        assertEquals(originalLuminence, color.getLuminence());
    }

    @Test
    public void brighten_withPositiveFactor_scalesUpLuminence() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30); // Lum = 20
        color.brighten(1.5f); // L = 20 * 1.5 = 30
        assertEquals(30, color.getLuminence());
    }

    @Test
    public void brighten_withSubUnitaryFactor_scalesDownLuminence() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(100, 200, 100); // Lum = 150
        color.brighten(0.5f); // L = 150 * 0.5 = 75
        assertEquals(75, color.getLuminence());
    }

    @Test
    public void brighten_scalingPastLimit_clampsTo255() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(200, 200, 200); // Lum = 200
        color.brighten(2.0f); // L = 200 * 2.0 = 400 -> clamp to 255
        assertEquals(255, color.getLuminence());
    }

    @Test
    public void brighten_withNegativeFactor_clampsToZero() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(50, 50, 50); // Lum = 50
        color.brighten(-1.0f); // L = 50 * -1.0 = -50 -> clamp to 0
        assertEquals(0, color.getLuminence());
    }

    // =================================================================
    // 6. blend() Tests
    // =================================================================

    @Test
    public void blend_withZeroFactor_doesNotChangeColor() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30);
        color.blend(255, 0, 0, 0.0f);
        assertEquals(10, color.getRed());
        assertEquals(20, color.getGreen());
        assertEquals(30, color.getBlue());
    }

    @Test
    public void blend_withNegativeFactor_doesNotChangeColor() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30);
        color.blend(255, 0, 0, -1.0f);
        assertEquals(10, color.getRed());
        assertEquals(20, color.getGreen());
        assertEquals(30, color.getBlue());
    }

    @Test
    public void blend_withFactorOfOne_becomesTargetColor() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30);
        color.blend(255, 100, 50, 1.0f);
        assertEquals(255, color.getRed());
        assertEquals(100, color.getGreen());
        assertEquals(50, color.getBlue());
    }

    @Test
    public void blend_withFactorGreaterThanOne_becomesTargetColor() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30);
        color.blend(255, 100, 50, 2.0f);
        assertEquals(255, color.getRed());
        assertEquals(100, color.getGreen());
        assertEquals(50, color.getBlue());
    }

    @Test
    public void blend_withHalfFactor_interpolatesLinearly() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30);
        color.blend(110, 220, 130, 0.5f);
        assertEquals(60, color.getRed());
        assertEquals(120, color.getGreen());
        assertEquals(80, color.getBlue());
    }

    // =================================================================
    // 7. Boundary and Anomaly Tests
    // =================================================================

    @Test
    public void initHslByRgb_withBlack_calculatesHslCorrectly() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(0, 0, 0);
        assertEquals(170, color.getHue()); // UNDEFINED
        assertEquals(0, color.getSaturation());
        assertEquals(0, color.getLuminence());
    }

    @Test
    public void initHslByRgb_withWhite_calculatesHslCorrectly() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(255, 255, 255);
        assertEquals(170, color.getHue()); // UNDEFINED
        assertEquals(0, color.getSaturation());
        assertEquals(255, color.getLuminence());
    }

    @Test
    public void initRgbByHsl_withBlack_calculatesRgbCorrectly() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(0, 0, 0);
        assertEquals(0, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());
    }

    @Test
    public void initRgbByHsl_withWhite_calculatesRgbCorrectly() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(0, 0, 255);
        assertEquals(255, color.getRed());
        assertEquals(255, color.getGreen());
        assertEquals(255, color.getBlue());
    }

    @Test
    public void initRgbByHsl_withZeroSaturation_producesGray() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(100, 0, 128); // Hue should have no effect
        assertEquals(128, color.getRed());
        assertEquals(128, color.getGreen());
        assertEquals(128, color.getBlue());
    }

    @Test
    public void anomaly_initRgbByHsl_withMaxLuminence_producesWhite() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(128, 255, 255);
        assertEquals("Red should be 255 for max luminence", 255, color.getRed());
        assertEquals("Green should be 255 for max luminence", 255, color.getGreen());
        assertEquals("Blue should be 255 for max luminence", 255, color.getBlue());
    }

    @Test
    public void anomaly_initRgbByHsl_withZeroLuminence_producesBlack() {
        HSLColor color = new HSLColor();
        color.initRGBbyHSL(128, 255, 0);
        assertEquals("Red should be 0 for zero luminence", 0, color.getRed());
        assertEquals("Green should be 0 for zero luminence", 0, color.getGreen());
        assertEquals("Blue should be 0 for zero luminence", 0, color.getBlue());
    }

    // =================================================================
    // 8. Coverage Tests for Private Setters
    // =================================================================

    @Test
    public void privateSetRed_updatesColor() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30);
        // This test implicitly calls setRed via initHSLbyRGB
        HSLColor newColor = new HSLColor();
        newColor.initHSLbyRGB(100, 20, 30);
        assertEquals(100, newColor.getRed());
        assertEquals(20, newColor.getGreen());
        assertEquals(30, newColor.getBlue());
    }

    @Test
    public void privateSetGreen_updatesColor() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30);
        // This test implicitly calls setGreen via initHSLbyRGB
        HSLColor newColor = new HSLColor();
        newColor.initHSLbyRGB(10, 100, 30);
        assertEquals(10, newColor.getRed());
        assertEquals(100, newColor.getGreen());
        assertEquals(30, newColor.getBlue());
    }

    @Test
    public void privateSetBlue_updatesColor() {
        HSLColor color = new HSLColor();
        color.initHSLbyRGB(10, 20, 30);
        // This test implicitly calls setBlue via initHSLbyRGB
        HSLColor newColor = new HSLColor();
        newColor.initHSLbyRGB(10, 20, 100);
        assertEquals(10, newColor.getRed());
        assertEquals(20, newColor.getGreen());
        assertEquals(100, newColor.getBlue());
    }

}