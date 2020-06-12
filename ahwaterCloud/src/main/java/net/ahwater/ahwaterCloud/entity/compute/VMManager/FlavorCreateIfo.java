package net.ahwater.ahwaterCloud.entity.compute.VMManager;

public class FlavorCreateIfo {
	String flavorName;
	int ram;
	int vcpus;
	int disk;
	int ephemeral;
	int swap;
//	boolean isPublic;
	String tenantIdListStr;
	public String getFlavorName() {
		return flavorName;
	}
	public void setFlavorName(String flavorName) {
		this.flavorName = flavorName;
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
	public int getEphemeral() {
		return ephemeral;
	}
	public void setEphemeral(int ephemeral) {
		this.ephemeral = ephemeral;
	}
	public int getSwap() {
		return swap;
	}
	public void setSwap(int swap) {
		this.swap = swap;
	}
	public String getTenantIdListStr() {
		return tenantIdListStr;
	}
	public void setTenantIdListStr(String tenantIdListStr) {
		this.tenantIdListStr = tenantIdListStr;
	}
	
	
	
}
