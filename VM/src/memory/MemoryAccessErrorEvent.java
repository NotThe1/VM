package memory;

import java.util.EventObject;
/**
 * 
 * @author Frank Martyn
 * @version 1.0
 *<p> captures the data about memory access errors
 */
public class MemoryAccessErrorEvent extends EventObject {
	

	private static final long serialVersionUID = 1L;
	private int location;
	private String type;
/**
 * 
 * @param source Object that throws the event
 * @param location illegal address
 * @param type if DMA access error or regular access error
 */
	public MemoryAccessErrorEvent(Core source, int location,String type) {
		super(source);
		this.location = location;
		this.type = type;
	}//Constructor - MemoryErrorEvent
/**
 * 
 * @return (starting)value that caused the error
 */
	public int getLocation(){
		return location;
	}//getLocation
/**
 * 	
 * @return If normal or DMA access error
 */
	public String getType(){
		return type;
	}//getType
/**
 * 
 * @return String with error type and location
 */
	public String getMessage(){
		return String.format("Error type: %s%n location: 0X%04X", type,location);
	}//getMessage
	
}//class MemoryEvent

