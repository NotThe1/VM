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
	
	public void updateAll();
	/**
	 * all flags (Sign,Zero,Aux Carry, Parity & Carry) will be updated to current values.
	 */
	public void updateFlags();
	
	/**
	 * all registers (A,B,C,D,E,H,L & (M) ) will be updated to current value
	 */
	public void updateRegisters();
	
	/**
	 *  The Stack Pointer will be updated to the current value
	 */
	public void updateStackPointer();
	
	/**
	 *  The Program Counter will be updated to the current value
	 */
	public void updateProgramCounter();
	
	/**
	 * the Sign flag will be updated to current value
	 */
	public void updateSignFlag();
	
	/**
	 * the Zero flag will be updated to current value
	 */
	public void updateZeroFlag();
	
	/**
	 * the Aux Carry flag will be updated to current value
	 */
	public void updateAuxCarryFlag();
	
	/**
	 * the Parity flag will be updated to current value
	 */
	public void updateParityFlag();
	
	/**
	 * the Carry flag will be updated to current value
	 */
	public void updateCarryFlag();
	
	/**
	 *  The A Register (Accumulator) will be updated to the current value
	 */
	public void updateRegisterA();
	
	/**
	 *  The B Register will be updated to the current value
	 */
	public void updateRegisterB();
	
	/**
	 *  The C Register will be updated to the current value
	 */
	public void updateRegisterC();
	
	/**
	 *  The D Register will be updated to the current value
	 */
	public void updateRegisterD();
	
	/**
	 *  The E Register will be updated to the current value
	 */
	public void updateRegisterE();
	
	/**
	 *  The H Register will be updated to the current value
	 */
	public void updateRegisterH();
	
	/**
	 *  The L Register will be updated to the current value
	 */
	public void updateRegisterL();
	
	/**
	 *  The M Register (value pointed at by the contenets of HL) will be updated to the current value
	 */
	public void updateRegisterM();


}// interface StateDisplay
