package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.model.network.ExternalGateway;
import org.openstack4j.model.network.HostRoute;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Router;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.network.RouterOS;
import net.ahwater.ahwaterCloud.network.entity.HostRouteInfo;
import net.ahwater.ahwaterCloud.network.entity.RouterBriefInfo;
import net.ahwater.ahwaterCloud.network.entity.RouterDetailInfo;

@Controller
@RequestMapping("/contr")
/**
 * 定义了对路由的相关操作
 * @author dell
 *
 */
public class RouterProcess {
	/**
	 * 列出当前租户所有的路由
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListTenantRouter")
	 public void ListTenantRouter(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException
	 {
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String currentTenantId=tkn.getTenant().getId();;
		List<? extends Router> routerList=os.networking().router().list();
		List<RouterBriefInfo> routerBriefInfos=new ArrayList<RouterBriefInfo>();
		for(Router router: routerList)
		{
			RouterOS ros=new RouterOS(router);
			if(!ros.getTenantId().equals(currentTenantId))
			{
				continue;
			}
			RouterBriefInfo info=new RouterBriefInfo();
			info.setRouterId(ros.getId());
			info.setRouterName(ros.getName());
			info.setState(ros.getStatus().toString());
			String networkId=ros.getExternalGateway().getNetworkId();
			String networkName=os.networking().network().get(networkId).getName();
			info.setExternalNetworkName(networkName);
			if(ros.isAdminStateUp())
				info.setAdminState("up");
			else
				info.setAdminState("down");
			routerBriefInfos.add(info);
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(routerBriefInfos);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	 }
	/**
	 * 列出所有路由，面向管理员账户
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListAllRouter")
	 public void ListAllRouter(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException
	 {
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		List<? extends Router> routerList=os.networking().router().list();
		List<RouterBriefInfo> routerBriefInfos=new ArrayList<RouterBriefInfo>();
		for(Router router: routerList)
		{
			RouterOS ros=new RouterOS(router);
			RouterBriefInfo info=new RouterBriefInfo();
			info.setRouterId(ros.getId());
			info.setRouterName(ros.getName());
			info.setState(ros.getStatus().toString());
			String networkId=ros.getExternalGateway().getNetworkId();
			String networkName=os.networking().network().get(networkId).getName();
			info.setExternalNetworkName(networkName);
			String tenantId=ros.getTenantId();
			String tenantName=os.identity().tenants().get(tenantId).getName();
			info.setProjectName(tenantName);
			if(ros.isAdminStateUp())
				info.setAdminState("up");
			else
				info.setAdminState("down");
			routerBriefInfos.add(info);
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(routerBriefInfos);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	 }
	
	@RequestMapping("/GetRouterDetail")
	public void GetRouterDetail(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException
	 {
		String routerId=request.getParameter("routerId");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		//String routerId="8f35d895-c635-41bb-8174-1f3a75b8b930";
		RouterOS ros=new RouterOS(os,routerId);
		RouterDetailInfo info=new RouterDetailInfo();
		info.setRouterId(ros.getId());
		info.setRouterName(ros.getName());
		info.setProjectId(ros.getTenantId());
		info.setState(ros.getStatus().toString());
		if(ros.isAdminStateUp())
			info.setAdminState("up");
		else
			info.setAdminState("down");
		String networkId=ros.getExternalGateway().getNetworkId();
		String networkName=os.networking().network().get(networkId).getName();
		info.setExternalNetworkName(networkName);
		info.setExternalNetworkId(networkId);
		if(ros.getExternalGateway().isEnableSnat())
			info.setSnat("激活");
		else
			info.setSnat("未激活");
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(info);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	 }
	
	@RequestMapping("/GetHostRoute")
	public void GetHostRoute(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException
	 {
		String routerId=request.getParameter("routerId");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		//String routerId="8f35d895-c635-41bb-8174-1f3a75b8b930";
		RouterOS ros=new RouterOS(os,routerId);
		List<? extends HostRoute> hostRoutes=ros.getRoutes();
		List<HostRouteInfo> hostRouteInfos=new ArrayList<HostRouteInfo>();
		for(HostRoute hostRoute:hostRoutes)
		{
			HostRouteInfo info=new HostRouteInfo();
			info.setDestination(hostRoute.getDestination());
			info.setNextHop(hostRoute.getNexthop());
			hostRouteInfos.add(info);
		}
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(hostRouteInfos);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	 }
}
