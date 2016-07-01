package hardware;

import memory.Core;
import memory.CpuBuss;

/**
 * This class is responsible for the execution of the instruction for the system
 * 
 * @author Frank Martyn
 * @version 1.0
 *
 */

public class CentralProcessingUnit {
	CpuBuss cpuBuss;
	ConditionCodeRegister ccr;
	WorkingRegisterSet wrs;
	ArithmeticUnit au;
	ErrorType error;

	public CentralProcessingUnit() {
		this(CpuBuss.getCpuBuss(),
				WorkingRegisterSet.getWorkingRegisterSet(),
				ArithmeticUnit.getArithmeticUnit(),
				ConditionCodeRegister.getConditionCodeRegister());

	}// Constructor

	public CentralProcessingUnit(CpuBuss cpuBuss,
			WorkingRegisterSet wrs,
			ArithmeticUnit au,
			ConditionCodeRegister ccr) {
		this.cpuBuss = cpuBuss;
		this.wrs = wrs;
		this.au = au;

	}// Constructor

	public boolean startInstruction() {
		executeInstruction(cpuBuss.read(wrs.getProgramCounter()));
		return isError();
	}// startInstruction

	public void executeInstruction(int currentAddress) {

		byte opCode = cpuBuss.read(currentAddress);
		int instructionLength = 0;
		// PP YYY ZZZ
		int page = (opCode >> 6) & 0X0003; // only want the value of bits 6 & 7
		int yyy = (opCode >> 3) & 0X0007; // only want the value of bits 3,4 & 5
		int zzz = opCode & 0X0007; // only want the value of bits 0,1 & 2
		switch (page) {
		case 0:
			instructionLength = opCodePage00(currentAddress, opCode, yyy, zzz);
			break;
		case 1:
			// instructionLength = opCodePage01(yyy, zzz);
			break;
		case 2:
			// instructionLength = opCodePage10(yyy, zzz);
			break;
		case 3:
			// instructionLength = opCodePage11(yyy, zzz);
			break;
		default:
			setError(ErrorType.INVALID_OPCODE);
			return;
		}// Switch
			// byte opCode =
		wrs.setProgramCounter(instructionLength + currentAddress);

		return;
	}// executeInstruction
		// --------------------------------------------------------------------------------------------------------

	private int opCodePage00(int currentAddress, byte opCode, int yyy, int zzz) {
		// byte hiByte, loByte;
		int word;
		Register register16Bit = RegisterDecode.getRegisterPairStd(opCode);
		Register register8bit = RegisterDecode.getHighRegister(opCode);
		int codeLength = 0;
		// 00 YYY ZZZ
		switch (zzz) {
		case 0: // zzz = 000
			// NOP
			codeLength = 1;
			break;
		case 1: // zzz = 001 - Register Pair (BC,DE,HL,SP)
			if ((yyy & 0X01) == 0) { // LXI
				word = cpuBuss.readWordReversed(currentAddress + 1);
				wrs.setDoubleReg(register16Bit, word);
				codeLength = 3;
			} else {// DAD
				int hlValue = wrs.getDoubleReg(Register.HL);
				int regValue = wrs.getDoubleReg(register16Bit);
				wrs.setDoubleReg(Register.HL, au.add(hlValue, regValue));
				codeLength = 1;
			}// if
			break;
		case 2: // zzz = 010
			int location = wrs.getDoubleReg(register16Bit);
			switch (yyy) {
			case 0: // STAX BC
			case 2: // STAX DE
				cpuBuss.write(location, wrs.getAcc());
				codeLength = 1;
				break;
			case 1: // LDAX BC
			case 3: // LDAX DE
				wrs.setAcc(cpuBuss.read(location));
				codeLength = 1;
				break;
			case 4:	//SHLD	
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			default:
				codeLength = 0;
				setError(ErrorType.INVALID_OPCODE);
				return codeLength;

			}// switch (yyy)
			break;
		case 3: // zzz = 011
			break;
		case 4: // zzz = 100
			break;
		case 5: // zzz = 101
			break;
		case 6: // zzz = 110
			break;
		case 7: // zzz = 111
			break;
		default:
		}// switch
		return codeLength;

	}//
		// --------------------------------------------------------------------------------------------------------

	/**
	 * Retrieves an error of ErrorType
	 * 
	 * @return error type of error
	 */
	private ErrorType getError() {
		return this.error;
	}// setErrorFlag

	/**
	 * records an error of ErrorType
	 * 
	 * @param error
	 *            type of error to record
	 */
	private void setError(ErrorType error) {
		this.error = error;
	}// setErrorFlag

	/**
	 * indicated if an error is recorded
	 * 
	 * @return false if no error, else true
	 */
	public boolean isError() {
		return error.equals(ErrorType.NONE) ? false : true;
	}// isError

}// class CentralProcessingUnit
