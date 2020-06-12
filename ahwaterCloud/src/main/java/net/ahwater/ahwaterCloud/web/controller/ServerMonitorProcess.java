package net.ahwater.ahwaterCloud.web.controller;

import net.ahwater.ahwaterCloud.entity.monitor.MonitorInfo;
import net.ahwater.ahwaterCloud.service.compute.ServerOS;
import net.ahwater.ahwaterCloud.service.monitor.ServerMonitor;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 云监控控制类
 *
 * @author gwh
 */
@RestController
@RequestMapping("/ctr")
public class ServerMonitorProcess {

    @Autowired
    private ServerOS serverOS;
    @Autowired
    private ServerMonitor serverMonitor;

    /**
     * 列出租户所有云主机的实时监控信息
     *
     * @param session
     */
    @RequestMapping("/listNewMonitor")
    public List<MonitorInfo> listNewMonitor(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Server> servers = serverOS.listAllServersDetail(os);

        List<String> uuidList = new ArrayList<>();
        for (Server s : servers) {
            uuidList.add(s.getId());
        }

        return serverMonitor.queryNewServerMonitorInfo(uuidList);
    }

    /**
     * 列出所有租户所有云主机的实时监控信息
     *
     * @param session
     */
    @RequestMapping("/listAllTenantNewMonitor")
    public List<MonitorInfo> listAllTenantNewMonitor(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        List<? extends Server> servers = serverOS.listAllTenantServes(os);

        List<String> uuidList = new ArrayList<>();
        for (Server s : servers) {
            uuidList.add(s.getId());
        }

        return serverMonitor.queryNewServerMonitorInfo(uuidList);
    }

    /**
     * 列出某个云主机的历史监控信息
     *
     * @param uuid
     * @param hours
     * @throws SQLException
     * @throws Exception
     */
    @RequestMapping("listOneServerMonitor")
    public List<MonitorInfo> listOneServerMonitor(String uuid, int hours) {
//		String uuid="51b17efb-8799-465f-b8f2-f0abf34f69f8";
//		int hours=1;
         return serverMonitor.queryOneServerMonitorInfo(uuid, hours);
    }

}
