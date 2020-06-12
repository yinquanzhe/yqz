package net.ahwater.bean;
/** 
 * @author huoyufei 
 * @date 2017-08-02 17:42:28 
 * @version 1.0  
 */
public enum MsgTypeEnum {

	TEXT(1,"text"),
	IMAGE(2,"image"),
	VOICE(3,"voice"),
	VIDEO(4,"video"),
	MUSIC(5,"music"),
	NEWS(6,"news"),
	MPNEWS(7,"mpnews"),
	SHORTVIDEO(8,"shortvideo"),
	LOCATION(9,"location"),
	LINK(10,"link"),
	EVENT(11,"event");
	
	private int code;
	private String type;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private MsgTypeEnum(int code, String type) {
		this.code = code;
		this.type = type;
	}
	
}
