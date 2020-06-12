package net.ahwater.yqz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.ahwater.yqz.dao.UserDao;
import net.ahwater.yqz.entity.User;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yqz on 2019/6/28
 */
@Service
public class ActiveMsgListener {
    @Autowired
    private UserDao userDao;

    @JmsListener(destination = "q2")
    public void rctiveMsg(String message){
        User u= userDao.selectById(message);

        System.out.println("------监听到activemq的数据"+u.getId());
    }

}