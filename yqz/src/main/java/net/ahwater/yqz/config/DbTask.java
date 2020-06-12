package net.ahwater.yqz.config;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.ahwater.yqz.dao.UserDao;
import net.ahwater.yqz.entity.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Created by yqz on 2019/6/28
 */
@Component
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DbTask {

    @Autowired
    private UserDao userDao;

    //@Scheduled(fixedRate = 5 * 1000)
    public R taskList() {
        return R.ok(userDao.selectList(new QueryWrapper<>()));
    }
}