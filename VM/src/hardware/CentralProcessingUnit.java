package hardware;

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
		this.ccr = ccr;

		this.error = ErrorType.NONE;

	}// Constructor

	public boolean startInstruction() {
		executeInstruction(wrs.getProgramCounter());
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
			instructionLength = opCodePage01(opCode, yyy, zzz);
			break;
		case 2:
			instructionLength = opCodePage10(opCode, yyy);
			break;
		case 3:
			instructionLength = opCodePage11(currentAddress, opCode, yyy, zzz);
			break;
		default:
			setError(ErrorType.INVALID_OPCODE);
			return;
		}// Switch
			// byte opCode =
		wrs.incrementProgramCounter(instructionLength);

		return;
	}// executeInstruction
		// --------------------------------------------------------------------------------------------------------

	private int opCodePage00(int currentAddress, byte opCode, int yyy, int zzz) {
		byte hiByte, loByte;
		int intValue;
		byte byteValue;
		int directAddress = cpuBuss.readWordReversed(currentAddress + 1);
		Register register16Bit = RegisterDecode.getRegisterPairStd(opCode);
		Register register8bit = RegisterDecode.getHighRegister(opCode);
		int codeLength;
		// 00 YYY ZZZ
		switch (zzz) {
		case 0: // zzz = 000 (NOP)
			// NOP
			codeLength = 1;
			break;
		case 1: // zzz = 001 (LXI & DAD)- Register Pair (BC,DE,HL,SP)
			if ((yyy & 0X01) == 0) { // LXI
				// word = cpuBuss.readWordReversed(currentAddress + 1);
				wrs.setDoubleReg(register16Bit, directAddress);
				codeLength = 3;
			} else {// DAD
				int hlValue = wrs.getDoubleReg(Register.HL);
				int regValue = wrs.getDoubleReg(register16Bit);
				wrs.setDoubleReg(Register.HL, au.add(hlValue, regValue));
				codeLength = 1;
			}// if
			break;
		case 2: // zzz = 010 (STAX LDAX)
			int location = wrs.getDoubleReg(register16Bit);
			switch (yyy) {// zzz = 100
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
			case 4: // SHLD
				hiByte = wrs.getReg(Register.L);
				loByte = wrs.getReg(Register.H);
				cpuBuss.writeWord(directAddress, hiByte, loByte);
				codeLength = 3;
				break;
			case 5: // LHLD
				wrs.setReg(Register.L, cpuBuss.read(directAddress));
				wrs.setReg(Register.H, cpuBuss.read(directAddress + 1));
				codeLength = 3;
				break;
			case 6: // STA
				cpuBuss.write(directAddress, wrs.getAcc());
				codeLength = 3;
				break;
			case 7: // LDA
				wrs.setAcc(cpuBuss.read(directAddress));
				codeLength = 3;
				break;
			default:
				codeLength = 0;
				setError(ErrorType.INVALID_OPCODE);
				// return codeLength;
			}// switch (yyy)
			break;
		case 3: // zzz = 011 (INX & DCX) - Register Pair (BC,DE,HL,SP)
			codeLength = 1;
			intValue = wrs.getDoubleReg(register16Bit);
			if ((yyy & 0X01) == 0) {// INX
				wrs.setDoubleReg(register16Bit, au.increment(intValue));
			} else { // DCX
				wrs.setDoubleReg(register16Bit, au.decrement(intValue));
			}// if

			break;
		case 4: // zzz = 100 (INC)- Register (B,C,D,E,H,L,A)
			if (register8bit.equals(Register.M)) { // Memory reference through (HL)
				int indirectAddress = wrs.getDoubleReg(Register.M);
				byteValue = au.increment(cpuBuss.read(indirectAddress));
				cpuBuss.write(indirectAddress, byteValue);
			} else { // Standard 8-bit register
				byteValue = wrs.getReg(register8bit);
				wrs.setReg(register8bit, au.increment(byteValue));
			}// if
			codeLength = 1;
			break;
		case 5: // zzz = 101 (DCR)- Register (B,C,D,E,H,L,A)
			if (register8bit.equals(Register.M)) { // Memory reference through (HL)
				int indirectAddress = wrs.getDoubleReg(Register.M);
				byteValue = au.decrement(cpuBuss.read(indirectAddress));
				cpuBuss.write(indirectAddress, byteValue);
			} else { // Standard 8-bit register
				byteValue = wrs.getReg(register8bit);
				wrs.setReg(register8bit, au.decrement(byteValue));
			}// if
			codeLength = 1;
			break;
		case 6: // zzz = 110 (MVI)- Register (B,C,D,E,H,L,A)
			byteValue = cpuBuss.read(currentAddress + 1);
			if (register8bit.equals(Register.M)) { // Memory reference through (HL)
				int indirectAddress = wrs.getDoubleReg(Register.M);
				cpuBuss.write(indirectAddress, byteValue);
			} else { // Standard 8-bit register
				wrs.setReg(register8bit, byteValue);
			}// if
			codeLength = 2;
			break;
		case 7: // zzz = 111 (RLC,RRC,RAL,RAR,DAA,CMA,STC,CMC
			byteValue = wrs.getAcc();
			switch (yyy) {
			case 0: // zzz = 000 (RLC)
				wrs.setAcc(au.rotateLeft(byteValue));
				codeLength = 1;
				break;
			case 1: // zzz = 001 (RRC)
				wrs.setAcc(au.rotateRight(byteValue));
				codeLength = 1;
				break;
			case 2: // zzz = 010 (RAL)
				wrs.setAcc(au.rotateLeftThruCarry(byteValue));
				codeLength = 1;
				break;
			case 3: // zzz = 011 (RAR)
				wrs.setAcc(au.rotateRightThruCarry(byteValue));
				codeLength = 1;
				break;
			case 4: // zzz = 100 (DAA)
				wrs.setAcc(au.decimalAdjustByte(byteValue));
				codeLength = 1;
				break;
			case 5: // zzz = 101 (CMA)
				wrs.setAcc(au.complement(byteValue));
				codeLength = 1;
				break;
			case 6: // zzz = 110 (STC)
				ccr.setCarryFlag(true);
				codeLength = 1;
				break;
			case 7: // zzz = 111 (CMC)
				boolean state = ccr.isCarryFlagSet();
				ccr.setCarryFlag(!state);
				codeLength = 1;
				break;
			default:
				setError(ErrorType.INVALID_OPCODE);
				codeLength = 0;
			}// switch yyy
			break;
		default:
			setError(ErrorType.INVALID_OPCODE);
			return 0;
		}// switch zzz
		return codeLength;

	}// opCodePage00
		// ----------------------------------------------------

	// private int opCodePage01(int currentAddress, byte opCode, int yyy, int zzz){

	private int opCodePage01(byte opCode, int yyy, int zzz) {
		if (opCode == (byte) 0X76) { // (HLT)
			setError(ErrorType.HLT_INSTRUCTION);
		} else { // (MOV)
			Register destination = RegisterDecode.getHighRegister(opCode);
			Register source = RegisterDecode.getLowRegister(opCode);
			byte value;

			if (source.equals(Register.M)) { // Memory reference through (HL)
				int indirectAddress = wrs.getDoubleReg(Register.M);
				value = cpuBuss.read(indirectAddress);
			} else { // Standard 8-bit register
				value = wrs.getReg(source);
			}// if

			if (destination.equals(Register.M)) { // Memory reference through (HL)
				int indirectAddress = wrs.getDoubleReg(Register.M);
				cpuBuss.write(indirectAddress, value);
			} else { // Standard 8-bit register
				wrs.setReg(destination, value);
			}// if
		}// else
		int codeLength = 1;
		return codeLength;
	}// opCodePage01

	private int opCodePage10(byte opCode, int yyy) {
		byte sourceValue, accValue;
		int indirectAddress;
		Register register8bit = RegisterDecode.getLowRegister(opCode);
		if (register8bit.equals(Register.M)) {
			indirectAddress = wrs.getDoubleReg(Register.M);
			sourceValue = cpuBuss.read(indirectAddress);
		} else {
			sourceValue = wrs.getReg(register8bit);
		}// if register M for source

		accValue = wrs.getAcc();
		int codeLength = 1;
		switch (yyy) {
		case 0: // yyy = 000 (ADD)
			wrs.setAcc(au.add(accValue, sourceValue));
			break;
		case 1: // yyy = 001 (ADC)
			wrs.setAcc(au.addWithCarry(accValue, sourceValue));
			break;
		case 2: // yyy = 010 (SUB)
			wrs.setAcc(au.subtract(accValue, sourceValue));
			break;
		case 3: // yyy = 011 (SBB)
			wrs.setAcc(au.subtractWithBorrow(accValue, sourceValue));
			break;
		case 4: // yyy = 100 (ANA)
			wrs.setAcc(au.logicalAnd(accValue, sourceValue));
			break;
		case 5: // yyy = 101 (XRA)
			wrs.setAcc(au.logicalXor(accValue, sourceValue));
			break;
		case 6: // yyy = 110 (ORA)
			wrs.setAcc(au.logicalOr(accValue, sourceValue));
			break;
		case 7: // yyy = 111 (CMP)
			au.subtract(accValue, sourceValue); // leave both values untouched
			break;
		default:
			setError(ErrorType.INVALID_OPCODE);
			codeLength = 0;
		}// switch yyy

		return codeLength;
	}// opCodePage10

	private int opCodePage11(int currentAddress, byte opCode, int yyy, int zzz) {
		int codeLength = -1;
		byte accValue;
		Register register16bit = RegisterDecode.getRegisterPairAlt(opCode);
		ConditionFlag condition = RegisterDecode.getCondition(opCode);
		accValue = wrs.getAcc();

		switch (zzz) {
		case 0: // zzz 000 Conditional return CCC
			if (opCodeConditionTrue(condition)) { // do the return
				opCode_Return();
				codeLength = 0;
			} else { // just skip past
				codeLength = 1;
			}// if
			break;
		case 1: // zzz 001 POP/RET/PCHL/SPHL
			if ((yyy & 0X01) == 0) { // POP
				int stackLocation = wrs.getStackPointer();
				int valueInt = cpuBuss.popWord(stackLocation);
				if (register16bit.equals(Register.AF)) { // PSW
					wrs.setReg(Register.A, (byte) ((valueInt >> 8) & 0X00FF));
					ccr.setConditionCode((byte) (valueInt & 0X00FF));
				} else {
					wrs.setDoubleReg(register16bit, valueInt);
				}// if
				wrs.setStackPointer(stackLocation + 2);
				codeLength = 1;
			} else { // RET/PCHL/SPHL
				if (opCode == (byte) 0XC9) {// RET
					opCode_Return();
					codeLength = 0;
				} else if (opCode == (byte) 0XE9) { // PCHL
					int hlValue = wrs.getDoubleReg(Register.HL);
					wrs.setProgramCounter(hlValue);
					codeLength = 0;
				} else if (opCode == (byte) 0XF9) { // SPHL
					int hlValue = wrs.getDoubleReg(Register.HL);
					wrs.setStackPointer(hlValue);
					codeLength = 1;
				} else { // Not used
					setError(ErrorType.INVALID_OPCODE);
					codeLength = 0;
				}// inner if
			}// if lsb = 0/1
			break;
		case 2: // zzz 010 Conditional Jump CCC
			if (opCodeConditionTrue(condition)) { // do the return
				opCode_Jump();
				codeLength = 0;
			} else { // just skip past
				codeLength = 3;
			}// if
			break;
		case 3: // zzz 011 JMP/OUT/IN/XTHL/XCHL/DI/EI
			switch (yyy) {
			case 0: // yyy 000 JMP
				opCode_Jump();
				codeLength = 0;
				break;
			case 1: // yyy 001 Not Used
				setError(ErrorType.INVALID_OPCODE);
				codeLength = 0;
				break;
			case 2: // yyy 010 OUT
				System.out.println("Not yet implemented");
				codeLength = 2;
				break;
			case 3: // yyy 011 IN
				System.out.println("Not yet implemented");
				codeLength = 2;
				break;
			case 4: // yyy 100 XTHL
				byte valueL = wrs.getReg(Register.L);
				byte valueH = wrs.getReg(Register.H);
				
//				int hlValue = wrs.getDoubleReg(Register.HL);
				int stackLocation = wrs.getStackPointer();
				wrs.setReg(Register.L, cpuBuss.read(stackLocation));
				wrs.setReg(Register.H, cpuBuss.read(stackLocation+1));
//				int stackValue = cpuBuss.popWord(stackLocation);
//				wrs.setDoubleReg(Register.HL, stackValue);
//				cpuBuss.pushWord(stackLocation, hlValue);
				cpuBuss.write(stackLocation, valueL);
				cpuBuss.write(stackLocation + 1, valueH);
				
				codeLength = 1;
				break;
			case 5: // yyy 101 XCHG
				int deValue = wrs.getDoubleReg(Register.DE);
				wrs.setDoubleReg(Register.DE, wrs.getDoubleReg(Register.HL));
				wrs.setDoubleReg(Register.HL, deValue);
				codeLength = 1;
				break;
			case 6: // yyy 110 DI
				System.out.println("Not yet implemented");
				codeLength = 1;
				break;
			case 7: // yyy 111 EI
				System.out.println("Not yet implemented");
				codeLength = 1;
				break;
			default:
				setError(ErrorType.INVALID_OPCODE);
				codeLength = 0;
			}// switch(opCod
			break;
		case 4: // zzz 100 Conditional Call CCC
			if (opCodeConditionTrue(condition)) { // do the return
				opCode_Call();
				codeLength = 0;
			} else { // just skip past
				codeLength = 3;
			}// if
			break;
		case 5: // zzz 101 PUSH/CALL
			if ((yyy & 0X01) == 0) { // PUSH
				if (register16bit.equals(Register.AF)) {
					opCode_Push(wrs.getReg(Register.A), ccr.getConditionCode());
				} else {
					opCode_Push(wrs.getDoubleReg(register16bit));
				}// if
				codeLength = 1;
			} else if (opCode == (byte) 0XCD) { // CALL
				opCode_Call();
				codeLength = 0;
			} else {
				setError(ErrorType.INVALID_OPCODE);
				codeLength = 0;
			}// if odd/even
			break;
		case 6: // zzz 110 ADI/ACI/SUI/SBI/ANI/XRI/ORI/CPI
			byte resultValue;
			byte immediateValue = cpuBuss.read(wrs.getProgramCounter() + 1);
			// accValue = wrs.getAcc();
			codeLength = 2;
			switch (yyy) {
			case 0: // yyy 000 ADI
				resultValue = au.add(accValue, immediateValue);
				wrs.setAcc(resultValue);
				break;
			case 1: // yyy 001 ACI
				resultValue = au.addWithCarry(accValue, immediateValue);
				wrs.setAcc(resultValue);
				break;
			case 2: // yyy 010 SUI
				resultValue = au.subtract(accValue, immediateValue);
				wrs.setAcc(resultValue);
				break;
			case 3: // yyy 011 SBI
				resultValue = au.subtractWithBorrow(accValue, immediateValue);
				wrs.setAcc(resultValue);
				break;
			case 4: // yyy 100 ANI
				resultValue = au.logicalAnd(accValue, immediateValue);
				wrs.setAcc(resultValue);
				break;
			case 5: // yyy 101 XRI
				resultValue = au.logicalXor(accValue, immediateValue);
				wrs.setAcc(resultValue);
				break;
			case 6: // yyy 110 ORI
				resultValue = au.logicalOr(accValue, immediateValue);
				wrs.setAcc(resultValue);
				break;
			case 7: // yyy 111 CPI
				resultValue = au.subtract(accValue, immediateValue);
				break;
			default:
				setError(ErrorType.INVALID_OPCODE);
				codeLength = 0;
			}// switch(yyy)
			break;
		case 7: // zzz 111 RST PPP
			opCode_Push(wrs.getProgramCounter());
			int address = yyy * 8;
			wrs.setProgramCounter(address);
			codeLength = 0;
			break;
		default: // zzz 000
			setError(ErrorType.INVALID_OPCODE);
			return 0;
		}// switch(zzz)

		return codeLength;

	}// opCodePage11

	private void opCode_Push(byte hiByte, byte loByte) {
		int stackLocation = wrs.getStackPointer();
		cpuBuss.pushWord(stackLocation, hiByte, loByte); // push the return address
		wrs.setStackPointer(stackLocation - 2);
	}// opCode_Push

	private void opCode_Push(int value) {
		byte hiByte = (byte) (value >> 8);
		byte loByte = (byte) (value & 0X00FF);
		opCode_Push(hiByte, loByte);
		// int stackLocation = wrs.getStackPointer();
		// cpuBuss.pushWord(stackLocation, hiByte, loByte); // push the return address
		// wrs.setStackPointer(stackLocation - 2);
	}// opCode_Push

	private void opCode_Jump() {
		int currentProgramCounter = wrs.getProgramCounter();
		int memoryLocation = cpuBuss.readWordReversed(currentProgramCounter + 1);
		wrs.setProgramCounter(memoryLocation);
	}// opCode_Jump

	private void opCode_Call() {
		int currentProgramCounter = wrs.getProgramCounter();
		opCode_Push(currentProgramCounter + 3);
		int memoryLocation = cpuBuss.readWordReversed(currentProgramCounter + 1);
		wrs.setProgramCounter(memoryLocation);
		// opCodeSize = 0;

	}// opCode_Call()

	private void opCode_Return() {
		int stackPointer = wrs.getStackPointer();
		wrs.setProgramCounter(cpuBuss.popWord(stackPointer));
		wrs.setStackPointer(stackPointer + 2);
	}// opCode_Return

	/**
	 * checks to see if the given condition flag is set
	 * 
	 * @param condition
	 *            to be tested
	 * @return true if condition is met
	 */
	private boolean opCodeConditionTrue(ConditionFlag condition) {
		boolean ans = false;
		switch (condition) {
		case NZ:
			ans = ccr.isZeroFlagSet() ? false : true;
			break;
		case Z:
			ans = ccr.isZeroFlagSet() ? true : false;
			break;
		case NC:
			ans = ccr.isCarryFlagSet() ? false : true;
			break;
		case C:
			ans = ccr.isCarryFlagSet() ? true : false;
			break;
		case PO:
			ans = ccr.isParityFlagSet() ? false : true;
			break;
		case PE:
			ans = ccr.isParityFlagSet() ? true : false;
			break;
		case P:
			ans = ccr.isSignFlagSet() ? false : true;
			break;
		case M:
			ans = ccr.isSignFlagSet() ? true : false;
			break;
		default:

		}// switch
		return ans;
	}// conditionTrue

	// --------------------------------------------------------------------------------------------------------

	/**
	 * Retrieves an error of ErrorType
	 * 
	 * @return error type of error
	 */
	public ErrorType getError() {
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
