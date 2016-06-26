package hardware;

import java.util.HashMap;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 * 
 *          Contains the information about all the registers in the machine. Has both the data registers as well as Program
 *          Counter and Stack pointer.
 * 	<p> This class is a singleton
 *
 */
public class WorkingRegisterSet implements IWorkingRegisterSet {
	
	private static IWorkingRegisterSet workingRegisterSet;

	private int programCounter;
	private int stackPointer;
	private HashMap<Register, Byte> registers;
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#getWorkingRegisterSet()
	 */
//	@Override
	public static IWorkingRegisterSet getWorkingRegisterSet(){
		if (workingRegisterSet==null){
			workingRegisterSet = new WorkingRegisterSet();
		}//if
		return workingRegisterSet;
	}//getWorkingRegisterSet

	/**
	 * Sets up the Program Counter, Stack Pointer and establishes the data registers based on the class - Register. It
	 * determines the register names and count.
	 */
	private WorkingRegisterSet() {
		registers = new HashMap<Register, Byte>();
		initialize();
	}// Constructor
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	
	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#initialize()
	 */
	@Override
	public void initialize() {
		registers.clear();
		for (Register register : Register.values()) {
			registers.put(register, (byte) 0);
		}// for
		programCounter = 0000;
		stackPointer = 0X0100; // set to non zero
	}// initialize

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#getProgramCounter()
	 */
	@Override
	public int getProgramCounter() {
		return programCounter;
	}// getProgramCounter

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#setProgramCounter(int)
	 */
	@Override
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter & 0XFFFF;
	}// setProgramCounter

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#getStackPointer()
	 */
	@Override
	public int getStackPointer() {
		return stackPointer;
	}// getStackPointer

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#setStackPointer(int)
	 */
	@Override
	public void setStackPointer(int stackPointer) {
		this.stackPointer = stackPointer & 0XFFFF;
	}// setStackPointer

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#setStackPointer(byte, byte)
	 */
	@Override
	public void setStackPointer(byte hiByte, byte loByte) {
		int hi = (int) (hiByte << 8);
		int lo = (int) (loByte & 0X00FF);
		this.stackPointer = (hi + lo) & 0XFFFF;
	}// setStackPointer

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#setReg(hardware.Register, byte)
	 */
	@Override
	public void setReg(Register reg, byte value) {
		registers.put(reg, value);
	}// loadReg

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#getReg(hardware.Register)
	 */

	@Override
	public byte getReg(Register reg) {
		return registers.get(reg);
	}// getReg

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#setDoubleReg(hardware.Register, int)
	 */
	@Override
	public void setDoubleReg(Register reg, int value) {
		int hi = value & 0XFF00;
		byte hiByte = (byte) ((hi >> 8) & 0XFF);
		byte loByte = (byte) (value & 0XFF);

		switch (reg) {
		case BC:
			setReg(Register.B, hiByte);
			setReg(Register.C, loByte);
			break;
		case DE:
			setReg(Register.D, hiByte);
			setReg(Register.E, loByte);
			break;
		case HL:
			setReg(Register.H, hiByte);
			setReg(Register.L, loByte);
			break;
		case SP:
			setStackPointer(value);
			break;
		case PC:
			setProgramCounter(value);
			break;
		default:
			// just fall thru
		}// switch

		return;
	}// setDoubleReg

	/* (non-Javadoc)
	 * @see hardware.IWorkingRegisterSet#getDoubleReg(hardware.Register)
	 */
	@Override
	public int getDoubleReg(Register reg) {
		byte hi = 0;
		byte lo = 0;

		switch (reg) {
		case BC:
			hi = this.getReg(Register.B);
			lo = this.getReg(Register.C);
			break;
		case DE:
			hi = this.getReg(Register.D);
			lo = this.getReg(Register.E);
			break;
		case HL:
		case M:
			hi = this.getReg(Register.H);
			lo = this.getReg(Register.L);
			break;
		case SP:
			return this.getStackPointer();
			// exits here for SP
		case PC:
			return this.getProgramCounter();
			// exits here for PC
		default:
			// just use 0;
		}// switch
		int result = (((hi << 8) + (lo & 0XFF)) & 0XFFFF);

		return result;
	}// getDoubleReg

}// class WorkingRegisterSet
