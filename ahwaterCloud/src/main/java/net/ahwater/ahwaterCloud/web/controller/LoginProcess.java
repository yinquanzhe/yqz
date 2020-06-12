package net.ahwater.ahwaterCloud.web.controller;

import net.ahwater.ahwaterCloud.entity.identityV3.DefaultAndTenantList;
import net.ahwater.ahwaterCloud.entity.identityV3.LoginStatus;
import net.ahwater.ahwaterCloud.entity.identityV3.TenantNameId;
import net.ahwater.ahwaterCloud.service.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.CommonUtils;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Role;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 登录验证控制类
 *
 * @author gwh
 */
@RestController //controller里面的方法都以json格式输出
@RequestMapping("/ctr")
public class LoginProcess {

    @Autowired
    private IdentityOS identityOS;

    /**
     * 用户登录验证
     *
     * @param userName
     * @param pwd
     * @param session
     * @throws IOException
     */
    @RequestMapping("/LoginProcess")
    public LoginStatus login(@RequestParam("username") String userName, @RequestParam("userpwd") String pwd, HttpSession session) throws IOException {
        LoginStatus ls = new LoginStatus();

        if(CommonUtils.isEmpty(userName) || CommonUtils.isEmpty(pwd)){
            ls.setStatu("fail");
            return ls;
        }
        IdentityOS uc = new IdentityOS(userName, pwd);
        if (uc.init()) {
            ls.setStatu("succ");
            if (uc.isAdmin()) {
                ls.setIsAdmin("admin");
            } else {
                ls.setIsAdmin("user");
            }
        } else {
            ls.setStatu("fail");
        }

        if (ls.getStatu().equals("succ")) {
            session.setAttribute("Access", uc.getOSClientV2().getAccess());
            session.setAttribute("userName", userName);
            session.setAttribute("pwd", pwd);
            session.setAttribute("defaultTenant", uc.getDefaultTenant());

            List<? extends Tenant> tenants = uc.getTenants();
            List<TenantNameId> tenantNameIds = new ArrayList<>();
            for (Tenant t : tenants) {
                TenantNameId tni = new TenantNameId();
                tni.setTenantId(t.getId());
                tni.setTenantName(t.getName());

                tenantNameIds.add(tni);
            }
            session.setAttribute("tenantNameIds", tenantNameIds);
        }

        return ls;
    }

    /**
     * 切换租户
     *
     * @param tenantName
     * @param userName
     * @param pwd
     * @param session
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping("/changeTenant")
    public String changeTenant(String tenantName, String userName, String pwd, HttpSession session) throws Exception {
        String endpoint = identityOS.getAccessAPI().get("keystone");

        OSClientV2 os = OSFactory.builderV2()
                .endpoint(endpoint)
                .credentials(userName, pwd)
                .tenantName(tenantName)
                .authenticate();
        session.setAttribute("Access", os.getAccess());

        List<? extends Role> rl = os.getAccess().getUser().getRoles();

        boolean isAdmin = false;
        if (rl.size() == 1) {
            Iterator<? extends Role> it = rl.iterator();
            if (it.next().getName().equals("admin")) {
                isAdmin = true;
            }
        } else {
            for (Role r : rl) {
                if (r.getName().equals("admin")) {
                    isAdmin = true;
                }
            }
        }

        String msg = "user";
        if (isAdmin) {
            msg = "admin";
        }
        return msg;
    }

    /**
     * 用户的租户列表
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/tenantsListOfUser")
    public DefaultAndTenantList tenantsListOfUser(HttpSession session) {

        Tenant defaultTetant = (Tenant) session.getAttribute("defaultTenant");
        @SuppressWarnings("unchecked")
        List<TenantNameId> tenantNames = (List<TenantNameId>) session.getAttribute("tenantNameIds");

        DefaultAndTenantList adtl = new DefaultAndTenantList();
        adtl.setDefaultName(defaultTetant.getName());
        adtl.setDefaultId(defaultTetant.getId());
        adtl.setTenantNames(tenantNames);

        return adtl;
    }

}
