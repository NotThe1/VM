package misc;
import java.util.Random;





public class PlainTest {

	public PlainTest() {
		
	}

	public static void main(String[] args) {
		test1();

	}
	private static void test1(){
		Random random = new Random();
		
		
		for (int i = 0; i <100;i++){
			System.out.printf(" i = %d, Value = %02X%n", i,random.nextInt(0XFFFF));
		}

		
	}

}
