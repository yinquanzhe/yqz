package net.ahwater.ahwaterCloud.entity.availabilityZone;

import java.util.List;

public class AvailabilityZone {
	private String zoneState;
	private List<String> host;
	private String zoneName;
	public String getZoneState() {
		return zoneState;
	}
	public void setZoneState(String zoneState) {
		if(zoneState.equals("true")){
			this.zoneState = "是";
		}else{
			this.zoneState = "否";
		}
	}
	public List<String> getHost() {
		return host;
	}
	public void setHost(List<String> host) {
		this.host = host;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	
}
