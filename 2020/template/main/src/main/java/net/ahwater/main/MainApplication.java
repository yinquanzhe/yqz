package net.ahwater.main;

import net.ahwater.base.starter.EnableLog;
import net.ahwater.base.starter.EnableSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EnableLog(handler = LogHandlerImpl.class)
@EnableSecurity(securityService = MySecurityService.class)
@SpringBootApplication
public class MainApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MainApplication.class);
    }

}
