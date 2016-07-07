package memory;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class TestCpuBuss implements Observer {
	static int K = 1024;
	int badLocation;
	int trapLocation;
	Core core;
	CpuBuss cpuBuss;

	@Before
	public void setUp() throws Exception {
		core = Core.getCore(25 * K);
		cpuBuss = CpuBuss.getCpuBuss();
	}//setUp
	
	@Test
	public void testSimpleReadAndWrite() {
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
				cpuBuss.write(location, value);
				assertThat("Simple Read and write", value, equalTo(cpuBuss.read(location)));
			}// for value
		}// for location
	}//testSimpleReadAndWrite

	@Override
	public void update(Observable arg0, Object arg1) {
		MemoryTrapEvent mte = (MemoryTrapEvent) arg1;
		System.out.println("Observable \n" + mte.getMessage() + "\n");

		Core.Trap trap = mte.getTrap();
		switch (trap) {
		case IO:
			trapLocation = mte.getLocation();
			break;
		case DEBUG:
			trapLocation = mte.getLocation();
			break;
		case INVALID:
			badLocation = mte.getLocation();
			break;
		default:
		}//switch

	}// update

	@Test
	public void testBadAddress() {
		badLocation = 0;

		core = Core.getCore();
		core.addObserver(this);
		int location = 66 * K;
		byte value = (byte) 0XFF;

		cpuBuss.write(location, value);
		assertThat("Bad write address", badLocation, equalTo(location));

		location = -1;
		value = cpuBuss.read(location);
		assertThat("Bad read address", badLocation, equalTo(location));
		core.deleteObserver(this);
	}// testBadAddress

	@Test
	public void testTraps() {

		List<Integer> locationsDebug =
				new ArrayList<Integer>(Arrays.asList(0X0200, 0X0400, 0X0800, 0X0A00, 0X0C00, 0X0E00));

		List<Integer> locationsIO =
				new ArrayList<Integer>(Arrays.asList(0X0300, 0X0500, 0X0700, 0X0900, 0X0B00, 0X0D00, 0X0F00));

		assertEquals("Trap on new Core", 0, cpuBuss.getTraps().size());
		assertEquals("Trap on new Core", 0, cpuBuss.getTraps(Core.Trap.IO).size());

		for (Integer location : locationsDebug) {
			cpuBuss.addTrap(location, Core.Trap.DEBUG);
		}
		assertEquals("Add Debug traps - count", locationsDebug.size(), cpuBuss.getTraps().size());
		assertThat("Add Debug traps - locations ", cpuBuss.getTraps(),
				hasItems(0X0200, 0X0400, 0X0800, 0X0A00, 0X0C00, 0X0E00));

		for (Integer location : locationsIO) {
			cpuBuss.addTrap(location, Core.Trap.IO);
		}
		assertEquals("Add IO traps - count", locationsIO.size(), cpuBuss.getTraps(Core.Trap.IO).size());
		assertThat("Add IO traps - locations ", cpuBuss.getTraps(Core.Trap.IO),
				hasItems(0X0300, 0X0500, 0X0700, 0X0900, 0X0B00, 0X0D00, 0X0F00));

		cpuBuss.removeTrap(0X0200, Core.Trap.DEBUG);
		cpuBuss.removeTrap(0X0400, Core.Trap.DEBUG);
		assertEquals("Remove Debug traps - count", locationsDebug.size() - 2, cpuBuss.getTraps().size());
		assertThat("Remove Debug traps - locations ", cpuBuss.getTraps(),
				hasItems(0X0800, 0X0A00, 0X0C00, 0X0E00));
		assertEquals("Remove Debug traps - IO traps  count", locationsIO.size(), cpuBuss.getTraps(Core.Trap.IO).size());
		assertThat("Remove Debug traps - IO traps locations ", cpuBuss.getTraps(Core.Trap.IO),
				hasItems(0X0300, 0X0500, 0X0700, 0X0900, 0X0B00, 0X0D00, 0X0F00));

		cpuBuss.removeTraps(Core.Trap.IO);
		assertEquals("Remove IO traps - IO trap count", 0, cpuBuss.getTraps(Core.Trap.IO).size());
		assertEquals("Remove IO traps- Debug  count", locationsDebug.size() - 2, cpuBuss.getTraps().size());
		assertThat("Remove IO traps- Debug  locations ", cpuBuss.getTraps(),
				hasItems(0X0800, 0X0A00, 0X0C00, 0X0E00));
		cpuBuss.removeTraps(Core.Trap.DEBUG);
		assertEquals("Remove All traps - IO trap count", 0, cpuBuss.getTraps(Core.Trap.IO).size());
		assertEquals("Remove All traps- Debug  count", 0, cpuBuss.getTraps(Core.Trap.DEBUG).size());

	}// testTraps

	@Test
	public void testReadAndWriteWord() {
		int location = 0X0100;
		final byte value55 = (byte) 0X055;
		final byte valueAA = (byte) 0X0AA;
		final byte valueC3 = (byte) 0X0C3;
		final byte value3C = (byte) 0X03C;
		final byte valueFF = (byte) 0X0FF;

		// fill memory
		for (int index = 0; index < 16; index++) {
			cpuBuss.write(location + index, valueFF);
		}// for
		for (int index = 0; index < 16; index++) {
			assertThat("Confirm memory setup", valueFF, equalTo(cpuBuss.read(location + index)));
		}// for

		cpuBuss.writeWord(location, valueAA, value55);
		int ans = ((valueAA << 8) + (value55 & 0X0FF)) & 0XFFFF;
		assertThat("Word read", ans, equalTo(cpuBuss.readWord(location)));

		ans = ((value55 << 8) + (valueAA & 0X0FF)) & 0XFFFF;
		assertThat("Word read reverse", ans, equalTo(cpuBuss.readWordReversed(location)));

		cpuBuss.writeWord(location, valueC3, value3C);
		ans = ((valueC3 << 8) + (value3C & 0X0FF)) & 0XFFFF;
		assertThat("Word read", ans, equalTo(cpuBuss.readWord(location)));

		ans = ((value3C << 8) + (valueC3 & 0X0FF)) & 0XFFFF;
		assertThat("Word read reversed", ans, equalTo(cpuBuss.readWordReversed(location)));

	}// testReadAndWriteWord

	@Test
	public void testPushPop() {

		int location = 0X0100;
		final byte valueFF = (byte) 0X0FF;

		// fill memory
		for (int index = 0; index < 16; index++) {
			cpuBuss.write(location + index, valueFF);
		}// for
		for (int index = 0; index < 16; index++) {
			assertThat("Confirm memory setup", valueFF, equalTo(cpuBuss.read(location + index)));
		}// for
		byte hiByte = (byte) 0X55;
		byte loByte = (byte) 0XAA;

		int ans = ((hiByte << 8) | (loByte & 0X00FF)) & 0XFFFF;
		cpuBuss.pushWord(location, hiByte, loByte);
		assertThat("Push and Pop", ans, equalTo(cpuBuss.popWord(location - 2)));
		location = 110;
		cpuBuss.pushWord(location, ans);
		assertThat("Push and Pop value", ans, equalTo(cpuBuss.popWord(location - 2)));
		

	}// testPushPop

	@Test
	public void testEvents() {
		// Access violation
		cpuBuss.addObserver(this);
		
		 int location = 0X40;		
		 byte valueWrite = (byte) 0X80;

//	 IO
		 trapLocation = 00;
		 byte zero = (byte) 0X00;
		 cpuBuss.write(location, (byte) zero);
		 cpuBuss.addTrap(location, Core.Trap.IO);
		 assertThat("write to IO Trap location ", 0, equalTo(trapLocation));
		 assertThat("trapLocation before IO Trap set", zero, equalTo(cpuBuss.read(location)));
		 cpuBuss.read(location);
		 // after write
		 cpuBuss.write(location, valueWrite);
		 assertThat("write to IO Trap location ", location, equalTo(trapLocation));
		 assertThat("write to IO Trap location Value", valueWrite, equalTo(cpuBuss.read(location)));
		
 // Debug
		 trapLocation = 00;
		 location = 0X200;
		 valueWrite = (byte) 0XFF;
		 cpuBuss.addTrap(location, Core.Trap.DEBUG);
		 cpuBuss.setDebugTrapEnabled(false);
		 cpuBuss.write(location, valueWrite);
		 assertThat("write to Debug Trap location - not enabled ", trapLocation, equalTo(trapLocation));
		 assertThat("write to Debug Trap Value - not enabled ", valueWrite, equalTo(cpuBuss.read(location)));
		
		 cpuBuss.setDebugTrapEnabled(true);
		 cpuBuss.write(location, valueWrite);
		
		
		 assertThat("read Debug location first time", (byte) 0X76, equalTo(cpuBuss.read(location)));
		 assertThat("write to Debug Trap location -  enabled ", trapLocation, equalTo(location));
		 assertThat("read Debug location second time", valueWrite, equalTo(cpuBuss.read(location)));
		 
		 cpuBuss.deleteObserver(this);

	}// testEvents

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }

}
