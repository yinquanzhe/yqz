package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.network.*;
import net.ahwater.ahwaterCloud.service.network.NetworkOS;
import net.ahwater.ahwaterCloud.service.network.PortOS;
import net.ahwater.ahwaterCloud.service.network.SubnetOS;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;
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
 * 网络（network）控制类
 * 接收前端对网络的操作的请求，调用相关模块完成响应
 *
 * @author dell
 */

@RestController
@RequestMapping("/cont")
public class NetworkProcess {

    @Autowired
    private NetworkOS networkOS;
    @Autowired
    private SubnetOS subnetOS;

    /**
     * 列出当前租户拥有的网络
     *
     * @param session
     */
    @RequestMapping("/listAllNetwork")
    public List<NetworkElement> listAllNetwork(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        KeystoneToken token = (KeystoneToken) os.getAccess().getToken();
        String tenantId = token.getTenant().getId();

        List<? extends Network> networks = networkOS.listAllNetwork(os);
        List<NetworkElement> nets = new ArrayList<>();
        for (Network n : networks) {
            if (n.isShared() || n.getTenantId().equals(tenantId)) {
                NetworkElement net = new NetworkElement();
                NetworkOS nos = new NetworkOS(n);
                net.setNetworkId(nos.getNetworkId());
                net.setNetworkName(nos.getNetworkName());
                net.setNetworkSubnet(nos.getSubnetId(), os);
                net.setNetworkIsShared(nos.isNetworkIsShared());
                net.setNetworkState(nos.getNetworkState());
                net.setNetworkIsAdminStateUp(nos.isNetworkIsAdminStateUp());
                nets.add(net);
            }
        }
        return nets;
    }

    /**
     * 列出网络的详细信息
     *
     * @param networkId
     * @param session
     */
    @RequestMapping("/networkDetail")
    public NetworkDetail networkDetail(String networkId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Network network = os.networking().network().get(networkId);
        NetworkDetail networkDetail = new NetworkDetail();
        networkDetail.setIsExternal(network.isRouterExternal());
        networkDetail.setNetworkId(network.getId());
        networkDetail.setNetworkIsAdminStateUp(network.isAdminStateUp());
        networkDetail.setNetworkIsShared(network.isShared());
        networkDetail.setNetworkName(network.getName());
        networkDetail.setNetworkState(network.getStatus());
        networkDetail.setNetworkType(network.getNetworkType());
        networkDetail.setPhycialNetwork(network.getProviderPhyNet());
        networkDetail.setSegId(network.getProviderSegID());
        networkDetail.setTenantId(network.getTenantId());

        return networkDetail;
    }


    /**
     * 传递网络的Name和Id，用于创建server
     *
     * @param tenantId
     * @param session
     */
    @RequestMapping("/listNetworkNameId")
    public List<NetworkNameId> listNetworkNmaeId(String tenantId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Network> networks = networkOS.listAllNetwork(os);
        List<NetworkNameId> netNameIds = new ArrayList<>();
        for (Network n : networks) {
            if (n.isShared() || n.getTenantId().equals(tenantId)) {
                NetworkNameId netNameId = new NetworkNameId();
                NetworkOS nos = new NetworkOS(n);
                netNameId.setNetworkName(nos.getNetworkName());
                netNameId.setNetworkId(nos.getNetworkId());
                netNameIds.add(netNameId);
            }
        }
        return netNameIds;
    }

    @RequestMapping("/createNetwork")
    public void createNetwork(NetworkCreateInf nci, HttpSession session) {

        String networkName = nci.getNetworkName();
        boolean networkIsShared = nci.isNetworkIsShared();
        boolean NetworkIsAdminStateUp = nci.isNetworkIsAdminStateUp();

        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        Network network = os.networking().network()
                .create(Builders.network().name("test").adminStateUp(true).isShared(true).build());
    }


    /**
     * 列出一个网络下的所有的端口
     *
     * @param networkId
     * @param session
     */
    @RequestMapping("/listPorts")
    public List<PortElement> listPorts(String networkId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Port> ports = PortOS.listAllPorts(os, networkId);
        List<PortElement> pos = new ArrayList<>();
        for (Port port : ports) {
            PortElement po = new PortElement();
            po.setPortName(port.getName());
            po.setPortFixedIps(port.getFixedIps());
            po.setPortDevice(port.getDeviceId());
            po.setPortState(port.getState());
            po.setPortisAdminStateUp(port.isAdminStateUp());
            po.setPortId(port.getId());
            pos.add(po);
        }
        return pos;
    }

