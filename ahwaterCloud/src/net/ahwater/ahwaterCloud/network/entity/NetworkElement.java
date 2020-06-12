package net.ahwater.ahwaterCloud.network.entity;
import java.util.ArrayList;
import java.util.List;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.State;
import org.openstack4j.model.network.Subnet;
/**
 * 用于显示网络的列表信息中的网络的属性
 * @author zdf
 *
 */
public class NetworkElement {
	private String networkId;  //网络Id
	private String networkName;  //网络的名字
	private List<String> networkSubnet = new ArrayList<String>();  //网络的子网
	private boolean networkIsShared;  //是否共享
	private State networkState;  //网络状态
	private boolean networkIsAdminStateUp;  //管理员状态
	
	private String tenantName;
	
	
	
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(OSClientV2 os, String tenantId) {
		this.tenantName = os.identity().tenants().get(tenantId).getName();
	}
	
	public String getNetworkId() {
		return networkId;
	}
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public List<String> getNetworkSubnet() {
		return networkSubnet;
	}
	public void setNetworkSubnet(List<String> networkSubnetId, OSClientV2 os) {
		//this.networkSubnet = networkSubnet;
		for(int i = 0; i < networkSubnetId.size(); i++){
			String subNetId = networkSubnetId.get(i);
			Subnet subnet = os.networking().subnet().get(subNetId);
			String subNetName = subnet.getName();
			String subNetCidr = subnet.getCidr();
			this.networkSubnet.add(subNetName+" "+subNetCidr);
		}
		
//		for(String subNetId:networkSubnetId){
//			Subnet subnet = os.networking().subnet().get(subNetId);
//			String 
//			this.networkSubnet.add(e)
//		}
		
	}
	
	public boolean isNetworkIsShared() {
		return networkIsShared;
	}
	public void setNetworkIsShared(boolean networkIsShared) {
		this.networkIsShared = networkIsShared;
	}
	
	public State getNetworkState() {
		return networkState;
	}
	public void setNetworkState(State networkState) {
		this.networkState = networkState;
	}
	
	public boolean isNetworkIsAdminStateUp() {
		return networkIsAdminStateUp;
	}
	public void setNetworkIsAdminStateUp(boolean networkIsAdminStateUp) {
		this.networkIsAdminStateUp = networkIsAdminStateUp;
	}
	
	
}
