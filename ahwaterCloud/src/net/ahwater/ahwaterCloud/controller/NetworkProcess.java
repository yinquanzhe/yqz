package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.network.NetworkOS;
import net.ahwater.ahwaterCloud.network.PortOS;
import net.ahwater.ahwaterCloud.network.SubnetOS;
import net.ahwater.ahwaterCloud.network.entity.NetworkCreateInf;
import net.ahwater.ahwaterCloud.network.entity.NetworkDetail;
import net.ahwater.ahwaterCloud.network.entity.NetworkElement;
import net.ahwater.ahwaterCloud.network.entity.NetworkNameId;
import net.ahwater.ahwaterCloud.network.entity.PortElement;
import net.ahwater.ahwaterCloud.network.entity.SubnetDetail;
import net.ahwater.ahwaterCloud.network.entity.SubnetElement;

/**
 * 网络（network）控制类
 * 接收前端对网络的操作的请求，调用相关模块完成响应
 * @author dell
 *
 */

@Controller
@RequestMapping("/cont")
public class NetworkProcess {
	/**
	 * 列出当前租户拥有的网络
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listAllNetwork")
	public void listAllNetwork(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		KeystoneToken token = (KeystoneToken)os.getAccess().getToken();
		String tenantId = token.getTenant().getId();
//		String tenantId = request.getParameter("tenantId");
		
		List<? extends Network> networks = NetworkOS.listAllNetwork(os);
		List<NetworkElement> nets = new ArrayList<>();
		for(Network n:networks){
			if(n.isShared() || n.getTenantId().equals(tenantId)){
				NetworkElement net = new NetworkElement();
				NetworkOS nos = new NetworkOS(n);
				net.setNetworkId(nos.getNetworkId());
				net.setNetworkName(nos.getNetworkName());
				net.setNetworkSubnet(nos.getSubnetId(), os);
				net.setNetworkIsShared(nos.isNetworkIsShared());
				net.setNetworkState(nos.getNetworkState());
				net.setNetworkIsAdminStateUp(nos.isNetworkIsAdminStateUp());
				nets.add(net);
			}
			
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(nets);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 列出网络的详细信息
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/networkDetail")
	public void networkDetail(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String networkId = request.getParameter("networkId");
		Network network = os.networking().network().get(networkId);
		NetworkDetail networkDetail = new NetworkDetail();
		networkDetail.setIsExternal(network.isRouterExternal());
		networkDetail.setNetworkId(network.getId());
		networkDetail.setNetworkIsAdminStateUp(network.isAdminStateUp());
		networkDetail.setNetworkIsShared(network.isShared());
		networkDetail.setNetworkName(network.getName());
		networkDetail.setNetworkState(network.getStatus());
		networkDetail.setNetworkType(network.getNetworkType());
		networkDetail.setPhycialNetwork(network.getProviderPhyNet());
		networkDetail.setSegId(network.getProviderSegID());
		networkDetail.setTenantId(network.getTenantId());
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(networkDetail);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 传递网络的Name和Id，用于创建server
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listNetworkNameId")
	public void listNetworkNmaeId(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String tenantId = request.getParameter("tenantId");
		List<? extends Network> networks = NetworkOS.listAllNetwork(os);
		List<NetworkNameId> netNameIds = new ArrayList<>();
		for(Network n:networks){
			if(n.isShared() || n.getTenantId().equals(tenantId)){
				NetworkNameId netNameId = new NetworkNameId();
				NetworkOS nos = new NetworkOS(n);
				netNameId.setNetworkName(nos.getNetworkName());
				netNameId.setNetworkId(nos.getNetworkId());
				netNameIds.add(netNameId);
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(netNameIds);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	@RequestMapping("/createNetwork")
	public void createNetwork(NetworkCreateInf nci, HttpServletResponse response, HttpServletRequest request, HttpSession session){
		
//		String networkName = request.getParameter("networkName");
//		boolean networkIsShared = request.getParameter("networkIsShared");
		String networkName = nci.getNetworkName();
		boolean networkIsShared = nci.isNetworkIsShared();
		boolean NetworkIsAdminStateUp = nci.isNetworkIsAdminStateUp();
		
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		
		Network network = os.networking().network()
                .create(Builders.network().name("test").adminStateUp(true).isShared(true).build());
	}
	
	
	/**
	 * 列出一个网络下的所有的端口
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listPorts")
	public void listPorts(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String networkId = request.getParameter("networkId");
		List<? extends Port> ports = PortOS.listAllPorts(os, networkId);
		List<PortElement> pos = new ArrayList<PortElement>();
		for(Port port:ports){
			PortElement po = new PortElement();
			po.setPortName(port.getName());
			po.setPortFixedIps(port.getFixedIps());
			po.setPortDevice(port.getDeviceId());
			po.setPortState(port.getState());
			po.setPortisAdminStateUp(port.isAdminStateUp());
			po.setPortId(port.getId());
			pos.add(po);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(pos);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 更改端口的名字
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/updatePortName")
	public void updatePortName(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String newName = request.getParameter("newName");
		String portId = request.getParameter("portId");
		Port port = os.networking().port().get(portId);
		PortOS pos = new PortOS(port);
		String message = pos.updatePortName(os, newName);
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	

	/**
	 * 列出一个网络下的所有的子网
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listSubnet")
	public void listSubnet(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String networkId = request.getParameter("networkId");
		List<String> subnetIds = SubnetOS.listSubnet(os, networkId);
		List<SubnetElement> subneteles = new ArrayList<SubnetElement>();
		for(String id:subnetIds){
			Subnet subnet = os.networking().subnet().get(id);
			SubnetElement subnetele = new SubnetElement();
			subnetele.setGateway(subnet.getGateway());
			subnetele.setIpVersion(subnet.getIpVersion().toString());
			subnetele.setSubnetCidr(subnet.getCidr());
			subnetele.setSubNetId(id);
			subnetele.setSubnetName(subnet.getName());
			subneteles.add(subnetele);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(subneteles);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
		
	}
	
	
	/**
	 * 子网详情
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/subnetDetail")
	public void subnetDetail(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String subnetId = request.getParameter("subnetId");
		Token token = os.getAccess().getToken();
		String result = SubnetOS.subnetDetail(token, os, subnetId);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(result);
		JsonNode subnetJson = rootNode.get("subnet");
		SubnetDetail subnetd = new SubnetDetail();
		subnetd.setEnableDHCP(subnetJson.get("enable_dhcp").toString());
		subnetd.setGateway(subnetJson.get("gateway_ip").toString());
		JsonNode allocation_poolsJson =  subnetJson.get("allocation_pools");
		String pools = allocation_poolsJson.toString();
		pools = pools.substring(1, pools.length()-1);
		allocation_poolsJson = mapper.readTree(pools);
		String poolend = allocation_poolsJson.get("end").toString();
		poolend = poolend.substring(1, poolend.length()-1);
		subnetd.setIPEnd(poolend);
		String poolstart = allocation_poolsJson.get("start").toString();
		poolstart = poolstart.substring(1, poolstart.length()-1);
		subnetd.setIPStart(poolstart);
		subnetd.setIpVersion(subnetJson.get("ip_version").toString());
		String network_id = subnetJson.get("network_id").toString();
		network_id = network_id.substring(1, network_id.length()-1);
		subnetd.setNetworkId(network_id);
		String cidr = subnetJson.get("cidr").toString();
		subnetd.setSubnetCidr(cidr.substring(1, cidr.length()-1));
		String id = subnetJson.get("id").toString();
		subnetd.setSubNetId(id.substring(1, id.length()-1));
		
		String name = subnetJson.get("name").toString();
		subnetd.setSubnetName(name.substring(1, name.length()-1));
		String subnetpool_id = subnetJson.get("subnetpool_id").toString();
		subnetd.setSubnetPoolId(subnetpool_id.substring(1, subnetpool_id.length()-1));
		
		String dns_nameservers = subnetJson.get("dns_nameservers").toString();
		subnetd.setDns_nameservers(dns_nameservers.substring(2, dns_nameservers.length()-2));
		
		String msg = mapper.writeValueAsString(subnetd);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 列出云平台上的所有的网络
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/adminListAllNetworks")
	public void adminListAllNetworks(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		KeystoneToken token = (KeystoneToken)os.getAccess().getToken();
		String tenantId = token.getTenant().getId();
		
		List<? extends Network> networks = NetworkOS.listAllNetwork(os);
		List<NetworkElement> nets = new ArrayList<>();
		for(Network n:networks){
			NetworkElement net = new NetworkElement();
			NetworkOS nos = new NetworkOS(n);
			net.setNetworkId(nos.getNetworkId());
			net.setNetworkName(nos.getNetworkName());
			net.setNetworkSubnet(nos.getSubnetId(), os);
			net.setNetworkIsShared(nos.isNetworkIsShared());
			net.setNetworkState(nos.getNetworkState());
			net.setNetworkIsAdminStateUp(nos.isNetworkIsAdminStateUp());
			tenantId = nos.getNetworkTenantId();
			net.setTenantName(os, tenantId);
			nets.add(net);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(nets);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
}
