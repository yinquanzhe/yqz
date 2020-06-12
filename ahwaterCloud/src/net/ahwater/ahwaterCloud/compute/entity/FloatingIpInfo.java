package net.ahwater.ahwaterCloud.compute.entity;

public class FloatingIpInfo {
	String Id;
	String floatingIpAddress;
	String fixedIpAddress;
	String pool;
	String state;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getFloatingIpAddress() {
		return floatingIpAddress;
	}
	public void setFloatingIpAddress(String floatingIpAddress) {
		this.floatingIpAddress = floatingIpAddress;
	}
	public String getFixedIpAddress() {
		return fixedIpAddress;
	}
	public void setFixedIpAddress(String fixedIpAddress) {
		this.fixedIpAddress = fixedIpAddress;
	}
	public String getPool() {
		return pool;
	}
	public void setPool(String pool) {
		this.pool = pool;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
