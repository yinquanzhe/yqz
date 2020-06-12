package net.ahwater.ahwaterCloud.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.Payload;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.image.ContainerFormat;
import org.openstack4j.model.image.DiskFormat;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.image.Image.Status;
import org.openstack4j.model.image.StoreType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.identityV3.HttpPatch;
import net.ahwater.ahwaterCloud.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.image.entity.ImageName;

/**
 * 镜像的相关操作
 * @author zdf
 *
 */

public class ImageOS {
	private Image image;
	public ImageOS(){
	}
	
	public ImageOS(Image image){
		this.image = image;
	}
	
	
	/**
	 * 创建一个Image
	 * @param os
	 * @return
	 * @throws MalformedURLException
	 */
	public Image createImage(OSClientV2 os) throws MalformedURLException{
		Payload<URL> payload = Payloads.create(new URL("https://some/url/cirros-0.3.0-x86_64-disk.img"));
		Image image = os.images().create((Builders.image()
                .name("Cirros 0.3.0 x64")
                .isPublic(true)
                .containerFormat(ContainerFormat.BARE)
                .diskFormat(DiskFormat.QCOW2)
                .build()
                ), payload);
		
//		image = os.imagesV2().create(
//				Builders.imageV2()
//				        .name(" ")
//				        .containerFormat(ContainerFormat.BARE)
//				        .visibility(Image.ImageVisibility.PUBLIC)
//				        .diskFormat(DiskFormat.QCOW2)
//				        .minDisk(0)
//				        .minRam(0)
//				        .build()
//				);
		return image;
	
	}
	
	
	public String getImageName(){
		return image.getName();
	}
	
	public StoreType getImageType() {
		return image.getStoreType();   //ImageV1里的函数， ImageV2里没有这个函数

		//return image.
	}
	
	public Status getImageStatus() {
		return image.getStatus();
	}
	
	public boolean isImageisPublic() {
		return image.isPublic();
	}
	
	public boolean isImageisProtected() {
		return image.isProtected();
	}
	
	public org.openstack4j.model.image.DiskFormat getImageFormat() {
		return image.getDiskFormat();
	}
	
	public Long getImageSize() {
		return image.getSize();
	}
	
	public String getImageId(){
		
		return image.getId();
	}
	
	public boolean getIsSnapshot(){
		return image.isSnapshot();
	}
	
