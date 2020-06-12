package net.ahwater.ahwaterCloud.compute;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openstack4j.api.OSClient.OSClientV2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.entity.AllServerListEle;
import net.ahwater.ahwaterCloud.compute.entity.VMManager.ComputeServiceDiableEle;
import net.ahwater.ahwaterCloud.compute.entity.VMManager.ComputeServiceEnableEle;
import net.ahwater.ahwaterCloud.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.monitor.entity.MonitorInfo;
import net.ahwater.ahwaterCloud.util.JdbcUtil;

/**
 * 计算域服务类
 * @author gwh
 *
 */
public class ComputeServiceOS {
	static final String binary="nova-compute";
	
	/**
	 * 列出所有计算服务
	 * @param tokenId
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	public static String listComputeServices(String tenantId,String tokenId) throws Exception{
		String novaAPI=IdentityOS.getAccessAPI().get("nova")+"/"+tenantId;
		String strURL=novaAPI+"/os-services";
		String result=null;
		try {
			URL url=new URL(strURL);
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("X-Auth-Token", tokenId);
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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(result);
		JsonNode services = rootNode.get("services");
		
		List<JsonNode> res= new ArrayList<JsonNode>();
		for(int i=0;i<services.size();i++){
			JsonNode ch=services.get(i);
			if(ch.get("zone").asText().equals("nova")){
				res.add(ch);
			}
		}	
		result=mapper.writeValueAsString(res);
		return result;
	}
	
	/**
	 * 更据缓存表列出所有计算服务   2017-03-07  蔡国成
	 * @param tokenId
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	public static String listComputeServicesByCache(String tenantId,String tenantName) throws Exception{
		
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
	/**
	 * 关闭服务
	 * @param tokenId
	 * @param tenantId
	 * @param hostName
	 * @param reason
	 * @return
	 * @throws IOException 
	 */
	public static String disableComputeService(String tenantId,String tokenId,String hostName,String reason) throws IOException{
		String novaAPI=IdentityOS.getAccessAPI().get("nova")+"/"+tenantId;
		String strURL=novaAPI+"/os-services/disable-log-reason";
		ComputeServiceDiableEle csde=new ComputeServiceDiableEle();
		csde.setHost(hostName);
		csde.setBinary(binary);
		csde.setDisabled_reason(reason);
		
		ObjectMapper mapper = new ObjectMapper();
		String param=null;
		try {
			param = mapper.writeValueAsString(csde);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		
		String result=null;
		try {
			URL url=new URL(strURL);
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("X-Auth-Token", tokenId);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.connect();
			OutputStreamWriter out=new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			out.append(param);
			out.flush();
			out.close();
			
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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 打开服务
	 * @param tokenId
	 * @param tenantId
	 * @param hostName
	 * @return
	 * @throws IOException 
	 */
	public static String enableComputeService(String tenantId,String tokenId,String hostName) throws IOException{
		String novaAPI=IdentityOS.getAccessAPI().get("nova")+"/"+tenantId;
		String strURL=novaAPI+"/os-services/enable";
		ComputeServiceEnableEle csee=new ComputeServiceEnableEle();
		csee.setHost(hostName);
		csee.setBinary(binary);
		
		ObjectMapper mapper = new ObjectMapper();
		String param=null;
		try {
			param = mapper.writeValueAsString(csee);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		String result=null;
		try {
			URL url=new URL(strURL);
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("X-Auth-Token", tokenId);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.connect();
			OutputStreamWriter out=new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			out.append(param);
			out.flush();
			out.close();
			
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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	} 
}
