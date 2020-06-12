package net.ahwater.dao;

import net.ahwater.bean.Hh_billboard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yqz on 2018/8/23
 */
@Mapper
public interface BillBoardDao {

     int insertOne(@Param("entity") Hh_billboard billboard);

    List<Hh_billboard> listAllByAddvcdAndLevel(@Param("addvcd")String addvcdStartWith,@Param("level") String level);
}