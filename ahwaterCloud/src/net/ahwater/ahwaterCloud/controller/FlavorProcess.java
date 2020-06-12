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
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.FlavorOS;
import net.ahwater.ahwaterCloud.compute.entity.VMManager.FlavorAllListEle;
import net.ahwater.ahwaterCloud.compute.entity.VMManager.FlavorCreateIfo;
import net.ahwater.ahwaterCloud.compute.entity.VMManager.FlavorEditIfo;
import net.ahwater.ahwaterCloud.compute.entity.VMManager.TenantListEle;

/**
 * 云资源模板控制类
 * @author gwh
 *
 */

@Controller
@RequestMapping("/ctr")
public class FlavorProcess {
	
	/**
	 * 云主机类型列表
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/listAllFlavors")
	public void listAllFlavors(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Flavor> lfr=FlavorOS.listPublicAndNonpublicFlavors(os);
		
		List<FlavorAllListEle> lfale=new ArrayList<>();
		for(Flavor f:lfr){
			FlavorOS fo=new FlavorOS(f);
			FlavorAllListEle fale=new FlavorAllListEle();
			fale.setName(f.getName());
			fale.setVcpus(f.getVcpus());
			fale.setRam(f.getRam());
			fale.setDisk(f.getDisk());
			fale.setEphemeral(f.getEphemeral());
			fale.setSwap(f.getSwap());
			fale.setID(f.getId());
			fale.setPublic(f.isPublic());
			fale.setExtraSpecs(fo.listExtraSpecsForAFlavor(os));
			
			lfale.add(fale);
		}
		
		ObjectMapper mapper=new ObjectMapper();
		String msg=mapper.writeValueAsString(lfale);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 删除云主机类型
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/deleteFlavor")
	public void deleteFlavor(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String flavorId=request.getParameter("flavorId");
//		String flavorId="b9f2f554-927a-4f91-8381-134da56cae21";
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		FlavorOS fo=new FlavorOS(os.compute().flavors().get(flavorId));
		ActionResponse ac= fo.deleteFlavor(os);
		
		String msg=null;
		if(ac.getFault()==null){
			msg="succ";
		}else{
			msg=ac.getFault();
		}
		
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 批量删除云主机
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/patchDeleteFlavor")
	public void patchDeleteFlavor(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String flavorIdListStr=request.getParameter("flavorIdListStr");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		ObjectMapper mapper=new ObjectMapper();
		List<String> flavorIdList=mapper.readValue(flavorIdListStr, new TypeReference<List<String>>() {});
		
		for(String flavorId:flavorIdList){
			FlavorOS fo=new FlavorOS(os.compute().flavors().get(flavorId));
			fo.deleteFlavor(os);
		}
		
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write("succ");
	}
	
	/**
	 * 创建云主机类型
	 * @param info
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/createFlavor")
	public void createFlavor(FlavorCreateIfo info,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String flavorName=info.getFlavorName();
		int ram=info.getRam();
		int vcpus=info.getVcpus();
		int disk=info.getDisk();
		int ephemeral=info.getEphemeral();
		int swap=info.getSwap();
		String tenantIdListStr=info.getTenantIdListStr();
		
		boolean isPublic=true;
		if(tenantIdListStr!=null && !tenantIdListStr.equals("")){
			isPublic=false;
		}
//		if(!tenantIdListStr.equals("")){
//			isPublic=false;
//		}
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		FlavorOS fo=new FlavorOS();
		String msg=fo.createFlavor(os, flavorName, ram, vcpus, disk, ephemeral, swap, isPublic);
		
		if(isPublic==false){
			ObjectMapper mapper=new ObjectMapper();
			List<String> newTenantIdList=mapper.readValue(tenantIdListStr, new TypeReference<List<String>>() {});
			List<String> oldTenantIdList=fo.listTenantAccess(os);
			List<String> newTenantIdListCpy=new ArrayList<>(newTenantIdList);
			
			newTenantIdList.removeAll(oldTenantIdList);
			oldTenantIdList.removeAll(newTenantIdListCpy);
			
			for(String id:newTenantIdList){
				fo.addTenantAccess(os, id);
			}
			for(String id:oldTenantIdList){
				fo.removeTenantAccess(os, id);
			}
		}
		
		if(msg==null){
			msg="succ";
		}
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 云主机类型现有的租户
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 */
	@RequestMapping("/haveTenantList")
	public void haveTenantList(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String flavorId=request.getParameter("flavorId");
//		String flavorId="d36a834e-4d9e-444a-b0b0-0427a548af98";
				
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		FlavorOS fo=new FlavorOS(os.compute().flavors().get(flavorId));
		
		List<TenantListEle> oldTenantIdList=fo.haveTenantList(os);
		
		ObjectMapper mapper=new ObjectMapper();
		String msg=mapper.writeValueAsString(oldTenantIdList);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 修改使用权
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/changeTenantAccess")
	public void changeTenantAccess(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String tenantIdListStr=request.getParameter("tenantIdListStr");
		String flavorId=request.getParameter("flavorId");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		FlavorOS fo=new FlavorOS(os.compute().flavors().get(flavorId));
		
		ObjectMapper mapper=new ObjectMapper();
		List<String> newTenantIdList=mapper.readValue(tenantIdListStr, new TypeReference<List<String>>() {});
		List<String> oldTenantIdList=fo.listTenantAccess(os);
		List<String> newTenantIdListCpy=new ArrayList<>(newTenantIdList);
		
		newTenantIdList.removeAll(oldTenantIdList);
		oldTenantIdList.removeAll(newTenantIdListCpy);
		
		for(String id:newTenantIdList){
			fo.addTenantAccess(os, id);
		}
		for(String id:oldTenantIdList){
			fo.removeTenantAccess(os, id);
		}
		
		String msg="succ";
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 列出所有的租户
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/listAllTenants")
	public void listAllTenants(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		
		List<TenantListEle> ltle=FlavorOS.listAllTenant(os);
		ObjectMapper mapper=new ObjectMapper();
		String msg=mapper.writeValueAsString(ltle);
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 编辑云主机类型
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping("/editFlavor")
	public void editFlavor(FlavorEditIfo info,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws JsonParseException, JsonMappingException, IOException{
		String flavorID=info.getFlavorId();
		String flavorName=info.getFlavorName();
		int ram=info.getRam();
		int vcpus=info.getVcpus();
		int disk=info.getDisk();
		int ephemeral=info.getEphemeral();
		int swap=info.getSwap();
		String tenantIdListStr=info.getTenantIdListStr();
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		
		FlavorOS fo=new FlavorOS(os.compute().flavors().get(flavorID));
		fo.deleteFlavor(os);
		
		boolean isPublic=false;
		if(tenantIdListStr.equals("[]") || tenantIdListStr==null || tenantIdListStr.equals("")){
			isPublic=true;
		}
		
		String msg=fo.createFlavor(os, flavorName, ram, vcpus, disk, ephemeral, swap, isPublic);
		
		if(isPublic==false){
			ObjectMapper mapper=new ObjectMapper();
			List<String> newTenantIdList=mapper.readValue(tenantIdListStr, new TypeReference<List<String>>() {});
			List<String> oldTenantIdList=fo.listTenantAccess(os);
			List<String> newTenantIdListCpy=new ArrayList<>(newTenantIdList);
			
			newTenantIdList.removeAll(oldTenantIdList);
			oldTenantIdList.removeAll(newTenantIdListCpy);
			
			for(String id:newTenantIdList){
				fo.addTenantAccess(os, id);
			}
			for(String id:oldTenantIdList){
				fo.removeTenantAccess(os, id);
			}
		}
		
		if(msg==null){
			msg="succ";
		}
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		out.write(msg);
		
	}
}
