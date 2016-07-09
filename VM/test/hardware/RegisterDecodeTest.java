package hardware;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

//import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class RegisterDecodeTest {
	

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testWordRegisters() {
		byte[] opCodesStd = new byte[] { (byte) 0X01, (byte) 0X11, (byte) 0X21, (byte) 0X31,
				(byte) 0X09, (byte) 0X19, (byte) 0X29, (byte) 0X39
		};
//		byte[] opCodesAlt = new byte[] { (byte) 0XC1, (byte) 0XD1, (byte) 0XE1, (byte) 0XF1,
//				(byte) 0XC5, (byte) 0XD5, (byte) 0XE5, (byte) 0XF5
//		};
		Register[] wordRegistersStd = new Register[] { Register.BC, Register.DE, Register.HL, Register.SP,
				Register.BC, Register.DE, Register.HL, Register.SP
		};
		Register[] wordRegistersAlt = new Register[] { Register.BC, Register.DE, Register.HL, Register.AF,
				Register.BC, Register.DE, Register.HL, Register.AF
		};

		int count = opCodesStd.length;
		for (int i = 0; i < count; i++) {
			assertThat("getRegisterPairStd " + i, wordRegistersStd[i], equalTo(RegisterDecode.getRegisterPairStd(opCodesStd[i])));
			assertThat("getRegisterPairAlt " + i, wordRegistersAlt[i], equalTo(RegisterDecode.getRegisterPairAlt(opCodesStd[i])));

		}// for

	}// testWordRegisters
	
	@Test
	public void testSingleRegisters() {
		byte[] opCodesLow = new byte[] { (byte) 0X47, (byte) 0X60, (byte) 0X91, (byte) 0XA2,
				(byte) 0X43, (byte) 0X94, (byte) 0X75, (byte) 0X56,

				(byte) 0X6F, (byte) 0X88, (byte) 0X99, (byte) 0XAA,
				(byte) 0XBB, (byte) 0X4C, (byte) 0X7D, (byte) 0X9E
		};
		byte[] opCodesHigh = new byte[] { (byte) 0X3C, (byte) 0X05, (byte) 0X0E, (byte) 0X14,
				(byte) 0X1D, (byte) 0X26, (byte) 0X2C, (byte) 0X35,

				(byte) 0X7F, (byte) 0X41, (byte) 0X4E, (byte) 0X52,
				(byte) 0X5B, (byte) 0X63, (byte) 0X6A, (byte) 0X75
		};
		Register[] singleRegisters = new Register[] { Register.A, Register.B, Register.C, Register.D,
				Register.E, Register.H, Register.L, Register.M,

				Register.A, Register.B, Register.C, Register.D,
				Register.E, Register.H, Register.L, Register.M
		};

		int count = singleRegisters.length;
		for (int i = 0; i < count; i++) {
			assertThat("getLowRegister ", singleRegisters[i], equalTo(RegisterDecode.getLowRegister(opCodesLow[i])));
			assertThat("getHighRegister " + i, singleRegisters[i],
					equalTo(RegisterDecode.getHighRegister(opCodesHigh[i])));
		}// for

	}// testSingleRegisters

	@Test
	public void testConditions() {
		byte[] opCodes = new byte[] { (byte) 0XC4, (byte) 0XCC, (byte) 0XD4, (byte) 0XDC,
				(byte) 0XE4, (byte) 0XEC, (byte) 0XF4, (byte) 0XFC,

				(byte) 0XC2, (byte) 0XCA, (byte) 0XD2, (byte) 0XDA,
				(byte) 0XE2, (byte) 0XEA, (byte) 0XF2, (byte) 0XFA
		};

		ConditionFlag[] conditionFlags = { ConditionFlag.NZ, ConditionFlag.Z, ConditionFlag.NC, ConditionFlag.C,
				ConditionFlag.PO, ConditionFlag.PE, ConditionFlag.P, ConditionFlag.M,

				ConditionFlag.NZ, ConditionFlag.Z, ConditionFlag.NC, ConditionFlag.C,
				ConditionFlag.PO, ConditionFlag.PE, ConditionFlag.P, ConditionFlag.M,
		};

		int count = opCodes.length;
		for (int i = 0; i < count; i++) {
			assertThat("getLowRegister " + i, conditionFlags[i], equalTo(RegisterDecode.getCondition(opCodes[i])));
		}// for

	}// testConditions

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }//

}
