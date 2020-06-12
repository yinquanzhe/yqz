package net.ahwater.dao;


import net.ahwater.bean.Hh_addvcd;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Reeye on 2017/7/17.
 * Nothing is true but improving yourself.
 */
@Mapper
public interface AddvcdDao {

    /**
     * 查询所有行政区划码
     * @return
     */
    List<Hh_addvcd> listAddvcd();

    /**
     * 查询市级别所有区划码
     * @return
     */
    List<Hh_addvcd> listAddvcdOfCity();


    /**
     * 查询县级别所有区划码
     * @param addvcd 市级别的区划码
     * @return
     */
    List<Hh_addvcd> listAddvcdOfCounty(@Param("addvcd") String addvcd);

    /**
     * 查询市下所有县区划码
     * @param addvcd
     * @return
     */
    List<Hh_addvcd> listAddvcdOfCountyByCity(@Param("addvcd") String addvcd);


}
