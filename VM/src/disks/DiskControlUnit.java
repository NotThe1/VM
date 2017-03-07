package disks;

import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JOptionPane;

import disks.diskPanel.DiskPanel;
import memory.Core;
import memory.CpuBuss;
import memory.IoBuss;
import memory.MemoryTrapEvent;

//public class DiskControlUnit implements MemoryTrapListener, MemoryAccessErrorListener, VDiskErrorListener {
public class DiskControlUnit implements Observer, VDiskErrorListener {

	// private Core core;
	private CpuBuss cpuBuss;
	private IoBuss ioBuss;
	private DiskDrive[] drives;
	private int maxNumberOfDrives;

	private int currentDrive;
	private int currentDiskControlByte;
	private byte currentCommand;
	private int currentUnit;
	private int currentHead;
	private int currentTrack;
	private int currentSector;
	private int currentByteCount;
	private int currentDMAAddress;
	private boolean goodOperation;

	private static DiskControlUnit instance = new DiskControlUnit(CpuBuss.getInstance(), 4);

	//
	private DiskControlUnit(CpuBuss cpuBuss, int maxNumberOfDrives) {
		this.cpuBuss = cpuBuss;
		this.ioBuss = IoBuss.getInstance();
		cpuBuss.addObserver(this);
		cpuBuss.addTrap(DISK_CONTROL_BYTE_5, Core.Trap.IO);
		this.maxNumberOfDrives = maxNumberOfDrives;
		drives = new DiskDrive[maxNumberOfDrives];
	}// Constructor

	private DiskControlUnit(CpuBuss cpuBuss) {
		this(cpuBuss, 4);
	}// Constructor

	public static DiskControlUnit getgetInstance() {
		return instance;
	}//

	public void close() { // remove the core listeners
		cpuBuss.deleteObserver(this);
		cpuBuss.removeTraps(Core.Trap.IO);
		for (int i = 0; i < drives.length; i++) {
			if (drives[i] != null) {
				drives[i].removeVDiskErroListener(this);
				removeDiskDrive(i);
			} // if
		} // for
	}// close
	
	public boolean isBootDiskLoaded(){
		return drives[0]!= null?true:false;
	}//isBootDiskLoaded

	public boolean addDiskDrive(int index, String fileName) {

		if (drives[index] != null) {
			JOptionPane.showMessageDialog(null, "Drive has disk already", "addDiskDrive", JOptionPane.WARNING_MESSAGE);
			return false;
		} // if

		for (DiskDrive d : drives) {
			if (d == null) {
				continue;
			} // if skip empty slots

			if (d.getFileAbsoluteName().equals(fileName)) {
				JOptionPane.showMessageDialog(null, " Disk is already Mounted", "addDiskDrive",
						JOptionPane.WARNING_MESSAGE);
				return false;
			} // if - used 2X

		} // for
		drives[index] = new DiskDrive(fileName);
		drives[index].addVDiskErroListener(this);
		return true;
	}// addDiskDrive

	public void removeDiskDrive(int index) {
		if (drives[index] == null) {
			JOptionPane.showMessageDialog(null, "Already Dismounted", "removeDiskDrive", JOptionPane.WARNING_MESSAGE);
			return;
		} // if
		drives[index].removeVDiskErroListener(this);
		drives[index].dismount();
		drives[index] = null;
	}// removeDiskDrive
	
	public void removeAllDiskDrives(DiskPanel diskPanel){
		for (int i = 0; i < drives.length; i++) {
			if (drives[i] != null) {
				//drives[i].removeVDiskErroListener(this);
				removeDiskDrive(i);
			} // if
		} // for
		diskPanel.noDisks();
	}//removeAllDiskDrives

	public int getMaxNumberOfDrives() {
		return maxNumberOfDrives;
	}// getMaxNumberOfDrives

	public int getCurrentDrive() {
		return currentDrive;
	}// getCurrentDrive

	public void setCurrentDrive(int currentDrive) {
		if ((currentDrive >= 0) & (currentDrive < maxNumberOfDrives)) {
			this.currentDrive = currentDrive;
		} // if
	}// setCurrentDrive

	public DiskDrive[] getDrives() {
		return drives;
	}// getDrives

