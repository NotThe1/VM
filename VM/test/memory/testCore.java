package memory;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;


public class testCore implements Observer{
	static int K = 1024;
	int badLocation;
	int trapLocation;

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

		// enable to check out JOption Pane re using default memory size
		// coreConstructor = null;
		// size = 125;
		// coreConstructor = new Core(size * K);
		// assertThat("constructor Too large", (64 * K), equalTo(coreConstructor.getSize()));
		core = null;
	}

	@Test
	public void testTraps() {
		Core core = new Core(24 * K);
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

		for (int loc = 0; loc < locations.size(); loc++) {
			for (int val = 0; val < values.size(); val++) {
				location = locations.get(loc);
				value = values.get(val);
				core.write(location, value);
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

		for (int loc = 0; loc < locations.size(); loc++) {
			for (int val = 0; val < values.size(); val++) {
				location = locations.get(loc);
				value = values.get(val);
				core.writeForIO(location, value);
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
		int maxMemory = core.getSize();
		int location = 0;
		core.writeDMA(location, values1);
		assertThat("Good DMA read and write", values1, equalTo(core.readDMA(0, numberOfBytes)));

		badLocation = 0;
		 core.addObserver(this);
		badLocation = 00;

		int thisLocation = maxMemory - numberOfBytes + 10;
		core.writeDMA(thisLocation, values1);
		assertThat("write to a bad location ", thisLocation, equalTo(badLocation));

		core = null;
	}

	@Test
	public void testEvents() {
		// Access violation
		badLocation = 0;
		Core core = new Core(24 * K);
		 core.addObserver(this);

		int ThisLocation = 65 * K;
		assertThat("write to a bad location ", 0, equalTo(badLocation));
		core.write(ThisLocation, (byte) 0x0FF);
		assertThat("write to a bad location ", ThisLocation, equalTo(badLocation));

		// IO
		trapLocation = 0;
		core.addObserver(this);
		trapLocation = 00;
		int location = 0X40;
		byte zero = (byte) 0X00;
		core.write(location, (byte) zero);
		byte valueWrite = (byte) 0X80;
		core.addTrap(location, Core.Trap.IO);
		assertThat("write to IO Trap location ", 0, equalTo(trapLocation));
		assertThat("trapLocation before IO Trap set", zero, equalTo(core.read(location)));
		core.read(location);
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

	@Test
	public void testReadAndWriteEord() {
		Core core = new Core(24 * K);
		int location = 0X0100;
		final byte value55 = (byte) 0X055;
		final byte valueAA = (byte) 0X0AA;
		final byte valueC3 = (byte) 0X0C3;
		final byte value3C = (byte) 0X03C;
		final byte valueFF = (byte) 0X0FF;

		// fill memory
		for (int index = 0; index < 16; index++) {
			core.write(location + index, valueFF);
		}// for
		for (int index = 0; index < 16; index++) {
			assertThat("Confirm memory setup", valueFF, equalTo(core.read(location + index)));
		}// for

		core.writeWord(location, valueAA, value55);
		int ans = ((valueAA << 8) + (value55 & 0X0FF)) & 0XFFFF;
		assertThat("Word read", ans, equalTo(core.readWord(location)));

		ans = ((value55 << 8) + (valueAA & 0X0FF)) & 0XFFFF;
		assertThat("Word read reverse", ans, equalTo(core.readWordReversed(location)));

		core.writeWord(location, valueC3, value3C);
		ans = ((valueC3 << 8) + (value3C & 0X0FF)) & 0XFFFF;
		assertThat("Word read", ans, equalTo(core.readWord(location)));

		ans = ((value3C << 8) + (valueC3 & 0X0FF)) & 0XFFFF;
		assertThat("Word read reversed", ans, equalTo(core.readWordReversed(location)));

		core = null;
	}

	@Test
	public void testPushPop() {
		Core core = new Core(24 * K);
		int location = 0X0100;
		final byte valueFF = (byte) 0X0FF;

		// fill memory
		for (int index = 0; index < 16; index++) {
			core.write(location + index, valueFF);
		}// for
		for (int index = 0; index < 16; index++) {
			assertThat("Confirm memory setup", valueFF, equalTo(core.read(location + index)));
		}// for
		byte hiByte = (byte) 0X55;
		byte loByte = (byte) 0XAA;
		
		int ans = ((hiByte << 8) | (loByte & 0X00FF)) & 0XFFFF;
		core.pushWord(location, hiByte, loByte);
		assertThat("Pus and Pop",ans,equalTo(core.popWord(location-2)));

		core = null;
	}

	   @Test
	 public void testDebugNotify() {
	 Core core = new Core(24 * K);
	 int testLocation = 0X0100;
	 trapLocation = 0;
	 core.addObserver(this);
	 assertThat("Observable test",0,equalTo(trapLocation));
	 
	 core.addTrap(testLocation, Core.Trap.DEBUG);
	 core.read(testLocation);
	 assertThat("Observable test before",0,equalTo(trapLocation));
	 
	 core.setDebugTrapEnabled(true);
	 core.read(testLocation);
	 assertThat("Observable test after",testLocation,equalTo(trapLocation));

	
		core = null;
	 }

	@Override
	public void update(Observable obs, Object arg) {
		MemoryTrapEvent mte = (MemoryTrapEvent)arg;
		
		Core.Trap trap  = mte.getTrap();
		switch (trap){
		case IO:
		case DEBUG:
			trapLocation = mte.getLocation();
			break;
		case INVALID:
			badLocation = mte.getLocation();
			break;
		default:
		}
		System.out.println("Observable \n" + mte.getMessage() + "\n");	
	}



	// // @Test
	// public void test() {
	// Core core = new Core(24 * K);
	//
	//
	// core = null;
	// }

}
