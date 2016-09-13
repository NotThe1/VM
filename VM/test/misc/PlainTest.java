package misc;

import hardware.ArithmeticUnit;
import hardware.CalculateCC;
import hardware.CentralProcessingUnit;
import hardware.ConditionCodeRegister;

import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class PlainTest {

	public static void main(String[] args) {
		test9();
		// test8();
		// test7();
		// test6();
		// test5();
		// test4();
		// test3();
		// test2();
		// test1();
	}// main

	public static void test9() {
		final int buffSize = 16;
		byte[] original = new byte[] { (byte) 0X00, (byte) 0X01, (byte) 0X02, (byte) 0X03,
				(byte) 0X04, (byte) 0X05, (byte) 0X06, (byte) 0X07,
				(byte) 0X08, (byte) 0X09, (byte) 0X0A, (byte) 0X0B,
				(byte) 0X0C, (byte) 0X0D, (byte) 0X0E, (byte) 0X0F,
				(byte) 0X10, (byte) 0X11, (byte) 0X12, (byte) 0X13,
				(byte) 0X14, (byte) 0X15, (byte) 0X16, (byte) 0X17,
				(byte) 0X18, (byte) 0X19, (byte) 0X1A, (byte) 0X1B,
				(byte) 0X1C, (byte) 0X1D, (byte) 0X1E, (byte) 0X1F,
				(byte) 0X20, (byte) 0X21, (byte) 0X22, (byte) 0X23 };
		byte[] myBuff = new byte[buffSize];
		int srcPos = 0;
		int bytesToRead = buffSize;
		int originalNumberOfBytes = original.length;
		int bytesRemaining = originalNumberOfBytes;
		while (bytesToRead == 16) {
			bytesToRead = (bytesRemaining >= bytesToRead) ? buffSize : bytesRemaining;
			System.arraycopy(original, srcPos, myBuff, 0, bytesToRead);

			for (int i = 0; i < bytesToRead; i++) {
				System.out.printf("i: %d, myBuff: %02X%n", i, myBuff[i]);
			}// for
			srcPos += bytesToRead;
			bytesRemaining = originalNumberOfBytes- srcPos;

		}// while
		System.out.printf("%nbytesToRead: % d%n", bytesToRead);

	}

	public static void test8() {
		showValue(1000);
		showValue(2048);
		showValue(1048576);
		showValue(10485760);
		showValue(60485760 - 6048576);
	}// test8

	public static void showValue(long value) {
		String strFileSize = null;
		if (value < 1024) {
			strFileSize = String.format("%,d Bytes", value);
		} else if (value < 1048576) {
			strFileSize = String.format("%,.2f KB", (float) (value / 1024));
		} else {
			strFileSize = String.format("%,f MB", (float) (value / 1048576));
		}// if

		System.out.printf("%d:  %s%n", value, strFileSize);

	}// showValue

	public static void test7() {
		int ccMaskZeros = 0B1111111111010111;
		int ccMaskOnes = 0B0000000000000010;

		int base00 = 0;
		int base11 = 0XFFFF;

		int ans00 = base00 & ccMaskZeros;
		int ans01 = base00 | ccMaskOnes;

		int ans10 = base11 & ccMaskZeros;
		int ans11 = base11 | ccMaskOnes;

	}// test7

	public static void test6() {
		Deque<Integer> myStack = new ArrayDeque<Integer>();
		for (int i = 0; i < 20; i++) {
			myStack.push(i);
		}// for
		while (!myStack.isEmpty()) {
			System.out.printf("Value = %d%n", myStack.pop());
		}// while
	}// test6

	public static void test5() {
		ArithmeticUnit au = ArithmeticUnit.getInstance();
		ConditionCodeRegister ccr = ConditionCodeRegister.getInstance();
		byte value1 = (byte) 0X1E;
		byte value2 = (byte) 0XA6;
		ccr.setCarryFlag(false);
		byte result = au.add(value1, value2);
		result = (byte) (value1 & value2);
		byte a = result;
		boolean auxC = CalculateCC.isAuxCarry(value1, value2, false);
	}// test5

	public static void test4() {
		ArithmeticUnit au = ArithmeticUnit.getInstance();
		ConditionCodeRegister ccr = ConditionCodeRegister.getInstance();
		boolean isFlagSet;
		boolean carryIn = false;
		String msg;
		byte valueAns;
		byte[] valueAcc = new byte[] { (byte) 0XF8, (byte) 0XF8, (byte) 0X10, (byte) 0X10, (byte) 0X00, (byte) 0X00 };
		byte[] valueSource = new byte[] { (byte) 0X60, (byte) 0X61, (byte) 0X20, (byte) 0X21, (byte) 0X00, (byte) 0X01 };
		// byte[] valueAns = new byte[] { (byte) 0XFF, (byte) 0XFF, (byte) 0XFF, (byte) 0XFF, (byte) 0XFF, (byte) 0XFF
		// };

		for (int i = 0; i < valueAcc.length; i++) {
			isFlagSet = CalculateCC.isAuxCarrySub(valueAcc[i], valueSource[i], carryIn);
			valueAns = au.subtract(valueAcc[i], valueSource[i]);
			System.out.printf("au says %s.%n", ccr.isAuxilaryCarryFlagSet());
			msg = String.format("AuxCarry %s, Acc: %02X, Source: %02X, Result: %02X .%n",
					isFlagSet, valueAcc[i], valueSource[i], valueAns);
			System.out.println(msg);
		}// for i

	}// test4

	private static void test3() {

		int count = 3;
		byte[] values = new byte[8 * count];
		Random random = new Random();
		random.nextBytes(values);

		byte[] mem = new byte[values.length * 2];
		for (int i = 0; i < values.length - 1;) {
			for (byte opCode = 0X06; opCode <= 0X3E; opCode += 0X08) {
				System.out.printf("i = %d %n", i);
				mem[2 * i] = opCode;
				mem[(2 * i) + 1] = values[i];
				System.out.printf("%2X %2X%n", opCode, values[i]);
				i++;
			}// for opCode
		}// for i

	}// /test 3

	private static void test2() {
		for (int i = 0; i <= 0XFF; i++) {
			byte opCode = (byte) i;

			System.out.printf("opCode : %02X( %8s ) , Index45 = %d,, Index012 = %d, Index345 = %d%n",
					i, Integer.toBinaryString(i), getIndex45(opCode), getIndex012(opCode), getIndex345(opCode));
			if ((i % 16) == 0) {
				System.out.println();
			}
		}// for
	}// test2

	private static int getIndex45(byte opCode) {
		return (opCode & 0B00110000) >> 4;
	}// getIndex45

	private static int getIndex345(byte opCode) {
		return (opCode & 0B00111000) >> 3;
	}// getIndex345

	private static int getIndex012(byte opCode) {
		return (opCode & 0B00000111);
	}// getIndex012
		// -------------------------------

	private static void test1() {
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			System.out.printf(" i = %d, Value = %02X%n", i, random.nextInt(0XFFFF));
		}// for
	}// test1

}
