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
@TableName("t_module_grab_log")
public class BeanModuleGrabLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 抓取记录ID(外键)
     */
    private Integer grabLogId;

    /**
     * 模块ID
     */
    private Integer moduleId;

    /**
     * 开始抓取时间
     */
    private Date startTime;

    /**
     * 抓取结束时间
     */
    private Date endTime;

    /**
     * 抓取到数量
     */
    private Integer grabCount;

    /**
     * 入库数量
     */
    private Integer savedCount;

}
