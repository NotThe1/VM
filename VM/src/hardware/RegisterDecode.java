package hardware;

/**
 * 
 * @author Frank Martyn
 * @version 1.0 
 *
 *          This class is a class that will return the correct Register/Condition for an opcode. 
 */

public class RegisterDecode {
	/**
	 *  word registers DD
	 */
	private static final Register[] wordRegistersStd = { Register.BC, Register.DE, Register.HL, Register.SP};
	/**
	 *  word registers QQ
	 */
	private static final Register[] wordRegistersAlt = { Register.BC, Register.DE, Register.HL, Register.AF};
	/**
	 *  byte registers RRR
	 */
	private static final Register[] singleRgisters = { Register.B, Register.C, Register.D, Register.E,
			Register.H, Register.L, Register.M, Register.A };
	/**
	 * Conditions CCC
	 */
	private static final ConditionFlag[] conditionFlags = {ConditionFlag.NZ,ConditionFlag.Z,
		ConditionFlag.NC,ConditionFlag.C,ConditionFlag.PO,ConditionFlag.PE,
		ConditionFlag.P,ConditionFlag.M	};
	/**
	 * The method will return the condition referenced by bits 3-4-5
	 * 
	 * @param opCode
	 *            value to be interpreted
	 * @return condition referenced by the opCode in bits 3-4-5
	 */
	public static ConditionFlag getCondition(byte opCode) {
		return conditionFlags[getIndex345(opCode)];
	}// getCondition
	
	/**
	 * The method will return the single register referenced by bits 3-4-5
	 * 
	 * @param opCode
	 *            value to be interpreted
	 * @return Single register referenced by the opCode in bits 3-4-5
	 */
	public static Register getHighRegister(byte opCode) {
		return singleRgisters[getIndex345(opCode)];
	}// getHighRegister

	/**
	 * The method will return the single register referenced by bits 0-1-2
	 * 
	 * @param opCode
	 *            value to be interpreted
	 * @return Single register referenced by the opCode in bits 0-1-2
	 */
	public static Register getLowRegister(byte opCode) {
		return singleRgisters[getIndex012(opCode)];
	}// getLowRegister

	/**
	 * This Method returns the a Register pair (it only differs from getRegisterPairAlt in that it returns Register.SP
	 * where this method returns Register.AF for the same input range)
	 * 
	 * @param opCode
	 *            value to be interpreted
	 * @return RegisterPair that is referenced by this opCode
	 */
	public static Register getRegisterPairStd(byte opCode) {
		return wordRegistersStd[getIndex45(opCode)];
	}// getRegisterPair

	/**
	 * This Method returns the alternate Register pair (it only differs from getRegisterPair in that it returns
	 * Register.AF where this method returns Register.SP for the same input range)
	 * 
	 * @param opCode
	 *            value to be interpreted
	 * @return RegisterPair that is referenced by this opCode
	 */
	public static Register getRegisterPairAlt(byte opCode) {
		return wordRegistersAlt[getIndex45(opCode)];
	}// getRegisterPairAlt

	/**
	 * returns the value of the bit grouping of bits 4 & 5 00-NN0-000
	 * 
	 * @param opCode
	 *            opcode to be examined
	 * @return value of bits 4-5
	 */
	private static int getIndex45(byte opCode) {
		return (opCode & 0B00110000) >> 4;
	}// getIndex45

	/**
	 * returns the value of the bit grouping of bits 3, 4 & 5 00-NNN-000
	 * 
	 * @param opCode
	 *            opcode to be examined
	 * @return value of bits 3-4-5
	 */
	private static int getIndex345(byte opCode) {
		return (opCode & 0B00111000) >> 3;
	}// getIndex345

	/**
	 * returns the value of the bit grouping of bits 0, 1 & 2 00-000-NNN
	 * 
	 * @param opCode
	 *            opcode to be examined
	 * @return value of bits 0-1-2
	 */
	private static int getIndex012(byte opCode) {
		return (opCode & 0B00000111);
	}// getIndex012

}// class RegisterDecode
