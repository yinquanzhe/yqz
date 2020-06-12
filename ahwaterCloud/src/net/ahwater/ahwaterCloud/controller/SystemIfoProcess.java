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

import net.ahwater.ahwaterCloud.compute.systemInfo.ServiceOS;

/**
 * 系统信息控制类
 * @author gwh
 *
 */

@Controller
@RequestMapping("/ctr")
public class SystemIfoProcess {
	
	/**
	 * 列出所有服务
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/listService")
	public void listService(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		Access access=(Access)session.getAttribute("Access");
		KeystoneToken token=(KeystoneToken) access.getToken();
		String tokenId=token.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(access);
		String msg= ServiceOS.listService(tokenId);
		
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 列出所有计算服务
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/listSysComputeService")
	public void listComputeService(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		Access access=(Access)session.getAttribute("Access");
		KeystoneToken token=(KeystoneToken) access.getToken();
		String tokenId=token.getId();
		String tenantId=token.getTenant().getId();
//		OSClientV2 os=OSFactory.clientFromAccess(access);
		
		String msg=ServiceOS.listComputeServices(tenantId,tokenId);
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
	}
}
