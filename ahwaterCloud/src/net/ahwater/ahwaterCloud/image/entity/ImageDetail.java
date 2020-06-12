package net.ahwater.ahwaterCloud.image.entity;

import java.util.Date;

import org.openstack4j.model.image.ContainerFormat;
/**
 * 用于显示镜像的详细信息
 * 
 * @author zdf
 *
 */
public class ImageDetail extends ImageElement{
	private String imageCheckSum;     //校验和
	private String imageCreatedDate;  //创建时间
	private String imageUpdateDate;   //更新时间
	private ContainerFormat imageContainerFormat;  //容器格式
	public String getImageCheckSum() {
		return imageCheckSum;
	}
	public void setImageCheckSum(String imageCheckSum) {
		this.imageCheckSum = imageCheckSum;
	}
	public String getImageCreatedDate() {
		return imageCreatedDate;
	}
	public void setImageCreatedDate(Date date) {
//		this.imageCreatedDate = imageCreatedDate;
	}
	public String getImageUpdateDate() {
		return imageUpdateDate;
	}
	public void setImageUpdateDate(Date date) {
//		this.imageUpdateDate = imageUpdateDate;
	}
	public ContainerFormat getImageContainerFormat() {
		return imageContainerFormat;
	}
	public void setImageContainerFormat(ContainerFormat imageContainerFormat) {
		this.imageContainerFormat = imageContainerFormat;
	}
	
	
}
