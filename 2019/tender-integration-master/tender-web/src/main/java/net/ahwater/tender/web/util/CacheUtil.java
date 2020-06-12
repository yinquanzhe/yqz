package net.ahwater.tender.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Created by Reeye on 2019/1/22 10:48
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class CacheUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //    private volatile static CacheUtil instance = null;
    //
    //    private CacheUtil() {
    //        if (instance == null) {
    //            synchronized (CacheUtil.class) {
    //                if (instance == null) {
    //                    instance = new CacheUtil();
    //                }
    //            }
    //        }
    //    }
    //
    //    public static CacheUtil getInstance() {
    //        return instance;
    //    }

    public <T> T getIfExistElseSet(String key, Supplier<T> supplier) {
        if (!redisTemplate.hasKey(key)) {
            synchronized (this) {
                if (!redisTemplate.hasKey(key)) {
                    if (supplier != null) {
                        T value = supplier.get();
                        redisTemplate.opsForValue().set(key, value);
                        return value;
                    }
                }
                return null;
            }
        } else {
            //noinspection unchecked
            return (T) redisTemplate.opsForValue().get(key);
        }
    }

    //    public static void main(String[] args) throws Exception {
    //        A<String> a = new A<String>() {
    //            @Override
    //            public String get() {
    //                return "123";
    //            }
    //        };
    //        System.out.println(a.clazz);
    //
    //    }
    //
    //    @SuppressWarnings("all")
    //    public static abstract class A<T> {
    //
    //        public A(T... ts) {
    //            clazz = howToClass(ts);
    //        }
    //
    //        public Class<?> clazz;
    //
    //        private <T> Class<?> howToClass(T index) {
    //            String name = "[Ljava.lang.Object;";
    //            if (index != null)
    //                name = index.getClass().getName();
    //            StringBuffer sbf = new StringBuffer();
    //            for (int i = 2; i < name.length() - 1; i++)
    //                sbf.append(name.charAt(i));
    //            name = sbf.toString();
    //            try {
    //                return Class.forName(name);
    //            } catch (ClassNotFoundException e) {
    //                System.err.println(e.getMessage());
    //            }
    //            return null;
    //        }
    //
    //        public abstract T get();
    //
    //    }

}