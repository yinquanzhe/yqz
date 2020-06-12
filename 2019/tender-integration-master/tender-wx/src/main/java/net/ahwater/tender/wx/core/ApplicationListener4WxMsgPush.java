package net.ahwater.tender.wx.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.db.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yyc on 2018/2/5 18:21.
 * Nothing is true but improving yourself.
 */
@Component
@Slf4j
public class ApplicationListener4WxMsgPush implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PushScheduler pushScheduler;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<BeanUser> users = userMapper.selectList(new QueryWrapper<BeanUser>().lambda().ne(BeanUser::getPushTime, -1));
        for (BeanUser user : users) {
            pushScheduler.newOrUpdateTask(user);
        }
        log.info("成功添加推送任务: {}条, UserIds: {}", users.size(), users.stream().map(BeanUser::getId).collect(Collectors.toList()));
    }

}