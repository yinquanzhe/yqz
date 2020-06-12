package net.ahwater.bean;

public class WxUrl {

    private String oauth2= "_uri=REDIRECT_URI&reshttps://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirectponse_type=code&scope=SCOPE&state=STATE#wechat_redirect" ;//#用户授权，获取code
    private String  accesstoken= "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";// #通过code换取网页授权access_token GET
    private String  refreshtoken= "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";// #当access_token超时后，可以使用refresh_token进行刷新 refresh_token有效期为30天 GET
    private String   userinfo= "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";// #如果网页授权作用域为snsapi_userinfo拉取用户信息(需scope为 snsapi_userinfo) GET
    private String checktoken= "https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID" ;//#检验授权凭证（access_token）是否有效 GET
    private String jsapi= "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi" ;//#jsapi_ticket

    public String getOauth2() {
        return oauth2;
    }

    public void setOauth2(String oauth2) {
        this.oauth2 = oauth2;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }

    public void setRefreshtoken(String refreshtoken) {
        this.refreshtoken = refreshtoken;
    }

    public String getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(String userinfo) {
        this.userinfo = userinfo;
    }

    public String getChecktoken() {
        return checktoken;
    }

    public void setChecktoken(String checktoken) {
        this.checktoken = checktoken;
    }

    public String getJsapi() {
        return jsapi;
    }

    public void setJsapi(String jsapi) {
        this.jsapi = jsapi;
    }
}
