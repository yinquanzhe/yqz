package net.ahwater.ahwaterCloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan("net.ahwater.ahwaterCloud")
@MapperScan("net.ahwater.ahwaterCloud.dao")
@EnableTransactionManagement // 开启事务支持
public class AhwaterCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(AhwaterCloudApplication.class, args);
	}
}
