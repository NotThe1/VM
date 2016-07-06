package hardware;

import java.util.Random;

/**
 * support class to help test the setting of Condition Codes
 * 
 * @author Frank Martyn
 * @version 1.0 *
 */

public class CalculateCC {
	private static Random random = new Random();

	public static boolean isCarry(byte value1, byte value2, boolean carryIn) {
		byte carry = carryIn ? (byte) 0X01 : (byte) 0X00;
		return ((carry & 0X01) + (value1 & 0XFF) + (value2 & 0XFF)) >= 256;
	}// isCarry

	public static boolean isCarryWord(int value1, int value2) {
		return ((value1 & 0XFFFF) + (value2 & 0XFFFF)) >= 0X10000;
	}// isCarry

	public static boolean isCarrySub(byte value1, byte value2, boolean carryIn) {
		byte carry = carryIn ? (byte) 0X01 : (byte) 0X00;
		byte valueComplement = (byte) ~(value2 + carry);
		return !(( (value1 & 0XFF) + (valueComplement & 0XFF) + 1) >= 256);
	}// isCarrySub


	public static boolean isAuxCarry(byte value1, byte value2, boolean carryIn) {
		byte carry = carryIn ? (byte) 0X01 : (byte) 0X00;
		return ((carry & 0X01) + (value1 & 0X0F) + (value2 & 0X0F)) >= 16;
	}// isAuxCarryGenerated

	public static boolean isAuxCarrySub(byte value1, byte value2, boolean carryIn) {
		byte carry = carryIn ? (byte) 0X01 : (byte) 0X00;
		byte valueComplement = (byte) ((~(value2 + carry) & 0X0F) + 1);
		int sum = ((value1 & 0X0F) + (valueComplement & 0X1F));
		return (sum >= 16); // ***
	}// isAuxCarryGenerated

	public static boolean isSign(byte value1) {
		return (value1 & 0B10000000) == 0B10000000;
	}// isAuxCarryGenerated

	public static boolean isParity(byte value1) {
		return (Integer.bitCount(value1) % 2) == 0;
	}// isAuxCarryGenerated

	public static boolean isZero(byte value1) {
		return value1 == 0;
	}// isAuxCarryGenerated

	public static byte nextRandomByte() {
		return (byte) random.nextInt(0X100);
	}// nextRandomByte

}// class CalculateCC
