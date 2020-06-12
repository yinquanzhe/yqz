package net.ahwater.service;



import net.ahwater.bean.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */
public interface WeixinService {

    /**
     * 查询所有市信息
     * @return
     */
    List<Hh_addvcd> listAddvcdOfCity();

    /**
     * 查询市下所有县信息
     * @param addvcd
     * @return
     */
    List<Hh_addvcd> listAddvcdOfCountyByAddvcd(String addvcd);

    /**
     * 查询市(包括县信息)或县下所有河段信息
     * @param addvcd
     * @return
     */
    List<Hh_river_head> listRiverHeadByAddvcd(String addvcd);

    /**
     * 新增一条公众上报记录
     * @param repprob
     * @return
     */
    int insertReport(Hh_repprob repprob);

    /**
     * 新增一条上报照片记录
     */
    int insertReportPic(Hh_reppic reppic);

    /**
     * 查询上报记录
     * @param rlrcnos
     * @param stm
     * @param etm
     * @return
     */
    List<Hh_repprob> listReport(List<Integer> rlrcnos, Date stm, Date etm);

    /**
     * 查询在用的上报类型
     * @return
     */
    List<Hh_reptp> listReportType();

    int insertBillboard(Hh_billboard billboard);

    List<Hh_billboard> listAllByAddvcdAndLevel(String addvcdStartWith, String level);
}
