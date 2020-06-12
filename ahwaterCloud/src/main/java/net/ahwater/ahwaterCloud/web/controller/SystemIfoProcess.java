package net.ahwater.ahwaterCloud.web.controller;


import net.ahwater.ahwaterCloud.service.compute.systemInfo.ServiceOS;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


/**
 * 系统信息控制类
 *
 * @author gwh
 */
@RestController
@RequestMapping("/ctr")
public class SystemIfoProcess {

    @Autowired
    private ServiceOS serviceOS;

    /**
     * 列出所有服务
     *
     * @param session
     * @throws Exception
     */
    @RequestMapping("/listService")
    public String listService(HttpSession session) throws Exception {
        Access access = (Access) session.getAttribute("Access");
        KeystoneToken token = (KeystoneToken) access.getToken();//NullPointerException: null
        String tokenId = token.getId();
        return serviceOS.listService(tokenId);
    }

    /**
     * 列出所有计算服务
     *
     * @param session
     * @throws Exception
     */
    @RequestMapping("/listSysComputeService")
    public String listComputeService(HttpSession session) throws Exception {
        Access access = (Access) session.getAttribute("Access");
        KeystoneToken token = (KeystoneToken) access.getToken();
        String tokenId = token.getId();
        String tenantId = token.getTenant().getId();

        return serviceOS.listComputeServices(tenantId, tokenId);
    }
}
