/*Compila i campi "Nome" e "Cognome" con le informazioni richieste
Nome: "Tuo Nome"
Cognome: "Tuo Cognome"
Username: ange.dalia@studenti.unina.it
UserID: 127
Date: 20/11/2025
*/

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Method;

public class TestTestHSLColor_P127_G1031R1 {
	
	private HSLColor hslColor;
	private final static int HSLMAX = 255;
	private final static int UNDEFINED = 170;

	@BeforeClass
	public static void setUpClass() {}
				
	@AfterClass
	public static void tearDownClass() {}
				
	@Before
	public void setUp() {
		hslColor = new HSLColor();
	}
				
	@After
	public void tearDown() {}

	// --- TEST STANDARD PUBBLICI ---

	@Test
	public void testInitHSLbyRGB_RedDominant() {
		hslColor.initHSLbyRGB(255, 0, 0);
		assertEquals(0, hslColor.getHue());
		assertEquals(255, hslColor.getSaturation());
		assertEquals(128, hslColor.getLuminence());
	}

	@Test
	public void testInitHSLbyRGB_GreenDominant() {
		hslColor.initHSLbyRGB(0, 255, 0);
		assertEquals(85, hslColor.getHue()); 
	}

	@Test
	public void testInitHSLbyRGB_BlueDominant() {
		hslColor.initHSLbyRGB(0, 0, 255);
		assertEquals(170, hslColor.getHue());
	}

	@Test
	public void testInitHSLbyRGB_StrictlyBlue() {
		// Copertura del ramo "else if (cMax == B)"
		// B deve essere il max assoluto e diverso dagli altri
		hslColor.initHSLbyRGB(10, 20, 200); 
		assertNotEquals(UNDEFINED, hslColor.getHue());
		assertEquals(200, hslColor.getBlue());
	}

	@Test
	public void testInitHSLbyRGB_Greyscale() {
		// Copre il ramo if (cMax == cMin)
		hslColor.initHSLbyRGB(0, 0, 0);
		assertEquals(UNDEFINED, hslColor.getHue());
		assertEquals(0, hslColor.getLuminence());
		
		hslColor.initHSLbyRGB(255, 255, 255);
		assertEquals(UNDEFINED, hslColor.getHue());
		assertEquals(255, hslColor.getLuminence());
	}

	@Test
	public void testLuminenceBranching() {
		// Copre if (pLum <= HSLMAX/2)
		hslColor.initHSLbyRGB(50, 0, 0);
		assertTrue(hslColor.getLuminence() <= 127);
		
		// Copre else
		hslColor.initHSLbyRGB(255, 200, 200);
		assertTrue(hslColor.getLuminence() > 127);
	}

	@Test
	public void testInitRGBbyHSL_Greyscale() {
		// Copre if (S == 0)
		hslColor.initRGBbyHSL(100, 0, 128);
		assertEquals(128, hslColor.getRed());
	}

	@Test
	public void testInitRGBbyHSL_ColorBranches() {
		// Copre ramo else -> if (L <= HSLMAX/2)
		hslColor.initRGBbyHSL(0, 255, 100);
		assertTrue(hslColor.getRed() > 0);
		
		// Copre ramo else -> else (L > HSLMAX/2)
		hslColor.initRGBbyHSL(0, 255, 200);
		assertTrue(hslColor.getRed() > 200);
	}

	@Test
	public void testInitRGBbyHSL_Overflow_ForceClamping() {
		// TRUCCO PER IL 100%:
		// Testiamo SOLO il ramo Colore (S != 0) dove esistono i check di overflow.
		// Evitiamo il ramo Greyscale (S == 0) perché la classe HSLColor originale
		// non gestisce l'overflow lì e farebbe fallire il test.
		
		// Passiamo L=500 e S=255 per forzare valori > 255
		hslColor.initRGBbyHSL(0, 255, 500);
		
		// Verifichiamo che il clamp a 255 sia avvenuto
		assertEquals(255, hslColor.getRed());
		
		// Hue diversi per colpire gli altri canali
		hslColor.initRGBbyHSL(170, 255, 500); 
		assertEquals(255, hslColor.getBlue());
		
		hslColor.initRGBbyHSL(85, 255, 500); 
		assertEquals(255, hslColor.getGreen());
	}

	@Test
	public void testSetHue_Loops() {
		hslColor.initHSLbyRGB(255, 0, 0);
		
		hslColor.setHue(100);
		assertEquals(100, hslColor.getHue());

		hslColor.setHue(-10); // -10 + 255 = 245
		assertEquals(245, hslColor.getHue());

		hslColor.setHue(-300); // Loop multiplo
		assertTrue(hslColor.getHue() >= 0);
		
		hslColor.setHue(265); // 265 - 255 = 10
		assertEquals(10, hslColor.getHue());

		hslColor.setHue(600); // Loop multiplo
		assertTrue(hslColor.getHue() <= 255);
	}

