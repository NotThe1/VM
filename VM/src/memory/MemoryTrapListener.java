package memory;

import java.util.EventListener;
/**
 * 
 * 
 * @author Frank Martyn
 * @version 1.0
 *          <p>
 *          Listens for memory traps,  IO or Debug
 */
public interface MemoryTrapListener extends EventListener {
	void memoryTrap(MemoryTrapEvent mte);
}//MemoryTrapListener
