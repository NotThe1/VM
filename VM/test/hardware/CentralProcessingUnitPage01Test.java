package hardware;

import static org.junit.Assert.*;

import java.util.Random;
import memory.CpuBuss;
import memory.IoBuss;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class CentralProcessingUnitPage01Test {
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
	public void testHalt() {
		int pc = 0X0100;
		wrs.setProgramCounter(pc);
		cpuBuss.write(pc, (byte) 0X00); // NOP
		cpuBuss.write(pc + 1, (byte) 0X76); // HLT
		assertThat("HALT - 1", false, equalTo(cpu.isError()));
		assertThat("HALT - 2", ErrorType.NONE, equalTo(cpu.getError()));
		assertThat("HALT - 3", false, equalTo(cpu.startInstruction()));
		assertThat("HALT - 4", true, equalTo(cpu.startInstruction()));
		assertThat("HALT - 5", ErrorType.HLT_INSTRUCTION, equalTo(cpu.getError()));
	}// testHalt

	@Test
	public void testMov() {
		int count = 2000;
		// Random random = new Random();
		byte opCode, value;
		int pc = 0X0100;

		byte opCode67 = 0B01000000;
		byte opCode345 = 0B00000000;
		byte opCode012 = 0B00000000;

		int sourceIndex, destIndex;
		Register sourceRegister, destRegister;
		for (int i = 0; i < count; i++) {
			clearAllRegs();

			sourceIndex = random.nextInt(8);
			sourceRegister = registers[sourceIndex];
			destIndex = random.nextInt(8);
			destRegister = registers[destIndex];
			opCode012 = (byte) (sourceIndex);
			opCode345 = (byte) (destIndex << 3);
			opCode = (byte) (opCode012 | opCode345 | opCode67);
			if (opCode == (byte) 0X76) {
				continue;
			}// if HLT
			cpuBuss.write(pc, opCode);

			value = (byte) random.nextInt(0X100); // between 00 and 0XFF

			if (sourceRegister.equals(Register.M) | destRegister.equals(Register.M)) {
				continue; // tested below
			}// if Register.M

			wrs.setReg(sourceRegister, value); // set the source
			cpu.executeInstruction(pc);

			assertThat(String.format("Mov source- opCode %02X, Dest: %s, Source: %s, value = %2X",
					opCode, destRegister, sourceRegister, value),
					value, equalTo(wrs.getReg(sourceRegister)));
			assertThat(String.format("Mov dest- opCode %02X, Dest: %s, Source: %s, value = %2X",
					opCode, destRegister, sourceRegister, value),
					value, equalTo(wrs.getReg(destRegister)));

			// String msg = String.format("Mov source- opCode %02X, Dest: %s, Source: %s, value = %2X",
			// opCode,destRegister,sourceRegister,value);
			// System.out.println(msg);
		}// for count

	}// testMov

	public void clearAllRegs() {
		byte value = (byte) 0X00;
		wrs.setReg(Register.A, value);
		wrs.setReg(Register.B, value);
		wrs.setReg(Register.C, value);
		wrs.setReg(Register.D, value);
		wrs.setReg(Register.E, value);
		wrs.setReg(Register.H, value);
		wrs.setReg(Register.L, value);
	}// clearAllRegs

	@Test
	public void testRegMSource() {
		// MOV (H,L,B,C,D,E,A),M
		byte[] mem = new byte[] { (byte) 0X66, (byte) 0X6E, (byte) 0X46, (byte) 0X4E, (byte) 0X56, (byte) 0X5E,
				(byte) 0X7E
		};
		int pc = 0X0100;
		wrs.setProgramCounter(pc);
		ioBuss.writeDMA(pc, mem);

		int indirectAddress = 0X500;
		byte value;
		clearAllRegs();
		wrs.setDoubleReg(Register.HL, indirectAddress);

		value = putRandomValueInMemory(indirectAddress);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV H,M
		// System.out.printf("value: %02X, reg H:%02X %n",value, wrs.getReg(Register.H));
		assertThat("MOV H,M", value, equalTo(wrs.getReg(Register.H)));

		wrs.setDoubleReg(Register.HL, indirectAddress);
		value = putRandomValueInMemory(indirectAddress);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV L,M
		assertThat("MOV L,M", value, equalTo(wrs.getReg(Register.L)));

		wrs.setDoubleReg(Register.HL, indirectAddress);
		value = putRandomValueInMemory(indirectAddress);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV B,M
		assertThat("MOV B,M", value, equalTo(wrs.getReg(Register.B)));

		value = putRandomValueInMemory(indirectAddress);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV C,M
		assertThat("MOV C,M", value, equalTo(wrs.getReg(Register.C)));

		value = putRandomValueInMemory(indirectAddress);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV D,M
		assertThat("MOV D,M", value, equalTo(wrs.getReg(Register.D)));

		value = putRandomValueInMemory(indirectAddress);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV E,M
		assertThat("MOV E,M", value, equalTo(wrs.getReg(Register.E)));

		value = putRandomValueInMemory(indirectAddress);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV A,M
		assertThat("MOV A,M", value, equalTo(wrs.getReg(Register.A)));

	}// testRegMSource

	@Test
	public void testRegMDest() {
		// MOV M,(B,C,D,E,A,H,L)
		byte[] mem = new byte[] { (byte) 0X70, (byte) 0X71, (byte) 0X72, (byte) 0X73,
				(byte) 0X77, (byte) 0X74, (byte) 0X75
		};
		int pc = 0X0100;
		wrs.setProgramCounter(pc);
		ioBuss.writeDMA(pc, mem);

		int indirectAddress = 0X500;
		byte value;
		clearAllRegs();
		wrs.setDoubleReg(Register.HL, indirectAddress);

		wrs.setDoubleReg(Register.HL, indirectAddress);
		value = (byte) random.nextInt(0X100);
		wrs.setReg(Register.B, value);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV M,B
		assertThat("MOV M,B", value, equalTo(cpuBuss.read(indirectAddress)));

		value = (byte) random.nextInt(0X100);
		wrs.setReg(Register.C, value);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV M,C
		assertThat("MOV M,C", value, equalTo(cpuBuss.read(indirectAddress)));

		value = (byte) random.nextInt(0X100);
		wrs.setReg(Register.D, value);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV M,D
		assertThat("MOV M,D", value, equalTo(cpuBuss.read(indirectAddress)));

		value = (byte) random.nextInt(0X100);
		wrs.setReg(Register.E, value);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV M,E
		assertThat("MOV M,E", value, equalTo(cpuBuss.read(indirectAddress)));

		value = (byte) random.nextInt(0X100);
		wrs.setReg(Register.A, value);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV M,A
		assertThat("MOV M,A", value, equalTo(cpuBuss.read(indirectAddress)));

		wrs.setDoubleReg(Register.HL, indirectAddress);
		value = wrs.getReg(Register.H);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV M,H
		// System.out.printf("value: %02X, reg H:%02X %n",value, wrs.getReg(Register.H));
		assertThat("MOV M,H", value, equalTo(cpuBuss.read(indirectAddress)));

		wrs.setDoubleReg(Register.HL, indirectAddress);
		value = wrs.getReg(Register.L);
		cpu.executeInstruction(wrs.getProgramCounter()); // MOV M,L
		assertThat("MOV M,L", value, equalTo(cpuBuss.read(indirectAddress)));

	}// testRegM

	public byte putRandomValueInMemory(int indirectAddress) {
		byte value = (byte) random.nextInt(0X100); // between 00 and 0XFF
		cpuBuss.write(indirectAddress, value); // put value in memory
		return value;
	}

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }//

}
