package net.ahwater.bean;

/**
 * Created by YYC on 2017/7/4.
 */
public class WxMsg {

    private int id;
    private int user_id;
    private String time;
    private String title;
    private String local_img;
    private String wx_img;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocal_img() {
        return local_img;
    }

    public void setLocal_img(String local_img) {
        this.local_img = local_img;
    }

    public String getWx_img() {
        return wx_img;
    }

    public void setWx_img(String wx_img) {
        this.wx_img = wx_img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "WxMsg{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", local_img='" + local_img + '\'' +
                ", wx_img='" + wx_img + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

}
