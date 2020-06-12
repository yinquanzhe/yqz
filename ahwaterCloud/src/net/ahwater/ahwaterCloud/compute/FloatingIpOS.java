package net.ahwater.ahwaterCloud.compute;

import java.util.List;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;

public class FloatingIpOS {
	FloatingIP floatingIP;
	public FloatingIpOS(){}
	public FloatingIpOS(FloatingIP floatingIP)
	{
		this.floatingIP=floatingIP;
	}
	public static List<?extends FloatingIP> ListAllFloatingIp(OSClientV2 os)
	{
		return os.compute().floatingIps().list();
	}
	
	public static void AllocateFloatingIp(OSClientV2 os,String pool)
	{
		os.compute().floatingIps().allocateIP(pool);
	}
	public static ActionResponse DeallocateFloatingIp(OSClientV2 os,String id)
	{
		return os.compute().floatingIps().deallocateIP(id);
	}
	public static ActionResponse AddFloatingIp(OSClientV2 os,String floatingIpAddress,String serverId)
	{
		Server server=os.compute().servers().get(serverId);
		return os.compute().floatingIps().addFloatingIP(server, floatingIpAddress);
	}
	public String getId()
	{
		return floatingIP.getId();
	}
	public String getFloatingIpAddress()
	{
		return floatingIP.getFloatingIpAddress();
	}
	public String getFixedIpAddress()
	{
		return floatingIP.getFixedIpAddress();
	}
	public String getPool()
	{
		return floatingIP.getPool();
	}
	public String getInstanceId()
	{
		return floatingIP.getInstanceId();
	}
	public boolean isUsable()
	{
		String instanceId=getInstanceId();
		if(instanceId==""||instanceId==null)
			return true;
		else
			return false;
	}
	public static List<String> GetPoolNames(OSClientV2 os) {
		return os.compute().floatingIps().getPoolNames();
	}
	
}
