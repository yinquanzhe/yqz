package net.ahwater.zjk.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Reeye on 2020/4/23 17:01
 * Nothing is true but improving yourself.
 */
@Data
@AllArgsConstructor
public class MsgData{

    private String type;
    private String yccd;
    private String time;
    private Number value;
    private String handleTime;

}
