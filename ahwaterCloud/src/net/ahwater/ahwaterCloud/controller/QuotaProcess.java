package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.compute.AbsoluteLimit;
import org.openstack4j.model.compute.QuotaSet;
import org.openstack4j.model.compute.QuotaSetUpdate;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;
import org.openstack4j.model.compute.SimpleTenantUsage;
import org.openstack4j.model.compute.SimpleTenantUsage.ServerUsage;
import org.openstack4j.model.compute.builder.QuotaSetUpdateBuilder;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.network.NetQuota;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.builder.NetQuotaBuilder;
import org.openstack4j.model.storage.block.BlockLimits.Absolute;
import org.openstack4j.model.storage.block.BlockQuotaSet;
import org.openstack4j.model.storage.block.builder.BlockQuotaSetBuilder;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.ServerOS;
import net.ahwater.ahwaterCloud.compute.entity.os_getConsoleOutput;
import net.ahwater.ahwaterCloud.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.summary.QuotaOs;
import net.ahwater.ahwaterCloud.summary.entity.QuotaEntity;
import net.ahwater.ahwaterCloud.summary.entity.ServerBriefInfo;
import net.ahwater.ahwaterCloud.summary.entity.TenantUsageInfo;
import net.ahwater.ahwaterCloud.summary.entity.UsageAndLimitInfo;

@Controller
@RequestMapping("/contr")

/**
 * 概况页相关操作，包括查询配额，和按时间查询
 * @author dell
 *
 */
