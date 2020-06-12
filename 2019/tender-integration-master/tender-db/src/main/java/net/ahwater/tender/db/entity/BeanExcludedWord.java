package net.ahwater.tender.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *  排除词
 * </p>
 *
 * @author Reeye
 * @since 2019-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_excluded_word")
public class BeanExcludedWord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单位ID
     */
    private Integer unitId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 词语
     */
    private String word;

    /**
     * 类型 1混淆词 2排除词
     */
    private Integer type;

}
