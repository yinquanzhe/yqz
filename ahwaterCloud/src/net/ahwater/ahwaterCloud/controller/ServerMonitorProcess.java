package net.ahwater.ahwaterCloud.controller;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.ServerOS;
import net.ahwater.ahwaterCloud.monitor.ServerMonitor;
import net.ahwater.ahwaterCloud.monitor.entity.MonitorInfo;

/**
 * 监控控制类
 * @author gwh
 *
 */
@Controller
@RequestMapping("/ctr")
public class ServerMonitorProcess {
	
	/**
	 * 列出租户所有云主机的实时监控信息
	 * @param request
	 * @param response
	 * @param session
	 * @throws SQLException
	 * @throws Exception
	 */
	@RequestMapping("/listNewMonitor")
	public void listNewMonitor(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws SQLException, Exception{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Server> servers=ServerOS.listAllServersDetail(os);
		
		List<String> uuidList=new ArrayList<>();
		for(Server s:servers){
			uuidList.add(s.getId());
		}
		
		List<MonitorInfo> lm=new ServerMonitor().queryNewServerMonitorInfo(uuidList);
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(lm);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
		
	}
	
	/**
	 * 列出所有租户所有云主机的实时监控信息
	 * @param request
	 * @param response
	 * @param session
	 * @throws SQLException
	 * @throws Exception
	 */
	@RequestMapping("/listAllTenantNewMonitor")
	public void listAllTenantNewMonitor(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws SQLException, Exception{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		List<? extends Server> servers=ServerOS.listAllTenantServes(os);
		
		List<String> uuidList=new ArrayList<>();
		for(Server s:servers){
			uuidList.add(s.getId());
		}
		
		List<MonitorInfo> lm=new ServerMonitor().queryNewServerMonitorInfo(uuidList);
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(lm);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 列出某个云主机的历史监控信息
	 * @param request
	 * @param response
	 * @param session
	 * @throws SQLException
	 * @throws Exception
	 */
	@RequestMapping("listOneServerMonitor")
	public void listOneServerMonitor(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws SQLException, Exception{
		String uuid=request.getParameter("uuid");
		int hours=Integer.parseInt(request.getParameter("hours"));
//		String uuid="51b17efb-8799-465f-b8f2-f0abf34f69f8";
//		int hours=1;
		
		List<MonitorInfo> lm=new ServerMonitor().queryOneServerMonitorInfo(uuid,hours);
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(lm);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
		
	}
	
}
