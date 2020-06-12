package net.ahwater.ahwaterCloud.entity.compute;


public class ServerCreateIfo {
	String serverName;
	String flavorId;
	int number;
	String imageId;
	String keypairsName;
	String securityGroupName;
	String networksStr;
//	List<String> networksStr;
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getFlavorId() {
		return flavorId;
	}
	public void setFlavorId(String flavorId) {
		this.flavorId = flavorId;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getKeypairsName() {
		return keypairsName;
	}
	public void setKeypairsName(String keypairsName) {
		this.keypairsName = keypairsName;
	}
	public String getSecurityGroupName() {
		return securityGroupName;
	}
	public void setSecurityGroupName(String securityGroupName) {
		this.securityGroupName = securityGroupName;
	}
	public String getNetworksStr() {
		return networksStr;
	}
	public void setNetworksStr(String networksStr) {
		this.networksStr = networksStr;
	}
//	public List<String> getNetworksStr() {
//		return networksStr;
//	}
//	public void setNetworksStr(List<String> networksStr) {
//		this.networksStr = networksStr;
//	}
	
}
