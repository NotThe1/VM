package hardware;

import static org.junit.Assert.*;

import java.util.Random;

import memory.CpuBuss;
import memory.IoBuss;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class CentralProcessingUnitPage11ATest {
	CentralProcessingUnit cpu;
	CpuBuss cpuBuss;
	IoBuss ioBuss;
	WorkingRegisterSet wrs;
	ConditionCodeRegister ccr;
	Random random;
	// Register[] registers;
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
		// registers = new Register[] { Register.B, Register.C, Register.D, Register.E,
		// Register.H, Register.L, Register.M, Register.A };
		testCount = 2000;
	}// setUp

	@Test
	public void testyyy000ADI() {
		// int testCount = 200;

		byte opCode, valueAcc, valueSource, valueResult;
		int pc = 0X0100;
		String msg;

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

			opCode = (byte) 0XC6;
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);
			cpuBuss.write(pc + 1, valueSource);

			valueResult = (byte) (valueAcc + valueSource);
			wrs.setProgramCounter(pc);
			cpu.executeInstruction(wrs.getProgramCounter());
			msg = String.format("%n ADI - OpCode: %2X, Acc: %02X, Source: %02X,  Result: %02X",
					opCode, valueAcc, valueSource, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, CalculateCC.isAuxCarry(valueAcc, valueSource, false),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, CalculateCC.isCarry(valueAcc, valueSource, false), equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy000ADI

	@Test
	public void testyyy001ACI() {
		// testCount = 20;

		byte valueAcc, valueSource, valueResult, valueCarry;
		byte opCode = (byte) 0XCE; // ACI
		int pc = 0X0100;
		String msg;

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
			valueCarry = isTestCarry ? (byte) 0X01 : (byte) 0X00;

			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);
			cpuBuss.write(pc + 1, valueSource);

			valueResult = (byte) (valueAcc + valueSource + valueCarry);

			wrs.setProgramCounter(pc);
			cpu.executeInstruction(wrs.getProgramCounter());

			msg = String.format("%n ACI - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, CalculateCC.isAuxCarry(valueAcc, valueSource, isTestCarry),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, CalculateCC.isCarry(valueAcc, valueSource, isTestCarry), equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy001ACI

	@Test
	public void testyyy010SUI() {
		// testCount = 20;

		byte valueAcc, valueSource, valueResult ;
		byte opCode = (byte) 0XD6; // SUI
		int pc = 0X0100;
		String msg;

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

			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);
			cpuBuss.write(pc + 1, valueSource);

			valueResult = (byte) (valueAcc - valueSource);

			wrs.setProgramCounter(pc);
			cpu.executeInstruction(wrs.getProgramCounter());
			msg = String.format(" %n SUI - OpCode: %2X, Acc: %02X, Source: %02X,  Result: %02X",
					opCode, valueAcc, valueSource, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, CalculateCC.isAuxCarrySub(valueAcc, valueSource, false),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, CalculateCC.isCarrySub(valueAcc, valueSource, false), equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy010SUI

	@Test
	public void testyyy011SBI() {
		// testCount = 20;

		byte valueAcc, valueSource, valueResult, valueCarry;
		byte opCode = (byte) 0XDE; // SBI
		int pc = 0X0100;
		String msg;

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
			valueCarry = isTestCarry ? (byte) 0X01 : (byte) 0X00;

			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);
			cpuBuss.write(pc + 1, valueSource);

			valueResult = (byte) (valueAcc - (valueSource + valueCarry));
			wrs.setProgramCounter(pc);
			cpu.executeInstruction(wrs.getProgramCounter());

			msg = String.format("%n SBI - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, CalculateCC.isAuxCarrySub(valueAcc, valueSource, isTestCarry),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, CalculateCC.isCarrySub(valueAcc, valueSource, isTestCarry),
					equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy011SBI

	@Test
	public void testyyy100ANI() {
		// testCount = 20;

		byte valueAcc, valueSource, valueResult;
		byte opCode = (byte) 0XE6; // ANI
		int pc = 0X0100;
		String msg;

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

			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);
			cpuBuss.write(pc + 1, valueSource);

			valueResult = (byte) ((valueAcc & valueSource) & 0XFF);

			wrs.setProgramCounter(pc);
			cpu.executeInstruction(wrs.getProgramCounter());

			msg = String.format("%n ANI - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy100ANI

	@Test
	public void testyyy101XRI() {
		// testCount = 20;

		byte valueAcc, valueSource, valueResult;
		byte opCode = (byte) 0XEE; // XRI
		int pc = 0X0100;
		String msg;

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

			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);
			cpuBuss.write(pc + 1, valueSource);

			valueResult = (byte) ((valueAcc ^ valueSource) & 0XFF);

			wrs.setProgramCounter(pc);
			cpu.executeInstruction(wrs.getProgramCounter());

			msg = String.format("%n XRI - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy101XRI

	@Test
	public void testyyy110ORI() {
		// testCount = 20;

		byte valueAcc, valueSource, valueResult;
		byte opCode = (byte) 0XF6; // ORI
		int pc = 0X0100;
		String msg;

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

			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);
			cpuBuss.write(pc + 1, valueSource);

			valueResult = (byte) ((valueAcc | valueSource) & 0XFF);

			wrs.setProgramCounter(pc);
			cpu.executeInstruction(wrs.getProgramCounter());

			msg = String.format("%n ORI - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy110ORI

	@Test
	public void testyyy111CPI() {
		// testCount = 20;

		byte valueAcc, valueSource, valueResult;
		byte opCode = (byte) 0XFE; // CPI
		int pc = 0X0100;
		String msg;

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

			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);
			cpuBuss.write(pc + 1, valueSource);

			valueResult = (byte) (valueAcc - valueSource);

			wrs.setProgramCounter(pc);
			cpu.executeInstruction(wrs.getProgramCounter());

			msg = String.format("%n CMI - OpCode: %2X, Acc: %02X, Source: %02X",
					opCode, valueAcc, valueSource);
			assertThat(msg, valueAcc, equalTo(wrs.getAcc()));
			assertThat(msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(msg, CalculateCC.isAuxCarrySub(valueAcc, valueSource, false),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, CalculateCC.isCarrySub(valueAcc, valueSource, false), equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy111CPI

}// class CentralProcessingUnitPage11ATest
