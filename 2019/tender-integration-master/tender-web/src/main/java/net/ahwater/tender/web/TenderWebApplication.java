package net.ahwater.tender.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@ComponentScan("net.ahwater.tender")
public class TenderWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenderWebApplication.class, args);
	}

}
