package net.ahwater.ahwaterCloud.compute.entity.VMManager;

public class HypervisorListEle {
	String id;
	String hostname;
	String type;
	int usedVcpu;
	int vcpus;
	float localMemoryUsed;
	float localMemory;
	float localDiskUsed;
	float localDisk;
	int runningVM;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getUsedVcpu() {
		return usedVcpu;
	}
	public void setUsedVcpu(int usedVcpu) {
		this.usedVcpu = usedVcpu;
	}
	public int getVcpus() {
		return vcpus;
	}
	public void setVcpus(int vcpus) {
		this.vcpus = vcpus;
	}
	
	
	public float getLocalMemoryUsed() {
		return localMemoryUsed;
	}
	public void setLocalMemoryUsed(float localMemoryUsed) {
		this.localMemoryUsed = localMemoryUsed;
	}
	public float getLocalMemory() {
		return localMemory;
	}
	public void setLocalMemory(float localMemory) {
		this.localMemory = localMemory;
	}
	public float getLocalDiskUsed() {
		return localDiskUsed;
	}
	public void setLocalDiskUsed(float localDiskUsed) {
		this.localDiskUsed = localDiskUsed;
	}
	public float getLocalDisk() {
		return localDisk;
	}
	public void setLocalDisk(float localDisk) {
		this.localDisk = localDisk;
	}
	public int getRunningVM() {
		return runningVM;
	}
	public void setRunningVM(int runningVM) {
		this.runningVM = runningVM;
	}
	
}
