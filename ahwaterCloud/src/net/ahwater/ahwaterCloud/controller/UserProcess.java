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
import org.openstack4j.model.identity.v2.User;
import org.openstack4j.model.identity.v2.builder.UserBuilder;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.identityV3.UserOS;
import net.ahwater.ahwaterCloud.identityV3.entity.UserListInfo;

@Controller
@RequestMapping("/contr")
public class UserProcess {
	/**
	 * 列出所有的用户信息（面向管理员）
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ListAllUser")
	public void ListAllUser(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		List<?extends User> userList=UserOS.ListAllUser(os);
		List<UserListInfo> userListInfos=new ArrayList<UserListInfo>();
		for(User user:userList)
		{
			UserOS uos=new UserOS(user);
			UserListInfo info=new UserListInfo();
			info.setUserId(uos.getId());
			info.setUserName(uos.getName());
			info.setEmail(uos.getEmail());
			info.setEnabled(uos.isEnabled());
			info.setTenantId(uos.getTenantId());
			userListInfos.add(info);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(userListInfos);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	@RequestMapping("/GetUserDetail")
	public void GetUserDetail(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		String userId=request.getParameter("userId");
		UserOS uos=new UserOS(os,userId);
		UserListInfo info=new UserListInfo();
		info.setUserId(uos.getId());
		info.setUserName(uos.getName());
		info.setEmail(uos.getEmail());
		info.setEnabled(uos.isEnabled());
		info.setTenantId(uos.getTenantId());	
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(info);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	@RequestMapping("/CreateUser")
	public void CreateUser(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		String userName=request.getParameter("userName");
		String email=request.getParameter("email");
		String enabled=request.getParameter("enabled");
		String password=request.getParameter("password");
		String tenantId=request.getParameter("tenantId");
		String roleName=request.getParameter("roleName");
		String errorMsg="success";
		boolean isEnabled=false;
		if(enabled.equals("true"))
			isEnabled=true;
		UserOS uos=new UserOS(os);
		try
		{
			uos.CreateUser(userName, email, password, tenantId,isEnabled);//创建用户
			uos.AssociateRoleToUser(roleName);//将用户与角色绑定
		}
		catch (Exception e) {
			errorMsg="Failed to create a user : "+e.getMessage();
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(errorMsg);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	@RequestMapping("/DeleteUser")
	public void DeleteUser(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		String userIds=request.getParameter("userId");
		ObjectMapper mapper = new ObjectMapper(); 
		List<String>userIdList=mapper.readValue(userIds, new TypeReference<List<String>>() {});
		int success=0,failed=0;
		for(String userId:userIdList )
		{
		UserOS uos=new UserOS(os);
		try{
			uos.DeleteUser(userId);
			success++;
		}
		catch (Exception e) {
			failed++;
		}
		}
		String errorMsg="删除成功 "+success+" 个，失败 "+failed+" 个！";
		String msg = mapper.writeValueAsString(errorMsg);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	@RequestMapping("/UpdateUserInfo")
	public void UpdateUserInfo(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		String userId=request.getParameter("userId");
//		String userId="87f3395631fe4b97baf3f210cd2d2efd";
		User user=os.identity().users().get(userId);
		UserBuilder builder=user.toBuilder();
		try
		{
			String userName=request.getParameter("userName");
//			String userName="gwhTest";
			if(userName!=""&&userName!=null)
			{
				builder.name(userName);
			}
		}
		catch(Exception ex){}
		try
		{
			String email=request.getParameter("email");
//			String email="afasfsaf@qq.com";
			if(email!=""&&email!=null)
			{
				builder.email(email);
			}
		}
		catch(Exception ex){}
		try
		{
			String password=request.getParameter("password");
			if(password!=""&&password!=null)
			{
				builder.password(password);
			}
		}
		catch(Exception ex){}
		try
		{
			String mainTenantId=request.getParameter("mainTenantId");
			if(mainTenantId!=""&&mainTenantId!=null)
			{
				builder.tenantId(mainTenantId);
			}
		}
		catch(Exception ex){}
		try
		{
			String enabled=request.getParameter("enabled");
			if(enabled!=""&&enabled!=null)
			{
				if(enabled!=""&&enabled!=null)
				{
					boolean state=user.isEnabled();
					if(enabled=="true")
						state=true;
					if(enabled=="false")
						state=false;
					builder.enabled(state);
				}
			}
		}
		catch(Exception ex){}
		String errorMsg="success";
		try
		{
			os.identity().users().update(builder.build());
		}
		catch (Exception ex) {
			errorMsg="Failed to edit : "+ex.getMessage();
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(errorMsg);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
}
