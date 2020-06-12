package net.ahwater.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 公示牌上传
 * Created by yqz on 2018/8/23
 */
public class Hh_billboard {

    private Integer bbno;
    private String apath;
    private String bpath;
    private Double ln;
    private Double lt;
    private String admlev;
    private String type;
    private String sctnm;
    private String hdnm;
    private String hdph;
    private String staddr;
    private String etaddr;
    private String addvcd;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date tm;

    public Integer getBbno() {
        return bbno;
    }

    public void setBbno(Integer bbno) {
        this.bbno = bbno;
    }

    public String getApath() {
        return apath;
    }

    public void setApath(String apath) {
        this.apath = apath;
    }

    public String getBpath() {
        return bpath;
    }

    public void setBpath(String bpath) {
        this.bpath = bpath;
    }

    public Double getLn() {
        return ln;
    }

    public void setLn(Double ln) {
        this.ln = ln;
    }

    public Double getLt() {
        return lt;
    }

    public void setLt(Double lt) {
        this.lt = lt;
    }

    public String getAdmlev() {
        return admlev;
    }

    public void setAdmlev(String admlev) {
        this.admlev = admlev;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSctnm() {
        return sctnm;
    }

    public void setSctnm(String sctnm) {
        this.sctnm = sctnm;
    }

    public String getHdnm() {
        return hdnm;
    }

    public void setHdnm(String hdnm) {
        this.hdnm = hdnm;
    }

    public String getHdph() {
        return hdph;
    }

    public void setHdph(String hdph) {
        this.hdph = hdph;
    }

    public String getStaddr() {
        return staddr;
    }

    public void setStaddr(String staddr) {
        this.staddr = staddr;
    }

    public String getEtaddr() {
        return etaddr;
    }

    public void setEtaddr(String etaddr) {
        this.etaddr = etaddr;
    }

    public String getAddvcd() {
        return addvcd;
    }

    public void setAddvcd(String addvcd) {
        this.addvcd = addvcd;
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }
}