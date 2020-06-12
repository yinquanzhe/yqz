package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.identityV3.UserListInfo;
import net.ahwater.ahwaterCloud.service.identityV3.UserOS;
import net.ahwater.ahwaterCloud.util.CommonUtils;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.User;
import org.openstack4j.model.identity.v2.builder.UserBuilder;
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
 * 用户管理控制类
 */
@RestController
@RequestMapping("/contr")
public class UserProcess {

    @Autowired
    private UserOS userOS;

    /**
     * 列出所有的用户信息（面向管理员）
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/ListAllUser")
    public List<UserListInfo> ListAllUser(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));//NullPointerException: null
        os.perspective(Facing.ADMIN);
        List<? extends User> userList = userOS.ListAllUser(os);
        List<UserListInfo> userListInfos = new ArrayList<>();
        for (User user : userList) {
            UserOS uos = new UserOS(user);
            UserListInfo info = new UserListInfo();
            info.setUserId(uos.getId());
            info.setUserName(uos.getName());
            info.setEmail(uos.getEmail());
            info.setEnabled(uos.isEnabled());
            info.setTenantId(uos.getTenantId());
            userListInfos.add(info);
        }

        return userListInfos;
    }

    /**
     * @param userId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/GetUserDetail")
    public UserListInfo GetUserDetail(String userId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        UserOS uos = new UserOS(os, userId);
        UserListInfo info = new UserListInfo();
        info.setUserId(uos.getId());
        info.setUserName(uos.getName());
        info.setEmail(uos.getEmail());
        info.setEnabled(uos.isEnabled());
        info.setTenantId(uos.getTenantId());

        return info;
    }

    /**
     * @param userInfo
     * @param password
     * @param roleName
     * @param session
     * @return
     */
    @RequestMapping("/CreateUser")
    public String CreateUser(UserListInfo userInfo, String password, String roleName, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        String errorMsg = "success";
        boolean isEnabled = false;
        if (userInfo.isEnabled())
            isEnabled = true;
        UserOS uos = new UserOS(os);
        try {
            uos.CreateUser(userInfo.getUserName(), userInfo.getEmail(), password, userInfo.getTenantId(), isEnabled);//创建用户
            uos.AssociateRoleToUser(roleName);//将用户与角色绑定
        } catch (Exception e) {
            errorMsg = "Failed to create a user : " + e.getMessage();
        }

        return errorMsg;
    }

    /**
     * @param userIds
     * @param session
     * @return
     * @throws IOException
     */
    @RequestMapping("/DeleteUser")
    public String DeleteUser(@RequestParam("userId") String userIds, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        ObjectMapper mapper = new ObjectMapper();
        List<String> userIdList = mapper.readValue(userIds, new TypeReference<List<String>>() {
        });
        int success = 0, failed = 0;
        for (String userId : userIdList) {
            UserOS uos = new UserOS(os);
            try {
                uos.DeleteUser(userId);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }
        String errorMsg = "删除成功 " + success + " 个，失败 " + failed + " 个！";
        return errorMsg;
    }

    /**
     * @param userId
     * @param userName
     * @param email
     * @param enabled
     * @param password
     * @param mainTenantId
     * @param session
     * @return
     */
    @RequestMapping("/UpdateUserInfo")
    public String UpdateUserInfo(String userId, String userName, String email, String enabled, String password, String mainTenantId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        //String userId="87f3395631fe4b97baf3f210cd2d2efd";
        User user = os.identity().users().get(userId);
        UserBuilder builder = user.toBuilder();
        String errorMsg = "success";
        try {
            //String userName="gwhTest";
            if (!CommonUtils.isEmpty(userName)) {
                builder.name(userName);
            }
            //String email="afasfsaf@qq.com";
            if (!CommonUtils.isEmpty(email)) {
                builder.email(email);
            }
            if (!CommonUtils.isEmpty(password)) {
                builder.password(password);
            }
            if (!CommonUtils.isEmpty(mainTenantId)) {
                builder.tenantId(mainTenantId);
            }
            if (!CommonUtils.isEmpty(enabled)) {
                boolean state = user.isEnabled();
                if (enabled.equals("true")) {
                    state = true;
                }
                if (enabled.equals("false")) {
                    state = true;
                }
                builder.enabled(state);
            }

            os.identity().users().update(builder.build());

        } catch (Exception ex) {
            errorMsg = "Failed to edit : " + ex.getMessage();
        }

        return errorMsg;
    }
}
