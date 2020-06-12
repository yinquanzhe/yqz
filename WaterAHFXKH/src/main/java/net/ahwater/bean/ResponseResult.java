package net.ahwater.bean;



/**
 * Created by YYC on 2017/7/4.
 */
public class ResponseResult<T> {
    // errno 即错误代码，0 表示没有错误。
    private int errno;
    // msg 表示返回信息
    private String msg;
    // data 是一个数组，返回若干图片的线上地址
    private T data;

    public ResponseResult() {
        super();
    }

    public ResponseResult(int errno, String msg, T data) {
        this.errno = errno;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseResult correct(T data) {
        return new ResponseResult(0, "正常", data);
    }

    public static <T> ResponseResult wrong(T data) {
        return new ResponseResult(1000, "错误", data);
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "errno=" + errno +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }


}
