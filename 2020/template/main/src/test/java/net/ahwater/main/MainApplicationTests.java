package net.ahwater.main;

import net.ahwater.main.accept.RedisLockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
class MainApplicationTests {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisLockService service;

    @Test
    void contextLoads() throws Exception {
        int count = 5;
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " 开启运行");
                String key = "123";
                if (service.tryLock(key)) {
                    System.out.println(threadName + " lock成功");
                    try {
                        Thread.sleep(3000);
                        System.out.println(threadName + " 解锁:" + service.unlock(key));
                        latch.countDown();
                    } catch (Exception ignored) {}
                } else {
                    System.out.println(threadName + " lock失败");
                }
            }).start();
        }
        latch.await();
    }

    @Test
    void test1() throws Exception {

    }

}
