package tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import fastCText2.*;

public class BMColumn_tests {
	static BMcolumn tester0;
	static BMcolumn tester1;
	static BMcolumn tester2;
	static BMcolumn tester3;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Testing strings
		// testW1 = 89478485
		String testW0 = "0101010101010101010101010101";
		// testW1 = 1431655765
		String testW1 = "10101010101010101010101010101010";
		// testW2 = 1431655765,1431655765
		String testW2 = "0101010101010101010101010101010101010101010101010101010101010101";
		// testW3 = 1431655765,1431655765,5
		String testW3 = "01010101010101010101010101010101010101010101010101010101010101010101";
		
		tester0 = new BMcolumn(testW0);
		tester1 = new BMcolumn(testW1);
		tester2 = new BMcolumn(testW2);
		tester3 = new BMcolumn(testW3);
	}

	@Test
	public void testBMcolumn() {
		// Tests
		assertEquals("Debe tener 28 filas", 28, tester0.length);
		assertEquals("Debe tener 1 sola palabra", 1, tester0.nwords);
		assertEquals("La palabra debe valer 89478485", 89478485, tester0.data[0]);
		//========================================================================
		assertEquals("Debe tener 32 filas", 32, tester1.length);
		assertEquals("Debe tener 1 sola palabra", 1, tester1.nwords);
		assertEquals("La palabra debe valer -1431655766", -1431655766, tester1.data[0]);
		//========================================================================
		assertEquals("Debe tener 64 filas", 64, tester2.length);
		assertEquals("Debe tener 2 palabras", 2, tester2.nwords);
		assertEquals("La palabra debe valer 1431655765", 1431655765, tester2.data[0]);
		assertEquals("La palabra debe valer 1431655765", 1431655765, tester2.data[1]);
		//========================================================================
		assertEquals("Debe tener 68 filas", 68, tester3.length);
		assertEquals("Debe tener 3 palabras", 3, tester3.nwords);
		assertEquals("La palabra debe valer 1431655765", 1431655765, tester3.data[0]);
		assertEquals("La palabra debe valer 1431655765", 1431655765, tester3.data[1]);
		assertEquals("La palabra debe valer 5", 5, tester3.data[2]);
	}
}
