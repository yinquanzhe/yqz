package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.tenant.TenantElement;
import net.ahwater.ahwaterCloud.entity.tenant.UserIdRoleId;
import net.ahwater.ahwaterCloud.service.tenant.TenantOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Role;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.model.identity.v2.TenantUser;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 租户管理控制类
 */
@RestController
@RequestMapping("/cont")
public class TenantProcess {

    @Autowired
    private TenantOS tenantOS;

    /**
     * 列出所有的租户
     *
     * @param session
     */
    @RequestMapping("/listAllTenant")
    public List<TenantElement> listAllTenant(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));//NullPointerException: null
        os.perspective(Facing.ADMIN);
        List<? extends Tenant> tenants = tenantOS.ListAllTenant(os);
        List<TenantElement> tenantElements = new ArrayList<>();
        for (Tenant tenant : tenants) {
            TenantElement tenantElement = new TenantElement();
            tenantElement.setTenantId(tenant.getId());
            tenantElement.setTenantName(tenant.getName());
            tenantElement.setTenantDescription(tenant.getDescription());
            tenantElement.setTenantActive(tenant.isEnabled());
            tenantElements.add(tenantElement);
        }

        return tenantElements;
    }

    /**
     * 创建一个租户
     *
     * @param tenantName
     * @param tenantDescription
     * @param tenantEnableStr
     * @param session
     */
    @RequestMapping("/createTenant")
    public String createTenant(String tenantName, String tenantDescription, @RequestParam("tenantEnable") String tenantEnableStr, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        boolean tenantEnable = Boolean.parseBoolean(tenantEnableStr);
        TenantOS tos = new TenantOS();
        return tos.creatTenant(os, tenantName, tenantDescription, tenantEnable);
    }

    /**
     * 修改租户的名字和描述
     *
     * @param tenantId
     * @param newName
     * @param newDescription
     * @param session
     */
    @RequestMapping("/updateTenantNameDes")
    public String updateTenantNameDes(String tenantId, String newName, String newDescription, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        Tenant t = os.identity().tenants().get(tenantId);
        TenantOS tos = new TenantOS(t);
        return tos.editTenant(os, newName, newDescription);
    }

    /**
     * 添加用户
     *
     * @param request
     * @param response
     * @param session
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException          注：弃用
     */
    @RequestMapping("/addUsers")
    public void addUsers(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws JsonParseException, JsonMappingException, IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        KeystoneToken token = (KeystoneToken) os.getAccess().getToken();
//		Tenant tenant = token.getTenant();
        String tenantId = request.getParameter("tenantId");
        Tenant tenant = os.identity().tenants().get(tenantId);
        String userIdRoleStr = request.getParameter("userIdRoleStr");
        ObjectMapper mapper = new ObjectMapper();
        List<String> tenantIdRoles = mapper.readValue(userIdRoleStr, new TypeReference<List<String>>() {
        });
        TenantOS tos = new TenantOS(tenant);
        List<String> messages = new ArrayList<String>();
        for (String idRole : tenantIdRoles) {
            String userId = idRole.split(" ")[0];
            String roleId = idRole.split(" ")[1];
            String mess = tos.tenantAddUser(userId, roleId);
            messages.add(mess);
        }
        String msg = mapper.writeValueAsString(messages);
        PrintWriter out = null;
        response.setContentType("application/json");
        out = response.getWriter();
        out.write(msg);
    }

    /**
     * 删除用户（将租户中的用户从租户中删除）
     *
     * @param request
     * @param response
     * @param session
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException          注：弃用
     */
    @RequestMapping("/deleteUsers")
    public void deleteUsers(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws JsonParseException, JsonMappingException, IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        KeystoneToken token = (KeystoneToken) os.getAccess().getToken();
//		Tenant tenant = token.getTenant();
        String tenantId = request.getParameter("tenantId");
        Tenant tenant = os.identity().tenants().get(tenantId);
        String userIdRoleStr = request.getParameter("userIdRoleStr");
        ObjectMapper mapper = new ObjectMapper();
        List<String> tenantIdRoles = mapper.readValue(userIdRoleStr, new TypeReference<List<String>>() {
        });
        TenantOS tos = new TenantOS(tenant);
        List<String> messages = new ArrayList<String>();
        for (String idRole : tenantIdRoles) {
            String userId = idRole.split(" ")[0];
            String roleId = idRole.split(" ")[1];
            String mess = tos.tenantDeleteUser(userId, roleId);
            messages.add(mess);
        }
        String msg = mapper.writeValueAsString(messages);
        PrintWriter out = null;
        response.setContentType("application/json");
        out = response.getWriter();
        out.write(msg);
    }


    /**
     * 删除租户
     *
     * @param tenantId
     * @param session
     */
    @RequestMapping("/deleteTenant")
    public String deleteTenant(String tenantId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        return tenantOS.deleteTenant(os, tenantId);
    }

    /**
     * 删除多个租户
     *
     * @param tenantIdsStr
     * @param session
     * @throws IOException
     */
    @RequestMapping("/deleteMultiTenants")
    public List<String> deleteMultiTenants(@RequestParam("tenantIds") String tenantIdsStr, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        ObjectMapper mapper = new ObjectMapper();
        List<String> tenantIds = mapper.readValue(tenantIdsStr, new TypeReference<List<String>>() {
        });
        List<String> messages = new ArrayList<>();
        for (String tenantId : tenantIds) {
            String message = tenantOS.deleteTenant(os, tenantId);
            messages.add(message);
        }

        return messages;
    }

    /**
     * 显示选中的租户里包含的用户以及用户的角色
     *
     * @param tenantId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/listUserIdRoleId")
    public List<UserIdRoleId> listUserIdRoleId(String tenantId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        return tenantOS.listuserIdRoleId(os, tenantId);
    }

    /**
     * 添加或删除租户中的用户
     *
     * @param tenantId
     * @param userIdRoleIdstr
     * @param session
     * @throws IOException
     */
    @RequestMapping("/addDeleteUsers")
    public String addDeleteUsers(@RequestParam("tenantId") String tenantId, @RequestParam("tenantIdListStr") String userIdRoleIdstr, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        Tenant tenant = os.identity().tenants().get(tenantId);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode userIdRoleIdnodes = mapper.readTree(userIdRoleIdstr);
        List<UserIdRoleId> userIdRoleIds = new ArrayList<>();
        List<String> userIdsNew = new ArrayList<>();
        String messages;
        for (JsonNode node : userIdRoleIdnodes) {
            UserIdRoleId urId = new UserIdRoleId();
            String userId = node.get("userId").toString();
            userId = userId.substring(1, userId.length() - 1);
            userIdsNew.add(userId);
            urId.setUserId(userId);

            String roleIdsstr = node.get("roleId").toString();
            roleIdsstr = roleIdsstr.substring(1, roleIdsstr.length() - 1);

            String[] roleIds = roleIdsstr.split(",");
            urId.setRoleIds(Arrays.asList(roleIds));
            userIdRoleIds.add(urId);
        }

        List<? extends TenantUser> users = os.identity().tenants().listUsers(tenantId);
        List<String> userIdsAgo = new ArrayList<>();
        for (TenantUser u : users) {
            userIdsAgo.add(u.getId());
        }

        //删除用户
        Tenant t = os.identity().tenants().get(tenantId);
        TenantOS tos = new TenantOS(t);
        for (String userId : userIdsAgo) {
            List<? extends Role> roles = os.identity().roles().listRolesForUser(userId, tenantId);
            for (Role role : roles) {
                String roleId = role.getId();
                tos.tenantDeleteUser(userId, roleId);
            }
        }

        //添加用户
        for (String userId : userIdsNew) {
            List<String> roleids = new ArrayList<>();
            for (UserIdRoleId uidrid : userIdRoleIds) {
                if (uidrid.getUserId().equals(userId)) {
                    roleids = uidrid.getRoleIds();
                    break;
                }
            }
            for (String roleId : roleids) {
                tos.tenantAddUser(userId, roleId);
            }
        }

        messages = "succ";
        return messages;



/*
List<String> userIdsNewCopy = new ArrayList<>();
		userIdsNewCopy.addAll(userIdsNew);
		List<String> userIdsAgoCopy = new ArrayList<>();
		userIdsAgoCopy.addAll(userIdsAgo);

		userIdsNew.retainAll(userIdsAgo);//交集

		List<String> userIdIntersection = userIdsNew;

		userIdsNewCopy.removeAll(userIdsNew);//增加的(差集)
		userIdsAgoCopy.removeAll(userIdIntersection);//删除的(差集)

		//删除用户
		Tenant t = os.identity().tenants().get(tenantId);
		TenantOS tos = new TenantOS(t);
		for(String userId:userIdsAgoCopy){
			List<? extends Role> roles = os.identity().roles().listRolesForUser(userId, tenantId);
			for(Role role:roles){
				String roleId = role.getId();
				tos.tenantDeleteUser(userId, roleId);
			}
		}

		//添加用户
		for(String userId:userIdsNewCopy){
			List<String> roleids = new ArrayList<>();
			for(UserIdRoleId uidrid:userIdRoleIds){
				if(uidrid.getUserId().equals(userId)){
					roleids = uidrid.getRoleIds();
					break;
				}
			}
			for(String roleId:roleids){
				tos.tenantAddUser(userId, roleId);
			}
		}
		*/

//		for(String userId:userIdIntersection){
//			List<? extends Role> roles = os.identity().roles().listRolesForUser(userId, tenantId);
//			List<String> roleIdsAgo = new ArrayList<>();
//			List<String> roleIdsNew = new ArrayList<>();
//			for(Role role:roles){
//				roleIdsAgo.add(role.getId());
//			}
//			for(UserIdRoleId uidrid:userIdRoleIds){
//				if(uidrid.getUserId().equals(userId)){
//					roleIdsNew = uidrid.getRoleIds();
//					break;
//				}
//			}
//			List<String> roleIdsAgoCopy = new ArrayList<>();
//			List<String> roleIdsNewCopy = new ArrayList<>();
//			roleIdsAgoCopy.addAll(roleIdsAgo);
//			roleIdsNewCopy.addAll(roleIdsNew);
//			
//			roleIdsNew.retainAll(roleIdsAgoCopy); //交集
//			List<String> roleIdsIntersect = roleIdsNew;
//			
//			roleIdsNewCopy.removeAll(roleIdsIntersect);//添加的			
//			roleIdsAgoCopy.removeAll(roleIdsIntersect);//删除的
//			
//			for(String roleId:roleIdsNewCopy){      //添加用户（没有添加新的用户，只是在原有的用户的基础上添加指定角色）
//				tos.tenantAddUser(userId, roleId);
//			}
//			
//			for(String roleId:roleIdsAgoCopy){       //删除用户（用户还是以前的用户没被完全删除，只是删除指定的角色）
//				tos.tenantDeleteUser(userId, roleId);
//			}	
//		}

    }
}
