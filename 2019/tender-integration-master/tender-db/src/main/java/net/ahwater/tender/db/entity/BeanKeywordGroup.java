package net.ahwater.tender.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  关键词分组
 * </p>
 *
 * @author Reeye
 * @since 2019-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_keyword_group")
public class BeanKeywordGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 一级部门ID(外键)
     */
    private Integer unitId;

    /**
     * 创建人ID(外键)
     */
    private Integer createdUserId;

    /**
     * 是否开启混淆词过滤
     */
    private Boolean noConfused;

    /**
     * 是否开启排除词过滤
     */
    private Boolean noExcluded;

    /**
     * 分组下的关键词集合
     */
    @TableField(exist = false)
    private List<BeanKeyword> keywords;

    /**
     * 关联的子部门
     */
    @TableField(exist = false)
    private List<BeanKeywordGroupUnit> allocatedUnits;

}
