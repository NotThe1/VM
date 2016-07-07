package hardware;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import memory.CpuBuss;
import memory.IoBuss;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class CentralProcessingUnitPage11Test {
	CentralProcessingUnit cpu;
	CpuBuss cpuBuss;
	IoBuss ioBuss;
	WorkingRegisterSet wrs;
	ConditionCodeRegister ccr;
	Random random;
	Register[] registersByte;
	Register[] registersWord;
	Register registerByte, registerWord;
	int testCount;
	boolean isTestSign, isTestZero, isTestAuxCarry, isTestParity, isTestCarry;

	@Before
	public void setUp() throws Exception {
		cpu = new CentralProcessingUnit();
		cpuBuss = CpuBuss.getCpuBuss();
		wrs = WorkingRegisterSet.getWorkingRegisterSet();
		ioBuss = IoBuss.getIoBuss();
		ccr = ConditionCodeRegister.getConditionCodeRegister();

		random = new Random();
		registersByte = new Register[] { Register.B, Register.C, Register.D, Register.E,
				Register.H, Register.L, Register.M, Register.A };
		registersWord = new Register[] { Register.BC, Register.DE, Register.HL, Register.AF };
		testCount = 2000;
	}// setUp

	@Test
	public void testPushPop() {
		testCount = 15;
		byte pushCode, popCode;
		byte pushBase = (byte) 0XC5;
		byte popBase = (byte) 0XC1;
		Deque<Integer> valueStack = new ArrayDeque<Integer>();

		int regIndex;
		int pc = 0X0100;
		int value, valueResult;
		int stackStart = 0X8000;
		int stackcurrent = stackStart;
		wrs.setStackPointer(stackcurrent);
		// cc = S|Z|0|A|)|P|1|C
		int ccMaskZeros = 0B1111111111010111;
		int ccMaskOnes = 0B0000000000000010;
		for (int i = 0; i < testCount; i++) {
			regIndex = random.nextInt(4);
			registerWord = registersWord[regIndex];
			clearWordRegisters();
			value = random.nextInt(0X10000);
			if (registerWord.equals(Register.AF)) {
				byte hiByte = (byte) ((value >> 8) & 0X00FF);
				byte loByte = (byte) (value & 0X00FF);
				wrs.setReg(Register.A, hiByte);
				ccr.setConditionCode(loByte);
				// adjust for Zeros and Ones in CC
				value &= ccMaskZeros;
				value |= ccMaskOnes;
			} else {
				wrs.setDoubleReg(registerWord, value);
			}// if Register A
			valueStack.push(value);

			pushCode = (byte) (pushBase + (regIndex * 0X10));
			cpuBuss.write(pc, pushCode);
			cpu.executeInstruction(pc);

		}// for push
		int ii = valueStack.size() + 1;
		while (!valueStack.isEmpty()) {
			regIndex = random.nextInt(4);
			registerWord = registersWord[regIndex];
			if (registerWord.equals(Register.AF)) {
				continue;
			}// if Register A

			clearWordRegisters();
			value = valueStack.pop();

			popCode = (byte) (popBase + (regIndex * 0X10));
			cpuBuss.write(pc, popCode);
			cpu.executeInstruction(pc);
			if (registerWord.equals(Register.AF)) {
				valueResult = wrs.getAcc() * 0X100;
				valueResult += ccr.getConditionCode();
				// adjust for Zeros and Ones in CC
				value &= ccMaskZeros;
				value |= ccMaskOnes;
			} else {
				valueResult = wrs.getDoubleReg(registerWord);
			}// if
				// System.out.printf("ii: %02d, value %d,register: %s,  register value: %d%n",
			// ii, value,registerWord, wrs.getDoubleReg(registerWord));
			assertThat(ii + " Push/Pop", value, equalTo(valueResult));
			ii--;
		}// for pop

	}// testPushPop

	private void clearWordRegisters() {
		for (int i = 0; i < registersWord.length; i++) {
			wrs.setDoubleReg(registersWord[i], 0);
		}// for
	}// clearWordRegisters

	@Test
	public void testReturn() {
		byte retOpCode = (byte) 0XC9; // RET
		int pc1 = 0XAA55;
		int pc = 0X0100;
		int spStart = 0X2000;

		cpuBuss.pushWord(spStart, pc1); // push pc1 into
		wrs.setStackPointer(spStart - 2);

		cpuBuss.write(pc, retOpCode); // put the opcode in
		wrs.setProgramCounter(pc);

		cpu.executeInstruction(wrs.getProgramCounter()); // execute the return
		assertThat("RET ", pc1, equalTo(wrs.getProgramCounter()));

	}// testReturn

	@Test
	public void testConditionalReturns() {
		// int pc1 = 0XAA55;
		// int spStart = 0X2000;
		// RET - NZ/NC/PO/P...Z/C/PE/M
		byte[] opCodes = new byte[] { (byte) 0XC0, (byte) 0XD0, (byte) 0XE0, (byte) 0XF0,
				(byte) 0XC8, (byte) 0XD8, (byte) 0XE8, (byte) 0XF8 };
		byte retOpCode;
		int pc = 0X0100;
		ioBuss.writeDMA(pc, opCodes);
		// no returns
		wrs.setProgramCounter(pc);
		ccr.setZeroFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // RET NZ
		ccr.setCarryFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // RET NC
		ccr.setParityFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // RET PO
		ccr.setSignFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // RET P

		ccr.setZeroFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // RET Z
		ccr.setCarryFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // RET C
		ccr.setParityFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // RET PE
		ccr.setSignFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // RET M

		assertThat("testConditionalReturns", pc + opCodes.length, equalTo(wrs.getProgramCounter()));

		// All returns
		int pc1 = 0XAA55;
		pc = 0X0100;
		int spStart = 0X2000;
		cpuBuss.pushWord(spStart, pc1); // push pc1 into stack

		wrs.setStackPointer(spStart - 2);
		ccr.setZeroFlag(false);
		cpu.executeInstruction(pc); // RET NZ
		assertThat("RNZ ", pc1, equalTo(wrs.getProgramCounter()));

		wrs.setStackPointer(spStart - 2);
		ccr.setCarryFlag(false);
		cpu.executeInstruction(pc + 1); // RET NC
		assertThat("RNC ", pc1, equalTo(wrs.getProgramCounter()));

		wrs.setStackPointer(spStart - 2);
		ccr.setParityFlag(false);
		cpu.executeInstruction(pc + 2); // RET PO
		assertThat("RPO ", pc1, equalTo(wrs.getProgramCounter()));

		wrs.setStackPointer(spStart - 2);
		ccr.setSignFlag(false);
		cpu.executeInstruction(pc + 3); // RET P
		assertThat("RP ", pc1, equalTo(wrs.getProgramCounter()));

		wrs.setStackPointer(spStart - 2);
		ccr.setZeroFlag(true);
		cpu.executeInstruction(pc + 4); // RET Z
		assertThat("RZ ", pc1, equalTo(wrs.getProgramCounter()));

		wrs.setStackPointer(spStart - 2);
		ccr.setCarryFlag(true);
		cpu.executeInstruction(pc + 5); // RET C
		assertThat("RC ", pc1, equalTo(wrs.getProgramCounter()));

		wrs.setStackPointer(spStart - 2);
		ccr.setParityFlag(true);
		cpu.executeInstruction(pc + 6); // RET PE
		assertThat("RPE ", pc1, equalTo(wrs.getProgramCounter()));

		wrs.setStackPointer(spStart - 2);
		ccr.setSignFlag(true);
		cpu.executeInstruction(pc + 7); // RET M
		assertThat("RM ", pc1, equalTo(wrs.getProgramCounter()));

	}// testConditionalReturns

	@Test
	public void testCall() {
		// CALL (NZ/NC/PO/P) 0X55AA...CALL (Z/C/PE/M) 0X55AA
		byte[] mem = new byte[] { (byte) 0XCD, (byte) 0XAA, (byte) 0X55 };
		int pc = 0X0100;
		ioBuss.writeDMA(pc, mem); // put the code in memory
		// RET RET RET
		byte[] mem1 = new byte[] { (byte) 0XC9, (byte) 0XC9, (byte) 0XC9 };
		int pc1 = 0X55AA;
		ioBuss.writeDMA(pc1, mem1); // put the code in memory

		wrs.setProgramCounter(pc);
		// make the Call & return
		ccr.setZeroFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // execute the call
		assertThat("CALL ", pc1, equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter()); // execute the Return
		assertThat("CALL - RET ", pc + 3, equalTo(wrs.getProgramCounter()));

	}// testReturn

	@Test
	public void testConditionalCalls() {
		// CALL (NZ/NC/PO/P) 0X55AA...CALL (Z/C/PE/M) 0X55AA
		byte[] mem = new byte[] { (byte) 0XC4, (byte) 0XAA, (byte) 0X55,
				(byte) 0XD4, (byte) 0XAA, (byte) 0X55,
				(byte) 0XE4, (byte) 0XAA, (byte) 0X55,
				(byte) 0XF4, (byte) 0XAA, (byte) 0X55,
				(byte) 0XCC, (byte) 0XAA, (byte) 0X55,
				(byte) 0XDC, (byte) 0XAA, (byte) 0X55,
				(byte) 0XEC, (byte) 0XAA, (byte) 0X55,
				(byte) 0XF4, (byte) 0XAA, (byte) 0X55 };
		int pc = 0X0100;
		ioBuss.writeDMA(pc, mem); // put the code in memory
		// RET RET RET
		byte[] mem1 = new byte[] { (byte) 0XC9, (byte) 0XC9, (byte) 0XC9 };
		int pc1 = 0X55AA;
		ioBuss.writeDMA(pc1, mem1); // put the code in memory

		wrs.setProgramCounter(pc);
		// do not make the calls

		ccr.setZeroFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // CNZ
		ccr.setCarryFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // CNC
		ccr.setParityFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // CPO
		ccr.setSignFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // CP

		ccr.setZeroFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // CZ
		ccr.setCarryFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // CC
		ccr.setParityFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // CPE
		ccr.setSignFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // CM

		assertThat("testConditionalCalls", pc + (mem.length/3), equalTo(wrs.getProgramCounter()));

		
		wrs.setProgramCounter(pc);
		// make the calls
		ccr.setZeroFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // CNZ
		assertThat("CNZ ",pc1,equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter());	//RET
		assertThat("CNS - RET",pc + 3,equalTo(wrs.getProgramCounter()));
		
		ccr.setCarryFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // CNC
		assertThat("CNC ",pc1,equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter());	//RET
		assertThat("CNC - RET",pc + 6,equalTo(wrs.getProgramCounter()));

		ccr.setParityFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // CPO
		assertThat("CPO ",pc1,equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter());	//RET
		assertThat("CPO - RET",pc + 6,equalTo(wrs.getProgramCounter()));

		
		ccr.setSignFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // CP
		assertThat("CP ",pc1,equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter());	//RET
		assertThat("CP - RET",pc + 6,equalTo(wrs.getProgramCounter()));

		ccr.setZeroFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // CZ
		assertThat("CZ ",pc1,equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter());	//RET
		assertThat("CZ - RET",pc + 6,equalTo(wrs.getProgramCounter()));

		ccr.setCarryFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // CC
		assertThat("CC ",pc1,equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter());	//RET
		assertThat("CC - RET",pc + 6,equalTo(wrs.getProgramCounter()));

		ccr.setParityFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // CPE
		assertThat("CPE ",pc1,equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter());	//RET
		assertThat("CPE - RET",pc + 6,equalTo(wrs.getProgramCounter()));

		ccr.setSignFlag(true);
		cpu.executeInstruction(wrs.getProgramCounter()); // CM
		assertThat("CM ",pc1,equalTo(wrs.getProgramCounter()));
		cpu.executeInstruction(wrs.getProgramCounter());	//RET
		assertThat("CM - RET",pc + 6,equalTo(wrs.getProgramCounter()));





	}// testConditionalCalls

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }//

}// class CentralProcessingUnitPage11Test
