package net.ahwater.ahwaterCloud.entity.compute.VMManager;

public class HypervisorStatisticsEle {
	int vcspus;
	int vcpusUsed;
	float memory;
	float memoryUsed;
	float localDisk;
	float localDiskUsed;
	public int getVcspus() {
		return vcspus;
	}
	public void setVcspus(int vcspus) {
		this.vcspus = vcspus;
	}
	public int getVcpusUsed() {
		return vcpusUsed;
	}
	public void setVcpusUsed(int vcpusUsed) {
		this.vcpusUsed = vcpusUsed;
	}
	public float getMemory() {
		return memory;
	}
	public void setMemory(float memory) {
		this.memory = memory;
	}
	public float getMemoryUsed() {
		return memoryUsed;
	}
	public void setMemoryUsed(float memoryUsed) {
		this.memoryUsed = memoryUsed;
	}
	public float getLocalDisk() {
		return localDisk;
	}
	public void setLocalDisk(float localDisk) {
		this.localDisk = localDisk;
	}
	public float getLocalDiskUsed() {
		return localDiskUsed;
	}
	public void setLocalDiskUsed(float localDiskUsed) {
		this.localDiskUsed = localDiskUsed;
	}
	
	
}
