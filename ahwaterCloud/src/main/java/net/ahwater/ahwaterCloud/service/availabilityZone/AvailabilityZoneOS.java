package net.ahwater.ahwaterCloud.service.availabilityZone;

import net.ahwater.ahwaterCloud.util.HttpUtils;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Token;
import org.springframework.stereotype.Service;


/**
 * 可用域操作类
 */
@Service
public class AvailabilityZoneOS {
    public String listAvailabilityZone(Token token, OSClientV2 os, String tenantId) {
        String host = "http://ahwater-cloud-controller:8774/v2/";
        String urlst = host + tenantId + "/os-availability-zone/detail";
        String result = null;
        try {
            result = HttpUtils.get(urlst, token.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
