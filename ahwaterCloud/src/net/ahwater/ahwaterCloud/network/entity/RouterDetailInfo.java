package net.ahwater.ahwaterCloud.network.entity;

public class RouterDetailInfo {
	String routerId;
	String routerName;
	String projectId;
	String state;
	String adminState;
	
	String externalNetworkName;
	String externalNetworkId;
	String snat;
	public String getRouterId() {
		return routerId;
	}
	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}
	public String getRouterName() {
		return routerName;
	}
	public void setRouterName(String routerName) {
		this.routerName = routerName;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getAdminState() {
		return adminState;
	}
	public void setAdminState(String adminState) {
		this.adminState = adminState;
	}
	public String getExternalNetworkName() {
		return externalNetworkName;
	}
	public void setExternalNetworkName(String externalNetworkName) {
		this.externalNetworkName = externalNetworkName;
	}
	public String getExternalNetworkId() {
		return externalNetworkId;
	}
	public void setExternalNetworkId(String externalNetworkId) {
		this.externalNetworkId = externalNetworkId;
	}
	public String getSnat() {
		return snat;
	}
	public void setSnat(String snat) {
		this.snat = snat;
	}
	
}
