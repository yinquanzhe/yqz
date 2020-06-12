package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeAttachment;
import org.openstack4j.model.storage.block.VolumeSnapshot;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.cinder.SnapshotOS;
import net.ahwater.ahwaterCloud.cinder.VolumeOS;
import net.ahwater.ahwaterCloud.cinder.entity.VolumeElement;
import net.ahwater.ahwaterCloud.cinder.entity.VolumeNameIdSize;
import net.ahwater.ahwaterCloud.cinder.entity.VolumeServerAttachment;
import net.ahwater.ahwaterCloud.cinder.entity.VolumeSnapshotElement;
import net.ahwater.ahwaterCloud.cinder.entity.VolumeSnapshotNameIdSize;
import net.ahwater.ahwaterCloud.compute.ServerOS;
import net.ahwater.ahwaterCloud.compute.entity.ServerNameId;


@Controller
@RequestMapping("/cont")
public class VolumeProcess {
	
	/**
	 * 列出一个租户的所有的云硬盘
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listAllVolumes")
	public void listAllVolume(HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Volume> volumes= VolumeOS.listAllVolumes(os);		
		List<VolumeElement> vols = new ArrayList<>();
		
		for(Volume v:volumes){
			VolumeElement volEle = new VolumeElement();
			volEle.setVolumeId(v.getId());
			volEle.setVolumeName(v.getName());
			volEle.setVolumeDescription(v.getDescription());
			volEle.setVolumeSize(v.getSize());
			volEle.setVolumeStatus(v.getStatus());
			volEle.setVolumeType(v.getVolumeType());
			volEle.setVolumeAttachment(v.getAttachments(), os);
			volEle.setDate(v.getCreated());
			vols.add(volEle);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(vols); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 列出云硬盘的名字，Id, 容量大小
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	
	@RequestMapping("/listVolumesNameIdSize")
	public void listVolumesNameIdSize(HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Volume> volumes= VolumeOS.listAllVolumes(os);
		List<VolumeNameIdSize> volNISs = new ArrayList<>();
		for(Volume v:volumes){
			if(v.getStatus().toString().equals("available")){
				VolumeNameIdSize volnis = new VolumeNameIdSize();
				volnis.setVolumeId(v.getId());
				volnis.setVolumeName(v.getName());
				volnis.setVolumeSize(v.getSize());
				volNISs.add(volnis);
			}
			
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(volNISs);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 创建云硬盘（无源）
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/createVolume")
	public void createVolume(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		String volumeName = request.getParameter("volumeName");
		String volumeDescription = request.getParameter("volumeDescription");
		int volumeSize = Integer.valueOf(request.getParameter("volumSize")).intValue() ;
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		VolumeOS vos = new VolumeOS();
		String message = vos.creatVolume(os, volumeName, volumeDescription, volumeSize);
		while(true){
			boolean createFinish =  vos.getVolume().getStatus().toString().equals("available");
			if(createFinish || !message.equals("success")){
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	

	/**
	 * 创建云硬盘（从快照）
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/createVolumefromVolumeSnapshot")
	public void createVolumefromVolumeSnapshot(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		String volumeName = request.getParameter("volumeName");
		String volumeDescription = request.getParameter("volumeDescription");
		int volumeSize = Integer.valueOf(request.getParameter("volumSize")).intValue() ;
		String volumeSnapshotId = request.getParameter("volumeSnapshotId");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		VolumeOS vos = new VolumeOS();
		String message = vos.creatVolumefromSnapshot(os, volumeName, volumeDescription, volumeSize, volumeSnapshotId);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 创建云硬盘（从镜像）
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/createVolumefromImage")
	public void createVolumefromImage(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		String volumeName = request.getParameter("volumeName");
		String volumeDescription = request.getParameter("volumeDescription");
		int volumeSize = Integer.valueOf(request.getParameter("volumSize")).intValue() ;
		String imageId = request.getParameter("imageId");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		VolumeOS vos = new VolumeOS();
		String message = vos.creatVolumefromImage(os, volumeName, volumeDescription, volumeSize, imageId);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 创建云硬盘（从已有云硬盘 ）
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/createVolumefromVolume")
	public void createVolumefromVolume(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		create(response,request,session,4);
	}
	
	public void create(HttpServletResponse response, HttpServletRequest request, HttpSession session, int type) throws IOException{
		String volumeName = request.getParameter("volumeName");
		String volumeDescription = request.getParameter("volumeDescription");
		int volumeSize = Integer.valueOf(request.getParameter("volumSize")).intValue() ;
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		VolumeOS vos = new VolumeOS();
		String message = null;
		if(type == 1){
			message = vos.creatVolume(os, volumeName, volumeDescription, volumeSize);
		}
		else if(type == 2){
			String volumeSnapshotId = request.getParameter("volumeSnapshotId");
			message = vos.creatVolumefromSnapshot(os, volumeName, volumeDescription, volumeSize, volumeSnapshotId);
		}
		else if(type == 3){
			String imageId = request.getParameter("imageId");
			message = vos.creatVolumefromImage(os, volumeName, volumeDescription, volumeSize, imageId);
		}
		else if(type == 4){
			String volumeId = request.getParameter("volumeId");
			message = vos.creatVolumefromVolume(os, volumeName, volumeDescription, volumeSize, volumeId);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);	
	}
	
	
	/**
	 * 修改云硬盘的名字和描述
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/editVolume")
	public void editVolume(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		String volumeId = request.getParameter("volumeId");
		String newName = request.getParameter("volumeName");
		String newDescription = request.getParameter("volumeDescription");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		VolumeOS vos = new VolumeOS();
		String message = vos.updateNameDescription(os, volumeId, newName, newDescription);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 扩展云硬盘的容量
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/extendVolume")
	public void extendVolume(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		String volumeId = request.getParameter("volumeId");
		int newSize = Integer.valueOf(request.getParameter("newSize")).intValue() ;
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		VolumeOS vos = new VolumeOS();
		String message = vos.extendVolume(os, volumeId, newSize);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 管理云硬盘（将云硬盘挂载到主机或将云硬盘从主机卸载）
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/manageVolume")
	public void manageVolume(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String volumeId = request.getParameter("volumeId");
		Volume volume = os.blockStorage().volumes().get(volumeId);
		List<? extends VolumeAttachment> volumeAttachment= volume.getAttachments();
		List<VolumeServerAttachment> vsAttachments = new ArrayList<>();
		
		for(VolumeAttachment va:volumeAttachment){
			VolumeServerAttachment vsa = new VolumeServerAttachment();
			vsa.setAttachmentId(va.getId());
			vsa.setDevice(va.getDevice());
			vsa.setServerId(va.getServerId());
			vsa.setServerName(os, va.getServerId());
			vsa.setVolumeId(va.getVolumeId());
			vsAttachments.add(vsa);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(vsAttachments);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 将云硬盘挂载到server
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/attachVolumeServer")
	public void attachVolumeServer(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		VolumeOS vos = new VolumeOS();
		String serverId = request.getParameter("serverId");
		String volumeId = request.getParameter("volumeId");
//		String device = request.getParameter("device");
		String device = null;
		String message = vos.attachVolumeAndServer(os, serverId, volumeId, device);
		
		
		Volume volume = os.blockStorage().volumes().get(volumeId);
		while(volume.getStatus().toString().equals("in-use")){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 列出server的name和Id
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listServerNameId")
	public void listServerNameId(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		List<? extends Server> servers=ServerOS.listAllServersDetail(os);
		List<ServerNameId> sNameId = new ArrayList<>();
		for(Server s:servers){
			ServerNameId nameid = new ServerNameId();
			nameid.setServerName(s.getName());
			nameid.setServerId(s.getId());
			sNameId.add(nameid);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(sNameId); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 卸载云硬盘
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/detachVolumeServer")
	public void detachVolumeServer(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		VolumeOS vos = new VolumeOS();
		String serverId = request.getParameter("serverId");
		String attachmentId = request.getParameter("attachmentId");
		String volumeId = request.getParameter("volumeId");
		String message = vos.detachVolumeFromServer(os, serverId, attachmentId);
		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		Volume volume = os.blockStorage().volumes().get(volumeId);
//		while(!volume.getStatus().toString().equals("available")){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 更改云硬盘的状态
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/resetVolumeState")
	public void resetVolumeState(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		String volumeId = request.getParameter("volumeId");
		String newState = request.getParameter("newState");
		VolumeOS vos = new VolumeOS(os.blockStorage().volumes().get(volumeId));
		String message = vos.resetVolumeState(os, newState);
		Volume vol = os.blockStorage().volumes().get(volumeId);
		
		if(message == "succ"){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			while(vol.getStatus().toString()!=newState){
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 删除云硬盘
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/deleteVolume")
	public void deleteVolume(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String volumeId = request.getParameter("volumeId");
		
		VolumeOS vos = new VolumeOS();
		String message = vos.deleteVolume(os, volumeId);
	    Volume vol = os.blockStorage().volumes().get(volumeId);
		if(message.equals("succ")){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			while(vol.getStatus().toString().equals("deleting")){
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 批量删除云硬盘
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/deleteMultiVolume")
	public void deleteMultiVolume(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String volumeIdsStr = request.getParameter("volumeIds");
		ObjectMapper mapper = new ObjectMapper(); 
		List<String> volumeIds = mapper.readValue(volumeIdsStr, new TypeReference<List<String>>() {});
		List<String> messages = new ArrayList<>();
		VolumeOS vos = new VolumeOS();
		for(String volumeId:volumeIds){
			String message = vos.deleteVolume(os, volumeId);
			messages.add(message);
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(messages);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 查询所有的云硬盘快照
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listAllVolumeSnapshot")
	public void listAllVolumeSnapshot(HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends VolumeSnapshot> volumeSnapshots= SnapshotOS.listAllVolumeSnapshot(os);
		List<VolumeSnapshotElement> vsnapshots = new ArrayList<>();
		for(VolumeSnapshot vs:volumeSnapshots){
			VolumeSnapshotElement vsnapshot = new VolumeSnapshotElement();
			vsnapshot.setVolumeSnapshotId(vs.getId());
			vsnapshot.setVolumeSnapshotName(vs.getName());
			vsnapshot.setVolumeSnapshotDescription(vs.getDescription());
			vsnapshot.setVolumeSnapshotState(vs.getStatus());
			vsnapshot.setVolumeName(vs.getVolumeId(), os);
			vsnapshot.setVolumeId(vs.getVolumeId());
			vsnapshot.setVolumeSnapshotSize(vs.getSize());
			vsnapshot.setDate(vs.getCreated());
			vsnapshots.add(vsnapshot);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(vsnapshots);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 返回所有的云硬盘快照的Name,Id,Size
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listVolumeSnapshotsNameIdSize")
	public void listVolumeSnapshotsNameIdSize(HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends VolumeSnapshot> volumeSnapshots= SnapshotOS.listAllVolumeSnapshot(os);
		List<VolumeSnapshotNameIdSize> vsnapshotNISs = new ArrayList<>();
		for(VolumeSnapshot vs:volumeSnapshots){
			VolumeSnapshotNameIdSize vsnapshotNIS = new VolumeSnapshotNameIdSize();
			vsnapshotNIS.setVolumeSnapshotName(vs.getName());
			vsnapshotNIS.setVolumeSnapshotId(vs.getId());
			vsnapshotNIS.setVolumeSnapshotSize(vs.getSize());
			vsnapshotNISs.add(vsnapshotNIS);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(vsnapshotNISs);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 创建云硬盘快照
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/createVolumeSnapshot")
	public void createVolumeSnapshot(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		String volumeSnapshotName = request.getParameter("volumeSnapshotName");
		String volumeSnapshotDescription = request.getParameter("volumeSnapshotDescription");
		String volumeId = request.getParameter("volumeId");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		SnapshotOS sos = new SnapshotOS();
		
		String message = sos.createVolumeSnapshot(os, volumeSnapshotName, volumeSnapshotDescription, volumeId);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 编辑云硬盘快照
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/editVolumeSnapshot")
	public void editVolumeSnapshot(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		String volumeSnapshotId = request.getParameter("volumeSnapshotId");
		String newVolumeSnapshotName = request.getParameter("newVolumeSnapshotName");
		String newVolumeSnapshotDescription = request.getParameter("newVolumeSnapshotDescription");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		SnapshotOS sos = new SnapshotOS();
		String message = sos.updateNameDescription(os, volumeSnapshotId, newVolumeSnapshotName, newVolumeSnapshotDescription);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 删除一个云硬盘的快照
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/deleteVolumeSnapshot")
	public void deleteVolumeSnapshot(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String volumeSnapshotId = request.getParameter("volumeSnapshotId");
		SnapshotOS sos = new SnapshotOS(os.blockStorage().snapshots().get(volumeSnapshotId));
		String message = sos.deleteSnapshot(os, volumeSnapshotId);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message); 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 批量删除云硬盘快照
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("deleteMultiVolumeSnapshots")
	public void deleteMultiVolumeSnapshots(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String volumeSnapshotIdsStr = request.getParameter("volumeSnapshotIds");
		ObjectMapper mapper = new ObjectMapper();
		List<String> volumeSnapshotIds = mapper.readValue(volumeSnapshotIdsStr, new TypeReference<List<String>>() {}); 
		List<String> messages = new ArrayList<>();
		SnapshotOS sos = new SnapshotOS();
		for(String volumeSnapshotId:volumeSnapshotIds){
			String message = sos.deleteSnapshot(os, volumeSnapshotId);
			messages.add(message);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String msg = mapper.writeValueAsString(messages);
		PrintWriter out=null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 列出云平台上的所有的云硬盘
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/adminListAllVolumes")
	public void adminListAllVolumes(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		String tenantId = request.getParameter("tenantId");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		Token token = os.getAccess().getToken();
		String result = VolumeOS.AdminListAllImage(token, os, tenantId);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(result);
		JsonNode volumesjson = rootNode.get("volumes");
		List<VolumeElement> volumesElement = new ArrayList<>();
		for(int i = 0; i < volumesjson.size(); i++){
			JsonNode volumejson = volumesjson.get(i);
			String volumeTenantId = volumejson.get("os-vol-tenant-attr:tenant_id").toString();
			volumeTenantId = volumeTenantId.substring(1, volumeTenantId.length()-1);
			VolumeElement volele = new VolumeElement();
			String volumeId = volumejson.get("id").toString();
			volumeId = volumeId.substring(1,volumeId.length()-1);
			Volume volume = os.blockStorage().volumes().get(volumeId);
			String hostName = volumejson.get("os-vol-host-attr:host").toString();
			hostName = hostName.substring(1, hostName.length()-1);
			volele.setHostName(hostName);
//			volele.setTenantName(os, volumeTenantId);
			Tenant tenent = os.identity().tenants().get(volumeTenantId);
			volele.setTenantName(tenent.getName());
			volele.setVolumeAttachment(volume.getAttachments(), os);
			volele.setVolumeDescription(volume.getDescription());
			volele.setVolumeId(volumeId);
			volele.setVolumeName(volume.getName());
			volele.setVolumeSize(volume.getSize());
			volele.setVolumeStatus(volume.getStatus());
			volele.setVolumeType(volume.getVolumeType());
			volele.setDate(volume.getCreated());
			volumesElement.add(volele);
		}
		String msg = mapper.writeValueAsString(volumesElement);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 列出云平台上的所有的云硬盘快照
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/adminListAllVolumeSnapshots")
	public void adminListAllVolumeSnapshots(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		String tenantId = request.getParameter("tenantId");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		Token token = os.getAccess().getToken();
//		SnapshotOS sos = new SnapshotOS();
		String result = SnapshotOS.AdminListAllVolumeSnapshot(token, os, tenantId);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(result);
		JsonNode volumeSnapshotsjson = rootNode.get("snapshots");
		List<VolumeSnapshotElement> volumeSnapshotsElement = new ArrayList<>();
		for(int i=0; i < volumeSnapshotsjson.size(); i++){
			JsonNode volumesnapshotjson = volumeSnapshotsjson.get(i);
			String snapshotTenantId = volumesnapshotjson.get("os-extended-snapshot-attributes:project_id").toString(); 
			snapshotTenantId = snapshotTenantId.substring(1, snapshotTenantId.length()-1);
			VolumeSnapshotElement vsele = new VolumeSnapshotElement();
			String volumeSnapshotId = volumesnapshotjson.get("id").toString();
			volumeSnapshotId = volumeSnapshotId.substring(1, volumeSnapshotId.length()-1);
			VolumeSnapshot volumeSnapshot = os.blockStorage().snapshots().get(volumeSnapshotId);
			vsele.setVolumeId(volumeSnapshot.getVolumeId());
			vsele.setVolumeName(volumeSnapshot.getVolumeId(), os);
			vsele.setVolumeSnapshotDescription(volumeSnapshot.getDescription());
			vsele.setVolumeSnapshotId(volumeSnapshot.getId());
			vsele.setVolumeSnapshotName(volumeSnapshot.getName());
			vsele.setVolumeSnapshotSize(volumeSnapshot.getSize());
			vsele.setVolumeSnapshotState(volumeSnapshot.getStatus());
			vsele.setDate(volumeSnapshot.getCreated());
			vsele.setTenantName(os, snapshotTenantId);
			volumeSnapshotsElement.add(vsele);
		}
		String msg = mapper.writeValueAsString(volumeSnapshotsElement);
		PrintWriter out =null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
}
