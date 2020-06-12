package net.ahwater.zjk.controller;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.zjk.config.DirectRabbitConfig;
import net.ahwater.zjk.entity.vo.R;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by yqz on 2020/5/28
 */
@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/test")
public class indexController{

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final String lock_key_pre = "LOCK_";
    private final int max_try_seconds = 60;
    private long sessionMinutes = 30;
    private final Duration default_duration = Duration.ofSeconds(10);//10秒
    private final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/t4")
    public R lock(String key){
       // boolean res = redisTemplate.opsForValue().setIfAbsent(lock_key_pre + key, fetchValue(), default_duration);
        redisTemplate.opsForValue().setIfAbsent(lock_key_pre + key, fetchValue(), sessionMinutes, TimeUnit.MINUTES);//设置了 30 分钟
        return R.ok("成功");
    }

    @GetMapping("/t5")
    public R getRedisKey(String key){
        Object res = redisTemplate.opsForValue().get(key);
        return R.ok("成功",res);
    }


    @PostMapping("/t3")
    public String sendDirectMessage(@RequestParam String msg) {
        rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_NAME, DirectRabbitConfig.ROUTING_KEY, msg);
        return "ok";
    }

    private String fetchValue() {
        return UUID.randomUUID().toString() + "_" + df.format(new Date());
    }
}