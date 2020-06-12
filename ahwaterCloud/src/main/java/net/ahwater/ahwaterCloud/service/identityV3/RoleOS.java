package net.ahwater.ahwaterCloud.service.identityV3;

import net.ahwater.ahwaterCloud.entity.identityV3.RoleParam;
import net.ahwater.ahwaterCloud.entity.identityV3.UpdateRole;
import net.ahwater.ahwaterCloud.util.HttpUtils;
import net.ahwater.ahwaterCloud.util.JsonUtils;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.identity.v2.Role;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


/**
 * 角色类
 *
 * @author gwh
 */
@Service
public class RoleOS {

    Role role;

    public RoleOS() {
    }

    public RoleOS(Role role) {
        this.role = role;
    }

    /**
     * 创建角色
     *
     * @param os
     * @param roleName
     * @return
     */
    public String createRole(OSClientV2 os, String roleName) {
        String excepStr = null;
        try {
            role = os.identity().roles().create(roleName);
        } catch (Exception e) {
            excepStr = e.getMessage();
        }
        return excepStr;
    }

    /**
     * 编辑角色
     *
     * @param os
     * @param newRoleName
     * @throws IOException
     */
    public String editRole(OSClientV2 os, String tokenId, String roleId, String newRoleName) throws IOException {
        String keystone = IdentityOS.getAccessAPI().get("keystone");
        keystone = keystone.replaceAll("v2.0", "v3");
        String strURL = keystone + "/roles/" + roleId;
        String result = null;

        UpdateRole updateRoleParam = new UpdateRole();
        RoleParam rp = new RoleParam();
        rp.setName(newRoleName);
        updateRoleParam.setRole(rp);

        //ObjectMapper mapper = new ObjectMapper();
        //String param = null;
        //try {
        //    param = mapper.writeValueAsString(uptateRoleParam);
        //} catch (JsonProcessingException e1) {
        //    e1.printStackTrace();
        //}
        //
        //String result = null;
        //HttpClient httpClient = new DefaultHttpClient();
        //HttpPatch httpPatch = new HttpPatch(strURL);
        //httpPatch.setHeader("Content-type", "application/json");
        //httpPatch.setHeader("Charset", HTTP.UTF_8);
        //httpPatch.setHeader("Accept", "application/json");
        //httpPatch.setHeader("Accept-Charset", HTTP.UTF_8);
        //httpPatch.setHeader("X-Auth-Token", tokenId);
        //try {
        //    StringEntity entity = new StringEntity(param, HTTP.UTF_8);
        //    httpPatch.setEntity(entity);
        //
        //    HttpResponse response = httpClient.execute(httpPatch);
        //    result = EntityUtils.toString(response.getEntity());
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        try {
            result = HttpUtils.patch(strURL, tokenId, JsonUtils.toJson(updateRoleParam));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * 删除角色
     *
     * @param os
     * @return
     */
    public ActionResponse deleteRole(OSClientV2 os) {
        ActionResponse ac = os.identity().roles().delete(role.getId());
        return ac;
    }

    /**
     * 批量删除角色
     *
     * @param os
     * @param roleIds
     */
    public void patchDeleteRole(OSClientV2 os, List<String> roleIds) {
        for (String id : roleIds) {
            os.identity().roles().delete(id);
        }
    }

    /**
     * 角色列表
     *
     * @param os
     * @return
     */
    public List<? extends Role> listRoles(OSClientV2 os) {
        return os.identity().roles().list();
    }
}
