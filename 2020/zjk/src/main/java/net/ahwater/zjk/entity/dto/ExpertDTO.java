package net.ahwater.zjk.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by yqz on 2020/5/26
 */
@Data
public class ExpertDTO {

    @ApiModelProperty("主键")
    private Integer id;
    @ApiModelProperty("专家名称")
    private String name;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("是否启用")
    private Integer enable;
    @ApiModelProperty("工作单位")
    private String institution;
    @ApiModelProperty("省份")
    private String province;
    @ApiModelProperty("城市")
    private String city;
    @ApiModelProperty("职务")
    private String duty;
    @ApiModelProperty("职称")
    private String title;
    @ApiModelProperty("身份证号码")
    private String idcode;
    @ApiModelProperty("专业领域")
    private String domain;
    @ApiModelProperty("工作范围")
    private String works;
    @ApiModelProperty("生日")
    private String birthday;
    @ApiModelProperty("毕业院校")
    private String university;
    @ApiModelProperty("所学专业")
    private String major;
    @ApiModelProperty("学位")
    private String degree;
    @ApiModelProperty("座机")
    private String telephone;
    @ApiModelProperty("移动电话")
    private String cellphone1;
    @ApiModelProperty("地址")
    private String addre;
    @ApiModelProperty("邮编")
    private String postalcode;
    @ApiModelProperty("禁用理由")
    private String disableReason;
    @ApiModelProperty("是否加入黑名单")
    private Integer inBlackList;
    @ApiModelProperty("备注历史信息")
    private String note;
    @ApiModelProperty("时间")
    private Date tm;

}