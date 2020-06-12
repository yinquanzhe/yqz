package net.ahwater.ahwaterCloud.web.controller;

import net.ahwater.ahwaterCloud.entity.summary.QuotaEntity;
import net.ahwater.ahwaterCloud.entity.summary.ServerBriefInfo;
import net.ahwater.ahwaterCloud.entity.summary.TenantUsageInfo;
import net.ahwater.ahwaterCloud.entity.summary.UsageAndLimitInfo;
import net.ahwater.ahwaterCloud.service.compute.ServerOS;
import net.ahwater.ahwaterCloud.service.summary.QuotaOs;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.compute.AbsoluteLimit;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;
import org.openstack4j.model.compute.SimpleTenantUsage;
import org.openstack4j.model.compute.SimpleTenantUsage.ServerUsage;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.model.network.NetQuota;
import org.openstack4j.model.storage.block.BlockLimits.Absolute;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 概况页相关操作，包括查询配额，和按时间查询
 *
 * @author dell
 */
@RestController
@RequestMapping("/contr")
public class QuotaProcess {

    @Autowired
    private ServerOS serverOS;
    @Autowired
    private QuotaOs quotaOs;

    /**
     * 获取当前租户配额及使用情况
     *
     * @param session
     */
    @RequestMapping("/GetUsageAndLimit")
    public UsageAndLimitInfo GetUsageAndLimit(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Access ac = (Access) session.getAttribute("Access");
        os.perspective(Facing.PUBLIC);
        KeystoneToken token = (KeystoneToken) os.getAccess().getToken();
        String tenantId = token.getTenant().getId();
        Absolute volunmLimit = os.blockStorage().getLimits().getAbsolute();//磁盘配额对象
        AbsoluteLimit limit = os.compute().quotaSets().limits().getAbsolute();//nova配额对象
        NetQuota netQuota = os.networking().quotas().get(tenantId);
        UsageAndLimitInfo info = new UsageAndLimitInfo();
        info.setUsedInstances(String.valueOf(limit.getTotalInstancesUsed()));
        info.setMaxInstances(String.valueOf(limit.getMaxTotalInstances()));
        info.setUsedRAMSize(String.valueOf(limit.getTotalRAMUsed()));
        info.setMaxRAMSize(String.valueOf(limit.getMaxTotalRAMSize()));
        info.setUsedCores(String.valueOf(limit.getTotalCoresUsed()));
        info.setMaxCores(String.valueOf(limit.getMaxTotalCores()));

        info.setUsedVolumes(String.valueOf(volunmLimit.getTotalVolumesUsed()));
        info.setMaxVolumes(String.valueOf(volunmLimit.getMaxTotalVolumes()));
        info.setUsedVolumeSize(String.valueOf(volunmLimit.getTotalGigabytesUsed()));
        info.setMaxVolumeSize(String.valueOf(volunmLimit.getMaxTotalVolumeGigabytes()));
//		List<?extends NetFloatingIP> fip=os.networking().floatingip().list();
//		List<? extends SecurityGroup> sg=os.networking().securitygroup().list();
        info.setUsedFloatingIPs(String.valueOf(os.compute().floatingIps().list().size()));
        info.setMaxFloatingIPs(String.valueOf(netQuota.getFloatingIP()));
        info.setUsedSecurityGroups(String.valueOf(os.compute().securityGroups().list().size()));
        info.setMaxSecurityGroups(String.valueOf(netQuota.getSecurityGroup()));

        return info;
    }

    /**
     * 按时间查询当前租户主机使用情况
     *
     * @param startTime
     * @param endTime
     * @param session
     */
    @RequestMapping("/ListServersByTime")
    public List<ServerBriefInfo> ListServersByTime(String startTime, String endTime, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        startTime += " 00:00:00";
        endTime += " 23:59:59";
        List<Server> serverListbyTime = new ArrayList<>();
        List<? extends Server> serverList = serverOS.listAllServersDetail(os);
        List<ServerBriefInfo> serverInfoList = new ArrayList<>();
        try {
            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = fmt.parse(startTime);
            Date end = fmt.parse(endTime);
            for (Server s : serverList) {
                if (s.getCreated().after(start) && s.getCreated().before(end))//判断主机创建时间是否在查询时间范围内
                    serverListbyTime.add(s);
            }
        } catch (Exception e) {
        }
        for (Server s : serverListbyTime) {
            ServerOS sOs = new ServerOS(s);
            ServerBriefInfo serverInfo = new ServerBriefInfo();
            serverInfo.setServerId(sOs.getServerId());
            serverInfo.setServerName(sOs.getServerName());
            serverInfo.setVcpus(sOs.getFlavor().getVcpus());
            serverInfo.setDisk(sOs.getFlavor().getDisk());
            serverInfo.setRam(sOs.getFlavor().getRam());
            serverInfo.setTimeFromCreated(sOs.getTimeFromCreated());
            serverInfoList.add(serverInfo);
        }
        return serverInfoList;
    }

