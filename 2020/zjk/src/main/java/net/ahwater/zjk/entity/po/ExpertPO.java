package net.ahwater.zjk.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yqz on 2020/5/26
 * 专家信息
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("WT_ZJK_Expert")
public class ExpertPO implements Serializable{
    private static final long serialVersion =0L;

    private Integer id;
    private String name;
    private String sex;
    private Integer enable;
    private String institution;
    private String province;
    private String city;
    private String duty;
    private String title;
    private String idcode;
    private String domain;
    private String works;
    private String birthday;
    private String university;
    private String major;
    private String degree;
    private String telephone;
    private String cellphone1;
    private String addre;
    private String postalcode;
    private String disableReason;
    private Integer inBlackList;
    private String note;
    private Date tm;

}