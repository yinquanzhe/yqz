package org.mintleaf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: MengchuZhang
 * @Date: 2018/8/8 8:10
 * @Version 1.0
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter
{
    @Override
    //拦截器
    public void addInterceptors(InterceptorRegistry registry) {

//        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/**");
    }
    //URL到视图的映射
    public void addViewControllers(ViewControllerRegistry registry){
//        registry.addViewController("/h5test.html").setViewName("test/h5test.html");
//        registry.addViewController("/trackingtest.html").setViewName("test/trackingtest.html");
    }

    //跨域访问配置
    public void addCorsMappings(CorsRegistry registry){

    }
    //格式化配置
    public void addFormatters(FormatterRegistry registry){

    }

}