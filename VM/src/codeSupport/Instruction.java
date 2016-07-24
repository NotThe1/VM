package codeSupport;

import java.io.Serializable;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 *
 *          This class manipulate the details of an instruction. The instructions are modeled after the Intel 8080.
 */
public class Instruction implements Serializable, IInstruction {

	private static final long serialVersionUID = 1L;
	private int opCode;
	private int opCodeSize;
	private int instructionSize;
	private ArgumentSignature argumentSignature;
	private ArgumentType arg1;
	private ArgumentType arg2;
	private CCFlags ccFlags;
	private Command command;
	private String description;
	private String function;

	// private static final int INTEL8080 = 1;
	//
	// private int machineType = INTEL8080;

	public Instruction(int opCode, int opCodeSize, int instructionSize,
			ArgumentSignature argumentSignature, ArgumentType arg1, ArgumentType arg2, CCFlags ccFlags,
			Command command, String description, String function) {
		this.setOpCode(opCode);
		this.setOpCodeSize(opCodeSize);
		this.setInstructionSize(instructionSize);
		this.setArgumentSignature(argumentSignature);
		this.setArg1(arg1);
		this.setArg2(arg2);
		this.setCcFlags(ccFlags);
		this.setCommand(command);
		this.setDescription(description);
		this.setFunction(function);

	}// Constructor
	public Instruction(int opCode){
		this.setOpCode(opCode);
	}// Constructor
	
		// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>

	/**
	 * Retrieve machine code that this instruction defines
	 * 
	 * @return opCode defined ( key)
	 */
	@Override
	public int getOpCode() {
		return opCode;
	}// getOpCode

	/**
	 * The machine code that this instruction defines
	 * 
	 * @param opCode
	 *            Code to be defined
	 */
	@Override
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}// setOpCode

	/**
	 * Retrieves the number of bytes the opcode itself uses ( on 8080 machines it is 1)
	 * 
	 * @return the number of bytes the opcode itself uses
	 */
	@Override
	public int getOpCodeSize() {
		return opCodeSize;
	}// getOpCodeSize

	/**
	 * Defines the number of bytes the opcode itself uses ( on 8080 machines it is 1)
	 * 
	 * @param opCodeSize
	 *            the number of bytes the opcode itself uses
	 */
	@Override
	public void setOpCodeSize(int opCodeSize) {
		this.opCodeSize = opCodeSize;
	}// setOpCodeSize

	/**
	 * Returns defines the number of bytes that this opCode uses in memory.
	 * 
	 * @return number of bytes that this opCode uses in memory
	 */
	@Override
	public int getInstructionSize() {
		return instructionSize;
	}// getInstructionSize

	/**
	 * Defines the number of bytes that this opCode uses in memory. It is the sum of the Instruction Size and the number
	 * of bytes used for the arguments
	 * 
	 * @param instructionSize
	 *            number of bytes that this opCode uses in memory
	 */
	@Override
	public void setInstructionSize(int instructionSize) {
		this.instructionSize = instructionSize;
	}// setInstructionSize

	/**
	 * Retrieves the argument Signature of this OpCode. Such as R8D8
	 * 
	 * @return description of this opCodes argument signature
	 */
	@Override
	public ArgumentSignature getArgumentSignature() {
		return argumentSignature;
	}// getArgumentSignature

	/**
	 * Defines the argument Signature of this OpCode. Such as R8D8
	 * 
	 * @param argumentSignature
	 *            description of this opCodes argument signature
	 */
	@Override
	public void setArgumentSignature(ArgumentSignature argumentSignature) {
		this.argumentSignature = argumentSignature;
	}// setArgumentSignature

	/**
	 * Retrieves the type of the first Argument for this opCode (if any)
	 * 
	 * @return The type of this opCodes argument
	 */
	@Override
	public ArgumentType getArg1() {
		return arg1;
	}// ArgumentType

	/**
	 * Defines the type of the first Argument for this opCode (if any)
	 * 
	 * @param arg1
	 *            The type of this opCodes first argument
	 */
	@Override
	public void setArg1(ArgumentType arg1) {
		this.arg1 = arg1;
	}// setArg1

	/**
	 * Defines the type of the second Argument for this opCode (if any)
	 * 
	 * @param arg1
	 *            The type of this opCodes second argument
	 */
	@Override
	public void setArg2(ArgumentType arg2) {
		this.arg2 = arg2;
	}// setArg2

	/**
	 * Retrieves the type of the second Argument for this opCode (if any)
	 * 
	 * @return The type of this opCodes second argument
	 */
	@Override
	public ArgumentType getArg2() {
		return arg2;
	}// getArg2

	/**
	 * Retrieves the Condition Codes that are set/reset by this opCode.
	 * 
	 * @return Condition Codes that will be involved with this opCode
	 */
	@Override
	public CCFlags getCcFlags() {
		return ccFlags;
	}// getCcFlags

	/**
	 * Describes the Condition Codes that are set/reset by this opCode.
	 * 
	 * @param ccFlags
	 *            Condition Codes that will be involved with this opCode
	 */
	@Override
	public void setCcFlags(CCFlags ccFlags) {
		this.ccFlags = ccFlags;
	}// setCcFlags

	/**
	 * Retrieves the command for this opCode. It is the string recognized by the Assembler
	 * 
	 * @return Compiler command
	 */
	@Override
	public Command getCommand() {
		return command;
	}// getCommand

	/**
	 * Sets the command for this opCode. It is the string recognized by the Assembler
	 * 
	 * @param command
	 *            compiler command for this opCode
	 */
	@Override
	public void setCommand(Command command) {
		this.command = command;
	}// setCommand

	/**
	 * Return the verbose description of the operation
	 * 
	 * @return description
	 */
	@Override
	public String getDescription() {
		return description;
	}// getDescription

	/**
	 * Put in a description of the instruction. Such as for Decimal Adjust Accumulator - The 8-bit hexadecimal number in
	 * the accumulator is adjusted to form two 4-bit binary-coded-decimal digits.
	 * 
	 * @param description
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}// setDescription

	/**
	 * Returns a description in a functional type of explanation. Such as (A)<-(B) for the MOV A,B instruction
	 * 
	 * @return the functional description
	 */
	@Override
	public String getFunction() {
		return function;
	}// getFunction

	/**
	 * Put in the description in a functional type of explanation. Such as (A)<-(B) for the MOV A,B instruction
	 * 
	 * @param function
	 *            the functional description
	 */
	@Override
	public void setFunction(String function) {
		this.function = function;
	}// setFunction

}// class Instruction
