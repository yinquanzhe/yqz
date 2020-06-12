package net.ahwater.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;



/**
 * @author huoyufei
 * @date 2017-07-21 14:31:54
 * @version 1.0
 */
public interface OAuth2Service {

	/**
	 * 进入授权页面
	 * @param request
	 * @param response
	 * @param param
	 * @throws Exception
	 */
	public void oauth2(HttpServletRequest request, HttpServletResponse response, Map<String, String> param) throws Exception;

	/**
	 * 获取网页accessToken
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String accessToken(Map<String, String> param) throws Exception;
	
	/**
	 * 刷新access_token
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String refreshToken(Map<String, String> param) throws Exception;

	/**
	 *  拉取用户信息(需scope为 snsapi_userinfo)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String userInfo(Map<String, String> param) throws Exception;

	/**
	 * 检验授权凭证（access_token）是否有效
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String checkToken(Map<String, String> param) throws Exception;

	/**
	 * 获取网页ticket
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String takeJsApi(Map<String, String> param)  throws Exception;
}
