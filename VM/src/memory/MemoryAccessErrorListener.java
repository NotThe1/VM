package memory;

import java.util.EventListener;

/**
 * 
 * 
 * @author Frank Martyn
 * @version 1.0
 *          <p>
 *          Listens for memory access violations,  trying to get to memory that is not there
 */
public interface MemoryAccessErrorListener extends EventListener {
	void memoryAccessError(MemoryAccessErrorEvent me);
}// MemoryErorListener
