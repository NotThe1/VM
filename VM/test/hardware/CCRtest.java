package hardware;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CCRtest {

	IConditionCodeRegister ccr;
	Byte ans;

	static Byte zeros = (byte) 0x00;
	static Byte ones = (byte) 0xFF;
	
	static byte BASE_CC_0 = (byte) 0B00000010;		// condition code when all flags Reset
	static byte BASE_CC_1 = (byte) 0B11010111;		// condition code when all flags Set
	static byte MASK_SIGN = (byte) 0B10000000;
	static byte MASK_SIGN_NOT = (byte) 0B01111111;
	static byte MASK_ZERO = (byte) 0B01000000;
	static byte MASK_ZERO_NOT = (byte) 0B10111111;
	static byte MASK_AUX_CARRY = (byte) 0B00010000;
	static byte MASK_AUX_CARRY_NOT = (byte) 0B11101111;
	static byte MASK_PARITY = (byte) 0B00000100;
	static byte MASK_PARITY_NOT = (byte) 0B11111011;
	static byte MASK_CARRY = (byte) 0B00000001;
	static byte MASK_CARRY_NOT = (byte) 0B11111110;


	@Before
	public void setUp() throws Exception {
		ccr = ConditionCodeRegister.getInstance();
	}//setUp

	

	@Test
	public void testSetGetConditionCode() {

		ans = BASE_CC_0;
		ccr.setConditionCode(zeros);
		assertThat("00", ans, equalTo(ccr.getConditionCode()));

		ans = (byte) 0B11010111;
		ccr.setConditionCode(ones);
		assertThat("testSetGetConditionCode", ans, equalTo(ccr.getConditionCode()));
	}//testSetGetConditionCode

	@Test
	public void testSignFlag() {

		ccr.setConditionCode(zeros);

		ccr.setSignFlag(true);
		assertThat("00", true, equalTo(ccr.isSignFlagSet()));
		ans = (byte) (BASE_CC_0 | MASK_SIGN) ;
		assertThat("testSignFlag", ans, equalTo(ccr.getConditionCode()));

		ccr.setSignFlag(false);
		assertThat("testSignFlag", false, equalTo(ccr.isSignFlagSet()));
		assertThat("testSignFlag", BASE_CC_0, equalTo(ccr.getConditionCode()));

		ccr.setConditionCode(ones);

		ccr.setSignFlag(true);
		assertThat("testSignFlag", true, equalTo(ccr.isSignFlagSet()));
		assertThat("testSignFlag", BASE_CC_1, equalTo(ccr.getConditionCode()));

		ccr.setSignFlag(false);
		assertThat("testSignFlag", false, equalTo(ccr.isSignFlagSet()));
		ans = (byte) (BASE_CC_1 & MASK_SIGN_NOT);
		assertThat("testSignFlag", ans, equalTo(ccr.getConditionCode()));
	}//testSignFlag

	@Test
	public void testZeroFlag() {
		
		ccr.setConditionCode(zeros);

		ccr.setZeroFlag(true);
		assertThat("testZeroFlag", true, equalTo(ccr.isZeroFlagSet()));
		ans = (byte) (BASE_CC_0 | MASK_ZERO) ;
		assertThat("testZeroFlag", ans, equalTo(ccr.getConditionCode()));

		ccr.setZeroFlag(false);
		assertThat("testZeroFlag", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("testZeroFlag", BASE_CC_0, equalTo(ccr.getConditionCode()));

		ccr.setConditionCode(ones);

		ccr.setZeroFlag(true);
		assertThat("testZeroFlag", true, equalTo(ccr.isZeroFlagSet()));
		assertThat("testZeroFlag", BASE_CC_1, equalTo(ccr.getConditionCode()));

		ccr.setZeroFlag(false);
		assertThat("testZeroFlag", false, equalTo(ccr.isZeroFlagSet()));
		ans = (byte) (BASE_CC_1 & MASK_ZERO_NOT);
		assertThat("testZeroFlag", ans, equalTo(ccr.getConditionCode()));
	}//testZeroFlag

	@Test
	public void testAuxilaryCarryFlag() {
		
		ccr.setConditionCode(zeros);

		ccr.setAuxilaryCarryFlag(true);
		assertThat("testAuxilaryCarryFlag", true, equalTo(ccr.isAuxilaryCarryFlagSet()));
		ans = (byte) (BASE_CC_0 | MASK_AUX_CARRY) ;
		assertThat("testAuxilaryCarryFlag", ans, equalTo(ccr.getConditionCode()));

		ccr.setAuxilaryCarryFlag(false);
		assertThat("testAuxilaryCarryFlag", false, equalTo(ccr.isAuxilaryCarryFlagSet()));
		assertThat("testAuxilaryCarryFlag", BASE_CC_0, equalTo(ccr.getConditionCode()));

		ccr.setConditionCode(ones);

		ccr.setAuxilaryCarryFlag(true);
		assertThat("testAuxilaryCarryFlag", true, equalTo(ccr.isAuxilaryCarryFlagSet()));
		assertThat("testAuxilaryCarryFlag", BASE_CC_1, equalTo(ccr.getConditionCode()));

		ccr.setAuxilaryCarryFlag(false);
		assertThat("testAuxilaryCarryFlag", false, equalTo(ccr.isAuxilaryCarryFlagSet()));
		ans = (byte) (BASE_CC_1 & MASK_AUX_CARRY_NOT);
		assertThat("testAuxilaryCarryFlag", ans, equalTo(ccr.getConditionCode()));
	}//testAuxilaryCarryFlag

	@Test
	public void testParityFlag() {
		
		ccr.setConditionCode(zeros);

		ccr.setParityFlag(true);
		assertThat("testParityFlag", true, equalTo(ccr.isParityFlagSet()));
		ans = (byte) (BASE_CC_0 | MASK_PARITY) ;
		assertThat("testParityFlag", ans, equalTo(ccr.getConditionCode()));

		ccr.setParityFlag(false);
		assertThat("testParityFlag", false, equalTo(ccr.isParityFlagSet()));
		assertThat("testParityFlag", BASE_CC_0, equalTo(ccr.getConditionCode()));

		ccr.setConditionCode(ones);

		ccr.setParityFlag(true);
		assertThat("testParityFlag", true, equalTo(ccr.isParityFlagSet()));
		assertThat("testParityFlag", BASE_CC_1, equalTo(ccr.getConditionCode()));

		ccr.setParityFlag(false);
		assertThat("testParityFlag", false, equalTo(ccr.isParityFlagSet()));
		ans = (byte) (BASE_CC_1 & MASK_PARITY_NOT);
		assertThat("testParityFlag", ans, equalTo(ccr.getConditionCode()));
	}//testParityFlag

	@Test
	public void testCarryFlag() {
		
		ccr.setConditionCode(zeros);

		ccr.setCarryFlag(true);
		assertThat("testCarryFlag", true, equalTo(ccr.isCarryFlagSet()));
		ans = (byte) (BASE_CC_0 | MASK_CARRY) ;
		assertThat("testCarryFlag", ans, equalTo(ccr.getConditionCode()));

		ccr.setCarryFlag(false);
		assertThat("testCarryFlag", false, equalTo(ccr.isCarryFlagSet()));
		assertThat("testCarryFlag", BASE_CC_0, equalTo(ccr.getConditionCode()));

		ccr.setConditionCode(ones);

		ccr.setCarryFlag(true);
		assertThat("testCarryFlag", true, equalTo(ccr.isCarryFlagSet()));
		assertThat("testCarryFlag", BASE_CC_1, equalTo(ccr.getConditionCode()));

		ccr.setCarryFlag(false);
		assertThat("testCarryFlag", false, equalTo(ccr.isCarryFlagSet()));
		ans = (byte) (BASE_CC_1 & MASK_CARRY_NOT);
		assertThat("testCarryFlag", ans, equalTo(ccr.getConditionCode()));
	}//testCarryFlag

	@Test
	public void testZSP() {
		
		ccr.setConditionCode(zeros);
		
		ccr.setZSP((byte) 0x80); // set S, reset Z&P
		assertThat("sign - zeros with 0x80", true, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x80", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x80", false, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(ones);
		ccr.setZSP((byte) 0x80); // set S, reset Z&P
		assertThat("sign - zeros with 0x80", true, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x80", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x80", false, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(zeros);
		ccr.setZSP((byte) 0x40); // set ., reset S,Z&P
		assertThat("sign - zeros with 0x40", false, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x40", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x40", false, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(ones);
		ccr.setZSP((byte) 0x40); // set ., reset S,Z&P
		assertThat("sign - zeros with 0x40", false, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x40", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x40", false, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(zeros);
		ccr.setZSP((byte) 0x00); // set Z & P, reset S
		assertThat("sign - zeros with 0x00", false, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x00", true, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x00", true, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(ones);
		ccr.setZSP((byte) 0x00); // set Z & P, reset S
		assertThat("sign - zeros with 0x00", false, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x00", true, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x00", true, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(zeros);
		ccr.setZSP((byte) 0x81); // set Z & P, reset S
		assertThat("sign - zeros with 0x00", true, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x00", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x00", true, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(ones);
		ccr.setZSP((byte) 0x81); // set Z & P, reset S
		assertThat("sign - zeros with 0x00", true, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x00", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x00", true, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(zeros);
		ccr.setZSP((byte) 0xFF); // set Z & P, reset S
		assertThat("sign - zeros with 0x00", true, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x00", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x00", true, equalTo(ccr.isParityFlagSet()));

		ccr.setConditionCode(ones);
		ccr.setZSP((byte) 0xFF); // set Z & P, reset S
		assertThat("sign - zeros with 0x00", true, equalTo(ccr.isSignFlagSet()));
		assertThat("zero - zeros with 0x00", false, equalTo(ccr.isZeroFlagSet()));
		assertThat("parity - zeros with 0x00", true, equalTo(ccr.isParityFlagSet()));
	}//testZSP

}//class CCRtest
