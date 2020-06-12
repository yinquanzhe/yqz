package net.ahwater.ahwaterCloud.web.controller;

import net.ahwater.ahwaterCloud.entity.network.HostRouteInfo;
import net.ahwater.ahwaterCloud.entity.network.RouterBriefInfo;
import net.ahwater.ahwaterCloud.entity.network.RouterDetailInfo;
import net.ahwater.ahwaterCloud.service.network.RouterOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.network.HostRoute;
import org.openstack4j.model.network.Router;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 定义了对路由的相关操作
 *
 * @author dell
 */
@RestController
@RequestMapping("/contr")
public class RouterProcess {


    /**
     * 列出当前租户所有的路由
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/ListTenantRouter")
    public List<RouterBriefInfo> ListTenantRouter(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String currentTenantId = tkn.getTenant().getId();
        ;
        List<? extends Router> routerList = os.networking().router().list();
        List<RouterBriefInfo> routerBriefInfos = new ArrayList<>();
        for (Router router : routerList) {
            RouterOS ros = new RouterOS(router);
            if (!ros.getTenantId().equals(currentTenantId)) {
                continue;
            }
            RouterBriefInfo info = new RouterBriefInfo();
            info.setRouterId(ros.getId());
            info.setRouterName(ros.getName());
            info.setState(ros.getStatus().toString());
            String networkId = ros.getExternalGateway().getNetworkId();
            String networkName = os.networking().network().get(networkId).getName();
            info.setExternalNetworkName(networkName);
            if (ros.isAdminStateUp())
                info.setAdminState("up");
            else
                info.setAdminState("down");
            routerBriefInfos.add(info);
        }
        return routerBriefInfos;
    }

    /**
     * 列出所有路由，面向管理员账户
     *
     * @param session
     */
    @RequestMapping("/ListAllRouter")
    public List<RouterBriefInfo> ListAllRouter(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        List<? extends Router> routerList = os.networking().router().list();
        List<RouterBriefInfo> routerBriefInfos = new ArrayList<>();
        for (Router router : routerList) {
            RouterOS ros = new RouterOS(router);
            RouterBriefInfo info = new RouterBriefInfo();
            info.setRouterId(ros.getId());
            info.setRouterName(ros.getName());
            info.setState(ros.getStatus().toString());
            String networkId = ros.getExternalGateway().getNetworkId();
            String networkName = os.networking().network().get(networkId).getName();
            info.setExternalNetworkName(networkName);
            String tenantId = ros.getTenantId();
            String tenantName = os.identity().tenants().get(tenantId).getName();
            info.setProjectName(tenantName);
            if (ros.isAdminStateUp())
                info.setAdminState("up");
            else
                info.setAdminState("down");
            routerBriefInfos.add(info);
        }
        return routerBriefInfos;
    }


    @RequestMapping("/GetRouterDetail")
    public RouterDetailInfo GetRouterDetail(String routerId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        //String routerId="8f35d895-c635-41bb-8174-1f3a75b8b930";
        RouterOS ros = new RouterOS(os, routerId);
        RouterDetailInfo info = new RouterDetailInfo();
        info.setRouterId(ros.getId());
        info.setRouterName(ros.getName());
        info.setProjectId(ros.getTenantId());
        info.setState(ros.getStatus().toString());
        if (ros.isAdminStateUp())
            info.setAdminState("up");
        else
            info.setAdminState("down");
        String networkId = ros.getExternalGateway().getNetworkId();
        String networkName = os.networking().network().get(networkId).getName();
        info.setExternalNetworkName(networkName);
        info.setExternalNetworkId(networkId);
        if (ros.getExternalGateway().isEnableSnat()) {
            info.setSnat("激活");
        } else {
            info.setSnat("未激活");
        }

        return info;
    }

    @RequestMapping("/GetHostRoute")
    public List<HostRouteInfo> GetHostRoute(String routerId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        //String routerId="8f35d895-c635-41bb-8174-1f3a75b8b930";
        RouterOS ros = new RouterOS(os, routerId);
        List<? extends HostRoute> hostRoutes = ros.getRoutes();
        List<HostRouteInfo> hostRouteInfos = new ArrayList<>();
        for (HostRoute hostRoute : hostRoutes) {
            HostRouteInfo info = new HostRouteInfo();
            info.setDestination(hostRoute.getDestination());
            info.setNextHop(hostRoute.getNexthop());
            hostRouteInfos.add(info);
        }
        return hostRouteInfos;
    }
}
