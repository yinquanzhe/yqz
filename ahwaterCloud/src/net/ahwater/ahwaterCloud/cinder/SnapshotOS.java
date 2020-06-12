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
import org.openstack4j.model.storage.block.VolumeSnapshot;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.entity.AllServerListEle;
import net.ahwater.ahwaterCloud.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.JdbcUtil;

/**
 * 云硬盘快照操作类，
 * 功能：创建云硬盘快照；
 *     列出当前租户拥有的云硬盘快照；
 *     更新云硬盘快照的名字和描述；
 *     删除一个云硬盘快照；
 *     列出云平台上的所有的云硬盘快照.
 *     
 * @author ZhangDaofu
 *
 */
public class SnapshotOS {
	private VolumeSnapshot snapshot;
	public SnapshotOS(){
	}
	public SnapshotOS(VolumeSnapshot snapshot){
		this.snapshot = snapshot;
	}
	
	/**
	 * 创建snapshot
	 * @param os
	 * @param snapshotName
	 * @param snapshotDescription
	 * @param volumeId
	 * @return
	 */
	public String createVolumeSnapshot(OSClientV2 os,String snapshotName, String snapshotDescription, String volumeId){
		String exceptionMessage = null;
		try{
			snapshot = os.blockStorage().snapshots()
				     .create(Builders.volumeSnapshot()
				     .name(snapshotName)
				     .description(snapshotDescription)
				     .volume(volumeId)
				     .build()
				    );
			exceptionMessage = "success";
		}catch(Exception e){
			exceptionMessage = e.getMessage();
		}
		return exceptionMessage;
	}
	
	/**
	 * 列出当前租户拥有的云硬盘快照
	 * @param os
	 * @return
	 */
	public static List<? extends VolumeSnapshot> listAllVolumeSnapshot(OSClientV2 os){
		List<? extends VolumeSnapshot> volumeSnapshots = os.blockStorage().snapshots().list();//查询云硬盘的快照
		return volumeSnapshots;
	}
	
	
	
	
	
	/**
	 * 更新云硬盘快照的名字和描述
	 * @param os
	 * @param snapshotId
	 * @param newName
	 * @param newDescription
	 * @return
	 */
	public String updateNameDescription(OSClientV2 os, String snapshotId, String newName, String newDescription){
		String exceptionMessage = null;
		ActionResponse response = os.blockStorage().snapshots().update(snapshotId, newName, newDescription);
	    if(response.isSuccess()){
	    	exceptionMessage = "succ";
	    }else{
	    	exceptionMessage = response.getFault();
	    }
	    return exceptionMessage;
	}
	
	
	/**
	 * 删除一个云硬盘快照（删除一个snapshot）
	 * @param os
	 * @param volumeSnapshotId
	 * @return
	 */
	public String deleteSnapshot(OSClientV2 os, String volumeSnapshotId){
		String exceptionMessage = null;
		ActionResponse response = os.blockStorage().snapshots().delete(volumeSnapshotId);
		if(response.isSuccess()){
			exceptionMessage = "succ";
		}else{
			exceptionMessage = response.getFault();
		}
		return exceptionMessage;
	}
	
	
	/**
	 * 列出云平台上的所有的云硬盘快照
	 * @param token
	 * @param os
	 * @param tenantId
	 * @return
	 * @throws IOException
	 */
	public static String AdminListAllVolumeSnapshot(Token token, OSClientV2 os, String tenantId) throws IOException{
//		String host = "http://ahwater-cloud-controller:8776/v2/";
//		tenantsId = "6a98f9d2278d40cb8f58bf348db66ecc";
//		String ternantId = "d87bb24f88344b61aa5ee081082f58ac";
//		String urlst = host+tenantId+"/snapshots/detail?all_tenants=1";
		
		String volumeAPI = IdentityOS.getAccessAPI().get("cinderv2");
		String urlst = volumeAPI+"/"+tenantId+"/snapshots/detail?all_tenants=1";
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
