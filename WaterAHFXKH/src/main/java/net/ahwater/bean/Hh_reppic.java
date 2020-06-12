package net.ahwater.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 上报图片信息表
 */
public class Hh_reppic {

    private Integer picno;
    private Integer repno;
    private String flpath;
    private Date tm;

    public Hh_reppic() {
    }
    public Hh_reppic(Integer repno, String flpath, Date tm) {
        this.repno = repno;
        this.flpath = flpath;
        this.tm = tm;
    }


    public Integer getPicno() {
        return picno;
    }

    public void setPicno(Integer picno) {
        this.picno = picno;
    }

    public Integer getRepno() {
        return repno;
    }

    public void setRepno(Integer repno) {
        this.repno = repno;
    }

    public String getFlpath() {
        return flpath;
    }

    public void setFlpath(String flpath) {
        this.flpath = flpath;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    @Override
    public String toString() {
        return "Hh_reppic{" +
                "picno=" + picno +
                ", repno=" + repno +
                ", flpath='" + flpath + '\'' +
                ", tm=" + tm +
                '}';
    }

}
