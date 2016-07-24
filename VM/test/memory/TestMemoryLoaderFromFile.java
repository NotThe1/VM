package memory;

// uses the files : "MemoryLoaderTest.mem" & "MemoryLoaderTest.hex" co-located with this test

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class TestMemoryLoaderFromFile {
	IoBuss ioBuss = IoBuss.getInstance();
	byte[] mem0100;
	byte[] mem0110;
	byte[] mem0120;
	byte[] mem0130;

	@Before
	public void setUp() throws Exception {

		mem0100 = new byte[] { (byte) 0X21, (byte) 0X46, (byte) 0X01, (byte) 0X36,
				(byte) 0X01, (byte) 0X21, (byte) 0X47, (byte) 0X01,
				(byte) 0X36, (byte) 0X00, (byte) 0X7E, (byte) 0XFE,
				(byte) 0X09, (byte) 0XD2, (byte) 0X19, (byte) 0X01
		};
		mem0110 = new byte[] { (byte) 0X21, (byte) 0X46, (byte) 0X01, (byte) 0X7E,
				(byte) 0X17, (byte) 0XC2, (byte) 0X00, (byte) 0X01,
				(byte) 0XFF, (byte) 0X5F, (byte) 0X16, (byte) 0X00,
				(byte) 0X21, (byte) 0X48, (byte) 0X01, (byte) 0X19
		};
		mem0120 = new byte[] { (byte) 0X19, (byte) 0X4E, (byte) 0X79, (byte) 0X23,
				(byte) 0X46, (byte) 0X23, (byte) 0X96, (byte) 0X57,
				(byte) 0X78, (byte) 0X23, (byte) 0X9E, (byte) 0XDA,
				(byte) 0X3F, (byte) 0X01, (byte) 0XB2, (byte) 0XCA
		};
		mem0130 = new byte[] { (byte) 0X3F, (byte) 0X01, (byte) 0X56, (byte) 0X70,
				(byte) 0X2B, (byte) 0X5E, (byte) 0X71, (byte) 0X2B,
				(byte) 0X72, (byte) 0X2B, (byte) 0X73, (byte) 0X21,
				(byte) 0X46, (byte) 0X01, (byte) 0X34, (byte) 0X21
		};
	}// setUp

	@Test
	public void testHex() {
		// MemoryLoaderTest.hex
		byte value =(byte) 0X00;
		int start = 0000;
		int end = 0X01FF;
		for (int i = start; i < end; i++) {
			ioBuss.write(i, value);
		}// for

		for (int i = start; i < end; i++) {
			assertThat("testHex, fillMemory " + value, value, equalTo(ioBuss.read(i)));
		}// for
		
		String userDirectory = System.getProperty("user.dir", ".");
		Path dataPath = Paths.get(userDirectory,"test","memory","MemoryLoaderTest.mem");
		System.out.println(dataPath.toAbsolutePath().toString());
		if (!Files.exists(dataPath, LinkOption.NOFOLLOW_LINKS)) {
			fail("MemoryLoaderTest.mem NOT found");
		}
		File sourceFile = new File(dataPath.toString());
		MemoryLoaderFromFile.loadMemoryImage(sourceFile);
		
		assertThat("testHex - read 1",mem0100,equalTo(ioBuss.readDMA(0X100, 0X0010)));
		assertThat("testHex - read 2",mem0110,equalTo(ioBuss.readDMA(0X110, 0X0010)));
		assertThat("testHex - read 3",mem0120,equalTo(ioBuss.readDMA(0X120, 0X0010)));
		assertThat("testHex - read 4",mem0130,equalTo(ioBuss.readDMA(0X130, 0X0010)));

	}// testHex

	@Test
	public void testMem() {
		//MemoryLoaderTest.mem
		byte value =(byte) 0X00;
		int start = 0000;
		int end = 0X01FF;
		for (int i = start; i < end; i++) {
			ioBuss.write(i, value);
		}// for

		for (int i = start; i < end; i++) {
			assertThat("testHex, fillMemory " + value, value, equalTo(ioBuss.read(i)));
		}// for
		
		String userDirectory = System.getProperty("user.dir", ".");
		Path dataPath = Paths.get(userDirectory,"test","memory","MemoryLoaderTest.hex");
		System.out.println(dataPath.toAbsolutePath().toString());
		if (!Files.exists(dataPath, LinkOption.NOFOLLOW_LINKS)) {
			fail("MemoryLoaderTest.mem NOT found");
		}
		File sourceFile = new File(dataPath.toString());
		MemoryLoaderFromFile.loadMemoryImage(sourceFile);
		
		assertThat("testHex - read 1",mem0100,equalTo(ioBuss.readDMA(0X100, 0X0010)));
		assertThat("testHex - read 2",mem0110,equalTo(ioBuss.readDMA(0X110, 0X0010)));
		assertThat("testHex - read 3",mem0120,equalTo(ioBuss.readDMA(0X120, 0X0010)));
		assertThat("testHex - read 4",mem0130,equalTo(ioBuss.readDMA(0X130, 0X0010)));
	}// testMem



}// TestMemoryLoaderFromFile
