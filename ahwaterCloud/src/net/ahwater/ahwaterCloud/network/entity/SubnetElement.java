package net.ahwater.ahwaterCloud.network.entity;

import org.openstack4j.model.network.IPVersionType;
/**
 * 用于显示子网列表中子网的属性
 * @author zdf
 *
 */
public class SubnetElement {
	private String subNetId;
	private String subnetName;
	private String subnetCidr;
	private String ipVersion;
	private String gateway;
	
	public String getSubNetId() {
		return subNetId;
	}
	public void setSubNetId(String subNetId) {
		this.subNetId = subNetId;
	}
	public String getSubnetName() {
		return subnetName;
	}
	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}
	public String getSubnetCidr() {
		return subnetCidr;
	}
	public void setSubnetCidr(String subnetCidr) {
		this.subnetCidr = subnetCidr;
	}
	public String getIpVersion() {
		return ipVersion;
	}
	public void setIpVersion(String ipVersion) {
		this.ipVersion = ipVersion;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	
}
