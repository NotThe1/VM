package hardware;

import static org.junit.Assert.*;
import memory.CpuBuss;
import memory.IoBuss;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class CentralProcessingUnitPage00Rest {
	CentralProcessingUnit cpu;
	CpuBuss cpuBuss;
	IoBuss ioBuss;
	WorkingRegisterSet wrs;
	ConditionCodeRegister ccr;

	@Before
	public void setUp() throws Exception {
		cpu = new CentralProcessingUnit();
		cpuBuss = CpuBuss.getCpuBuss();
		wrs = WorkingRegisterSet.getWorkingRegisterSet();
		ioBuss = IoBuss.getIoBuss();
		ccr = ConditionCodeRegister.getConditionCodeRegister();
	}

	@Test
	public void testZZZ000() { // NOP
		int pc = 0X0100;
		byte opCode = 00;
		wrs.setProgramCounter(pc);
		cpuBuss.write(pc, opCode);
		assertThat("testZZZ000 nop pc", pc, equalTo(wrs.getProgramCounter()));
		assertThat("testZZZ000 nop opCode", opCode, equalTo(cpuBuss.read(pc)));
		cpu.executeInstruction(pc);
		assertThat("testZZZ000 nop pc +1", pc + 1, equalTo(wrs.getProgramCounter()));
	}//

	@Test
	public void testZZZ001() {
		int pc = 0X0100;
		wrs.setProgramCounter(pc);
		byte[] memValues = new byte[] { (byte) 0X01, (byte) 0X34, (byte) 0X12 };

		// LXI
		byte[] opCodes = new byte[] { (byte) 0X01, (byte) 0X11, (byte) 0X21, (byte) 0X31 };
		Register[] registers = new Register[] { Register.BC, Register.DE, Register.HL, Register.SP };

		ioBuss.writeDMA(pc, memValues); // load memory

		byte opCode;
		Register regTarget;
		int regStartValue = 0X0000;
		int regEndValue = 0X1234;
		int instructionLength = 3;
		for (int i = 0; i < opCodes.length; i++) {
			wrs.setProgramCounter(pc);
			regTarget = registers[i];
			opCode = opCodes[i];
			cpuBuss.write(pc, opCode);
			wrs.setDoubleReg(regTarget, regStartValue); // int the register
			assertThat("testZZZ001 before " + i, regStartValue, equalTo(wrs.getDoubleReg(regTarget)));
			cpu.executeInstruction(pc); // run the instruction
			assertThat("testZZZ001 after " + i, regEndValue, equalTo(wrs.getDoubleReg(regTarget)));
			assertThat("testZZZ001 pc after " + i, pc + instructionLength, equalTo(wrs.getProgramCounter()));
		}// for
		regEndValue = 0X1234;
		// DAD
		instructionLength = 1;
		opCodes = new byte[] { (byte) 0X09, (byte) 0X19, (byte) 0X29, (byte) 0X39 };
		int values[] = new int[] { 1234, 2222, 32768, 0XFFFF };
		int results[] = new int[] { 2345, 4567, 0, 0XFFFF };

		int seed = 1111;
		wrs.setDoubleReg(Register.HL, seed);
		for (int i = 0; i < results.length; i++) {
			wrs.setDoubleReg(registers[i], values[i]);
			cpuBuss.write(pc, opCodes[i]);
			cpu.executeInstruction(pc);
			assertThat("testZZZ001 DAD " + i, results[i], equalTo(wrs.getDoubleReg(Register.HL)));
			assertThat("testZZZ001 pc after " + i, pc + instructionLength, equalTo(wrs.getProgramCounter()));
		}// for

		// check carry flag
		wrs.setDoubleReg(Register.HL, seed);
		wrs.setDoubleReg(Register.BC, 0000);
		cpuBuss.write(pc, (byte) 0X09); // DAD BC
		cpu.executeInstruction(pc);
		assertThat("testZZZ001 test carry - 0", false, equalTo(ccr.isCarryFlagSet()));

		wrs.setDoubleReg(Register.HL, 0X8001);
		cpuBuss.write(pc, (byte) 0X29); // DAD HL
		cpu.executeInstruction(pc);
		assertThat("testZZZ001 test carry - 1", true, equalTo(ccr.isCarryFlagSet()));

	}// testZZZ001

	@Test
	public void testZZZ010() {
		// STAX BC - STAX DE - STAX BC - STAX DE
		byte[] opCodes = new byte[] { (byte) 0X02, (byte) 0X12, (byte) 0X02, (byte) 0X12 };
		int pc = 0X0100;
		ioBuss.writeDMA(pc, opCodes);
		wrs.setProgramCounter(pc);
		int targetLocation = 0X1100;

		wrs.setDoubleReg(Register.BC, targetLocation);
		wrs.setDoubleReg(Register.DE, targetLocation + 1);
		cpuBuss.write(targetLocation, (byte) 0XFF);
		cpuBuss.write(targetLocation + 1, (byte) 0XFF);

		byte value = (byte) 0X55;
		wrs.setAcc(value);
		cpu.executeInstruction(wrs.getProgramCounter());
		assertThat("STAX - BC 55", value, equalTo(cpuBuss.read(targetLocation)));
		cpu.executeInstruction(wrs.getProgramCounter());
		assertThat("STAX - DE 55", value, equalTo(cpuBuss.read(targetLocation)));

		value = (byte) 0XAA;
		wrs.setAcc(value);
		cpu.executeInstruction(wrs.getProgramCounter());
		assertThat("STAX - BC 55", value, equalTo(cpuBuss.read(targetLocation)));
		cpu.executeInstruction(wrs.getProgramCounter());
		assertThat("STAX - DE 55", value, equalTo(cpuBuss.read(targetLocation)));

		// LDAX BC - LDAX DE - LDAX BC - LDAX DE
		opCodes = new byte[] { (byte) 0X0A, (byte) 0X1A, (byte) 0X0A, (byte) 0X1A,
				(byte) 0X0A, (byte) 0X1A, (byte) 0X0A, (byte) 0X1A};
		ioBuss.writeDMA(pc, opCodes);
		
		byte[] values = new byte[] { (byte) 0X55, (byte) 0XAA, (byte) 0XA5, (byte) 0X5A };
		targetLocation = 0X1100;
		ioBuss.writeDMA(targetLocation, values);
		
		pc = 0X0100;
		wrs.setProgramCounter(pc);
		wrs.setAcc((byte) 00);
		for (int i = 0; i < values.length; i++){
			wrs.setDoubleReg(Register.BC, targetLocation + i);
			wrs.setDoubleReg(Register.DE, targetLocation + i);
			cpu.executeInstruction(wrs.getProgramCounter());
			assertThat("LDAX - BC " + i, values[i], equalTo(wrs.getAcc()));
			cpu.executeInstruction(wrs.getProgramCounter());
			assertThat("LDAX - DE " + i, values[i], equalTo(wrs.getAcc()));
		
		}//for
		
		

		
		
		
	}// testZZZ010

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }//

}
