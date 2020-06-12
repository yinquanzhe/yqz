package net.ahwater.wx.message;
/**
 * 信息公共字段
 * @author Administrator
 *
 */
public class Message {

	private String toUserName;//接收方账号
	private String fromUserName;//开发者微信号
	private String createTime;//消息创建时间（整型）
	private String msgType;//消息类型
	
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	
	
}
