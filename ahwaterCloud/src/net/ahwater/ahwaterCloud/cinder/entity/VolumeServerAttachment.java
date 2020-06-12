package net.ahwater.ahwaterCloud.cinder.entity;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.Server;
/**
 * 连接类，用于描述将云硬盘挂载到云主机
 * @author ZhangDaofu
 *
 */
public class VolumeServerAttachment {
	private String attachmentId;  //挂载的Id
	private String device;        //设备
	private String serverId;      //主机（实例）的Id
	private String volumeId;      //云硬盘的Id
	private String serverName;    //主机（实例）的名字
	
	public String getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
	
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	
	public String getVolumeId() {
		return volumeId;
	}
	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(OSClientV2 os, String serverId) {
		Server server = os.compute().servers().get(serverId);
		this.serverName = server.getName();
	}
	
}
