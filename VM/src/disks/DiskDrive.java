package disks;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Vector;

public class DiskDrive {
	private String diskType;
	private boolean bootable;
	protected int heads;
	private int currentHead;
	protected int tracksPerHead;
	private int currentTrack;
	protected int sectorsPerTrack;
	private int currentSector;
	private int currentAbsoluteSector;
	protected int bytesPerSector;
	protected int sectorsPerHead;
	protected int totalSectorsOnDisk;
	protected long totalBytesOnDisk;
	private String fileAbsoluteName;
	private String fileLocalName;

	private FileChannel fileChannel;
	private MappedByteBuffer disk;
	private byte[] readSector;
	private ByteBuffer writeSector;
	
	RandomAccessFile raf;

	public DiskDrive(Path path) {
		this(path.resolve(path).toString());
	}

	public DiskDrive(String strPathName) {
		// String strPathName = path.resolve(path).toString();
		resolveDiskType(strPathName);

		try {
			File file = new File(strPathName);
			
			 raf = new RandomAccessFile(file,"rw");
			fileChannel = raf.getChannel();
			
//			fileChannel = new RandomAccessFile(file, "rw").getChannel();
			disk = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());// this.totalBytesOnDisk);
			fileAbsoluteName = file.getAbsolutePath();
			fileLocalName = file.getName();

		} catch (IOException e) {
			fireVDiskError((long) 1, "Physical I/O error" + e.getMessage());
			System.err.printf("Physical I/O error - %s%n", e.getMessage());
		} // try
		readSector = new byte[bytesPerSector];
		writeSector = ByteBuffer.allocate(bytesPerSector);

	}// Constructor

	public void dismount() {
		try {
			raf.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		if (disk != null) {
			disk = null;
		} // if
		if (fileChannel != null) {
			try {
				fileChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fileChannel = null;
		} // if
			// fileChannel.close();
	}// close();

	private void resolveDiskType(String drive) {
		String[] fileNameComponents = drive.split("\\.");
		if (fileNameComponents.length != 2) {
			fireVDiskError((long) 0, "Bad Disk - " + drive);
			return;
		} // if
		String fileExtension = fileNameComponents[1]; // have the file extension

		DiskMetrics diskMetric = DiskMetrics.getDiskMetric(fileExtension);
		if (diskMetric == null) {
			fireVDiskError((long) 2, "Not a Valid disk type " + fileNameComponents[1]);
			return;
		} // if
		this.diskType = fileExtension;
		this.heads = diskMetric.heads;
		this.tracksPerHead = diskMetric.tracksPerHead;
		this.sectorsPerTrack = diskMetric.sectorsPerTrack;
		this.bytesPerSector = diskMetric.bytesPerSector;
		this.sectorsPerHead = diskMetric.getTotalSectorsPerHead();
		this.totalSectorsOnDisk = diskMetric.getTotalSectorsOnDisk();
		this.totalBytesOnDisk = diskMetric.getTotalBytes();
		this.bootable = diskMetric.isBootDisk();

	}// resolveDiskType

	public boolean isBootable() {
		return this.bootable;
	}// isBootable

	public String getFileAbsoluteName() {
		return this.fileAbsoluteName;
	}// getFileAbsoluteName

	public String getFileLocalName() {
		return this.fileLocalName;
	}// getFileLocalName

	public String getDiskType() {
		return this.diskType;
	}// getDiskType

	public void homeHeads() {
		this.currentHead = 0;
		this.currentTrack = 0;
		this.currentSector = 1;
		this.currentAbsoluteSector = 0;
	}// homeHeads

	private void setSectorPosition() {
		int offset = currentAbsoluteSector * bytesPerSector;
		disk.position(offset);
	}// setSectorPosition

	public byte[] read() {
		setSectorPosition();
		disk.get(readSector);
		return readSector;
	}// read

	public byte[] readNext() {
		setCurrentAbsoluteSector(currentAbsoluteSector + 1);
		return read();
	}// readNext

	public void write(byte[] sector) {
		writeSector.clear();
		if (sector.length != bytesPerSector) {
			fireVDiskError((long) sector.length, "Wrong sized sector");
		} else {
			setSectorPosition();
			disk.put(sector);
			System.out.printf("[DiskDrive.write]  Sector = %d, byte[0] = %02X%n", this.currentAbsoluteSector,
					sector[0]);
		} // if
	}// write

	public void writeNext(byte[] sector) {
		setCurrentAbsoluteSector(currentAbsoluteSector + 1);
		write(sector);
		return;
	}// writeNext

	public long getTotalBytes() {
		return this.totalBytesOnDisk;
	}// getTotalBytes

	public int getCurrentHead() {
		return currentHead;
	}// getCurrentHead

	public void setCurrentHead(int currentHead) {
		if (validateHead(currentHead)) {
			this.currentHead = currentHead;
		} // if
	}// setCurrentHead

	public int getCurrentTrack() {
		return currentTrack;
	}// getCurrentTrack

	public int getBytesPerSector() {
		return this.bytesPerSector;
	}// getBytesPerSector

	public void setCurrentTrack(int currentTrack) {
		if (validateTrack(currentTrack)) {
			this.currentTrack = currentTrack;
		} // if
	}// setCurrentTrack

	public int getCurrentSector() {
		return currentSector;
	}// getCurrentSector

	public void setCurrentSector(int currentSector) {
		if (validateSector(currentSector)) {
			this.currentSector = currentSector;
		} // if
	}// setCurrentSector

	public int getCurrentAbsoluteSector() {
		return currentAbsoluteSector;
	}// getCurrentAbsoluteSector

	public boolean setCurrentAbsoluteSector(int currentAbsoluteSector) {
		boolean setCurrentAbsoluteSector = false;
		if (validateAbsoluteSector(currentAbsoluteSector)) {
			this.currentAbsoluteSector = currentAbsoluteSector;
			int sectorsPerTrackHead = this.sectorsPerTrack * this.heads;
			int headSectors = currentAbsoluteSector % sectorsPerTrackHead;
			setCurrentHead(headSectors / sectorsPerTrack); // /sectorsPerTrackHead
			setCurrentTrack(currentAbsoluteSector / sectorsPerTrackHead);
			setCurrentSector((currentAbsoluteSector % sectorsPerTrack) + 1); // sectorsPerTrackHead
			setCurrentAbsoluteSector = true;
		} // if
		return setCurrentAbsoluteSector;
	}// setCurrentAbsoluteSector

	public boolean setCurrentAbsoluteSector(int head, int track, int sector) {
		boolean setCurrentAbsoluteSector = false;
		if (validateHead(head) & validateSector(sector) & validateTrack(track)) {
			int absoluteSector = (sector - 1) + (head * this.sectorsPerTrack)
					+ (track * this.sectorsPerTrack * this.heads);
			if (validateAbsoluteSector(absoluteSector)) {
				this.currentAbsoluteSector = absoluteSector;
				this.currentHead = head;
				this.currentTrack = track;
				this.currentSector = sector;
				setCurrentAbsoluteSector = true;
			} // inner if
		} // outer if
		return setCurrentAbsoluteSector;
	}// setCurrentAbsoluteSector

	private boolean validateHead(int head) {
		boolean validateHead = true;
		// between 0 and heads-1
		if (!((head >= 0) & (head < heads))) {
			homeHeads();
			fireVDiskError((long) head, "Bad head");
			validateHead = false;
		} // if
		return validateHead;
	}// validateHead

	private boolean validateTrack(int track) {
		boolean validateTrack = true;
		// between 0 and tracksPerHead-1
		if (!((track >= 0) & (track < tracksPerHead))) {
			homeHeads();
			fireVDiskError((long) track, "Bad track");
			validateTrack = false;
		} // if
		return validateTrack;
	}// validateTrack

	private boolean validateSector(int sector) {
		// between 1 andsectorsPerTrack
		boolean validateSector = true;
		if (!((sector > 0) & (sector <= sectorsPerTrack))) {
			homeHeads();
			fireVDiskError((long) sector, "Bad Sector");
			validateSector = false;
		} // if
		return validateSector;
	}// validateSector

	private boolean validateAbsoluteSector(long absoluteSector) {
		// between 0 and totalSectorsOnDisk-1
		boolean validateAbsoluteSector = true;
		if (!((absoluteSector >= 0) & (absoluteSector < totalSectorsOnDisk))) {
			homeHeads();
			fireVDiskError((long) absoluteSector, "Bad absoluteSector");
			validateAbsoluteSector = false;
		} // if
		return validateAbsoluteSector;
	}// validateAbsoluteSector

	// ---------------------------------Error
	// Events----------------------------------------

	private Vector<VDiskErrorListener> vdiskErrorListeners = new Vector<VDiskErrorListener>();

	public synchronized void addVDiskErroListener(VDiskErrorListener vdel) {
		if (vdiskErrorListeners.contains(vdel)) {
			return; // Already has it
		} // if
		vdiskErrorListeners.addElement(vdel);
	}// addVDiskErroListener

	public synchronized void removeVDiskErroListener(VDiskErrorListener vdel) {
		vdiskErrorListeners.remove(vdel);
	}// addVDiskErroListener

	private void fireVDiskError(long value, String errorMessage) {
		Vector<VDiskErrorListener> vdel;
		synchronized (this) {
			vdel = (Vector<VDiskErrorListener>) vdiskErrorListeners.clone();
		} // sync
		int size = vdel.size();
		if (size == 0) {
			return; // no listeners
		} // if

		VDiskErrorEvent vdiskErrorEvent = new VDiskErrorEvent(this, value, errorMessage);
		for (int i = 0; i < size; i++) {
			VDiskErrorListener listener = (VDiskErrorListener) vdel.elementAt(i);
			listener.vdiskError(vdiskErrorEvent);
		} // for

	}// fireVDsikError

}// MyDiskDrive