public class QuotaProcess {
	/**
	 * 获取当前租户配额及使用情况
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/GetUsageAndLimit")
	public void GetUsageAndLimit(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Access ac=(Access) session.getAttribute("Access");
		os.perspective(Facing.PUBLIC);
		KeystoneToken token = (KeystoneToken)os.getAccess().getToken();
		String tenantId=token.getTenant().getId();
		Absolute volunmLimit=os.blockStorage().getLimits().getAbsolute();//磁盘配额对象
		AbsoluteLimit limit=os.compute().quotaSets().limits().getAbsolute();//nova配额对象
		NetQuota netQuota=os.networking().quotas().get(tenantId);
		UsageAndLimitInfo info=new UsageAndLimitInfo();
		info.setUsedInstances(String.valueOf(limit.getTotalInstancesUsed()));
		info.setMaxInstances(String.valueOf(limit.getMaxTotalInstances()));
		info.setUsedRAMSize(String.valueOf(limit.getTotalRAMUsed()));
		info.setMaxRAMSize(String.valueOf(limit.getMaxTotalRAMSize()));
		info.setUsedCores(String.valueOf(limit.getTotalCoresUsed()));
		info.setMaxCores(String.valueOf(limit.getMaxTotalCores()));
		
		info.setUsedVolumes(String.valueOf(volunmLimit.getTotalVolumesUsed()));
		info.setMaxVolumes(String.valueOf(volunmLimit.getMaxTotalVolumes()));
		info.setUsedVolumeSize(String.valueOf(volunmLimit.getTotalGigabytesUsed()));
		info.setMaxVolumeSize(String.valueOf(volunmLimit.getMaxTotalVolumeGigabytes()));
//		List<?extends NetFloatingIP> fip=os.networking().floatingip().list();
//		List<? extends SecurityGroup> sg=os.networking().securitygroup().list();
		info.setUsedFloatingIPs(String.valueOf(os.compute().floatingIps().list().size()));
		info.setMaxFloatingIPs(String.valueOf(netQuota.getFloatingIP()));
		info.setUsedSecurityGroups(String.valueOf(os.compute().securityGroups().list().size()));
		info.setMaxSecurityGroups(String.valueOf(netQuota.getSecurityGroup()));
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(info);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 按时间查询当前租户主机使用情况
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListServersByTime")
	public void ListServersByTime(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		startTime+=" 00:00:00";
		endTime+=" 23:59:59";
		List<Server> serverListbyTime=new ArrayList<Server>();
		List<?extends Server> serverList=ServerOS.listAllServersDetail(os);
		List<ServerBriefInfo> serverInfoList=new ArrayList<ServerBriefInfo>();
		try{
			DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start=fmt.parse(startTime);
			Date end=fmt.parse(endTime);
			for(Server s:serverList)
			{
				if(s.getCreated().after(start)&&s.getCreated().before(end))//判断主机创建时间是否在查询时间范围内
					serverListbyTime.add(s);
			}
		}
		catch(Exception e)
		{
		}
		for(Server s:serverListbyTime)
		{
			ServerOS sOs=new ServerOS(s);
			ServerBriefInfo serverInfo=new ServerBriefInfo();
			serverInfo.setServerId(sOs.getServerId());
			serverInfo.setServerName(sOs.getServerName());
			serverInfo.setVcpus(sOs.getFlavor().getVcpus());
			serverInfo.setDisk(sOs.getFlavor().getDisk());
			serverInfo.setRam(sOs.getFlavor().getRam());
			serverInfo.setTimeFromCreated(sOs.getTimeFromCreated());
			serverInfoList.add(serverInfo);
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(serverInfoList);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 按时间查询所有租户的配额及使用情况
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListTenantUsages")
	public void ListTenantUsages(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		startTime+="T00:00:00";
		endTime+="T23:59:59";
		List<?extends Tenant> tenants=os.identity().tenants().list();//获取租户列表
		List<TenantUsageInfo> tenantUsageInfos=new ArrayList<TenantUsageInfo>();
		for(Tenant tenant:tenants)
		{
			String tenantId=tenant.getId();
			String tenantName=tenant.getName();
			SimpleTenantUsage usage=os.compute().quotaSets().getTenantUsage(tenantId, startTime, endTime);
//			if(usage.getTotalHours()==null)//过滤使用时间为空的租户
//				continue;
			TenantUsageInfo info=new TenantUsageInfo();
			int cores=0;
			int ram_Mb=0;
			int diskSize_Gb=0;
			try{
			info.setTenantName(tenant.getName());
			double runHours=Double.parseDouble(usage.getTotalHours());//运行时间
			double memoryMbUsage=usage.getTotalMemoryMbUsage().doubleValue();
			double vcpuUsage=usage.getTotalVcpusUsage().doubleValue();
			double diskGbUsage=usage.getTotalLocalGbUsage().doubleValue();
			info.setRunHours(runHours);
			info.setVcpuUsage(vcpuUsage);
			info.setMemoryMbUsage(memoryMbUsage);
			info.setDiskGbUsage(diskGbUsage);
			List<?extends ServerUsage> serverUsages=usage.getServerUsages();
			
			for(ServerUsage su:serverUsages)
			{
				Status status=su.getState();
				//需要统计的主机状态
				if(status==Status.ACTIVE)
				{
				cores+=su.getVcpus();
				ram_Mb+=su.getMemoryMb();
				diskSize_Gb+=su.getLocalDiskSize();
				}
			}
			}
			catch (Exception e) {
			}
			info.setVcpu(cores);
			info.setMemory(ram_Mb);
			info.setDiskSize(diskSize_Gb);
			tenantUsageInfos.add(info);
		}

		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(tenantUsageInfos);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 获取默认值
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/GetLimits")
	public void GetLimits(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		
		QuotaEntity limit=QuotaOs.GetLimits();
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(limit);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 设置默认值
	 * @param limit
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/SetLimits")
	public void SetLimits(QuotaEntity limit,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String errorMsg="success";
		try
		{
			QuotaOs.SetLimits(limit);
		}
		catch (Exception e) {
			errorMsg="error:"+e.getMessage();
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(errorMsg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 更新租户配额(面向管理员)
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/UpdateTenantQuota")
	public void UpdateTenantQuota(QuotaEntity limit,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String tenantId = request.getParameter("tenantId");
		//String tenantId="ab89008e0122476ead8220a71b43eb43";
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String errorMsg="success";
		try
		{
		QuotaOs.UpdateTenantQuota(os, tenantId, limit);
		}
		catch (Exception e) {
			errorMsg=e.getMessage();
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(errorMsg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 获取租户配额(面向管理员)
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/GetTenantQuota")
	public void GetTenantQuota(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String tenantId = request.getParameter("tenantId");
		//String tenantId="37a76b53fc744bbaac5bc46d6ae99f2a";
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		QuotaEntity limit=QuotaOs.GetTenantQuota(os, tenantId);
	    ObjectMapper mapper=new ObjectMapper();
	    String msg = mapper.writeValueAsString(limit);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	
}