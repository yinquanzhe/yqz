package net.ahwater.yqz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by yqz on 2019/6/28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("TS_USER")
public class User implements Serializable {
    private int id;
    private int fid;
    private String username;
    private String password;
    private String txsj;
}