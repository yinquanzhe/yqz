package net.ahwater.ahwaterCloud.cinder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.Volume.Status;
import org.openstack4j.model.storage.block.VolumeUploadImage;
import org.openstack4j.model.storage.block.builder.VolumeBuilder;
import org.openstack4j.model.storage.block.options.UploadImageData;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.entity.AllServerListEle;
import net.ahwater.ahwaterCloud.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.JdbcUtil;

/**
 * 云硬盘操作类
 * 功能：创建云硬盘（没有源）;
 *     创建云硬盘（从image创建）;
 *     创建云硬盘（从快照创建）;
 *     创建云硬盘（从硬盘创建）;
 *     查询当前租户所有的云硬盘;
 *     更新云硬盘的名字和描述;
 *     扩展云硬盘;
 *     将因云硬盘挂载到server;
 *     卸载云硬盘;
 *     更改云硬盘的状态;
 *     删除云硬盘;
 *     列出云平台上的所有云硬盘
 *     
 * @author ZhangDaofu
 *
 */
public class VolumeOS {
//	private OSClientV2 os;
	private Volume volume;
	
	public VolumeOS(){	
	}
	
	public VolumeOS(Volume volume){
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}
	
	
	/**
	 * 创建云硬盘（没有源）
	 * @param os
	 * @param volumeName
	 * @param volumeDescription
	 * @param volumeSize
	 * @return
	 */
	public String creatVolume(OSClientV2 os,String volumeName, String volumeDescription, int volumeSize){
		String exceptionMessage = "success";
		try{
			volume = os.blockStorage().volumes()
					   .create(Builders.volume()
					   .name(volumeName)
					   .description(volumeDescription)
					   .size(volumeSize)
					   .build()
					   );
		}catch(Exception e){
			exceptionMessage = e.getMessage();
		}
		
		return exceptionMessage;
	}
	

	/**
	 * 创建云硬盘（从image创建）
	 * @param os
	 * @param volumeName
	 * @param volumeDescription
	 * @param volumeSize
	 * @param imageId
	 * @return
	 */
	public String creatVolumefromImage(OSClientV2 os,String volumeName, String volumeDescription, int volumeSize, String imageId){
		String exceptionMessage = "";
		try{
			volume = os.blockStorage().volumes()
					   .create(Builders.volume()
					   .name(volumeName)
					   .description(volumeDescription)
					   .imageRef(imageId)
					   .size(volumeSize)
					   .build()
					   );   
			exceptionMessage = "success";
		} catch(Exception e){
			exceptionMessage = e.getMessage();
		}
		return exceptionMessage;
	}
	
	
	/**
	 * 创建云硬盘（从快照创建）
	 * @param os
	 * @param volumeName
	 * @param volumeDescription
	 * @param volumeSize
	 * @param snapshotId
	 * @return
	 */
	public String creatVolumefromSnapshot(OSClientV2 os, String volumeName, String volumeDescription, int volumeSize, String snapshotId){
		String exceptionMessage = null;
		try{
			volume = os.blockStorage().volumes()
					   .create(Builders.volume()
					   .name(volumeName)
					   .description(volumeDescription)
					   .snapshot(snapshotId)
					   .size(volumeSize)
					   .build()
					    );
			exceptionMessage = "success";
		}catch(Exception e){
			exceptionMessage = e.getMessage();
		}
		
		return exceptionMessage;
	}
	
	
	/**
	 * 创建云硬盘（从硬盘创建）
	 * @param os
	 * @param volumeName
	 * @param volumeDescription
	 * @param volumeSize
	 * @param volumeId
	 * @return
	 */
	public String creatVolumefromVolume(OSClientV2 os, String volumeName, String volumeDescription, int volumeSize, String volumeId){
		String exceptionMessage = null;
		try{
			
			volume = os.blockStorage().volumes()
					   .create(Builders.volume()
							   .name(volumeName)
			                   .description(volumeDescription)
			                   .source_volid(volumeId)
			                   .size(volumeSize)
			                   .build()	
							   );
			exceptionMessage = "success";
		}catch(Exception e){
			exceptionMessage = e.getMessage();
		}
		
		return exceptionMessage;
	}
	
	
	
