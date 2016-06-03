package codeSupport;

public enum Command {
	STC,CMC,				//Carry Bit Instructions
	INR,DCR,CMA,DAA,			//Single Register Instructions
	NOP,				//NOP Instruction
	MOV,STAX,LDAX,			//Data Transfer Instructions
	ADD,ADC,SUB,SBB,ANA,XRA,ORA,CMP,	//Register or Memory to Acc Instructions
	RLC,RRC,RAL,RAR,			// Rotate Acc instructions
	PUSH,POP,DAD,INX,DCX,XCHG,XTHL,SPHL,	//Register Pair Instructions
	LXI,MVI,ADI,ACI,SUI,SBI,ANI,XRI,ORI,CPI,	// Immediate Instructions
	STA,LDA,SHLD,LHLD,			// Direct Accessing Instructions
	PCHL,JMP,JC,JNC,JZ,JNZ,JM,JP,JPE,JPO,	// Jump Instructions
	CALL,CC,CNC,CZ,CNZ,CM,CP,CPE,CPO,	// Call Instructions
	RET,RC,RNC,RZ,RNZ,RM,RP,RPE,RPO,	// Return from Subroutine Instructions
	RST,EI,DI,IN,OUT,HLT		// Miscellaneous Instructions
}//
