package net.ahwater.dao;


import net.ahwater.bean.Hh_river_head;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Reeye on 2017/7/17.
 * Nothing is true but improving yourself.
 */
@Mapper
public interface RiverHeadDao {

    /**
     * 查询所有河段河长关系信息
     * @return
     */
    List<Hh_river_head> listRiverHead();

    /**
     * 根据区划码查询所有河段-河长信息
     * @param addvcd
     * @return
     */
    List<Hh_river_head> listRiverHeadByAddvcd(@Param("addvcd") String addvcd);

    /**
     * 根据河流编码查询所有河段-河长信息
     * @param ennmcd
     * @return
     */
    List<Hh_river_head> listRiverHeadByEnnmcd(@Param("ennmcd") String ennmcd);

    /**
     * 根据行政编码&河流编码查询所有河流-河段信息
     * @param addvcd
     * @param ennmcd
     * @return
     */
    Hh_river_head getRiverHeadByAddvcdAndEnnmcd(@Param("addvcd") String addvcd, @Param("ennmcd") String ennmcd);

    /**
     * 根据河长编码查询河长管理的河流-河段信息
     * @param hdno
     * @return
     */
    List<Hh_river_head> listRiverHeadByHdNo(@Param("hdno") String hdno);

    /**
     * 根据行政区划码返回河长姓名，河段名，河流名，河流编码
     * @param addvcd
     * @return
     */
    List<HashMap<String, Object>> listRiverMsgAndRvhdMsgByAddvcd(@Param("addvcd") String addvcd);
}
