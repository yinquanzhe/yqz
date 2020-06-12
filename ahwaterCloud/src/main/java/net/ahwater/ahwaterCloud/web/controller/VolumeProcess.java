package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.cinder.*;
import net.ahwater.ahwaterCloud.entity.compute.ServerNameId;
import net.ahwater.ahwaterCloud.entity.image.ImageElement;
import net.ahwater.ahwaterCloud.service.cinder.SnapshotOS;
import net.ahwater.ahwaterCloud.service.cinder.VolumeOS;
import net.ahwater.ahwaterCloud.service.compute.ServerOS;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 云硬盘控制类
 * 定义了对云硬盘的相关操作
 */
@RestController
@RequestMapping("/cont")
public class VolumeProcess {

    @Autowired
    private VolumeOS volumeOS;
    @Autowired
    private SnapshotOS snapshotOS;
    @Autowired
    private ServerOS serverOS;

    /**
     * 列出一个租户的所有的云硬盘
     *
     * @param session
     */
    @RequestMapping("/listAllVolumes")
    public List<VolumeElement> listAllVolume(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Volume> volumes = volumeOS.listAllVolumes(os);
        List<VolumeElement> vols = new ArrayList<>();

        for (Volume v : volumes) {
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
        return vols;
    }


    /**
     * 列出云硬盘的名字，Id, 容量大小
     *
     * @param session
     */
    @RequestMapping("/listVolumesNameIdSize")
    public List<VolumeNameIdSize> listVolumesNameIdSize(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Volume> volumes = volumeOS.listAllVolumes(os);
        List<VolumeNameIdSize> volNISs = new ArrayList<>();
        for (Volume v : volumes) {
            if (v.getStatus().toString().equals("available")) {
                VolumeNameIdSize volnis = new VolumeNameIdSize();
                volnis.setVolumeId(v.getId());
                volnis.setVolumeName(v.getName());
                volnis.setVolumeSize(v.getSize());
                volNISs.add(volnis);
            }
        }
        return volNISs;
    }


    /**
     * 创建云硬盘（无源）
     *
     * @param ve
     * @param session
     */
    @RequestMapping("/createVolume")
    public String createVolume(VolumeElement ve, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String message = volumeOS.creatVolume(os, ve.getVolumeName(), ve.getVolumeDescription(), ve.getVolumeSize());
        while (true) {
            boolean createFinish = volumeOS.getVolume().getStatus().toString().equals("available");
            if (createFinish || !message.equals("success")) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        return message;
    }


    /**
     * 创建云硬盘（从快照）
     *
     * @param ve
     * @param vse
     * @param session
     */
    @RequestMapping("/createVolumefromVolumeSnapshot")
    public String createVolumefromVolumeSnapshot(VolumeElement ve, VolumeSnapshotElement vse, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return volumeOS.creatVolumefromSnapshot(os, ve.getVolumeName(), ve.getVolumeDescription(), ve.getVolumeSize(), vse.getVolumeSnapshotId());
    }


    /**
     * 创建云硬盘（从镜像）
     *
     * @param ve
     * @param ie
     * @param session
     */
    @RequestMapping("/createVolumefromImage")
    public String createVolumefromImage(VolumeElement ve, ImageElement ie, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return volumeOS.creatVolumefromImage(os, ve.getVolumeName(), ve.getVolumeDescription(), ve.getVolumeSize(), ie.getImageId());
    }


    /**
     * 创建云硬盘（从已有云硬盘 ）
     *
     * @param volumeElement
     * @param volumeSnapshotElement
     * @param imageElement
     * @param session
     */
    @RequestMapping("/createVolumefromVolume")
    public String createVolumefromVolume(VolumeElement volumeElement, VolumeSnapshotElement volumeSnapshotElement, ImageElement imageElement, HttpSession session) {
        return create(volumeElement, volumeSnapshotElement, imageElement, session, 4);
    }

    public String create(VolumeElement volumeElement, VolumeSnapshotElement volumeSnapshotElement, ImageElement imageElement, HttpSession session, int type) {
        String volumeName = volumeElement.getVolumeName();
        String volumeDescription = volumeElement.getVolumeDescription();
        int volumeSize = volumeElement.getVolumeSize();
        String volumeSnapshotId = volumeSnapshotElement.getVolumeSnapshotId();
        String imageId = imageElement.getImageId();
        String volumeId = volumeElement.getVolumeId();
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String message = null;
        if (type == 1) {
            message = volumeOS.creatVolume(os, volumeName, volumeDescription, volumeSize);
        } else if (type == 2) {
            message = volumeOS.creatVolumefromSnapshot(os, volumeName, volumeDescription, volumeSize, volumeSnapshotId);
        } else if (type == 3) {
            message = volumeOS.creatVolumefromImage(os, volumeName, volumeDescription, volumeSize, imageId);
        } else if (type == 4) {
            message = volumeOS.creatVolumefromVolume(os, volumeName, volumeDescription, volumeSize, volumeId);
        }
        return message;
    }


    /**
     * 修改云硬盘的名字和描述
     *
     * @param volumeElement
     * @param session
     */
    @RequestMapping("/editVolume")
    public String editVolume(VolumeElement volumeElement, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return volumeOS.updateNameDescription(os, volumeElement.getVolumeId(), volumeElement.getVolumeName(), volumeElement.getVolumeDescription());
    }


    /**
     * 扩展云硬盘的容量
     *
     * @param volumeId
     * @param newSize
     * @param session
     */
    @RequestMapping("/extendVolume")
    public String extendVolume(String volumeId, int newSize, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return volumeOS.extendVolume(os, volumeId, newSize);
    }


    /**
     * 管理云硬盘（将云硬盘挂载到主机或将云硬盘从主机卸载）
     *
     * @param volumeId
     * @param session
     */
    @RequestMapping("/manageVolume")
    public List<VolumeServerAttachment> manageVolume(String volumeId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Volume volume = os.blockStorage().volumes().get(volumeId);
        List<? extends VolumeAttachment> volumeAttachment = volume.getAttachments();
        List<VolumeServerAttachment> vsAttachments = new ArrayList<>();

        for (VolumeAttachment va : volumeAttachment) {
            VolumeServerAttachment vsa = new VolumeServerAttachment();
            vsa.setAttachmentId(va.getId());
            vsa.setDevice(va.getDevice());
            vsa.setServerId(va.getServerId());
            vsa.setServerName(os, va.getServerId());
            vsa.setVolumeId(va.getVolumeId());
            vsAttachments.add(vsa);
        }
        return vsAttachments;
    }


    /**
     * 将云硬盘挂载到server
     *
     * @param serverId 要挂载的服务器id
     * @param volumeId 云硬盘id
     * @param session  sesion
     */
    @RequestMapping("/attachVolumeServer")
    public String attachVolumeServer(String serverId, String volumeId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String device = null;
        String message = volumeOS.attachVolumeAndServer(os, serverId, volumeId, device);

        Volume volume = os.blockStorage().volumes().get(volumeId);
        while (volume.getStatus().toString().equals("in-use")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    /**
     * 列出server的name和Id
     *
     * @param session session
     */
    @RequestMapping("/listServerNameId")
    public List<ServerNameId> listServerNameId(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        List<? extends Server> servers = serverOS.listAllServersDetail(os);
        List<ServerNameId> sNameId = new ArrayList<>();
        for (Server s : servers) {
            ServerNameId nameid = new ServerNameId();
            nameid.setServerName(s.getName());
            nameid.setServerId(s.getId());
            sNameId.add(nameid);
        }
        return sNameId;
    }


    /**
     * 卸载云硬盘
     *
     * @param serverId     云硬盘id
     * @param attachmentId 附件id
     * @param session      session
     */
    @RequestMapping("/detachVolumeServer")
    public String detachVolumeServer(String serverId, String attachmentId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return volumeOS.detachVolumeFromServer(os, serverId, attachmentId);
    }


    /**
     * 更改云硬盘的状态
     *
     * @param volumeId 云硬盘id
     * @param newState 新的状态
     * @param session  session
     */
    @RequestMapping("/resetVolumeState")
    public String resetVolumeState(String volumeId, String newState, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        VolumeOS vos = new VolumeOS(os.blockStorage().volumes().get(volumeId));
        String message = vos.resetVolumeState(os, newState);
        if (message == "succ") {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message;
    }


    /**
     * 删除云硬盘
     *
     * @param volumeId 云硬盘id
     * @param session  session
     */
    @RequestMapping("/deleteVolume")
    public String deleteVolume(String volumeId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        String message = volumeOS.deleteVolume(os, volumeId);
        if (message.equals("succ")) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message;
    }


    /**
     * 批量删除云硬盘
     *
     * @param volumeIds 云硬盘id
     * @param session   session
     * @throws IOException
     */
    @RequestMapping("/deleteMultiVolume")
    public List<String> deleteMultiVolume(String volumeIds, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        ObjectMapper mapper = new ObjectMapper();
        List<String> volumeIdList = mapper.readValue(volumeIds, new TypeReference<List<String>>() {
        });
        List<String> messages = new ArrayList<>();
        VolumeOS vos = new VolumeOS();
        for (String volumeId : volumeIdList) {
            String message = vos.deleteVolume(os, volumeId);
            messages.add(message);
        }
        return messages;
    }


    /**
     * 查询所有的云硬盘快照
     *
     * @param session session
     */
    @RequestMapping("/listAllVolumeSnapshot")
    public List<VolumeSnapshotElement> listAllVolumeSnapshot(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends VolumeSnapshot> volumeSnapshots = SnapshotOS.listAllVolumeSnapshot(os);
        List<VolumeSnapshotElement> vsnapshots = new ArrayList<>();
        for (VolumeSnapshot vs : volumeSnapshots) {
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
        return vsnapshots;
    }


    /**
     * 返回所有的云硬盘快照的Name,Id,Size
     *
     * @param session session
     */
    @RequestMapping("/listVolumeSnapshotsNameIdSize")
    public List<VolumeSnapshotNameIdSize> listVolumeSnapshotsNameIdSize(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends VolumeSnapshot> volumeSnapshots = SnapshotOS.listAllVolumeSnapshot(os);
        List<VolumeSnapshotNameIdSize> vsnapshotNISs = new ArrayList<>();
        for (VolumeSnapshot vs : volumeSnapshots) {
            VolumeSnapshotNameIdSize vsnapshotNIS = new VolumeSnapshotNameIdSize();
            vsnapshotNIS.setVolumeSnapshotName(vs.getName());
            vsnapshotNIS.setVolumeSnapshotId(vs.getId());
            vsnapshotNIS.setVolumeSnapshotSize(vs.getSize());
            vsnapshotNISs.add(vsnapshotNIS);
        }
        return vsnapshotNISs;
    }


    /**
     * 创建云硬盘快照
     *
     * @param session session
     */
    @RequestMapping("/createVolumeSnapshot")
    public String createVolumeSnapshot(VolumeSnapshotElement volumeSnapshotElement, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return snapshotOS.createVolumeSnapshot(os, volumeSnapshotElement.getVolumeSnapshotName(), volumeSnapshotElement.getVolumeSnapshotDescription(), volumeSnapshotElement.getVolumeId());
    }


    /**
     * 编辑云硬盘快照
     *
     * @param volumeSnapshotId             编辑的云硬盘快照的id
     * @param newVolumeSnapshotName        新的云硬盘快照名称
     * @param newVolumeSnapshotDescription 新的云硬盘快照描述
     * @param session                      session
     */
    @RequestMapping("/editVolumeSnapshot")
    public String editVolumeSnapshot(String volumeSnapshotId, String newVolumeSnapshotName, String newVolumeSnapshotDescription, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return snapshotOS.updateNameDescription(os, volumeSnapshotId, newVolumeSnapshotName, newVolumeSnapshotDescription);
    }


    /**
     * 删除一个云硬盘的快照
     *
     * @param volumeSnapshotId 云硬盘的快照的id
     * @param session          session
     */
    @RequestMapping("/deleteVolumeSnapshot")
    public String deleteVolumeSnapshot(String volumeSnapshotId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        SnapshotOS sos = new SnapshotOS(os.blockStorage().snapshots().get(volumeSnapshotId));
        String message = sos.deleteSnapshot(os, volumeSnapshotId);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return message;
    }


    /**
     * 批量删除云硬盘快照
     *
     * @param volumeSnapshotIds 云硬盘快照的id
     * @param session           session
     * @throws IOException
     */
    @RequestMapping("deleteMultiVolumeSnapshots")
    public List<String> deleteMultiVolumeSnapshots(String volumeSnapshotIds, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        ObjectMapper mapper = new ObjectMapper();
        List<String> volumeSnapshotIdList = mapper.readValue(volumeSnapshotIds, new TypeReference<List<String>>() {
        });
        List<String> messages = new ArrayList<>();
        for (String volumeSnapshotId : volumeSnapshotIdList) {
            String message = snapshotOS.deleteSnapshot(os, volumeSnapshotId);
            messages.add(message);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }


    /**
     * 列出云平台上的所有的云硬盘
     *
     * @param tenantId 租户id
     * @param session  session
     * @throws IOException
     */
    @RequestMapping("/adminListAllVolumes")
    public List<VolumeElement> adminListAllVolumes(String tenantId, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        Token token = os.getAccess().getToken();
        String result = volumeOS.AdminListAllImage(token, os, tenantId);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode volumesjson = rootNode.get("volumes");
        List<VolumeElement> volumesElement = new ArrayList<>();
        for (int i = 0; i < volumesjson.size(); i++) {
            JsonNode volumejson = volumesjson.get(i);
            String volumeTenantId = volumejson.get("os-vol-tenant-attr:tenant_id").toString();
            volumeTenantId = volumeTenantId.substring(1, volumeTenantId.length() - 1);
            VolumeElement volele = new VolumeElement();
            String volumeId = volumejson.get("id").toString();
            volumeId = volumeId.substring(1, volumeId.length() - 1);
            Volume volume = os.blockStorage().volumes().get(volumeId);
            String hostName = volumejson.get("os-vol-host-attr:host").toString();
            hostName = hostName.substring(1, hostName.length() - 1);
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
        return volumesElement;
    }


    /**
     * 列出云平台上的所有的云硬盘快照
     *
     * @param tenantId 租户id
     * @param session  session
     * @throws IOException
     */
    @RequestMapping("/adminListAllVolumeSnapshots")
    public List<VolumeSnapshotElement> adminListAllVolumeSnapshots(String tenantId, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        Token token = os.getAccess().getToken();
        String result = SnapshotOS.AdminListAllVolumeSnapshot(token, os, tenantId);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode volumeSnapshotsjson = rootNode.get("snapshots");
        List<VolumeSnapshotElement> volumeSnapshotsElement = new ArrayList<>();
        for (int i = 0; i < volumeSnapshotsjson.size(); i++) {
            JsonNode volumesnapshotjson = volumeSnapshotsjson.get(i);
            String snapshotTenantId = volumesnapshotjson.get("os-extended-snapshot-attributes:project_id").toString();
            snapshotTenantId = snapshotTenantId.substring(1, snapshotTenantId.length() - 1);
            VolumeSnapshotElement vsele = new VolumeSnapshotElement();
            String volumeSnapshotId = volumesnapshotjson.get("id").toString();
            volumeSnapshotId = volumeSnapshotId.substring(1, volumeSnapshotId.length() - 1);
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
        return volumeSnapshotsElement;
    }
}
