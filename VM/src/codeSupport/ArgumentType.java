package codeSupport;

public enum ArgumentType {
	NONE,
	ADDRESS,
	D8,
	D16,
	VECTOR,
	A, B, C, D, E, H, L,		// single byte register
	BC, DE, HL, M, SP, AF, PC	// two byte  register
}
