package net.ahwater.dataServ.entity.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Created by yqz on 2020/3/26
 */
@Data
public class UserBO implements UserDetails{

    private final String ROLE_PREFIX = "ROLE_";

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


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status.equals("1");
    }

}