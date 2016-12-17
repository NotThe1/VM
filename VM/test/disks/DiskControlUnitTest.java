package disks;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import memory.CpuBuss;
import memory.IoBuss;

public class DiskControlUnitTest {

	String[] diskPaths = new String[] { "C:\\Users\\admin\\VMdata\\Disks\\TestA.F5DD",
			"C:\\Users\\admin\\VMdata\\Disks\\TestB.F5DD", "C:\\Users\\admin\\VMdata\\Disks\\TestC.F5DD",
			"C:\\Users\\admin\\VMdata\\Disks\\TestD.F5DD" };

	CpuBuss cpuBuss;
	IoBuss ioBuss;
	DiskControlUnit dcu;
	// static File testDisk;
	//
	// @BeforeClass
	// public static void setUpBeforeClass() throws Exception {
	// testDisk = MakeNewDisk.makeNewDisk();
	// }

	@Before
	public void setUp() {
		cpuBuss = CpuBuss.getInstance();
		ioBuss = IoBuss.getInstance();
//		dcu = new DiskControlUnit(cpuBuss);
		dcu = DiskControlUnit.getgetInstance();
	}// setUp

	@Test
	public void testDiskControlUnitCpuSimple() {
		assertThat("Max Drives", 4, equalTo(dcu.getMaxNumberOfDrives()));
		DiskDrive[] disks = dcu.getDrives();
		int diskCount = disks.length;
		assertThat("drives slots", 4, equalTo(diskCount));

		DiskDrive diskDrive;
		for (int i = 0; i < diskCount; i++) {
			diskDrive = disks[i];
			assertThat("Empty drives Start", null, equalTo(diskDrive));
		} // for

		for (int i = 0; i < diskCount; i++) {
			dcu.addDiskDrive(i, diskPaths[i]);
		} // add disks

		disks = dcu.getDrives();
		for (int i = 0; i < diskCount; i++) {
			assertThat("Added disks", diskPaths[i], equalTo(disks[i].getFileAbsoluteName()));
		} // paths match up

		for (int i = 0; i < diskCount; i++) {
			dcu.removeDiskDrive(i);
		} // remove all disks

		for (int i = 0; i < diskCount; i++) {
			diskDrive = disks[i];
			assertThat("Empty drives End", null, equalTo(diskDrive));
		} // for

	}// testDiskControlUnitCpuSimple

	@Test
	public void testCurrentDisk() {
		DiskDrive[] disks = dcu.getDrives();
		int diskCount = disks.length;
		for (int i = 0; i < diskCount; i++) {
			dcu.addDiskDrive(i, diskPaths[i]);
		} // add disks
		for (int i = 0; i < diskCount; i++) {
			dcu.setCurrentDrive(i);
			assertThat("Current Disk", i, equalTo(dcu.getCurrentDrive()));
		} // add disks
	}// testCurrentDisk

	@Test
	public void testReadCurrentDisk() {
		int DMA = 0X1000;

		byte[] filler = new byte[SECTOR_SIZE];
		Arrays.fill(filler, MINUS_ONE);
		ioBuss.writeDMA(DMA, filler);
		assertThat("Test DMA buffer has -1", filler, equalTo(ioBuss.readDMA(DMA, filler.length)));

		setUpControlTable(DISK_READ, DMA);
		dcu.addDiskDrive(0, diskPaths[0]);
		cpuBuss.write(CONTROL_BYTE_LOC, (byte) 0X80); /* Start the IO */

		assertThat("Status after read", (byte) 0X80, equalTo(ioBuss.read(STATUS_LOC)));

		// assertThat("After Read, Test DMA buffer has -1",filler,equalTo(ioBuss.readDMA(DMA, filler.length)));

	}// testReadCurrentDisk

	@Test
	public void testWriteCurrentDisk() {
		int DMA = 0X1000;

		byte[] filler1 = new byte[SECTOR_SIZE];
		byte[] filler0 = new byte[SECTOR_SIZE];
		Arrays.fill(filler0, (byte) 0X00);
		Arrays.fill(filler1, MINUS_ONE);
		ioBuss.writeDMA(DMA, filler1);
		assertThat(" Before, Test DMA buffer has -1's", filler1, equalTo(ioBuss.readDMA(DMA, filler1.length)));
		dcu.addDiskDrive(0, diskPaths[0]);

		/* write 1's */
		setUpControlTable(DISK_WRITE, DMA);
		cpuBuss.write(CONTROL_BYTE_LOC, (byte) 0X80); /* Start the IO */
		assertThat("Status after write", (byte) 0X80, equalTo(ioBuss.read(STATUS_LOC)));

		ioBuss.writeDMA(DMA, filler0);
		assertThat("Test DMA buffer has 00's", filler0, equalTo(ioBuss.readDMA(DMA, filler0.length)));

		/* read 1's */
		setUpControlTable(DISK_READ, DMA);
		cpuBuss.write(CONTROL_BYTE_LOC, (byte) 0X80); /* Start the IO */
		assertThat("Status after write", (byte) 0X80, equalTo(ioBuss.read(STATUS_LOC)));
		assertThat("After, Test DMA buffer has -1's", filler1, equalTo(ioBuss.readDMA(DMA, filler1.length)));

	}// testWriteCurrentDisk

	public void setUpControlTable(byte operation, int DMA) {
		/* load first page with -1 & test */
		byte[] filler = new byte[SECTOR_SIZE];
		Arrays.fill(filler, MINUS_ONE);
		ioBuss.writeDMA(0, filler);
		assertThat("Fill with MINUS_ONE", MINUS_ONE, equalTo(ioBuss.read(0)));

		/* setup the control table pointer */
		byte[] controlTablePointer = new byte[] { (byte) 0X00, (byte) 0X01 }; // point to control table
		ioBuss.writeDMA(CONTROL_BYTE_LOC + 1, controlTablePointer); // set up pointer

		/* set up the control table */
		byte[] controlTable = new byte[] { (byte) 0X01, (byte) 0X00, (byte) 0X00, (byte) 0X00, (byte) 0X02, (byte) 0X00,
				(byte) 0X06, (byte) 0X11, (byte) 0X11, (byte) 0X43, (byte) 0X00, (byte) 0X45, (byte) 0X00 };
		/* ReadWriteCode, U=0,H=0,T=0,S=2,Count = 3*512,DMA =1000,status block = 0043, next table = 0045 */
		controlTable[0] = operation;
		controlTable[7] = (byte) (DMA & 0XFF);
		controlTable[8] = (byte) ((DMA & 0XFF00) >> 8);

		ioBuss.writeDMA(DCT_LOC, controlTable);
		assertThat("After MINUS_ONE", controlTable[0], equalTo(ioBuss.read(DCT_LOC)));
	}// setUpControlTable

	final byte DISK_READ = (byte) 0X01;
	final byte DISK_WRITE = (byte) 0X02;
	final byte MINUS_ONE = (byte) 0XFF;
	final int STATUS_LOC = 0X043;
	final int DCT_LOC = 0X0100;
	final int CONTROL_BYTE_LOC = 0X45;
	final int SECTOR_SIZE = 512;

}// class DiskControlUnitTest
