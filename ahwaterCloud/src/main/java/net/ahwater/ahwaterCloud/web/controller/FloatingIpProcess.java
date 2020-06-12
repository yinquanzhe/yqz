package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.compute.FloatingIpInfo;
import net.ahwater.ahwaterCloud.service.compute.FloatingIpOS;
import net.ahwater.ahwaterCloud.util.JsonUtils;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 浮动IP控制类
 */
@RestController
@RequestMapping("/contr")
public class FloatingIpProcess {

    @Autowired
    private FloatingIpOS floatingIpOS;

    /**
     * 获取浮动IP列表
     *
     * @param session
     */
    @RequestMapping("/ListAllFloatingIp")
    public List<FloatingIpInfo> ListAllFloatingIppublic(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends FloatingIP> floatingIpList = floatingIpOS.ListAllFloatingIp(os);
        List<FloatingIpInfo> floatingIpInfoList = new ArrayList<>();
        for (FloatingIP fip : floatingIpList) {
            FloatingIpInfo fIpInfo = new FloatingIpInfo();
            FloatingIpOS fipos = new FloatingIpOS(fip);
            fIpInfo.setId(fipos.getId());
            fIpInfo.setFloatingIpAddress(fipos.getFloatingIpAddress());
            fIpInfo.setFixedIpAddress(fipos.getFixedIpAddress());
            fIpInfo.setPool(fipos.getPool());
            if (fipos.isUsable())
                fIpInfo.setState("未使用");
            else
                fIpInfo.setState("已使用");
            floatingIpInfoList.add(fIpInfo);
        }
        return floatingIpInfoList;
    }

    /**
     * 分配浮动IP给项目
     *
     * @param pool
     * @param session
     * @throws IOException
     */
    @RequestMapping("/AllocateFloatingIp")
    public String AllocateFloatingIp(String pool, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String errorMsg = "success";
        try {
            floatingIpOS.AllocateFloatingIp(os, pool);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        String msg = JsonUtils.toJson(errorMsg);
        System.out.println("-----------water.com-----------msg值=" + msg + "," + "当前类=FloatingIpProcess.AllocateFloatingIp()");
        return msg;
    }

    /**
     * 释放浮动IP
     *
     * @param ids
     * @param session
     * @throws IOException
     */
    @RequestMapping("/DeallocateFloatingIp")
    public String DeallocateFloatingIp(@RequestParam("id") String ids, HttpSession session) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> idList = mapper.readValue(ids, new TypeReference<List<String>>() {
        });
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        int success = 0, failed = 0;
        for (String id : idList) {
            try {
                floatingIpOS.DeallocateFloatingIp(os, id);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }
        String errorMsg = "删除成功 " + success + " 个，失败 " + failed + " 个！";
        System.out.println("-----------water.com-----------errorMsg值=" + errorMsg + "," + "当前类=FloatingIpProcess.DeallocateFloatingIp()");
        return errorMsg;
    }

    /**
     * 获取资源池名称列表
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/GetPoolNames")
    public List<String> GetPoolNames(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return floatingIpOS.GetPoolNames(os);
    }

    /**
     * 关联IP,即将动态IP与主机绑定
     *
     * @param floatingIpAddress
     * @param serverId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/AddFloatingIp")
    public String AddFloatingIp(String floatingIpAddress, String serverId, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String errorMsg = "success";
        try {
            floatingIpOS.AddFloatingIp(os, floatingIpAddress, serverId);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        String msg = JsonUtils.toJson(errorMsg);
        System.out.println("-----------water.com-----------msg值=" + msg + "," + "当前类=FloatingIpProcess.AddFloatingIp()");
        return msg;
    }
}
