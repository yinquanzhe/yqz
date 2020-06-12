package net.ahwater.ahwaterCloud.cinder.entity;
import java.util.Calendar;
import java.util.Date;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.Volume.Status;

/**
 * 定义要显示的云硬盘快照的属性，用于列表显示
 * @author ZhangDaofu
 *
 */
public class VolumeSnapshotElement {
	private String volumeSnapshotId;         //云硬盘快照的Id
	private String volumeSnapshotName;       //云硬盘快照的名字
	private String volumeSnapshotDescription;//云硬盘快照的描述
	private int volumeSnapshotSize;          //云硬盘快照的大小
	private Status volumeSnapshotState;      //云硬盘快照的状态
	private String volumeName;               //云硬盘的名字
	private String volumeId;                 //云硬盘的Id
	
	private String tenantName;               //云硬盘快照的租户的名字
	private String date;                     //云硬盘快照创建的时间
	
	
	
	public String getDate() {
		return date;
	}
	public void setDate(Date date) {
		//this.date = date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		this.date = ""+year+"年"+month+"月"+day+"日"+" "+hour+":"+minute;
	}
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(OSClientV2 os, String tenantId) {
		this.tenantName = os.identity().tenants().get(tenantId).getName();
	}
	public String getVolumeSnapshotId() {
		return volumeSnapshotId;
	}
	public void setVolumeSnapshotId(String volumeSnapshotId) {
		this.volumeSnapshotId = volumeSnapshotId;
	}
	public String getVolumeSnapshotName() {
		return volumeSnapshotName;
	}
	public void setVolumeSnapshotName(String volumeSnapshotName) {
		this.volumeSnapshotName = volumeSnapshotName;
	}
	public String getVolumeSnapshotDescription() {
		return volumeSnapshotDescription;
	}
	public void setVolumeSnapshotDescription(String volumeSnapshotDescription) {
		this.volumeSnapshotDescription = volumeSnapshotDescription;
	}
	public int getVolumeSnapshotSize() {
		return volumeSnapshotSize;
	}
	public void setVolumeSnapshotSize(int volumeSnapshotSize) {
		this.volumeSnapshotSize = volumeSnapshotSize;
	}
	public Status getVolumeSnapshotState() {
		return volumeSnapshotState;
	}
	public void setVolumeSnapshotState(Status volumeSnapshotState) {
		this.volumeSnapshotState = volumeSnapshotState;
	}
	public String getVolumeName() {
		return volumeName;
	}
	public void setVolumeName(String volumeId, OSClientV2 os) {	
		Volume volume = os.blockStorage().volumes().get(volumeId);
		this.volumeName = volume.getName();
	}
	public String getVolumeId() {
		return volumeId;
	}
	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}
	
	
}
