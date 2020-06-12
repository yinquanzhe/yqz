package net.ahwater.utils;


import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageUtil {
	
	private static StringBuilder html = new StringBuilder();

    // 抓取新闻列表的正则
    private static String reg1 = "<li><a href=\"([/A-Za-z0-9]+\\.html)\"( target=\"_blank\" title=\"(([^\\x00-\\xff]|\\w|\\s)+)\")?>(([^\\x00-\\xff]|\\w|\\s|\\?)+)</a><span>\\[(([0-9\\-]+?))\\]</span>";
    private static String reg2 = "<li><a href=\"(/.+?)\">((.+?))</a>";

	private static StringBuilder getHtml(URL url) {
		String line;
		int responsecode;
		HttpURLConnection urlConnection;
		BufferedReader reader;
		StringBuilder sb = new StringBuilder();
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-type", "text/plain;charset=UTF-8");
			responsecode = urlConnection.getResponseCode();
			if (responsecode == 200) {
				reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
                }
			} else {
				System.err.println("获取不到网页的源码，服务器响应代码为：" + responsecode);
			}
		} catch (Exception e) {
			System.err.println("获取不到网页的源码,出现异常：" + e);
		}
		return sb;
	}
	
	public static String getListHtml(String baseUrl, URL url) {
		html = new StringBuilder();
		String line;
		int responsecode;
		HttpURLConnection urlConnection;
		BufferedReader reader;
		StringBuilder sb = new StringBuilder();
		boolean isStart = false, isEnd = false;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			responsecode = urlConnection.getResponseCode();
			if (responsecode == 200) {
				reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
				while ((line = reader.readLine()) != null) {
					html.append(line + "\n");
					if (line.contains("<div class=\"ym-gr is-border is-list-ri\">")) {
						isStart = true;
						sb.append(line.substring(line.indexOf("<div class=\"ym-gr is-border is-list-ri\">")) + "\n");
						continue;
					}
					if (line.contains("<div class=\"is-clear\">")) {
						isEnd = true;
						sb.append(line.substring(0, line.indexOf("<div class=\"is-clear\">")) + "\n");
						break;
					}
					if (isStart && !isEnd) {
						sb.append(line + "\n");
					}
				}
			} else {
				System.err.println("获取不到网页的源码，服务器响应代码为：" + responsecode);
			}
		} catch (Exception e) {
			System.err.println("获取不到网页的源码,出现异常：" + e);
		}
		String result = sb.toString();
		if (result.contains("/content/detail")) {
			result = result.replaceAll("\"/content/detail", "\""+ baseUrl + "detail?url=http://www.ahsl.gov.cn/content/detail").replaceAll("选择年份:", "");
			result = result.replaceAll("/index.php", baseUrl + "list?url=http://www.ahsl.gov.cn/index.php").replaceAll("&", "%26");
		}
		if (result.contains("/xxgkweb/detail")) {
			result = result.replaceAll("\"/xxgkweb/detail", "\""+ baseUrl + "detail?url=http://www.ahsl.gov.cn/xxgkweb/detail").replaceAll("选择年份:", "");
			result = result.replaceAll("/index.php", baseUrl + "list?url=http://www.ahsl.gov.cn/index.php").replaceAll("&", "%26");
		}
		if (result.contains("websites14")) {
			result = result.replaceAll("\"/websites14/detail", "\""+ baseUrl + "detail?url=http://www.ahsl.gov.cn/websites14/detail").replaceAll("选择年份:", "");
			result = result.replaceAll("/websites14/channel", baseUrl + "list?url=http://www.ahsl.gov.cn/websites14/channel");
		}
		return result;
	}

	/*public static List<News> getListInfo(String url, String reg, int titleIndex, int urlIndex, int timeIndex) throws Exception {
		List<News> list = new ArrayList<>();
		URL u = new URL(url);
		Pattern p = Pattern.compile(reg);
		String content = getHtml(u).toString();
//		System.err.println(content);
		Matcher m = p.matcher(content);
		while (m.find()) {
			if (m.groupCount() >= titleIndex && m.groupCount() >= urlIndex && m.groupCount() >= timeIndex) {
				News news = new News(m.group(titleIndex), "http://www.ahsl.gov.cn" + m.group(urlIndex), timeIndex == -1 ? null : m.group(timeIndex));
				list.add(news);
			}
//            for (int i = 0; i <= m.groupCount(); i++) {
//                System.out.println(i + " == " + m.group(i));
//            }
        }
		return list;
	}*/

	public static String getDetailHtml(URL url) {
		html = new StringBuilder();
		String start = "class=\"is-content-detail\" id=\"zoom\">";
		String end = "<div class=\"is-print\">";
		String line;
		int responsecode;
		HttpURLConnection urlConnection;
		BufferedReader reader;
		StringBuilder sb = new StringBuilder();
		boolean isStart = false, isEnd = false;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			responsecode = urlConnection.getResponseCode();
			if (responsecode == 200) {
				reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
				while ((line = reader.readLine()) != null) {
					html.append(line + "\n");
					if (line.contains(start)) {
						isStart = true;
						sb.append(line.substring(line.indexOf(start) + start.length()) + "\n");
						continue;
					}
					if (line.contains(end)) {
						isEnd = true;
						sb.append(line.substring(0, line.indexOf(end)) + "\n");
						break;
					}
					if (isStart && !isEnd) {
						sb.append(line + "\n");
					}
				}
			} else {
				System.err.println("获取不到网页的源码，服务器响应代码为：" + responsecode);
			}
		} catch (Exception e) {
			System.err.println("获取不到网页的源码,出现异常：" + e);
		}
		String result = sb.toString();
		if (result.contains("<div class=\"is-downlist\">")) {
			int index = result.indexOf("<div class=\"is-downlist\">");
			result = result.substring(0, index);
			result += "</div>";
		}
		return result;
	}
	
	public static String getTitle() {
		String title = "";
		String h = html.toString();
		if (h.contains("<title>")) {
			title = h.substring(h.indexOf("<title>") + 7, h.indexOf("</title>"));
		}
		return title;
	}
	
	public static void main(String[] args) throws Exception {
//		System.out.println(getListHtml("http://localhost:9000/WebPageCrawler", new URL(url3)).toString());
//		System.out.println(getDetailHtml(new URL("http://www.ahsl.gov.cn/websites14/detail/5374b8e4fd6f364c1600001d.html")));
	//	List<News> list = getListInfo("http://www.ahsl.gov.cn//index.php?c=opennessTarget&m=channel&partcode=57f9b311fd6f36e419000003", "<div\\sclass=\"is-tda\"><a\\shref=\"(.*?)\"\\s.*?>(.*?)</a>.*?>(\\d{4}-\\d{2}-\\d{2})</td>\\s?</tr>", 2,1,3);
		//List<News> list2 = getListInfo(IndexController.url5, reg2, 2,1,-1);
		//list.stream().filter(e -> e.getTime() != null && e.getTime() != "").forEach(System.out::println);
		//list.forEach(System.out::println);
//        getDetailHtml(new URL("http://www.ahsl.gov.cn/websites14/detail/594df1c1fd6f364811000000.html"));
//        HtmlCleaner cleaner = new HtmlCleaner();
//        TagNode node = cleaner.clean(new URL("http://www.ahsl.gov.cn/index.php?c=mobile&m=xxgkwebdetail&_id=55da6458fd6f36940b000000"));
//        //按tag取.
//        Object[] ns = node.getElementsByName("title", true);    //标题
//        if(ns.length > 0) {
//            System.out.println("title="+((TagNode)ns[0]).getText());
//        }
//        String content = "";
//        //按xpath取
//        Object[] ns1 = node.evaluateXPath("//div[@class='content']/div");
//        if (ns1 != null) {
//            System.out.println("ns1=====" + ns1.length);
//            for(int i = 0; i < ns1.length; i++) {
//                TagNode n = (TagNode) ns1[i];
//                if (n.getAttributeByName("class").trim().equals("content_title")) {
//                    content += "<h4>" + n.getText().toString().trim() + "</h4>";
//                }
//            }
//        }
//
//        Object[] ns2 = node.evaluateXPath("//div[@class='content_body']//p");
//        if (ns2 != null) {
//            System.out.println("ns2=====" + ns2.length);
//            for(int i = 0; i < ns2.length; i++) {
//                TagNode n = (TagNode) ns2[i];
//                content += "<p>" + n.getText().toString().trim() + "</p>";
//            }
//        }
//        System.out.println("content====" + content);
	}

}
