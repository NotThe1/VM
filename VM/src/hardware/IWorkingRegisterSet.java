package hardware;

public interface IWorkingRegisterSet {

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Resets the Program Counter, Stack Pointer and data registers
	 */
	public abstract void initialize();// initialize

	/**
	 * 
	 * @return current Value of Program Counter
	 */
	public abstract int getProgramCounter();// getProgramCounter

	/**
	 * 
	 * @param programCounter
	 *            Value to place into Program Counter
	 */
	public abstract void setProgramCounter(int programCounter);// setProgramCounter

	/**
	 * 
	 * @return current value of Stack Pointer
	 */
	public abstract int getStackPointer();// getStackPointer

	/**
	 * 
	 * @param stackPointer
	 *            Value to be placed into Stack Pointer
	 */
	public abstract void setStackPointer(int stackPointer);// setStackPointer

	/**
	 * 
	 * @param hiByte
	 *            Most Significant Byte of Stack pointer
	 * @param loByte
	 *            Least Significant Byte of Stack pointer
	 */
	public abstract void setStackPointer(byte hiByte, byte loByte);// setStackPointer

	/**
	 * 
	 * @param reg
	 *            8-bit register
	 * @param value
	 *            Value to be put into specified register
	 */
	public abstract void setReg(Register reg, byte value);// loadReg

	/**
	 * 
	 * @param reg
	 *            8-bit register
	 * @return Value stored in specified register
	 */

	public abstract byte getReg(Register reg);// getReg

	/**
	 * 
	 * @param reg
	 *            16-Bit register
	 * @param value
	 *            Value to be put into specified register
	 */
	public abstract void setDoubleReg(Register reg, int value);// setDoubleReg

	/**
	 * 
	 * @param reg
	 *            16-Bit register
	 * @return Value stored in specified register
	 */
	public abstract int getDoubleReg(Register reg);// getDoubleReg

}