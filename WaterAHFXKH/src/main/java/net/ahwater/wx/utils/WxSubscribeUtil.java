package net.ahwater.wx.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import net.ahwater.config.WxConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信订阅号
 * @author Administrator
 *
 */
public class WxSubscribeUtil {

	private static Logger log = LoggerFactory.getLogger(WxSubscribeUtil.class);

	/**
	 * 获取服务器access_token
	 * 
	 * @return
	 */
	public static String getServerAccessToken() {
		long sysTime = System.currentTimeMillis();
		if (WxConfig.SERVER_ACCESS_TOKEN.equals("")||WxConfig.SERVER_ACCESS_TOKEN_TIME==0||WxConfig.SERVER_ACCESS_TOKEN_TIME+7000*1000<=sysTime) {
			log.info("服务token尚未刷新,正在等待刷新...");
			synchronized ("token") {
				sysTime = System.currentTimeMillis();
				if(WxConfig.SERVER_ACCESS_TOKEN_TIME+7000*1000<=sysTime){
					try {

						URL url = new URL("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ WxConfig.APPID + "&secret=" + WxConfig.APPSECRET);// 获取accessToken地址的url
						URLConnection connection = url.openConnection();
						connection.connect();
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line = in.readLine();
						System.out.println(line);
						JSONObject json = new JSONObject(line);
						WxConfig.SERVER_ACCESS_TOKEN = json.getString("access_token");// 存入token
						WxConfig.SERVER_ACCESS_TOKEN_TIME = System.currentTimeMillis();// 存入当前时间戳
						log.info("access_token：已刷新");
					} catch (Exception e) {
						e.printStackTrace();
						WxConfig.SERVER_ACCESS_TOKEN = "";
						WxConfig.SERVER_ACCESS_TOKEN_TIME = 0;
					    log.info("access_token 刷新失败！" + e.getMessage());
					}
				}else{
					log.info("access_token：当前可用");
				}
			}
		}		
		return WxConfig.SERVER_ACCESS_TOKEN;
	}
	/**
	 * 获取已经关注的用户openid列表
	 * 
	 * @param nextId
	 * @return
	 */
	public static List<String> getAllUserOperId(String nextId,String access_token) {
		List<String> list = new ArrayList<String>();
		if (access_token == "") {
			return null;
		}
		try {
			URL url = null;
			if ("-1".equals(nextId)) {
				url = new URL("https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + access_token);
			} else {
				url = new URL("https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + access_token
						+ "&next_openid=" + nextId);
			}
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = in.readLine();
			in.close();
			JSONObject json1 = new JSONObject(line);
			JSONObject json2 = new JSONObject(json1.get("data").toString());
			JSONArray jsonArray = json2.getJSONArray("openid");
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(jsonArray.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("关注用户列表获取失败>>>>>>>>>>>>>" + e.getMessage());
		}
		return list;

	}

	/**
	 * 根据用户openid获取用户信息
	 * 
	 * @param openId
	 * @return
	 */
	public static Map<String, String> getUserInfoByOpenId(String openId,String access_token) {
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			URL url = new URL("https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid="
					+ openId + "&lang=zh_CN");
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			JSONObject jb = new JSONObject(br.readLine());
			Iterator<String> it = jb.keys();
			while (it.hasNext()) {
				String key = String.valueOf(it.next());
				String value = jb.get(key) + "";
				resMap.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("获取用户信息失败！>>>>>>>>>>>>>" + e.getMessage());
		}
		return resMap;
	}
	
	/**
	 * 互动48小时内发送客服消息
	 * @param serverToken
	 * @param custiomMessage
	 * @return
	 */
	public static boolean sendCustomMessage(String serverToken,String custiomMessage){
		boolean flag = false;
		try {
            //创建连接
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+serverToken);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.connect();
            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(custiomMessage.getBytes("UTF-8"));
            out.flush();
            out.close();
            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            reader.close();
            connection.disconnect();
            JSONObject json = new JSONObject(sb.toString());
            if(json.getString("errmsg").equals("ok")){
            	flag = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return flag;
	}
	
	/**
	 * 调用网页授权
	 * @param scope
	 * @return
	 */
 	public static void oauth2(HttpServletResponse response, String redirect, String scope) {
		try {
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WxConfig.APPID
					+ "&redirect_uri=" + URLEncoder.encode(redirect, "UTF-8") + "&response_type=code&scope=" + scope
					+ "&state=" + System.currentTimeMillis() + "#wechat_redirect";
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
			log.info("重新定向失败！>>>>>>>>>>>>>>" + e.getMessage());
		}
	}

	/**
	 * 获取授权网页授权信息
	 * @param code
	 * @return
	 */
	public static Map<String, Object> getPageAccessToken(String code) {
		Map<String, Object> tokeninfo = null;
		try {
			URL url = new URL("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WxConfig.APPID + "&secret="
					+ WxConfig.APPSECRET + "&code=" + code + "&grant_type=authorization_code");
			URLConnection connection = url.openConnection();
			connection.connect();// 建立时间链接
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine();
			JSONObject json = new JSONObject(line);
			tokeninfo = new HashMap<>();
			tokeninfo.put("access_token", json.getString("access_token"));
			tokeninfo.put("expires_in", json.getInt("expires_in"));
			tokeninfo.put("refresh_token", json.getString("refresh_token"));
			tokeninfo.put("openid", json.getString("openid"));
			tokeninfo.put("scope", json.getString("scope"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.debug("获取网页授权的信息失败>>>>>>>>>>>>>>>>>" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.debug("获取网页授权的信息失败>>>>>>>>>>>>>>>>>>" + e.getMessage());
		}
		return tokeninfo;
	}

	/**
	 * 获取jsapi_ticket
	 * 网页票据
	 * @return
	 */
	public  static String getServerJsApiTicket() {
		String accessToken = getServerAccessToken();
		long sysTime = System.currentTimeMillis();
		if (WxConfig.JsapiTicket.equals("") || WxConfig.JsapiTicketTime == 0 || WxConfig.JsapiTicketTime + 7000 * 1000 < sysTime) {
			log.info("JsApiTicket,正在等待刷新...");
			synchronized ("accessToken") {
				sysTime = System.currentTimeMillis();
				if(WxConfig.JsapiTicketTime + 7000 * 1000 < sysTime){
					try {
						URL url = new URL("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi");
						URLConnection connection = url.openConnection();
						connection.connect();
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line = in.readLine();// 转换为json
						JSONObject json = new JSONObject(line);
						System.out.println(json.toString());
						WxConfig.JsapiTicket = json.getString("ticket");// 存入token
						WxConfig.JsapiTicketTime = System.currentTimeMillis();//票据存入时间
						log.info("JsApiTicket,已刷新!"+WxConfig.JsapiTicket);
					} catch (Exception e) {
						e.printStackTrace();
						WxConfig.JsapiTicket = "";
						WxConfig.JsapiTicketTime = 0;
						log.info("JsApiTicket获取失败！>>>>>>>>>>>>>>>>>" + e.getMessage());
					}
				}
			}	
		}
		return WxConfig.JsapiTicket;
	}

	/**
	 * 页面token刷新
	 * 
	 * @param appid
	 * @param refreshToken
	 * @return
	 */
	public synchronized static String refreshPageToken(String appid, String refreshToken) {
		String pageToken = null;
		try {
			URL url = new URL("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + appid
					+ "&grant_type=refresh_token&refresh_token=" + refreshToken);
			URLConnection connection = url.openConnection();
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line = in.readLine();// 转换为json
			JSONObject json = new JSONObject(line);
			pageToken = json.getString("access_token");// 存入token

		} catch (Exception e) {
			e.printStackTrace();
			log.info("access_token get 失败！>>>>>>>>>>>>>>>>>" + e.getMessage());
		}
		return pageToken;
	}

	/**
	 * 验证pagetoken是否失效
	 * 
	 * @param pageToken
	 * @param openId
	 * @return
	 */
	public static boolean checkPageToken(String pageToken, String openId) {
		boolean result = false;
		try {
			URL url = new URL("https://api.weixin.qq.com/sns/auth?access_token=" + pageToken + "&openid=" + openId);
			URLConnection connection = url.openConnection();
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine();// 转换为json
			JSONObject json = new JSONObject(line);
			System.out.println(json.toString() + "============================");
			result = json.getString("errmsg").equals("ok") ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("pageToken 失败！>>>>>>>>>>>>>>>>>" + e.getMessage());
		}
		return result;
	}

    /**
     * 地理逆解析
     * gps坐标  wgs84ll（ GPS经纬度）
     *
     * 返回正常时
     * {
     "status": 0,
     "result": {
     "location": {
     "lng": 116.33574357310609,
     "lat": 39.99044548226657
     },
     "formatted_address": "北京市海淀区中关村南三街8号",
     "business": "中关村,五道口,清华大学",
     "addressComponent": {
     "country": "中国",
     "country_code": 0,
     "province": "北京市",
     "city": "北京市",
     "district": "海淀区",
     "adcode": "110108",
     "street": "中关村南三街",
     "street_number": "8号",
     "direction": "北",
     "distance": "77"
     },
     "pois": [],
     "roads": [],
     "poiRegions": [],
     "sematic_description": "中科资源大厦东125米",
     "cityCode": 131
     }
     }
     *
     * 返回错误时格式
     * {
     "status": 200,
     "message": "APP不存在，AK有误请检查再重试"
     }
     * @param lng 经度
     * @param lat 纬度
     * @return
     */
    public static String geocode(String lng, String lat){
        try {
            URL url = new URL(WxConfig.BDLocationService + "?callback=renderReverse&coordtype=wgs84ll&location="+lat+","+lng+"&output=json&pois=0&ak="+WxConfig.BDServiceKey);
            URLConnection connection = url.openConnection();
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = in.readLine();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