	//上传镜像数据
	public void uploadImage(OSClientV2 os, String imageUrl){
		Payload<URL> payload = null;
		try {
			payload = Payloads.create(new URL(imageUrl));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String imageId = image.getId();
		os.images().upload(imageId, payload, image);
		//os.imagesV2().upload(imageId, payload, image);
	}
	
	
	/**
	 * 列出所有的Image
	 * @param os
	 * @return
	 */
	public static List<? extends Image> queryAllImages(OSClientV2 os){
		//List<? extends Image> images = os.imagesV2().list();
		List<? extends Image> images = os.images().list();
		return images;
	}
	
	/*
	 *列出租户拥有的image
	 */
	public static List<? extends Image> queryOwnedImage(OSClientV2 os, String tenantId){
		List<? extends Image> images = os.images().list();
		List<Image> ownedImages = new ArrayList<>();
		for(Image image:images){
			if(image.getId().equals(tenantId)){
				ownedImages.add(image);
			}
		}
		return ownedImages;
	}
	
	
	/**
	 * 列出公有的image
	 * @param os
	 * @return
	 */
	public static List<? extends Image> queryPublicImage(OSClientV2 os){
		List<? extends Image> images = os.images().list();
		List<Image> publicImages = new ArrayList<>();
		for(Image image:images){
			if(image.isPublic()){
				publicImages.add(image);
			}
		}
		return publicImages;
	}
	
	
	//删除一个Image

	public void deleteImage(OSClientV2 os){
		//os.imagesV2().delete(image.getId());
		os.images().delete(image.getId());
	}
	
	/**
	 * 删除一个Image
	 * @param os
	 * @param imageId
	 * @return
	 */
	public String deteteImage(OSClientV2 os, String imageId){
		String exceptionMessage = null;
		ActionResponse response = os.images().delete(imageId);
		if(response.isSuccess()){
			exceptionMessage = "succ";
		}else{
			exceptionMessage = response.getFault();
		}
		return exceptionMessage;
	}
	
	/*
	 * 更新镜像的名字和描述
	 */
	public String updateImageName(OSClientV2 os, String imageId, String newName){
		String exceptionMessage = null;
		Image image = os.images().get(imageId);
		try{
			os.images().update( (Image) image.toBuilder().name(newName));
			
			
//			os.images().update(
//					          (Image) image.toBuilder()
//					                  .name("newName")
//					                  .isPublic(true)
//					                  .diskFormat(DiskFormat.QCOW2)
//					                  .minDisk((long)1)
//					                  .minRam((long)1)
//					          );
			exceptionMessage = "succ";
		}catch(Exception e){
			exceptionMessage = e.getMessage();
		}
		return exceptionMessage;
	}
	
	
	/**
	 * 更新镜像的名字
	 * @param tokenId
	 * @param imageId
	 * @param newName
	 * @return
	 * @throws IOException
	 */
	public String updateImageName(String tokenId, String imageId,String newName) throws IOException{
		
		String imageAPI = IdentityOS.getAccessAPI().get("glance");
		String strURL = imageAPI+"/v2/images";//查询所有镜像的url
		strURL = strURL+"/"+imageId;
//		String strURL = "http://ahwater-cloud-controller:9292/v2/images/b0e064d5-e443-49b7-bd4b-4c18b50f2ba5";
		ImageName name = new ImageName();
		name.setOp("replace");
		name.setPath("/name");
		name.setValue(newName);
		ObjectMapper mapper = new ObjectMapper();
		String param = null;
		try {
			param = mapper.writeValueAsString(name);
			param = "["+param+"]";
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String result = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPatch httpPatch = new HttpPatch(strURL);  
		httpPatch.setHeader("Content-type", "application/openstack-images-v2.1-json-patch");  
        httpPatch.setHeader("Charset", HTTP.UTF_8);  
        httpPatch.setHeader("Accept", "application/json");  
        httpPatch.setHeader("Accept-Charset", HTTP.UTF_8);  
        httpPatch.setHeader("X-Auth-Token", tokenId);
        
        
        try {
       	 StringEntity entity = new StringEntity(param,HTTP.UTF_8);  
            httpPatch.setEntity(entity);  

           HttpResponse response = httpClient.execute(httpPatch);  
           result =EntityUtils.toString(response.getEntity());  
       } catch (Exception e) {  
           e.printStackTrace();  
       }  
       System.out.println(result);
       return result;
	}
	
	/**
	 * 列出云平台上的所有的镜像
	 * @param os
	 * @return
	 * @throws IOException
	 */
	public String AdminListAllImage(OSClientV2 os) throws IOException{
		Token token = os.getAccess().getToken();
//		String urlst = "http://ahwater-cloud-controller:9292/v2/images";//查询所有镜像的url
		String imageAPI = IdentityOS.getAccessAPI().get("glance");
		String urlst = imageAPI+"/v2/images";//查询所有镜像的url
		URL url;
		String result = null;
		try {
			url = new URL(urlst);
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("X-Auth-Token", token.getId());
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("charset", "UTF-8"); 
			connection.connect();
			
			int len= connection.getContentLength();
			InputStream is=connection.getInputStream();
			if(len!=-1){
				byte[] data=new byte[len];
				byte[] temp=new byte[512];
				int readLen=0;
				int destPos=0;
				while((readLen=is.read(temp))>0){
					System.arraycopy(temp, 0, data, destPos, readLen);
					destPos+=readLen;
				}
				result=new String(data,"UTF-8");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result;
	}
}
