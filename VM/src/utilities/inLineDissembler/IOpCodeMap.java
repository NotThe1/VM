package utilities.inLineDissembler;

/**
 * 
 * @author Frank Martyn
 * 
 *         Each opCode's structure is accessed thru this interface
 */
public interface IOpCodeMap {

	/**
	 * 
	 * @param opCode
	 *            - the key(opCode) for the operation
	 * @return the number of bytes occupied by this instruction
	 */
	public int getSize(byte opCode);

	/**
	 * @param opCode
	 *            - the key(opCode) for the operation
	 * @return simple text explanation of the operation
	 */
	public String getFunction(byte opCode);

	/**
	 * 
	 * @param opCode
	 *            - the key(opCode) for the operation
	 * @return the assembler code for this instruction - one with no arguments
	 */
	public String getAssemblerCode(byte opCode);

	/**
	 * 
	 * @param opCode
	 *            - the key(opCode) for the operation
	 * @return the assembler code for this instruction - one with one argument
	 */
	public String getAssemblerCode(byte opCode, byte plusOne);

	/**
	 * 
	 * @param opCode
	 *            - the key(opCode) for the operation
	 * @return the assembler code for this instruction - one with two arguments
	 */
	public String getAssemblerCode(byte opCode, byte plusOne, byte plusTwo);

}// interface IOpCodeMap
