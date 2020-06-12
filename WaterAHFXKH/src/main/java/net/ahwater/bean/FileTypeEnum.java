package net.ahwater.bean;
/** 
 * @author huoyufei 
 * @date 2017-07-17 16:03:32 
 * @version 1.0  
 */
public enum FileTypeEnum {

	IMAGE(1,"image"),
	VOICE(2,"voice"),
	VIDEO(3,"video"),
	THUMB(4,"thumb"),
	NEWS(5,"news");
	
	private int code;
	private String type;
	private FileTypeEnum(int code, String type) {
		this.code = code;
		this.type = type;
	}
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
	
	
	
	
}
