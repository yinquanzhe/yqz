package net.ahwater.ahwaterCloud.compute;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.identityV3.IdentityOS;

/**
 * 虚拟机管理器类
 * @author gwh
 *
 */
public class HypervisorsOS {
	/**
	 * 虚拟机管理程序列表
	 * @param os
	 * @return
	 */
	public static List<? extends Hypervisor> listHypervisors(OSClientV2 os){
		
		return os.compute().hypervisors().list();
	}
	
	/**
	 * 虚拟管理程序统计信息
	 * @param os
	 * @return
	 */
	public static HypervisorStatistics getHypervisorStatistics(OSClientV2 os){
		return os.compute().hypervisors().statistics();
	}
	
	/**
	 * 列出一个主机下所有server
	 * @param tokenId
	 * @param tenantId
	 * @param hypervisorsId
	 * @return
	 * @throws IOException 
	 */
	public static String listHypervisorServers(String tenantId,String tokenId,String hypervisorsId) throws IOException{
		String novaAPI=IdentityOS.getAccessAPI().get("nova")+"/"+tenantId;
		String strURL=novaAPI+"/os-hypervisors/"+hypervisorsId+"/servers";
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
		JsonNode hyper = rootNode.get("hypervisors");
		JsonNode hyper1=hyper.get(0);
		JsonNode servers=hyper1.get("servers");
		result=servers.toString();
		return result;
	}
	
	
}