	@Override
	public void update(Observable o, Object event) {

		if (((MemoryTrapEvent) event).getTrap().equals(Core.Trap.DEBUG)) {
			return; // Don't care
		} // if

		// we have an IO trap
		int trapLocation = ((MemoryTrapEvent) event).getLocation() & 0XFFF;
		currentDiskControlByte = trapLocation; // 0X0040 for 8" / 0X0045 for 5"

		if ((ioBuss.read(currentDiskControlByte) & 0X80) == 0) {
			return; // not a disk activation command
		} // if

		goodOperation = true; // assume all goes well

//		System.out.printf("DCU: Location: %04X, Value: %02X%n", currentDiskControlByte,
//				ioBuss.read(currentDiskControlByte));

		int controlTableLocation = cpuBuss.readWordReversed(currentDiskControlByte + 1);
		currentCommand = ioBuss.read(controlTableLocation + DCT_COMMAND);
		currentUnit = ioBuss.read(controlTableLocation + DCT_UNIT);
		currentHead = ioBuss.read(controlTableLocation + DCT_HEAD);
		currentTrack = ioBuss.read(controlTableLocation + DCT_TRACK);
		currentSector = ioBuss.read(controlTableLocation + DCT_SECTOR);
		currentByteCount = cpuBuss.readWordReversed(controlTableLocation + DCT_BYTE_COUNT);
		currentDMAAddress = cpuBuss.readWordReversed(controlTableLocation + DCT_DMA_ADDRESS);
		debugShowControlTable();
		currentDrive = (currentDiskControlByte == DISK_CONTROL_BYTE_5) ? 0 : 2; // 5" => A or B
		currentDrive += currentUnit;

		if (currentDrive >= maxNumberOfDrives) {
			diskErrorReport(ERROR_NO_DRIVE, String.format("No unit %d", currentDrive));
			return;
		} // if

		if (drives[currentDrive] == null) {
			diskErrorReport(ERROR_NO_DISK, String.format(" No disk in unit %d", currentDrive));
			return;
		} // if

		int currentSectorSize = drives[currentDrive].getBytesPerSector();
		if (!drives[currentDrive].setCurrentAbsoluteSector(currentHead, currentTrack, currentSector)) {
			diskErrorReport(ERROR_SECTOR_NOT_SET, "Sector not set properly");
			return;
		} //
		int numberOfSectorsToMove = currentByteCount / currentSectorSize;
		if (numberOfSectorsToMove < 0) {
			diskErrorReport(ERROR_INVALID_BYTE_COUNT, String.format("Invalid Byte Count: %04X", currentByteCount));
			return;
		} // if - bad byte count

//		System.out.printf("DCU: Head: %d, Track: %d, Sector: %d AbsoluteSector: %d%n", currentHead, currentTrack,
//				currentSector, drives[currentDrive].getCurrentAbsoluteSector());
		if (!goodOperation) {
			return; // return if any problems - don't do any I/O
		} // if
		
		fireDCUAction(currentDrive,currentCommand); // notify the listeners

		// ----- now get to work

		if (currentCommand == COMMAND_READ) {
			ByteBuffer readByteBuffer = ByteBuffer.allocate(numberOfSectorsToMove * currentSectorSize);
			readByteBuffer.put(drives[currentDrive].read());
			for (int i = 0; i < numberOfSectorsToMove - 1; i++) {
				readByteBuffer.put(drives[currentDrive].readNext());
			} // for
			byte[] readBuffer = readByteBuffer.array();
			ioBuss.writeDMA(currentDMAAddress, readBuffer);
//			System.out.printf("DCU:Value: %02X, length = %d%n", readBuffer[1], readBuffer.length);
		} else { // its a COMMAND_WRITE
			// byte[] writeBuffer = core.readDMA(currentDMAAddress, currentSectorSize);
			// drives[currentDrive].write(writeBuffer);

			byte[] writeSector = new byte[currentSectorSize];
			byte[] readFromCore = ioBuss.readDMA(currentDMAAddress, currentByteCount);

			ByteBuffer writeByteBuffer = ByteBuffer.wrap(readFromCore);
			// ByteBuffer writeSectorBuffer =
			ByteBuffer.allocate(currentSectorSize);
			writeByteBuffer.get(writeSector);
			drives[currentDrive].write(writeSector);
			for (int i = 0; i < numberOfSectorsToMove - 1; i++) {
				writeByteBuffer.get(writeSector);
				drives[currentDrive].writeNext(writeSector);
			} // for

		} //
		if (goodOperation) {
			reportStatus((byte) 0X80, (byte) 00); // reset - operation is over
		} // if

	}// update

	@Override
	public void vdiskError(VDiskErrorEvent vdee) {
		diskErrorReport(ERROR_INVALID_SECTOR_DESIGNAMTOR, vdee.getMessage());
		return;
	}// vdiskError

