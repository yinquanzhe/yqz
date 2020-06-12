package net.ahwater.tender.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;

/**
 * Created by yyc on 2018/4/2 14:35.
 * Nothing is true but improving yourself.
 */
@Configuration
public class Swagger2Config {

    @Profile({"dev","test"})
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securitySchemes(Arrays.asList(new BasicAuth("test"), new ApiKey("a", "b", "c")))
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.ahwater.tender.web.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("沃特招投标信息采集系统")
//                .description("简单优雅的restfun风格 https://reeye.cn")
//                .termsOfServiceUrl("https://reeye.cn")
                .contact(new Contact("Reeye", "https://reeye.cn", "reeye@qq.com"))
                .version("2.0")
                .build();
    }

}
