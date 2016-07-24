package memory;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class TestCore implements Observer {
	static int K = 1024;
	int badLocation;
	// int trapLocation;
	Core core;


	@Before
	public void setUp() throws Exception {
		core = Core.getInstance();
	}//setUp


//	@Test
//	public void testConstructor() {
//
//		// assertThat("constructor Default size", (64 * K), equalTo(core.getSize()));
//		// assertThat("constructor Default size", 64, equalTo(core.getSizeInK()));
//
////		int size = 25;
////		int sizeInBytes = size * K;
////		assertThat("constructor Default size", size, equalTo(core.getSizeInK()));
////		assertThat("constructor 25K", (sizeInBytes), equalTo(core.getSize()));
//
//		// enable to check out JOption Pane re using default memory size
//		// Core coreConstructor = null;
//		// int size = 125;
//		// coreConstructor = Core.getCore(size * K);
//		// assertThat("constructor Too large", (64 * K), equalTo(coreConstructor.getSize()));
//		// coreConstructor = null;
//	}

	@Override
	public void update(Observable arg0, Object arg1) {
		MemoryTrapEvent mte = (MemoryTrapEvent) arg1;
		badLocation = mte.getLocation();
		System.out.println(mte.getMessage());
	}// update

	@Test
	public void testBadAddress() {
		badLocation = 0;
		core.addObserver(this);
		int location = 66 * K;
		byte value = (byte) 0XFF;

		core.write(location, value);
		assertThat("Bad write address", badLocation, equalTo(location));

		location = -1;
		core.read(location);
		assertThat("Bad read address", badLocation, equalTo(location));
		core.deleteObserver(this);
	}// testBadAddress



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
				core.write(location, value);
				assertThat("Simple Read and write", value, equalTo(core.read(location)));
			}// for value
		}// for location
		core = null;
	}//testSimpleReadAndWrite



	// // @Test
	// public void test() {
	// Core core = new Core(24 * K);
	//
	//
	// core = null;
	// }

}
