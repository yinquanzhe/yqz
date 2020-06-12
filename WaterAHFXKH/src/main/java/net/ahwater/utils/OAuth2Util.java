package net.ahwater.utils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author huoyufei
 * @date 2017-07-17 18:40:24
 * @version 1.0
 */
public class OAuth2Util extends WxHttpUtil {

	/**
	 * 进入授权页面
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @param param
	 */
	public static void oauth2(HttpServletRequest request, HttpServletResponse response, String url,
			Map<String, String> param) {

		String formattedUrl = reformUrl(url, param);
		redirector(request, response, formattedUrl);
	}

	/**
	 * 获取网页accessToken
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String accessToken(String url, Map<String, String> param) {

		String formattedUrl = reformUrl(url, param);
		return sendGet(formattedUrl);
	}

	/**
	 * 刷新access_token
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String refreshToken(String url, Map<String, String> param) {

		String formattedUrl = reformUrl(url, param);
		return sendGet(formattedUrl);
	}

	/**
	 * 拉取用户信息(需scope为 snsapi_userinfo)
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String userInfo(String url, Map<String, String> param) {

		String formattedUrl = reformUrl(url, param);
		return sendGet(formattedUrl);
	}

	/**
	 * 检验授权凭证（access_token）是否有效
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String checkToken(String url, Map<String, String> param) {

		String formattedUrl = reformUrl(url, param);
		return sendGet(formattedUrl);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String pageTicket(String url, Map<String, String> param) {
		String formattedUlr = reformUrl(url, param);
		return sendGet(formattedUlr);
				
	}

}
