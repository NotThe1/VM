package hardware;

import static org.junit.Assert.*;

import java.util.Random;


import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class ArithmeticUnitTest {
	IConditionCodeRegister ccr;
	ArithmeticUnit au;

	@Before
	public void setUp() throws Exception {
		au = ArithmeticUnit.getArithmeticUnit();
		ccr = ConditionCodeRegister.getConditionCodeRegister();
	}// setUp

	@Test
	public void testAddBytes() {

		ccr.clearAllCodes();
		byte byte00 = (byte) 0x00;
		byte byteFF = (byte) 0xFF;
		byte byteFE = (byte) 0xFE;
		Byte cc;

		// 00 00
		cc = (byte) 0B01000110; // Zero & Parity
		assertThat("testAddBytes 00 00", byte00, equalTo(au.add(byte00, byte00)));
		assertThat("testAddBytes 00 00 cc", cc, equalTo(ccr.getConditionCode()));
		// 00 FF
		cc = (byte) 0B10000110; // Sign & Parity
		assertThat("testAddBytes 00 FF", byteFF, equalTo(au.add(byte00, byteFF)));
		assertThat("testAddBytes 00 FF cc", cc, equalTo(ccr.getConditionCode()));
		// FF FF
		cc = (byte) 0B10010011; // Sign Aux Carry & Carry
		assertThat("testAddBytes FF FF", byteFE, equalTo(au.add(byteFF, byteFF)));
		assertThat("testAddBytes FF FF cc", cc, equalTo(ccr.getConditionCode()));

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
			assertThat("random  byte testAddBytes", ans, equalTo(au.add(value1, value2)));
		}// for
	}// testAddBytes

	@Test
	public void testAddInts() {

		ccr.clearAllCodes();
		int int0 = 0X0000;
		int intFFFF = 0XFFFF;
		int intFFFE = 0XFFFE;

		byte cc = (byte) 0B00000010; // nothing set
		assertThat("testAddInts 0000 0000", int0, equalTo(au.add(int0, int0)));
		assertThat("testAddInts 0000 0000", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B00000010; // nothing set
		assertThat("testAddInts 0000 FFFF", intFFFF, equalTo(au.add(int0, intFFFF)));
		assertThat("testAddInts 0000 FFFF", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B00000011; // Carry set
		assertThat("testAddInts FFFF FFFF", intFFFE, equalTo(au.add(intFFFF, intFFFF)));
		assertThat("testAddInts FFFF FFFF", cc, equalTo(ccr.getConditionCode()));

		Random random = new Random();
		int testCount = 1000;
		int valueA, valueB, valueC;

		for (int i = 0; i < testCount; i++) {
			valueA = random.nextInt(0XFFFF);
			valueB = random.nextInt(0XFFFF);
			valueC = valueA + valueB;
			cc = valueC > 0XFFFF ? (byte) 0B00000011 : (byte) 0B00000010;
			valueC = valueC & 0XFFFF;
			assertThat("Random word testAddInts", valueC, equalTo(au.add(valueA, valueB)));
			assertThat("Random word testAddInts", cc, equalTo(ccr.getConditionCode()));
		}// for

	}// testAddInts

	@Test
	public void testAddWithCarry() {
		// Carry is in Condition least Significant Bit

		ccr.clearAllCodes();
		byte byte00 = (byte) 0x00;
		byte byte01 = (byte) 0x01;
		byte byteFF = (byte) 0xFF;
		byte byteFE = (byte) 0xFE;
		Byte cc;

		// 00 00
		ccr.setCarryFlag(false);
		cc = (byte) 0B01000110; // Zero & Parity
		assertThat("testAddWithCarry 00 00", byte00, equalTo(au.addWithCarry(byte00, byte00)));
		assertThat("testAddWithCarry 00 00 cc", cc, equalTo(ccr.getConditionCode()));
		ccr.setCarryFlag(true);
		cc = (byte) 0B0000010; //
		assertThat("testAddWithCarry 00 00", byte01, equalTo(au.addWithCarry(byte00, byte00)));
		assertThat("testAddWithCarry 00 00 cc", cc, equalTo(ccr.getConditionCode()));

		// 00 FF
		ccr.setCarryFlag(false);
		cc = (byte) 0B10000110; // Sign & Parity
		assertThat("testAddWithCarry 00 FF", byteFF, equalTo(au.addWithCarry(byte00, byteFF)));
		assertThat("testAddWithCarry 00 FF cc", cc, equalTo(ccr.getConditionCode()));
		ccr.setCarryFlag(true);
		cc = (byte) 0B01010111; // Sign,Zero & Parity & Carry
		assertThat("testAddWithCarry 00 FF", byte00, equalTo(au.addWithCarry(byte00, byteFF)));
		assertThat("testAddWithCarry 00 FF cc", cc, equalTo(ccr.getConditionCode()));

		// FF FF
		ccr.setCarryFlag(false);
		cc = (byte) 0B10010011; // Sign Aux Carry & Carry
		assertThat("testAddWithCarry FF FF", byteFE, equalTo(au.addWithCarry(byteFF, byteFF)));
		assertThat("testAddWithCarry FF FF cc", cc, equalTo(ccr.getConditionCode()));
		ccr.setCarryFlag(true);
		cc = (byte) 0B10010111; // Sign , Parity, Aux Carry & Carry
		assertThat("testAddWithCarry FF FF", byteFF, equalTo(au.addWithCarry(byteFF, byteFF)));
		assertThat("testAddWithCarry FF FF cc", cc, equalTo(ccr.getConditionCode()));

		// Random addWith Carry
		Random random = new Random();
		int testCount = 1000;
		byte[] values1 = new byte[testCount];
		byte[] values2 = new byte[testCount];
		byte value1, value2, ans;

		// Carry Flag = 0;
		random.nextBytes(values1);
		random.nextBytes(values2);
		for (int i = 0; i < testCount; i++) {
			value1 = values1[i];
			value2 = values2[i];
			ans = (byte) (value1 + value2);
			ccr.setCarryFlag(false);
			assertThat("random  byte testAddWithCarry", ans, equalTo(au.addWithCarry(value1, value2)));
		}// for

		// Carry Flag = 0;

		random.nextBytes(values1);
		random.nextBytes(values2);
		for (int i = 0; i < testCount; i++) {
			value1 = values1[i];
			value2 = values2[i];
			ans = (byte) (value1 + value2 + 1);
			ccr.setCarryFlag(true);
			assertThat("random  byte testAddWithCarry", ans, equalTo(au.addWithCarry(value1, value2)));
		}// for

	}// testAddWithCarry

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Test
	public void testSubtractBytes() {

		ccr.clearAllCodes();
		byte byte00 = (byte) 0x00;
		byte byte01 = (byte) 0x01;
		byte byteFF = (byte) 0xFF;
		Byte cc;

		// 00 00
		cc = (byte) 0B01000110; // Zero & Parity
		assertThat("testSubtractBytes 00 00", byte00, equalTo(au.subtract(byte00, byte00)));
		assertThat("testSubtractBytes 00 00 cc", cc, equalTo(ccr.getConditionCode()));

		// 00 FF
		cc = (byte) 0B00000011; // only Carry
		assertThat("testSubtractBytes 00 FF", byte01, equalTo(au.subtract(byte00, byteFF)));
		assertThat("testSubtractBytes 00 FF cc", cc, equalTo(ccr.getConditionCode()));

		// FF FF
		cc = (byte) 0B01010110; // Zero Aux Carry & Parity
		assertThat("testSubtractBytes FF FF", byte00, equalTo(au.subtract(byteFF, byteFF)));
		assertThat("testSubtractBytes FF FF cc", cc, equalTo(ccr.getConditionCode()));

		// Random Subtracts
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
			ans = (byte) (value1 - value2);
			ccr.setCarryFlag(false);
			assertThat("random  byte testSubtractBytes", ans, equalTo(au.subtract(value1, value2)));
		}// for
	}// testSubtractBytes

	@Test
	public void testSubtractInts() {

		ccr.clearAllCodes();
		int int0000 = 0X0000;
		int int0001 = 0X0001;
		int intFFFF = 0XFFFF;

		byte cc = (byte) 0B00000010; // nothing set
		ccr.setConditionCode(cc);
		assertThat("testSubtractInts 0000 0000", int0000, equalTo(au.subtract(int0000, int0000)));
		assertThat("testSubtractInts 0000 0000", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B00000011; // Carry
		ccr.setConditionCode(cc);
		assertThat("testSubtractInts 0000 FFFF", int0001, equalTo(au.subtract(int0000, intFFFF)));
		assertThat("testSubtractInts 0000 FFFF", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B01000110; // Zero & Parity set1
		ccr.setConditionCode(cc);
		assertThat("testSubtractInts FFFF FFFF", int0000, equalTo(au.subtract(intFFFF, intFFFF)));
		assertThat("testSubtractInts FFFF FFFF", cc, equalTo(ccr.getConditionCode()));

		// Random Subtracts
		Random random = new Random();
		int testCount = 1000;
		int valueA, valueB, valueC;

		for (int i = 0; i < testCount; i++) {
			valueA = random.nextInt(0XFFFF);
			valueB = random.nextInt(0XFFFF);
			valueC = (valueA - valueB) & 0XFFFF;
			cc = valueC > 0XFFFF ? (byte) 0B00000011 : (byte) 0B00000010;
			valueC = valueC & 0XFFFF;
			assertThat("Random word testSubtractInts", valueC, equalTo(au.subtract(valueA, valueB)));
		}// for
	}// testSubtractInts

	@Test
	public void testSubtractBytesWithCarry() {

		ccr.clearAllCodes();
		byte byte00 = (byte) 0x00;
		byte byte01 = (byte) 0x01;
		byte byteFF = (byte) 0xFF;
		Byte cc;

		// 00 00
		cc = (byte) 0B01000110; // Zero & Parity
		ccr.setCarryFlag(false);
		assertThat("testSubtractBytesWithCarry 00 00", byte00, equalTo(au.subtractWithBorrow(byte00, byte00)));
		assertThat("testSubtractBytesWithCarry 00 00 cc", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B10000111; // Sign Carry & Parity
		ccr.setCarryFlag(true);
		assertThat("testSubtractBytesWithCarry 00 00", byteFF, equalTo(au.subtractWithBorrow(byte00, byte00)));
		assertThat("testSubtractBytesWithCarry 00 00 cc", cc, equalTo(ccr.getConditionCode()));

		// 00 FF
		cc = (byte) 0B00000011; // only Carry
		ccr.setCarryFlag(false);
		assertThat("testSubtractBytesWithCarry 00 FF", byte01, equalTo(au.subtractWithBorrow(byte00, byteFF)));
		assertThat("testSubtractBytesWithCarry 00 FF cc", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B01000110; // Zero & Parity
		ccr.setCarryFlag(true);
		assertThat("testSubtractBytesWithCarry 00 FF", byte00, equalTo(au.subtractWithBorrow(byte00, byteFF)));
		assertThat("testSubtractBytesWithCarry 00 FF cc", cc, equalTo(ccr.getConditionCode()));

		// FF FF
		cc = (byte) 0B01010110; // Zero Aux Carry & Parity
		ccr.setCarryFlag(false);
		assertThat("testSubtractBytesWithCarry FF FF", byte00, equalTo(au.subtractWithBorrow(byteFF, byteFF)));
		assertThat("testSubtractBytesWithCarry FF FF cc", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B10000110; // Sign & Parity
		ccr.setCarryFlag(true);
		assertThat("testSubtractBytesWithCarry FF FF", byteFF, equalTo(au.subtractWithBorrow(byteFF, byteFF)));
		assertThat("testSubtractBytesWithCarry FF FF cc", cc, equalTo(ccr.getConditionCode()));

		// Random Subtracts
		Random random = new Random();
		int testCount = 1000;
		byte[] values1 = new byte[testCount];
		byte[] values2 = new byte[testCount];
		byte value1, value2, ans;
		random.nextBytes(values1);
		random.nextBytes(values2);

		// no carry
		for (int i = 0; i < testCount; i++) {
			value1 = values1[i];
			value2 = values2[i];
			ans = (byte) (value1 - value2);
			ccr.setCarryFlag(false);
			assertThat("random  byte testSubtractBytesWithCarry", ans, equalTo(au.subtractWithBorrow(value1, value2)));
		}// for
			// with carry
		for (int i = 0; i < testCount; i++) {
			value1 = values1[i];
			value2 = values2[i];
			ans = (byte) (value1 - value2 - 1);
			ccr.setCarryFlag(true);
			assertThat("random  byte testSubtractBytesWithCarry", ans, equalTo(au.subtractWithBorrow(value1, value2)));
		}// for
	}// testSubtractBytesWithCarry

	@Test
	public void testIncrementBytes() {

		ccr.clearAllCodes();
		byte byte00 = (byte) 0x00;
		byte byte01 = (byte) 0x01;
		byte byteFF = (byte) 0xFF;
		Byte cc;

		// 00
		cc = (byte) 0B00000010; // None set
		assertThat("testIncrementBytes 00 00", byte01, equalTo(au.increment(byte00)));
		assertThat("testIncrementBytes 00 00 cc", cc, equalTo(ccr.getConditionCode()));
		// FF
		cc = (byte) 0B01010110; // Sign & Parity
		assertThat("testIncrementBytes 00 FF", byte00, equalTo(au.increment(byteFF)));
		assertThat("testIncrementBytes 00 FF cc", cc, equalTo(ccr.getConditionCode()));

		// Random
		Random random = new Random();
		int testCount = 1000;
		byte[] values1 = new byte[testCount];
		byte value1, ans;
		random.nextBytes(values1);
		for (int i = 0; i < testCount; i++) {
			value1 = values1[i];
			ans = (byte) (value1 + 1);
			assertThat("random  byte testIncrementBytes", ans, equalTo(au.increment(value1)));
		}// for
	}// testIncrementBytes

	@Test
	public void testIncrementInts() {

		ccr.clearAllCodes();
		int int0000 = 0X0000;
		int intFFFF = 0XFFFF;

		byte cc = (byte) 0B11000010; // stuff
		ccr.setConditionCode(cc);
		assertThat("testIncrementInts 0000 0000", 0X0001, equalTo(au.increment(0X0000)));
		assertThat("testIncrementInts 0000 0000", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B00000111; // Stuff
		ccr.setConditionCode(cc);
		assertThat("testIncrementInts 0000 FFFF", int0000, equalTo(au.increment(intFFFF)));
		assertThat("testIncrementInts 0000 FFFF", cc, equalTo(ccr.getConditionCode()));

		Random random = new Random();
		int testCount = 1000;
		int valueA, ans;

		for (int i = 0; i < testCount; i++) {
			valueA = random.nextInt(0XFFFF);
			ans = valueA + 1;
			ans = ans & 0XFFFF;
			assertThat("Random word testIncrementInts", ans, equalTo(au.increment(valueA)));
		}// for

	}// testIncrementInts

	@Test
	public void testDecrementBytes() {

		ccr.clearAllCodes();
		byte byte00 = (byte) 0x00;
		byte byteFF = (byte) 0xFF;
		byte byteFE = (byte) 0xFE;
		Byte cc;
		// 00
		cc = (byte) 0B10000110; // None set
		assertThat("testDecrementBytes 00", byteFF, equalTo(au.decrement(byte00)));
		assertThat("testDecrementBytes 00 cc", cc, equalTo(ccr.getConditionCode()));

		// FF
		cc = (byte) 0B10010010; // Sign & Parity
		assertThat("testDecrementBytes 00 FF", byteFE, equalTo(au.decrement(byteFF)));
		assertThat("testDecrementBytes 00 FF cc", cc, equalTo(ccr.getConditionCode()));

		// Random
		Random random = new Random();
		int testCount = 1000;
		byte[] values1 = new byte[testCount];
		byte value1, ans;
		random.nextBytes(values1);

		for (int i = 0; i < testCount; i++) {
			value1 = values1[i];
			ans = (byte) (value1 - 1);
			assertThat("random  byte testDecrementBytes", ans, equalTo(au.decrement(value1)));
		}// for
	}// testDecrementBytes

	@Test
	public void testDecrementInts() {

		ccr.clearAllCodes();
		int intFFFF = 0XFFFF;
		int intFFFE = 0XFFFE;

		byte cc = (byte) 0B11000010; // stuff
		ccr.setConditionCode(cc);
		assertThat("testIncrementInts 0000 0000", intFFFF, equalTo(au.decrement(0X0000)));
		assertThat("testIncrementInts 0000 0000", cc, equalTo(ccr.getConditionCode()));

		cc = (byte) 0B00000111; // Stuff
		ccr.setConditionCode(cc);
		assertThat("testIncrementInts 0000 FFFF", intFFFE, equalTo(au.decrement(intFFFF)));
		assertThat("testIncrementInts 0000 FFFF", cc, equalTo(ccr.getConditionCode()));

		Random random = new Random();
		int testCount = 1000;
		int valueA, ans;

		for (int i = 0; i < testCount; i++) {
			valueA = random.nextInt(0XFFFF);
			ans = valueA - 1;
			ans = ans & 0XFFFF;
			assertThat("Random word testIncrementInts", ans, equalTo(au.decrement(valueA)));
		}// for

	}// testIncrementInts

	@Test
	public void testLogicalOperations() {

		byte byte00 = (byte) 0x00;
		byte byte05 = (byte) 0x05;
		byte byte0F = (byte) 0x0F;
		byte byte55 = (byte) 0x55;
		byte byteA0 = (byte) 0xA0;
		byte byteAA = (byte) 0xAA;
		byte byteA5 = (byte) 0xA5;
		byte byteAF = (byte) 0xAF;
		byte byteF0 = (byte) 0xF0;
		byte byteF5 = (byte) 0xF5;
		byte byteFF = (byte) 0xFF;

		assertThat("testLogicalOperations AND", byte00, equalTo(au.logicalAnd(byte55, byteAA)));
		assertThat("testLogicalOperations AND", byte05, equalTo(au.logicalAnd(byteA5, byte55)));
		assertThat("testLogicalOperations AND", byteA0, equalTo(au.logicalAnd(byteA5, byteAA)));

		assertThat("testLogicalOperations OR", byteFF, equalTo(au.logicalOr(byte55, byteAA)));
		assertThat("testLogicalOperations OR", byteAF, equalTo(au.logicalOr(byteA5, byteAA)));
		assertThat("testLogicalOperations OR", byteF5, equalTo(au.logicalOr(byteA5, byte55)));

		assertThat("testLogicalOperations XOR", byteFF, equalTo(au.logicalXor(byte55, byteAA)));
		assertThat("testLogicalOperations XOR", byte0F, equalTo(au.logicalXor(byteA5, byteAA)));
		assertThat("testLogicalOperations XOR", byteF0, equalTo(au.logicalXor(byteA5, byte55)));

	}// testLogicalOperations

	@Test
	public void testRotate() {
		byte byte00 = (byte) 0x00;
		byte byte01 = (byte) 0x01;
		byte byte55 = (byte) 0x55;
		byte byte80 = (byte) 0x80;
		byte byteAA = (byte) 0xAA;
		byte byteAB = (byte) 0xAB;
		byte byteD5 = (byte) 0xD5;
		byte byteFE = (byte) 0xFE;
		byte byteFF = (byte) 0xFF;
		
		// 00
		ccr.setCarryFlag(false);
		assertThat("testRotate Right", byte00, equalTo(au.rotateRight(byte00)));
		assertThat("testRotate Right", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate Right", byte00, equalTo(au.rotateRight(byte00)));
		assertThat("testRotate Right", false, equalTo(ccr.isCarryFlagSet()));
		// 01
		ccr.setCarryFlag(false);
		assertThat("testRotate Right", byte55, equalTo(au.rotateRight(byteAA)));
		assertThat("testRotate Right", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate Right", byteD5, equalTo(au.rotateRight(byteAB)));
		assertThat("testRotate Right", true, equalTo(ccr.isCarryFlagSet()));

		//
		ccr.setCarryFlag(false);
		assertThat("testRotate RightThruCarry", byte00, equalTo(au.rotateRightThruCarry(byte00)));
		assertThat("testRotate RightThruCarry", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate RightThruCarry", byte80, equalTo(au.rotateRightThruCarry(byte00)));
		assertThat("testRotate RightThruCarry", false, equalTo(ccr.isCarryFlagSet()));
		// 01
		ccr.setCarryFlag(false);
		assertThat("testRotate RightThruCarry", byte55, equalTo(au.rotateRightThruCarry(byteAA)));
		assertThat("testRotate RightThruCarry", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate RightThruCarry", byteD5, equalTo(au.rotateRightThruCarry(byteAB)));
		assertThat("testRotate RightThruCarry", true, equalTo(ccr.isCarryFlagSet()));
		// Left

		// 00
		ccr.setCarryFlag(false);
		assertThat("testRotate Left", byte00, equalTo(au.rotateLeft(byte00)));
		assertThat("testRotate Left", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate Left", byte00, equalTo(au.rotateLeft(byte00)));
		assertThat("testRotate Left", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate Left", byteAA, equalTo(au.rotateLeft(byte55)));
		assertThat("testRotate Left", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate Left", byteAB, equalTo(au.rotateLeft(byteD5)));
		assertThat("testRotate Left", true, equalTo(ccr.isCarryFlagSet()));

		//
		ccr.setCarryFlag(false);
		assertThat("testRotate LeftThruCarry", byte00, equalTo(au.rotateLeftThruCarry(byte00)));
		assertThat("testRotate LeftThruCarry", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate LeftThruCarry", byte01, equalTo(au.rotateLeftThruCarry(byte00)));
		assertThat("testRotate LeftThruCarry", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(false);
		assertThat("testRotate LeftThruCarry", byteFE, equalTo(au.rotateLeftThruCarry(byteFF)));
		assertThat("testRotate LeftThruCarry", true, equalTo(ccr.isCarryFlagSet()));

		ccr.setCarryFlag(true);
		assertThat("testRotate LeftThruCarry", byteFF, equalTo(au.rotateLeftThruCarry(byteFF)));
		assertThat("testRotate LeftThruCarry", true, equalTo(ccr.isCarryFlagSet()));

	}// testRotate

	@Test
	public void testComplement() {
		byte byte00 = (byte) 0x00;
		byte byte55 = (byte) 0x55;
		byte byteAA = (byte) 0xAA;
		byte byteFF = (byte) 0xFF;

		assertThat("testComplement", byte55, equalTo(au.complement(byteAA)));
		assertThat("testComplement", byteAA, equalTo(au.complement(byte55)));
		assertThat("testComplement", byteFF, equalTo(au.complement(byte00)));
		assertThat("testComplement", byte00, equalTo(au.complement(byteFF)));

	}// testComplement

	@Test
	public void testDAA() {
		byte byte07 = (byte) 0x07;
		byte byte27 = (byte) 0x27;
		byte byte2B = (byte) 0x2B;
		byte byte2D = (byte) 0x2D;
		byte byte31 = (byte) 0x31;
		byte byte87 = (byte) 0x87;
		byte byte8D = (byte) 0x8D;
		byte byteA7 = (byte) 0xA7;

		ccr.setAuxilaryCarryFlag(false);
		ccr.setCarryFlag(false);
		assertThat("testDAA", byte27, equalTo(au.decimalAdjustByte(byte27)));
		assertThat("TestDAA", false, equalTo(ccr.isAuxilaryCarryFlagSet()));
		assertThat("TestDAA", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setAuxilaryCarryFlag(true);
		ccr.setCarryFlag(false);
		assertThat("testDAA", byte2D, equalTo(au.decimalAdjustByte(byte27)));
		assertThat("TestDAA", false, equalTo(ccr.isAuxilaryCarryFlagSet()));
		assertThat("TestDAA", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setAuxilaryCarryFlag(false);
		ccr.setCarryFlag(true);
		assertThat("testDAA", byte87, equalTo(au.decimalAdjustByte(byte27)));
		assertThat("TestDAA", false, equalTo(ccr.isAuxilaryCarryFlagSet()));
		assertThat("TestDAA", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setAuxilaryCarryFlag(true);
		ccr.setCarryFlag(true);
		assertThat("testDAA", byte8D, equalTo(au.decimalAdjustByte(byte27)));
		assertThat("TestDAA", false, equalTo(ccr.isAuxilaryCarryFlagSet()));
		assertThat("TestDAA", false, equalTo(ccr.isCarryFlagSet()));

		//
		ccr.setAuxilaryCarryFlag(false);
		ccr.setCarryFlag(false);
		assertThat("testDAA", byte31, equalTo(au.decimalAdjustByte(byte2B)));
		assertThat("TestDAA", true, equalTo(ccr.isAuxilaryCarryFlagSet()));
		assertThat("TestDAA", false, equalTo(ccr.isCarryFlagSet()));

		ccr.setAuxilaryCarryFlag(false);
		ccr.setCarryFlag(false);
		assertThat("testDAA", byte07, equalTo(au.decimalAdjustByte(byteA7)));
		assertThat("TestDAA", false, equalTo(ccr.isAuxilaryCarryFlagSet()));
		assertThat("TestDAA", true, equalTo(ccr.isCarryFlagSet()));

	}// testDAA

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }

}
