package net.ahwater.ahwaterCloud.entity.compute;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuleCreationInfo {
	String securityGroupId;
	String protocol;
	String direction;
	@JsonProperty("hasPort")
	boolean hasPort;
	int portRangeMin;
	int portRangeMax;
	@JsonProperty("isCustomisedIcmp")
	boolean isCustomisedIcmp;
	int icmpType;
	int icmpCode;
	@JsonProperty("isCIDR")
	boolean isCIDR;
	String remoteIpPrefix;
	String remoteGroupId;
	String etherType;
	
	public boolean isCustomisedIcmp() {
		return isCustomisedIcmp;
	}
	public void setCustomisedIcmp(boolean isCustomisedIcmp) {
		this.isCustomisedIcmp = isCustomisedIcmp;
	}
	public int getIcmpType() {
		return icmpType;
	}
	public void setIcmpType(int icmpType) {
		this.icmpType = icmpType;
	}
	public int getIcmpCode() {
		return icmpCode;
	}
	public void setIcmpCode(int icmpCode) {
		this.icmpCode = icmpCode;
	}
	public String getRemoteIpPrefix() {
		return remoteIpPrefix;
	}
	public void setRemoteIpPrefix(String remoteIpPrefix) {
		this.remoteIpPrefix = remoteIpPrefix;
	}
	public String getRemoteGroupId() {
		return remoteGroupId;
	}
	public void setRemoteGroupId(String remoteGroupId) {
		this.remoteGroupId = remoteGroupId;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public boolean isHasPort() {
		return hasPort;
	}
	public void setHasPort(boolean hasPort) {
		this.hasPort = hasPort;
	}
	public int getPortRangeMin() {
		return portRangeMin;
	}
	public void setPortRangeMin(int portRangeMin) {
		this.portRangeMin = portRangeMin;
	}
	public int getPortRangeMax() {
		return portRangeMax;
	}
	public void setPortRangeMax(int portRangeMax) {
		this.portRangeMax = portRangeMax;
	}
	public boolean isCIDR() {
		return isCIDR;
	}
	public void setCIDR(boolean isCIDR) {
		this.isCIDR = isCIDR;
	}
	public String getSecurityGroupId() {
		return securityGroupId;
	}
	public void setSecurityGroupId(String securityGroupId) {
		this.securityGroupId = securityGroupId;
	}
	public String getEtherType() {
		return etherType;
	}
	public void setEtherType(String etherType) {
		this.etherType = etherType;
	}
	
	
	 
}
