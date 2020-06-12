package net.ahwater.ahwaterCloud.network.entity;

import org.openstack4j.model.network.NetworkType;
/**
 * 用于显示网络的详细信息
 * @author zdf
 *
 */
public class NetworkDetail extends NetworkElement{
	private String tenantId;
	private String isExternal;
	private String networkType;
	private String phycialNetwork;
	private String segId;
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getIsExternal() {
		return isExternal;
	}
	public void setIsExternal(boolean isext) {
		if(isext == true)
			this.isExternal = "是";
		else
			this.isExternal = "否";
		
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(NetworkType nettype) {
		if(nettype == null){
			this.networkType = null;
		}
		else
			this.networkType = nettype.toString();
	}
	public String getPhycialNetwork() {
		return phycialNetwork;
	}
	public void setPhycialNetwork(String phycialNetwork) {
		if(phycialNetwork == null)
			this.phycialNetwork = null;
		else
			this.phycialNetwork = phycialNetwork;
	}
	public String getSegId() {
		return segId;
	}
	public void setSegId(String segId) {
		this.segId = segId;
	}
	
}
