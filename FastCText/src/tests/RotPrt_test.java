package tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import tools.RotPtr;
import fastCText2.BMcolumn;

public class RotPrt_test {
	static RotPtr ptr1, ptr2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Testing pointers
		ptr1 = new RotPtr(3); 
		ptr2 = new RotPtr(7);
	}
	
	@Test
	public void testNext() {
		int i;
		for(i=0;i<10;i++){
			System.out.println(ptr1.next());
		}
		for(i=0;i<100;i++){
			System.out.println(ptr2.next());
		}
	}

}