	  //Create a Bootable Volume from an Image
	public Volume creatVolumefrom(OSClientV2 os,String volumeName, String volumeDescription, String imageId, boolean bootableTF){
		volume = os.blockStorage().volumes()
				   .create(Builders.volume()
				   .name(volumeName)
				   .description(volumeDescription)
				   .imageRef(imageId)
				   .bootable(bootableTF)
				   .build()
				   );   
		return volume;
	}
	
	
	/**
	 * 查询当前租户所有的volume
	 * @param os
	 * @return
	 */
	public static List<? extends Volume> listAllVolumes(OSClientV2 os) {
		List<? extends Volume> volumes = os.blockStorage().volumes().list();
		// TODO Auto-generated method stub
		return volumes;
	}
	
	
	/**
	 * 更新云硬盘的名字和描述
	 * @param os
	 * @param volumeId
	 * @param newName
	 * @param newDescription
	 * @return
	 */
	public String updateNameDescription(OSClientV2 os,String volumeId, String newName, String newDescription){
		String exceptionMessage = null;
		ActionResponse response= os.blockStorage().volumes().update(volumeId, newName, newDescription);
		if(response.isSuccess()){
			exceptionMessage = "success";
		}
		else{
			exceptionMessage = response.getFault();
		}
//		try{
//			os.blockStorage().volumes().update(volumeId, newName, newDescription);
//			exceptionMessage = "success";
//		}catch(Exception e){
//			exceptionMessage = e.getMessage();
//		}
		return exceptionMessage;
	}
	
	
	/**
	 * 扩展云硬盘
	 * @param os
	 * @param volumeId
	 * @param newSize
	 * @return
	 */
	public String extendVolume(OSClientV2 os, String volumeId, int newSize){
		String exceptionMessage = "";
		ActionResponse response = os.blockStorage().volumes().extend(volumeId, newSize);
		if(response.isSuccess()){
			exceptionMessage = "success";
		}
		else{
			exceptionMessage = response.getFault();
		}
		return exceptionMessage;
	}
	

	/**
	 * 将因云硬盘挂载到server
	 * @param os
	 * @param serverId
	 * @param volumeId
	 * @param device
	 * @return
	 */
	public String attachVolumeAndServer(OSClientV2 os, String serverId, String volumeId, String device){
		String message = null;
		try{
			os.compute().servers().attachVolume(serverId, volumeId, device);
			message = "success";
		}catch(Exception e){
			message = e.getMessage();
		}
		return message;
	}
	
	
	/**
	 * 卸载云硬盘
	 * @param os
	 * @param serverId
	 * @param attachmentId
	 * @return
	 */
	public String detachVolumeFromServer(OSClientV2 os, String serverId, String attachmentId){
		String message = null;
		ActionResponse response = os.compute().servers().detachVolume(serverId, attachmentId);
		if(response.isSuccess()){
			message = "success";
		}else{
			message = response.getFault();
		}
		return message;
	}
	
	
	/**
	 * 更改云硬盘的状态
	 * @param os
	 * @param newState
	 * @return
	 */
	public String  resetVolumeState(OSClientV2 os, String newState){
		//Volume.Status newState
		ActionResponse response = null;
		String exceptionMessage = null;
		if(newState.equals("in_use")){
			response = os.blockStorage().volumes().resetState(volume.getId(), Status.IN_USE);
		}else if(newState.equals("error")){
			response = os.blockStorage().volumes().resetState(volume.getId(), Status.ERROR);
		}else if(newState.equals("creating")){
			response = os.blockStorage().volumes().resetState(volume.getId(), Status.CREATING);
		}else if(newState.equals("deleting")){
			response = os.blockStorage().volumes().resetState(volume.getId(), Status.DELETING);
		}else if(newState.equals("detaching")){
			response = os.blockStorage().volumes().resetState(volume.getId(), Status.DETACHING);
		}else if(newState.equals("error_deleting")){
			response = os.blockStorage().volumes().resetState(volume.getId(), Status.ERROR_DELETING);
		}else if(newState.equals("available")){
			response = os.blockStorage().volumes().resetState(volume.getId(), Status.AVAILABLE);
		}
		if(response.isSuccess()){
			exceptionMessage = "succ";
		}else{
			exceptionMessage = response.getFault();
		}
		return exceptionMessage;
		
	}
	
	
	/**
	 * 删除云硬盘
	 * @param os
	 * @param volumeId
	 * @return
	 */
	public String deleteVolume(OSClientV2 os, String volumeId){
		String exceptionMessage = null;
		ActionResponse response = os.blockStorage().volumes().delete(volumeId);
		if(response.isSuccess()){
			exceptionMessage = "succ";
		}else{
			exceptionMessage = response.getFault();
		}
		return exceptionMessage;
	}
	
