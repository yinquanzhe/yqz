/**
 *
 */
package net.ahwater.ahwaterCloud.service.network;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.ExternalGateway;
import org.openstack4j.model.network.HostRoute;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.State;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 定义了路由的创建、删除和查询操作
 *
 * @author dell
 */
@Service
public class RouterOS {

    Router router;


    public RouterOS() {
    }

    public RouterOS(Router router) {
        this.router = router;
    }

    public RouterOS(OSClientV2 os, String routerId) {
        router = os.networking().router().get(routerId);
    }

    /**
     * 获取路由列表
     */
    public static List<? extends Router> ListAllRouter(OSClientV2 os) {
        return os.networking().router().list();
    }

    public ExternalGateway getExternalGateway() {
        return router.getExternalGatewayInfo();
    }

    public String getId() {
        return router.getId();
    }

    public String getName() {
        return router.getName();
    }

    public State getStatus() {
        return router.getStatus();
    }

    public String getTenantId() {
        return router.getTenantId();
    }

    public boolean isAdminStateUp() {
        return router.isAdminStateUp();
    }

    public List<? extends HostRoute> getRoutes() {
        return router.getRoutes();
    }

}
