package net.ahwater.ahwaterCloud.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.ahwater.ahwaterCloud.compute.ComputeServiceOS;
/**
 * 计算域服务控制类
 * @author gwh
 *
 */
@Controller
@RequestMapping("ctr")
public class ComputeServiceProcess {
	
	/**
	 * 列出所有计算服务
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/listComputeServices")
	public void listComputeServices2(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String tokenId=tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
		String tenantId=tkn.getTenant().getId();
		
		String msg=ComputeServiceOS.listComputeServices(tenantId,tokenId);
		
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 关闭服务
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/disableComputeService")
	public void disableComputeService(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String host=request.getParameter("host");
		String reason=request.getParameter("reason");
		
//		String host="ahwater-cloud-compute001";
//		String reason="";
		
		if(null==reason||reason.equals("")){
			reason="none";
		}
		
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String tokenId=tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
		String tenantId=tkn.getTenant().getId();
		
		String msg=ComputeServiceOS.disableComputeService(tenantId,tokenId, host, reason);
		
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 打开服务
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/enableComputeService")
	public void enableComputeService(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String host=request.getParameter("host");
//		String host="ahwater-cloud-compute001";
		
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String tokenId=tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
		String tenantId=tkn.getTenant().getId();
		
		String msg=ComputeServiceOS.enableComputeService(tenantId,tokenId, host);
		
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
}
