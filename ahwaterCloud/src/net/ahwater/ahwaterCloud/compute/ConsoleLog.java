package net.ahwater.ahwaterCloud.compute;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openstack4j.api.OSClient.OSClientV2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.entity.ConsoleLogParam;
import net.ahwater.ahwaterCloud.compute.entity.os_getConsoleOutput;
import net.ahwater.ahwaterCloud.identityV3.IdentityOS;

/**
 * 控制台日志类
 * @author gwh
 *
 */
public class ConsoleLog {
	
	/**
	 * 获取控制台日志
	 * @param tokenId
	 * @param tenantId
	 * @param serverId
	 * @param numLines
	 * @return
	 * @throws IOException 
	 */
	public static String getConsoleLog(String tenantId,String tokenId,String serverId,int numLines) throws IOException{
		String novaAPI=IdentityOS.getAccessAPI().get("nova")+"/"+tenantId;
		String strURL=novaAPI+"/servers/"+serverId+"/action";
		
		ConsoleLogParam clp=new ConsoleLogParam();
		os_getConsoleOutput ogt=new os_getConsoleOutput();
		ogt.setLength(numLines);
		clp.setOgt(ogt);
		ObjectMapper mapper = new ObjectMapper();
		String param=null;
		try {
			param = mapper.writeValueAsString(clp);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		param=param.replaceAll("ogt", "os-getConsoleOutput");
		
		String result=null;
		try {
			URL url=new URL(strURL);
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("X-Auth-Token", tokenId);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", "21");
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
