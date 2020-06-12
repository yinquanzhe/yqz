package net.ahwater.ahwaterCloud;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by TECHMAN on 2018/1/18.
 * 拦截器配置类
 */
//@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        super.addInterceptors(registry);
    }
}
