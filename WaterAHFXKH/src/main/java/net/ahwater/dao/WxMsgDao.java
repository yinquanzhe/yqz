package net.ahwater.dao;


import net.ahwater.bean.WxMsg;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

/**
 * Created by YYC on 2017/7/4.
 */
@MapperScan
public interface WxMsgDao {

    int addOne(@Param("msg") WxMsg msg);

}
