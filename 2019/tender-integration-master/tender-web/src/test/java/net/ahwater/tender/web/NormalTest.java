package net.ahwater.tender.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.SimpleDateFormat;
import java.time.Instant;

/**
 * Created by Reeye on 2019/2/27 14:43
 * Nothing is true but improving yourself.
 */
public class NormalTest {

    @Test
    public void test0() throws Exception {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("1234"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        Bean bean = new Bean();
        bean.a = 1;
        bean.b = 2;
        bean.c = 3;
        bean.d = 4;
        bean.f = Instant.now();
        bean.g = "123";
        System.out.println(mapper.writeValueAsString(bean));
    }

    static class Bean {
        private Integer a;
        protected Integer b;
        public Integer c;
        Integer d;
        public Integer e;
        public Instant f;
        public String g;
//        public Integer getA() {
//            return a;
//        }
    }

    @Test
    public void test1() {
        System.out.println("340100".replaceAll("(00)+$", ""));
        System.out.println("340000".replaceAll("(00)+$", ""));
    }
}
