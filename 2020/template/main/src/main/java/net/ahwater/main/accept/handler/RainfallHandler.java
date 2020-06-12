package net.ahwater.main.accept.handler;

import net.ahwater.main.accept.MsgData;
import org.springframework.stereotype.Component;

/**
 * Created by Reeye on 2020/4/24 15:04
 * Nothing is true but improving yourself.
 */
@Component
public class RainfallHandler implements BaseHandler {

    private static final String SUPPORT_TYPE = "R";

    @Override
    public boolean support(MsgData data) {
        return data.getType().equals(SUPPORT_TYPE);
    }

    @Override
    public void handle(MsgData data) {
        System.err.println("处理：" + data);
    }

}
