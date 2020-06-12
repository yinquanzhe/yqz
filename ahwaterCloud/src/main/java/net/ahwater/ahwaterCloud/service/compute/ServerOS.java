package net.ahwater.ahwaterCloud.service.compute;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.cinder.ServerVolume;
import net.ahwater.ahwaterCloud.service.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.HttpUtils;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.*;
import org.openstack4j.model.compute.VNCConsole.Type;
import org.openstack4j.model.compute.actions.LiveMigrateOptions;
import org.openstack4j.model.compute.actions.RebuildOptions;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeAttachment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 云主机类
 *
 * @author gwh
 */
@Service
public class ServerOS {

    private Server server;

    @SuppressWarnings("serial")
    public final static Map<String, String> status2China = new HashMap<String, String>() {
        {
            put(Server.Status.ACTIVE.value(), "运行中");
            put(Server.Status.BUILD.value(), "创建中");
            put(Server.Status.REBUILD.value(), "重新创建中");
            put(Server.Status.SUSPENDED.value(), "挂起");
            put(Server.Status.PAUSED.value(), "暂停");
            put(Server.Status.RESIZE.value(), "调整大小");
            put(Server.Status.VERIFY_RESIZE.value(), "确认调整大小");
            put(Server.Status.REVERT_RESIZE.value(), "恢复调整大小");
            put(Server.Status.PASSWORD.value(), "密码");
            put(Server.Status.REBOOT.value(), "软重启");
            put(Server.Status.HARD_REBOOT.value(), "硬重启");
            put(Server.Status.DELETED.value(), "删除");
            put(Server.Status.UNKNOWN.value(), "未知状态");
            put(Server.Status.ERROR.value(), "错误");
            put(Server.Status.STOPPED.value(), "停止");
            put(Server.Status.SHUTOFF.value(), "关机");
            put(Server.Status.MIGRATING.value(), "迁移中");
            put(Server.Status.SHELVED.value(), "搁置");
            put(Server.Status.SHELVED_OFFLOADED.value(), "卸载搁置");
            put(Server.Status.UNRECOGNIZED.value(), "未识别");

        }
    };

    public ServerOS() {
    }

    public ServerOS(Server sv) {
        server = sv;
    }

    /**
     * 从镜像或快照创建云主机
     *
     * @param os
     * @param newServerName
     * @param flavorId
     * @param imageId
     * @param keypairsName
     * @param securityGroupName
     * @param networks
     * @return
     */
    public String createServer(OSClientV2 os, String newServerName, String flavorId, String imageId
            , String keypairsName, String securityGroupName, List<String> networks) {
        String excepStr = null;
        try {
            ServerCreate sc = Builders.server()
                    .name(newServerName)
                    .flavor(flavorId)
                    .image(imageId)
                    .keypairName(keypairsName)
                    .addSecurityGroup(securityGroupName)
                    .networks(networks)
                    .build();

            Server s = os.compute().servers().boot(sc);
            server = os.compute().servers().get(s.getId());
        } catch (Exception e) {
            excepStr = e.getMessage();
        }
        return excepStr;
    }

    /**
     * 获取租户ID
     *
     * @return
     */
    public String getTenantId() {
        String tenantId = server.getTenantId();
        return tenantId;
    }

    public Server getServer() {
        return server;
    }

    public String getServerId() {
        return server.getId();
    }

    public String getServerName() {
        return server.getName();
    }

    public String getImageName() {
        org.openstack4j.model.compute.Image i = server.getImage();
        if (i != null)
            return server.getImage().getName();
        else
            return "-";
    }

    public String getImageId() {
        org.openstack4j.model.compute.Image i = server.getImage();

        if (i != null) {
            return server.getImage().getId();
        } else {
            return "-";
        }

    }

    /**
     * 获取可用域
     *
     * @return
     */
    public String getAvailabilityZone() {
        return server.getAvailabilityZone();
    }

    /**
     * 返回ip类型和ip地址
     *
     * @return
     */
    public Map<String, String> getIPAddrs() {
        Map<String, List<? extends Address>> ad = server.getAddresses().getAddresses();
        if (ad.isEmpty()) {
            return new HashMap<String, String>();
        }
        Map<String, String> mp = new HashMap<>();
        for (String str : ad.keySet()) {
            List<? extends Address> ls = ad.get(str);
            for (Address as : ls) {
                mp.put(str, as.getAddr());
            }
        }
        return mp;
    }