	private void reportStatus(byte firstCode, byte secondCode) {
		ioBuss.write(DISK_STATUS_BLOCK, firstCode);
		ioBuss.write(DISK_STATUS_BLOCK + 1, secondCode);
		ioBuss.write(currentDiskControlByte, (byte) 00); // reset - operation is
															// over
	}//

	private void diskErrorReport(int code, String message) {
		// TODO write to error code, and clear ControlByte
		System.err.printf("DCU: DiskError - %d - %s%n", code, message);
		goodOperation = false;
		reportStatus((byte) 00, (byte) code);
		ioBuss.write(DISK_STATUS_BLOCK, (byte) 00);
		ioBuss.write(DISK_STATUS_BLOCK + 1, (byte) code);
		ioBuss.write(currentDiskControlByte, (byte) 00); // reset - operation
		// is over
	}// diskErrorReport

	private void debugShowControlTable() {
//		System.out.printf("currentCommand: %02X%n", currentCommand);
//		System.out.printf("currentUnit: %02X%n", currentUnit);
//		System.out.printf("currentHead: %02X%n", currentHead);
//		System.out.printf("currentTrack: %02X%n", currentTrack);
//		System.out.printf("currentSector: %02X%n", currentSector);
//		System.out.printf("currentByteCount: %04X%n", currentByteCount);
//		System.out.printf("currentDMAAddress: %04X%n", currentDMAAddress);
		
//		System.out.printf("[DCU] Location: %04X, Value: %02X%n", currentDiskControlByte,
//				ioBuss.read(currentDiskControlByte));
		String command = currentCommand == COMMAND_READ?"Read":"Write";
		System.out.printf("\t%s - Unit %02X, Head %02X, Trk %02X, Sec %04X, Bytes %04X, DMA %04X%n",
				command,currentUnit,currentHead,currentTrack,currentSector,currentByteCount,currentDMAAddress);


	}// debugShowControlTable

	/* \/ Event Handling Routines \/ */

	private Vector<DCUActionListener> dcuActionListeners = new Vector<DCUActionListener>();

	public synchronized void addDCUActionListener(DCUActionListener dcuListener) {
		if (dcuActionListeners.contains(dcuListener)) {
			return; // Already has it
		} // if
		dcuActionListeners.addElement(dcuListener);
	}// addVDiskErroListener

	public synchronized void removeDCUActionListener(DCUActionListener dcuListener) {
		dcuActionListeners.remove(dcuListener);
	}// addVDiskErroListener

	private void fireDCUAction(int diskIndex, int actionType) {
		Vector<DCUActionListener> actionListeners;
		synchronized (this) {
			actionListeners = (Vector<DCUActionListener>) dcuActionListeners.clone();
		} // sync
		int size = actionListeners.size();
		if (size == 0) {
			return; // no listeners
		} // if

		DCUActionEvent dcuActionEvent = new DCUActionEvent(this, diskIndex, actionType);
		for (int i = 0; i < size; i++) {
			DCUActionListener listener = (DCUActionListener) actionListeners.elementAt(i);
			listener.dcuAction(dcuActionEvent);
		} // for

	}// fireDCUAction

	/* /\ Event Handling Routines /\ */

	private static final int ERROR_NO_DISK = 10;
	private static final int ERROR_INVALID_SECTOR_DESIGNAMTOR = 11;
	private static final int ERROR_NO_DRIVE = 12;
	private static final int ERROR_SECTOR_NOT_SET = 13;
	private static final int ERROR_INVALID_DMA_ADDRESS = 14;
	private static final int ERROR_INVALID_BYTE_COUNT = 15;

	private static final byte COMMAND_READ = 01;
	private static final byte COMMAND_WRITE = 02;

	private static final int DISK_CONTROL_BYTE_8 = 0X0040;
	private static final int DISK_CONTROL_BYTE_5 = 0X0045;
	private static final int DISK_STATUS_BLOCK = 0X0043;
	// Disk Control Table
	private static final int DCT_COMMAND = 0; // DB 1
	private static final int DCT_UNIT = 1; // DB 1
	private static final int DCT_HEAD = 2; // DB 1
	private static final int DCT_TRACK = 3; // DB 1
	private static final int DCT_SECTOR = 4; // DB 1
	private static final int DCT_BYTE_COUNT = 5; // DW 1
	private static final int DCT_DMA_ADDRESS = 7; // DW 1
	private static final int DCT_NEXT_STATUS_BLOCK = 9; // DW 1
	private static final int DCT_NEXT_DCT = 11; // DW 1

}// class DiskControlUnit
