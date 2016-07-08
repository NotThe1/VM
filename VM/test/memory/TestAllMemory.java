package memory;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestCore.class, TestCpuBuss.class, TestIoBuss.class })
public class TestAllMemory {

}
