package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.compute.VMManager.FlavorAllListEle;
import net.ahwater.ahwaterCloud.entity.compute.VMManager.FlavorCreateIfo;
import net.ahwater.ahwaterCloud.entity.compute.VMManager.FlavorEditIfo;
import net.ahwater.ahwaterCloud.entity.compute.VMManager.TenantListEle;
import net.ahwater.ahwaterCloud.service.compute.FlavorOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 云资源模板控制类
 *
 * @author gwh
 */

@RestController
@RequestMapping("/ctr")
public class FlavorProcess {

    @Autowired
    private FlavorOS flavorOS;

    /**
     * 云主机类型列表
     *
     * @param session
     * @throws Exception
     */
    @RequestMapping("/listAllFlavors")
    public List<FlavorAllListEle> listAllFlavors(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Flavor> lfr = flavorOS.listPublicAndNonpublicFlavors(os);

        List<FlavorAllListEle> lfale = new ArrayList<>();
        for (Flavor f : lfr) {
            FlavorOS fo = new FlavorOS(f);
            FlavorAllListEle fale = new FlavorAllListEle();
            fale.setName(f.getName());
            fale.setVcpus(f.getVcpus());
            fale.setRam(f.getRam());
            fale.setDisk(f.getDisk());
            fale.setEphemeral(f.getEphemeral());
            fale.setSwap(f.getSwap());
            fale.setID(f.getId());
            fale.setPublic(f.isPublic());
            fale.setExtraSpecs(fo.listExtraSpecsForAFlavor(os));

            lfale.add(fale);
        }
        return lfale;
    }

    /**
     * 删除云主机类型
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/deleteFlavor")
    public String deleteFlavor(String flavorId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        FlavorOS fo = new FlavorOS(os.compute().flavors().get(flavorId));
        ActionResponse ac = fo.deleteFlavor(os);

        String msg;
        if (ac.getFault() == null) {
            msg = "succ";
        } else {
            msg = ac.getFault();
        }
        return msg;
    }

    /**
     * 批量删除云主机
     *
     * @param flavorIdListStr
     * @param session
     * @throws IOException
     */
    @RequestMapping("/patchDeleteFlavor")
    public String patchDeleteFlavor(String flavorIdListStr, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        ObjectMapper mapper = new ObjectMapper();
        List<String> flavorIdList = mapper.readValue(flavorIdListStr, new TypeReference<List<String>>() {
        });

        for (String flavorId : flavorIdList) {
            FlavorOS fo = new FlavorOS(os.compute().flavors().get(flavorId));
            fo.deleteFlavor(os);
        }
        return "succ";
    }

    /**
     * 创建云主机类型
     *
     * @param info
     * @param session
     * @throws Exception
     */
    @RequestMapping("/createFlavor")
    public String createFlavor(FlavorCreateIfo info, HttpSession session) throws Exception {
        String flavorName = info.getFlavorName();
        int ram = info.getRam();
        int vcpus = info.getVcpus();
        int disk = info.getDisk();
        int ephemeral = info.getEphemeral();
        int swap = info.getSwap();
        String tenantIdListStr = info.getTenantIdListStr();

        boolean isPublic = true;
        if (tenantIdListStr != null && !tenantIdListStr.equals("")) {
            isPublic = false;
        }
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String msg = flavorOS.createFlavor(os, flavorName, ram, vcpus, disk, ephemeral, swap, isPublic);

        process(tenantIdListStr, os, flavorOS, isPublic);

        if (msg == null) {
            msg = "succ";
        }
        return msg;
    }


    /**
     * 云主机类型现有的租户
     *
     * @param flavorId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/haveTenantList")
    public List<TenantListEle> haveTenantList(String flavorId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        FlavorOS fo = new FlavorOS(os.compute().flavors().get(flavorId));

        return fo.haveTenantList(os);
    }

    /**
     * 修改使用权
     *
     * @param tenantIdListStr
     * @param flavorId
     * @param session
     * @throws Exception
     */
    @RequestMapping("/changeTenantAccess")
    public String changeTenantAccess(String tenantIdListStr, String flavorId, HttpSession session) throws Exception {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        FlavorOS fo = new FlavorOS(os.compute().flavors().get(flavorId));

        ObjectMapper mapper = new ObjectMapper();
        List<String> newTenantIdList = mapper.readValue(tenantIdListStr, new TypeReference<List<String>>() {
        });
        List<String> oldTenantIdList = fo.listTenantAccess(os);
        List<String> newTenantIdListCpy = new ArrayList<>(newTenantIdList);

        newTenantIdList.removeAll(oldTenantIdList);
        oldTenantIdList.removeAll(newTenantIdListCpy);

        for (String id : newTenantIdList) {
            fo.addTenantAccess(os, id);
        }
        for (String id : oldTenantIdList) {
            fo.removeTenantAccess(os, id);
        }

        return "succ";
    }

    /**
     * 列出所有的租户
     *
     * @param session
     * @throws Exception
     */
    @RequestMapping("/listAllTenants")
    public List<TenantListEle> listAllTenants(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);

        return flavorOS.listAllTenant(os);
    }

    /**
     * 编辑云主机类型
     *
     * @param info
     * @param session
     * @throws IOException
     */
    @RequestMapping("/editFlavor")
    public String editFlavor(FlavorEditIfo info, HttpSession session) throws IOException {
        String flavorID = info.getFlavorId();
        String flavorName = info.getFlavorName();
        int ram = info.getRam();
        int vcpus = info.getVcpus();
        int disk = info.getDisk();
        int ephemeral = info.getEphemeral();
        int swap = info.getSwap();
        String tenantIdListStr = info.getTenantIdListStr();

        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);

        FlavorOS fo = new FlavorOS(os.compute().flavors().get(flavorID));
        fo.deleteFlavor(os);

        boolean isPublic = false;
        if (tenantIdListStr.equals("[]") || tenantIdListStr == null || tenantIdListStr.equals("")) {
            isPublic = true;
        }

        String msg = fo.createFlavor(os, flavorName, ram, vcpus, disk, ephemeral, swap, isPublic);

        process(tenantIdListStr, os, fo, isPublic);

        if (msg == null) {
            msg = "succ";
        }
        return msg;
    }

    private void process(String tenantIdListStr, OSClientV2 os, FlavorOS fo, boolean isPublic) throws IOException {
        if (isPublic == false) {
            ObjectMapper mapper = new ObjectMapper();
            List<String> newTenantIdList = mapper.readValue(tenantIdListStr, new TypeReference<List<String>>() {
            });
            List<String> oldTenantIdList = fo.listTenantAccess(os);
            List<String> newTenantIdListCpy = new ArrayList<>(newTenantIdList);

            newTenantIdList.removeAll(oldTenantIdList);
            oldTenantIdList.removeAll(newTenantIdListCpy);

            for (String id : newTenantIdList) {
                fo.addTenantAccess(os, id);
            }
            for (String id : oldTenantIdList) {
                fo.removeTenantAccess(os, id);
            }
        }
    }
}
