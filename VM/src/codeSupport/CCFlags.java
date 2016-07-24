package codeSupport;

public enum CCFlags {
	NONE,	// 
	S,		// Sign
	Z,		// Zero
	AC,		// Auxiliary Carry
	P,		// Parity
	CY,		// * Carry 
	ZSP,	// Zero, Sign & Parity
	ZSPAC,	// * Zero, Sign, Parity & Auxiliary Carry
	ZSPACCY,	// * Zero, Sign, Parity,Auxiliary Carry & Carry
	ZSPCY	// * Zero, Sign, Parity & Carry
}
