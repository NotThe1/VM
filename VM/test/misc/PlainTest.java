package misc;

import hardware.ArithmeticUnit;
import hardware.CalculateCC;
import hardware.CentralProcessingUnit;
import hardware.ConditionCodeRegister;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class PlainTest {

	public static void main(String[] args) {
		test7();
//		test6();
//		test5();
//		test4();
//		 test3();
//		 test2();
//		 test1();
	}// main
	
public static void test7(){
	int ccMaskZeros = 0B1111111111010111;
	int ccMaskOnes = 0B0000000000000010;
	
	int base00 = 0;
	int base11 = 0XFFFF;
	
	int ans00 = base00 & ccMaskZeros;
	int ans01 = base00 | ccMaskOnes;
	
	int ans10 = base11 & ccMaskZeros;
	int ans11 = base11 | ccMaskOnes;
	

}//test7
	public static void test6(){
		Deque<Integer> myStack = new ArrayDeque<Integer>();
		for (int i = 0;i <20;i++){
			myStack.push(i);
		}//for
		while(!myStack.isEmpty()){
			System.out.printf("Value = %d%n", myStack.pop());
		}//while
	}//test6
	
	public static void test5(){
		ArithmeticUnit au = ArithmeticUnit.getArithmeticUnit();
		ConditionCodeRegister ccr = ConditionCodeRegister.getConditionCodeRegister();
		byte value1 = (byte) 0X1E;
		byte value2 = (byte) 0XA6;
		ccr.setCarryFlag(false);
		byte result = au.add(value1, value2);
		result = (byte) (value1 & value2);
		byte  a = result;
		boolean auxC = CalculateCC.isAuxCarry(value1, value2, false);
	}//test5

	public static void test4() {
		ArithmeticUnit au = ArithmeticUnit.getArithmeticUnit();
		ConditionCodeRegister ccr = ConditionCodeRegister.getConditionCodeRegister();
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
			System.out.printf("au says %s.%n",ccr.isAuxilaryCarryFlagSet() );
			msg = String.format("AuxCarry %s, Acc: %02X, Source: %02X, Result: %02X .%n",
					isFlagSet, valueAcc[i], valueSource[i],valueAns);
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
