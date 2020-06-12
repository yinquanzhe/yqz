package net.ahwater.ahwaterCloud.entity.network;
/**
 * 用于显示子网详情
 * @author zdf
 *
 */
public class SubnetDetail extends SubnetElement{
	private String networkId;
	private String subnetPoolId;
	private String IPStart;
	private String IPEnd;
	private String enableDHCP;
	private String dns_nameservers;
	public String getNetworkId() {
		return networkId;
	}
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	public String getSubnetPoolId() {
		return subnetPoolId;
	}
	public void setSubnetPoolId(String subnetPoolId) {
		this.subnetPoolId = subnetPoolId;
	}
	public String getIPStart() {
		return IPStart;
	}
	public void setIPStart(String iPStart) {
		this.IPStart = iPStart;
	}
	public String getIPEnd() {
		return IPEnd;
	}
	public void setIPEnd(String iPEnd) {
		this.IPEnd = iPEnd;
	}
	public String getEnableDHCP() {
		return enableDHCP;
	}
	public void setEnableDHCP(String enableDHCP) {
		this.enableDHCP = enableDHCP;
	}
	
	public String getDns_nameservers() {
		return dns_nameservers;
	}
	public void setDns_nameservers(String dns_nameservers) {
		this.dns_nameservers = dns_nameservers;
	}
	
}
