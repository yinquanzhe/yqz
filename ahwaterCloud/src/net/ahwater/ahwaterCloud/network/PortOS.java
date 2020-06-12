package net.ahwater.ahwaterCloud.network;

import java.util.List;
import java.util.Set;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.options.PortListOptions;
/**
 * 端口的相关操作
 * @author dell
 *
 */
public class PortOS {
	private Port port;
	
	//
	/**
	 * 找出一个网络下的所有port
	 * @param os
	 * @param networkId
	 * @return
	 */
	public PortOS(){	
	}
	public PortOS(Port port){
		this.port = port;
	}
	public static List<? extends Port> listAllPorts(OSClientV2 os, String networkId){
		PortListOptions options = PortListOptions.create();
		PortListOptions networkIdOption = options.networkId(networkId);
		List<? extends Port> portsOptions = os.networking().port().list(networkIdOption);
		return portsOptions;
	}
	
	public String getPortName(){
		return port.getName();
	}
	
	public Set<? extends IP> getPortFixedIps(){
		return port.getFixedIps();
	}
	
	public String updatePortName(OSClientV2 os, String newName){
		String exceptionMessage = null;
		try{
			os.networking().port().update(port.toBuilder().name(newName).build());
			exceptionMessage  = "succ";
		}catch(Exception e){
			exceptionMessage = e.getMessage();
		}
		
		return exceptionMessage;
	}
}
