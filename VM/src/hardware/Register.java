/**
 * 
 */
package hardware;

/**
 * @author Frank Martyn
 * @version 1.0
 *
 */
public enum Register {
	// Single Byte Registers
	A, B, C, D, E, H, L,

	// Double Byte Registers
	// used for identification only
	// nothing is stored directly into one of these
	BC, DE, HL, M, SP, AF, PC
}//enum Register
