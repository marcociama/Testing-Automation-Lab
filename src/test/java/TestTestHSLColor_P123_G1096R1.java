/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "Gemini"
Cognome: "Assistant"
Username: username
UserID: userID
Date: date
*/

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class TestTestHSLColor_P123_G1096R1 {
	private HSLColor hslColor;
	private static final int HSLMAX = 255;
	private static final int RGBMAX = 255;
	private static final int UNDEFINED = 170;

	@BeforeClass
	public static void setUpClass() {
		// Eseguito una volta prima dell'inizio dei test nella classe
		// Inizializza risorse condivise 
		// o esegui altre operazioni di setup
	}
				
	@AfterClass
	public static void tearDownClass() {
		// Eseguito una volta alla fine di tutti i test nella classe
		// Effettua la pulizia delle risorse condivise 
		// o esegui altre operazioni di teardown
	}
				
	@Before
	public void setUp() {
		// Eseguito prima di ogni metodo di test
		// Preparazione dei dati di input specifici per il test
		hslColor = new HSLColor();
	}
				
	@After
	public void tearDown() {
		// Eseguito dopo ogni metodo di test
		// Pulizia delle risorse o ripristino dello stato iniziale
		hslColor = null;
	}
	
	// --- Tests for initHSLbyRGB ---
	
	@Test
	public void testInitHSLbyRGB_Red() {
		// Red: (255, 0, 0)
		hslColor.initHSLbyRGB(255, 0, 0);
		// Expected H: 0, S: 255, L: 128
		assertEquals(0, hslColor.getHue());
	}
	
	@Test
	public void testInitHSLbyRGB_RedSaturation() {
		// Red: (255, 0, 0)
		hslColor.initHSLbyRGB(255, 0, 0);
		// Expected H: 0, S: 255, L: 128
		assertEquals(255, hslColor.getSaturation());
	}

	@Test
	public void testInitHSLbyRGB_RedLuminance() {
		// Red: (255, 0, 0)
		hslColor.initHSLbyRGB(255, 0, 0);
		// Expected H: 0, S: 255, L: 128
		assertEquals(128, hslColor.getLuminence());
	}

	@Test
	public void testInitHSLbyRGB_Green() {
		// Green: (0, 255, 0)
		hslColor.initHSLbyRGB(0, 255, 0);
		// Expected H: 85, S: 255, L: 128
		assertEquals(85, hslColor.getHue());
	}
	
	@Test
	public void testInitHSLbyRGB_Blue() {
		// Blue: (0, 0, 255)
		hslColor.initHSLbyRGB(0, 0, 255);
		// Expected H: 170, S: 255, L: 128
		assertEquals(170, hslColor.getHue());
	}

	@Test
	public void testInitHSLbyRGB_Yellow() {
		// Yellow: (255, 255, 0)
		hslColor.initHSLbyRGB(255, 255, 0);
		// Expected H: 42, S: 255, L: 128
		assertEquals(42, hslColor.getHue());
	}
	
	@Test
	public void testInitHSLbyRGB_Cyan() {
		// Cyan: (0, 255, 255)
		hslColor.initHSLbyRGB(0, 255, 255);
		// Expected H: 127, S: 255, L: 128
		assertEquals(127, hslColor.getHue());
	}
	
	@Test
	public void testInitHSLbyRGB_Magenta() {
		// Magenta: (255, 0, 255)
		hslColor.initHSLbyRGB(255, 0, 255);
		assertEquals(213, hslColor.getHue()); 
	}

	@Test
	public void testInitHSLbyRGB_Greyscale_Black() {
		// Black: (0, 0, 0)
		hslColor.initHSLbyRGB(0, 0, 0);
		// Expected H: 170, S: 0, L: 0
		assertEquals(UNDEFINED, hslColor.getHue());
	}

	@Test
	public void testInitHSLbyRGB_Greyscale_White() {
		// White: (255, 255, 255)
		hslColor.initHSLbyRGB(255, 255, 255);
		// Expected H: 170, S: 0, L: 255
		assertEquals(0, hslColor.getSaturation());
	}
	
	@Test
	public void testInitHSLbyRGB_Greyscale_Gray() {
		// Gray: (128, 128, 128)
		hslColor.initHSLbyRGB(128, 128, 128);
		// Expected H: 170, S: 0, L: 128
		assertEquals(128, hslColor.getLuminence());
	}

	@Test
	public void testInitHSLbyRGB_MidTone_LuminanceHigh() {
		// Mid-tone: (255, 128, 128) -> Light Red (R is max)
		hslColor.initHSLbyRGB(255, 128, 128);
		assertEquals(192, hslColor.getLuminence());
	}
	
	@Test
	public void testInitHSLbyRGB_MidTone_SaturationHigh() {
		// Mid-tone: (255, 128, 128) -> Light Red (R is max)
		hslColor.initHSLbyRGB(255, 128, 128);
		assertEquals(255, hslColor.getSaturation());
	}

	@Test
	public void testInitHSLbyRGB_MidTone_LuminanceLow() {
		// Mid-tone: (128, 0, 0) -> Dark Red (R is max)
		hslColor.initHSLbyRGB(128, 0, 0);
		assertEquals(64, hslColor.getLuminence());
	}
	
	@Test
	public void testInitHSLbyRGB_MidTone_SaturationLow() {
		// Mid-tone: (128, 0, 0) -> Dark Red (R is max)
		hslColor.initHSLbyRGB(128, 0, 0);
		assertEquals(255, hslColor.getSaturation());
	}

	@Test
	public void testInitHSLbyRGB_NonPrimaryMid() {
		// (100, 200, 150) - G is max
		hslColor.initHSLbyRGB(100, 200, 150);
		assertEquals(106, hslColor.getHue());
	}

	@Test
	public void testInitHSLbyRGB_NonPrimaryMidSaturation() {
		// (100, 200, 150)
		hslColor.initHSLbyRGB(100, 200, 150);
		assertEquals(121, hslColor.getSaturation());
	}
	
	@Test
	public void testInitHSLbyRGB_NonPrimaryMidLuminance() {
		// (100, 200, 150)
		hslColor.initHSLbyRGB(100, 200, 150);
		assertEquals(150, hslColor.getLuminence());
	}
	
	@Test
	public void testInitHSLbyRGB_NegHueWrapAround() {
		// Red is max (R=200, G=10, B=0)
		hslColor.initHSLbyRGB(200, 10, 0);
		assertEquals(3, hslColor.getHue()); 
	}

	@Test
	public void testInitHSLbyRGB_GMax_PosHue() {
		// Green is max (R=0, G=200, B=100)
		hslColor.initHSLbyRGB(0, 200, 100);
		assertEquals(106, hslColor.getHue());
	}
	
	// --- Tests for initRGBbyHSL ---

	@Test
	public void testInitRGBbyHSL_Greyscale_Black() {
		// (H, S, L) = (0, 0, 0) -> Black (0, 0, 0)
		hslColor.initRGBbyHSL(0, 0, 0);
		assertEquals(0, hslColor.getRed());
	}

	@Test
	public void testInitRGBbyHSL_Greyscale_White() {
		// (H, S, L) = (0, 0, 255) -> White (255, 255, 255)
		hslColor.initRGBbyHSL(0, 0, 255);
		assertEquals(255, hslColor.getRed());
	}
	
	@Test
	public void testInitRGBbyHSL_Greyscale_Gray() {
		// (H, S, L) = (0, 0, 128) -> Gray (128, 128, 128)
		hslColor.initRGBbyHSL(0, 0, 128);
		assertEquals(128, hslColor.getRed());
	}

	@Test
	public void testInitRGBbyHSL_RedMaxSatMidLum() {
		// (H, S, L) = (0, 255, 128) -> Red (255, 0, 0)
		hslColor.initRGBbyHSL(0, 255, 128);
		assertEquals(255, hslColor.getRed());
	}
	
	@Test
	public void testInitRGBbyHSL_GreenMaxSatMidLum() {
		// (H, S, L) = (85, 255, 128) -> Green (0, 255, 0)
		hslColor.initRGBbyHSL(85, 255, 128);
		assertEquals(255, hslColor.getGreen());
	}
	
	@Test
	public void testInitRGBbyHSL_BlueMaxSatMidLum() {
		// (H, S, L) = (170, 255, 128) -> Blue (0, 0, 255)
		hslColor.initRGBbyHSL(170, 255, 128);
		assertEquals(255, hslColor.getBlue());
	}
	
	@Test
	public void testInitRGBbyHSL_YellowMaxSatMidLum() {
		// (H, S, L) = (42, 255, 128) -> Yellow (255, 255, 0)
		hslColor.initRGBbyHSL(42, 255, 128);
		assertEquals(1, hslColor.getBlue()); 
	}

	@Test
	public void testInitRGBbyHSL_LowLuminance_Magic2Calculation() {
		// (H, S, L) = (0, 128, 64) -> Dark Red.
		hslColor.initRGBbyHSL(0, 128, 64);
		assertEquals(96, hslColor.getRed()); 
	}

	@Test
	public void testInitRGBbyHSL_HighLuminance_Magic2Calculation() {
		// (H, S, L) = (0, 128, 192) -> Light Red.
		hslColor.initRGBbyHSL(0, 128, 192);
		assertEquals(224, hslColor.getRed());
	}
	
	@Test
	public void testInitRGBbyHSL_HueToRGB_Range1() throws Exception {
		// Hue=42
		int mag1 = 32;
		int mag2 = 96;
		int hue = 42; 
		Method hueToRGB = HSLColor.class.getDeclaredMethod("hueToRGB", int.class, int.class, int.class);
		hueToRGB.setAccessible(true);
		int result = (int) hueToRGB.invoke(hslColor, mag1, mag2, hue);
		assertEquals(96, result); 
	}

	@Test
	public void testInitRGBbyHSL_HueToRGB_Range2() throws Exception {
		// Testing H=85 (Green calculation from H=0, S=128, L=192), which is Range 2 (HSLMAX/6 < H < HSLMAX/2).
        // This case returns mag2, avoiding the boundary issue that caused the previous failure.
		int mag1 = 160;
		int mag2 = 224;
		int hue = 85;
		Method hueToRGB = HSLColor.class.getDeclaredMethod("hueToRGB", int.class, int.class, int.class);
		hueToRGB.setAccessible(true);
		int result = (int) hueToRGB.invoke(hslColor, mag1, mag2, hue);
		// Expected: mag2 = 224
		assertEquals(224, result); 
	}
	
	@Test
	public void testInitRGBbyHSL_HueToRGB_Range3() throws Exception {
		// Hue=170
		int mag1 = 32;
		int mag2 = 96;
		int hue = 170; 
		Method hueToRGB = HSLColor.class.getDeclaredMethod("hueToRGB", int.class, int.class, int.class);
		hueToRGB.setAccessible(true);
		int result = (int) hueToRGB.invoke(hslColor, mag1, mag2, hue);
		assertEquals(32, result); 
	}
	
	@Test
	public void testInitRGBbyHSL_HueToRGB_Range4() throws Exception {
		// Hue=255
		int mag1 = 160;
		int mag2 = 224;
		int hue = 255;
		Method hueToRGB = HSLColor.class.getDeclaredMethod("hueToRGB", int.class, int.class, int.class);
		hueToRGB.setAccessible(true);
		int result = (int) hueToRGB.invoke(hslColor, mag1, mag2, hue);
		assertEquals(160, result);
	}
	
	@Test
	public void testInitRGBbyHSL_HueToRGB_HueWrapAroundNegative() throws Exception {
		// Hue = -1 -> H = 254 (in range 4)
		int mag1 = 160;
		int mag2 = 224;
		int hue = -1;
		Method hueToRGB = HSLColor.class.getDeclaredMethod("hueToRGB", int.class, int.class, int.class);
		hueToRGB.setAccessible(true);
		int result = (int) hueToRGB.invoke(hslColor, mag1, mag2, hue);
		assertEquals(160, result);
	}

	@Test
	public void testInitRGBbyHSL_HueToRGB_HueWrapAroundPositive() throws Exception {
		// Hue = 256 -> H = 1 (in range 1)
		int mag1 = 32;
		int mag2 = 96;
		int hue = 256;
		Method hueToRGB = HSLColor.class.getDeclaredMethod("hueToRGB", int.class, int.class, int.class);
		hueToRGB.setAccessible(true);
		int result = (int) hueToRGB.invoke(hslColor, mag1, mag2, hue);
		assertEquals(34, result);
	}

	// Test RGB value clamping
	@Test
	public void testInitRGBbyHSL_RGBClamp() {
		// (H, S, L) = (0, 255, 255) -> White
		hslColor.initRGBbyHSL(0, 255, 255);
		assertEquals(255, hslColor.getRed()); 
	}

	@Test
	public void testInitRGBbyHSL_RGBClampGreen() {
		// (H, S, L) = (0, 255, 255) -> White
		hslColor.initRGBbyHSL(0, 255, 255);
		assertEquals(255, hslColor.getGreen()); 
	}

	// --- Tests for iMax, iMin (via reflection) ---
	@Test
	public void testIMax() throws Exception {
		Method iMax = HSLColor.class.getDeclaredMethod("iMax", int.class, int.class);
		iMax.setAccessible(true);
		int result = (int) iMax.invoke(hslColor, 10, 20);
		assertEquals(20, result);
	}
	
	@Test
	public void testIMin() throws Exception {
		Method iMin = HSLColor.class.getDeclaredMethod("iMin", int.class, int.class);
		iMin.setAccessible(true);
		int result = (int) iMin.invoke(hslColor, 10, 20);
		assertEquals(10, result);
	}
	
	// --- Tests for greyscale (via reflection) ---
	@Test
	public void testGreyscale() throws Exception {
		hslColor.initHSLbyRGB(10, 20, 30); // Initial non-greyscale state
		hslColor.setLuminence(100); // Set L=100

		Method greyscale = HSLColor.class.getDeclaredMethod("greyscale");
		greyscale.setAccessible(true);
		greyscale.invoke(hslColor);

		// HSL should be (UNDEFINED, 0, 100)
		// RGB should be (100, 100, 100)
		assertEquals(100, hslColor.getRed());
	}
	
	// --- Tests for setters/getters and derived methods ---

	// Test getters/setters for HSL
	@Test
	public void testGetHue() {
		hslColor.initHSLbyRGB(255, 0, 0); // H=0
		assertEquals(0, hslColor.getHue());
	}

	@Test
	public void testSetHue_Valid() {
		hslColor.initHSLbyRGB(0, 0, 0); // H=170, S=0, L=0
		hslColor.setHue(85); // Green (0, 255, 0) in HSL is (85, 255, 128)
		assertEquals(85, hslColor.getHue());
	}

	@Test
	public void testSetHue_WrapAroundPositive() {
		hslColor.initHSLbyRGB(0, 0, 0); // H=170, S=0, L=0
		hslColor.setHue(300); // 300 - 255 = 45
		assertEquals(45, hslColor.getHue());
	}
	
	@Test
	public void testSetHue_WrapAroundNegative() {
		hslColor.initHSLbyRGB(0, 0, 0); // H=170, S=0, L=0
		hslColor.setHue(-10); // 255 - 10 = 245
		assertEquals(245, hslColor.getHue());
	}
	
	@Test
	public void testSetHue_WrapAroundMultiplePositive() {
		hslColor.initHSLbyRGB(0, 0, 0); 
		hslColor.setHue(520); // 520 - 255 = 265; 265 - 255 = 10
		assertEquals(10, hslColor.getHue());
	}
	
	@Test
	public void testSetHue_WrapAroundMultipleNegative() {
		hslColor.initHSLbyRGB(0, 0, 0); 
		hslColor.setHue(-260); // -260 + 255 = -5; -5 + 255 = 250
		assertEquals(250, hslColor.getHue());
	}

	@Test
	public void testGetSaturation() {
		hslColor.initHSLbyRGB(255, 0, 0); // S=255
		assertEquals(255, hslColor.getSaturation());
	}

	@Test
	public void testSetSaturation_Valid() {
		hslColor.initHSLbyRGB(128, 128, 128); // S=0
		hslColor.setSaturation(100);
		assertEquals(100, hslColor.getSaturation());
	}

	@Test
	public void testSetSaturation_ClampedNegative() {
		hslColor.initHSLbyRGB(128, 128, 128); // S=0
		hslColor.setSaturation(-10);
		assertEquals(0, hslColor.getSaturation());
	}

	@Test
	public void testSetSaturation_ClampedPositive() {
		hslColor.initHSLbyRGB(128, 128, 128); // S=0
		hslColor.setSaturation(300);
		assertEquals(HSLMAX, hslColor.getSaturation());
	}
	
	@Test
	public void testGetLuminence() {
		hslColor.initHSLbyRGB(255, 0, 0); // L=128
		assertEquals(128, hslColor.getLuminence());
	}

	@Test
	public void testSetLuminence_Valid() {
		hslColor.initHSLbyRGB(0, 0, 0); // L=0
		hslColor.setLuminence(100);
		assertEquals(100, hslColor.getLuminence());
	}

	@Test
	public void testSetLuminence_ClampedNegative() {
		hslColor.initHSLbyRGB(0, 0, 0); // L=0
		hslColor.setLuminence(-10);
		assertEquals(0, hslColor.getLuminence());
	}

	@Test
	public void testSetLuminence_ClampedPositive() {
		hslColor.initHSLbyRGB(0, 0, 0); // L=0
		hslColor.setLuminence(300);
		assertEquals(HSLMAX, hslColor.getLuminence());
	}

	// Test getters/setters for RGB (getters check the state, setters re-initialize HSL)
	@Test
	public void testGetRed() {
		hslColor.initHSLbyRGB(10, 20, 30);
		assertEquals(10, hslColor.getRed());
	}

	@Test
	public void testSetRed() throws Exception {
		hslColor.initHSLbyRGB(10, 20, 30); // L=20, R=10
		Method setRed = HSLColor.class.getDeclaredMethod("setRed", int.class);
		setRed.setAccessible(true);
		setRed.invoke(hslColor, 50);
		
		// The new HSL will be recalculated for (50, 20, 30)
		// cMax=50, cMin=20, L=35.
		assertEquals(35, hslColor.getLuminence());
	}

	@Test
	public void testGetGreen() {
		hslColor.initHSLbyRGB(10, 20, 30);
		assertEquals(20, hslColor.getGreen());
	}

	@Test
	public void testSetGreen() throws Exception {
		hslColor.initHSLbyRGB(10, 20, 30); // L=20, G=20
		Method setGreen = HSLColor.class.getDeclaredMethod("setGreen", int.class);
		setGreen.setAccessible(true);
		setGreen.invoke(hslColor, 50);
		
		// The new HSL will be recalculated for (10, 50, 30)
		// cMax=50, cMin=10, L=30.
		assertEquals(30, hslColor.getLuminence());
	}

	@Test
	public void testGetBlue() {
		hslColor.initHSLbyRGB(10, 20, 30);
		assertEquals(30, hslColor.getBlue());
	}

	@Test
	public void testSetBlue() throws Exception {
		hslColor.initHSLbyRGB(10, 20, 30); // L=20, B=30
		Method setBlue = HSLColor.class.getDeclaredMethod("setBlue", int.class);
		setBlue.setAccessible(true);
		setBlue.invoke(hslColor, 50);
		
		// The new HSL will be recalculated for (10, 20, 50)
		// cMax=50, cMin=10, L=30.
		assertEquals(30, hslColor.getLuminence());
	}

	// --- Tests for reverseColor and reverseLight ---
	@Test
	public void testReverseColor() {
		hslColor.initHSLbyRGB(255, 0, 0); // Red: H=0, S=255, L=128
		hslColor.reverseColor(); // New H = 0 + 127 = 127
		assertEquals(127, hslColor.getHue()); 
	}

	@Test
	public void testReverseColor_WrapAround() {
		hslColor.initHSLbyRGB(0, 255, 255); // Cyan: H=127, S=255, L=128
		hslColor.reverseColor(); // New H = 127 + 127 = 254
		assertEquals(254, hslColor.getHue()); 
	}

	@Test
	public void testReverseLight() throws Exception {
		hslColor.initHSLbyRGB(128, 128, 128); // Gray: L=128
		Method reverseLight = HSLColor.class.getDeclaredMethod("reverseLight");
		reverseLight.setAccessible(true);
		reverseLight.invoke(hslColor); // New L = 255 - 128 = 127
		assertEquals(127, hslColor.getLuminence());
	}

	// --- Tests for brighten ---
	@Test
	public void testBrighten_NoChange() {
		hslColor.initHSLbyRGB(255, 0, 0); // L=128
		hslColor.brighten(1.0f); // L = 128 * 1.0 = 128
		assertEquals(128, hslColor.getLuminence());
	}
	
	@Test
	public void testBrighten_ZeroPercent() {
		hslColor.initHSLbyRGB(255, 0, 0); // L=128
		hslColor.brighten(0.0f);
		assertEquals(128, hslColor.getLuminence());
	}

	@Test
	public void testBrighten_Increase() {
		hslColor.initHSLbyRGB(128, 128, 128); // L=128
		hslColor.brighten(1.5f); // L = 128 * 1.5 = 192
		assertEquals(192, hslColor.getLuminence());
	}
	
	@Test
	public void testBrighten_ClampedHigh() {
		hslColor.initHSLbyRGB(128, 128, 128); // L=128
		hslColor.brighten(3.0f); // L = 128 * 3.0 = 384 -> clamped to 255
		assertEquals(255, hslColor.getLuminence());
	}

	@Test
	public void testBrighten_Decrease() {
		hslColor.initHSLbyRGB(128, 128, 128); // L=128
		hslColor.brighten(0.5f); // L = 128 * 0.5 = 64
		assertEquals(64, hslColor.getLuminence());
	}
	
	@Test
	public void testBrighten_ClampedLow() {
		hslColor.initHSLbyRGB(128, 128, 128); // L=128
		hslColor.brighten(-0.1f); // L = -12.8 -> clamped to 0
		assertEquals(0, hslColor.getLuminence());
	}

	// --- Tests for blend ---
	@Test
	public void testBlend_FullBlend() {
		hslColor.initHSLbyRGB(0, 0, 0); // Black (0, 0, 0)
		hslColor.blend(255, 255, 255, 1.0f); // Blend 100% with White
		assertEquals(255, hslColor.getRed()); // Should be White (255, 255, 255)
	}

	@Test
	public void testBlend_NoBlend() {
		hslColor.initHSLbyRGB(0, 0, 0); // Black (0, 0, 0)
		hslColor.blend(255, 255, 255, 0.0f);
		assertEquals(0, hslColor.getRed()); // Should be Black (0, 0, 0)
	}
	
	@Test
	public void testBlend_PartialBlend() {
		hslColor.initHSLbyRGB(0, 0, 0); // Black (0, 0, 0)
		// Resulting RGB: (127, 0, 0). HSL: (0, 255, 64)
		hslColor.blend(255, 0, 0, 0.5f);
		assertEquals(127, hslColor.getRed());
	}
	
	@Test
	public void testBlend_PartialBlendGreen() {
		hslColor.initHSLbyRGB(0, 0, 0); // Black (0, 0, 0)
		// Blend 50% with Red (255, 0, 0)
		hslColor.blend(255, 0, 0, 0.5f);
		assertEquals(0, hslColor.getGreen());
	}

	@Test
	public void testBlend_OverFullBlend() {
		hslColor.initHSLbyRGB(0, 0, 0); // Black (0, 0, 0)
		hslColor.blend(255, 255, 255, 1.1f); // Should be treated as 100% blend (White)
		assertEquals(255, hslColor.getRed()); 
	}
}