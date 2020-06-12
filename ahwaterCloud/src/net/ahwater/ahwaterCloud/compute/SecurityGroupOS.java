package net.ahwater.ahwaterCloud.compute;

import java.util.List;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.compute.SecGroupExtension.Rule;

/**
 * 定义了安全组的创建、删除、编辑以及规则的操作
 * @author dell
 *
 */
public class SecurityGroupOS {
	private SecGroupExtension group;
	public SecurityGroupOS(){}
	public SecurityGroupOS(SecGroupExtension group)
	{
		this.group=group;
	}
	public SecurityGroupOS(OSClientV2 os,String securityGroupId)
	{
		this.group=os.compute().securityGroups().get(securityGroupId);
	}
	/**
	 * 获取安全组列表
	 * @param os
	 * @return
	 */
	public static List<? extends SecGroupExtension> ListAllSecurityGroup(OSClientV2 os)
	{
		return os.compute().securityGroups().list();
	}
	
	public void CreateSecurityGroup(OSClientV2 os,String name,String description)
	{
		group=os.compute().securityGroups().create(name, description);
	}
	
	public void UpdateSecurityGroup(OSClientV2 os,String securityGroupId,String name,String description)
	{
		group=os.compute().securityGroups().update(securityGroupId, name, description);
	}
	
	public ActionResponse DeleteSecurityGroup(OSClientV2 os,String securityGroupId)
	{
		return os.compute().securityGroups().delete(securityGroupId);
	}
	
	public static List<? extends SecGroupExtension> ListServerGroup(OSClientV2 os,String serverId)
	{
		return os.compute().securityGroups().listServerGroups(serverId);
	}
	
	public List<? extends Rule> getRules()
	{
		return group.getRules();
	}
	
	public String getId()
	{
		return group.getId();
	}
	
	public String getName()
	{
		return group.getName();
	}
	
	public String getDescription()
	{
		return group.getDescription();
	}
	
	public String getTenantId()
	{
		return group.getTenantId();
	}
	
}
