package net.ahwater.swqcgl.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yqz on 2019/1/17
 */
@RestController
public class ActiveController {
    @Autowired
    private JmsTemplate jmsTemplate;

    @RequestMapping("/send")
    public String send(){
        jmsTemplate.send("testQueue",(session -> session.createTextMessage("Java在召唤")));
        return "success";
    }

    @RequestMapping("/")
    public String hello(){
        return "Hello word";
    }
}