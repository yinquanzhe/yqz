package net.ahwater.ahwaterCloud.service.compute;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.service.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.HttpUtils;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


/**
 * 虚拟机管理器类
 *
 * @author gwh
 */
@Service
public class HypervisorsOS {
    /**
     * 虚拟机管理程序列表
     *
     * @param os
     * @return
     */
    public List<? extends Hypervisor> listHypervisors(OSClientV2 os) {

        return os.compute().hypervisors().list();
    }

    /**
     * 虚拟管理程序统计信息
     *
     * @param os
     * @return
     */
    public HypervisorStatistics getHypervisorStatistics(OSClientV2 os) {
        return os.compute().hypervisors().statistics();
    }

    /**
     * 列出一个主机下所有server
     *
     * @param tokenId
     * @param tenantId
     * @param hypervisorsId
     * @return
     * @throws IOException
     */
    public String listHypervisorServers(String tenantId, String tokenId, String hypervisorsId) throws IOException {
        String novaAPI = IdentityOS.getAccessAPI().get("nova") + "/" + tenantId;
        String strURL = novaAPI + "/os-hypervisors/" + hypervisorsId + "/servers";
        String result = null;
        try {
            result = HttpUtils.get(strURL, tokenId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode hyper = rootNode.get("hypervisors");
        JsonNode hyper1 = hyper.get(0);
        JsonNode servers = hyper1.get("servers");
        result = servers.toString();
        return result;
    }
}
