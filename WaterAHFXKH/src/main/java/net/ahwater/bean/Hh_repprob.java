package net.ahwater.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
/**
 * 上报问题信息表
 */
public class Hh_repprob {

        private Integer repno;
        private Integer rlrcno;
        private String rlrcnm;
        private Integer tpno;
        private String tpnm;
        private String name;
        private String phone;
        private String qadrs;
        private Double ln;
        private Double lt;
        private String qdesc;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date reptm;
        private Date tm;
        private List<Hh_reppic> pics;

        public Integer getRepno() {
            return repno;
        }

        public void setRepno(Integer repno) {
            this.repno = repno;
        }

        public Integer getRlrcno() {
            return rlrcno;
        }

        public void setRlrcno(Integer rlrcno) {
            this.rlrcno = rlrcno;
        }

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

        public String getRlrcnm() {
            return rlrcnm;
        }

        public void setRlrcnm(String rlrcnm) {
            this.rlrcnm = rlrcnm;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getQadrs() {
            return qadrs;
        }

        public void setQadrs(String qadrs) {
            this.qadrs = qadrs;
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

        public String getQdesc() {
            return qdesc;
        }

        public void setQdesc(String qdesc) {
            this.qdesc = qdesc;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        public Date getReptm() {
            return reptm;
        }

        public void setReptm(Date reptm) {
            this.reptm = reptm;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        public Date getTm() {
            return tm;
        }

        public void setTm(Date tm) {
            this.tm = tm;
        }

        public List<Hh_reppic> getPics() {
            return pics;
        }

        public void setPics(List<Hh_reppic> pics) {
            this.pics = pics;
        }

        @Override
        public String toString() {
            return "Hh_repprob{" +
                    "repno=" + repno +
                    ", rlrcno=" + rlrcno +
                    ", rlrcnm='" + rlrcnm + '\'' +
                    ", phone='" + phone + '\'' +
                    ", qadrs='" + qadrs + '\'' +
                    ", ln=" + ln +
                    ", lt=" + lt +
                    ", qdesc='" + qdesc + '\'' +
                    ", reptm=" + reptm +
                    ", tm=" + tm +
                    ", pics=" + pics +
                    '}';
        }

    }
