package net.ahwater.wx.message;


/**
 * 文本消息
 * @author Administrator
 *
 */
public class TextMessage extends Message{

	/*<xml>
	<ToUserName><![CDATA[toUser]]></ToUserName>
	<FromUserName><![CDATA[fromUser]]></FromUserName>
	<CreateTime>12345678</CreateTime>
	<MsgType><![CDATA[text]]></MsgType>
	<Content><![CDATA[你好]]></Content>
	</xml>*/
	private String content;//文本消息内容

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
