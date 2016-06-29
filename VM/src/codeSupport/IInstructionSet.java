package codeSupport;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 *
 */
public interface IInstructionSet {

	/**
	 * creates a new instruction with the following attributes, if there already is an instruction with the same opCode
	 * it will remain and the new one will be discarded
	 * 
	 * @param opCode
	 *            - the hex value of the instruction.
	 * @param opCodeSize
	 *            - the number of bytes the opcode itself occupies
	 * @param instructionSize
	 *            - the number of bytes the opcode and all its arguments occupy
	 * @param argumentSignature
	 *            - description of the opCode's arguments
	 * @param arg1
	 *            - the type of the first argument
	 * @param arg2
	 *            - the type of the second argument
	 * @param ccFlags
	 *            - identifies what Condition Codes are set/reset
	 * @param command
	 *            mnemonic understood by an 8080 Assembler
	 * @param description
	 *            - brief description of the command's action
	 * @param function
	 *            - quick functional description - (A)<-(B) for MOV A,B
	 */
	public void addInstruction(int opCode, int opCodeSize, int instructionSize,
			ArgumentSignature argumentSignature, ArgumentType arg1, ArgumentType arg2, CCFlags ccFlags,
			Command command, String description, String function);

	/**
	 * 
	 * creates a new instruction with the no attributes, if there already is an instruction with the same opCode it will
	 * remain and the new one will be discarded
	 * 
	 * @param opCode
	 *            - the hex value of the instruction.
	 */
	public void addInstruction(int opCode);

	/**
	 * return the Instruction that has the given opCode
	 * 
	 * @param opCode
	 *            Key to the Instruction sought
	 * @return Instruction with the given opCode
	 */
	public Instruction getInstruction(int opCode);

	/**
	 * the instruction in the set will be replaced by the passed instruction. If it is a new instruction it will be
	 * inserted.
	 * 
	 * @param instruction
	 *            instruction to be updated
	 */
	public void updateInstruction(Instruction instruction);

	/**
	 * if the instruction is found in the set it will be removed
	 * 
	 * @param opCode
	 *            TThe key for the instruction to be removed
	 */
	public void removeInstruction(int opCode);

	/**
	 * if the instruction is found in the set it will be removed
	 * 
	 * @param instruction
	 *            instruction to be removed
	 */
	public void removeInstruction(Instruction instruction);

	/**
	 * clears all the instructions in the instruction set
	 */
	public void clear();

	/**
	 * returns the number of instructions in the set
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Returns true if this set contains an instruction for the specified opCode.
	 * 
	 * @param opCode
	 *            instruction in question
	 * @return
	 */
	public boolean contains(int opCode);

	
}// interface IInstructionSet

