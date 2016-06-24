package hardware;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class ArithmeticUnitTest {
	ConditionCodeRegister ccr;
	ArithmeticUnit au;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ccr = new ConditionCodeRegister();
		au = new ArithmeticUnit(ccr);
	}

	@After
	public void tearDown() throws Exception {
		ccr = null;
		au = null;
	}

	@Test
	public void testAddBytes() {
		ccr.clearAllCodes();
		byte byte00 = (byte) 0x00;
		byte byteFF = (byte) 0xFF;
		byte byteFE = (byte) 0xFE;
		Byte cc;
		// 00 00
		cc = (byte) 0B01000110; // Zero & Parity
		assertThat("Add 00 00", byte00, equalTo(au.add(byte00, byte00)));
		assertThat("Add 00 00 cc", cc, equalTo(ccr.getConditionCode()));
		// 00 FF
		cc = (byte) 0B10000110; // Sign & Parity
		assertThat("Add 00 FF", byteFF, equalTo(au.add(byte00, byteFF)));
		assertThat("Add 00 FF cc", cc, equalTo(ccr.getConditionCode()));
		// FF FF
		cc = (byte) 0B10010011; // Sign Aux Carry & Carry
		assertThat("Add FF FF", byteFE, equalTo(au.add(byteFF, byteFF)));
		assertThat("Add FF FF cc", cc, equalTo(ccr.getConditionCode()));

		Random random = new Random();
		int testCount = 1000;
		byte[] values1 = new byte[testCount];
		byte[] values2 = new byte[testCount];
		byte value1, value2, ans;
		random.nextBytes(values1);
		random.nextBytes(values2);
		for (int i = 0; i < testCount; i++) {
			value1 = values1[i];
			value2 = values2[i];
			ans = (byte) (value1 + value2);
			assertThat("random  byte adds", ans, equalTo(au.add(value1, value2)));
		}// for
	}

	@Test
	public void testAddInts() {
		ccr.clearAllCodes();
		int int0 = 0X0000;
		int intFFFF = 0XFFFF;
		int intFFFE = 0XFFFE;

		byte cc = (byte) 0B00000010; // nothing set
		assertThat("Add 0000 0000", int0, equalTo(au.add(int0, int0)));
		assertThat("Add 0000 0000", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B00000010; // nothing set
		assertThat("Add 0000 FFFF", intFFFF, equalTo(au.add(int0, intFFFF)));
		assertThat("Add 0000 FFFF", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B00000011; // Carry set
		assertThat("Add FFFF FFFF", intFFFE, equalTo(au.add(intFFFF, intFFFF)));
		assertThat("Add FFFF FFFF", cc, equalTo(ccr.getConditionCode()));

		Random random = new Random();
		int testCount = 1000;int valueA,valueB,valueC;

		for (int i = 0; i < testCount; i++) {
			valueA = random.nextInt(0XFFFF);
			valueB = random.nextInt(0XFFFF);
			valueC = valueA + valueB;
			cc = valueC > 0XFFFF?(byte) 0B00000011:(byte) 0B00000010;
			valueC = valueC & 0XFFFF;
			assertThat("Random word adds",valueC,equalTo(au.add(valueA, valueB)) );
			assertThat("Random word adds",cc,equalTo(ccr.getConditionCode()));
		}//for

	}

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }

}
