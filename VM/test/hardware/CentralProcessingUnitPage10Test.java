package hardware;

import static org.junit.Assert.*;

import java.util.Random;

import memory.CpuBuss;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class CentralProcessingUnitPage10Test {

	CentralProcessingUnit cpu;
	CpuBuss cpuBuss;
	WorkingRegisterSet wrs;
	ConditionCodeRegister ccr;
	Random random;
	Register[] registers;
	int testCount;
	boolean isTestSign, isTestZero, isTestAuxCarry, isTestParity, isTestCarry;

	@Before
	public void setUp() throws Exception {
		cpu = new CentralProcessingUnit();
		cpuBuss = CpuBuss.getCpuBuss();
		wrs = WorkingRegisterSet.getWorkingRegisterSet();
		ccr = ConditionCodeRegister.getConditionCodeRegister();

		random = new Random();
		registers = new Register[] { Register.B, Register.C, Register.D, Register.E,
				Register.H, Register.L, Register.M, Register.A };
		testCount = 2000;
	}// setUp

	@Test
	public void testyyy000ADD() {
//		int testCount = 200;

		byte opCode, valueAcc, valueSource, valueResult;
		byte opCodeBase = (byte) 0X80;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
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

			regIndex = random.nextInt(8);
			register = registers[regIndex];
			opCode = (byte) ((byte) (regIndex | opCodeBase));
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);

			if (register.equals(Register.M)) {
				wrs.setDoubleReg(Register.HL, indirectAddress);
				cpuBuss.write(indirectAddress, valueSource);
			} else {
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			valueResult = (byte) (valueAcc + valueSource);

			cpu.executeInstruction(pc);
			msg = String.format("ADD - OpCode: %2X, Acc: %02X, Source: %02X,  Result: %02X",
					opCode, valueAcc, valueSource, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, CalculateCC.isAuxCarry(valueAcc, valueSource, false), equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, CalculateCC.isCarry(valueAcc, valueSource, false), equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy000ADD

	@Test
	public void testyyy001ADC() {
//		testCount = 20;

		byte opCode, valueAcc, valueSource, valueResult, valueCarry;
		byte opCodeBase = (byte) 0X88;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
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

			regIndex = random.nextInt(8);
			register = registers[regIndex];
			opCode = (byte) ((byte) (regIndex | opCodeBase));
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);

			if (register.equals(Register.M)) {
				wrs.setDoubleReg(Register.HL, indirectAddress);
				cpuBuss.write(indirectAddress, valueSource);
			} else {
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			valueResult = (byte) (valueAcc + valueSource + valueCarry);

			cpu.executeInstruction(pc);
			msg = String.format(" ADC - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, CalculateCC.isAuxCarry(valueAcc, valueSource, isTestCarry),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, CalculateCC.isCarry(valueAcc, valueSource, isTestCarry), equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy001ADC

	@Test
	public void testyyy010SUB() {
//		testCount = 20;

		byte opCode, valueAcc, valueSource, valueResult;
		byte opCodeBase = (byte) 0X90;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
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

			regIndex = random.nextInt(8);
			register = registers[regIndex];
			opCode = (byte) ((byte) (regIndex | opCodeBase));
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);

			if (register.equals(Register.M)) {
				wrs.setDoubleReg(Register.HL, indirectAddress);
				cpuBuss.write(indirectAddress, valueSource);
			} else {
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			valueResult = (byte) (valueAcc - valueSource);

			cpu.executeInstruction(pc);
			msg = String.format("SUB - OpCode: %2X, Acc: %02X, Source: %02X,  Result: %02X",
					opCode, valueAcc, valueSource, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, CalculateCC.isAuxCarrySub(valueAcc, valueSource, false),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, CalculateCC.isCarrySub(valueAcc, valueSource, false), equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy010SUB

	@Test
	public void testyyy011SBB() {
//		testCount = 20;

		byte opCode, valueAcc, valueSource, valueResult, valueCarry;
		byte opCodeBase = (byte) 0X98;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
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

			regIndex = random.nextInt(8);
			register = registers[regIndex];
			opCode = (byte) ((byte) (regIndex | opCodeBase));
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);

			if (register.equals(Register.M)) {
				wrs.setDoubleReg(Register.HL, indirectAddress);
				cpuBuss.write(indirectAddress, valueSource);
			} else {
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			valueResult = (byte) (valueAcc - (valueSource + valueCarry));

			cpu.executeInstruction(pc);
			msg = String.format(" ADC - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
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

	}// testyyy001ADC

	@Test
	public void testyyy100ANA() {
//		testCount = 20;

		byte opCode, valueAcc, valueSource, valueResult;
		byte opCodeBase = (byte) 0XA0;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
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

			regIndex = random.nextInt(8);
			register = registers[regIndex];
			opCode = (byte) ((byte) (regIndex | opCodeBase));
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);

			if (register.equals(Register.M)) {
				wrs.setDoubleReg(Register.HL, indirectAddress);
				cpuBuss.write(indirectAddress, valueSource);
			} else {
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			valueResult = (byte) ((valueAcc & valueSource) & 0XFF);

			cpu.executeInstruction(pc);
			msg = String.format(" ANA - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy100ANA


	@Test
	public void testyyy101XRA() {
//		testCount = 20;

		byte opCode, valueAcc, valueSource, valueResult;
		byte opCodeBase = (byte) 0XA8;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
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

			regIndex = random.nextInt(8);
			register = registers[regIndex];
			opCode = (byte) ((byte) (regIndex | opCodeBase));
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);

			if (register.equals(Register.M)) {
				wrs.setDoubleReg(Register.HL, indirectAddress);
				cpuBuss.write(indirectAddress, valueSource);
			} else {
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			valueResult = (byte) ((valueAcc ^ valueSource) & 0XFF);

			cpu.executeInstruction(pc);
			msg = String.format(" XRA - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy101XRA


	@Test
	public void testyyy110ORA() {
//		testCount = 20;

		byte opCode, valueAcc, valueSource, valueResult;
		byte opCodeBase = (byte) 0XB0;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
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

			regIndex = random.nextInt(8);
			register = registers[regIndex];
			opCode = (byte) ((byte) (regIndex | opCodeBase));
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);

			if (register.equals(Register.M)) {
				wrs.setDoubleReg(Register.HL, indirectAddress);
				cpuBuss.write(indirectAddress, valueSource);
			} else {
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			valueResult = (byte) ((valueAcc | valueSource) & 0XFF);

			cpu.executeInstruction(pc);
			msg = String.format(" ORA - OpCode: %2X, Acc: %02X, Source: %02X, Carry %s  Result: %02X",
					opCode, valueAcc, valueSource, isTestCarry, valueResult);
			assertThat(i + msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(i + msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(i + msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(i + msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(i + msg, false, equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy110ORA


	@Test
	public void testyyy111CMP() {
//		testCount = 20;

		byte opCode, valueAcc, valueSource, valueResult;
		byte opCodeBase = (byte) 0XB8;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
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

			regIndex = random.nextInt(8);
			register = registers[regIndex];
			opCode = (byte) ((byte) (regIndex | opCodeBase));
			cpuBuss.write(pc, opCode);
			valueAcc = (byte) random.nextInt(0X100);
			wrs.setAcc(valueAcc);

			valueSource = (byte) random.nextInt(0X100);

			if (register.equals(Register.M)) {
				wrs.setDoubleReg(Register.HL, indirectAddress);
				cpuBuss.write(indirectAddress, valueSource);
			} else {
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			valueResult = (byte) (valueAcc - valueSource);

			cpu.executeInstruction(pc);
			msg = String.format("CMP - OpCode: %2X, Acc: %02X, Source: %02X",
					opCode, valueAcc, valueSource);
			assertThat(msg, valueAcc, equalTo(wrs.getAcc()));
			assertThat(msg, CalculateCC.isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, CalculateCC.isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
			assertThat(msg, CalculateCC.isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(msg, CalculateCC.isAuxCarrySub(valueAcc, valueSource, false),
					equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, CalculateCC.isCarrySub(valueAcc, valueSource, false), equalTo(ccr.isCarryFlagSet()));

		}// for

	}// testyyy010SUB


	
}// class CentralProcessingUnitPage10Test
