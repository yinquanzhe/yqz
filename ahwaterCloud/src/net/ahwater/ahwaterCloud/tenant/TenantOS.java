package net.ahwater.ahwaterCloud.tenant;

import java.util.ArrayList;
import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.identity.v2.Role;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.model.identity.v2.TenantUser;

import net.ahwater.ahwaterCloud.tenant.entity.UserIdRoleId;
/**
 * 租户相关操作
 * @author zdf
 *
 */
public class TenantOS {
	private Tenant tenant;
	public TenantOS(){	
	}
	
	public TenantOS(Tenant tenant){
		this.tenant = tenant;
	}
	
	/**
	 * 列出云平台上的所有的租户
	 * @param os
	 * @return
	 */
	public static List<? extends Tenant> ListAllTenant(OSClientV2 os){
		List<? extends Tenant> tenants = os.identity().tenants().list();
		return tenants;
	}
	
	public String creatTenant(OSClientV2 os, String tenantName, String tenantDescription, boolean tenantEnable){
		String exceptionMessage = null;
		try{
			tenant = os.identity().tenants()
	                .create(Builders.identityV2().tenant()
	                              .name(tenantName)
	                              .description(tenantDescription)
	                              .enabled(tenantEnable)
	                              .build());
			exceptionMessage = "succ";
		}catch(Exception e){
			exceptionMessage = e.getMessage();
		}

		return exceptionMessage;
	}
	
	
	/**
	 * 添加一个用户
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public String tenantAddUser(String userId, String roleId){
		tenant.addUser(userId, roleId);
		return "succ";
	}
	
	
	/**
	 * 删除一个用户
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public String tenantDeleteUser(String userId, String roleId){
		tenant.removeUser(userId, roleId);
		return "succ";
	}
	

	/**
	 * 修改租户的名字和描述
	 * @param os
	 * @param newName
	 * @param newDescription
	 * @return
	 */
	public String editTenant(OSClientV2 os, String newName, String newDescription){
		String exceptionMessage = null;
		try{
			Tenant t = (Tenant) tenant.toBuilder().name(newName).description(newDescription).build();
			os.identity().tenants().update(t);
			exceptionMessage = "succ";
		}catch(Exception e){
			exceptionMessage = e.getMessage();
		}
		return exceptionMessage;
	}
	
	
	/**
	 * 删除租户
	 * @param os
	 * @param tenantId
	 * @return
	 */
	public String deleteTenant(OSClientV2 os, String tenantId){
		String exceptionMessage = null;
		ActionResponse response = os.identity().tenants().delete(tenantId);
		if(response.isSuccess()){
			exceptionMessage = "succ";
		}else{
			exceptionMessage = response.getFault();
		}
		return exceptionMessage;
	}
	
	/**
	 * 显示当前租户中的用户和角色
	 * @param os
	 * @param tenantId
	 * @return
	 */
	public List<UserIdRoleId> listuserIdRoleId(OSClientV2 os, String tenantId){
		List<UserIdRoleId> uIdrIds = new ArrayList<>();
		Tenant tenant = os.identity().tenants().get(tenantId);
		List<? extends TenantUser> users = os.identity().tenants().listUsers(tenantId);
		List<String> userIdsAgo = new ArrayList<>();
		for(TenantUser u:users){
//			userIdsAgo.add(u.getId());
			UserIdRoleId ur = new UserIdRoleId();
			String userId = u.getId();
			ur.setUserId(userId);
			List<? extends Role> roles = os.identity().roles().listRolesForUser(userId, tenantId);
			List<String> roleIds = new ArrayList<>();
			for(Role role:roles){
				String roleId = role.getId();
				roleIds.add(roleId);
			}
			ur.setRoleIds(roleIds);
			uIdrIds.add(ur);
		}
		return uIdrIds;
	}
}
