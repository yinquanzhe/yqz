package net.ahwater.tender.wx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan("net.ahwater.tender")
@EnableScheduling
@SpringBootApplication
public class TenderWxApplication {

	public static void main(String[] args) {
        SpringApplication.run(TenderWxApplication.class, args);
	}

}
