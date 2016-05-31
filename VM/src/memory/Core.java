package memory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 * 
 *          Core is the class that acts as the physical memory.
 *          <p> Core is a base level object. It represents the memory in a virtual machine.
 *          
 *           The access to the data is handled by 3 sets of read and write operations:
 *          
 *          1) read, write, readWord, writeWord, popWord & pushWord all participate in the monitoring of the locations
 *              read for Debug Trap and written for IO trap.
 *          2) readForIO & writeForIO are to be used for byte memory access by devices.
 *              They do not engage the Trap system.
 *          3) readDMA & writeDMA are for burst mode reading and writing.
 *              They also do not engage the Trap system.
 *              
 *          Traps for IO are triggered by the writes.
 *          Debug traps are triggered by the reads in conjunction with the isDebugEnabled flag
 *               
 * 
 * @throws MemoryTrapEvent. Trap
 *             is fired if: 1) writing to a Disk Control Byte 2) writing to a debug marked location
 * 
 * @throws MemoryAccessErrorEvent. Trap
 *             is fired if: 1) attempt is made to address location not in memory
 *
 */

public class Core {
	private byte[] storage;
	private int maxAddress;

	private boolean isDebug = false;
	private boolean isDebugEnabled = false;

	public enum Trap {
		IO, DEBUG
	}

	private HashMap<Integer, Trap> traps;

	/**
	 * Constructor
	 * 
	 * @param size
	 *            Is the size of Memory
	 */
	public Core(int size) {
		if ((size < MINIMUM_MEMORY) | size > MAXIMUM_MEMORY) {
			String msg = String.format("%1$d [0X%1$X] bad size for memory,%2$d used instead", size, DEFAULT_MEMORY);
			JOptionPane.showMessageDialog(null, msg, "Core",
					JOptionPane.ERROR_MESSAGE);
			size = DEFAULT_MEMORY;
		}// if
		traps = new HashMap<Integer, Trap>();
		storage = new byte[size];
		maxAddress = size - 1;
	}

	/**
	 * Constructor uses Default size for memory
	 */
	public Core() {
		this(DEFAULT_MEMORY);
	}

	/**
	 * write - put value into memory and check for IO trap
	 * 
	 * @param location
	 *            where to put the value in memory
	 * @param value
	 *            what to put into memory
	 */
	public synchronized void write(int location, byte value) {
		if (!isValidAddress(location)) {
			return; // bad address;
		}// if

		storage[location] = value;

		if (isDiskTrapLocation(location, value)) {
			fireMemoryTrap(location, Trap.IO);
		}// if
	}// write

	/**
	 * writeForIO - write to a location. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            where to put the value
	 * @param value
	 *            what to put into specified location
	 */
	public synchronized void writeForIO(int location, byte value) {
		// writeValue = value; // save for IO trap
		if (isValidAddress(location) == true) {
			storage[location] = value;
		}// if
	}// writeForIO

	/**
	 * writeDMA - write consecutive locations. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            starting address for write
	 * @param values
	 *            actual values to be written
	 */
	public synchronized void writeDMA(int location, byte[] values) {
		int numberOfBytes = values.length;
		if (isValidAddressDMA(location, numberOfBytes) == true) {
			for (int i = 0; i < numberOfBytes; i++) {
				storage[location + i] = values[i];
			}// for
		}// if
	}// writeDMA

	/**
	 * writeWord - write a word (16) bits to memory
	 * 
	 * @param location
	 *            starting place in memory for the write
	 * @param hiByte
	 *            - first byte to write, at location
	 * @param loByte
	 *            - second byte to write, at location + 1
	 */
	public void writeWord(int location, byte hiByte, byte loByte) {
		this.write(location, hiByte);
		this.write(location + 1, loByte);

	}// putWord

	/**
	 * pushWord writes bytes in location -1 and location-2. Primarily used for stack work.
	 * 
	 * @param location
	 *            1 higher than actual memory address that will be written
	 * @param hiByte
	 *            - goes into location -1
	 * @param loByte
	 *            - goes into location -2
	 */
	public void pushWord(int location, byte hiByte, byte loByte) {
		this.write(location - 1, hiByte);
		this.write(location - 2, loByte);
	}// pushWord used for stack work

	/**
	 * read - returns the value found at the specified location
	 * 
	 * @param location
	 *            where to get the value from
	 * @return value found at the location, or a HLT command if this is the first access to a debug marked location
	 */
	public synchronized byte read(int location) {
		if (!isValidAddress(location)) {
			return 00; // bad address;
		}// if

		if (!isDebugEnabled) {
			return storage[location];
		}

		if (isDebugLocation(location)) {
			// may want to fire trap - fireMemoryTrap(location, Trap.DEBUG);
			return 0X76; // Return a fake Halt instruction
		} else {
			return storage[location];
		}
	}// read

	/**
	 * readForIO - read from a location. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            where to get the returned value
	 * @return value at specified location
	 */
	public synchronized byte readForIO(int location) {
		byte readForIO = 00;
		if (isValidAddress(location) == true) {
			readForIO = storage[location];
		}// if
		return readForIO;
	}// readForIO

