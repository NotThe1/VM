package hardware;

/**
 * Panel that visually represents the state of the machine. It displays all the general purpose registers
 * (A,B,C,D,E,H,L). It also shows the contents of the memory location pointed to by the contents of HL - (M).
 * The condition code flags are displayed. Also shown are the StackPointer and the Program Counter.
 *   This class is a View for the machine.
 * 
 * @author Frank Martyn
 * @version 1.0
 *
 */
public interface IStateDisplay {
	/**
	 * all registers and flags will be updated to current values.
	 */
	
	public void updateDisplayAll();
	/**
	 * all flags (Sign,Zero,Aux Carry, Parity & Carry) will be updated to current values.
	 */
	public void updateDisplayAllFlags();
	
	/**
	 * all registers (A,B,C,D,E,H,L & (M) ) will be updated to current value
	 */
	public void updateDisplayAllRegisters();
	
	/**
	 *  The Stack Pointer will be updated to the current value
	 */
	public void updateDisplayStackPointer();
	
	/**
	 *  The Program Counter will be updated to the current value
	 */
	public void updateDisplayProgramCounter();
	
	/**
	 * the Sign flag will be updated to current value
	 */
	public void updateDisplaySignFlag();
	
	/**
	 * the Zero flag will be updated to current value
	 */
	public void updateDisplayZeroFlag();
	
	/**
	 * the Aux Carry flag will be updated to current value
	 */
	public void updateDisplayAuxCarryFlag();
	
	/**
	 * the Parity flag will be updated to current value
	 */
	public void updateDisplayParityFlag();
	
	/**
	 * the Carry flag will be updated to current value
	 */
	public void updateDisplayCarryFlag();
	
	/**
	 *  The Accumulator (A Register) will be updated to the current value
	 */
	public void updateDisplayAcc();
	/**
	 *  The A Register (Accumulator) will be updated to the current value
	 */
	public void updateDisplayRegisterA();
	
	/**
	 *  The B Register will be updated to the current value
	 */
	public void updateDisplayRegisterB();
	
	/**
	 *  The C Register will be updated to the current value
	 */
	public void updateDisplayRegisterC();
	
	/**
	 *  The D Register will be updated to the current value
	 */
	public void updateDisplayRegisterD();
	
	/**
	 *  The E Register will be updated to the current value
	 */
	public void updateDisplayRegisterE();
	
	/**
	 *  The H Register will be updated to the current value
	 */
	public void updateDisplayRegisterH();
	
	/**
	 *  The L Register will be updated to the current value
	 */
	public void updateDisplayRegisterL();
	


}// interface StateDisplay
