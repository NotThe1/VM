package utilities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import utilities.memoryFormatter.MemFormatter;

import static org.hamcrest.CoreMatchers.*;

public class MemFormatterTest {
	int metaAddress = 0X0100;
	byte[] metaSource = new byte[] {
			(byte) 0X021, (byte) 0X046, (byte) 0X001, (byte) 0X036,
			(byte) 0X001, (byte) 0X021, (byte) 0X047, (byte) 0X001,
			(byte) 0X036, (byte) 0X000, (byte) 0X07E, (byte) 0X0FE,
			(byte) 0X009, (byte) 0X0D2, (byte) 0X019, (byte) 0X001,

			(byte) 0X021, (byte) 0X046, (byte) 0X001, (byte) 0X07E,
			(byte) 0X017, (byte) 0X0C2, (byte) 0X000, (byte) 0X001,
			(byte) 0X0FF, (byte) 0X05F, (byte) 0X016, (byte) 0X000,
			(byte) 0X021, (byte) 0X048, (byte) 0X001, (byte) 0X019,

			(byte) 0X019, (byte) 0X04E, (byte) 0X079, (byte) 0X023,
			(byte) 0X046, (byte) 0X023, (byte) 0X096, (byte) 0X057,
			(byte) 0X078, (byte) 0X023, (byte) 0X09E, (byte) 0X0DA,
			(byte) 0X03F, (byte) 0X001, (byte) 0X0B2, (byte) 0X0CA,

			(byte) 0X03F, (byte) 0X001, (byte) 0X056, (byte) 0X070,
			(byte) 0X02B, (byte) 0X05E, (byte) 0X071, (byte) 0X02B,
			(byte) 0X072, (byte) 0X02B, (byte) 0X073, (byte) 0X021,
			(byte) 0X046, (byte) 0X001, (byte) 0X034, (byte) 0X021
	};

	String line1A = "0100: 21 46 01 36 01 21 47 01  36 00 7E FE 09 D2 19 01  !F.6.!G.6.~.....";
	String line1 = "0100: 21 46 01 36 01 21 47 01  36 00 7E FE 09 D2 19 01";
	String line2A = "0110: 21 46 01 7E 17 C2 00 01  FF 5F 16 00 21 48 01 19  !F.~....._..!H..";
	String line2 = "0110: 21 46 01 7E 17 C2 00 01  FF 5F 16 00 21 48 01 19";
	String line3A = "0120: 19 4E 79 23 46 23 96 57  78 23 9E DA 3F 01 B2 CA  .Ny#F#.Wx#..?...";
	String line3 = "0120: 19 4E 79 23 46 23 96 57  78 23 9E DA 3F 01 B2 CA";
	String line4A = "0130: 3F 01 56 70 2B 5E 71 2B  72 2B 73 21 46 01 34 21  ?.Vp+^q+r+s!F.4!";
	String line4 = "0130: 3F 01 56 70 2B 5E 71 2B  72 2B 73 21 46 01 34 21";

	String line1A6 = "000100: 21 46 01 36 01 21 47 01  36 00 7E FE 09 D2 19 01  !F.6.!G.6.~.....";

	String[] linesA = new String[] { line1A, line2A, line3A, line4A };
	String[] lines = new String[] { line1, line2, line3, line4 };

	@Before
	public void setUp() throws Exception {
	}// setUp

