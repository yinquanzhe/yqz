package net.ahwater.service.impl;


import net.ahwater.bean.*;
import net.ahwater.dao.AddvcdDao;
import net.ahwater.dao.BillBoardDao;
import net.ahwater.dao.ReportDao;
import net.ahwater.dao.RiverHeadDao;
import net.ahwater.service.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */
@Service
public class WeixinServiceImpl implements WeixinService {

    @Autowired
    private AddvcdDao addvcdDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private RiverHeadDao riverHeadDao;
    @Autowired
    private BillBoardDao billBoardDao;

    @Override
    public List<Hh_addvcd> listAddvcdOfCity() {
        return addvcdDao.listAddvcdOfCity();
    }

    @Override
    public List<Hh_addvcd> listAddvcdOfCountyByAddvcd(String addvcd) {
        return addvcdDao.listAddvcdOfCountyByCity(addvcd);
    }

    @Override
    public List<Hh_river_head> listRiverHeadByAddvcd(String addvcd) {
        return riverHeadDao.listRiverHeadByAddvcd(addvcd);
    }

    @Override
    public int insertReport(Hh_repprob repprob) {
        return reportDao.insertReport(repprob);
    }

    @Override
    public int insertReportPic(Hh_reppic reppic) {
        return reportDao.insertReportPic(reppic);
    }

    @Override
    public List<Hh_repprob> listReport(List<Integer> rlrcnos, Date stm, Date etm) {
        return reportDao.listReport(rlrcnos, stm, etm);
    }

    @Override
    public List<Hh_reptp> listReportType() {
        return reportDao.listReportType();
    }

    @Override
    public int insertBillboard(Hh_billboard billboard) {
        return billBoardDao.insertOne(billboard);
    }

    @Override
    public List<Hh_billboard> listAllByAddvcdAndLevel(String addvcdStartWith, String level) {
        return billBoardDao.listAllByAddvcdAndLevel(addvcdStartWith, level);
    }

}
