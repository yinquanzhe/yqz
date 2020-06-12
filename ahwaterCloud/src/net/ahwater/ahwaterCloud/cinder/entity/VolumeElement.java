package net.ahwater.ahwaterCloud.cinder.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeAttachment;

/**
 * 定义云硬盘的要显示的属性，用于列表显示
 * @author ZhangDaofu
 *
 */
public class VolumeElement {
	private String volumeId;             //云硬盘的Id
	private String volumeName;           //云硬盘的名字
	private String volumeDescription;    //云硬盘的描述
	private int volumeSize;              //云硬盘的配置（大小）
	private Volume.Status volumeStatus;  //云硬盘的状态
	private String volumeType;           //云硬盘的类型
	private VolumeServerAttachment volumeAttachment;  //云硬盘的挂载的主机
	private String tenantName;           //云硬盘所属的租户的名字
	private String hostName;             //云硬盘所在的物理主机的名字
	private String date;                 //云硬盘创建的时间
	
	
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
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public void setTenantName(OSClientV2 os,String tenantId) {
//		this.tenantName = os.blockStorage().volumes().get(tenantId).getName();
		String id = tenantId;
		id = "6a98f9d2278d40cb8f58bf348db66ecc";
		Tenant tenant = os.identity().tenants().get(id);
//		System.out.println(id);
		
		this.tenantName = tenant.getName();
	}
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
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
	
	public String getVolumeDescription() {
		return volumeDescription;
	}
	public void setVolumeDescription(String volumeDescription) {
		this.volumeDescription = volumeDescription;
	}
	
	public int getVolumeSize() {
		return volumeSize;
	}
	public void setVolumeSize(int size) {
		this.volumeSize = size;
	}
	
	public Volume.Status getVolumeStatus() {
		return volumeStatus;
	}
	public void setVolumeStatus(Volume.Status volumeStatus) {
		this.volumeStatus = volumeStatus;
	}
	
	public String getVolumeType() {
		return volumeType;
	}
	public void setVolumeType(String volumeType) {
		this.volumeType = volumeType;
	}
	
	
//	public List<? extends VolumeAttachment> getVolumeAttachment() {
//		return volumeAttachment;
//	}
//	public void setVolumeAttachment(List<? extends VolumeAttachment> volumeAttachment) {
//		this.volumeAttachment = volumeAttachment;
//	}
	
//	public List<VolumeServerAttachment> getVolumeAttachment() {
//		return volumeAttachment;
//	}
//	public void setVolumeAttachment(List<? extends VolumeAttachment> attachment, OSClientV2 os) {
//		for(VolumeAttachment attach:attachment){
//			VolumeServerAttachment vsAttach = new VolumeServerAttachment();
//			vsAttach.setAttachmentId(attach.getId());
//			vsAttach.setDevice(attach.getDevice());
//			vsAttach.setVolumeId(attach.getVolumeId());
//			vsAttach.setServerId(attach.getServerId());
//			vsAttach.setServerName(os, attach.getServerId());
//			this.volumeAttachment.add(vsAttach);
//		}
//	}
	
	public VolumeServerAttachment getVolumeAttachment() {
		return volumeAttachment;
	}
	public void setVolumeAttachment(List<? extends VolumeAttachment> attachment, OSClientV2 os) {
		if(attachment.size()>=1){
			VolumeAttachment attach = attachment.get(0);
			VolumeServerAttachment vsAttach = new VolumeServerAttachment();
			vsAttach.setAttachmentId(attach.getId());
			vsAttach.setDevice(attach.getDevice());
			vsAttach.setVolumeId(attach.getVolumeId());
			vsAttach.setServerId(attach.getServerId());
			vsAttach.setServerName(os, attach.getServerId());
			this.volumeAttachment = vsAttach;
		}
	}
	
//	public String getVolumeZone() {
//		return volumeZone;
//	}
//	public void setVolumeZone(String volumeZone) {
//		this.volumeZone = volumeZone;
//	}
	
}
