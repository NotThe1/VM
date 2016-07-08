package hardware;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AllCentralProcessingUnitTests.class, ArithmeticUnitTest.class, CCRtest.class, RegisterDecodeTest.class,
		WorkingRegisterSetTest.class })
public class AllHardwareTests {

}
