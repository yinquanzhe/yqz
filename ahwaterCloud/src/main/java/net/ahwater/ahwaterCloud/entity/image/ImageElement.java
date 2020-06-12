package net.ahwater.ahwaterCloud.entity.image;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.image.DiskFormat;
import org.openstack4j.model.image.Image.Status;
import org.openstack4j.model.image.StoreType;
/**
 * 用于列表显示镜像的属性
 * @author zdf
 *
 */
public class ImageElement {
	private String imageName;  //镜像名字
	private String imagePersonalName;  //镜像的个性名字
	private String imageOStype;  //镜像的类型
	private String imageOSVersion;  //镜像的版本
	private String imageOSBit;  //镜像的位数
	//private StoreType imageType;
	private String imageType;  //镜像的类型
	private Status imageStatus;  //镜像的状态
//	private boolean imageisPublic;
	private String imageisPublic;  //镜像是非为公共的
//	private boolean imageisProtected;
	private String imageisProtected;  //镜像是非为受保护
	private DiskFormat imageFormat;  //镜像的格式
	//private long imageSize;
	private String imageSize;  //镜像的大小
	private String imageId;  //镜像的Id
	private String imageOwner;  //镜像的租户
	private String imageOwnerName;  //镜像的租户的名字
	
	
	
	public String getImageOwnerName() {
		return imageOwnerName;
	}
	public void setImageOwnerName(OSClientV2 os, String tenantId) {
		this.imageOwnerName = os.identity().tenants().get(tenantId).getName();
	}
	public String getImageOwner() {
		return imageOwner;
	}
	public void setImageOwner(String imageOwner) {
		this.imageOwner = imageOwner;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getImageName() {
		return imageName;
	}
	
	public String getImagePersonalName(){
		return imagePersonalName;
	}
	
	public String getImageOStype(){
		return imageOStype;
	}
	
	public String getImageOSVersion(){
		return imageOSVersion;
	}
	
	public String getImageOSBit(){
		return imageOSBit;
	}
	
	public void setImageName(String imageName) {
		this.imageName = imageName;
		String[] name = imageName.split("#");
		this.imagePersonalName = name[0];
		if(name.length >= 2){
			this.imageOStype = name[1];
		}else{
			this.imageOStype = "无";
		}
		
		if(name.length >= 3){
			this.imageOSVersion = name[2];
		}
		else{
			this.imageOSVersion = "无";
		}
		
		if(name.length>=4){
			this.imageOSBit = name[3];
		}
	}
	
//	public StoreType getImageType() {
//		return imageType;
//	}
//	public void setImageType(StoreType imageType) {
//		this.imageType = imageType;
//	}
	
	public String getImageType() {
		return imageType;
	}
	public void setImageType(boolean imageType) {
		if(imageType == true){
			this.imageType = "快照";
		}
		else{
			this.imageType = "镜像";
		}
		//this.imageType = imageType;
	}
	
	public Status getImageStatus() {
		return imageStatus;
	}
	public void setImageStatus(Status status) {
		this.imageStatus = status;
	}
//	
//	public boolean isImageisPublic() {
//		return imageisPublic;
//	}
//	public void setImageisPublic(boolean imageisPublic) {
//		this.imageisPublic = imageisPublic;
//	}

	
	public String getImageisPublic() {
		return imageisPublic;
	}
	public void setImageisPublic(boolean imageisPublic) {
		if(imageisPublic == true){
			this.imageisPublic = "是";
		}else{
			this.imageisPublic = "否";
		}
	}
	
//	public String isImageisPublic() {
//		return imageisPublic;
//	}
//	public void setImageisPublic(boolean imageisPublic) {
//		if(imageisPublic){
//			this.imageisPublic = "y";
//		}
//		else{
//			this.imageisPublic = "n";
//		}
//		
//	}
	
//	public boolean isImageisProtected() {
//		return imageisProtected;
//	}
//	public void setImageisProtected(boolean imageisProtected) {
//		this.imageisProtected = imageisProtected;
//	}
	
	public String getImageisProtected() {
		return imageisProtected;
	}
	public void setImageisProtected(boolean imageisProtected) {
		if(imageisProtected){
			this.imageisProtected = "是";
			//System.out.println("this.imageisProtected="+this.imageisProtected);
		}
		else{
			this.imageisProtected = "否";
			//System.out.println("this.imageisProtected="+this.imageisProtected);
		}
	}
	
	public DiskFormat getImageFormat() {
		return imageFormat;
	}
	public void setImageFormat(DiskFormat imageFormat) {
		this.imageFormat = imageFormat;
	}
//	public long getImageSize() {
//		return imageSize;
//	}
//	public void setImageSize(long imageSize) {
//		this.imageSize = imageSize;
//	}
	
	public String getImageSize() {
		return imageSize;
	}
	public void setImageSize(long imageSize) {
		if(imageSize<1048576){     //不足1MB的，单位用KB
			double size = (double)imageSize/(1024); //(double)imageSize/(1*1024)
			size = (double)(Math.round(size*10))/10;
			this.imageSize = String.valueOf(size);
			this.imageSize = this.imageSize+" KB";
		}
		else if(imageSize>=1048576 && imageSize<1073741824){  //超过1M但不足1G的用MB表示
			double size = (double)imageSize/(1048576); //(double)imageSize/(2*1024)
			size = (double)(Math.round(size*10))/10;
			this.imageSize = String.valueOf(size);
			this.imageSize = this.imageSize+" MB";
		}
		else if(imageSize >= 1073741824){     //超过1G的用GB表示
			double size = (double)imageSize/(1073741824); //(double)imageSize/(3*1024)
			size = (double)(Math.round(size*10))/10;
			this.imageSize = String.valueOf(size);
			this.imageSize = this.imageSize+" GB";
		}
		
	}
}