	/**
	 * readDMA - read consecutive locations. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            where to start reading
	 * @param length
	 *            how many locations to return
	 * @return the values read
	 */
	public synchronized byte[] readDMA(int location, int length) {
		byte[] readDMA = new byte[length];
		if (isValidAddressDMA(location, length) == true) {
			for (int i = 0; i < length; i++) {
				readDMA[i] = storage[location + i];
			}// for
		} else {
			readDMA = null;
		}
		return readDMA;
	}// readDMA

	/**
	 * readWord - returns a word value (16 bits)
	 * 
	 * @param location
	 *            - location contains hi byte, location + 1 contains lo byte
	 * @return word - 16 bit value
	 */
	public int readWord(int location) {

		int hiByte = (this.read(location) << 8) & 0XFF00;
		int loByte = this.read(location + 1) & 0X00FF;
		return 0XFFFF & (hiByte + loByte);
	}// getWord

	/**
	 * readWordReversed - reverses the order of the immediate word byte 2 is lo byte byte 3 is hi byte
	 * 
	 * @param location
	 * @return
	 */
	public int readWordReversed(int location) {

		int loByte = (this.read(location + 1) << 8) & 0XFF00;
		int hiByte = this.read(location) & 0X00FF;
		return 0XFFFF & (hiByte + loByte);

	}// readWordReversed

	/**
	 * popWord reads bytes from location and location +1. Primarily used for stack work.Reads the locations opposite to
	 * the way readWord does
	 * 
	 * @param location
	 *            - location contains lo byte, location + 1 contains hi byte
	 * @return word - 16 bit value
	 */
	public int popWord(int location) {
		int loByte = (int) this.read(location) & 0X00FF;
		int hiByte = (int) (this.read(location + 1) << 8) & 0XFF00;
		return 0XFFFF & (hiByte + loByte);
	}// popWord

	/**
	 * isValidAddress - confirms the location is in addressable memory.
	 * <p>
	 * Will fire an MemoryAccessError if out of addressable memory
	 * 
	 * @param location
	 *            - address to be checked
	 * @return true if address is valid
	 * 
	 */

	private boolean isValidAddress(int location) {
		boolean checkAddress = true;
		if (location < 0) {
			// negative location
			checkAddress = false;
			fireAccessError(location, "Attempt to access bad location");
		} else if (location > maxAddress) {
			// out of bounds error
			checkAddress = false;
			fireAccessError(location, "Attempt to access bad location");
		}// if
		return checkAddress;
	}// isValidAddress

	/**
	 * 
	 * @param location
	 *            - starting address to be checked
	 * @param length
	 *            - for how many locations
	 * @return true if address range is valis
	 */
	private boolean isValidAddressDMA(int location, int length) {
		boolean checkAddressDMA = true;
		if ((location < 0) | ((location + (length - 1)) > maxAddress)) {
			checkAddressDMA = false;
			fireAccessError(location, "Invalid DMA memory location");
		}// if
		return checkAddressDMA;
	}// checkAddressDMA

	/**
	 * isDiskTrapLocation - let the write know it needs to fire the MemoryTrap Event
	 * 
	 * @param location
	 * @param value
	 * @return
	 */
	private boolean isDiskTrapLocation(int location, byte value) {
		if (traps.containsKey(location)) {
			Trap thisTrap = traps.get(location);
			return thisTrap.equals(Trap.IO) ? true : false;
		} else {
			return false;
		}//
	}

	/**
	 * isDebugLocation
	 * <p>
	 * Checks to see if this location is marked for debugging.
	 * <p>
	 * we only want to stop the machine on the first encounter, the second time will be the resumption of execution
	 * 
	 * @param location
	 *            location to check
	 * @return true if the program is to halt
	 */
	private boolean isDebugLocation(int location) {
		boolean isDebugLocation = false;
		if (!traps.containsKey(location)) {
			isDebugLocation = false;
		} else {
			Trap thisTrap = traps.get(location);
			if (thisTrap.equals(Trap.DEBUG)) {
				if (!isDebug) {// is this the first encounter ?
					isDebug = true; // then set the flag
					isDebugLocation = true;
				} else {
					isDebug = false; // else reset set the flag
					isDebugLocation = false;
				}// inner if
			}// else
		}// outer if
		return isDebugLocation;
	}

	/**
	 * addTrap add a location to the trap list and identifies it type
	 * 
	 * @param location
	 *            where to set the trap
	 * @param trap
	 *            what kind of trap - IO or Debug
	 */
	public void addTrap(int location, Trap trap) {
		if (!traps.containsKey(location)) { // Not trapped yet
			if (isValidAddress(location) == false) {
				return; // bad address - get out of here
			} // inner if
		}// if
		traps.put(location, trap); // may be different trap type
	}// addTrapLocation

	/**
	 * removeTrap removes a specific trap at one location
	 * 
	 * @param location
	 *            remove entry from trap list
	 * @param trap
	 *            the kind of trap to remove - IO or Debug
	 */
	public void removeTrap(int location, Trap trap) {
		traps.remove(location, trap);
	}// removeTrapLocation

