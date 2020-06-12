package net.ahwater.tender.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("t_grab_error")
public class BeanGrabError implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 模块ID
     */
    private Integer moduleId;

    /**
     * 时间
     */
    private Date time;

    /**
     * URL
     */
    private String url;

    /**
     * HTTP状态码
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String msg;


}
