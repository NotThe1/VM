package hardware;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class WorkingRegisterSetTest {
		static IWorkingRegisterSet wrs;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		wrs = WorkingRegisterSet.getWorkingRegisterSet();
	}//setUpBeforeClass


	@Test
	public void testPCandSP() {
		int value0000 = 0;
		int valueFFFF = 0XFFFF;
		int valueA5A5 = 0XA5A5;
		int value0100 = 0X0100;
		
		wrs = WorkingRegisterSet.getWorkingRegisterSet();
		// Stack Pointer
		assertThat("SP Initial", value0100, equalTo(wrs.getStackPointer()));
		wrs.setStackPointer(valueFFFF);
		assertThat("SP FFFF", valueFFFF, equalTo(wrs.getStackPointer()));
		wrs.setStackPointer(valueA5A5);
		assertThat("SP A5A5", valueA5A5, equalTo(wrs.getStackPointer()));
		// ProgramCounter
		assertThat("PC Initial", value0000, equalTo(wrs.getProgramCounter()));
		wrs.setProgramCounter(valueFFFF);
		assertThat("PC FFFF", valueFFFF, equalTo(wrs.getProgramCounter()));
		wrs.setProgramCounter(valueA5A5);
		assertThat("PC A5A5", valueA5A5, equalTo(wrs.getProgramCounter()));
		wrs = null;
	}//testPCandSP

	@Test
	public void testSingleByteRegisters() {
		wrs = WorkingRegisterSet.getWorkingRegisterSet();

		byte[] values = new byte[100];
		Random random = new Random();
		random.nextBytes(values);

		ArrayList<Register> byteRegisters =
				new ArrayList<Register>(Arrays.asList(Register.A, Register.B, Register.C,
						Register.D, Register.E, Register.H, Register.L));
		for (int v = 0; v < values.length; v++) {
			for (int r = 0; r < byteRegisters.size(); r++) {

				wrs.setReg(byteRegisters.get(r), values[v]);
				assertThat("Single Byte read and Write", values[v], equalTo(wrs.getReg(byteRegisters.get(r))));

			}// for registers

		}// for values

		wrs = null;
	}//testSingleByteRegisters

	@Test
	public void testDoubleByteRegisters() {
		wrs = WorkingRegisterSet.getWorkingRegisterSet();
		
		int testCount = 100;
		byte[] hiValues = new byte[testCount];
		byte[] loValues = new byte[testCount];
		byte hiValue,loValue;
		int wordValue;
		Random random = new Random();
		random.nextBytes(hiValues);
		random.nextBytes(loValues);
		
		for ( int i = 0; i < testCount;i++){
			hiValue = hiValues[i];
			loValue = loValues[i];
			wordValue = (((hiValue << 8) & 0XFF00) + (loValue & 0X00FF)) & 0XFFFF;
			//BC
			wrs.setDoubleReg(Register.BC, wordValue);
			assertThat("Word Reg BC read Word",wordValue,equalTo(wrs.getDoubleReg(Register.BC)));
			assertThat("Word Reg BC read byte 1",hiValue,equalTo(wrs.getReg(Register.B)));
			assertThat("Word Reg BC read byte 2",loValue,equalTo(wrs.getReg(Register.C)));
			//DE
			wrs.setDoubleReg(Register.DE, wordValue);
			assertThat("Word Reg DE read Word",wordValue,equalTo(wrs.getDoubleReg(Register.DE)));
			assertThat("Word Reg DE read byte 1",hiValue,equalTo(wrs.getReg(Register.D)));
			assertThat("Word Reg DE read byte 2",loValue,equalTo(wrs.getReg(Register.E)));
			//HL
			wrs.setDoubleReg(Register.HL, wordValue);
			assertThat("Word Reg HL read Word",wordValue,equalTo(wrs.getDoubleReg(Register.HL)));
			assertThat("Word Reg HL read byte 1",hiValue,equalTo(wrs.getReg(Register.H)));
			assertThat("Word Reg HL read byte 2",loValue,equalTo(wrs.getReg(Register.L)));
			//SP
			wrs.setDoubleReg(Register.SP, wordValue);
			assertThat("Word Reg SP read Word",wordValue,equalTo(wrs.getDoubleReg(Register.SP)));
			assertThat("Word Reg SP read SP",wordValue,equalTo(wrs.getStackPointer()));
			//PC
			wrs.setDoubleReg(Register.PC, wordValue);
			assertThat("Word Reg PC read Word",wordValue,equalTo(wrs.getDoubleReg(Register.PC)));
			assertThat("Word Reg PC read SP",wordValue,equalTo(wrs.getProgramCounter()));
		}//for
		// Initialize
		byte byte00 = (byte)0X00;
		
		wrs.initialize();
		assertThat("Word Reg - initialize SP",0X0100,equalTo(wrs.getDoubleReg(Register.SP)));
		assertThat("Word Reg - initialize PC",0,equalTo(wrs.getDoubleReg(Register.PC)));
		
		assertThat("Word Reg - initialize BC",0,equalTo(wrs.getDoubleReg(Register.BC)));
		assertThat("Word Reg - initialize DE",0,equalTo(wrs.getDoubleReg(Register.DE)));
		assertThat("Word Reg - initialize HL",0,equalTo(wrs.getDoubleReg(Register.HL)));
		
		assertThat("Word Reg - initialize B)",byte00,equalTo(wrs.getReg(Register.B)));
		assertThat("Word Reg - initialize C)",byte00,equalTo(wrs.getReg(Register.C)));
		assertThat("Word Reg - initialize D)",byte00,equalTo(wrs.getReg(Register.D)));
		assertThat("Word Reg - initialize E)",byte00,equalTo(wrs.getReg(Register.E)));
		assertThat("Word Reg - initialize H)",byte00,equalTo(wrs.getReg(Register.H)));
		assertThat("Word Reg - initialize L)",byte00,equalTo(wrs.getReg(Register.L)));
			
	}//testDoubleByteRegisters



}// class WorkingRegisterSetTest
