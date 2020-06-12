package net.ahwater.service.impl;

import net.ahwater.bean.WxMsg;
import net.ahwater.dao.WxMsgDao;

import net.ahwater.service.WxMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by YYC on 2017/7/4.
 */
@Service
public class WxMsgServiceImpl implements WxMsgService {

    @Autowired
    private WxMsgDao dao;

    @Override
    public int addOne(WxMsg msg) {
        return dao.addOne(msg);
    }

}