	@Test
	public void testSetUp() {
		MemFormatter memFormatter = MemFormatter.memFormatterFactory();
		assertThat("testSetUp Constructor, no args ", true, equalTo(memFormatter.hasNext()));
		memFormatter = null;

		byte[] source = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int address = source.length;
		memFormatter = MemFormatter.memFormatterFactory(address, source);
		assertThat("testSetUp Constructor, Args - good", true, equalTo(memFormatter.hasNext()));
		memFormatter = null;

		// JOptions......................................................................
		// address = source.length + 1;
		// memFormatter = MemFormatter.memFormatterFactory(address, source);
		// assertThat("testSetUp Constructor, Args - bad address", true, equalTo(memFormatter.hasNext()));
		// memFormatter = null;
		//
		// source = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		// address = 16;
		// memFormatter = MemFormatter.memFormatterFactory(address, source);
		// assertThat("testSetUp Constructor, Args - bad source", true, equalTo(memFormatter.hasNext()));
		// memFormatter = null;
		// JOptions......................................................................

		memFormatter = MemFormatter.memFormatterFactory();
		assertThat("testSetUp ASCII flag - default", true, equalTo(memFormatter.isShowASCII()));
		assertThat("testSetUp newLine flag - default", true, equalTo(memFormatter.isLineSeparator()));
		memFormatter.setShowASCII(MemFormatter.NO_ASCII);
		assertThat("testSetUp ASCII flag - off", false, equalTo(memFormatter.isShowASCII()));
		memFormatter.setLineSeparator(MemFormatter.NO_LINE_SEPARATOR);
		assertThat("testSetUp newLine flag - off", false, equalTo(memFormatter.isLineSeparator()));

		memFormatter = null;

	}// testSetUp

	@Test
	public void testNoAscii() {
		String eol = System.lineSeparator();
		MemFormatter memFormatter = MemFormatter.memFormatterFactory(metaAddress, metaSource);
		memFormatter.setShowASCII(false);
		
		for (int i = 0; i < lines.length; i++) {
			assertThat("default - hasNext " + i, true, equalTo(memFormatter.hasNext()));
			assertThat("default - getNext " + i, lines[i] + eol, equalTo(memFormatter.getNext()));
		}// for lines - no ASCII - with LineSeparator

		memFormatter.reset();// reset the internal pointer & address width
		memFormatter.setLineSeparator(false);
		memFormatter.setShowASCII(false);
		for (int i = 0; i < lines.length; i++) {
			assertThat("no line separator - hasNext " + i, true, equalTo(memFormatter.hasNext()));
			assertThat("no line separator - getNext " + i, lines[i], equalTo(memFormatter.getNext()));
		}// for lines - no ASCII, no line Separator

		memFormatter.reset();// reset the internal pointer & address width
		memFormatter.setAddressWidth(6);
		memFormatter.setShowASCII(false);
		for (int i = 0; i < lines.length; i++) {
			assertThat("setAddressWidth(6) - hasNext " + i, true, equalTo(memFormatter.hasNext()));
			assertThat("setAddressWidth(6) - getNext " + i, "00" + lines[i], equalTo(memFormatter.getNext()));
		}// for lines - no ASCII, no line Separator

	}// testNoAscii
	
	@Test
	public void testWithAscii() {
		String eol = System.lineSeparator();
		MemFormatter memFormatter = MemFormatter.memFormatterFactory(metaAddress, metaSource);
//		memFormatter.setShowASCII(true);
		for (int i = 0; i < lines.length; i++) {
			assertThat("default - hasNext " + i, true, equalTo(memFormatter.hasNext()));
			assertThat("default - getNext " + i, linesA[i] + eol, equalTo(memFormatter.getNext()));
		}// for lines - no ASCII - with LineSeparator

		memFormatter.reset();// reset the internal pointer & address width
		memFormatter.setLineSeparator(false);
		for (int i = 0; i < lines.length; i++) {
			assertThat("no line separator - hasNext " + i, true, equalTo(memFormatter.hasNext()));
			assertThat("no line separator - getNext " + i, linesA[i], equalTo(memFormatter.getNext()));
		}// for lines - no ASCII, no line Separator

		memFormatter.reset();// reset the internal pointer & address width
		memFormatter.setAddressWidth(6);
		for (int i = 0; i < lines.length; i++) {
			assertThat("setAddressWidth(6) - hasNext " + i, true, equalTo(memFormatter.hasNext()));
			assertThat("setAddressWidth(6) - getNext " + i, "00" + linesA[i], equalTo(memFormatter.getNext()));
		}// for lines - no ASCII, no line Separator

		memFormatter.reset();// reset the internal pointer & address width
		// System.out.print(memFormatter.getNext());

	}// testtestWithAsciiAscii
	
	

	// System.out.print(memFormatter.getNext());
	// System.out.print(memFormatter.getNext());
	// System.out.print(memFormatter.getNext());
	// System.out.print(memFormatter.getNext());
	// System.out.print(memFormatter.getNext());

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }// test

}// class MemFormatterTest
