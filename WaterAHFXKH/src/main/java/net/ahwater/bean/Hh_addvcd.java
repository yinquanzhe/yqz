package net.ahwater.bean;

/**
 * 行政区划码
 */
public class Hh_addvcd {
    private String addvcd;
    private String pubyear;
    private String addvnm;

    public Hh_addvcd() {
    }

    public Hh_addvcd(String addvcd, String addvnm) {
        this.addvcd = addvcd;
        this.addvnm = addvnm;
    }

    public String getAddvcd() {
        return addvcd;
    }

    public void setAddvcd(String addvcd) {
        this.addvcd = addvcd;
    }

    public String getPubyear() {
        return pubyear;
    }

    public void setPubyear(String pubyear) {
        this.pubyear = pubyear;
    }

    public String getAddvnm() {
        return addvnm;
    }

    public void setAddvnm(String addvnm) {
        this.addvnm = addvnm;
    }
}
