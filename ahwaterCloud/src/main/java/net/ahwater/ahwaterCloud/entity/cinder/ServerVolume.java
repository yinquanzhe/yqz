package net.ahwater.ahwaterCloud.entity.cinder;

import java.util.List;

public class ServerVolume {
	String volumeId;
	String volumeName;
	List<String> device;
	public String getVolumeId() {
		return volumeId;
	}
	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}
	public String getVolumeName() {
		return volumeName;
	}
	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}
	public List<String> getDevice() {
		return device;
	}
	public void setDevice(List<String> device) {
		this.device = device;
	}

}