	/**
	 * removeTraps removes all traps of a specified type from trap list
	 * 
	 * @param trap
	 *            Type of trap to remove - IO or Debug
	 */
	public void removeTraps(Trap trap) {

		Set<Integer> locations = traps.keySet();
		ArrayList<Integer> locs = new ArrayList<Integer>();
		for (Integer location : locations) {
			if (traps.get(location).equals(trap)) {
				locs.add(location);
			}// inner if
		}// for location
		locations = null;

		for (Integer loc : locs) {
			traps.remove(loc);
		}// for location

	}// removeTraps

	/**
	 * getTraps returns an array of all debug trap locations
	 * 
	 * @return array of debug locations
	 */
	public ArrayList<Integer> getTraps() {
		return getTraps(Trap.DEBUG);
	}// getTrapLocations - DEBUG

	/**
	 * getTraps returns an array of all traps of a specified type
	 * 
	 * @param trap
	 *            type of trap - IO or Debug
	 * @return ArrayList of traps specified by type
	 */
	public ArrayList<Integer> getTraps(Trap trap) {
		ArrayList<Integer> getTrapLocations = new ArrayList<Integer>();
		Set<Integer> locations = traps.keySet();
		for (Integer location : locations) {
			if (traps.get(location).equals(trap)) {
				getTrapLocations.add(location);
			}// inner if
		}// for location
		return getTrapLocations;
	}// getTrapLocations

	/**
	 * getSize() gets memory size
	 * 
	 * @return the size of the memory in bytes
	 */
	public int getSize() {
		return storage.length;
	}// getSize

	/**
	 * getSizeInK() gets memory size in K
	 * 
	 * @return the size of the memory in K (1024)
	 */
	public int getSizeInK() {
		return storage.length / K;
	}// getSizeInBytes

	// ///////////////////////////////
	/**
	 * setDebugTrapEnabled alows the debugging trapping to occur
	 * 
	 * @param state
	 *            true to enable debugging
	 */
	public void setDebugTrapEnabled(boolean state) {
		this.isDebugEnabled = state;
	}// enableTrap

	/**
	 * isDebugTrapEnabled identifies if the debug trap is enabled
	 * 
	 * @return state of debug enable flag
	 */
	public boolean isDebugTrapEnabled() {
		return isDebugEnabled;
	}// isDebugTrapEnabled

	private Vector<MemoryTrapListener> memoryTrapListeners = new Vector<MemoryTrapListener>();

	public synchronized void addMemoryTrapListener(MemoryTrapListener mtl) {
		if (memoryTrapListeners.contains(mtl)) {
			return; // Already here
		}// if
		memoryTrapListeners.addElement(mtl);
	}// addMemoryListener

	public synchronized void removeMemoryTrapListener(MemoryTrapListener mtl) {
		memoryTrapListeners.remove(mtl);
	}// removeMemoryListener

	private void fireMemoryTrap(int location, Trap trap) {
		Vector<MemoryTrapListener> mtl;
		synchronized (this) {
			mtl = (Vector<MemoryTrapListener>) memoryTrapListeners.clone();

			int size = mtl.size();
			if (0 == size) {
				return; // no listeners
			}// if
			MemoryTrapEvent memoryTrapEvent = new MemoryTrapEvent(this, location, trap);
			for (int i = 0; i < size; i++) {
				MemoryTrapListener listener = (MemoryTrapListener) mtl
						.elementAt(i);
				listener.memoryTrap(memoryTrapEvent);
			}// for
		}// sync
	}// fireProtectedMemoryAccess

	private Vector<MemoryAccessErrorListener> memoryAccessErrorListeners = new Vector<MemoryAccessErrorListener>();

	public synchronized void addMemoryAccessErrorListener(
			MemoryAccessErrorListener mael) {
		if (memoryAccessErrorListeners.contains(mael)) {
			return; // Already here
		}// if
		memoryAccessErrorListeners.addElement(mael);
	}// addMemoryListener

	public synchronized void removeMemoryAccessErrorListener(
			MemoryAccessErrorListener mael) {
		memoryAccessErrorListeners.remove(mael);
	}// removeMemoryListener

	private void fireAccessError(int location, String errorType) {
		Vector<MemoryAccessErrorListener> mael;
		synchronized (this) {
			mael = (Vector<MemoryAccessErrorListener>) memoryAccessErrorListeners.clone();
		}// sync
		int size = mael.size();
		if (0 == size) {
			return; // no listeners
		}// if
		MemoryAccessErrorEvent memoryAccessErrorEvent = new MemoryAccessErrorEvent(
				this, location, errorType);
		for (int i = 0; i < size; i++) {
			MemoryAccessErrorListener listener = (MemoryAccessErrorListener) mael
					.elementAt(i);
			listener.memoryAccessError(memoryAccessErrorEvent);
		}// for
	}// fireProtectedMemoryAccess
		// //////////////////////////////////////////////////

	static int K = 1024;
	static int PROTECTED_MEMORY = 0; // 100;
	static int MINIMUM_MEMORY = 16 * K;
	static int MAXIMUM_MEMORY = 64 * K;
	static int DEFAULT_MEMORY = 64 * K;

}// class Core
