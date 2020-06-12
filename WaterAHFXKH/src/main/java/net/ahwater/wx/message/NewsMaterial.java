package net.ahwater.wx.message;

//图文素材
public class NewsMaterial {
//	{
//	   "articles": [
//			 {
//	                        "thumb_media_id":"qI6_Ze_6PtV7svjolgs-rN6stStuHIjs9_DidOHaj0Q-mwvBelOXCFZiq2OsIU-p",
//	                        "author":"xxx",
//				 "title":"Happy Day",
//				 "content_source_url":"www.qq.com",
//				 "content":"content",
//				 "digest":"digest",
//	                        "show_cover_pic":"1"
//			 },
//			 {
//	                        "thumb_media_id":"qI6_Ze_6PtV7svjolgs-rN6stStuHIjs9_DidOHaj0Q-mwvBelOXCFZiq2OsIU-p",
//	                        "author":"xxx",
//				 "title":"Happy Day",
//				 "content_source_url":"www.qq.com",
//				 "content":"content",
//				 "digest":"digest",
//	                        "show_cover_pic":"0"
//			 }
//	   ]
//	}
	private String thumb_media_id;
	private String author;
	private String title;
	private String content_source_url;
	private String content;
	private String digest;
	private int show_cover_pic;
	
	public String getThumb_media_id() {
		return thumb_media_id;
	}
	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent_source_url() {
		return content_source_url;
	}
	public void setContent_source_url(String content_source_url) {
		this.content_source_url = content_source_url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public int getShow_cover_pic() {
		return show_cover_pic;
	}
	public void setShow_cover_pic(int show_cover_pic) {
		this.show_cover_pic = show_cover_pic;
	}
}
