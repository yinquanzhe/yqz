package net.ahwater.main.accept;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Reeye on 2020/4/24 9:15
 * Nothing is true but improving yourself.
 */
@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
@Slf4j
@Component
public class RedisLockService implements LockService {

    private final String lock_key_pre = "LOCK_";
    private final int max_try_seconds = 60;
    private final Duration default_duration = Duration.ofSeconds(10);
    private final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisLockService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(String key) {
        long firstTryTime = System.currentTimeMillis();
        do {
            boolean res = redisTemplate.opsForValue().setIfAbsent(lock_key_pre + key, fetchValue(), default_duration);
            if (!res) {
                try {
                    Thread.sleep(200);
                } catch (Exception ignored) {}
            } else {
                return true;
            }
        } while (System.currentTimeMillis() - firstTryTime <= 1000L * max_try_seconds);
        return false;
    }

    @Override
    public boolean unlock(String key) {
        key = lock_key_pre + key;
        boolean hasKey = redisTemplate.hasKey(key);
        if (!hasKey) {
            log.warn("lock key [{}] is not exists", key);
            return true;
        } else {
            return redisTemplate.delete(key);
        }
    }

    private String fetchValue() {
        return UUID.randomUUID().toString() + "_" + df.format(new Date());
    }

}
