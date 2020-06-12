package net.ahwater.filter;


import net.ahwater.bean.ParamEnum;
import net.ahwater.config.WxConfig;
import net.ahwater.service.OAuth2Service;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

//@Component
//@WebFilter(filterName="OAuth2Filter",urlPatterns= {"/report"},description="微信网页授权授权过滤器")
public class OAuth2Filter implements  Filter  {

	private static Logger log = LoggerFactory.getLogger(OAuth2Filter.class);

	@Autowired
	private OAuth2Service oas;


	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		log.info("OAuth2Filter init()");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		HttpSession session = req.getSession();
		String reqParam=req.getQueryString();
		//获取当前页面的请求地址
		String url = req.getScheme()+"://"+req.getServerName()+req.getRequestURI();
		url = reqParam!=null?url+"?"+req.getQueryString():url;
		//获取授权code值
		String code = request.getParameter("code");
		//请求参数
		Map<String,String> param = new HashMap<>();
		try {

			String openid = (String)session.getAttribute("openid");
			//未授权未登录【1】
			if((openid==null||openid.equals(""))&&code==null) {
				param.put(ParamEnum.APPID.getParamName(), WxConfig.APPID);
				param.put(ParamEnum.REDIRECT_URI.getParamName(),URLEncoder.encode(url, "utf-8"));
				param.put(ParamEnum.SCOPE.getParamName(), WxConfig.SCOPE);
				param.put(ParamEnum.STATE.getParamName(), System.currentTimeMillis()+"");
				//发起网页授权请求
				oas.oauth2(req, res, param);
			}else  if((openid==null||openid.equals(""))&&code != null){
				param.put(ParamEnum.APPID.getParamName(), WxConfig.APPID);
				param.put(ParamEnum.APPSECRET.getParamName(), WxConfig.APPSECRET);
				param.put(ParamEnum.CODE.getParamName(), code);
				String userInfo =  oas.accessToken(param);
				if(userInfo==null||userInfo.equals("")||userInfo.indexOf("access_token")==-1) {
					log.info("get userinfo fail:{}",userInfo);
					throw new Exception(userInfo);
				}

				JSONObject userObj = new JSONObject(userInfo);
				openid = userObj.getString("openid");

				session.setAttribute("openid", openid);
				session.setAttribute(url.split("\\?")[0], url);
				//缓存
				//oas.cacheToken(openid, userObj);
				log.info("page already authorized:{}",url);
				chain.doFilter(request, response);
			}else {
				//已登陆且已授权【3】
				log.info("page already authorized:{}",url);
				chain.doFilter(request, response);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("page authorize failed :{}",e.getMessage());
		} 
	}

	@Override
	public void destroy() {
		log.info("OAuth2Filter destroy()");
	}
	
}
