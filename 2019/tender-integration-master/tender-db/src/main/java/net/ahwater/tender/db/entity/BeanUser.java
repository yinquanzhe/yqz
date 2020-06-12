package net.ahwater.tender.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author Reeye
 * @since 2018-11-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class BeanUser implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单位ID(默认0个人用户)
     */
    private Integer unitId;

    /**
     * 角色ID 1系统管理员 2单位管理员 3普通用户
     */
    private Integer roleId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    private String pwd;

    /**
     * 微信openid
     */
    @JsonIgnore
    private String wxopenid;

    /**
     * 信息推送时间(-1为实时推送)
     */
    private Integer pushTime;

    /**
     * 付费用户服务到期时间
     */
    private Date expiredTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户状态(1正常 2已删除)
     */
    private Integer status;

    /**
     * 用户部门
     */
    @TableField(exist = false)
    private BeanUnit unit;

    /**
     * 用户角色
     */
    @TableField(exist = false)
    private BeanRole role;

    /**
     * 用户关注的网站
     */
    @TableField(exist = false)
    private List<BeanWebsite> websites;

    /**
     * 用户的关键词分组
     */
    @TableField(exist = false)
    private List<BeanKeywordGroup> keywordGroups;

    /**
     * 用户的排除词集合
     */
    @TableField(exist = false)
    private List<BeanExcludedWord> excludedWords;

    /**
     * 用户的混淆词集合
     */
    @TableField(exist = false)
    private List<BeanExcludedWord> confusedWords;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.roleId));
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.pwd;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return getStatus() == 1;
    }

}
