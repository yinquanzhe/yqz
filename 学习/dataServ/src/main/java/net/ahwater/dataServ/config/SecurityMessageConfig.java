package net.ahwater.dataServ.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Created by Reeye on 2019/8/22 10:04
 * Nothing is true but improving yourself.
 */
@Configuration
public class SecurityMessageConfig{

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
//        source.setBasename("classpath:org/springframework/security/messages");
        source.setBasename("classpath:messages");
        return source;
    }

}
