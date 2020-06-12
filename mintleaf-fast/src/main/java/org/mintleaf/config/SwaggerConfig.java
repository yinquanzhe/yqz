package org.mintleaf.config;

import springfox.documentation.service.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Author: MengchuZhang
 * @Date: 2018/9/28 10:33
 * @Version 1.0
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket swaggerSpringMvcPlugin(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("MintLeaf-Fast接口列表")
                .description("简单优雅的restfun风格")
                .contact(new Contact("ZhangMencghu", "https://gitee.com/ZhangMengchu/projects", "6153629@qq.com"))//作者
                .version("1.0")
                .build();

    }

}