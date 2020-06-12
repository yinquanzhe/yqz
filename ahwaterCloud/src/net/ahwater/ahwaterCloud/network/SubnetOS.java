package net.ahwater.ahwaterCloud.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;

import net.ahwater.ahwaterCloud.identityV3.IdentityOS;
/**
 * 子网的相关操作
 * @author zdf
 *
 */
public class SubnetOS {
	private Subnet subnet;
	
	public List<? extends Subnet> listAllSubnet(OSClientV2 os){
		List<? extends Subnet> subnets = os.networking().subnet().list();
		return subnets;
	}
	
	/**
	 * 列出一个网络下的所有的子网
	 * @param os
	 * @param networkId
	 * @return
	 */
	public static List<String> listSubnet(OSClientV2 os, String networkId){
		Network network = os.networking().network().get(networkId);
		List<String> subnet = network.getSubnets();
		return subnet;
	}
	
	
	/**
	 * 列出子网的详细信息
	 * @param token
	 * @param os
	 * @param subnetId
	 * @return
	 * @throws IOException
	 */
	public static String subnetDetail(Token token, OSClientV2 os, String subnetId) throws IOException{
		String result = "";
//		String host = "http://ahwater-cloud-controller:9696/v2.0";
//		String urlst = host+"/"+"subnets/"+subnetId;
		
		String networkAPI = IdentityOS.getAccessAPI().get("neutron");
		String urlst = networkAPI+"/v2.0"+"/subnets/"+subnetId;
		
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
