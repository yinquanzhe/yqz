package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.RuleOS;
import net.ahwater.ahwaterCloud.compute.SecurityGroupOS;
import net.ahwater.ahwaterCloud.compute.entity.RuleCreationInfo;
import net.ahwater.ahwaterCloud.compute.entity.RuleInfo;
import net.ahwater.ahwaterCloud.compute.entity.SecurityGroupBriefInfo;


@Controller
@RequestMapping("/contr")

public class SecurityGroupProcess {

	/**
	 * 显示所有安全组
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListAllSecurityGroup")
	public void ListAllSecurityGroup(HttpServletResponse response,HttpSession session) throws IOException{
			OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
			List<?extends SecGroupExtension> groups=SecurityGroupOS.ListAllSecurityGroup(os);
			List<SecurityGroupBriefInfo> groupBriefInfoList=new ArrayList<SecurityGroupBriefInfo>();
			for(SecGroupExtension group:groups)
			{
				SecurityGroupBriefInfo groupBriefInfo=new SecurityGroupBriefInfo();
				SecurityGroupOS groupOs=new SecurityGroupOS(group);
				groupBriefInfo.setSecurityGroupId(groupOs.getId());
				groupBriefInfo.setSecurityGroupName(groupOs.getName());
				groupBriefInfo.setDescription(groupOs.getDescription());
				groupBriefInfoList.add(groupBriefInfo);
			}
			ObjectMapper mapper = new ObjectMapper();
			String msg = mapper.writeValueAsString(groupBriefInfoList);
			PrintWriter out = null;
			response.setContentType("application/json");
			out = response.getWriter();
			out.write(msg);
	}
	
	/**
	 * 获取指定主机所属安全组和可用安全组
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListServerGroups")
	public void ListServerGroups(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("serverId");
		//String serverId="8ddc300b-8c1c-413f-8d3d-e4cab5570767";
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<?extends SecGroupExtension> usedGroups=SecurityGroupOS.ListServerGroup(os, serverId);
		List<?extends SecGroupExtension> usableGroups=SecurityGroupOS.ListAllSecurityGroup(os);
		usableGroups.removeAll(usedGroups);
		List<List<SecurityGroupBriefInfo>> returnList=new ArrayList<List<SecurityGroupBriefInfo>>();
		List<SecurityGroupBriefInfo> usedGroupList=new ArrayList<SecurityGroupBriefInfo>();
		List<SecurityGroupBriefInfo> usableGroupList=new ArrayList<SecurityGroupBriefInfo>();
		for(SecGroupExtension group:usedGroups)
		{
			SecurityGroupBriefInfo groupBriefInfo=new SecurityGroupBriefInfo();
			SecurityGroupOS groupOs=new SecurityGroupOS(group);
			groupBriefInfo.setSecurityGroupId(groupOs.getId());
			groupBriefInfo.setSecurityGroupName(groupOs.getName());
			//groupBriefInfo.setDescription(groupOs.getDescription());
			usedGroupList.add(groupBriefInfo);
		}
		returnList.add(usedGroupList);
		for(SecGroupExtension group:usableGroups)
		{
			SecurityGroupBriefInfo groupBriefInfo=new SecurityGroupBriefInfo();
			SecurityGroupOS groupOs=new SecurityGroupOS(group);
			groupBriefInfo.setSecurityGroupId(groupOs.getId());
			groupBriefInfo.setSecurityGroupName(groupOs.getName());
			//groupBriefInfo.setDescription(groupOs.getDescription());
			usableGroupList.add(groupBriefInfo);
		}
		returnList.add(usableGroupList);
		returnList.add(usedGroupList);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(returnList);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	/**
	 * 创建安全组
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/CreateSecurityGroup")
	public void CreateSecurityGroup(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String securityGroupName=request.getParameter("securityGroupName");
		String description=request.getParameter("description");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String errorMsg="success";
		SecurityGroupOS groupOs=new SecurityGroupOS();
		try{
			groupOs.CreateSecurityGroup(os, securityGroupName, description);
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
	 * 编辑安全组
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/EditSecurityGroup")
	public void EditSecurityGroup(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String securityGroupId=request.getParameter("securityGroupId");
		String securityGroupName=request.getParameter("securityGroupName");
		String description=request.getParameter("description");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String errorMsg="success";
		SecurityGroupOS groupOs=new SecurityGroupOS();
		try{
			groupOs.UpdateSecurityGroup(os, securityGroupId, securityGroupName, description);
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
	 * 删除安全组
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/DeleteSecurityGroup")
	public void DeleteSecurityGroup(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String securityGroupIds=request.getParameter("securityGroupId");
		ObjectMapper mapper = new ObjectMapper(); 
		List<String> securityGroupIdList=mapper.readValue(securityGroupIds, new TypeReference<List<String>>() {});
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		SecurityGroupOS groupOs=new SecurityGroupOS();
		int success=0,failed=0;
		for(String securityGroupId:securityGroupIdList )
		{
		try{
			groupOs.DeleteSecurityGroup(os, securityGroupId);
			success++;
		}
		catch (Exception e) {
			failed++;
		}
		}
		String errorMsg="删除成功 "+success+" 个，失败 "+failed+" 个！";
		String msg = mapper.writeValueAsString(errorMsg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 显示此安全组所属规则
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListGroupRules")
	public void ListGroupRules (HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String securityGroupId=request.getParameter("securityGroupId");
		//String securityGroupId="d061dce6-a315-42f5-a6c2-0699b8d307bf";
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<RuleInfo> ruleInfoList=getGroupRules(os, securityGroupId);
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(ruleInfoList);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 按安全组Id查询该安全组的所有规则
	 * @param os
	 * @param securityGroupId
	 * @return
	 */
	private List<RuleInfo> getGroupRules(OSClientV2 os ,String securityGroupId)
	{
		List<? extends SecurityGroupRule> ruleList=os.networking().securitygroup().get(securityGroupId).getRules();
		List<RuleInfo> ruleInfoList=new ArrayList<RuleInfo>();
		for(SecurityGroupRule r:ruleList)
		{
			RuleOS ros=new RuleOS(r);
			RuleInfo ruleInfo=new RuleInfo();
			ruleInfo.setRuleId(ros.getId());
			ruleInfo.setDirection(ros.getDirection());
			ruleInfo.setEtherType(ros.getEtherType());
			ruleInfo.setProtocol(ros.getProtocol());
			ruleInfo.setPortRange(ros.getPortRangeStr());
			ruleInfo.setCidr(ros.getRemoteIpPrefix());
			String remoteGroupId=ros.getRemoteGroupId();
			if(remoteGroupId!=null)
			{
				SecurityGroup remoteGroup=os.networking().securitygroup().get(remoteGroupId);
				ruleInfo.setRemoteSecurityGroup(remoteGroup.getName());
			}
			else
			{
				ruleInfo.setRemoteSecurityGroup(null);
			}
			ruleInfoList.add(ruleInfo);
		}
		return ruleInfoList;
	}
	/**
	 * 创建安全组规则
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/CreateRule")
	//public void CreateRule(RuleCreationInfo info,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
	public void CreateRule(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String securityGroupId=request.getParameter("securityGroupId");
		String direction=request.getParameter("direction");
		String protocol=request.getParameter("protocol");
		String hasPort=request.getParameter("hasPort");
		String portRangeMin=request.getParameter("portRangeMin");
		String portRangeMax=request.getParameter("portRangeMax");
		String isCidr=request.getParameter("isCIDR");
		String remoteIpPrefix=request.getParameter("remoteIpPrefix");
		String remoteGroupId=request.getParameter("remoteGroupId");
		String etherType=request.getParameter("etherType");
		String errorMsg="success";
		RuleCreationInfo info=new RuleCreationInfo();
		info.setSecurityGroupId(securityGroupId);
		info.setDirection(direction);
		info.setProtocol(protocol);
		if(hasPort.equals("true"))
			info.setHasPort(true);
		else
			info.setHasPort(false);;
		info.setPortRangeMax(Integer.parseInt(portRangeMax));
		info.setPortRangeMin(Integer.parseInt(portRangeMin));
		if(isCidr.equals("true"))
			info.setCIDR(true);
		else
			info.setCIDR(false);
		info.setRemoteIpPrefix(remoteIpPrefix);
		info.setRemoteGroupId(remoteGroupId);
		info.setEtherType(etherType);

		try{
			RuleOS.CreateRule(os, info);
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
	 * 删除规则
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("DeleteRule")
	public void DeleteRule (HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String ruleIds=request.getParameter("ruleId");
		ObjectMapper mapper = new ObjectMapper(); 
		List<String> ruleIdList=mapper.readValue(ruleIds, new TypeReference<List<String>>() {});
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		int success=0,failed=0;
		for(String ruleId:ruleIdList )
		{
		try{
			RuleOS.DeleteRule(os, ruleId);
			success++;
		}
		catch (Exception e) {
			failed++;
		}
		}
		String errorMsg="删除成功 "+success+" 个，失败 "+failed+" 个！";
		String msg = mapper.writeValueAsString(errorMsg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
		}
}
