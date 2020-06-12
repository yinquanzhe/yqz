package net.ahwater.ahwaterCloud.util;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Reeye on 2017/10/23.
 * Nothing is true but improving yourself.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespResult<T> {

    private int code;
    private String msg;
    private T data;

    public RespResult(int code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public static <T> RespResult correct(T data) {
        return new RespResult(ErrorCode.CURRECT, "正常", data);
    }

    public static <T> RespResult wrong(T data) {
        return new RespResult(ErrorCode.INTERNAL_ERROR, "错误", data);
    }

    public static <T> RespResult loginOutTime(String msg) {
        return new RespResult(ErrorCode.LOGINOUTTIME, msg);
    }

    public static <T> RespResult success(String msg){
        return new RespResult(ErrorCode.CURRECT,msg);
    }

    public static <T> RespResult error(String msg){
        return new RespResult(ErrorCode.INTERNAL_ERROR,msg);
    }

}
