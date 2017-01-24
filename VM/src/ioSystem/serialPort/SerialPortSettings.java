package ioSystem.serialPort;

import java.awt.Color;

public class SerialPortSettings implements Cloneable {
	
	private String portName;
	private int baudRate;
	private int dataBits;
	private int stopBits;
	private int parity;
	private Color screenForeground;
	private Color screenBackground;

	public SerialPortSettings() {
		setDefaultSettings();
	}// Constuctor

	SerialPortSettings(String portName, int baudRate, int dataBits, int stopBits, int parity) {
		this.setPortName(portName);
		this.setBaudRate(baudRate);
		this.setDataBits(dataBits);
		this.setStopBits(stopBits);
		this.setParity(parity);
	}// Constructor

	SerialPortSettings(String portName, int baudRate, int dataBits, int stopBits, int parity, Color foreGround,
			Color backGround) {
		this(portName, baudRate, dataBits, stopBits, parity);
		this.setScreenBackground(backGround);
		this.setScreenForeground(foreGround);
	}// Constructor

	public void setDefaultSettings() {
		this.setPortName(DEFAULT_PORT_NAME);
		this.setBaudRate(DEFAULT_BAUD_RATE);
		this.setDataBits(DEFAULT_DATA_BITS);
		this.setStopBits(DEFAULT_STOP_BITS);
		this.setParity(DEFAULT_PARITY);
		setDefaultScreenColors();
	}//setDefaultSettings
	
	public SerialPortSettings clone(){
		try {
			return (SerialPortSettings)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this;
		}//try
	}//clone

	public void setDefaultScreenColors() {
		this.setScreenColors(Color.BLACK, Color.LIGHT_GRAY);
	}//setDefaultScreenColors

	public String getPortName() {
		return portName;
	}//getPortName

	public void setPortName(String portName) {
		this.portName = portName;
	}//setPortName

	public int getBaudRate() {
		return baudRate;
	}// getBaudRate

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}// setBaudRate

	public int getDataBits() {
		return dataBits;
	}// getDataBits

	public void setDataBits(int dataBits) {
		if ((dataBits >= 5) && (dataBits <= 8)) {
			this.dataBits = dataBits;
		} else {
			this.dataBits = 8; // default
		}// if
	}// setDataBits

	public int getStopBits() {
		return stopBits;
	}// getStopBits

	public void setStopBits(int stopBits) {
		if ((stopBits >= 1) && (stopBits <= 3)) {
			this.stopBits = stopBits;
		} else {
			this.stopBits = 1; // default
		}// if }
	}// setStopBits

	public int getParity() {
		return parity;
	}// getParity

	public void setParity(int parity) {
		if ((parity >= 0) && (parity <= 5)) {
			this.parity = parity;
		} else {
			this.parity = 0; // default
		}// if
	}// setParity

	public Color getScreenForeground() {
		return screenForeground;
	}// getScreenForeground

	public void setScreenForeground(Color screenForeground) {
		this.screenForeground = screenForeground;
	}// setScreenForeground

	public Color getScreenBackground() {
		return screenBackground;
	}// getScreenBackground

	public void setScreenBackground(Color screenBackground) {
		this.screenBackground = screenBackground;
	}// setScreenBackground

	public void setScreenColors(Color screenForeground, Color screenBackground) {
		setScreenForeground(screenForeground);
		setScreenBackground(screenBackground);
	}// setScreenColors
	
	/*      valid values                 */
	
	public static final String DEFAULT_PORT_NAME = "COM1" ;
	public static final int DEFAULT_BAUD_RATE = 9600;
	public static final int DEFAULT_DATA_BITS = 8;
	public static final int DEFAULT_STOP_BITS = 1;
	public static final int DEFAULT_PARITY = 0;

	public static final String[] VALUES_BAUD_RATE = new String[] { "110", "300", "600", "1200", "4800", "9600",
			"14400", "19200", "38400", "57600", "115200" };
	public static final String[] VALUES_DATA_BITS = new String[] { "5", "6", "7", "8" };
	public static final String[] VALUES_STOP_BITS = new String[] { "1", "1.5", "2" };
	public static final String[] VALUES_PARITY = new String[] { "None", "Odd", "Even", "Mark", "Space" };
//	public static final String[] VALUES_
	
}//class serialPortSettings
