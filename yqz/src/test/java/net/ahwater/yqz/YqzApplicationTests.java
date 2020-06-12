package net.ahwater.yqz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YqzApplicationTests {

	@Test
	public void contextLoads() {

	}

	public static void main(String[] args) {
		int[] arr = new int[]{8,2,1,0,3};
		int[] index = new int[]{2,0,3,2,4,0,1,3,2,3,3};
		String tel="";
		for (int i: index){
			tel += arr[i];
		}
		System.out.println(tel);
	}

}
