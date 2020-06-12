package net.ahwater.yqz.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.ahwater.yqz.dao.UserDao;
import net.ahwater.yqz.entity.R;
import net.ahwater.yqz.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

/**
 * Created by yqz on 2019/6/28
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("/list")
    public R list(){
        List<User> ULIST = userDao.selectList(new QueryWrapper<>());
        ULIST.forEach(e->{
            jmsTemplate.send("q2", new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {

                    TextMessage textMessage = session.createTextMessage();
                    textMessage.setText(String.valueOf(e.getId()));
                    return textMessage;
                }
            });
        });
        return R.ok(ULIST);
    }


}