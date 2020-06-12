package net.ahwater.ahwaterCloud.entity.compute.VMManager;

import java.util.Map;

public class FlavorAllListEle {
	String ID;
	String name;
	int vcpus;
	int ram;
	int disk;
	int ephemeral;
	int swap;
	boolean isPublic;
	Map<String,String> extraSpecs;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public Map<String, String> getExtraSpecs() {
		return extraSpecs;
	}
	public void setExtraSpecs(Map<String, String> extraSpecs) {
		this.extraSpecs = extraSpecs;
	}
	
}
