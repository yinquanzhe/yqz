package net.ahwater.ahwaterCloud.compute.entity;

public class RuleInfo {
	String ruleId;
	String direction;
	String etherType;
	String protocol;
	String portRange;
	String cidr;
	String remoteSecurityGroup;
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getEtherType() {
		return etherType;
	}
	public void setEtherType(String etherType) {
		this.etherType = etherType;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getPortRange() {
		return portRange;
	}
	public void setPortRange(String portRange) {
		this.portRange = portRange;
	}
	public String getCidr() {
		return cidr;
	}
	public void setCidr(String cidr) {
		this.cidr = cidr;
	}
	public String getRemoteSecurityGroup() {
		return remoteSecurityGroup;
	}
	public void setRemoteSecurityGroup(String remoteSecurityGroup) {
		this.remoteSecurityGroup = remoteSecurityGroup;
	}
	
}
