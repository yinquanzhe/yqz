package net.ahwater.main.accept.handler;

import net.ahwater.main.accept.MsgData;

/**
 * Created by Reeye on 2020/4/24 15:05
 * Nothing is true but improving yourself.
 */
public interface BaseHandler {

    boolean support(MsgData data);

    void handle(MsgData data);

}
