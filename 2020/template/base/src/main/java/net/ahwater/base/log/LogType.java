package net.ahwater.base.log;

/**
 * Created by Reeye on 2020/4/16 13:27
 * Nothing is true but improving yourself.
 */
public enum LogType {

    ADD("新增"), REMOVE("删除"), VIEW("查询"), MODIFY("修改"), ERROR("错误");

    private String value;

    LogType(String value) {
        this.value = value;
    }

    public String val() {
        return this.value;
    }

}
