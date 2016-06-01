package hardware;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 *
 *          ArithmeticUnit - does the computation for the machine
 *          <p>
 *          This class does all computation for the virtual machine. It follows the rules laid out for the Intel 8080 as
 *          it states how and when those operations are:
 *          <p>
 *          1 affected by the condition codes
 *          <p>
 *          2 how those operations set/reset those same condition codes.
 *          <p>
 *          It is an 8bit engine with some 16 bit operations.
 * 
 * 
 * 
 * 
 */
public class ArithmeticUnit {
	private ConditionCodeRegister ccr;

	/**
	 * 
	 * @param ccr
	 *            requires the system Condition Code Register
	 */
	public ArithmeticUnit(ConditionCodeRegister ccr) {
		this.ccr = ccr;
	}// Constructor

	/**
	 * will give the appropriate carry condition based on the mask. Masks : CARRY_AUX,CARRY_BYTE,CARRY_WORD
	 * 
	 * @param operand1
	 *            first number to use
	 * @param operand2
	 *            second number
	 * @param carryMask
	 *            type of carry desired to be calculated
	 * @return state of carry desired
	 */
	private boolean carryOut(int operand1, int operand2, int carryMask) {
		int result = (operand1 & carryMask) + (operand2 & carryMask);
		return (result > carryMask) ? true : false;
	}// carryOut

	/**
	 * add the two provided numbers as Bytes or Words.
	 * <p>
	 * The operandSize (8/16) controls the type of add as well as what Carry flags are calculated and set/reset.Only a
	 * Byte add will calculate the AUX Carry. The Carry flag is always calculated.
	 * 
	 * @param operand1
	 *            first value
	 * @param operand2
	 *            second value
	 * @param operandMask
	 *            either MASK_BYTE or MASK_WORD to control the type of add
	 * @return
	 */
	private int add(int operand1, int operand2, int operandMask) {
		int carryMask;
		if (operandMask == MASK_BYTE) {// need to handle Auxiliary Carry
			boolean auxilaryCarryFlag = carryOut(operand1, operand2, CARRY_AUX);
			ccr.setAuxilaryCarryFlag(auxilaryCarryFlag);
			carryMask = CARRY_BYTE; // two nibbles
		} else {
			carryMask = CARRY_WORD; // two bytes
		}// if
		boolean carryFlag = carryOut(operand1, operand2, carryMask);
		ccr.setCarryFlag(carryFlag);
		return (operand1 + operand2) & operandMask;
	}// add

	/**
	 * add two bytes
	 * <p>
	 * Add the two supplied bytes and set/resets All condition flags
	 * 
	 * @param operand1
	 *            First value
	 * @param operand2
	 *            Second value
	 * @return 8 bit sum
	 */
	public byte add(byte operand1, byte operand2) {
		byte result = (byte) add((int) operand1, (int) operand2, MASK_BYTE);
		ccr.setZSP(result);
		return result;
	}// add(byte operand1, byte operand2)

	/**
	 * Add two 16 bit words
	 * <p>
	 * This operation set/resets the Carry flag, but does not affect any other flags
	 * @param operand1 First Value
	 * @param operand2 Other Value
	 * @return 16 bit sum of the two values
	 */
	public int add(int operand1, int operand2) { // add words
		return add(operand1, operand2, MASK_WORD);
	}// add(short operand1, short operand2)
	
	
//<><><><><><><><><><><><><><><><><><><><><><><><>
	private static final int CARRY_AUX = 0X000F;
	private static final int CARRY_BYTE = 0X00FF;
	private static final int CARRY_WORD = 0XFFFF;
	
	private static final int MASK_BYTE = 0X00FF;
	private static final int MASK_WORD = 0XFFFF;

}
