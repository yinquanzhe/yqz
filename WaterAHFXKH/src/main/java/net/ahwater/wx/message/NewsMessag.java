package net.ahwater.wx.message;

import java.util.ArrayList;
import java.util.List;

public class NewsMessag extends Message{
/*
	<xml>
	<ToUserName><![CDATA[toUser]]></ToUserName>
	<FromUserName><![CDATA[fromUser]]></FromUserName>
	<CreateTime>12345678</CreateTime>
	<MsgType><![CDATA[news]]></MsgType>
	<ArticleCount>2</ArticleCount>
	<Articles>
	<item>
	<Title><![CDATA[title1]]></Title> 
	<Description><![CDATA[description1]]></Description>
	<PicUrl><![CDATA[picurl]]></PicUrl>
	<Url><![CDATA[url]]></Url>
	</item>
	<item>
	<Title><![CDATA[title]]></Title>
	<Description><![CDATA[description]]></Description>
	<PicUrl><![CDATA[picurl]]></PicUrl>
	<Url><![CDATA[url]]></Url>
	</item>
	</Articles>
	</xml> */	
	private String articleCount;//图文消息个数，限制为10条以内
	private String articles;//	多条图文消息信息，默认第一个item为大图,注意，如果图文数超过10，则将会无响应
	private List<String> title = new ArrayList<>();//图文消息标题：可无
	private List<String> description = new ArrayList<>();//	图文消息描述：可无
	private List<String> picUrl =new ArrayList<>();//	图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200：可无
	private List<String> url = new ArrayList<>();//	点击图文消息跳转链接：可无
	
	public String getArticleCount() {
		return articleCount;
	}
	public void setArticleCount(String articleCount) {
		this.articleCount = articleCount;
	}
	public String getArticles() {
		return articles;
	}
	public void setArticles(String articles) {
		this.articles = articles;
	}
	public List<String> getTitle() {
		return title;
	}
	public void setTitle(List<String> title) {
		this.title = title;
	}
	public List<String> getDescription() {
		return description;
	}
	public void setDescription(List<String> description) {
		this.description = description;
	}
	public List<String> getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(List<String> picUrl) {
		this.picUrl = picUrl;
	}
	public List<String> getUrl() {
		return url;
	}
	public void setUrl(List<String> url) {
		this.url = url;
	}
	
	
}
