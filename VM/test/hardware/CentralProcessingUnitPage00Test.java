package hardware;

import static org.junit.Assert.*;

import java.util.Random;

import memory.CpuBuss;
import memory.IoBuss;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class CentralProcessingUnitPage00Test {
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
		ioBuss.writeDMA(pc, opCodes);
		int values[] = new int[] { 1234, 2222, 32768, 0XFFFF };
		int results[] = new int[] { 2345, 4567, 0, 0XFFFF };

		int seed = 1111;
		wrs.setDoubleReg(Register.HL, seed);
		wrs.setProgramCounter(pc);
		for (int i = 0; i < results.length; i++) {
			wrs.setDoubleReg(registers[i], values[i]);
			cpu.executeInstruction(wrs.getProgramCounter());
			assertThat("testZZZ001 DAD " + i, results[i], equalTo(wrs.getDoubleReg(Register.HL)));
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
				(byte) 0X0A, (byte) 0X1A, (byte) 0X0A, (byte) 0X1A };
		ioBuss.writeDMA(pc, opCodes);

		byte[] values = new byte[] { (byte) 0X55, (byte) 0XAA, (byte) 0XA5, (byte) 0X5A };
		targetLocation = 0X1100;
		ioBuss.writeDMA(targetLocation, values);

		pc = 0X0100;
		wrs.setProgramCounter(pc);
		wrs.setAcc((byte) 00);
		for (int i = 0; i < values.length; i++) {
			wrs.setDoubleReg(Register.BC, targetLocation + i);
			wrs.setDoubleReg(Register.DE, targetLocation + i);
			cpu.executeInstruction(wrs.getProgramCounter());
			assertThat("LDAX - BC " + i, values[i], equalTo(wrs.getAcc()));
			cpu.executeInstruction(wrs.getProgramCounter());
			assertThat("LDAX - DE " + i, values[i], equalTo(wrs.getAcc()));

		}// for

		// SHLD
		byte[] mem = new byte[] { (byte) 0X22, (byte) 0X00, (byte) 0X11, (byte) 0X5A };
		ioBuss.writeDMA(pc, mem);
		targetLocation = 0X1100;

		wrs.setReg(Register.L, (byte) 0X12);
		wrs.setReg(Register.H, (byte) 0X34);
		pc = 0X0100;
		wrs.setProgramCounter(pc);
		cpu.executeInstruction(wrs.getProgramCounter());
		assertThat("SHLD 1 L", wrs.getReg(Register.L), equalTo(cpuBuss.read(targetLocation)));
		assertThat("SHLD 1 H", wrs.getReg(Register.H), equalTo(cpuBuss.read(targetLocation + 1)));

		wrs.setReg(Register.L, (byte) 0X55);
		wrs.setReg(Register.H, (byte) 0XAA);
		pc = 0X0100;
		wrs.setProgramCounter(pc);
		cpu.executeInstruction(wrs.getProgramCounter());
		assertThat("SHLD 2 L", wrs.getReg(Register.L), equalTo(cpuBuss.read(targetLocation)));
		assertThat("SHLD 2 H", wrs.getReg(Register.H), equalTo(cpuBuss.read(targetLocation + 1)));

		// LHLD
		mem = new byte[] { (byte) 0X2A, (byte) 0X00, (byte) 0X11,
				(byte) 0X2A, (byte) 0X02, (byte) 0X11 };
		ioBuss.writeDMA(pc, mem);
		targetLocation = 0X1100;
		values = new byte[] { (byte) 0X34, (byte) 0X12, (byte) 0X55, (byte) 0XAA };
		ioBuss.writeDMA(targetLocation, values);
		pc = 0X0100;
		wrs.setProgramCounter(pc);
		cpu.executeInstruction(wrs.getProgramCounter());
		assertThat("LHLD 1 L", values[0], equalTo(wrs.getReg(Register.L)));
		assertThat("LHLD 1 H", values[1], equalTo(wrs.getReg(Register.H)));

		cpu.executeInstruction(wrs.getProgramCounter());
		assertThat("LHLD 2 L", values[2], equalTo(wrs.getReg(Register.L)));
		assertThat("LHLD 2 H", values[3], equalTo(wrs.getReg(Register.H)));

		// LDA & STA
		mem = new byte[] { (byte) 0X3A, (byte) 0X00, (byte) 0X11, (byte) 0X32, (byte) 0X00, (byte) 0X11,
				(byte) 0X3A, (byte) 0X01, (byte) 0X11, (byte) 0X32, (byte) 0X01, (byte) 0X11 }; // LDA STA
		ioBuss.writeDMA(pc, mem);
		targetLocation = 0X1100;
		values = new byte[] { (byte) 0X55, (byte) 0XAA };
		ioBuss.writeDMA(targetLocation, values);

		wrs.setAcc((byte) 0XFF);
		pc = 0X0100;
		wrs.setProgramCounter(pc);
		cpu.executeInstruction(wrs.getProgramCounter()); // LDA 1100
		assertThat("LDA 1", cpuBuss.read(targetLocation), equalTo(wrs.getAcc()));
		wrs.setAcc((byte) 00);
		cpu.executeInstruction(wrs.getProgramCounter()); // STA 1100
		assertThat("STA 1", (byte) 00, equalTo(cpuBuss.read(targetLocation)));

		cpu.executeInstruction(wrs.getProgramCounter()); // LDA 1100
		assertThat("LDA 2", cpuBuss.read(targetLocation + 1), equalTo(wrs.getAcc()));
		wrs.setAcc((byte) 0XFF);
		cpu.executeInstruction(wrs.getProgramCounter()); // STA 1100
		assertThat("STA 2", (byte) 0XFF, equalTo(cpuBuss.read(targetLocation + 1)));

	}// testZZZ010

	@Test
	public void testZZZ011() {
		// INX & DCX
		int pc = 0X0100;
		byte[] mem = new byte[] { (byte) 0X03, (byte) 0X13, (byte) 0X23, (byte) 0X33,
				(byte) 0X0B, (byte) 0X1B, (byte) 0X2B, (byte) 0X3B }; // LDA STA
		ioBuss.writeDMA(pc, mem);

		Register[] registers = new Register[] { Register.BC, Register.DE, Register.HL, Register.SP };
		Random random = new Random();
		int value;
		int sampleSize = 200;

		for (int i = 0; i < sampleSize; i++) {
			value = random.nextInt(0XFFFF);
			wrs.setProgramCounter(pc);
			for (int r = 0; r < registers.length; r++) { // INX
				wrs.setDoubleReg(registers[r], value);
				cpu.executeInstruction(wrs.getProgramCounter()); // LDA 1100
				assertThat(String.format("INX before- Register %s , value %04X", registers[r], value),
						value + 1, equalTo(wrs.getDoubleReg(registers[r])));
			}// for r

			for (int r = 0; r < registers.length; r++) { // DCX
				wrs.setDoubleReg(registers[r], value);
				cpu.executeInstruction(wrs.getProgramCounter()); // LDA 1100
				assertThat(String.format("INX before- Register %s , value %04X", registers[r], value),
						value - 1, equalTo(wrs.getDoubleReg(registers[r])));
			}// for r

		}// for i
	}// testZZZ011

	@Test
	public void testZZZ100() {
		int pc = 0X0100;
		Register[] registers = { Register.B, Register.C, Register.D, Register.E,
				Register.H, Register.L, Register.M, Register.A };
		// INC - B,C,D,E,H,L,M & A
		byte[] mem = new byte[] { (byte) 0X04, (byte) 0X0C, (byte) 0X14, (byte) 0X1C,
				(byte) 0X24, (byte) 0X2C, (byte) 0X34, (byte) 0X3C };
		ioBuss.writeDMA(pc, mem);
		int count = 10;
		byte value;
		byte[] values = new byte[count];
		Random random = new Random();
		random.nextBytes(values);

		int indirectAddress = 0X500;
		wrs.setDoubleReg(Register.M, indirectAddress); // set HL to point at location 500

		for (int i = 0; i < values.length; i++) {
			value = values[i];
			cpuBuss.write(indirectAddress, value);
			wrs.setProgramCounter(pc);
			for (int r = 0; r > registers.length; r++) {
				if (registers[i].equals(Register.M)) {
					wrs.setDoubleReg(Register.M, indirectAddress); // set HL to point at location 500
				}// if Register.M
				cpu.executeInstruction(wrs.getProgramCounter());
				assertThat(String.format("Random INC value = %02X, r = %d", value, r), (byte) (value + 1),
						equalTo(wrs.getReg(registers[i])));
			}// for r
		}// for i

		// test flags
		pc = 0X0100;
		value = (byte) 0XFE;
		wrs.setAcc(value);
		cpuBuss.write(pc, (byte) 0X3C); // INC A
		byte targetFlags = (byte) 0B10000110; // FF -> S & C
		byte noFlags = (byte) 0B00000010; // FF -> S & C
		byte allFlags = (byte) 0B11010111; // FF -> S & C

		// Sign & carry
		wrs.setAcc(value);
		ccr.setConditionCode(noFlags);
		cpu.executeInstruction(pc);
		assertThat("Flags - value 1", (byte) (value + 1), equalTo(wrs.getAcc()));
		assertThat("Flags - flags 1", targetFlags, equalTo(ccr.getConditionCode()));

		wrs.setAcc(value);
		ccr.setConditionCode(allFlags);
		cpu.executeInstruction(pc);
		assertThat("Flags - value 2", (byte) (value + 1), equalTo(wrs.getAcc()));
		assertThat("Flags - flags 2", (byte) (targetFlags | 0X01), equalTo(ccr.getConditionCode()));

		// Zero, Aux & Parity
		value = (byte) 0XFF;
		wrs.setAcc(value);
		ccr.clearAllCodes(); // 0B00000010
		targetFlags = (byte) 0B01010110; // FF -> Z,Ac,P,C
		cpu.executeInstruction(pc);
		assertThat("Flags - value 3", (byte) (00), equalTo(wrs.getAcc()));
		assertThat("Flags - flags 3", (byte) (targetFlags), equalTo(ccr.getConditionCode()));

	}// testZZZ100

	@Test
	public void testZZZ101() {
		int pc = 0X0100;
		Register[] registers = { Register.B, Register.C, Register.D, Register.E,
				Register.H, Register.L, Register.M, Register.A };
		// INC - B,C,D,E,H,L,M & A
		byte[] mem = new byte[] { (byte) 0X05, (byte) 0X0D, (byte) 0X15, (byte) 0X1D,
				(byte) 0X25, (byte) 0X2D, (byte) 0X35, (byte) 0X3D };
		ioBuss.writeDMA(pc, mem);
		int count = 10;
		byte value;
		byte[] values = new byte[count];
		Random random = new Random();
		random.nextBytes(values);

		int indirectAddress = 0X500;
		wrs.setDoubleReg(Register.M, indirectAddress); // set HL to point at location 500

		for (int i = 0; i < values.length; i++) {
			value = values[i];
			cpuBuss.write(indirectAddress, value);
			wrs.setProgramCounter(pc);
			for (int r = 0; r > registers.length; r++) {
				if (registers[i].equals(Register.M)) {
					wrs.setDoubleReg(Register.M, indirectAddress); // set HL to point at location 500
				}// if Register.M
				cpu.executeInstruction(wrs.getProgramCounter());
				assertThat(String.format("Random DCR value = %02X, r = %d", value, r), (byte) (value - 1),
						equalTo(wrs.getReg(registers[i])));
			}// for r
		}// for i

		// test flags
		pc = 0X0100;
		value = (byte) 0X00;
		wrs.setAcc(value);
		cpuBuss.write(pc, (byte) 0X3D); // DCR A
		byte targetFlags = (byte) 0B10000110; // FF -> S & C
		byte noFlags = (byte) 0B00000010; // FF -> S & C
		byte allFlags = (byte) 0B11010111; // FF -> S & C

		// Sign & carry
		wrs.setAcc(value);
		ccr.setConditionCode(noFlags);
		cpu.executeInstruction(pc);
		assertThat("Flags - value 1", (byte) (value - 1), equalTo(wrs.getAcc()));
		assertThat("Flags - flags 1", targetFlags, equalTo(ccr.getConditionCode()));

		wrs.setAcc(value);
		ccr.setConditionCode(allFlags);
		cpu.executeInstruction(pc);
		assertThat("Flags - value 2", (byte) (value - 1), equalTo(wrs.getAcc()));
		assertThat("Flags - flags 2", (byte) (targetFlags | 0X01), equalTo(ccr.getConditionCode()));

		// Zero, Aux & Parity
		value = (byte) 0X01;
		wrs.setAcc(value);
		ccr.clearAllCodes(); // 0B00000010
		targetFlags = (byte) 0B01010110; // FF -> Z,Ac,P,C
		cpu.executeInstruction(pc);
		assertThat("Flags - value 3", (byte) (00), equalTo(wrs.getAcc()));
		assertThat("Flags - flags 3", (byte) (targetFlags), equalTo(ccr.getConditionCode()));

	}// testZZZ101

	@Test
	public void testZZZ110() {
		int count = 3;
		byte[] values = new byte[8 * count];
		Random random = new Random();
		random.nextBytes(values);

		// count X MVI B,C,D,E,H,L,M,A
		byte[] mem = new byte[values.length * 2];
		for (int i = 0; i < values.length - 1;) {
			for (byte opCode = 0X06; opCode <= 0X3E; opCode += 0X08) {
				mem[2 * i] = opCode;
				mem[(2 * i) + 1] = values[i];
				i++;
			}// for opCode
		}// for i
		int pc = 0X0100;
		ioBuss.writeDMA(pc, mem);

		Register[] registers = { Register.B, Register.C, Register.D, Register.E,
				Register.H, Register.L, Register.M, Register.A };

		byte value;

		int indirectAddress = 0X500;
		wrs.setDoubleReg(Register.M, indirectAddress); // set HL to point at location 500

		wrs.setProgramCounter(pc);
		for (int i = 0; i < count; i++) {

			for (int r = 0; r > registers.length; r++) {
				value = cpuBuss.read(wrs.getProgramCounter() + 1);
				if (registers[i].equals(Register.M)) {
					wrs.setDoubleReg(Register.M, indirectAddress); // set HL to point at location 500
				} else {
					cpu.executeInstruction(wrs.getProgramCounter());
					assertThat(String.format("Random MVI value = %02X, r = %d", value, r), value,
							equalTo(wrs.getReg(registers[i])));

				}// if register.M
			}// for r

		}// for i

	}// testZZZ110

	@Test
	public void testZZZ111() {
		byte noFlags = (byte) 0B00000010; // FF -> S & C
		byte allFlags = (byte) 0B11010111; // FF -> S & C

		byte initValue, resultValue;
		// RLC
		int pc = 0X0100;
		cpuBuss.write(pc, (byte) 0X07); // RLC

		initValue = (byte) 0X00;
		resultValue = (byte) 0X00;
		wrs.setAcc(initValue);
		ccr.setConditionCode(noFlags);
		cpu.executeInstruction(pc);
		assertThat("RLC value 1", resultValue, equalTo(wrs.getAcc()));
		assertThat("RLC flags 1", noFlags, equalTo(ccr.getConditionCode()));

		initValue = (byte) 0X00;
		resultValue = (byte) 0X00;
		wrs.setAcc(initValue);
		ccr.setConditionCode(allFlags);
		cpu.executeInstruction(pc);
		assertThat("RLC value 2", resultValue, equalTo(wrs.getAcc()));
		assertThat("RLC flags 2", (byte) (allFlags & 0XFE), equalTo(ccr.getConditionCode()));

		initValue = (byte) 0XAA;
		resultValue = (byte) 0X55;
		wrs.setAcc(initValue);
		ccr.setCarryFlag(false); // turn off carry
		cpu.executeInstruction(pc);
		assertThat("RLC value 3", resultValue, equalTo(wrs.getAcc()));
		assertThat("RLC flags 3", true, equalTo(ccr.isCarryFlagSet()));

		initValue = (byte) 0X5A;
		resultValue = (byte) 0XB4;
		wrs.setAcc(initValue);
		ccr.setCarryFlag(true); // turn off carry
		cpu.executeInstruction(pc);
		assertThat("RLC value 4", resultValue, equalTo(wrs.getAcc()));
		assertThat("RLC flags 4", false, equalTo(ccr.isCarryFlagSet()));

		// RRC
		pc = 0X0100;
		cpuBuss.write(pc, (byte) 0X0F); // RRC

		initValue = (byte) 0X00;
		resultValue = (byte) 0X00;
		wrs.setAcc(initValue);
		ccr.setConditionCode(noFlags);
		cpu.executeInstruction(pc);
		assertThat("RRC value 1", resultValue, equalTo(wrs.getAcc()));
		assertThat("RRC flags 1", noFlags, equalTo(ccr.getConditionCode()));

		initValue = (byte) 0X00;
		resultValue = (byte) 0X00;
		wrs.setAcc(initValue);
		ccr.setConditionCode(allFlags);
		cpu.executeInstruction(pc);
		assertThat("RRC value 2", resultValue, equalTo(wrs.getAcc()));
		assertThat("RRC flags 2", (byte) (allFlags & 0XFE), equalTo(ccr.getConditionCode()));

		initValue = (byte) 0XAA;
		resultValue = (byte) 0X55;
		wrs.setAcc(initValue);
		ccr.setCarryFlag(false); // turn off carry
		cpu.executeInstruction(pc);
		assertThat("RRC value 3", resultValue, equalTo(wrs.getAcc()));
		assertThat("RRC flags 3", false, equalTo(ccr.isCarryFlagSet()));

		initValue = (byte) 0XA5;
		resultValue = (byte) 0XD2;
		wrs.setAcc(initValue);
		ccr.setCarryFlag(true); // turn off carry
		cpu.executeInstruction(pc);
		assertThat("RRC value 4", resultValue, equalTo(wrs.getAcc()));
		assertThat("RRC flags 4", true, equalTo(ccr.isCarryFlagSet()));

		// RAL
		pc = 0X0100;
		cpuBuss.write(pc, (byte) 0X17); // RAL

		initValue = (byte) 0X00;
		resultValue = (byte) 0X00;
		wrs.setAcc(initValue);
		ccr.setConditionCode(noFlags);
		cpu.executeInstruction(pc);
		assertThat("RAL value 1", resultValue, equalTo(wrs.getAcc()));
		assertThat("RAL flags 1", noFlags, equalTo(ccr.getConditionCode()));

		initValue = (byte) 0X00;
		resultValue = (byte) 0X01;
		wrs.setAcc(initValue);
		ccr.setConditionCode(allFlags);
		cpu.executeInstruction(pc);
		assertThat("RAL value 2", resultValue, equalTo(wrs.getAcc()));
		assertThat("RAL flags 2", (byte) (allFlags & 0XFE), equalTo(ccr.getConditionCode()));

		initValue = (byte) 0XAA;
		resultValue = (byte) 0X54;
		wrs.setAcc(initValue);
		ccr.setCarryFlag(false); // turn off carry
		cpu.executeInstruction(pc);
		assertThat("RAL value 3", resultValue, equalTo(wrs.getAcc()));
		assertThat("RAL flags 3", true, equalTo(ccr.isCarryFlagSet()));

		initValue = (byte) 0XAA;
		resultValue = (byte) 0X55;
		wrs.setAcc(initValue);
		ccr.setCarryFlag(true); // turn off carry
		cpu.executeInstruction(pc);
		assertThat("RAL value 4", resultValue, equalTo(wrs.getAcc()));
		assertThat("RAL flags 4", true, equalTo(ccr.isCarryFlagSet()));

		// RAR
		pc = 0X0100;
		cpuBuss.write(pc, (byte) 0X1F); // RAR

		initValue = (byte) 0X00;
		resultValue = (byte) 0X00;
		wrs.setAcc(initValue);
		ccr.setConditionCode(noFlags);
		cpu.executeInstruction(pc);
		assertThat("RAR value 1", resultValue, equalTo(wrs.getAcc()));
		assertThat("RAR flags 1", noFlags, equalTo(ccr.getConditionCode()));

		initValue = (byte) 0X00;
		resultValue = (byte) 0X80;
		wrs.setAcc(initValue);
		ccr.setConditionCode(allFlags);
		cpu.executeInstruction(pc);
		assertThat("RAR value 2", resultValue, equalTo(wrs.getAcc()));
		assertThat("RAR flags 2", (byte) (allFlags & 0XFE), equalTo(ccr.getConditionCode()));

		initValue = (byte) 0XAA;
		resultValue = (byte) 0X55;
		wrs.setAcc(initValue);
		ccr.setCarryFlag(false); // turn off carry
		cpu.executeInstruction(pc);
		assertThat("RAR value 3", resultValue, equalTo(wrs.getAcc()));
		assertThat("RAR flags 3", false, equalTo(ccr.isCarryFlagSet()));

		initValue = (byte) 0XA1;
		resultValue = (byte) 0XD0;
		wrs.setAcc(initValue);
		ccr.setCarryFlag(true); // turn off carry
		cpu.executeInstruction(pc);
		assertThat("RAR value 4", resultValue, equalTo(wrs.getAcc()));
		assertThat("RAR flags 4", true, equalTo(ccr.isCarryFlagSet()));

		// DAA
		pc = 0X0100;
		cpuBuss.write(pc, (byte) 0X27); // DAA
		initValue = (byte) 0X9B;
		resultValue = (byte) 0X01;
		wrs.setAcc(initValue);
		ccr.setConditionCode(noFlags);
		cpu.executeInstruction(pc);
		assertThat("RAR value 1", resultValue, equalTo(wrs.getAcc()));

		assertThat("RAR flags cf 1", true, equalTo(ccr.isCarryFlagSet()));
		assertThat("RAR flags 1", true, equalTo(ccr.isAuxilaryCarryFlagSet()));

		// CMA
		pc = 0X0100;
		cpuBuss.write(pc, (byte) 0X2F); // CMA

		initValue = (byte) 0X00;
		resultValue = (byte) 0XFF;
		wrs.setAcc(initValue);
		ccr.setConditionCode(noFlags);
		cpu.executeInstruction(pc);
		assertThat("CMA value 1", resultValue, equalTo(wrs.getAcc()));
		assertThat("CMA flags 1", noFlags, equalTo(ccr.getConditionCode()));

		initValue = (byte) 0XAA;
		resultValue = (byte) 0X55;
		wrs.setAcc(initValue);
		ccr.setConditionCode(allFlags);
		cpu.executeInstruction(pc);
		assertThat("CMA value 2", resultValue, equalTo(wrs.getAcc()));
		assertThat("CMA flags 2", allFlags, equalTo(ccr.getConditionCode()));

		// STC & CMC
		byte[] opCodes = new byte[] { (byte) 0X37, (byte) 0X37, (byte) 0X3F, (byte) 0X3F };
		pc = 0X0100;
		ioBuss.writeDMA(pc, opCodes);
		wrs.setProgramCounter(pc);
		
		ccr.setCarryFlag(false);
		cpu.executeInstruction(wrs.getProgramCounter()); // Set Carry
		assertThat("STC - 1",true,equalTo(ccr.isCarryFlagSet()));
		cpu.executeInstruction(wrs.getProgramCounter()); // Set Carry
		assertThat("STC - 1",true,equalTo(ccr.isCarryFlagSet()));
		cpu.executeInstruction(wrs.getProgramCounter()); // Set Carry
		assertThat("STC - 1",false,equalTo(ccr.isCarryFlagSet()));
		cpu.executeInstruction(wrs.getProgramCounter()); // Set Carry
		assertThat("STC - 1",true,equalTo(ccr.isCarryFlagSet()));


	}// testZZZ111

}
