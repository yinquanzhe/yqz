package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.identityV3.RoleListEle;
import net.ahwater.ahwaterCloud.service.identityV3.RoleOS;
import net.ahwater.ahwaterCloud.util.RespResult;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Role;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


/**
 * 角色控制类
 *
 * @author gwh
 */
@RestController
@RequestMapping("ctr")
public class RoleProcess {

    @Autowired
    private RoleOS roleOS;

    /**
     * 角色列表
     *
     * @param session
     */
    @RequestMapping("/listRoles")
    public List<RoleListEle> listRoles(HttpSession session) {
        //NullPointerException: null 原因:session超时到期导致原session失效，重新生成的session中并不包含登录时放入的信息
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);

        List<? extends Role> lrs = roleOS.listRoles(os);

        List<RoleListEle> lrle = new ArrayList<>();
        for (Role r : lrs) {
            RoleListEle rle = new RoleListEle();
            rle.setRoleId(r.getId());
            rle.setRoleName(r.getName());
            lrle.add(rle);
        }
        return lrle;
    }

    /**
     * 创建角色
     *
     * @param roleName
     * @param session
     */
    @RequestMapping("createRole")
    public String createRole(String roleName, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        String msg = roleOS.createRole(os, roleName);
        if (msg == null) {
            msg = "succ";
        }
        return msg;
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @param session
     */
    @RequestMapping("deleteRole")
    public RespResult deleteRole(String roleId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);

        RoleOS ro = new RoleOS(os.identity().roles().get(roleId));
        ro.deleteRole(os);

        return RespResult.success("删除成功");
    }

    /**
     * 批量删除角色
     *
     * @param roleIdListStr
     * @param session
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    @RequestMapping("PatchDeleteRole")
    public RespResult PatchDeleteRole(String roleIdListStr, HttpSession session) throws Exception {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);

        ObjectMapper mapper = new ObjectMapper();
        List<String> roleIdList = mapper.readValue(roleIdListStr, new TypeReference<List<String>>() {
        });

        roleOS.patchDeleteRole(os, roleIdList);

        return RespResult.success("批量删除成功");
    }

    /**
     * 编辑角色
     *
     * @param roleId
     * @param newRoleName
     * @param session
     * @throws Exception
     */
    @RequestMapping("/editRole")
    public RespResult editRole(String roleId, String newRoleName, HttpSession session) throws Exception {
        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String tokenId = tkn.getId();
        OSClientV2 os = OSFactory.clientFromAccess(ac);

        roleOS.editRole(os, tokenId, roleId, newRoleName);
        return RespResult.success("修改成功");
    }
}
