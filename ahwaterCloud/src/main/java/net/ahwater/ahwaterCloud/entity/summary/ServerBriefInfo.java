package net.ahwater.ahwaterCloud.entity.summary;

public class ServerBriefInfo {
	String serverId;
	String serverName;
	int vcpus;
	int disk;
	int ram;
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
	public int getRam() {
		return ram;
	}
	public void setRam(int ram) {
		this.ram = ram;
	}
	public String getTimeFromCreated() {
		return timeFromCreated;
	}
	public void setTimeFromCreated(String timeFromCreated) {
		this.timeFromCreated = timeFromCreated;
	}
	
	
}