    /**
     * 按时间查询所有租户的配额及使用情况
     *
     * @param startTime
     * @param endTime
     * @param session
     */
    @RequestMapping("/ListTenantUsages")
    public List<TenantUsageInfo> ListTenantUsages(String startTime, String endTime, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        startTime += "T00:00:00";
        endTime += "T23:59:59";
        List<? extends Tenant> tenants = os.identity().tenants().list();//获取租户列表
        List<TenantUsageInfo> tenantUsageInfos = new ArrayList<>();
        for (Tenant tenant : tenants) {
            String tenantId = tenant.getId();
            String tenantName = tenant.getName();
            SimpleTenantUsage usage = os.compute().quotaSets().getTenantUsage(tenantId, startTime, endTime);
//			if(usage.getTotalHours()==null)//过滤使用时间为空的租户
//				continue;
            TenantUsageInfo info = new TenantUsageInfo();
            int cores = 0;
            int ram_Mb = 0;
            int diskSize_Gb = 0;
            try {
                info.setTenantName(tenant.getName());
                double runHours = Double.parseDouble(usage.getTotalHours());//运行时间
                double memoryMbUsage = usage.getTotalMemoryMbUsage().doubleValue();
                double vcpuUsage = usage.getTotalVcpusUsage().doubleValue();
                double diskGbUsage = usage.getTotalLocalGbUsage().doubleValue();
                info.setRunHours(runHours);
                info.setVcpuUsage(vcpuUsage);
                info.setMemoryMbUsage(memoryMbUsage);
                info.setDiskGbUsage(diskGbUsage);
                List<? extends ServerUsage> serverUsages = usage.getServerUsages();

                for (ServerUsage su : serverUsages) {
                    Status status = su.getState();
                    //需要统计的主机状态
                    if (status == Status.ACTIVE) {
                        cores += su.getVcpus();
                        ram_Mb += su.getMemoryMb();
                        diskSize_Gb += su.getLocalDiskSize();
                    }
                }
            } catch (Exception e) {
            }
            info.setVcpu(cores);
            info.setMemory(ram_Mb);
            info.setDiskSize(diskSize_Gb);
            tenantUsageInfos.add(info);
        }
        return tenantUsageInfos;
    }

    /**
     * 获取默认值
     */
    @RequestMapping("/GetLimits")
    public QuotaEntity GetLimits() throws IOException {
        return quotaOs.GetLimits();
    }

    /**
     * 设置默认值
     *
     * @param limit
     */
    @RequestMapping("/SetLimits")
    public String SetLimits(QuotaEntity limit) {
        String errorMsg = "success";
        try {
            quotaOs.SetLimits(limit);
        } catch (Exception e) {
            errorMsg = "error:" + e.getMessage();
        }
        return errorMsg;
    }

    /**
     * 更新租户配额(面向管理员)
     *
     * @param limit
     * @param tenantId
     * @param session
     */
    @RequestMapping("/UpdateTenantQuota")
    public String UpdateTenantQuota(QuotaEntity limit, String tenantId, HttpSession session) {
        //String tenantId="ab89008e0122476ead8220a71b43eb43";
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String errorMsg = "success";
        try {
            quotaOs.UpdateTenantQuota(os, tenantId, limit);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        return errorMsg;
    }

    /**
     * 获取租户配额(面向管理员)
     *
     * @param tenantId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/GetTenantQuota")
    public QuotaEntity GetTenantQuota(String tenantId, HttpSession session) {
        //String tenantId="37a76b53fc744bbaac5bc46d6ae99f2a";
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return quotaOs.GetTenantQuota(os, tenantId);
    }
}