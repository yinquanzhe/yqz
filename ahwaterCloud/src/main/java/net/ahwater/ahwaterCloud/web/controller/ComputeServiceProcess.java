package net.ahwater.ahwaterCloud.web.controller;

import net.ahwater.ahwaterCloud.service.compute.ComputeServiceOS;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


/**
 * 计算域服务控制类
 *
 * @author gwh
 */
@RestController
@RequestMapping("ctr")
public class ComputeServiceProcess {

    @Autowired
    private ComputeServiceOS computeServiceOS;

    /**
     * 列出所有计算服务
     *
     * @param session
     * @throws Exception
     */
    @RequestMapping("/listComputeServices")
    public String listComputeServices2(HttpSession session) throws Exception {
        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String tokenId = tkn.getId();
        String tenantId = tkn.getTenant().getId();

        return computeServiceOS.listComputeServices(tenantId, tokenId);
    }

    /**
     * 关闭服务
     *
     * @param host
     * @param reason
     * @param session
     * @throws Exception
     */
    @RequestMapping("/disableComputeService")
    public String disableComputeService(String host, String reason, HttpSession session) throws Exception {
        if (null == reason || reason.equals("")) {
            reason = "none";
        }

        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String tokenId = tkn.getId();
        String tenantId = tkn.getTenant().getId();

        return computeServiceOS.disableComputeService(tenantId, tokenId, host, reason);
    }

    /**
     * 打开服务
     *
     * @param host
     * @param session
     * @throws Exception
     */
    @RequestMapping("/enableComputeService")
    public String enableComputeService(String host,HttpSession session) throws Exception {
        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String tokenId = tkn.getId();
        String tenantId = tkn.getTenant().getId();

        return computeServiceOS.enableComputeService(tenantId, tokenId, host);
    }

}
