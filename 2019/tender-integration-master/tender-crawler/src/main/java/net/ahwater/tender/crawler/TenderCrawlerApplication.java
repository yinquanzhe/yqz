package net.ahwater.tender.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("net.ahwater.tender")
@SpringBootApplication
public class TenderCrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenderCrawlerApplication.class, args);
	}

}
