package codeSupport;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 *
 *          This interface describes the activities to manipulate the details of an instruction. The instructions are
 *          modeled after the Intel 8080. They has these attributes:
 *          <p>
 *          <p>
 *          opCode int - the hex value of the instruction.
 *          <p>
 *          OpCodeSize int - the number of bytes the opcode itself occupies (1 for 8080)
 *          <p>
 *          instructionSize int - the number of bytes the opcode and all its arguments occupy
 *          <p>
 *          argumentSignature ArgumentSignature - description of the opCode's arguments
 *          <p>
 *          arg1 ArgumentType - the type of the first argument
 *          <p>
 *          arg2 ArgumentType - the type of the second argument
 *          <p>
 *          ccFlags CCFlags - identifies what Condition Codes are set/reset
 *          <p>
 *          command Command - mnemonic understood by an 8080 Assembler
 *          <p>
 *          description String - brief description of the command's action
 *          <p>
 *          function String - quick functional description - (A)<-(B) for MOV A,B
 */
public interface IInstruction {

	/**
	 * Retrieve machine code that this instruction defines
	 * 
	 * @return opCode defined ( key)
	 */
	public int getOpCode();

	/**
	 * The machine code that this instruction defines
	 * 
	 * @param opCode
	 *            Code to be defined
	 */
	public void setOpCode(int opCode);

	/**
	 * Retrieves the number of bytes the opcode itself uses ( on 8080 machines it is 1)
	 * 
	 * @return the number of bytes the opcode itself uses
	 */
	public int getOpCodeSize();

	/**
	 * Defines the number of bytes the opcode itself uses ( on 8080 machines it is 1)
	 * 
	 * @param opCodeSize
	 *            the number of bytes the opcode itself uses
	 */
	public void setOpCodeSize(int opCodeSize);

	/**
	 * Returns defines the number of bytes that this opCode uses in memory.
	 * 
	 * @return number of bytes that this opCode uses in memory
	 */
	public int getInstructionSize();

	/**
	 * Defines the number of bytes that this opCode uses in memory. It is the sum of the Instruction Size and the number
	 * of bytes used for the arguments
	 * 
	 * @param instructionSize
	 *            number of bytes that this opCode uses in memory
	 */
	public void setInstructionSize(int instructionSize);

	/**
	 * Retrieves the argument Signature of this OpCode. Such as R8D8
	 * 
	 * @return description of this opCodes argument signature
	 */
	public ArgumentSignature getArgumentSignature();

	/**
	 * Defines the argument Signature of this OpCode. Such as R8D8
	 * 
	 * @param argumentSignature
	 *            description of this opCodes argument signature
	 */
	public void setArgumentSignature(ArgumentSignature argumentSignature);

	/**
	 * Defines the type of the first Argument for this opCode (if any)
	 * 
	 * @param arg1
	 *            The type of this opCodes first argument
	 */
	public void setArg1(ArgumentType arg1);

	/**
	 * Retrieves the type of the first Argument for this opCode (if any)
	 * 
	 * @return The type of this opCodes argument
	 */
	public ArgumentType getArg1();

	/**
	 * Defines the type of the second Argument for this opCode (if any)
	 * 
	 * @param arg1
	 *            The type of this opCodes second argument
	 */
	public void setArg2(ArgumentType arg2);

	/**
	 * Retrieves the type of the second Argument for this opCode (if any)
	 * 
	 * @return The type of this opCodes second argument
	 */
	public ArgumentType getArg2();

	/**
	 * Retrieves the Condition Codes that are set/reset by this opCode.
	 * 
	 * @return Condition Codes that will be involved with this opCode
	 */
	public CCFlags getCcFlags();

	/**
	 * Describes the Condition Codes that are set/reset by this opCode.
	 * 
	 * @param ccFlags
	 *            Condition Codes that will be involved with this opCode
	 */
	public void setCcFlags(CCFlags ccFlags);

	/**
	 * Retrieves the command for this opCode. It is the string recognized by the Assembler
	 * 
	 * @return Compiler command
	 */
	public Command getCommand();

	/**
	 * Sets the command for this opCode. It is the string recognized by the Assembler
	 * 
	 * @param command
	 *            compiler command for this opCode
	 */
	public void setCommand(Command command);

	/**
	 * Return the verbose description of the operation
	 * 
	 * @return description
	 */
	public String getDescription();

	/**
	 * Put in a description of the instruction. Such as for Decimal Adjust Accumulator - The 8-bit hexadecimal number in
	 * the accumulator is adjusted to form two 4-bit binary-coded-decimal digits.
	 * 
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * Returns a description in a functional type of explanation. Such as (A)<-(B) for the MOV A,B instruction
	 * 
	 * @return the functional description
	 */
	public String getFunction();

	/**
	 * Put in the description in a functional type of explanation. Such as (A)<-(B) for the MOV A,B instruction
	 * 
	 * @param function
	 *            the functional description
	 */
	public void setFunction(String function);

}