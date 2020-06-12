package net.ahwater.ahwaterCloud.identityV3;

import java.util.List;


import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.identity.v2.Role;
import org.openstack4j.model.identity.v2.User;

public class UserOS {
	User user;
	OSClientV2 os;
	public UserOS(OSClientV2 os)
	{
		this.os=os;
	}
	public UserOS(OSClientV2 os,String userId)
	{
		this.os=os;
		user=os.identity().users().get(userId);
	}
	public UserOS(User user)
	{
		this.user=user;
	}
	public static List<?extends User> ListAllUser(OSClientV2 os)
	{
		return os.identity().users().list();
	}
	public List<?extends User> ListTenantUser(String tenantId)
	{
		return os.identity().users().listTenantUsers(tenantId);
	}
	public String getId()
	{
		return user.getId();
	}
	public String getName()
	{
		return user.getName();
	}
	public String getEmail()
	{
		return user.getEmail();
	}
	public String getTenantId()
	{
		return user.getTenantId();
	}
	public boolean isEnabled()
	{
		return user.isEnabled();
	}
	public ActionResponse DeleteUser(String userId)
	{
		return os.identity().users().delete(userId);
	}
	public void CreateUser(String name,String email,String password,String tenantId,boolean enabled )
	{
		user=os.identity().users().create(tenantId,name,  password,email,  enabled);
	}
	public void AssociateRoleToUser(String roleName)
	{
		Role memberRole=os.identity().roles().getByName(roleName);
		os.identity().roles().addUserRole(user.getTenantId(), user.getId(),memberRole.getId());
	}
	public void updateName(String name)
	{
		user=os.identity().users().update(user.toBuilder().name(name).build());
	}
	public void updateEmail(String email)
	{
		user=os.identity().users().update(user.toBuilder().email(email).build());
	}
	public void updatePassword(String password)
	{
		user=os.identity().users().update(user.toBuilder().password(password).build());
	}
	public void updateState(Boolean enabled)
	{
		user=os.identity().users().update(user.toBuilder().enabled(enabled).build());
	}
	public void updateTenantId(String tenantId)
	{
		user=os.identity().users().update(user.toBuilder().tenantId(tenantId).build());
	}
}
