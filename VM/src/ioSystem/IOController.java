package ioSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextArea;

import ioSysem.serialTerminal.SerialTerminal;
import ioSystem.listDevice.ListDevice;
import ioSystem.tty.TTY;

public class IOController {
	private static IOController instance = new IOController();
	
	private HashMap<Byte,Device8080> devicesInput = new HashMap<Byte,Device8080>();
	private HashMap<Byte,Device8080> devicesOutput = new HashMap<Byte,Device8080>();
	private HashMap<Byte,Device8080> devicesStatus = new HashMap<Byte,Device8080>();
	
	private Set<Device8080> devicePopulation = new HashSet<Device8080>();
	
	private Device8080 device;
	private SerialTerminal serialTerminal;
	private ListDevice listDevice;
	private TTY tty;
	
//	private String errMessage;
	
	
	private IOController(){
//		addSerialTerminal();
//		addTTY();
	}//constructor
	
	public static IOController getInstance(){
		return instance;
	}//getInstance
	
	public void addListDevice(JTextArea textArea){
		listDevice = new ListDevice(textArea);
		devicesOutput.put(listDevice.getAddressOut(), listDevice);
		devicesStatus.put(listDevice.getAddressStatus(),listDevice);
		
		devicePopulation.add(listDevice);
	}//addListDevice
	
	public ListDevice getListDevice(){
		return listDevice==null?null:listDevice;
	}//getListDevice
	
	public void addSerialTerminal(){
		serialTerminal = new SerialTerminal();	// default addresses 01,01,02
		devicesInput.put(serialTerminal.getAddressIn(), serialTerminal);
		devicesOutput.put(serialTerminal.getAddressOut(), serialTerminal);
		devicesStatus.put(serialTerminal.getAddressStatus(), serialTerminal);
		
		devicePopulation.add(serialTerminal);
	}//addConsole
	
	public void addTTY(){
		tty = new TTY();	// default addresses EC,EC,ED
		devicesInput.put(tty.getAddressIn(), tty);
		devicesOutput.put(tty.getAddressOut(), tty);
		devicesStatus.put(tty.getAddressStatus(), tty);
		
		devicePopulation.add(tty);		
	}//addConsole
	
	public void close(){
		for(Device8080 d:devicePopulation){
			if(d!=null){
				d.close();
				d = null;
			}//if
		}//
//		serialTerminal.close();
//		tty.close();
	}//close
	
//	public void setConsoleSerialSettings(){
//		serialTerminal.setSerialPortSettings();
//	}//setConsoleSerialSettings
	
	public String getConnectionString(){
		return serialTerminal.getConnectionString();
	}//getConnectionString
	
	public void closeConnection(){
		serialTerminal.close();
	}//closeConnection
	
	public void byteToDevice(Byte address,Byte value){
		if(devicesOutput.containsKey(address)){
			device = devicesOutput.get(address);
			device.byteFromCPU(address, value);
		}else{
			System.err.printf("Bad address %02X for bytsToDevice operation.%n", address);
		}//if - else
	}//byteToDevice
	
	public Byte byteFromDevice(Byte address){
		Byte value = null;
		if(devicesInput.containsKey(address)){	// read
			device = devicesInput.get(address);
			value = device.byteToCPU(address);
		}else if(devicesStatus.containsKey(address)){// get status
			device = devicesStatus.get(address);
			value = device.byteToCPU(address);
		}else{
			System.err.printf("Bad address %02X for byteFromDevice operation.%n", address);
		}//if else ..else
		return value;
	}//byteFromDevice


}//class DeviceController