	//上传镜像
	public VolumeUploadImage uploadImage(OSClientV2 os,UploadImageData data){
		VolumeUploadImage volumeImage = os.blockStorage().volumes().uploadToImage(volume.getId(), data);
		return volumeImage;
		
	}
	
	
	/**
	 * 列出云平台上的所有云硬盘
	 * @param token
	 * @param os
	 * @param tenantId
	 * @return
	 * @throws IOException
	 */
	public static String  AdminListAllImage(Token token, OSClientV2 os, String tenantId) throws IOException{
//		String host = "http://ahwater-cloud-controller:8776/v2/";
//		String tenantId1 = "6a98f9d2278d40cb8f58bf348db66ecc";
//		String ternantId = "d87bb24f88344b61aa5ee081082f58ac";
//		String urlst = host+tenantId+"/volumes/detail?all_tenants=1";
		
		String volumeAPI = IdentityOS.getAccessAPI().get("cinderv2");
		String urlst = volumeAPI+"/"+tenantId+"/volumes/detail?all_tenants=1";
		
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
	/**
	 * 更据缓存表列出所有计算服务   2017-03-07  蔡国成
	 * @param tokenId
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	public static String AdminListAllImageByCache(String tenantId,String tenantName) throws Exception{
		
		Connection conn = JdbcUtil.getConnection();
		String sql="select * from  ahwater_cloud_instances where tenanantName=?"; //
		List<AllServerListEle> lm=new ArrayList<>();
		PreparedStatement pstmt=conn.prepareStatement(sql);
		ResultSet rs=null; 
		pstmt.setString(1,tenantName);
		rs=pstmt.executeQuery();// 
		while (rs.next()){
			  AllServerListEle m=new AllServerListEle();
				m.setTenanantName(rs.getString(2));
				m.setHost(rs.getString(3));
				m.setServerId(rs.getString(4));
				m.setServerName(rs.getString(5));
				m.setImageName(rs.getString(6));
				m.setImagePersonalName(rs.getString(7));
				m.setImageOStype(rs.getString(8));
				m.setImageOSVersion(rs.getString(9));
				m.setImageOSBit(rs.getString(10));
				
				List<String> ips=new  ArrayList<>();
				ips.add(rs.getString(11));
				m.setIpAddr(ips);
				m.setVcpus(rs.getInt(12));
				m.setRam(rs.getInt(13));
				m.setDisk(rs.getInt(14));
				m.setStatus(rs.getString(15));
				m.setTimeFromCreated(rs.getString(16)); 
				lm.add(m);
		}		
		JdbcUtil.close(conn, pstmt, rs);
		//处理 成 json
		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writeValueAsString(lm);   
		return result;		 
	}
	
	
	
	
}
