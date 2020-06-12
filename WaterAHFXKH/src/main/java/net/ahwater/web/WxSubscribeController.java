package net.ahwater.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ahwater.config.WxConfig;
import net.ahwater.wx.message.NewsMessag;
import net.ahwater.wx.message.TextMessage;
import net.ahwater.wx.utils.ReceiveMessageUtil;
import net.ahwater.wx.utils.ReplayMessageUtil;
import net.ahwater.wx.utils.SignUtil;
import net.ahwater.wx.utils.WxSubscribeUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 订阅号
 * @author Administrator
 *
 */
@Controller
public class WxSubscribeController {

	private Logger log = LoggerFactory.getLogger(WxSubscribeController.class);
	/**
	 * 微信验证
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/accessWx", method = RequestMethod.GET)
	@ResponseBody
	public String accessWx(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
			@RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) throws Exception {
		List<String> sList = new ArrayList<String>();// 将要排序加密的数据放入集合
		sList.add(WxConfig.TOKEN);
		sList.add(timestamp);
		sList.add(nonce);

		Collections.sort(sList, new SpellComparator());// 将要sha1加密比对的数据 进行汉字拼音排序
		String wxstr = sList.get(0) + sList.get(1) + sList.get(2);// 排序后的数据

		if (DigestUtils.sha1Hex(wxstr).equals(signature.trim())) {
			log.info("<!----------微信接入服务器成功" + echostr + "----------!>");
			return echostr.trim();
		} else {
			log.info("<!----------微信接入服务器失败" + echostr + "----------!>");
			return "";
		}
	}

	/**
	 * 微信接收
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/accessWx", method = RequestMethod.POST, produces = "application/xml; charset=utf-8")
	@ResponseBody
	public String requestRrocessing(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8"); 
		String sendMessage = "";
		Map<String, String> xmls = ReceiveMessageUtil.parseXml(request, response);
		if (xmls.get("MsgType").equalsIgnoreCase("text")) {
            log.info(xmls.toString());
		} else if (xmls.get("MsgType").equalsIgnoreCase("event")) {
			if (xmls.get("Event").equalsIgnoreCase("subscribe")) {
				// 文本消息
				TextMessage msg = new TextMessage();
				msg.setCreateTime(xmls.get("CreateTime"));
				msg.setFromUserName(xmls.get("ToUserName"));
				msg.setToUserName(xmls.get("FromUserName"));
				msg.setContent("欢迎关注安徽防办公众号！/::D");
				sendMessage = ReplayMessageUtil.sendTextMessage(msg);
			} else if (xmls.get("Event").equalsIgnoreCase("click")) {
				if (xmls.get("EventKey").equalsIgnoreCase("flood")) {
					NewsMessag msg = new NewsMessag();
					List<String> titles = new ArrayList<>();
					List<String> picUrls = new ArrayList<>();
					List<String> urls = new ArrayList<>();
					titles.add("");
					titles.add("");
					titles.add("");
					titles.add("");

					urls.add(WxConfig.SITE_ROOT + "rsituation/index?tag=xq");
					urls.add(WxConfig.SITE_ROOT + "rsituation/index?tag=yq");
					urls.add(WxConfig.SITE_ROOT + "rsituation/index?tag=hd");
					urls.add(WxConfig.SITE_ROOT + "rsituation/index?tag=sk");

					Random rd = new Random();
					int num = rd.nextInt(11);
					picUrls.add(WxConfig.SITE_ROOT + "img/news/img" + num + ".png");
//					picUrls.add(WxConfig.SITE_ROOT + "img/news/yq.png");
//					picUrls.add(WxConfig.SITE_ROOT + "img/news/hd.png");
//					picUrls.add(WxConfig.SITE_ROOT + "img/news/sk.png");
					picUrls.add("");
					picUrls.add("");
					picUrls.add("");

					msg.setArticleCount(titles.size() + "");
					msg.setCreateTime(xmls.get("CreateTime"));
					msg.getDescription();
					msg.setFromUserName(xmls.get("ToUserName"));
					msg.setToUserName(xmls.get("FromUserName"));
					msg.setTitle(titles);
					msg.setPicUrl(picUrls);
					msg.setUrl(urls);
					sendMessage = ReplayMessageUtil.sendNewsMessag(msg);
				}
			}
		}
		return sendMessage; // 不做回应
	}

	/**
	 * 获取jsapi验证签名
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sna", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> signature(HttpServletRequest request, @RequestParam("url") String url) {
		Map<String, String> signatureInfo = new HashMap<String, String>();
		String jsapi_ticket = WxSubscribeUtil.getServerJsApiTicket();
		System.out.println(jsapi_ticket);
		signatureInfo = SignUtil.getSign(jsapi_ticket, url);
		signatureInfo.put("appid", WxConfig.APPID);
		log.info("验证签名>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>：" + signatureInfo.toString());
		return signatureInfo;
	}

    /**
     * 逆解析得出位置
     * @param lng
     * @param lat
     * @return
     */
    @RequestMapping(value = "/getLocation", method = RequestMethod.POST)
    @ResponseBody
    public String getLocation(@RequestParam String lng, @RequestParam String lat) {
	    StringBuilder sb = new StringBuilder();
	    String res  = WxSubscribeUtil.geocode(lng, lat);
	    if (res != null && !res.trim().equals("")) {
            res = res.substring(res.indexOf("(") + 1, res.lastIndexOf(")"));
	        log.info("逆解析结果: " + res);
            Map<String, Object> map = (Map<String, Object>) JSON.parse(res);
            if (map != null && map.get("result") != null) {
                Map m = (Map) JSON.parse(map.get("result").toString());
                if (m != null && m.get("formatted_address") != null) {
                    return m.get("formatted_address").toString();
                }
            }
        }
        return "地址解析错误";
    }

	/**
	 * 汉字拼音排序比较器
	 * 
	 * @author Administrator
	 */
	class SpellComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			try {
				String s1 = new String(o1.toString().getBytes("GB2312"), "ISO-8859-1");
				String s2 = new String(o2.toString().getBytes("GB2312"), "ISO-8859-1");
				return s1.compareTo(s2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}

}
