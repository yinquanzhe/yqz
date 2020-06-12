package net.ahwater.ahwaterCloud.availabilityZone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Token;

public class AvailabilityZoneOS {
	public String listAvailabilityZone(Token token, OSClientV2 os, String tenantId) throws IOException{
		String result = "";
		String host = "http://ahwater-cloud-controller:8774/v2/";
		String urlst = host+tenantId+"/os-availability-zone/detail";
		URL url;
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
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
