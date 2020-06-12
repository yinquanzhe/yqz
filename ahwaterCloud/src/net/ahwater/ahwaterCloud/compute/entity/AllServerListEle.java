package net.ahwater.ahwaterCloud.compute.entity;

import java.util.List;

public class AllServerListEle {
	String tenanantName;
	String host;
	String serverId;
	String serverName;
	
	String imageName;
	String imagePersonalName;
	String imageOStype;
	String imageOSVersion;
	String imageOSBit;
	
//	String ipAddr;
	List<String> ipAddr;
	int vcpus;
	int ram;
	int disk;
	String status;
	String timeFromCreated;
	
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
		String[] name = imageName.split("#");
		this.imagePersonalName = name[0];
		if(name.length >= 2){
			this.imageOStype = name[1];
		}else{
			this.imageOStype = "无";
		}
		
		if(name.length >= 3){
			this.imageOSVersion = name[2];
		}
		else{
			this.imageOSVersion = "无";
		}
		
		if(name.length>=4){
			this.imageOSBit = name[3];
		}
	}
	
	public String getImagePersonalName() {
		return imagePersonalName;
	}
	
	public void setImagePersonalName(String imagePersonalName) {
		this.imagePersonalName = imagePersonalName;
	}
	public String getImageOStype() {
		return imageOStype;
	}
	public String setImageOStype(String imageOStype) {
		return  this.imageOStype=imageOStype;
	}
	public String getImageOSVersion() {
		return imageOSVersion;
	}
	public String setImageOSVersion(String imageOSVersion) {
		return this.imageOSVersion=imageOSVersion;
	}
	public String getImageOSBit() {
		return imageOSBit;
	} 
	public String setImageOSBit(String imageOSBit) {
		return this.imageOSBit=imageOSBit;
	} 
	public String getTenanantName() {
		return tenanantName;
	}
	public void setTenanantName(String tenanantName) {
		this.tenanantName = tenanantName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public List<String> getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(List<String> ipAddr) {
		this.ipAddr = ipAddr;
	}
	public int getVcpus() {
		return vcpus;
	}
	public void setVcpus(int vcpus) {
		this.vcpus = vcpus;
	}
	public int getRam() {
		return ram;
	}
	public void setRam(int ram) {
		this.ram = ram;
	}
	public int getDisk() {
		return disk;
	}
	public void setDisk(int disk) {
		this.disk = disk;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTimeFromCreated() {
		return timeFromCreated;
	}
	public void setTimeFromCreated(String timeFromCreated) {
		this.timeFromCreated = timeFromCreated;
	}
}
