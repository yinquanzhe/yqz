package net.ahwater.ahwaterCloud.compute.systemInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openstack4j.api.OSClient.OSClientV2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.identityV3.IdentityOS;

public class ServiceOS {
	
	public static String listService(String tokenId) throws Exception{
		String host=IdentityOS.getAccessAPI().get("keystone");
		host=host.replaceAll("2.0", "3");
		String strURL=host+"/services";
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
		JsonNode services=rootNode.get("services");
		result=services.toString();
		return result;
	}
	
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
		
		result=services.toString();
		return result;
	}
	
}
