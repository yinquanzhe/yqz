package net.ahwater.ahwaterCloud.entity.cinder;

public class VolumeNameIdSize {
	private String volumeId;       //云硬盘的Id
	private String volumeName;     //云硬盘的名字
	private int volumeSize;        //云硬盘的配置（大小）
	
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
	
	public int getVolumeSize() {
		return volumeSize;
	}
	public void setVolumeSize(int volumeSize) {
		this.volumeSize = volumeSize;
	}
}
