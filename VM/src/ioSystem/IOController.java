package ioSystem;

import java.util.HashMap;

import ioSysem.console.Console;

public class IOController {
	private static IOController instance = new IOController();
	
	private HashMap<Byte,Device8080> devicesInput = new HashMap<Byte,Device8080>();
	private HashMap<Byte,Device8080> devicesOutput = new HashMap<Byte,Device8080>();
	private HashMap<Byte,Device8080> devicesStatus = new HashMap<Byte,Device8080>();
	private Console console;
	private Device8080 device;
	
	private String errMessage;
	
	
	private IOController(){
		addConsole();
	}//constructor
	
	public static IOController getInstance(){
		return instance;
	}//getInstance
	
	private void addConsole(){
		console = new Console();	// default addresses 01,01,02
		devicesInput.put(console.getAddressIn(), console);
		devicesOutput.put(console.getAddressOut(), console);
		devicesStatus.put(console.getAddressStatus(), console);
	}//addConsole
	
	public void close(){
		console.close();
	}//close
	
	public void setConsoleSerialSettings(){
		console.setSerialPortSettings();
	}//setConsoleSerialSettings
	
	public String getConnectionString(){
		return console.getConnectionString();
	}//getConnectionString
	
	public void closeConnection(){
		console.close();
	}//closeConnection
	
	public void byteToDevice(Byte address,Byte value){
		if(devicesInput.containsKey(address)){
			device = devicesInput.get(address);
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
