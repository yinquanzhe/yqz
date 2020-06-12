package net.ahwater.main;

import net.ahwater.base.log.LogEntity;
import net.ahwater.base.log.LogHandler;
import org.springframework.stereotype.Component;

/**
 * Created by Reeye on 2020/4/17 11:12
 * Nothing is true but improving yourself.
 */
@Component
public class LogHandlerImpl implements LogHandler {

    @Override
    public void handle(LogEntity entity) {
        System.err.println("处理日志：" + entity);
    }

}
