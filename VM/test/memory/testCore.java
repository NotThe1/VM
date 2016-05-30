package memory;

import static org.junit.Assert.*;

import java.awt.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.BaseMatcher.*;
import static org.hamcrest.TypeSafeMatcher.*;

public class testCore {
	static int K = 1024;
	int accessErrorLocation;
	int trapLocation;
	int trapValue;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Core core = new Core(24 * K);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		// core = null;
	}

	@Test
	public void testConstructor() {

		Core core = new Core();
		assertThat("constructor Default size", (64 * K), equalTo(core.getSize()));
		assertThat("constructor Default size", 64, equalTo(core.getSizeInK()));

		core = null;
		int size = 25;
		core = new Core(size * K);
		assertThat("constructor 25K", (25 * K), equalTo(core.getSize()));
		assertThat("constructor Default size", size, equalTo(core.getSizeInK()));

		// coreConstructor = null;
		// size = 125;
		// coreConstructor = new Core(size * K);
		// assertThat("constructor Too large", (64 * K), equalTo(coreConstructor.getSize()));
		core = null;
	}

	@Test
	public void testTraps() {
		Core core = new Core(24 * K);
		int value;
		ArrayList<Integer> locationsDebug =
				new ArrayList<Integer>(Arrays.asList(0X0200, 0X0400, 0X0800, 0X0A00, 0X0C00, 0X0E00));

		ArrayList<Integer> locationsIO =
				new ArrayList<Integer>(Arrays.asList(0X0300, 0X0500, 0X0700, 0X0900, 0X0B00, 0X0D00, 0X0F00));

		assertEquals("Trap on new Core", 0, core.getTraps().size());
		assertEquals("Trap on new Core", 0, core.getTraps(Core.Trap.IO).size());

		for (Integer location : locationsDebug) {
			core.addTrap(location, Core.Trap.DEBUG);
		}
		assertEquals("Add Debug traps - count", locationsDebug.size(), core.getTraps().size());
		assertThat("Add Debug traps - locations ", core.getTraps(),
				hasItems(0X0200, 0X0400, 0X0800, 0X0A00, 0X0C00, 0X0E00));

		for (Integer location : locationsIO) {
			core.addTrap(location, Core.Trap.IO);
		}
		assertEquals("Add IO traps - count", locationsIO.size(), core.getTraps(Core.Trap.IO).size());
		assertThat("Add IO traps - locations ", core.getTraps(Core.Trap.IO),
				hasItems(0X0300, 0X0500, 0X0700, 0X0900, 0X0B00, 0X0D00, 0X0F00));

		core.removeTrap(0X0200, Core.Trap.DEBUG);
		core.removeTrap(0X0400, Core.Trap.DEBUG);
		assertEquals("Remove Debug traps - count", locationsDebug.size() - 2, core.getTraps().size());
		assertThat("Remove Debug traps - locations ", core.getTraps(),
				hasItems(0X0800, 0X0A00, 0X0C00, 0X0E00));
		assertEquals("Remove Debug traps - IO traps  count", locationsIO.size(), core.getTraps(Core.Trap.IO).size());
		assertThat("Remove Debug traps - IO traps locations ", core.getTraps(Core.Trap.IO),
				hasItems(0X0300, 0X0500, 0X0700, 0X0900, 0X0B00, 0X0D00, 0X0F00));

		core.removeTraps(Core.Trap.IO);
		assertEquals("Remove IO traps - IO trap count", 0, core.getTraps(Core.Trap.IO).size());
		assertEquals("Remove IO traps- Debug  count", locationsDebug.size() - 2, core.getTraps().size());
		assertThat("Remove IO traps- Debug  locations ", core.getTraps(),
				hasItems(0X0800, 0X0A00, 0X0C00, 0X0E00));
		core.removeTraps(Core.Trap.DEBUG);
		assertEquals("Remove All traps - IO trap count", 0, core.getTraps(Core.Trap.IO).size());
		assertEquals("Remove All traps- Debug  count", 0, core.getTraps(Core.Trap.DEBUG).size());

		core = null;
	}

	@Test
	public void testSimpleReadAndWrite() {
		Core core = new Core(24 * K);
		int location;
		byte value;
		ArrayList<Integer> locations =
				new ArrayList<Integer>(Arrays.asList(0X0200, 0X0400, 0X0800, 0X0A00, 0X0C00, 0X0E00));

		ArrayList<Byte> values =
				new ArrayList<Byte>(Arrays.asList((byte) 0X00, (byte) 0X0FF, (byte) 0X0AA, (byte) 0X055, (byte) 0X0C3,
						(byte) 0X03C));
		String msg;

		for (int loc = 0; loc < locations.size(); loc++) {
			for (int val = 0; val < values.size(); val++) {
				location = locations.get(loc);
				value = values.get(val);
				core.write(location, value);
				msg = String.format("Read and write: Location %04X,  Value %02X", location, value);
				assertThat("Simple Read and write", value, equalTo(core.read(location)));
			}// for value
		}// for location
		core = null;
	}

	@Test
	public void testReadAndWriteForIO() {
		Core core = new Core(24 * K);
		int location;
		byte value;
		ArrayList<Integer> locations =
				new ArrayList<Integer>(Arrays.asList(0X0200, 0X0400, 0X0800, 0X0A00, 0X0C00, 0X0E00));

		ArrayList<Byte> values =
				new ArrayList<Byte>(Arrays.asList((byte) 0X00, (byte) 0X0FF, (byte) 0X0AA, (byte) 0X055, (byte) 0X0C3,
						(byte) 0X03C));
		String msg;

		for (int loc = 0; loc < locations.size(); loc++) {
			for (int val = 0; val < values.size(); val++) {
				location = locations.get(loc);
				value = values.get(val);
				core.writeForIO(location, value);
				msg = String.format("Read and write: Location %04X,  Value %02X", location, value);
				assertThat("Read and write for IO", value, equalTo(core.readForIO(location)));
			}// for value
		}// for location
		core = null;
	}

	@Test
	public void testReadAndWriteDMA() {
		Core core = new Core(24 * K);
		byte[] values1 = { (byte) 0X00, (byte) 0X0FF, (byte) 0X0AA, (byte) 0X055, (byte) 0X0C3, (byte) 0X03C,
				(byte) 0X00, (byte) 0X0FF, (byte) 0X0AA, (byte) 0X055, (byte) 0X0C3, (byte) 0X03C,
				(byte) 0X00, (byte) 0X0FF, (byte) 0X0AA, (byte) 0X055, (byte) 0X0C3, (byte) 0X03C };
		int numberOfBytes = values1.length;
		byte[] values2 = new byte[numberOfBytes];
		int maxMemory = core.getSize();
		int location = 0;
		core.writeDMA(location, values1);
		assertThat("Good DMA read and write",values1,equalTo(core.readDMA(0, numberOfBytes)));

		accessErrorLocation = 0;
		core.addMemoryAccessErrorListener(new MemoryAccessErrorListener() {
			@Override
			public void memoryAccessError(MemoryAccessErrorEvent me) {
				accessErrorLocation = me.getLocation();
				System.out.println("testReadAndWriteDMA \n" + me.getMessage() +"\n");
			}
		});// addMemoryAccessErrorListener
		int badLocation = maxMemory - numberOfBytes + 10;
		core.writeDMA(badLocation , values1);
		assertThat("write to a bad location ", badLocation, equalTo(accessErrorLocation));
		
		
		core = null;
	}

	@Test
	public void testEvents() {
		// Access violation
		accessErrorLocation = 0;
		Core core = new Core(24 * K);
		core.addMemoryAccessErrorListener(new MemoryAccessErrorListener() {
			@Override
			public void memoryAccessError(MemoryAccessErrorEvent me) {
				accessErrorLocation = me.getLocation();
				System.out.println("testEvents 1\n" + me.getMessage() +"\n");
			}
		});// addMemoryAccessErrorListener
		int badLocation = 65 * K;
		assertThat("write to a bad location ", 0, equalTo(accessErrorLocation));
		core.write(badLocation, (byte) 0x0FF);
		assertThat("write to a bad location ", badLocation, equalTo(accessErrorLocation));

		// IO
		trapLocation = 0;
		core.addMemoryTrapListener(new MemoryTrapListener() {
			@Override
			public void memoryTrap(MemoryTrapEvent mte) {
				trapLocation = mte.getLocation();
				System.out.println("testEvents 2\n" + mte.getMessage() +"\n");
			}
		});// addMemoryTrapListener
		trapLocation = 00;
		int location = 0X40;
		byte zero = (byte) 0X00;
		core.write(location, (byte) zero);
		byte valueWrite = (byte) 0X80;
		core.addTrap(location, Core.Trap.IO);
		assertThat("write to IO Trap location ", 0, equalTo(trapLocation));
		assertThat("trapLocation before IO Trap set", zero, equalTo(core.read(location)));
		byte valueRead = core.read(location);
		// after write
		core.write(location, valueWrite);
		assertThat("write to IO Trap location ", location, equalTo(trapLocation));
		assertThat("write to IO Trap location Value", valueWrite, equalTo(core.read(location)));

		// Debug
		trapLocation = 00;
		location = 0X200;
		valueWrite = (byte) 0XFF;
		core.addTrap(location, Core.Trap.DEBUG);
		core.setDebugTrapEnabled(false);
		core.write(location, valueWrite);
		assertThat("write to Debug Trap location - not enabled ", trapLocation, equalTo(trapLocation));
		assertThat("write to Debug Trap Value - not enabled ", valueWrite, equalTo(core.read(location)));

		core.setDebugTrapEnabled(true);
		core.write(location, valueWrite);

		assertThat("write to Debug Trap location -  enabled ", trapLocation, equalTo(trapLocation));

		assertThat("read Debug location first time", (byte) 0X76, equalTo(core.read(location)));
		assertThat("read Debug location second time", valueWrite, equalTo(core.read(location)));

		core = null;
	}

	// @Test
	// public void test() {
	// Core core = new Core(24 * K);
	//
	//
	// core = null;
	// }

}
