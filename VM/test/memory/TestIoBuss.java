package memory;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;


import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class TestIoBuss implements Observer {
	static int K = 1024;
	int badLocation;
	Core core;
	IoBuss ioBuss;

	@Before
	public void setUp() throws Exception {
		core = Core.getCore(25 * K);
		ioBuss = new IoBuss();
	}//setUp

	@Test
	public void testReadAndWrite() {
		
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
				ioBuss.write(location, value);
				assertThat("Read and write for IO", value, equalTo(ioBuss.read(location)));
			}// for value
		}// for location
		ioBuss = null;
	}// testReadAndWriteForIO

	@Test
	public void testReadAndWriteDMA() {
		
		byte[] values1 = { (byte) 0X00, (byte) 0X0FF, (byte) 0X0AA, (byte) 0X055, (byte) 0X0C3, (byte) 0X03C,
				(byte) 0X00, (byte) 0X0FF, (byte) 0X0AA, (byte) 0X055, (byte) 0X0C3, (byte) 0X03C,
				(byte) 0X00, (byte) 0X0FF, (byte) 0X0AA, (byte) 0X055, (byte) 0X0C3, (byte) 0X03C };
		int numberOfBytes = values1.length;
		int maxMemory = ioBuss.getSize();
		int location = 0;
		ioBuss.writeDMA(location, values1);
		assertThat("Good DMA read and write", values1, equalTo(ioBuss.readDMA(0, numberOfBytes)));

		core.addObserver(this);
		badLocation = 00;

		int thisLocation = maxMemory - numberOfBytes + 10;
		ioBuss.writeDMA(thisLocation, values1);
		assertThat("write to a bad location ", thisLocation +numberOfBytes, equalTo(badLocation));

		ioBuss = null;
	}// testReadAndWriteDMA

	@Override
	public void update(Observable arg0, Object arg1) {
		
		MemoryTrapEvent mte = (MemoryTrapEvent) arg1;
		System.out.println("Observable \n" + mte.getMessage() + "\n");

		Core.Trap trap = mte.getTrap();
		switch (trap) {
		case IO:
			badLocation = mte.getLocation() + 1;// force error if it gets here
			break;
		case DEBUG:
			badLocation = mte.getLocation() + 2;// force error if it gets here
			break;
		case INVALID:
			badLocation = mte.getLocation();
			break;
		default:
		}//switch

	}// update

	// @Test
	// public void test() {
	// fail("Not yet implemented");
	// }

}
