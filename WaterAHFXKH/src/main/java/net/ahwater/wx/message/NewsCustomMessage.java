package net.ahwater.wx.message;

import java.util.List;

public class NewsCustomMessage extends CustomMessage{

	private int itemNum;
	private List<String> title;
	private List<String> description;
	private List<String> url;
	private List<String> picurl;
	public int getItemNum() {
		return itemNum;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
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
	public List<String> getUrl() {
		return url;
	}
	public void setUrl(List<String> url) {
		this.url = url;
	}
	public List<String> getPicurl() {
		return picurl;
	}
	public void setPicurl(List<String> picurl) {
		this.picurl = picurl;
	}
	
}
