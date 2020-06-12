package net.ahwater.zjk.config;


import net.ahwater.zjk.entity.dto.MsgData;

/**
 * Created by Reeye on 2020/4/24 15:05
 * Nothing is true but improving yourself.
 */
public interface BaseHandler {

    boolean support(MsgData data);

    void handle(MsgData data);

}
