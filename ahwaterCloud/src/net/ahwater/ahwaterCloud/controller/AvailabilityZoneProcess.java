package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.availabilityZone.AvailabilityZoneOS;
import net.ahwater.ahwaterCloud.availabilityZone.entity.AvailabilityZone;
/**
 * 控制类，接收前端的请求，调用相关模块对前端请求进行响应
 * 功能：对前端的“列出可用域”请求进行响应
 * @author ZhangDaofu
 *
 */
@Controller
@RequestMapping("/cont")
public class AvailabilityZoneProcess {
	
	/**
	 * 列出可用域
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listAvailabilityZone")
	public void ListAvailabilityZones(HttpServletRequest request, HttpServletResponse response, HttpSession session ) throws IOException{
		String tenantId = request.getParameter("tenantId");
		OSClientV2 os = OSFactory.clientFromAccess((Access)session.getAttribute("Access"));
		Token token = os.getAccess().getToken();
		AvailabilityZoneOS azos = new AvailabilityZoneOS();
		String result = azos.listAvailabilityZone(token, os, tenantId);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(result);
		JsonNode zonesJson = rootNode.get("availabilityZoneInfo");
		
		List<AvailabilityZone> avaizones = new ArrayList<>();
		for(int i = 0; i < zonesJson.size(); i++){
			AvailabilityZone avaizone = new AvailabilityZone();
			JsonNode zoneJson= zonesJson.get(i);
			JsonNode state = zoneJson.get("zoneState");
			avaizone.setZoneState(state.get("available").toString());
			avaizone.setZoneName(zoneJson.get("zoneName").toString());
			JsonNode hosts = zoneJson.get("hosts");
			List<String> hostNames = new ArrayList<>();
			if(hosts.size() == 1){
				String hostsStr = hosts.toString();
				String hostName = hostsStr.split("\":")[0];
				hostName = hostName.substring(2, hostName.length());
				hostNames.add(hostName);
			}
			else if(hosts.size() > 1){
				String hostsStr = hosts.toString();
				hostsStr = hostsStr.substring(1, hostsStr.length());
				String[] hostsStrmul = hostsStr.split("\\},");
				for(int j=0; j<hostsStrmul.length; j++){
					String hostsName = hostsStrmul[j].split("\":")[0];
					hostsName = hostsName.substring(1, hostsName.length());
					hostNames.add(hostsName);
				}
			}
			avaizone.setHost(hostNames);
			avaizones.add(avaizone);
		}
		String msg = mapper.writeValueAsString(avaizones);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
}
