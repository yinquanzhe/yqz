package net.ahwater.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ahwater.bean.WxUrl;
import net.ahwater.service.OAuth2Service;
import net.ahwater.utils.OAuth2Util;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/** 
 * @author huoyufei 
 * @date 2017-07-24 09:02:10 
 * @version 1.0  
 */
@Service
public class OAuth2ServiceImpl implements OAuth2Service {

	private WxUrl wxUrl = new WxUrl();

	@Override
	public void oauth2(HttpServletRequest request, HttpServletResponse response, Map<String, String> param) throws Exception {
		
		OAuth2Util.oauth2(request, response, wxUrl.getOauth2(), param);
	}

	@Override
	public String accessToken(Map<String, String> param) throws Exception {
		
		return OAuth2Util.accessToken(wxUrl.getAccesstoken(), param);
	}

	@Override
	public String refreshToken(Map<String, String> param) throws Exception {
		
		return OAuth2Util.refreshToken(wxUrl.getRefreshtoken(), param);
	}

	@Override
	public String userInfo(Map<String, String> param) throws Exception {
		
		return OAuth2Util.userInfo(wxUrl.getUserinfo(), param);
	}

	@Override
	public String checkToken(Map<String, String> param) throws Exception {
		
		return OAuth2Util.checkToken(wxUrl.getChecktoken(), param);
	}

	@Override
	public String takeJsApi(Map<String, String> param) throws Exception {
		return OAuth2Util.pageTicket(wxUrl.getJsapi(), param);
	}

}
