package net.ahwater.main.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Reeye on 2020/4/21 13:28
 * Nothing is true but improving yourself.
 */
@ApiModel(description = "数据返回统一格式")
@Data
@NoArgsConstructor
public class R<T> implements Serializable {

    public static final int OK = 0;
    public static final int ERROR = 1000;
    public static final int NOT_FOUND = 1001;
    public static final int NULL_DATA = 1002;
    public static final int INVALID_PARAMETER = 1003;
    public static final int UNAUTHORIZED = 1004;

    @ApiModelProperty(value = "错误代码", notes = "0为正常")
    private int code;
    @ApiModelProperty("信息")
    private String msg;
    @ApiModelProperty("数据")
    private T data;

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R of(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    public static <T> R ok(T data) {
        return R.ok("成功", data);
    }

    public static <T> R ok(String msg, T data) {
        return R.of(OK, msg, data);
    }

    public static R error() {
        return R.error("错误", null);
    }

    public static R error(String msg) {
        return R.error(msg, null);
    }

    public static <T> R error(T data) {
        return R.error("错误", data);
    }

    public static <T> R error(String msg, T data) {
        return new R<>(ERROR, msg, data);
    }

    public static R nulls(String msg) {
        return R.of(NULL_DATA, msg, null);
    }

}
