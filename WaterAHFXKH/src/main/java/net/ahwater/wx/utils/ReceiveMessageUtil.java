package net.ahwater.wx.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * xml转换成map工具类
 * 
 * @author Administrator
 *
 */
public class ReceiveMessageUtil {
	
	/**
	 * 
	 * @Description 将用户的xml消息提取成map key value 类型
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		InputStream in = request.getInputStream();
		SAXReader reader = new SAXReader();
		Document document = reader.read(in);
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element element : elementList) {
			map.put(element.getName(), element.getText());
		}
		in.close();
		return map;
	}

	/**
	 * 返回相应消息
	 * @return
	 */
	public static String parseMessage(Map<String, String> xmls,List<Map<String,Object>> newsList) {
		return "";
	}

}
