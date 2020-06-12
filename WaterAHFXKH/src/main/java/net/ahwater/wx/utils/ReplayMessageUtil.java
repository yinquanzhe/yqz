package net.ahwater.wx.utils;

import net.ahwater.wx.message.NewsMessag;
import net.ahwater.wx.message.TextMessage;

import java.io.Serializable;

/**
 * @ClassName ReplayMessageUtil
 * @Description 构建回复消息
 * @version V1.0
 */
public class ReplayMessageUtil implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * <xml> <ToUserName><![CDATA[toUser]]></ToUserName>
	 * <FromUserName><![CDATA[fromUser]]></FromUserName>
	 * <CreateTime>12345678</CreateTime> <MsgType><![CDATA[news]]></MsgType>
	 * <ArticleCount>2</ArticleCount>
	 * <Articles> <item> <Title><![CDATA[title1]]></Title>
	 * <Description><![CDATA[description1]]></Description>
	 * <PicUrl><![CDATA[picurl]]></PicUrl> <Url><![CDATA[url]]></Url> </item>
	 * <item> <Title><![CDATA[title]]></Title>
	 * <Description><![CDATA[description]]></Description>
	 * <PicUrl><![CDATA[picurl]]></PicUrl>
	 * <Url><![CDATA[url]]></Url> </item> </Articles> </xml>
	 * 
	 * @Title sendImageTextMessage
	 * @Description 回复图文消息
	 * @param message
	 * @return
	 */
	public static String sendNewsMessag(NewsMessag message) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<ToUserName><![CDATA[" + message.getToUserName() + "]]></ToUserName>");
		sb.append("<FromUserName><![CDATA[" + message.getFromUserName() + "]]></FromUserName>");
		sb.append("<CreateTime>" + message.getCreateTime() + "</CreateTime>");
		sb.append("<MsgType><![CDATA[news]]></MsgType>");
		sb.append("<ArticleCount>" + message.getArticleCount() + "</ArticleCount>");
		sb.append("<Articles> ");
		int len = Integer.parseInt(message.getArticleCount());
		for (int i = 0; i < len; i++) {
			sb.append("<item>");
			if (message.getTitle().size() == len && message.getTitle().get(i) != null
					&& !"".equals(message.getTitle().get(i))) {
				sb.append("<Title><![CDATA[" + message.getTitle().get(i) + "]]></Title>");
			}
			if (message.getDescription().size() == len && message.getDescription().get(i) != null
					&& !"".equals(message.getDescription().get(i))) {
				sb.append("<Description><![CDATA[" + message.getDescription().get(i) + "]]></Description>");
			}
			if (message.getPicUrl().size() == len && message.getPicUrl().get(i) != null
					&& !"".equals(message.getPicUrl().get(i))) {
				sb.append("<PicUrl><![CDATA[" + message.getPicUrl().get(i) + "]]></PicUrl>");
			}
			if (message.getUrl().size() == len && message.getUrl().get(i) != null
					&& !"".equals(message.getUrl().get(i))) {
				sb.append("<Url><![CDATA[" + message.getUrl().get(i) + "]]></Url>");
			}
			sb.append("</item>");
		}

		sb.append("</Articles>");
		sb.append("</xml>");
		return sb.toString();
	}

	/**
	 * <xml> <ToUserName><![CDATA[toUser]]></ToUserName>
	 * <FromUserName><![CDATA[fromUser]]></FromUserName>
	 * <CreateTime>12345678</CreateTime> <MsgType><![CDATA[text]]></MsgType>
	 * <Content><![CDATA[你好]]></Content> </xml>
	 * 
	 * @Title sendTextMessage
	 * @Description 回复文本消息
	 * @param message
	 * @return
	 */
	public static String sendTextMessage(TextMessage message) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<ToUserName><![CDATA[" + message.getToUserName() + "]]></ToUserName>");
		sb.append("<FromUserName><![CDATA[" + message.getFromUserName() + "]]></FromUserName>");
		sb.append("<CreateTime>" + message.getCreateTime() + "</CreateTime>");
		sb.append("<MsgType><![CDATA[text]]></MsgType>");
		sb.append("<Content><![CDATA[" + message.getContent() + "]]></Content>");
		sb.append("</xml>");
		return sb.toString();
	}

}
