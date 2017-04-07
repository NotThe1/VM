package ioSysem.serialTerminal;

import java.util.LinkedList;
import java.util.Queue;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import ioSystem.Device8080;
import ioSystem.serialPort.PortSetupDialog;
import ioSystem.serialPort.SerialPortSettings;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialTerminal extends Device8080 {

	private SerialPort serialPort;
	private SerialPortSettings serialPortSettings = new SerialPortSettings();
	Queue<Byte> keyboardBuffer;
	Queue<Byte> inputBuffer;

	/**
	 * 
	 * @param name
	 *            - device name
	 * @param type
	 *            - storage/character/etc
	 * @param input
	 *            - is this an input device?
	 * @param addressIn
	 *            - address of device for input to CPU
	 * @param output
	 *            - is this an output device?
	 * @param addressOut-
	 *            address of device for output from CPU
	 * @param addressStatus
	 *            - address of status if different from in/out
	 */

	public SerialTerminal(String name, String type, boolean input, Byte addressIn, boolean output, Byte addressOut,
			Byte addressStatus) {
		super(name, type, input, addressIn, output, addressOut, addressStatus);
		restoreSerialPortSettings();
	}// Constructor

	public SerialTerminal(Byte addressIn, Byte addressOut, Byte addressStatus) {
		super("tty", "Serial", true, addressIn, true, addressOut, addressStatus);
		restoreSerialPortSettings();
		openSerialConnection();
		inputBuffer = new LinkedList<Byte>();
	}// Constructor
	
	public SerialTerminal(){
		this(CONSOLE_IN,CONSOLE_OUT,CONSOLE_STATUS);
	}

//=================================================================================================
	@Override
	public void byteFromCPU(Byte address, Byte value) {
		if (serialPort !=null){
			try {
				serialPort.writeByte(value);
			} catch (SerialPortException serialPortException) {
				String msg = String.format("Falied to write byte %02X to port %s with exception: %s",
						value,serialPortSettings.getPortName(),serialPortException.getMessage());
				JOptionPane.showMessageDialog(null, msg,"Keyboard In",JOptionPane.WARNING_MESSAGE);
			}//try
		}else{
			String msg = String.format("Serial Port %s is not opened",serialPortSettings.getPortName());
			JOptionPane.showMessageDialog(null, msg,"Keyboard In",JOptionPane.WARNING_MESSAGE);
		}//if - else
	}// byteFromCPU

	@Override
	public byte byteToCPU(Byte address) {//this is a blocking read
		Byte byteToCPU = null;
		if(address.equals(getAddressIn())){	// actually read data
			while(byteToCPU == null){
				byteToCPU = inputBuffer.poll();
			}//while
		}else if(address.equals(getAddressStatus())){ // return status byte
			byteToCPU = (byte) (CONSOLE_OUTPUT_STATUS_MASK |(byte) inputBuffer.size());
		}else {	// not my input/status address
			byteToCPU = 0X00;
		}//if else ..else
		return byteToCPU;
	}// byteToCPU

	// ------------------------------------------------------------------

	public void close() {
		closeSerialConnection();
	}// close
	
	private void openSerialConnection() {
		if (serialPort != null) {
			serialPort = null;
		}//if
		serialPort = new SerialPort(serialPortSettings.getPortName());
		try {
			serialPort.openPort();// Open serial port
			serialPort.setParams(serialPortSettings.getBaudRate(), serialPortSettings.getDataBits(),
					serialPortSettings.getStopBits(), serialPortSettings.getParity());
			serialPort.addEventListener(new SerialTerminalAdapter());
		} catch (SerialPortException ex) {
			System.out.println(ex);
		} // try
	}// openConnection

	// -----------------------------------------------------------------
	private void restoreSerialPortSettings() {
		Preferences myPrefs = Preferences.userNodeForPackage(SerialTerminal.class).node(this.getClass().getSimpleName());
		serialPortSettings.setPortName(myPrefs.get("PortName", SerialPortSettings.DEFAULT_PORT_NAME));
		serialPortSettings.setBaudRate(myPrefs.getInt("BaudRate", SerialPortSettings.DEFAULT_BAUD_RATE));
		serialPortSettings.setDataBits(myPrefs.getInt("DataBits", SerialPortSettings.DEFAULT_DATA_BITS));
		serialPortSettings.setStopBits(myPrefs.getInt("StopBits", SerialPortSettings.DEFAULT_STOP_BITS));
		serialPortSettings.setParity(myPrefs.getInt("Parity", SerialPortSettings.DEFAULT_PARITY));
		myPrefs = null;
	}// loadSettings()

	public void setSerialPortSettings() {
		closeSerialConnection();
		PortSetupDialog portSetupDialog = new PortSetupDialog(new SerialPortSettings());
		this.serialPortSettings = portSetupDialog.showDialog();
		openSerialConnection();
		saveSerialPortSettings();
	}// loadSettings(fileName)
	
	private void closeSerialConnection() {
		if (serialPort != null) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				//e.printStackTrace();
			}// try
			serialPort = null;
		}// if
	}// closeConnection

	private void saveSerialPortSettings() {
		Preferences myPrefs = Preferences.userNodeForPackage(SerialTerminal.class).node(this.getClass().getSimpleName());
		myPrefs.put("PortName", serialPortSettings.getPortName());
		myPrefs.putInt("BaudRate", serialPortSettings.getBaudRate());
		myPrefs.putInt("DataBits", serialPortSettings.getDataBits());
		myPrefs.putInt("StopBits", serialPortSettings.getStopBits());
		myPrefs.putInt("Parity", serialPortSettings.getParity());
		myPrefs = null;
	}// saveSerialPortSettings
	
	public String getConnectionString(){
		String connectionString = String.format("%s-%d-%d-%s-%s",
				serialPortSettings.getPortName(),
				serialPortSettings.getBaudRate(),
				serialPortSettings.getDataBits(),"0",
				SerialPortSettings.VALUES_PARITY[serialPortSettings.getParity()]
				);
		return connectionString;
	}//getConnectionString
	public SerialPortSettings getSerialPortSettings(){
		return this.serialPortSettings;
	}//getSerialPortSettings

	private static final byte CONSOLE_IN = (byte) 0X01; // data console ---> CPU
	private static final byte CONSOLE_OUT= (byte) 0X01; // data CPU ---> console
	private static final byte CONSOLE_STATUS= (byte) 0X02; // How many characters in read buffer
	
	public static final byte CONSOLE_OUTPUT_STATUS_MASK = (byte) 0X80; // ready for output
	public static final byte CONSOLE_INPUT_STATUS_MASK = (byte) 0X7F; // ready for output
	
	
	
	//...........................................................................................
	//........................................................................................

	public class SerialTerminalAdapter implements SerialPortEventListener {

		@Override
		public void serialEvent(SerialPortEvent spe) {
			if (spe.isRXCHAR()) {
				// System.out.printf(" spe.getEventValue() = %d%n",
				// spe.getEventValue());
				if (spe.getEventValue() > 0) {// data available
					try {
						byte[] buffer = serialPort.readBytes();
						for (Byte b : buffer) {
							inputBuffer.add(b);
						}
						// ******readInputBuffer();
						// System.out.println(Arrays.toString(buffer));

					} catch (SerialPortException speRead) {
						System.out.println(speRead);
					} // try
				} // inner if

			} else if (spe.isCTS()) { // CTS line has changed state
				String msg = (spe.getEventValue() == 1) ? "CTS - On" : "CTS - Off";
				System.out.println(msg);
			} else if (spe.isDSR()) { // DSR line has changed state
				String msg = (spe.getEventValue() == 1) ? "DSR - On" : "DSR - Off";
				System.out.println(msg);
			} else {
				System.out.printf("Unhandled event : %s%n", spe.toString());
			}

		}// serialEvent

	}// class SerialPortReader

}// class Console
