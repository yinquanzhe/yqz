package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.availabilityZone.AvailabilityZone;
import net.ahwater.ahwaterCloud.service.availabilityZone.AvailabilityZoneOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 控制类，接收前端的请求，调用相关模块对前端请求进行响应
 * 功能：对前端的“列出可用域”请求进行响应
 * @author ZhangDaofu
 *
 */
@RestController
@RequestMapping("/cont")
public class AvailabilityZoneProcess {

    @Autowired
    private AvailabilityZoneOS availabilityZoneOS;

	/**
	 * 列出可用域
     * @param tenantId
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listAvailabilityZone")
	public List<AvailabilityZone> ListAvailabilityZones(String tenantId, HttpSession session ) throws IOException {
		OSClientV2 os = OSFactory.clientFromAccess((Access)session.getAttribute("Access"));
		Token token = os.getAccess().getToken();
		String result = availabilityZoneOS.listAvailabilityZone(token, os, tenantId);
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
		return avaizones;
	}
}
