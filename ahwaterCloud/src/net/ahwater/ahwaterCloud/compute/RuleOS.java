package net.ahwater.ahwaterCloud.compute;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.IPProtocol;
import org.openstack4j.model.compute.builder.SecurityGroupRuleBuilder;
import org.openstack4j.model.network.SecurityGroupRule;
import org.openstack4j.model.network.builder.NetSecurityGroupRuleBuilder;

import net.ahwater.ahwaterCloud.compute.entity.RuleCreationInfo;

public class RuleOS {
	SecurityGroupRule rule;
	public RuleOS(){}
	public RuleOS(SecurityGroupRule rule)
	{
		this.rule=rule;
	}
	public static SecurityGroupRule CreateRule(OSClientV2 os,RuleCreationInfo info)
	{
		SecurityGroupRule rule=null;
		try
		{
		NetSecurityGroupRuleBuilder builder=Builders.securityGroupRule();
		
		builder=builder.securityGroupId(info.getSecurityGroupId())//所属安全组
								.direction(info.getDirection())//方向
								.protocol(info.getProtocol());//协议类型
		//设置端口范围
		builder=builder.portRangeMax(info.getPortRangeMax())
								.portRangeMin(info.getPortRangeMin());
		
		if(info.isCustomisedIcmp()){
			//TODO: no API
		}
		//设置CIDR或安全组
		if(info.isCIDR()){
			builder=builder.remoteIpPrefix(info.getRemoteIpPrefix());
		}
		else {
			builder=builder.remoteGroupId(info.getRemoteGroupId())
								    .ethertype(info.getEtherType());
		}
		rule=os.networking().securityrule().create(builder.build());
		}
		catch (Exception e) {
			
		}
		return rule;
	}
	
	public static void DeleteRule(OSClientV2 os,String ruleId)
	{
		os.compute().securityGroups().deleteRule(ruleId);
	}
	public String getId()
	{
		return rule.getId();
	}
	public String getDirection()
	{
		return rule.getDirection();
	}
	public String getEtherType()
	{
		return rule.getEtherType();
	}
	public String getProtocol()
	{
		return rule.getProtocol();
	}
	public int getPortRangeMax()
	{
		if(rule.getPortRangeMax()==null)
			return 65535;
		else
			return rule.getPortRangeMax();
	}
	public int getPortRangeMin()
	{
		if(rule.getPortRangeMin()==null)
			return 0;
		else
			return rule.getPortRangeMin();
	}
	public String getRemoteGroupId()
	{
		return rule.getRemoteGroupId();
	}
	public String getRemoteIpPrefix()
	{
		return rule.getRemoteIpPrefix();
	}
	public String getSecurityGroupId()
	{
		return rule.getSecurityGroupId();
	}
	public String getTenantId()
	{
		return rule.getTenantId();
	}
	public String getPortRangeStr()
	{
		int portMax=getPortRangeMax();
		int portMin=getPortRangeMin();
		if(portMin==portMax)
			return String.valueOf(portMin);
		else
			return String.valueOf(portMin)+"~"+String.valueOf(portMax);
	}
}
