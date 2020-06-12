package net.ahwater.dataServ.entity.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 系统分类模块
 * Created by yqz on 2020/3/19
 */
@Data
public class SortDTO {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("备注")
    private String remark;
}