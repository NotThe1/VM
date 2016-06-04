package codeSupport;

import java.io.Serializable;

public class Instruction implements Serializable {
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

	public Instruction(int opCode, int opCodeSize, int instructionSize,
			ArgumentSignature argumentSignature, ArgumentType arg1,ArgumentType arg2, CCFlags ccFlags,
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

	}

	public int getOpCode() {
		return opCode;
	}

	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}

	public int getOpCodeSize() {
		return opCodeSize;
	}

	public void setOpCodeSize(int opCodeSize) {
		this.opCodeSize = opCodeSize;
	}

	public int getInstructionSize() {
		return instructionSize;
	}

	public void setInstructionSize(int instructionSize) {
		this.instructionSize = instructionSize;
	}

	public ArgumentSignature getArgumentSignature() {
		return argumentSignature;
	}

	public void setArgumentSignature(ArgumentSignature argumentSignature) {
		this.argumentSignature = argumentSignature;
	}

	public ArgumentType  getArg1() {
		return arg1;
	}

	public void setArg2(ArgumentType arg2) {
		this. arg2 = arg2;
	}
	public ArgumentType  getArg2() {
		return arg2;
	}

	public void setArg1(ArgumentType arg1) {
		this. arg1 = arg1;
	}

	public CCFlags getCcFlags() {
		return ccFlags;
	}

	public void setCcFlags(CCFlags ccFlags) {
		this.ccFlags = ccFlags;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}
