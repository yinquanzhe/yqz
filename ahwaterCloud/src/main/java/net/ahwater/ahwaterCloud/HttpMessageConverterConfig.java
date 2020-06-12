package net.ahwater.ahwaterCloud;

import net.ahwater.ahwaterCloud.util.MappingJackson2HttpMessageConverterWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 使action返回的json封装一层
 * Created by TECHMAN on 2018/1/18.
 */
//@Configuration
public class HttpMessageConverterConfig {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverterWrapper();
    }

}
