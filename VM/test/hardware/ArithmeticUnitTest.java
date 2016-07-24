package hardware;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class ArithmeticUnitTest {
	IConditionCodeRegister ccr;
	ArithmeticUnit au;
	Random random;
	byte valueB1, valueB2, valueBResult;
	int valueI1, valueI2, valueIResult;
	boolean isTestSign, isTestZero, isTestAuxCarry, isTestParity, isTestCarry;
	int testCount;
	String msg;

	@Before
	public void setUp() throws Exception {
		au = ArithmeticUnit.getInstance();
		ccr = ConditionCodeRegister.getInstance();

		random = new Random();
		testCount = 2000;
	}// setUp

	@Test
	public void testAddBytes() {
//		testCount = 20;
		
		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueB2 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 + valueB2);
			msg = String.format("%4d, ADD - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueB1, valueB2, isTestCarry, valueBResult);

			assertThat(msg, valueBResult, equalTo(au.add(valueB1, valueB2)));

			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));
			assertThat(msg, CalculateCC.isAuxCarry(valueB1, valueB2, false), equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, CalculateCC.isCarry(valueB1, valueB2, false), equalTo(ccr.isCarryFlagSet()));
		}// for testCount

	}// testAddBytes

	@Test
	public void testAddInts() {
//		testCount = 20;
		
		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueI1 = random.nextInt(0X10000);
			valueI2 = random.nextInt(0X10000);
			valueIResult = (valueI1 + valueI2) & 0XFFFF;
			msg = String.format("%4d, ADD - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueI1, valueI2, isTestCarry, valueIResult);

			assertThat(msg, valueIResult, equalTo(au.add(valueI1, valueI2)));
			assertThat(msg, CalculateCC.isCarryWord(valueI1, valueI2), equalTo(ccr.isCarryFlagSet()));
			// are not affected
			assertThat(msg, isTestSign, equalTo(ccr.isSignFlagSet()));
			assertThat(msg, isTestZero, equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, isTestParity, equalTo(ccr.isParityFlagSet()));
			assertThat(msg, isTestAuxCarry, equalTo(ccr.isAuxilaryCarryFlagSet()));

		}// for testCount

	}// testAddInts

	@Test
	public void testAddWithCarry() {
//		testCount = 20;
		
		byte carryIn;

		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);
			carryIn = isTestCarry ? (byte) 0X01 : (byte) 0X00;

			valueB1 = (byte) random.nextInt(0X100);
			valueB2 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 + valueB2 + carryIn);
			msg = String.format("%4d, ADD - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueB1, valueB2, isTestCarry, valueBResult);

			assertThat(msg, valueBResult, equalTo(au.addWithCarry(valueB1, valueB2)));

			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));
			assertThat(msg, CalculateCC.isAuxCarry(valueB1, valueB2, isTestCarry),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, CalculateCC.isCarry(valueB1, valueB2, isTestCarry), equalTo(ccr.isCarryFlagSet()));
		}// for testCount

	}// testAddWithCarry

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Test
	public void testSubtractBytes() {
//		testCount = 20;
		
		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueB2 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 - valueB2);
			msg = String.format("%4d, ADD - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueB1, valueB2, isTestCarry, valueBResult);

			assertThat(msg, valueBResult, equalTo(au.subtract(valueB1, valueB2)));

			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));
			assertThat(msg, CalculateCC.isAuxCarrySub(valueB1, valueB2, false), equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, CalculateCC.isCarrySub(valueB1, valueB2, false), equalTo(ccr.isCarryFlagSet()));
		}// for testCount

	}// testSubtractBytes

	@Test
	public void testSubtractInts() {
//		testCount = 20;
		
		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueI1 = random.nextInt(0X10000);
			valueI2 = random.nextInt(0X10000);
			valueIResult = (valueI1 - valueI2) & 0XFFFF;
			msg = String.format("%4d, ADD - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueI1, valueI2, isTestCarry, valueIResult);

			assertThat(msg, valueIResult, equalTo(au.subtract(valueI1, valueI2)));
			// not set-reset
			assertThat(msg, isTestCarry, equalTo(ccr.isCarryFlagSet()));
			assertThat(msg, isTestSign, equalTo(ccr.isSignFlagSet()));
			assertThat(msg, isTestZero, equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, isTestParity, equalTo(ccr.isParityFlagSet()));
			assertThat(msg, isTestAuxCarry, equalTo(ccr.isAuxilaryCarryFlagSet()));

		}// for testCount

	}// testSubtractInts

	@Test
	public void testSubtractBytesWithCarry() {
//		testCount = 20;

		byte carryIn;
		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			carryIn = isTestCarry ? (byte) 0X01 : (byte) 0X00;
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueB2 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 - (valueB2 + carryIn));
			msg = String.format("%4d, SBB - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueB1, valueB2, isTestCarry, valueBResult);

			assertThat(msg, valueBResult, equalTo(au.subtractWithBorrow(valueB1, valueB2)));

			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));
			assertThat(msg, CalculateCC.isAuxCarrySub(valueB1, valueB2, isTestCarry),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, CalculateCC.isCarrySub(valueB1, valueB2, isTestCarry), equalTo(ccr.isCarryFlagSet()));
		}// for testCount

	}// testSubtractBytesWithCarry

	@Test
	public void testIncrementBytes() {
//		testCount = 20;
		
		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 + 1);
			msg = String.format("%4d, INR - value1: %02X,  CarryIn: %s, Result: %02X%n",
					i, valueB1, isTestCarry, valueBResult);
			if (isTestCarry) {
				System.out.println(msg);
			}
			assertThat(msg, valueBResult, equalTo(au.increment(valueB1)));
			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isAuxCarry(valueB1, (byte) 1, false), equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));

			assertThat(msg, isTestCarry, equalTo(ccr.isCarryFlagSet())); // Carry flag not set/rest
		}// for testCount

	}// testIncrementBytes

	@Test
	public void testIncrementInts() { // INX
//		testCount = 20;
		
		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueI1 = random.nextInt(0X100);
			valueIResult = (valueI1 + 1);
			msg = String.format("%4d, INX - value1: %02X,  Result: %02X%n", i, valueI1, valueIResult);
			assertThat(msg, valueIResult, equalTo(au.increment(valueI1)));
			assertThat(msg, isTestSign, equalTo(ccr.isSignFlagSet()));
			assertThat(msg, isTestZero, equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, isTestAuxCarry, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, isTestParity, equalTo(ccr.isParityFlagSet()));
			assertThat(msg, isTestCarry, equalTo(ccr.isCarryFlagSet()));
		}// for testCount

	}// testIncrementInts INX

	@Test
	public void testDecrementBytes() {
//		testCount = 20;

		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 - 1);
			msg = String.format("%4d, DCR - value1: %02X,  CarryIn: %s, Result: %02X%n",
					i, valueB1, isTestCarry, valueBResult);
			if (isTestCarry) {
				System.out.println(msg);
			}
			assertThat(msg, valueBResult, equalTo(au.decrement(valueB1)));
			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isAuxCarrySub(valueB1, (byte) 1, false), equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));

			assertThat(msg, isTestCarry, equalTo(ccr.isCarryFlagSet())); // Carry flag not set/rest
		}// for testCount

	}// testDecrementBytes

	@Test
	public void testDecrementInts() {
//		testCount = 20;

		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueI1 = random.nextInt(0X100);
			valueIResult = (valueI1 - 1) & 0XFFFF;
			msg = String.format("%4d, DCX - value1: %02X,  Result: %02X%n", i, valueI1, valueIResult);
			assertThat(msg, valueIResult, equalTo(au.decrement(valueI1)));
			assertThat(msg, isTestSign, equalTo(ccr.isSignFlagSet()));
			assertThat(msg, isTestZero, equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, isTestAuxCarry, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, isTestParity, equalTo(ccr.isParityFlagSet()));
			assertThat(msg, isTestCarry, equalTo(ccr.isCarryFlagSet()));
		}// for testCount

	}// testIncrementInts
	
	@Test
	public void testAND(){
//		testCount = 20;

		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueB2 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 & valueB2);
			msg = String.format("%4d, AND - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueB1, valueB2, isTestCarry, valueBResult);

			assertThat(msg, valueBResult, equalTo(au.logicalAnd(valueB1, valueB2)));

			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));
			//set to Zero
			assertThat(msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, false, equalTo(ccr.isCarryFlagSet()));
		}// for testCount
	
	}//testAND
	public void testOR(){
//		testCount = 20;

		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			// isTestCarry = false;
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueB2 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 | valueB2);
			msg = String.format("%4d, OR - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueB1, valueB2, isTestCarry, valueBResult);

			assertThat(msg, valueBResult, equalTo(au.logicalOr(valueB1, valueB2)));

			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));
			//set to Zero
			assertThat(msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, false, equalTo(ccr.isCarryFlagSet()));
		}// for testCount
	
	}//testOR

	public void testXOR(){
//		testCount = 20;

		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			// isTestCarry = false;
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueB2 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (valueB1 ^ valueB2);
			msg = String.format("%4d, XOR - value1: %02X, value2: %02X, CarryIn %s,  Result: %02X%n",
					i, valueB1, valueB2, isTestCarry, valueBResult);

			assertThat(msg, valueBResult, equalTo(au.logicalOr(valueB1, valueB2)));

			assertThat(msg, CalculateCC.isSign(valueBResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueBResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueBResult), equalTo(ccr.isParityFlagSet()));
			//set to Zero
			assertThat(msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, false, equalTo(ccr.isCarryFlagSet()));
		}// for testCount
	
	}//testXOR


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
//		testCount = 20;

		for (int i = 0; i < testCount; i++) {
			isTestSign = random.nextBoolean();
			ccr.setSignFlag(isTestSign);
			isTestZero = random.nextBoolean();
			ccr.setZeroFlag(isTestZero);
			isTestAuxCarry = random.nextBoolean();
			ccr.setAuxilaryCarryFlag(isTestAuxCarry);
			isTestParity = random.nextBoolean();
			ccr.setParityFlag(isTestParity);
			isTestCarry = random.nextBoolean();
			ccr.setCarryFlag(isTestCarry);

			valueB1 = (byte) random.nextInt(0X100);
			valueBResult = (byte) (~ valueB1 & 0XFF);
			msg = String.format("%4d, Complement - value1: %02X,  CarryIn %s,  Result: %02X%n",
					i, valueB1,  isTestCarry, valueBResult);

			assertThat(msg, valueBResult, equalTo(au.complement(valueB1)));

			assertThat(msg, isTestSign, equalTo(ccr.isSignFlagSet()));
			assertThat(msg, isTestZero, equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, isTestParity, equalTo(ccr.isParityFlagSet()));
			assertThat(msg, isTestAuxCarry, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, isTestCarry, equalTo(ccr.isCarryFlagSet()));
		}// for testCount

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



}//classArithmeticUnitTest
