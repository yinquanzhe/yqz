package net.ahwater.ahwaterCloud.service.compute;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FloatingIpOS {

    FloatingIP floatingIP;

    public FloatingIpOS() {
    }

    public FloatingIpOS(FloatingIP floatingIP) {
        this.floatingIP = floatingIP;
    }

    public List<? extends FloatingIP> ListAllFloatingIp(OSClientV2 os) {
        return os.compute().floatingIps().list();
    }

    public void AllocateFloatingIp(OSClientV2 os, String pool) {
        os.compute().floatingIps().allocateIP(pool);
    }

    public ActionResponse DeallocateFloatingIp(OSClientV2 os, String id) {
        return os.compute().floatingIps().deallocateIP(id);
    }

    public ActionResponse AddFloatingIp(OSClientV2 os, String floatingIpAddress, String serverId) {
        Server server = os.compute().servers().get(serverId);
        return os.compute().floatingIps().addFloatingIP(server, floatingIpAddress);
    }

    public String getId() {
        return floatingIP.getId();
    }

    public String getFloatingIpAddress() {
        return floatingIP.getFloatingIpAddress();
    }

    public String getFixedIpAddress() {
        return floatingIP.getFixedIpAddress();
    }

    public String getPool() {
        return floatingIP.getPool();
    }

    public String getInstanceId() {
        return floatingIP.getInstanceId();
    }

    public boolean isUsable() {
        String instanceId = getInstanceId();
        if (instanceId == "" || instanceId == null)
            return true;
        else
            return false;
    }

    public List<String> GetPoolNames(OSClientV2 os) {
        return os.compute().floatingIps().getPoolNames();
    }

}