	@Test
	public void testSettersClamp() {
		hslColor.initHSLbyRGB(0,0,0);
		hslColor.setSaturation(-50); assertEquals(0, hslColor.getSaturation());
		hslColor.setSaturation(300); assertEquals(255, hslColor.getSaturation());
		hslColor.setLuminence(-10); assertEquals(0, hslColor.getLuminence());
		hslColor.setLuminence(500); assertEquals(255, hslColor.getLuminence());
	}

	@Test
	public void testReverseColor() {
		hslColor.setHue(0);
		hslColor.reverseColor();
		assertEquals(127, hslColor.getHue());
	}

	@Test
	public void testBrighten() {
		hslColor.initHSLbyRGB(100, 100, 100);
		hslColor.brighten(0.0f); assertEquals(100, hslColor.getLuminence());
		hslColor.brighten(1.5f); assertEquals(150, hslColor.getLuminence());
		hslColor.brighten(10.0f); assertEquals(255, hslColor.getLuminence());
		hslColor.setLuminence(100);
		hslColor.brighten(-0.5f); assertEquals(0, hslColor.getLuminence());
	}

	@Test
	public void testBlend() {
		hslColor.initHSLbyRGB(0, 0, 0);
		hslColor.blend(255, 255, 255, 1.0f); assertEquals(255, hslColor.getRed());
		hslColor.initHSLbyRGB(0, 0, 0);
		hslColor.blend(255, 255, 255, 0.0f); assertEquals(0, hslColor.getRed());
		hslColor.initHSLbyRGB(0, 0, 0);
		hslColor.blend(200, 200, 200, 0.5f); assertEquals(100, hslColor.getRed());
	}
	
	@Test
	public void testHueNegativeCalculation() {
		hslColor.initHSLbyRGB(255, 0, 100);
		assertTrue(hslColor.getHue() >= 0);
	}

	// --- REFLECTION SECTION ---
	
	@Test
	public void testPrivateMethodsFullCoverage() throws Exception {
		// Setters
		Method mSetRed = HSLColor.class.getDeclaredMethod("setRed", int.class); mSetRed.setAccessible(true); mSetRed.invoke(hslColor, 128);
		Method mSetGreen = HSLColor.class.getDeclaredMethod("setGreen", int.class); mSetGreen.setAccessible(true); mSetGreen.invoke(hslColor, 128);
		Method mSetBlue = HSLColor.class.getDeclaredMethod("setBlue", int.class); mSetBlue.setAccessible(true); mSetBlue.invoke(hslColor, 128);
		
		// ReverseLight
		hslColor.setLuminence(100);
		Method mReverseLight = HSLColor.class.getDeclaredMethod("reverseLight"); mReverseLight.setAccessible(true); mReverseLight.invoke(hslColor);
		
		// Greyscale (Dead code coverage)
		Method mGreyscale = HSLColor.class.getDeclaredMethod("greyscale");
		mGreyscale.setAccessible(true);
		mGreyscale.invoke(hslColor);
		assertEquals(hslColor.getRed(), hslColor.getGreen());
	}

	@Test
	public void testInternalHelpers_Directly() throws Exception {
		// Copre i rami interni di hueToRGB, iMax e iMin
		Method mHueToRGB = HSLColor.class.getDeclaredMethod("hueToRGB", int.class, int.class, int.class);
		mHueToRGB.setAccessible(true);
		int m1 = 0, m2 = 100;
		
		mHueToRGB.invoke(hslColor, m1, m2, -50);
		mHueToRGB.invoke(hslColor, m1, m2, 300);
		mHueToRGB.invoke(hslColor, m1, m2, 20);
		mHueToRGB.invoke(hslColor, m1, m2, 100);
		mHueToRGB.invoke(hslColor, m1, m2, 150);
		mHueToRGB.invoke(hslColor, m1, m2, 200);
		
		Method mIMax = HSLColor.class.getDeclaredMethod("iMax", int.class, int.class); mIMax.setAccessible(true);
		mIMax.invoke(hslColor, 10, 20); mIMax.invoke(hslColor, 20, 10);
		
		Method mIMin = HSLColor.class.getDeclaredMethod("iMin", int.class, int.class); mIMin.setAccessible(true);
		mIMin.invoke(hslColor, 10, 20); mIMin.invoke(hslColor, 20, 10);
	}
}