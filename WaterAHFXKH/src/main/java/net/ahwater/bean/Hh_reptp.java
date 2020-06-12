package net.ahwater.bean;

public class Hh_reptp {

    private Integer tpno;
    private String tpnm;
    private Integer status;

    public Integer getTpno() {
        return tpno;
    }

    public void setTpno(Integer tpno) {
        this.tpno = tpno;
    }

    public String getTpnm() {
        return tpnm;
    }

    public void setTpnm(String tpnm) {
        this.tpnm = tpnm;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReportType{" +
                "tpno=" + tpno +
                ", tpnm='" + tpnm + '\'' +
                ", status=" + status +
                '}';
    }
}
