package net.ahwater.ahwaterCloud.summary.entity;


public class TenantUsageInfo {
	String tenantName;
	int vcpu;
	double memory;
	double diskSize;
	double runHours;
	double memoryMbUsage;
	double vcpuUsage;
	double diskGbUsage;
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public int getVcpu() {
		return vcpu;
	}
	public void setVcpu(int vcpu) {
		this.vcpu = vcpu;
	}
	public double getMemory() {
		return memory;
	}
	public void setMemory(double memory) {
		this.memory = memory;
	}
	public double getDiskSize() {
		return diskSize;
	}
	public void setDiskSize(double diskSize) {
		this.diskSize = diskSize;
	}
	public double getRunHours() {
		return runHours;
	}
	public void setRunHours(double runHours) {
		this.runHours = runHours;
	}
	public double getMemoryMbUsage() {
		return memoryMbUsage;
	}
	public void setMemoryMbUsage(double memoryMbUsage) {
		this.memoryMbUsage = memoryMbUsage;
	}
	public double getVcpuUsage() {
		return vcpuUsage;
	}
	public void setVcpuUsage(double vcpuUsage) {
		this.vcpuUsage = vcpuUsage;
	}
	public double getDiskGbUsage() {
		return diskGbUsage;
	}
	public void setDiskGbUsage(double diskGbUsage) {
		this.diskGbUsage = diskGbUsage;
	}
	
}
