package net.ahwater.ahwaterCloud.entity.cinder;

public class VolumeSnapshotNameIdSize {
	private String volumeSnapshotName;  //云硬盘快照的名字
	private String volumeSnapshotId;    //云硬盘快照的Id
	private int volumeSnapshotSize;     //云硬盘快照的大小
	
	public String getVolumeSnapshotName() {
		return volumeSnapshotName;
	}
	public void setVolumeSnapshotName(String volumeSnapshotName) {
		this.volumeSnapshotName = volumeSnapshotName;
	}
	
	public String getVolumeSnapshotId() {
		return volumeSnapshotId;
	}
	public void setVolumeSnapshotId(String volumeSnapshotId) {
		this.volumeSnapshotId = volumeSnapshotId;
	}
	
	public int getVolumeSnapshotSize() {
		return volumeSnapshotSize;
	}
	public void setVolumeSnapshotSize(int volumeSnapshotSize) {
		this.volumeSnapshotSize = volumeSnapshotSize;
	}
	
}
