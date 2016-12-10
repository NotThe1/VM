package disks;

import java.nio.ByteBuffer;

public class CPMFile extends CPMFileHeader {
	private DiskDrive diskDrive;

	public CPMFile(DiskDrive diskDrive, CPMDirectory directory, String fileName) {
		super(directory, fileName);
		this.diskDrive = diskDrive;
	}// Constructor

	public byte[] read() {
		ByteBuffer readData = ByteBuffer.allocate(numberOfSectors * diskDrive.getBytesPerSector());
		for (int i = 0; i < numberOfSectors; i++) {
			diskDrive.setCurrentAbsoluteSector(sectors.get(i));
			readData.put(diskDrive.read());
		} // - for- i : each sector
		readData.rewind();
		byte[] ans = new byte[actualByteCount];
		readData.get(ans);
		return ans;
	}// read

	public void write(byte[] writeData) {
		ByteBuffer writeBuffer = ByteBuffer.allocate(numberOfSectors * diskDrive.getBytesPerSector());
		writeBuffer.put(writeData);
		writeBuffer.rewind();
		byte[] dataToWrite = new byte[bytesPerSector];
		for (int i = 0; i < numberOfSectors; i++) {
			diskDrive.setCurrentAbsoluteSector(sectors.get(i));
			writeBuffer.get(dataToWrite);
			diskDrive.write(dataToWrite);
		} // for

	}// write

}// class CPMFile
