package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.FloatingIpOS;
import net.ahwater.ahwaterCloud.compute.entity.FloatingIpInfo;

@Controller
@RequestMapping("/contr")
public class FloatingIpProcess {
	
	/**
	 * 获取浮动IP列表
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListAllFloatingIp")
	public void ListAllFloatingIppublic(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends FloatingIP> floatingIpList=FloatingIpOS.ListAllFloatingIp(os);
		List<FloatingIpInfo> floatingIpInfoList=new ArrayList<FloatingIpInfo>();
		for(FloatingIP fip:floatingIpList)
		{
			FloatingIpInfo fIpInfo=new FloatingIpInfo();
			FloatingIpOS fipos=new FloatingIpOS(fip);
			fIpInfo.setId(fipos.getId());
			fIpInfo.setFloatingIpAddress(fipos.getFloatingIpAddress());
			fIpInfo.setFixedIpAddress(fipos.getFixedIpAddress());
			fIpInfo.setPool(fipos.getPool());
			if(fipos.isUsable())
				fIpInfo.setState("未使用");
			else
				fIpInfo.setState("已使用");
			floatingIpInfoList.add(fIpInfo);
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg=mapper.writeValueAsString(floatingIpInfoList);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 分配浮动IP给项目
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/AllocateFloatingIp")
	public void AllocateFloatingIp(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String pool=request.getParameter("pool");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String errorMsg="success";
		try
		{
			FloatingIpOS.AllocateFloatingIp(os, pool);
		}
		catch(Exception e)
		{
			errorMsg=e.getMessage();
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(errorMsg);
		System.out.println(msg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 释放浮动IP
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/DeallocateFloatingIp")
	public void DeallocateFloatingIp(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String ids=request.getParameter("id");
		ObjectMapper mapper = new ObjectMapper(); 
		List<String> idList=mapper.readValue(ids, new TypeReference<List<String>>() {});
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		int success=0,failed=0;
		for(String id:idList )
		{
		try
		{
			FloatingIpOS.DeallocateFloatingIp(os, id);
			success++;
		}
		catch(Exception e)
		{
			failed++;
		}
		}
		String errorMsg="删除成功 "+success+" 个，失败 "+failed+" 个！";
		String msg = mapper.writeValueAsString(errorMsg);
		System.out.println(msg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 获取资源池名称列表
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/GetPoolNames")
	public void GetPoolNames(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<String> poolNameList=FloatingIpOS.GetPoolNames(os);
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(poolNameList);
		System.out.println(msg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 关联IP,即将动态IP与主机绑定
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/AddFloatingIp")
	public void AddFloatingIp(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String floatingIpAddress=request.getParameter("floatingIpAddress");
		String serverId=request.getParameter("serverId");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String errorMsg="success";
		try
		{
			FloatingIpOS.AddFloatingIp(os, floatingIpAddress, serverId);
		}
		catch(Exception e)
		{
			errorMsg=e.getMessage();
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(errorMsg);
		System.out.println(msg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
}