    /**
     * 更改端口的名字
     *
     * @param newName
     * @param portId
     * @param session
     */
    @RequestMapping("/updatePortName")
    public String updatePortName(String newName, String portId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Port port = os.networking().port().get(portId);
        PortOS pos = new PortOS(port);
        String message = pos.updatePortName(os, newName);
        return message;
    }


    /**
     * 列出一个网络下的所有的子网
     *
     * @param networkId
     * @param session
     */
    @RequestMapping("/listSubnet")
    public List<SubnetElement> listSubnet(String networkId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<String> subnetIds = subnetOS.listSubnet(os, networkId);
        List<SubnetElement> subneteles = new ArrayList<>();
        for (String id : subnetIds) {
            Subnet subnet = os.networking().subnet().get(id);
            SubnetElement subnetele = new SubnetElement();
            subnetele.setGateway(subnet.getGateway());
            subnetele.setIpVersion(subnet.getIpVersion().toString());
            subnetele.setSubnetCidr(subnet.getCidr());
            subnetele.setSubNetId(id);
            subnetele.setSubnetName(subnet.getName());
            subneteles.add(subnetele);
        }
        return subneteles;
    }


    /**
     * 子网详情
     *
     * @param subnetId
     * @param session
     */
    @RequestMapping("/subnetDetail")
    public SubnetDetail subnetDetail(String subnetId, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Token token = os.getAccess().getToken();
        String result = subnetOS.subnetDetail(token, os, subnetId);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode subnetJson = rootNode.get("subnet");
        SubnetDetail subnetd = new SubnetDetail();
        subnetd.setEnableDHCP(subnetJson.get("enable_dhcp").toString());
        subnetd.setGateway(subnetJson.get("gateway_ip").toString());
        JsonNode allocation_poolsJson = subnetJson.get("allocation_pools");
        String pools = allocation_poolsJson.toString();
        pools = pools.substring(1, pools.length() - 1);
        allocation_poolsJson = mapper.readTree(pools);
        String poolend = allocation_poolsJson.get("end").toString();
        poolend = poolend.substring(1, poolend.length() - 1);
        subnetd.setIPEnd(poolend);
        String poolstart = allocation_poolsJson.get("start").toString();
        poolstart = poolstart.substring(1, poolstart.length() - 1);
        subnetd.setIPStart(poolstart);
        subnetd.setIpVersion(subnetJson.get("ip_version").toString());
        String network_id = subnetJson.get("network_id").toString();
        network_id = network_id.substring(1, network_id.length() - 1);
        subnetd.setNetworkId(network_id);
        String cidr = subnetJson.get("cidr").toString();
        subnetd.setSubnetCidr(cidr.substring(1, cidr.length() - 1));
        String id = subnetJson.get("id").toString();
        subnetd.setSubNetId(id.substring(1, id.length() - 1));

        String name = subnetJson.get("name").toString();
        subnetd.setSubnetName(name.substring(1, name.length() - 1));
        String subnetpool_id = subnetJson.get("subnetpool_id").toString();
        subnetd.setSubnetPoolId(subnetpool_id.substring(1, subnetpool_id.length() - 1));

        String dns_nameservers = subnetJson.get("dns_nameservers").toString();
        subnetd.setDns_nameservers(dns_nameservers.substring(2, dns_nameservers.length() - 2));

        return subnetd;
    }


    /**
     * 列出云平台上的所有的网络
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/adminListAllNetworks")
    public List<NetworkElement> adminListAllNetworks(HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        KeystoneToken token = (KeystoneToken) os.getAccess().getToken();
        String tenantId = token.getTenant().getId();

        List<? extends Network> networks = networkOS.listAllNetwork(os);
        List<NetworkElement> nets = new ArrayList<>();
        for (Network n : networks) {
            NetworkElement net = new NetworkElement();
            NetworkOS nos = new NetworkOS(n);
            net.setNetworkId(nos.getNetworkId());
            net.setNetworkName(nos.getNetworkName());
            net.setNetworkSubnet(nos.getSubnetId(), os);
            net.setNetworkIsShared(nos.isNetworkIsShared());
            net.setNetworkState(nos.getNetworkState());
            net.setNetworkIsAdminStateUp(nos.isNetworkIsAdminStateUp());
            tenantId = nos.getNetworkTenantId();
            net.setTenantName(os, tenantId);
            nets.add(net);
        }
        return nets;
    }
}
