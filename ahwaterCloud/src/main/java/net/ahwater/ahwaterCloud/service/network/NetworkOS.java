package net.ahwater.ahwaterCloud.service.network;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.State;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 网络的相关操作
 *
 * @author zdf
 */
@Service
public class NetworkOS {

    private Network network;

    public NetworkOS() {
    }

    public NetworkOS(Network network) {
        this.network = network;
    }

    /**
     * 列出所有的网络
     *
     * @param os
     * @return
     */
    public List<? extends Network> listAllNetwork(OSClientV2 os) {
        List<? extends Network> networks = os.networking().network().list();
        return networks;
    }

    public String getNetworkName() {
        return network.getName();
    }

    public String getNetworkId() {
        return network.getId();
    }

    //获取对应的租户的Id
    public String getNetworkTenantId() {
        return network.getTenantId();
    }


    public List<String> getSubnetId() {
        return network.getSubnets();
    }

    public boolean isNetworkIsShared() {
        return network.isShared();
    }

    public State getNetworkState() {
        return network.getStatus();
    }

    public boolean isNetworkIsAdminStateUp() {
        return network.isAdminStateUp();
    }


    //	public Network creatNetwork(String networkName, String ){
    //
    //		return network;
    //	}

    //	public {
    //		return network.
    //	}
}
