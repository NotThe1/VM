package memory;

import javax.swing.JOptionPane;

//need to throw address out of bounds error, protection violation
public class MainMemory implements MemoryAccessErrorListener { // , MemoryTrapListener

	Core core;

	public MainMemory(Core core) {
		this.core = core;
		core.addMemoryAccessErrorListener(this);
	}// Constructor

	public byte getByte(int location) {
		return core.read(location);
	}// getByte

	public void setByte(int location, byte value) {
		core.write(location, value);
	}// putByte

	public int getWord(int location) {
		int hiByte = (core.read(location) << 8) & 0XFF00;
		int loByte = core.read(location + 1) & 0X00FF;
		return 0XFFFF & (hiByte + loByte);
	}// getWord

	/*
	 * reverses the order of the immediate word byte 2 is lo byte byte 3 is hi byte
	 */
	public int getWordReversed(int location) {
		int loByte = (core.read(location + 1) << 8) & 0XFF00;
		int hiByte = core.read(location) & 0X00FF;
		return 0XFFFF & (hiByte + loByte);

	}// getWord

	public void setWord(int location, byte hiByte, byte loByte) {
		core.write(location, hiByte);
		core.write(location + 1, loByte);
	}// putWord

	public void pushWord(int location, byte hiByte, byte loByte) {
		core.write(location - 1, hiByte);
		core.write(location - 2, loByte);
	}// pushWord used for stack work

	public int popWord(int location) {
		int loByte = (int) core.read(location) & 0X00FF;
		int hiByte = (int) (core.read(location + 1) << 8) & 0XFF00;
		return 0XFFFF & (hiByte + loByte);
	}// popWord

	public int getMemorySizeInBytes() {
		return core.getSize();
	}// getSize

	public int getSizeInK() {
		return core.getSizeInK();
	}// getSize

	@Override
	public void memoryAccessError(MemoryAccessErrorEvent mae) {
		System.err.printf("MM: %n%nFatal memory error%n%n");
		System.err.printf(String.format("MM: Location: %s%n", mae.getLocation()));
		System.err.printf(String.format("MM: %s%n", mae.getMessage()));
		JOptionPane.showMessageDialog(null, mae.getMessage(), "Main Memory", JOptionPane.ERROR_MESSAGE);
		// System.exit(-1);
	}

}// class MainMemory
