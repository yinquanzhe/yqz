package net.ahwater.bean;
/** 
 * @author huoyufei 
 * @date 2017-07-21 12:01:41 
 * @version 1.0  
 */
public enum ParamEnum {
	APPID(1,"APPID"),
	APPSECRET(2,"APPSECRET"),
	ACCESS_TOKEN(3,"ACCESS_TOKEN"),
	BASE_SCOPE(4,"snsapi_base"),
	INFO_SCOPE(5,"snsapi_userinfo"),
	KFACCOUNT(6,"KFACCOUNT"),
	REDIRECT_URI(7,"REDIRECT_URI"),
	SCOPE(8,"SCOPE"),
	STATE(9,"STATE"),
	CODE(10,"CODE"),
	SECRET(11,"SECRET"),
	REFRESH_TOKEN(12,"REFRESH_TOKEN"),
	OPENID(13,"OPENID"),
	MEDIA_ID(14,"MEDIA_ID"),
	TYPE(15,"TYPE"),
	NEXT_OPENID(16,"NEXT_OPENID");
	
	
	private int paramCode;
	private String paramName;
	private ParamEnum(int paramCode, String paramName) {
		this.paramCode = paramCode;
		this.paramName = paramName;
	}
	public int getParamCode() {
		return paramCode;
	}
	public void setParamCode(int paramCode) {
		this.paramCode = paramCode;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	
	
}
