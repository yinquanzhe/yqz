package net.ahwater.ahwaterCloud.entity.network;

import java.util.HashSet;
import java.util.Set;

import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.State;
/**
 * 用于显示接口列表中列表的属性
 * @author zdf
 *
 */
public class PortElement {
	private String portName;  //端口名
	private Set<String> portFixedIps = new HashSet<String>();//IP
	private String portDevice; //设备
	private State portState;  //状态
	private boolean portisAdminStateUp;  //管理员状态
	private String portId; //id
	
	
	
	public String getPortId() {
		return portId;
	}
	public void setPortId(String portId) {
		this.portId = portId;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	
	public Set<String> getPortFixedIps() {
		return portFixedIps;
	}
	public void setPortFixedIps(Set<? extends IP> portFixedIps) {
		//this.portFixedIps = portFixedIps;
		for(IP ip:portFixedIps){
			this.portFixedIps.add(ip.getIpAddress());
		}
	}
	
	public String getPortDevice() {
		return portDevice;
	}
	public void setPortDevice(String portDevice) {
		
		//this.portDevice = portDevice;
	}
	
	public State getPortState() {
		return portState;
	}
	public void setPortState(State portState) {
		this.portState = portState;
	}
	
	public boolean isPortisAdminStateUp() {
		return portisAdminStateUp;
	}
	public void setPortisAdminStateUp(boolean portisAdminStateUp) {
		this.portisAdminStateUp = portisAdminStateUp;
	}
	
}
