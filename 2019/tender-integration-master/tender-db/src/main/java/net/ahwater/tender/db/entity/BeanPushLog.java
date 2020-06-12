package net.ahwater.tender.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
@TableName("t_push_log")
public class BeanPushLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * Item ID
     */
    private Integer itemId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 关键词ID
     */
    private Integer keywordId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 推送完成时间
     */
    private Date pushTime;

    /**
     * 状态 1未推送 2已推送 3已阅读
     */
    private Integer status;

    /**
     * 推送编号
     */
    private String code;


}
