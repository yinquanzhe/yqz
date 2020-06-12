package net.ahwater.ahwaterCloud.entity.network;

public class RouterBriefInfo {
	String routerId;
	String routerName;
	String externalNetworkName;
	String projectName;
	String state;
	String adminState;
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
	
	public String getExternalNetworkName() {
		return externalNetworkName;
	}
	public void setExternalNetworkName(String externalNetworkName) {
		this.externalNetworkName = externalNetworkName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
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
	
}
