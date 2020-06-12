package net.ahwater.wx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 微信消息群发
 * 
 * @author Administrator
 *
 */
public class WxSubscribeMassUtil {

	private static Logger log = LoggerFactory.getLogger(WxSubscribeUtil.class);
	
	/**
	 * 根据标签进行群发【订阅号与服务号认证后均可用】
	 *  http请求方式: POST
	 *  https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN
	 * POST数据示例【群发文本】
	 * {
		   "filter":{
		      "is_to_all":false,
		      "tag_id":2
		   },
		   "text":{
		      "content":"CONTENT"
		   },
		    "msgtype":"text"
	 * }
	 * POST数据示例【群发图文消息】
	 *  {
		   "filter":{
		      "is_to_all":false,
		      "tag_id":2
		   },
		   "mpnews":{
		      "media_id":"123dsdajkasd231jhksad"
		   },
		    "msgtype":"mpnews",
		    "send_ignore_reprint":0
	 *	}
	 *
	 *返回数据示例（正确时的JSON返回结果）
	 * {
		   "errcode":0,
		   "errmsg":"send job submission success",
		   "msg_id":34182, 
		   "msg_data_id": 206227730
	 *}
	 * 当请求错误时将返回空null
	 * @param jsonMessag
	 */
	public static String sendAllMessage(String jsonMessag) {
		String access_token = WxSubscribeUtil.getServerAccessToken();
		HttpURLConnection connection = null;
		try {
			URL url = new URL("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + access_token);// 获取accessToken地址的url
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(jsonMessag.getBytes("UTF-8"));
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine();
			in.close();
			connection.disconnect();
			return line;
		} catch (IOException e) {
			if (connection != null) {
				connection.disconnect();
			}
			log.info("sendAll text fail!" + e.getMessage());
		}
		return null;
	}

	/**
	 * 删除群发消息【订阅号与服务号认证后均可用】
	 * 群发之后，随时可以通过该接口删除群发。
	 * 删除群发消息只能删除图文消息和视频消息，其他类型的消息一经发送，无法删除。
	 * http请求方式: POST
	 * https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=ACCESS_TOKEN
	 * POST数据示例
	 * {
		   "msg_id":30124,
		   "article_idx":2
	 *}
	 *返回数据示例（正确时的JSON返回结果）
	 *{
		   "errcode":0,
		   "errmsg":"ok"
	 *}
	 *当请求错误时将返回空null
	 *@param jsonData
	 */
	public static String deleteMessage(String jsonData) {
		String access_token = WxSubscribeUtil.getServerAccessToken();
		HttpURLConnection connection = null;
		try {
			URL url = new URL("https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=" + access_token);// 获取accessToken地址的url
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(jsonData.getBytes("UTF-8"));
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine();
			in.close();
			connection.disconnect();
			return line;
		} catch (IOException e) {
			if (connection != null) {
				connection.disconnect();
			}
			log.info("deleteMessage fail!" + e.getMessage());
		}
		return "";
	}
	
	/**
	 * 群发消息预览接口【订阅号与服务号认证后均可用】
	 * 每日调用次数有限制（100次）
	 * http请求方式: POST
	 * https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=ACCESS_TOKEN
	 * 
	 * POST数据示例【文本消息】
	 * {     
	    "touser":"OPENID",
	    "text":{           
	           "content":"CONTENT"            
	           },     
	    "msgtype":"text"
		}
	 *
	 *POST数据示例【图文消息】
	 *{
		   "touser":"OPENID", 
		   "mpnews":{              
		            "media_id":"123dsdajkasd231jhksad"               
		             },
		   "msgtype":"mpnews" 
	 *}
	 *
	 *返回数据示例（正确时的JSON返回结果）
	 *{
		   "errcode":0,
		   "errmsg":"preview success",
		   "msg_id":34182
		}
	 *当请求错误时将返回空null
	 *@param jsonData
	 */
	public static String previewMessage(String jsonData) {
		String access_token = WxSubscribeUtil.getServerAccessToken();
		HttpURLConnection connection = null;
		try {
			URL url = new URL("https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=" + access_token);// 获取accessToken地址的url
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(jsonData.getBytes("UTF-8"));
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine();
			in.close();
			connection.disconnect();
			return line;
		} catch (IOException e) {
			if (connection != null) {
				connection.disconnect();
			}
			log.info("previewMessage fail!" + e.getMessage());
		}
		return null;
	}
	 
	/**
	 * 查询群发消息发送状态【订阅号与服务号认证后均可用】
	 * http请求方式: POST
	 * https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=ACCESS_TOKEN
	 * 
	 * POST数据示例
	 * {
		   "msg_id": "201053012"
		}
	 *
	 *
	 *返回数据示例（正确时的JSON返回结果）
	 *{
	     "msg_id":201053012,
	     "msg_status":"SEND_SUCCESS"
	 }
	 *当请求错误时将返回空null
	 *
	 *@param jsonData
	 */
	public static String getSendMessageState(String jsonData) {
		String access_token = WxSubscribeUtil.getServerAccessToken();
		HttpURLConnection connection = null;
		try {
			URL url = new URL("https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=" + access_token);// 获取accessToken地址的url
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(jsonData.getBytes("UTF-8"));
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = in.readLine();
			in.close();
			connection.disconnect();
			return line;
		} catch (IOException e) {
			if (connection != null) {
				connection.disconnect();
			}
			log.info("getSendMessageState fail!" + e.getMessage());
		}
		return null;
	}

	/**
	 * 文本消息格式字符串
	 * 
	 * @param content
	 * @return
	 */
	public static String parseTextMessage(String content) {
		/*
		 * { "filter":{ "is_to_all":false "group_id":"2" }, "text":{
		 * "content":"CONTENT" }, "msgtype":"text" }
		 */
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"filter\":{");
		sb.append("\"is_to_all\":true");
		sb.append("},");
		sb.append("\"text\":{");
		sb.append("\"content\":\"" + content + "\"");
		sb.append("},");
		sb.append("\"msgtype\":\"text\"");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 图文消息格式字符串
	 * 
	 * @param media_id
	 * @return
	 */
	public static String parseNewsMessage(String media_id) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"filter\":{");
		sb.append("\"is_to_all\":true");
		sb.append("},");
		sb.append("\"mpnews\":{");
		sb.append("\"media_id\":\"" + media_id + "\"");
		sb.append("},");
		sb.append("\"msgtype\":\"mpnews\"");
		sb.append("}");
		return sb.toString();
	}


	public static String previewNewsMsgStr(String open_id, String media_id) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("{\"touser\":\"");
	    sb.append(open_id);
	    sb.append("\",\"mpnews\":{\"media_id\":\"");
	    sb.append(media_id);
	    sb.append("\"},\"msgtype\":\"mpnews\"}");
	    log.info(sb.toString());
	    return sb.toString();
    }

    /*
    * {
    "touser":"OPENID",
    "text":{
           "content":"CONTENT"
           },
    "msgtype":"text"
    } */
    public static String previewTxtMsgStr(String open_id, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"touser\":\"");
        sb.append(open_id);
        sb.append("\",\"text\":{\"content\":\"");
        sb.append(content);
        sb.append("\"},\"msgtype\":\"text\"}");
        log.info(sb.toString());
        return sb.toString();
    }

}
