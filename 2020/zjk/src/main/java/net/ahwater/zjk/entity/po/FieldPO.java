package net.ahwater.zjk.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by yqz on 2020/5/27
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("WT_ZJK_Field")
public class FieldPO implements Serializable{
    private static final long serialVersion =0L;

    private Integer id;
    private String code;
    private String name;

}