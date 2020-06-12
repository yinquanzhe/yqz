package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Keypair;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.VNCConsole;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.ComputeServiceOS;
import net.ahwater.ahwaterCloud.compute.ConsoleLog;
import net.ahwater.ahwaterCloud.compute.SecurityGroupOS;
import net.ahwater.ahwaterCloud.compute.ServerOS;
import net.ahwater.ahwaterCloud.compute.entity.AllServerListEle;
import net.ahwater.ahwaterCloud.compute.entity.FlavorBriefEle;
import net.ahwater.ahwaterCloud.compute.entity.FlavorElements;
import net.ahwater.ahwaterCloud.compute.entity.ServerCreateIfo;
import net.ahwater.ahwaterCloud.compute.entity.ServerOverView;
import net.ahwater.ahwaterCloud.compute.entity.ServerProcessMsg;
import net.ahwater.ahwaterCloud.compute.entity.serverListElements;

/**
 * 云主机控制类
 * @author gwh
 *
 */

@Controller
@RequestMapping("/ctr")
public class ServerProcess {
	/**
	 * 向网页传输云主机列表
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listAllServers2") 
	public void listAllServers2(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		List<? extends Server> servers=ServerOS.listAllServersDetail(os);
		List<serverListElements> lse=new ArrayList<>();
		for(Server s:servers){
			serverListElements sle=new serverListElements();
			ServerOS so=new ServerOS(s);
			
			sle.setServerId(so.getServerId());
			sle.setServerName(so.getServerName());
			sle.setImageName(so.getImageName() );
//			sle.setIpAddr(so.getIPAddr());
			sle.setIpAddr(new ArrayList<String>(so.getIPAddrs().values()));
			
			sle.setFlavorName(so.getFlavor().getName());
			sle.setVcpus(so.getFlavor().getVcpus());
			sle.setRam(so.getFlavor().getRam());
			sle.setDisk(so.getFlavor().getDisk());
			
			sle.setStatus(so.getStatus());
			sle.setTimeFromCreated(so.getTimeFromCreated());
			
			lse.add(sle);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(lse);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 通过缓存 数据   向网页传输云主机列表  蔡国成 
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/listAllServers")
	public void listAllServers(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String tokenId=tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
		String tenantId=tkn.getTenant().getId();
		String tenantName=tkn.getTenant().getName();
		
		
		String msg=ComputeServiceOS.listComputeServicesByCache(tenantId, tenantName);
		
		//返回json 格式 
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 传递配置列表
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/flavorList")
	public void flavorList(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Flavor> flavors=ServerOS.listAllFlavors(os);
		List<FlavorBriefEle> lfbe=new ArrayList<>();
		for(Flavor f:flavors){
			FlavorBriefEle fbe=new FlavorBriefEle();
			fbe.setFlavorId(f.getId());
			fbe.setFlavorName(f.getName());
			
			lfbe.add(fbe);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(lfbe);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 传输配置的具体信息
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/flavorDetails")
	public void flavorDetails(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("flavorId");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Flavor fr=os.compute().flavors().get(serverId);
		
		List<FlavorElements> lfbe=new ArrayList<>();
		FlavorElements fes=new FlavorElements();
		fes.setID(fr.getId());
		fes.setName(fr.getName());
		fes.setRam(fr.getRam());
		fes.setVcpus(fr.getVcpus());
		fes.setDisk(fr.getDisk());
		fes.setEphemeral(fr.getEphemeral());
		lfbe.add(fes);
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(lfbe);   
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 传递秘钥对
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/keypairsList")
	public void keypairsList(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Keypair> kps=ServerOS.listAllKeypair(os);
		List<String> keyNames=new ArrayList<>();
		for(Keypair k:kps){
			keyNames.add(k.getName());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(keyNames);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 传递安全组
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/securityGroupsList")
	public void securityGroupsList(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends SecGroupExtension> spe=ServerOS.listSecurityGroups(os);
		List<String> secGroups=new ArrayList<>();
		for(SecGroupExtension sec:spe){
			secGroups.add(sec.getName());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(secGroups);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 创建server实例，有错误的返回错误具体信息
	 * @param sci
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/createServer")
	public void createServer(ServerCreateIfo sci,HttpServletRequest request,
			HttpServletResponse response,HttpSession session) throws IOException{
		String newServerName=sci.getServerName();
		String flavorId=sci.getFlavorId();
		int number=sci.getNumber();
		String imageId=sci.getImageId(); 
		String keypairsName=sci.getKeypairsName();
		
		if(keypairsName.equals("[]")||keypairsName.equals("")||keypairsName.equals("0")){
			keypairsName=null;
		}
		String securityGroupName=sci.getSecurityGroupName();
		String networksStr=sci.getNetworksStr();
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<String> networks=mapper.readValue(networksStr, new TypeReference<List<String>>() {});
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		List<ServerProcessMsg> lspm=new ArrayList<>();
		List<String> ListServerId=new ArrayList<>();
		
		for(int i=0;i<number;i++){
			String createMsg="succ";		
			ServerOS so=new ServerOS();
			String faultMsg=so.createServer(os, newServerName, flavorId, imageId,keypairsName,securityGroupName,networks);
			
			ServerProcessMsg scf=new ServerProcessMsg();
			if(faultMsg==null){
			scf.setMsg(createMsg);
			scf.setServerId(so.getServerId());
			scf.setServerName(newServerName);
			scf.setImageName(so.getImageName());
			scf.setVcpus(so.getFlavor().getVcpus());
			scf.setRam(so.getFlavor().getRam());
			scf.setDisk(so.getFlavor().getDisk());
			}else{
				scf.setMsg(faultMsg);
			}
			lspm.add(scf);
			
			if(faultMsg!=null){
				ListServerId.add(null);
			}else{
				ListServerId.add(so.getServerId());
			}
		}
		
		boolean isInTask=true;
		while(isInTask){
			isInTask=false;
			for(int i=0;i<number;i++){
				if(ListServerId.get(i)!=null){
					boolean bts=(os.compute().servers().get(ListServerId.get(i)).getTaskState()!=null);
					isInTask|=(bts);
				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String msg = mapper.writeValueAsString(lspm);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
		
//		Logger  logger = LoggerFactory.getLogger(this.getClass()); 
//		logger.error(msg);
	}
	
	
	/**
	 * 对server进行操作,如果失败返回错误信息
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/serverAction")
	public void serverAction(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String serverId=request.getParameter("serverId");
		String actionName=request.getParameter("actionName");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		Method action= sos.getClass().getMethod(actionName, OSClientV2.class);
		action.invoke(sos, os);
		
		if(actionName.equals("deleteServer")){
			Thread.sleep(3000);
		}else{
			String state=os.compute().servers().get(serverId).getTaskState();
			while(null!=state){
				state=os.compute().servers().get(serverId).getTaskState();
				Thread.sleep(1000);
			}
		}
		
		ServerProcessMsg scf=new ServerProcessMsg();
		scf.setMsg("succ");
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
		
//		Logger  logger = LoggerFactory.getLogger(this.getClass()); 
//		logger.error(msg);
	}
	
	/**
	 * 批量删除云主机
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/serverBatchDelete")
	public void serverBatchDelete(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String serverIdStr=request.getParameter("serverIdStr");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		
		ObjectMapper mapper = new ObjectMapper(); 
		List<String> serverIdList=mapper.readValue(serverIdStr, new TypeReference<List<String>>() {});
		
		for(String serverId:serverIdList){
			Server s=os.compute().servers().get(serverId);
			ServerOS sos=new ServerOS(s);
			sos.deleteServer(os);
		}
		
		Thread.sleep(1000);
		ServerProcessMsg scf=new ServerProcessMsg();
		scf.setMsg("succ");
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 创建云主机快照
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/createSnapShot")
	public void createSnapShot(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("serverId");
		String snapName=request.getParameter("snapName");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		sos.createNewServerSnapshot(os, snapName);
	
		ServerProcessMsg scf=new ServerProcessMsg();
		scf.setMsg("succ");
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 获取控制台
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/showVNCConsole")
	public void showVNCConsole(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("serverId");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		VNCConsole vc=sos.getVNCConsole(os);
		
		ServerProcessMsg scf=new ServerProcessMsg();
		scf.setMsg(vc.getURL());
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 编辑云主机
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 */
	@RequestMapping("/editServer")
	public void editServer(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("serverId");
		String newServerName=request.getParameter("newServerName");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		sos.editServer(os, newServerName);
		
		ServerProcessMsg scf=new ServerProcessMsg();
		scf.setMsg("succ");
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 调整云主机大小
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 * @throws Exception 
	 */
	@RequestMapping("/resizeServer")
	public void resizeServer(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException, Exception{
		String serverId=request.getParameter("serverId");
		String flavorId=request.getParameter("flavorId");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		ServerProcessMsg scf=new ServerProcessMsg();
		
		ActionResponse act=sos.resize(os, flavorId);
		
		if(act.isSuccess()){
			String state=os.compute().servers().get(serverId).getTaskState();
			while(null!=state){
				Thread.sleep(1000);
				state=os.compute().servers().get(serverId).getTaskState();
			}
			
			sos.comfirmResize(os);
			
			Thread.sleep(1000);
			
			scf.setMsg("succ");
		}else{
			scf.setMsg(act.getFault());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 编辑安全组
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/editSecurityGroups")
	public void editSecurityGroups(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String serverId=request.getParameter("serverId");
		String newSecGroupNames=request.getParameter("newSecGroupNames");
//		String serverId="0a69f4bf-54ed-469c-b1c8-fcbacbbd73dd";
//		String newSecGroupNames="[]";
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		ObjectMapper mapper = new ObjectMapper(); 
		
		List<String> newSecs=mapper.readValue(newSecGroupNames, new TypeReference<List<String>>() {});
		
		List<String> oldSecs=new ArrayList<String>();
		List<? extends SecGroupExtension> old=SecurityGroupOS.ListServerGroup(os, serverId);
		for(SecGroupExtension se:old){
			oldSecs.add(se.getName());
		}
		
		List<String> newSecsCopy=new ArrayList<>(newSecs);
		newSecs.removeAll(oldSecs);//add
		oldSecs.removeAll(newSecsCopy);//remove
		
		for(String str:newSecs){
			sos.addSecurityGroups(os, str);
		}
		for(String str:oldSecs){
			sos.removeSecurityGroups(os, str);
		}
		
		ServerProcessMsg scf=new ServerProcessMsg();
		scf.setMsg("succ");
		
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 绑定浮动IP
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/addFloatingIP")
	public void addFloatingIP(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("serverId");
		String ipAddress=request.getParameter("ipAddress");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		sos.addFloatingIP(os, ipAddress);
		
		ServerProcessMsg scf=new ServerProcessMsg();
		scf.setMsg("succ");
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 重建云主机
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException 
	 * @throws Exception 
	 */
	@RequestMapping("/reBuild")
	public void reBuild(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException, Exception{
		String serverId=request.getParameter("serverId");
		String imageId=request.getParameter("imageId");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		ServerProcessMsg scf=new ServerProcessMsg();
		
		ActionResponse act= sos.reBuildServer(os, imageId);
		
		if(act.isSuccess()){
			scf.setMsg("succ");
			String state=os.compute().servers().get(serverId).getTaskState();
			while(null!=state){
				Thread.sleep(1000);
				state=os.compute().servers().get(serverId).getTaskState();
			}
		}else{
			scf.setMsg(act.getFault());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(scf);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 返回云主机概况
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/serverOverviewShow")
	public void serverOverviewShow(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("serverId");
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		ServerOverView sow=new ServerOverView();
		
		sow.setServerName(sos.getServerName());
		sow.setServerId(serverId);
		sow.setStatus(sos.getStatus());
		sow.setAvailabilityZone(sos.getAvailabilityZone());
		sow.setCreatedTime(sos.getCreatedTime());
		sow.setTimeFromCreated(sos.getTimeFromCreated());
		sow.setHost(sos.getHost());
		sow.setFlavorName(sos.getFlavor().getName());
		sow.setFlavorId(sos.getFlavor().getId());
		sow.setRam(sos.getFlavor().getRam());
		sow.setVcpus(sos.getFlavor().getVcpus());
		sow.setDisk(sos.getFlavor().getDisk());
		sow.setIpAddrs(sos.getIPAddrs());
		sow.setMetaData(sos.listMetaData(os));
		sow.setKeyName(sos.getKeyName());
		sow.setImageId(sos.getImageId());
		sow.setImageName(sos.getImageName());
		sow.setVolumes(sos.getVolumesAttached(os));
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(sow);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 显示控制台日志
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ConsoleLogShow")
	public void consoleLogShow(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("serverId");
		int numLines=Integer.parseInt(request.getParameter("numLines"));
		
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String tokenId=tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
		String tenantId=tkn.getTenant().getId();
		
		String msg=ConsoleLog.getConsoleLog(tenantId,tokenId, serverId, numLines);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 列出对server的操作日志
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listActionForServer")
	public void listActionForServer(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		String serverId=request.getParameter("serverId");
		
		Access ac=(Access) session.getAttribute("Access");
		KeystoneToken tkn=(KeystoneToken) ac.getToken();
		String tokenId=tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
		String tenantId=tkn.getTenant().getId();
		
		String msg=ServerOS.listActionsForServer(tenantId,tokenId, serverId);
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 向网页传输所有租户的实例列表
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listAllTenantServers")
	public void listAllTenantServers(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException{
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		
		List<? extends Server> servers=ServerOS.listAllTenantServes(os);
		List<AllServerListEle> lse=new ArrayList<>();
		for(Server s:servers){
			
			AllServerListEle sle=new AllServerListEle();
			ServerOS so=new ServerOS(s);
			
			String tenantId=so.getTenantId();
			sle.setTenanantName(os.identity().tenants().get(tenantId).getName());
			sle.setHost(so.getHost());
			sle.setServerId(so.getServerId());
			sle.setServerName(so.getServerName());
			sle.setImageName(so.getImageName());
			sle.setIpAddr(new ArrayList<String>(so.getIPAddrs().values()));
			
			sle.setVcpus(so.getFlavor().getVcpus());
			sle.setRam(so.getFlavor().getRam());
			sle.setDisk(so.getFlavor().getDisk());
			
			sle.setStatus(so.getStatus());
			sle.setTimeFromCreated(so.getTimeFromCreated());
			
			lse.add(sle);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(lse);  
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 迁移云主机
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("migrate")
	public void migrate(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String serverId=request.getParameter("serverId");
//		String serverId="6f71fa1d-60d1-41d9-9b3c-45fc28791676";
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		ActionResponse ar=sos.migrate(os);
		
		String state=os.compute().servers().get(serverId).getTaskState();
		while(null!=state){
			Thread.sleep(1000);
			state=os.compute().servers().get(serverId).getTaskState();
		}
		
		String msg=null;
		if(ar.getFault()==null){
			msg="succ";
		}else{
			msg=ar.getFault();
		}
		
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 热迁移云主机
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/liveMigrate")
	public void liveMigrate(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		String serverId=request.getParameter("serverId");
		String host=request.getParameter("host");
		boolean enabled=Boolean.parseBoolean(request.getParameter("enabled"));
		boolean blocked=Boolean.parseBoolean(request.getParameter("blocked"));
		
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		Server s=os.compute().servers().get(serverId);
		ServerOS sos=new ServerOS(s);
		
		ActionResponse ar=sos.liveMigrate(os, host, enabled, blocked);
		
		String state=os.compute().servers().get(serverId).getTaskState();
		while(null!=state){
			Thread.sleep(1000);
			state=os.compute().servers().get(serverId).getTaskState();
		}
		
		String msg=null;
		if(ar.getFault()==null){
			msg="succ";
		}else{
			msg=ar.getFault();
		}
		
		PrintWriter out=null;
		response.setContentType("application/json");
		out=response.getWriter();
		out.write(msg);
	}
	
}
