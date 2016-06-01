package hardware;

import java.util.HashMap;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 * 
 *          Contains the information about all the registers in the machine. Has both the data registers as well as Program
 *          Counter and Stack pointer.
 * 
 *
 */
public class WorkingRegisterSet {

	private int programCounter;
	private int stackPointer;
	private HashMap<Register, Byte> registers;

	/**
	 * Sets up the Program Counter, Stack Pointer and establishes the data registers based on the class - Register. It
	 * determines the register names and count.
	 */
	public WorkingRegisterSet() {
		registers = new HashMap<Register, Byte>();
		initialize();
	}// Constructor

	/**
	 * Resets the Program Counter, Stack Pointer and data registers
	 */
	public void initialize() {
		registers.clear();
		for (Register register : Register.values()) {
			registers.put(register, (byte) 0);
		}// for
		programCounter = 0000;
		stackPointer = 0X0100; // set to non zero
	}// initialize

	/**
	 * 
	 * @return current Value of Program Counter
	 */
	protected int getProgramCounter() {
		return programCounter;
	}// getProgramCounter

	/**
	 * 
	 * @param programCounter
	 *            Value to place into Program Counter
	 */
	protected void setProgramCounter(int programCounter) {
		this.programCounter = programCounter & 0XFFFF;
	}// setProgramCounter

	/**
	 * 
	 * @return current value of Stack Pointer
	 */
	protected int getStackPointer() {
		return stackPointer;
	}// getStackPointer

	/**
	 * 
	 * @param stackPointer
	 *            Value to be placed into Stack Pointer
	 */
	protected void setStackPointer(int stackPointer) {
		this.stackPointer = stackPointer & 0XFFFF;
	}// setStackPointer

	/**
	 * 
	 * @param hiByte
	 *            Most Significant Byte of Stack pointer
	 * @param loByte
	 *            Least Significant Byte of Stack pointer
	 */
	protected void setStackPointer(byte hiByte, byte loByte) {
		int hi = (int) (hiByte << 8);
		int lo = (int) (loByte & 0X00FF);
		this.stackPointer = (hi + lo) & 0XFFFF;
	}// setStackPointer

	/**
	 * 
	 * @param reg
	 *            8-bit register
	 * @param value
	 *            Value to be put into specified register
	 */
	public void setReg(Register reg, byte value) {
		registers.put(reg, value);
	}// loadReg

	/**
	 * 
	 * @param reg
	 *            8-bit register
	 * @return Value stored in specified register
	 */

	public byte getReg(Register reg) {
		return registers.get(reg);
	}// getReg

	/**
	 * 
	 * @param reg
	 *            16-Bit register
	 * @param value
	 *            Value to be put into specified register
	 */
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

	/**
	 * 
	 * @param reg
	 *            16-Bit register
	 * @return Value stored in specified register
	 */
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
