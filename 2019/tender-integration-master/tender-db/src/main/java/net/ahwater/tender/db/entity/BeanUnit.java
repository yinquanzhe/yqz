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
@TableName("t_unit")
public class BeanUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单位名称
     */
    private String name;

    /**
     * 父级id
     */
    private Integer parentId;

    /**
     * 关键词修改权限
     */
    private Boolean keywordModify;

    /**
     * 分配的地区编码开头(逗号分隔)
     */
    private String divisionStr;

}
