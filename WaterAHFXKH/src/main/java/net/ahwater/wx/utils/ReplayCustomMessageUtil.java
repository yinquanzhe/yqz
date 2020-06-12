package net.ahwater.wx.utils;

import net.ahwater.wx.message.NewsCustomMessage;
import net.ahwater.wx.message.TextCustomMessage;

public class ReplayCustomMessageUtil {

	/*{
	    "touser":"OPENID",
	    "msgtype":"text",
	    "text":
	    {
	         "content":"Hello World"
	    }
	}*/
	/**
	 * 客服回复文字消息
	 */
	public static String repalyTextMessage(TextCustomMessage tcm){
		StringBuffer sb = new StringBuffer("");
		sb.append("{");
		sb.append("\"touser\":\""+tcm.getTouser()+"\",");
		sb.append("\"msgtype\":\"text\",");
		sb.append("\"text\":{");
		sb.append("				\"content\":\""+tcm.getContent()+"\"");
		sb.append("		}");
		sb.append("}");
		return sb.toString();
	}
	
	/*
	{
	    "touser":"OPENID",
	    "msgtype":"news",
	    "news":{
	        "articles": [
	         {
	             "title":"Happy Day",
	             "description":"Is Really A Happy Day",
	             "url":"URL",
	             "picurl":"PIC_URL"
	         },
	         {
	             "title":"Happy Day",
	             "description":"Is Really A Happy Day",
	             "url":"URL",
	             "picurl":"PIC_URL"
	         }
	         ]
	    }
	}*/
	/**
	 * 客服回复图文消息
	 */
	public static String repalyNewsMessage(NewsCustomMessage ncm){
		StringBuffer sb = new StringBuffer("");
		sb.append("{");
		sb.append("\"touser\":\""+ncm.getTouser()+"\",");
		sb.append("\"msgtype\":\"news\",");
		sb.append("\"news\":{");
		sb.append("\"articles\":[");
		for(int i=0 ; i<(ncm.getItemNum()>8?8:ncm.getItemNum());i++){
			sb.append("{");
			sb.append("\"title\":\""+ncm.getTitle().get(i)+"\",");
			sb.append("\"description\":\""+ncm.getDescription().get(i)+"\",");
			sb.append("\"url\":\""+ncm.getUrl().get(i)+"\",");
			sb.append("\"picurl\":\""+ncm.getPicurl().get(i)+"\"");
			if(i==ncm.getItemNum()-1){
				sb.append("}");//不加逗号
			}else{
				sb.append("},");//加逗号
			}
		}
		sb.append("]}");
		sb.append("}");

		return sb.toString();
	}
	
}
