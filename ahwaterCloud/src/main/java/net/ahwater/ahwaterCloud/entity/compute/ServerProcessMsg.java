package net.ahwater.ahwaterCloud.entity.compute;

public class ServerProcessMsg {
	String msg;
	String ServerId;
	String serverName;
	String imageName;
	String availabilityZone;
	String ipAddr;
	int vcpus;
	int ram;
	int disk;
	String status;
	String timeFromCreated;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getServerId() {
		return ServerId;
	}

	public void setServerId(String serverId) {
		ServerId = serverId;
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
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
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
