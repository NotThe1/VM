package misc;
import hardware.CentralProcessingUnit;

import java.util.Random;

public class PlainTest {

	public static void main(String[] args) {
		test3();
//		test2();
//		test1();
	}//main
	
	private static void test3(){
		CentralProcessingUnit cpu = new CentralProcessingUnit();
	
		
	}///test 3
	
	private static void test2(){
		for (int i = 0; i <= 0XFF ;i++){
			byte opCode = (byte) i;
			 
			System.out.printf("opCode : %02X( %8s ) , Index45 = %d,, Index012 = %d, Index345 = %d%n",
					i,Integer.toBinaryString(i),getIndex45(opCode),getIndex012(opCode),getIndex345(opCode));
			if ((i % 16)==0){
				System.out.println();
			}
		}//for
	}//test2
	private static int getIndex45(byte opCode){
		return (opCode & 0B00110000) >> 4;		
	}//getIndex45
	private static int getIndex345(byte opCode){
		return (opCode & 0B00111000) >> 3;		
	}//getIndex345
	private static int getIndex012(byte opCode){
		return (opCode & 0B00000111);		
	}//getIndex012
//-------------------------------	
	private static void test1(){
		Random random = new Random();
		for (int i = 0; i <100;i++){
			System.out.printf(" i = %d, Value = %02X%n", i,random.nextInt(0XFFFF));
		}//for
	}//test1

}
