package net.ahwater.tender.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
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
@TableName("t_role")
public class BeanRole implements Serializable {

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
     * 说明
     */
    private String intro;

    /**
     * 类别(1内部使用 2免费角色 3付费角色)
     */
    private Integer type;

    /**
     * 可抓取的网站数量
     */
    private Integer websiteCount;

    /**
     * 可配置的关键词数量
     */
    private Integer keywordCount;

    @TableField(exist = false)
    private List<BeanPrice> prices = new ArrayList<>();

}
