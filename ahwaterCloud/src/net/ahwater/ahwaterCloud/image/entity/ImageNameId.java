package net.ahwater.ahwaterCloud.image.entity;
/**
 * 用于返回用户的名字和id
 * @author zdf
 *
 */
public class ImageNameId {
	private String imageName;
	private String imageId;
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
}
