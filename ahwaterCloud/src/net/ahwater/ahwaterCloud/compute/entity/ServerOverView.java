package net.ahwater.ahwaterCloud.compute.entity;

import java.util.List;
import java.util.Map;

import net.ahwater.ahwaterCloud.cinder.entity.ServerVolume;

public class ServerOverView {
	String serverName;
	String serverId;
	String status;
	String availabilityZone;
	String createdTime;
	String timeFromCreated;
	String host;
	String flavorName;
	String flavorId;
	int ram;
	int vcpus;
	int disk;
	Map<String,String> ipAddrs;
	Map<String,String> metaData;
	String keyName;
	String imageId;
	String imageName;
	
	List<ServerVolume> volumes;
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAvailabilityZone() {
		return availabilityZone;
	}
	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getTimeFromCreated() {
		return timeFromCreated;
	}
	public void setTimeFromCreated(String timeFromCreated) {
		this.timeFromCreated = timeFromCreated;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getFlavorName() {
		return flavorName;
	}
	public void setFlavorName(String flavorName) {
		this.flavorName = flavorName;
	}
	public String getFlavorId() {
		return flavorId;
	}
	public void setFlavorId(String flavorId) {
		this.flavorId = flavorId;
	}
	public int getRam() {
		return ram;
	}
	public void setRam(int ram) {
		this.ram = ram;
	}
	public int getVcpus() {
		return vcpus;
	}
	public void setVcpus(int vcpus) {
		this.vcpus = vcpus;
	}
	public int getDisk() {
		return disk;
	}
	public void setDisk(int disk) {
		this.disk = disk;
	}
	
	
	public Map<String, String> getIpAddrs() {
		return ipAddrs;
	}
	public void setIpAddrs(Map<String, String> ipAddrs) {
		this.ipAddrs = ipAddrs;
	}
	public Map<String, String> getMetaData() {
		return metaData;
	}
	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public List<ServerVolume> getVolumes() {
		return volumes;
	}
	public void setVolumes(List<ServerVolume> volumes) {
		this.volumes = volumes;
	}

	
	
}
