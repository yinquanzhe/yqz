package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.HypervisorsOS;
import net.ahwater.ahwaterCloud.compute.entity.VMManager.HypervisorListEle;
import net.ahwater.ahwaterCloud.compute.entity.VMManager.HypervisorStatisticsEle;

/**
 * 虚拟机管理器控制类
 * @author gwh
 *
 */

@Controller
@RequestMapping("/ctr")
public class HypervisorsProcess {
	/**
	 * 输出虚拟机管理程序列表信息
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listHypervisors")
	public void listHypervisors(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		List<? extends Hypervisor> lh=HypervisorsOS.listHypervisors(os);
		List<HypervisorListEle> lhe=new ArrayList<>();
		for(Hypervisor h:lh){
			HypervisorListEle he=new HypervisorListEle();
			
			he.setId(h.getId());
			he.setHostname(h.getHypervisorHostname());
			he.setType(h.getType());
			he.setUsedVcpu(h.getVirtualUsedCPU());
			he.setVcpus(h.getVirtualCPU());
			he.setLocalMemoryUsed(Float.parseFloat(String.format("%.1f",h.getLocalMemoryUsed()*1.0/1024)));
			he.setLocalMemory(Float.parseFloat(String.format("%.1f",h.getLocalMemory()*1.0/1024)));
			he.setLocalDiskUsed(Float.parseFloat(String.format("%.1f",h.getLocalDiskUsed()*1.0/1024)));
			he.setLocalDisk(Float.parseFloat(String.format("%.1f",h.getLocalDisk()*1.0/1024)));
			he.setRunningVM(h.getRunningVM());
			
			lhe.add(he);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(lhe);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 虚拟机管理器概述
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/getStatistics")
	public void getStatistics(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		HypervisorStatistics hss=HypervisorsOS.getHypervisorStatistics(os);
		HypervisorStatisticsEle hse=new HypervisorStatisticsEle();
		hse.setVcspus(hss.getVirtualCPU());
		hse.setVcpusUsed(hss.getVirtualUsedCPU());
		hse.setMemory(Float.parseFloat(String.format("%.1f", hss.getMemory()*1.0/1024)));
		hse.setMemoryUsed(Float.parseFloat(String.format("%.1f", hss.getMemoryUsed()*1.0/1024)));
		hse.setLocalDisk(Float.parseFloat(String.format("%.1f", hss.getLocal()*1.0/1024)));
		hse.setLocalDiskUsed(Float.parseFloat(String.format("%.1f", hss.getLocalUsed()*1.0/1024)));
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(hse);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 列出一个主机下所有云主机实例
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/listHypervisorServers")
	public void listHypervisorServers(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws  Exception{
		String hypervisorsId=request.getParameter("hypervisorsId");
		
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String tokenId=tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
		String tenantId=tkn.getTenant().getId();
		
		String msg=HypervisorsOS.listHypervisorServers(tenantId,tokenId, hypervisorsId);
		
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	
}
