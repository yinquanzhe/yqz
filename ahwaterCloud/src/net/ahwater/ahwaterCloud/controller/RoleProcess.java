package net.ahwater.ahwaterCloud.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Role;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.identityV3.RoleOS;
import net.ahwater.ahwaterCloud.identityV3.entity.RoleListEle;

/**
 * 角色控制类
 * @author gwh
 *
 */

@Controller
@RequestMapping("ctr")
public class RoleProcess {
	
	/**
	 * 角色列表
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/listRoles")
	public void listRoles(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		
		List<?extends Role> lrs=RoleOS.listRoles(os);
		
		List<RoleListEle> lrle=new ArrayList<>();
		for(Role r:lrs){
			RoleListEle rle=new RoleListEle();
			rle.setRoleId(r.getId());
			rle.setRoleName(r.getName());
			lrle.add(rle);
		}
		
		ObjectMapper mapper=new ObjectMapper();
		String msg=mapper.writeValueAsString(lrle);
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 创建角色
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("createRole")
	public void createRole(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String roleName=request.getParameter("roleName");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		
		RoleOS ro=new RoleOS();
		String msg=ro.createRole(os, roleName);
		
		if(msg==null){
			msg="succ";
		}
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 删除角色
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("deleteRole")
	public void deleteRole(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String roleId=request.getParameter("roleId");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		
		RoleOS ro=new RoleOS(os.identity().roles().get(roleId));
		ro.deleteRole(os);
		
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write("succ");
	}
	
	/**
	 * 批量删除角色
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("PatchDeleteRole")
	public void PatchDeleteRole(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String roleIdListStr=request.getParameter("roleIdListStr");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		
		ObjectMapper mapper=new ObjectMapper();
		List<String> roleIdList=mapper.readValue(roleIdListStr, new TypeReference<List<String>>() {});
		
		RoleOS ro=new RoleOS();
		ro.patchDeleteRole(os, roleIdList);
		
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write("succ");
	}
	
	/**
	 * 编辑角色
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/editRole")
	public void editRole(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String roleId=request.getParameter("roleId");
		String newRoleName=request.getParameter("newRoleName");
		
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String tokenId=tkn.getId();
		OSClientV2 os=OSFactory.clientFromAccess(ac);
		
		RoleOS.editRole(os,tokenId, roleId, newRoleName);
		
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write("succ");
	}
}
