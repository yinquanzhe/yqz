package net.ahwater.base.log;

import lombok.Data;

import java.util.Date;

/**
 * Created by Reeye on 2020/4/17 10:36
 * Nothing is true but improving yourself.
 */
@Data
public class LogEntity {

    private LogType type;
    private String content;
    private Date time;

}
