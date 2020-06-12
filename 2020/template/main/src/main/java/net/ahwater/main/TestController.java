package net.ahwater.main;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.base.web.security.annotation.CurrentUser;
import net.ahwater.base.web.security.annotation.auth.RequiresPermissions;
import net.ahwater.base.web.security.annotation.auth.RequiresRoles;
import net.ahwater.main.accept.DirectRabbitConfig;
import net.ahwater.main.accept.LockService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Reeye on 2020/4/21 13:39
 * Nothing is true but improving yourself.
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private LockService lockService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @RequestMapping("/t1")
    @RequiresRoles("A")
    public String test() {
        String res = testService.doSomething(new MyUser("张三"), "跳楼");
        return "结果: " + res;
    }

    @RequestMapping("/t2")
    @RequiresPermissions("P1")
    public String test2(@CurrentUser MyUser user) {
        return "test2: " + user.getName();
    }

    @PostMapping("/t3")
    public String sendDirectMessage(@RequestParam String msg) {
        rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_NAME, DirectRabbitConfig.ROUTING_KEY, msg);
        return "ok";
    }

    @GetMapping("/t4")
    public String testLock(String key) {
        if (lockService.tryLock(key)) {
            System.out.println("lock成功");
            try {
                Thread.sleep(3000);
                return " 解锁:" + lockService.unlock(key);
            } catch (Exception ignored) {}
        } else {
            return "lock失败";
        }
        return "end";
    }

}