    public Flavor getFlavor() {
        return server.getFlavor();
    }

    /**
     * 获得云主机状态中文名
     *
     * @return
     */
    public String getStatus() {
        String sts = server.getStatus().value();

        return status2China.get(sts);
    }

    /**
     * 返回创建时刻
     *
     * @return
     */
    public String getCreatedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String t = sdf.format(server.getCreated());
        return t;
    }

    /**
     * 返回运行时间
     *
     * @return
     */
    public String getTimeFromCreated() {
        long now = new Date().getTime();
        long created = server.getCreated().getTime();
        int minutes = (int) ((now - created) / (1000 * 60));
        int hours = minutes / 60;
        int days = hours / 24;
        int months = days / 30;
        int years = days / 365;

        if (years > 0) {
            int leftmonth = (days - years * 365) / 30;
            return years + "年" + leftmonth + "月";
        } else if (months > 0) {
            int leftDay = days - months * 30;
            return months + "月" + leftDay + "天";
        } else if (days > 0) {
            int leftHours = hours - days * 24;
            return days + "天" + leftHours + "小时";
        } else if (hours > 0) {
            int leftM = minutes - hours * 60;
            return hours + "小时" + leftM + "分钟";
        } else {
            return minutes + "分钟";
        }
    }

    /**
     * 获得主机
     *
     * @return
     */
    public String getHost() {
        return server.getHost();
    }

    public String getKeyName() {
        return server.getKeyName();
    }

    /**
     * 获取电源状态
     *
     * @return
     */
    public String getPowerState() {
        return server.getPowerState();
    }

    /**
     * 获取任务状态
     *
     * @return
     */
    public String getTaskState() {
        return server.getTaskState();
    }

    /**
     * server连接的云硬盘
     *
     * @param os
     * @return
     */
    public List<ServerVolume> getVolumesAttached(OSClientV2 os) {
        List<String> volumes = server.getOsExtendedVolumesAttached();
        List<ServerVolume> lsv = new ArrayList<>();
        for (String s : volumes) {
            Volume v = os.blockStorage().volumes().get(s);
            ServerVolume sv = new ServerVolume();

            sv.setVolumeId(s);
            sv.setVolumeName(v.getName());

            List<? extends VolumeAttachment> lva = v.getAttachments();
            List<String> device = new ArrayList<>();
            for (VolumeAttachment va : lva) {
                device.add(va.getDevice());
            }
            sv.setDevice(device);

            lsv.add(sv);
        }

        return lsv;
    }

    /**
     * 绑定浮动IP
     *
     * @param os
     * @param ipAddress
     * @return
     */
    public ActionResponse addFloatingIP(OSClientV2 os, String ipAddress) {
        return os.compute().floatingIps().addFloatingIP(server, ipAddress);
    }

    /**
     * 解绑定浮动IP
     *
     * @param os
     * @param ipAddress
     * @return
     */
    public ActionResponse removeFloatingIP(OSClientV2 os, String ipAddress) {
        return os.compute().floatingIps().removeFloatingIP(server, ipAddress);
    }


    /**
     * Pause the server
     *
     * @param os
     * @return
     */
    public ActionResponse pause(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.PAUSE);
    }

    /**
     * Un-Pause the server
     *
     * @param os
     * @return
     */
    public ActionResponse unPause(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.UNPAUSE);
    }

    /**
     * Stop the server
     *
     * @param os
     * @return
     */
    public ActionResponse stop(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.STOP);
    }

    /**
     * Start the server
     *
     * @param os
     * @return
     */
    public ActionResponse start(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.START);
    }

    /**
     * Lock the server
     *
     * @param os
     * @return
     */
    public ActionResponse lock(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.LOCK);
    }

    /**
     * Unlock a locked server
     *
     * @param os
     * @return
     */
    public ActionResponse unLock(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.UNLOCK);
    }

    /**
     * Suspend the server (different from pause)
     *
     * @param os
     * @return
     */
    public ActionResponse suspend(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.SUSPEND);
    }

    /**
     * Resume a suspended server
     *
     * @param os
     * @return
     */
    public ActionResponse resume(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.RESUME);
    }

    /**
     * Rescue a suspended server
     *
     * @param os
     * @return
     */
    public ActionResponse rescue(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.RESCUE);
    }

    /**
     * Un-Rescue the server
     *
     * @param os
     * @return
     */
    public ActionResponse unRescue(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.UNRESCUE);
    }


    /**
     * All associated data and resources are kept but anything still in memory is not retained.
     * To restore a shelved instance, use the unshelve action.
     * To remove a shelved instance, use the shelveOffload action.
     *
     * @param os
     * @return
     */
    public ActionResponse shelve(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.SHELVE);
    }

    /**
     * Remove a shelved instance from the compute node
     *
     * @param os
     * @return
     */
    public ActionResponse shelveOffLoad(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.SHELVE_OFFLOAD);
    }

    /**
     * Un-Shelve the server
     *
     * @param os
     * @return
     */
    public ActionResponse unShelve(OSClientV2 os) {
        return os.compute().servers().action(server.getId(), Action.UNSHELVE);
    }

    /**
     * soft reboot a Server（软重启）
     *
     * @param os
     * @return
     */
    public ActionResponse softReBootServer(OSClientV2 os) {
        return os.compute().servers().reboot(server.getId(), RebootType.SOFT);
    }

    /**
     * hard reboot a Server（硬重启）
     *
     * @param os
     * @return
     */
    public ActionResponse hardRebootServer(OSClientV2 os) {
        return os.compute().servers().reboot(server.getId(), RebootType.HARD);
    }

    /**
     * Resize a Server
     *
     * @param os
     * @param newFlavorId
     * @return
     */
    public ActionResponse resize(OSClientV2 os, String newFlavorId) {
        return os.compute().servers().resize(server.getId(), newFlavorId);
    }

    /**
     * Confirm a Resize
     *
     * @param os
     * @return
     */
    public ActionResponse comfirmResize(OSClientV2 os) {
        return os.compute().servers().confirmResize(server.getId());
    }

    /**
     * Revert(恢复) a Resize
     *
     * @param os
     * @return
     */
    public ActionResponse revertResize(OSClientV2 os) {
        return os.compute().servers().revertResize(server.getId());
    }


    /**
     * Create a new Server Snapshot 创建一个新的实例快照
     *
     * @param os
     * @param snapshotName
     * @return
     */
    public String createNewServerSnapshot(OSClientV2 os, String snapshotName) {
        return os.compute().servers().createSnapshot(server.getId(), snapshotName);
    }

    /**
     * Add a floating IP address to a server
     *
     * @param os
     * @param ipId
     * @return
     */
    public ActionResponse addFloatingIPToServer(OSClientV2 os, String ipId) {
        NetFloatingIP nfi = os.networking().floatingip().get(ipId);
        return os.compute().floatingIps().addFloatingIP(server, nfi.getFloatingIpAddress());
    }


    /**
     * Remove a floating IP address from a server
     *
     * @param os
     * @param ipAddress
     * @return
     */
    public ActionResponse removeFloatingIPFromServer(OSClientV2 os, String ipAddress) {
        return os.compute().floatingIps().removeFloatingIP(server, ipAddress);
    }

    /**
     * 返回VNC控制台（返回信息包括请求类型和url）
     *
     * @param os
     * @return
     */
    public VNCConsole getVNCConsole(OSClientV2 os) {
        return os.compute().servers().getVNCConsole(server.getId(), Type.NOVNC);
    }

    /**
     * return diagnostics(即实例的使用信息，包括CPU,memory,io)
     *
     * @param os
     * @return
     */
    public Map<String, ? extends Number> diagnostics(OSClientV2 os) {
        return os.compute().servers().diagnostics(server.getId());
    }

    /**
     * Delete a Server
     *
     * @param os
     */
    public void deleteServer(OSClientV2 os) {
        os.compute().servers().delete(server.getId());
    }

    /**
     * 更新云主机
     *
     * @param os
     * @param newName
     */
    public void editServer(OSClientV2 os, String newName) {

        os.compute().servers().update(server.getId(), ServerUpdateOptions.create().name(newName));
    }

    /**
     * 增加安全组
     *
     * @param os
     * @param secGroupName
     */
    public void addSecurityGroups(OSClientV2 os, String secGroupName) {
        os.compute().servers().addSecurityGroup(server.getId(), secGroupName);
    }

    /**
     * 删除安全组
     *
     * @param os
     * @param secGroupName
     */
    public void removeSecurityGroups(OSClientV2 os, String secGroupName) {
        os.compute().servers().removeSecurityGroup(server.getId(), secGroupName);
    }

    /**
     * 重建云主机
     *
     * @param os
     * @param imageId
     */
    public ActionResponse reBuildServer(OSClientV2 os, String imageId) {
        return os.compute().servers().rebuild(server.getId(), RebuildOptions.create().image(imageId));
    }

    /**
     * 迁移云主机
     *
     * @param os
     * @return
     */
    public ActionResponse migrate(OSClientV2 os) {
        return os.compute().servers().migrateServer(server.getId());
    }

    /**
     * 热迁移主机
     *
     * @param os
     * @param host
     * @param enabled
     * @param blocked
     * @return
     */
    public ActionResponse liveMigrate(OSClientV2 os, String host, boolean enabled, boolean blocked) {
        LiveMigrateOptions options = LiveMigrateOptions.create();
        options.diskOverCommit(enabled).blockMigration(blocked);
        return os.compute().servers().liveMigrate(server.getId(), options);
    }

    /**
     * 返回主机的元数据
     *
     * @param os
     * @return
     */
    public Map<String, String> listMetaData(OSClientV2 os) {
        Map<String, String> md = os.compute().servers().getMetadata(server.getId());
        return md;
    }

    /**
     * List all Servers detail
     *
     * @param os
     * @return
     */
    public List<? extends Server> listAllServersDetail(OSClientV2 os) {
        List<? extends Server> servers = os.compute().servers().list();

        return servers;
    }

    /**
     * List all Servers brief(仅包括ID, Name and Links)
     *
     * @param os
     * @return
     */
    public List<? extends Server> listAllServersBrief(OSClientV2 os) {
        List<? extends Server> servers = os.compute().servers().list(false);
        return servers;
    }

    /**
     * 列出所有租户的云主机
     *
     * @param os
     * @return
     */
    public List<? extends Server> listAllTenantServes(OSClientV2 os) {
        return os.compute().servers().listAll(true);
    }

    /**
     * Get a specific Server by ID
     *
     * @param os
     * @param serverId
     * @return
     */
    public Server getServerById(OSClientV2 os, String serverId) {
        return os.compute().servers().get(serverId);
    }

    /**
     * 列出所有的flavors
     *
     * @param os
     * @return
     */
    public List<? extends Flavor> listAllFlavors(OSClientV2 os) {
        List<? extends Flavor> flavors = os.compute().flavors().list();
        return flavors;
    }

    /**
     * 列出某个云主机的操作日志
     *
     * @param tenantId
     * @param tokenId
     * @param serverId
     * @return
     * @throws IOException
     */
    public String listActionsForServer(String tenantId, String tokenId, String serverId) throws IOException {
        String novaAPI = IdentityOS.getAccessAPI().get("nova") + "/" + tenantId;
        String strURL = novaAPI + "/servers/" + serverId + "/os-instance-actions";
        String result = null;
        try {
            result = HttpUtils.get(strURL, tokenId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode actions = rootNode.get("instanceActions");
        result = actions.toString();
        return result;
    }

    /**
     * 列出所有keypairs
     *
     * @param os
     * @return
     */
    public List<? extends Keypair> listAllKeypair(OSClientV2 os) {
        return os.compute().keypairs().list();
    }

    /**
     * 列出所有安全组
     *
     * @param os
     * @return
     */
    public List<? extends SecGroupExtension> listSecurityGroups(OSClientV2 os) {
        return os.compute().securityGroups().list();
    }


    //static String getString(String tokenId, String strURL, String result) {
    //    try {
    //        URL url = new URL(strURL);
    //        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //        connection.setDoOutput(true);
    //        connection.setDoInput(true);
    //        connection.setUseCaches(false);
    //        connection.setInstanceFollowRedirects(true);
    //        connection.setRequestMethod("GET");
    //        connection.setRequestProperty("X-Auth-Token", tokenId);
    //        connection.connect();
    //
    //        result = HttpUtils.readUrlStream(connection);
    //
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //    return result;
    //}
}
