package net.ahwater.zjk.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yqz on 2020/5/27
 */
@Configuration
@MapperScan("net.ahwater.zjk.dao")
public class MyBatisPlusConfig{

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}