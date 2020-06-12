package net.ahwater.swqcgl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SwqcglApplicationTests {

	@Test
	@JmsListener(destination = "testQueue")
	public void contextLoads(String content) {
		System.out.println(content);

	}

}

