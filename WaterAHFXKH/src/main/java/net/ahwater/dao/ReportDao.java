package net.ahwater.dao;


import net.ahwater.bean.Hh_reppic;
import net.ahwater.bean.Hh_repprob;
import net.ahwater.bean.Hh_reptp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Reeye on 2017/11/10.
 * Nothing is true but improving yourself.
 */
@Mapper
public interface ReportDao {

    int insertReport(@Param("repprob") Hh_repprob repprob);

    int insertReportPic(@Param("reppic") Hh_reppic reppic);

    List<Hh_repprob> listReport(
            @Param("rlrcnos") List<Integer> rlrcnos,
            @Param("stm") Date stm,
            @Param("etm") Date etm);

    List<Hh_reptp> listReportType();

}
