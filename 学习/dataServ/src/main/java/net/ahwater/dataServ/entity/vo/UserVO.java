package net.ahwater.dataServ.entity.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

/**
 * Created by yqz on 2020/3/26
 */
@Data
public class UserVO {
    private Integer id;

    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("通讯录ID")
    private Long contactId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("帐号状态（1正常 0停用）")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("token")
    private String token;


}