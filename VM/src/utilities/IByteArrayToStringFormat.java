package utilities;

/**
 * 
 * @author Frank Martyn
 * this interface is for formatting a byte array to a predetermined text representation of the array
 */

public interface IByteArrayToStringFormat {
	/**
	 *  used to determine if there is any more values to be processed
	 * @return true if there is more, false if there is no more
	 */
	public boolean hasNext();
	/**
	 * formats the next line of text
	 * @return a String that has a text representation of the next  byte values
	 */
	public String getNext();

}//interface ByteArrayToMemFormat
