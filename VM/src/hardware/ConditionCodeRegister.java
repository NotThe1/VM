package hardware;

// |7|6|5|4|3|2|1|0
// |S|Z|0|A|0|P|1|C
/**
 * 
 * @author Frank Martyn
 * @version 1.0
 *
 *          ConditionCodeRegister - has the system registers
 *          <p>
 *          This class is just a little more than a repository. It mostly has getter and setters for the five boolean
 *          flags Sign,Zero, AUX parity,Parity and Carry. There are Methods for: - Clearing all the codes. - Setting the
 *          all the codes collectively - Setting the Zero, Sign and Parity based on value passed
 * 
 */
public class ConditionCodeRegister implements IConditionCodeRegister {

	private static IConditionCodeRegister conditionCodeRegister;

	private boolean auxilaryCarryFlag = false;
	private boolean carryFlag = false;
	private boolean signFlag = false; // set to most significant bit (7)
	private boolean parityFlag = false; // set to one if even parity, reset if odd
	private boolean zeroFlag = false; // set if result = 0;

	// +++++++++++++++++++++++++++++++++++++++++++++

	public static IConditionCodeRegister getConditionCodeRegister() {
		if (conditionCodeRegister == null) {
			conditionCodeRegister = new ConditionCodeRegister();
		}//
		return conditionCodeRegister;
	}// getConditionCodeRegister

	/**
	 * No work done just makes the object
	 */
	private ConditionCodeRegister() {
	}// Constructor - ConditionCodeRegister()
		// +++++++++++++++++++++++++++++++++++++++++++++

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#getConditionCode()
	 */
	@Override
	public byte getConditionCode() {
		// |7|6|5|4|3|2|1|0
		// |S|Z|0|A|0|P|1|C

		byte conditionCode = (byte) 0B00000010;
		conditionCode = (byte) ((signFlag) ? conditionCode | MASK_SIGN : conditionCode & MASK_SIGN_NOT);
		conditionCode = (byte) ((zeroFlag) ? conditionCode | MASK_ZERO : conditionCode & MASK_ZERO_NOT);
		conditionCode = (byte) ((auxilaryCarryFlag) ? conditionCode | MASK_AUX : conditionCode & MASK_AUX_NOT);
		conditionCode = (byte) ((parityFlag) ? conditionCode | MASK_PARITY : conditionCode & MASK_PARITY_NOT);
		conditionCode = (byte) ((carryFlag) ? conditionCode | MASK_CARRY : conditionCode & MASK_CARRY_NOT);

		return conditionCode;
	}// getConditionCode

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#setConditionCode(byte)
	 */
	@Override
	public void setConditionCode(byte flags) {
		// |7|6|5|4|3|2|1|0
		// |S|Z|0|A|0|P|1|C

		setSignFlag((flags & MASK_SIGN) == MASK_SIGN);
		setZeroFlag((flags & MASK_ZERO) == MASK_ZERO);
		setAuxilaryCarryFlag((flags & MASK_AUX) == MASK_AUX);
		setParityFlag((flags & MASK_PARITY) == MASK_PARITY);
		setCarryFlag((flags & MASK_CARRY) == MASK_CARRY);
	}// setConditionCode

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#clearAllCodes()
	 */
	@Override
	public void clearAllCodes() {
		auxilaryCarryFlag = false;
		carryFlag = false;
		signFlag = false;
		parityFlag = false;
		zeroFlag = false;
	}// clearAllCodes

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#setZSP(byte)
	 */
	@Override
	public void setZSP(byte value) {
		this.setZeroFlag(value == 0);
		this.setSignFlag((value & MASK_SIGN) != 0);
		this.setParityFlag((Integer.bitCount(value) % 2) == 0);
	}// setZSP

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#setZSPclearCYandAUX(byte)
	 */
	@Override
	public void setZSPclearCYandAUX(byte value) {
		clearAllCodes();
		setZSP(value);
	}// setZSPclearCYandAUX

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#isAuxilaryCarryFlagSet()
	 */
	@Override
	public boolean isAuxilaryCarryFlagSet() {
		return auxilaryCarryFlag;
	}// isAuxilaryCarryFlagSet

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#setAuxilaryCarryFlag(boolean)
	 */
	@Override
	public void setAuxilaryCarryFlag(boolean auxilaryCarryFlag) {
		this.auxilaryCarryFlag = auxilaryCarryFlag;
	}// setAuxilaryCarryFlag

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#isCarryFlagSet()
	 */
	@Override
	public boolean isCarryFlagSet() {
		return carryFlag;
	}// isCarryFlagSet

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#setCarryFlag(boolean)
	 */
	@Override
	public void setCarryFlag(boolean carryFlag) {
		this.carryFlag = carryFlag;
	}// setCarryFlag

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#isSignFlagSet()
	 */
	@Override
	public boolean isSignFlagSet() {
		return signFlag;
	}// isSignFlagSet

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#setSignFlag(boolean)
	 */
	@Override
	public void setSignFlag(boolean signFlag) {
		this.signFlag = signFlag;
	}// setSignFlag

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#isParityFlagSet()
	 */
	@Override
	public boolean isParityFlagSet() {
		return parityFlag;
	}// isParityFlagSet

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#setParityFlag(boolean)
	 */
	@Override
	public void setParityFlag(boolean parityFlag) {
		this.parityFlag = parityFlag;
	}// setParityFlag

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#isZeroFlagSet()
	 */
	@Override
	public boolean isZeroFlagSet() {
		return zeroFlag;
	}// isZeroFlagSet

	/*
	 * (non-Javadoc)
	 * 
	 * @see hardware.IConditionCodeRegister#setZeroFlag(boolean)
	 */
	@Override
	public void setZeroFlag(boolean zeroFlag) {
		this.zeroFlag = zeroFlag;
	}// setZeroFlag

	private static final byte MASK_SIGN = (byte) 0B10000000;
	private static final byte MASK_SIGN_NOT = (byte) 0B01111111;
	private static final byte MASK_ZERO = (byte) 0B01000000;
	private static final byte MASK_ZERO_NOT = (byte) 0B10111111;
	private static final byte MASK_AUX = (byte) 0B00010000;
	private static final byte MASK_AUX_NOT = (byte) 0B11101111;
	private static final byte MASK_PARITY = (byte) 0B00000100;
	private static final byte MASK_PARITY_NOT = (byte) 0B11111011;
	private static final byte MASK_CARRY = (byte) 0B00000001;
	private static final byte MASK_CARRY_NOT = (byte) 0B11111110;

}// ConditionCodeRegister
