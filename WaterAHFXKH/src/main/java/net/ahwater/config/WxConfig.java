package net.ahwater.config;

import net.ahwater.wx.utils.WxSubscribeUtil;

import java.util.HashMap;
import java.util.Map;

public class WxConfig {

	/**测试号
	 * wx67a798f37ea1309a 
	 * 6c19f1861ab5815f7e1ec5046c5f5228
     *
     * wx43180d240d6ce22a
     * 68c14468f448c03bf322fbca26a7baf7
	 */

    /**正式号
     * wx0966d8e31d5a1811
     * 70a684b863af879b3694bac8a2d756b7
     */

	/**
	 * 公众号应用信息
	 */
	public static final String APPID = "wx382cb025122bb9fd";// 账号id
	public static final String APPSECRET = "031e5ca1f203e8e41877b573ea1aac86";// 账号密钥

	public static String TOKEN = "weixinqiang";// 接口验证token
	public static String SERVER_ACCESS_TOKEN = "";// 服务器token
	public static long SERVER_ACCESS_TOKEN_TIME = 0;
	public static final String SCOPE = "snsapi_base";// snsapi_base||snsapi_userinfo授权类型
	public static final String SITE_ROOT = "http://wx.forling.com/wx/";
	//public static final String SITE_ROOT = " http://cz.welcometoback.com/czwxs"

	// jssdk网页票据
	public static String JsapiTicket = "";
	public static long JsapiTicketTime = 0;

	//高德逆地理编码API服务地址：
	public static final String GeoLocationService = "http://restapi.amap.com/v3/geocode/regeo";
	public static final String GeoServiceKey = "3eec111d9263cc379ceb65dbf3ff0701";
	public static final String UploadFilePath = "D:\\";

    //百度地理编码
    public static final String BDLocationService = "http://api.map.baidu.com/geocoder/v2/";
    public static final String BDServiceKey = "S9EffGUxRxR73jvWyKywoluWOTtNlBDA";



    public static void main(String[] args) {
        //String s = "<p>qeqeq<img src=\"http://mmbiz.qpic.cn/mmbiz_jpg/4eQDxUqftkCCOBfAwWAppWC03Z5mep4fGLr9A9uicnYqC5G1cqysNIXd0kwRK1XxC7XLB46X3aQyp8dHKm6nFicg/0\" style=\"max-width: 100%;\">qdwqweqe</p>";
//        System.out.println(s.replaceAll("\"", "\\\\\"").replaceAll("/", "\\\\/"));
//        System.out.println(WxSubscribeUtil.geocode("116.423672", "39.930927"));
    }

}
