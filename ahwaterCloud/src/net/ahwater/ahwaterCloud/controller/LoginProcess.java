package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Role;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.identityV3.entity.DefaultAndTenantList;
import net.ahwater.ahwaterCloud.identityV3.entity.LoginStatus;
import net.ahwater.ahwaterCloud.identityV3.entity.TenantNameId;
import net.ahwater.ahwaterCloud.util.JdbcUtil;

/**
 * 登录验证控制类
 * @author gwh
 *
 */

@Controller
@RequestMapping("/ctr")
public class LoginProcess {
	
	/**
	 * 用户登录验证
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/LoginProcess")
	public void login(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String userName=request.getParameter("username");
		String pwd=request.getParameter("userpwd");
		
		IdentityOS uc= new IdentityOS(userName,pwd);
		
		LoginStatus ls=new LoginStatus();
		if(uc.init()){
			ls.setStatu("succ");
			if(uc.isAdmin()){
				ls.setIsAdmin("admin");
				
			}else{
				ls.setIsAdmin("user");
			}
		}else{
			ls.setStatu("fail");
		}
		
		if(ls.getStatu().equals("succ")){
			session.setAttribute("Access", uc.getOSClientV2().getAccess());
			session.setAttribute("userName", userName);
			session.setAttribute("pwd", pwd);
			session.setAttribute("defaultTenant", uc.getDefaultTenant());
			
			List<? extends Tenant> tenants=uc.getTenants();
			List<TenantNameId> tenantNameIds= new ArrayList<>();
			for(Tenant t:tenants){
				TenantNameId tni=new TenantNameId();
				tni.setTenantId(t.getId());
				tni.setTenantName(t.getName());
				
				tenantNameIds.add(tni);
			}
			session.setAttribute("tenantNameIds", tenantNameIds);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(ls);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 切换租户
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 * @throws Exception 
	 */
	@RequestMapping("/changeTenant")
	public void changeTenant(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException, Exception{
		String tenantName=request.getParameter("tenantName");
		String userName=(String) session.getAttribute("userName");
		String pwd=(String) session.getAttribute("pwd");
		
		String endpoint=IdentityOS.getAccessAPI().get("keystone");
		
		OSClientV2 os=OSFactory.builderV2()
				.endpoint(endpoint)
				.credentials(userName, pwd)
				.tenantName(tenantName)
				.authenticate();
		session.setAttribute("Access", os.getAccess());
		
		List<? extends Role> rl=os.getAccess().getUser().getRoles();
		
		boolean isAdmin=false;
		if(rl.size()==1){
			Iterator<? extends Role> it=rl.iterator();
			if(it.next().getName().equals("admin")){
				isAdmin=true;
			}
		}else{
			for(Role r:rl){
				if(r.getName().equals("admin")){
					isAdmin=true;
				}
			}
		}
		
		String msg="user";
		if(isAdmin){
			msg="admin";
		}
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 用户的租户列表
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/tenantsListOfUser")
	public void tenantsListOfUser(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		
		Tenant defaultTetant=(Tenant) session.getAttribute("defaultTenant");
		@SuppressWarnings("unchecked")
		List<TenantNameId> tenantNames=(List<TenantNameId>) session.getAttribute("tenantNameIds");
		
		DefaultAndTenantList adtl=new DefaultAndTenantList();
		adtl.setDefaultName(defaultTetant.getName());
		adtl.setDefaultId(defaultTetant.getId());
		adtl.setTenantNames(tenantNames);
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(adtl);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
}
