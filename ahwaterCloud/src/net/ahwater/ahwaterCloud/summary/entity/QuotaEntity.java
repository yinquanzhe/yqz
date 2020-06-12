package net.ahwater.ahwaterCloud.summary.entity;

public class QuotaEntity {
	int maxImageMeta;//元数据条目
	int maxPersonality;//注入的文件
	int maxPersonalitySize;//注入的文件内容字节数
	int maxServerGroupMembers;//主机组成员
	int maxServerGroups;//主机组
	int maxServerMeta;//
	int maxTotalCores;//虚拟内核
	int maxTotalInstances;//实例
	int maxTotalKeypairs;//密钥对
	int maxTotalRAMSize;//内存 (MB)
	
	int maxGigabytes;//磁盘总大小
	int maxSnapShots;//快照数
	int maxVolumes;//虚拟磁盘数
	
	int maxTotalFloatingIps;//浮动IP
	int maxSecurityGroupRules;//安全组规则数
	int maxSecurityGroups;//安全组个数
	int maxSubnet;//子网数
	int maxRouter;//路由数
	int maxPort;//端口
	int maxNetwork;//网络
	public int getMaxSubnet() {
		return maxSubnet;
	}
	public void setMaxSubnet(int maxSubnet) {
		this.maxSubnet = maxSubnet;
	}
	public int getMaxRouter() {
		return maxRouter;
	}
	public void setMaxRouter(int maxRouter) {
		this.maxRouter = maxRouter;
	}
	public int getMaxPort() {
		return maxPort;
	}
	public void setMaxPort(int maxPort) {
		this.maxPort = maxPort;
	}
	public int getMaxNetwork() {
		return maxNetwork;
	}
	public void setMaxNetwork(int maxNetwork) {
		this.maxNetwork = maxNetwork;
	}
	public int getMaxImageMeta() {
		return maxImageMeta;
	}
	public void setMaxImageMeta(int maxImageMeta) {
		this.maxImageMeta = maxImageMeta;
	}
	public int getMaxPersonality() {
		return maxPersonality;
	}
	public void setMaxPersonality(int maxPersonality) {
		this.maxPersonality = maxPersonality;
	}
	public int getMaxPersonalitySize() {
		return maxPersonalitySize;
	}
	public void setMaxPersonalitySize(int maxPersonalitySize) {
		this.maxPersonalitySize = maxPersonalitySize;
	}
	public int getMaxServerGroupMembers() {
		return maxServerGroupMembers;
	}
	public void setMaxServerGroupMembers(int maxServerGroupMembers) {
		this.maxServerGroupMembers = maxServerGroupMembers;
	}
	public int getMaxServerGroups() {
		return maxServerGroups;
	}
	public void setMaxServerGroups(int maxServerGroups) {
		this.maxServerGroups = maxServerGroups;
	}
	public int getMaxServerMeta() {
		return maxServerMeta;
	}
	public void setMaxServerMeta(int maxServerMeta) {
		this.maxServerMeta = maxServerMeta;
	}
	public int getMaxTotalCores() {
		return maxTotalCores;
	}
	public void setMaxTotalCores(int maxTotalCores) {
		this.maxTotalCores = maxTotalCores;
	}
	public int getMaxTotalFloatingIps() {
		return maxTotalFloatingIps;
	}
	public void setMaxTotalFloatingIps(int maxTotalFloatingIps) {
		this.maxTotalFloatingIps = maxTotalFloatingIps;
	}
	public int getMaxTotalInstances() {
		return maxTotalInstances;
	}
	public void setMaxTotalInstances(int maxTotalInstances) {
		this.maxTotalInstances = maxTotalInstances;
	}
	public int getMaxTotalRAMSize() {
		return maxTotalRAMSize;
	}
	public void setMaxTotalRAMSize(int maxTotalRAMSize) {
		this.maxTotalRAMSize = maxTotalRAMSize;
	}
	public int getMaxGigabytes() {
		return maxGigabytes;
	}
	public void setMaxGigabytes(int maxGigabytes) {
		this.maxGigabytes = maxGigabytes;
	}
	public int getMaxSnapShots() {
		return maxSnapShots;
	}
	public void setMaxSnapShots(int maxSnapShots) {
		this.maxSnapShots = maxSnapShots;
	}
	public int getMaxVolumes() {
		return maxVolumes;
	}
	public void setMaxVolumes(int maxVolumes) {
		this.maxVolumes = maxVolumes;
	}
	public int getMaxTotalKeypairs() {
		return maxTotalKeypairs;
	}
	public void setMaxTotalKeypairs(int maxTotalKeypairs) {
		this.maxTotalKeypairs = maxTotalKeypairs;
	}
	public int getMaxSecurityGroupRules() {
		return maxSecurityGroupRules;
	}
	public void setMaxSecurityGroupRules(int maxSecurityGroupRules) {
		this.maxSecurityGroupRules = maxSecurityGroupRules;
	}
	public int getMaxSecurityGroups() {
		return maxSecurityGroups;
	}
	public void setMaxSecurityGroups(int maxSecurityGroups) {
		this.maxSecurityGroups = maxSecurityGroups;
	}
}
