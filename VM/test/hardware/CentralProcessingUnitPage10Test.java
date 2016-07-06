package hardware;

import static org.junit.Assert.*;

import java.util.Random;

import memory.CpuBuss;
import memory.IoBuss;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

public class CentralProcessingUnitPage10Test {

	CentralProcessingUnit cpu;
	CpuBuss cpuBuss;
	IoBuss ioBuss;
	WorkingRegisterSet wrs;
	ConditionCodeRegister ccr;
	Random random;
	Register[] registers;

	@Before
	public void setUp() throws Exception {
		cpu = new CentralProcessingUnit();
		cpuBuss = CpuBuss.getCpuBuss();
		wrs = WorkingRegisterSet.getWorkingRegisterSet();
		ioBuss = IoBuss.getIoBuss();
		ccr = ConditionCodeRegister.getConditionCodeRegister();

		random = new Random();
		registers = new Register[] { Register.B, Register.C, Register.D, Register.E,
				Register.H, Register.L, Register.M, Register.A };
	}// setUp

	@Test
	public void testyyy000ADD() {
		int count = 2000;
		byte opCode, valueAcc, valueSource, valueResult,valueCarry;
		byte opCodeBase = (byte) 0X80;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
		String msg;
		boolean carryIn;
		

		for (int i = 0; i < count; i++) {
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
			}else{
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			carryIn = (random.nextInt() % 2)== 1?true:false;
			valueCarry = carryIn?(byte) 0X01:(byte)0X00;
			ccr.setCarryFlag(carryIn);
			
			cpu.executeInstruction(pc);
			valueResult = (byte) (valueAcc + valueSource);
			msg = String.format("ADD - OpCode: %2X, Acc: %02X, Source: %02X, Carry: %s, Result: %02X",
					opCode, valueAcc, valueSource,carryIn, valueResult);
			assertThat(msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(msg, isCarry(valueAcc, valueSource,false), equalTo(ccr.isCarryFlagSet()));
			assertThat(msg, isAuxCarry(valueAcc, valueSource,false), equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(msg, isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
		}// for

	}// testyyy000ADD

	@Test
	public void testyyy001ADC() {
		int count = 2000;
		byte opCode, valueAcc, valueSource, valueResult;
		byte opCodeBase = (byte) 0X88;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
		String msg;
		boolean carryIn;
		byte valueCarry;

		for (int i = 0; i < count; i++) {
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
			}else{
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			
			carryIn = (random.nextInt() % 2)== 1?true:false;
			valueCarry = carryIn?(byte) 0X01:(byte)0X00;
			ccr.setCarryFlag(carryIn);
			
			cpu.executeInstruction(pc);
			valueResult = (byte) (valueAcc + valueSource + valueCarry);
			msg = String.format("ADC - OpCode: %2X, Acc: %02X, Source: %02X, Carry: %s, Result: %02X",
					opCode, valueAcc, valueSource,carryIn, valueResult);
			assertThat(msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(msg, isCarry(valueAcc, valueSource,carryIn), equalTo(ccr.isCarryFlagSet()));
			assertThat(msg, isAuxCarry(valueAcc, valueSource,carryIn), equalTo(ccr.isAuxilaryCarryFlagSet()));
			assertThat(msg, isSign(valueResult), equalTo(ccr.isSignFlagSet()));
			assertThat(msg, isParity(valueResult), equalTo(ccr.isParityFlagSet()));
			assertThat(msg, isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
		}// for

	}// testyyy001ADC

	@Test
	public void testyyy010SUB() {
		int count = 2000;
		byte opCode, valueAcc, valueSource, valueResult;
		byte opCodeBase = (byte) 0X90;
		int indirectAddress = 0X500;
		int pc = 0X0100;
		int regIndex;
		Register register;
		String msg;
		boolean carryIn;
		byte valueCarry;

		for (int i = 0; i < count; i++) {
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
			}else{
				wrs.setReg(register, valueSource);
			}// if reg M

			if (register.equals(Register.A)) {
				valueAcc = valueSource;
			}// if source is Acc
			
			carryIn = (random.nextInt() % 2)== 1?true:false;
			valueCarry = carryIn?(byte) 0X01:(byte)0X00;
			ccr.setCarryFlag(carryIn);
			
			cpu.executeInstruction(pc);
			valueResult = (byte) (valueAcc - valueSource );
			msg = String.format(" SUB - OpCode: %2X, Acc: %02X, Source: %02X, Carry, %s,Result: %02X",
					opCode, valueAcc, valueSource,carryIn, valueResult);
			assertThat(msg, valueResult, equalTo(wrs.getAcc()));
			assertThat(msg, isCarrySub(valueAcc, valueSource,false), equalTo(ccr.isCarryFlagSet()));
			assertThat(i + msg, isAuxCarrySub(valueAcc, valueSource,false), equalTo(ccr.isAuxilaryCarryFlagSet()));
//			assertThat(msg, isSign(valueResult), equalTo(ccr.isSignFlagSet()));
//			assertThat(msg, isParity(valueResult), equalTo(ccr.isParityFlagSet()));
//			assertThat(msg, isZero(valueResult), equalTo(ccr.isZeroFlagSet()));
		}// for

	}// testyyy010SUB
//
	private boolean isCarry(byte value1, byte value2, boolean carryIn) {
		byte carry = carryIn?(byte) 0X01:(byte) 0X00;
		return ((carry & 0X01)+ (value1 & 0XFF) + (value2 & 0XFF)) >= 256;
	}// isCarry
	private boolean isCarrySub(byte value1, byte value2, boolean carryIn) {
		byte carry = carryIn?(byte) 0X01:(byte) 0X00;
		byte valueComplement = (byte) ~value2;
		return !(((carry & 0X01)+ (value1 & 0XFF) + (valueComplement & 0XFF)+ 1) >= 256);
	}// isCarrySub

	
	private boolean isAuxCarry(byte value1, byte value2, boolean carryIn) {
		byte carry = carryIn?(byte) 0X01:(byte) 0X00;
		return ((carry & 0X01)+ (value1 & 0X0F) + (value2 & 0X0F)) >= 16;
	}// isAuxCarryGenerated
	private boolean isAuxCarrySub(byte value1, byte value2, boolean carryIn) {
		byte valueComplement = (byte)(( ~value2 & 0X0F) + 1);
		byte carry = carryIn?(byte) 0X01:(byte) 0X00;
		int sum = ((carry & 0X01)+ (value1 & 0X0F) + (valueComplement & 0X1F) );
		return (sum >= 16); //***
	}// isAuxCarryGenerated

	private boolean isSign(byte value1) {
		return (value1 & 0B10000000) == 0B10000000;
	}// isAuxCarryGenerated

	private boolean isParity(byte value1) {
		return (Integer.bitCount(value1) % 2) == 0;
	}// isAuxCarryGenerated

	private boolean isZero(byte value1) {
		return value1 == 0;
	}// isAuxCarryGenerated

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }//

}// class CentralProcessingUnitPage10Test
