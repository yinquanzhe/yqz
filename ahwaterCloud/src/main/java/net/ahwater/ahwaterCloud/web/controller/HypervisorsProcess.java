package net.ahwater.ahwaterCloud.web.controller;

import net.ahwater.ahwaterCloud.entity.compute.VMManager.HypervisorListEle;
import net.ahwater.ahwaterCloud.entity.compute.VMManager.HypervisorStatisticsEle;
import net.ahwater.ahwaterCloud.service.compute.HypervisorsOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 虚拟机管理器控制类
 *
 * @author gwh
 */
@RestController
@RequestMapping("/ctr")
public class HypervisorsProcess {

    @Autowired
    private HypervisorsOS hypervisorsOS;

    /**
     * 输出虚拟机管理程序列表信息
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/listHypervisors")
    public List<HypervisorListEle> listHypervisors(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        List<? extends Hypervisor> lh = hypervisorsOS.listHypervisors(os);
        List<HypervisorListEle> lhe = new ArrayList<>();
        for (Hypervisor h : lh) {
            HypervisorListEle he = new HypervisorListEle();

            he.setId(h.getId());
            he.setHostname(h.getHypervisorHostname());
            he.setType(h.getType());
            he.setUsedVcpu(h.getVirtualUsedCPU());
            he.setVcpus(h.getVirtualCPU());
            he.setLocalMemoryUsed(Float.parseFloat(String.format("%.1f", h.getLocalMemoryUsed() * 1.0 / 1024)));
            he.setLocalMemory(Float.parseFloat(String.format("%.1f", h.getLocalMemory() * 1.0 / 1024)));
            he.setLocalDiskUsed(Float.parseFloat(String.format("%.1f", h.getLocalDiskUsed() * 1.0 / 1024)));
            he.setLocalDisk(Float.parseFloat(String.format("%.1f", h.getLocalDisk() * 1.0 / 1024)));
            he.setRunningVM(h.getRunningVM());

            lhe.add(he);
        }
        return lhe;
    }

    /**
     * 虚拟机管理器概述
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/getStatistics")
    public HypervisorStatisticsEle getStatistics(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        HypervisorStatistics hss = hypervisorsOS.getHypervisorStatistics(os);
        HypervisorStatisticsEle hse = new HypervisorStatisticsEle();
        hse.setVcspus(hss.getVirtualCPU());
        hse.setVcpusUsed(hss.getVirtualUsedCPU());
        hse.setMemory(Float.parseFloat(String.format("%.1f", hss.getMemory() * 1.0 / 1024)));
        hse.setMemoryUsed(Float.parseFloat(String.format("%.1f", hss.getMemoryUsed() * 1.0 / 1024)));
        hse.setLocalDisk(Float.parseFloat(String.format("%.1f", hss.getLocal() * 1.0 / 1024)));
        hse.setLocalDiskUsed(Float.parseFloat(String.format("%.1f", hss.getLocalUsed() * 1.0 / 1024)));

        return hse;
    }

    /**
     * 列出一个主机下所有云主机实例
     *
     * @param hypervisorsId
     * @param session
     * @throws Exception
     */
    @RequestMapping("/listHypervisorServers")
    public String listHypervisorServers(String hypervisorsId, HttpSession session) throws Exception {
        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String tokenId = tkn.getId();
        String tenantId = tkn.getTenant().getId();

        String msg = hypervisorsOS.listHypervisorServers(tenantId, tokenId, hypervisorsId);
        return msg;
    }
}
