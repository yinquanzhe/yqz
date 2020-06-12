package net.ahwater.zjk.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by yqz on 2020/5/26
 */
@Data
@NoArgsConstructor
public class R<T> implements Serializable{
    private static final long serialVersionUID = 1L;

    public static final int OK = 0;
    public static final int ERROR = 1000;
    public static final int NOT_FOUND = 1001;
    public static final int NULL_DATA = 1002;
    public static final int INVALID_PARAMETER = 1003;
    public static final int UNAUTHORIZED = 1004;

    private int code;
    private String msg;
    private T data;

    private R(int code,String msg,T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R of(int code,String msg,Object data){ return new R<>(code, msg, data);}

    public static R ok(Object data){ return R.ok("ok",data);}

    public static R ok(String msg, Object data) {
        return R.of(OK, msg, data);
    }

    public static R error() {
        return R.error("出错了", null);
    }

    public static R error(String msg) {
        return R.error(msg, null);
    }

    public static R error(Object data) {
        return R.error("出错了", data);
    }

    public static R error(String msg, Object data) {
        return new R<>(ERROR, msg, data);
    }

    public static R nulls(String msg) {
        return R.of(NULL_DATA, msg, null);
    }
}