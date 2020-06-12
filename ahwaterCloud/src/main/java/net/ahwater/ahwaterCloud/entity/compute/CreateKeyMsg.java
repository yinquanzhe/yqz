package net.ahwater.ahwaterCloud.entity.compute;

public class CreateKeyMsg {
	String errorMsg;
	String privateKey;
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	
}
