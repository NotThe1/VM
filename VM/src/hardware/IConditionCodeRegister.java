package hardware;

public interface IConditionCodeRegister {

	/**
	 * Returns the state of all the conditions
	 * 
	 * @return - the state of the condition codes as bits: |7|6|5|4|3|2|1|0 |S|Z|0|A|0|P|1|C
	 */
	public byte getConditionCode();// getConditionCode

	/**
	 * Sets the state of all the conditions
	 * 
	 * @param flags
	 *            - the condition code as bits:  |7|6|5|4|3|2|1|0 |S|Z|0|A|0|P|1|C
	 */
	public void setConditionCode(byte flags);// setConditionCode

	/**
	 * Resets all the conditions
	 */
	public void clearAllCodes();// clearAllCodes

	/**
	 * Sets the Zero, Sign, and Parity conditions based on passed value
	 * 
	 * @param value
	 *            - byte to be analyzed
	 */
	public void setZSP(byte value);// setZSP

	/**
	 * Clears the Carry and Aux Carry, and sets Reset based on passed value
	 * 
	 * @param value
	 *            - byte to be analyzed
	 */
	public void setZSPclearCYandAUX(byte value);// setZSPclearCYandAUX

	/**
	 * 
	 * @return the auxilaryCarryFlag
	 */
	public boolean isAuxilaryCarryFlagSet();// isAuxilaryCarryFlagSet

	/**
	 * 
	 * @param auxilaryCarryFlag
	 *            the auxilaryCarryFlag to set
	 */
	public void setAuxilaryCarryFlag(boolean auxilaryCarryFlag);// setAuxilaryCarryFlag

	/**
	 * 
	 * @return the carryFlag
	 */
	public boolean isCarryFlagSet();// isCarryFlagSet

	/**
	 * 
	 * @param carryFlag
	 *            the carryFlag to set
	 */
	public void setCarryFlag(boolean carryFlag);// setCarryFlag

	/**
	 * 
	 * @return the signFlag
	 */
	public boolean isSignFlagSet();// isSignFlagSet

	/**
	 * 
	 * @param signFlag
	 *            the signFlag to set
	 */
	public void setSignFlag(boolean signFlag);// setSignFlag

	/**
	 * 
	 * @return the parityFlag
	 */
	public boolean isParityFlagSet();// isParityFlagSet

	/**
	 * 
	 * @param parityFlag
	 *            the parityFlag to set
	 */
	public void setParityFlag(boolean parityFlag);// setParityFlag

	/**
	 * 
	 * @return the zeroFlag
	 */
	public boolean isZeroFlagSet();// isZeroFlagSet

	/**
	 * 
	 * @param zeroFlag
	 *            the zeroFlag to set
	 */
	public void setZeroFlag(boolean zeroFlag);// setZeroFlag

}