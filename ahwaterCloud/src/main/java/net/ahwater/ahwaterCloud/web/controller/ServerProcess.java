package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.compute.*;
import net.ahwater.ahwaterCloud.service.compute.ComputeServiceOS;
import net.ahwater.ahwaterCloud.service.compute.ConsoleLog;
import net.ahwater.ahwaterCloud.service.compute.SecurityGroupOS;
import net.ahwater.ahwaterCloud.service.compute.ServerOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.*;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 云主机控制类
 *
 * @author gwh
 */
@RestController
@RequestMapping("/ctr")
public class ServerProcess {

    @Autowired
    private ServerOS serverOS;
    @Autowired
    private ComputeServiceOS computeServiceOS;
    @Autowired
    private SecurityGroupOS securityGroupOS;

    /**
     * 向网页传输云主机列表
     *
     * @param session
     */
    @RequestMapping("/listAllServers2")
    public List<serverListElements> listAllServers2(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        List<? extends Server> servers = serverOS.listAllServersDetail(os);
        List<serverListElements> lse = new ArrayList<>();
        for (Server s : servers) {
            serverListElements sle = new serverListElements();
            ServerOS so = new ServerOS(s);

            sle.setServerId(so.getServerId());
            sle.setServerName(so.getServerName());
            sle.setImageName(so.getImageName());
//			sle.setIpAddr(so.getIPAddr());
            sle.setIpAddr(new ArrayList<>(so.getIPAddrs().values()));

            sle.setFlavorName(so.getFlavor().getName());
            sle.setVcpus(so.getFlavor().getVcpus());
            sle.setRam(so.getFlavor().getRam());
            sle.setDisk(so.getFlavor().getDisk());

            sle.setStatus(so.getStatus());
            sle.setTimeFromCreated(so.getTimeFromCreated());

            lse.add(sle);
        }

        return lse;
    }


    /**
     * 通过缓存 数据   向网页传输云主机列表  蔡国成
     *
     * @param session
     */
    @RequestMapping("/listAllServers")
    public List<AllServerListEle> listAllServers(HttpSession session) {
        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String tokenId = tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
        String tenantId = tkn.getTenant().getId();
        String tenantName = tkn.getTenant().getName();

        return computeServiceOS.listComputeServicesByCache(tenantId, tenantName);
    }

    /**
     * 传递配置列表
     *
     * @param session
     */
    @RequestMapping("/flavorList")
    public List<FlavorBriefEle> flavorList(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Flavor> flavors = serverOS.listAllFlavors(os);
        List<FlavorBriefEle> lfbe = new ArrayList<>();
        for (Flavor f : flavors) {
            FlavorBriefEle fbe = new FlavorBriefEle();
            fbe.setFlavorId(f.getId());
            fbe.setFlavorName(f.getName());

            lfbe.add(fbe);
        }

        return lfbe;
    }

    /**
     * 传输配置的具体信息
     *
     * @param serverId
     * @param session
     */
    @RequestMapping("/flavorDetails")
    public List<FlavorElements> flavorDetails(@RequestParam("flavorId") String serverId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Flavor fr = os.compute().flavors().get(serverId);

        List<FlavorElements> lfbe = new ArrayList<>();
        FlavorElements fes = new FlavorElements();
        fes.setID(fr.getId());
        fes.setName(fr.getName());
        fes.setRam(fr.getRam());
        fes.setVcpus(fr.getVcpus());
        fes.setDisk(fr.getDisk());
        fes.setEphemeral(fr.getEphemeral());
        lfbe.add(fes);

        return lfbe;
    }

    /**
     * 传递秘钥对
     *
     * @param session
     */
    @RequestMapping("/keypairsList")
    public List<String> keypairsList(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Keypair> kps = serverOS.listAllKeypair(os);
        List<String> keyNames = new ArrayList<>();
        for (Keypair k : kps) {
            keyNames.add(k.getName());
        }

        return keyNames;
    }

    /**
     * 传递安全组
     *
     * @param session
     */
    @RequestMapping("/securityGroupsList")
    public List<String> securityGroupsList(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends SecGroupExtension> spe = serverOS.listSecurityGroups(os);
        List<String> secGroups = new ArrayList<>();
        for (SecGroupExtension sec : spe) {
            secGroups.add(sec.getName());
        }

        return secGroups;
    }

    /**
     * 创建server实例，有错误的返回错误具体信息
     *
     * @param sci
     * @param session
     * @throws IOException
     */
    @RequestMapping("/createServer")
    public List<ServerProcessMsg> createServer(ServerCreateIfo sci, HttpSession session) throws IOException {
        String newServerName = sci.getServerName();
        String flavorId = sci.getFlavorId();
        int number = sci.getNumber();
        String imageId = sci.getImageId();
        String keypairsName = sci.getKeypairsName();

        if (keypairsName.equals("[]") || keypairsName.equals("") || keypairsName.equals("0")) {
            keypairsName = null;
        }
        String securityGroupName = sci.getSecurityGroupName();
        String networksStr = sci.getNetworksStr();

        ObjectMapper mapper = new ObjectMapper();
        List<String> networks = mapper.readValue(networksStr, new TypeReference<List<String>>() {
        });

        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        List<ServerProcessMsg> lspm = new ArrayList<>();
        List<String> ListServerId = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            String createMsg = "succ";
            ServerOS so = new ServerOS();
            String faultMsg = so.createServer(os, newServerName, flavorId, imageId, keypairsName, securityGroupName, networks);

            ServerProcessMsg scf = new ServerProcessMsg();
            if (faultMsg == null) {
                scf.setMsg(createMsg);
                scf.setServerId(so.getServerId());
                scf.setServerName(newServerName);
                scf.setImageName(so.getImageName());
                scf.setVcpus(so.getFlavor().getVcpus());
                scf.setRam(so.getFlavor().getRam());
                scf.setDisk(so.getFlavor().getDisk());
            } else {
                scf.setMsg(faultMsg);
            }
            lspm.add(scf);

            if (faultMsg != null) {
                ListServerId.add(null);
            } else {
                ListServerId.add(so.getServerId());
            }
        }

        boolean isInTask = true;
        while (isInTask) {
            isInTask = false;
            for (int i = 0; i < number; i++) {
                if (ListServerId.get(i) != null) {
                    boolean bts = (os.compute().servers().get(ListServerId.get(i)).getTaskState() != null);
                    isInTask |= (bts);
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

        return lspm;

//		Logger logger = LoggerFactory.getLogger(this.getClass());
//		logger.error(msg);
    }


    /**
     * 对server进行操作,如果失败返回错误信息
     *
     * @param serverId
     * @param actionName
     * @param session
     * @throws Exception
     */
    @RequestMapping("/serverAction")
    public ServerProcessMsg serverAction(String serverId, String actionName, HttpSession session) throws Exception {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        Method action = sos.getClass().getMethod(actionName, OSClientV2.class);
        action.invoke(sos, os);

        if (actionName.equals("deleteServer")) {
            Thread.sleep(3000);
        } else {
            String state = os.compute().servers().get(serverId).getTaskState();
            while (null != state) {
                state = os.compute().servers().get(serverId).getTaskState();
                Thread.sleep(1000);
            }
        }

        ServerProcessMsg scf = new ServerProcessMsg();
        scf.setMsg("succ");

        return scf;

//		Logger  logger = LoggerFactory.getLogger(this.getClass()); 
//		logger.error(msg);
    }

    /**
     * 批量删除云主机
     *
     * @param request
     * @param response
     * @param session
     * @throws Exception
     */
    @RequestMapping("/serverBatchDelete")
    public ServerProcessMsg serverBatchDelete(String serverIdStr, HttpSession session) throws Exception {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));

        ObjectMapper mapper = new ObjectMapper();
        List<String> serverIdList = mapper.readValue(serverIdStr, new TypeReference<List<String>>() {
        });

        for (String serverId : serverIdList) {
            Server s = os.compute().servers().get(serverId);
            ServerOS sos = new ServerOS(s);
            sos.deleteServer(os);
        }

        Thread.sleep(1000);
        ServerProcessMsg scf = new ServerProcessMsg();
        scf.setMsg("succ");

        return scf;
    }


    /**
     * 创建云主机快照
     *
     * @param serverId
     * @param snapName
     * @param session
     */
    @RequestMapping("/createSnapShot")
    public ServerProcessMsg createSnapShot(String serverId, String snapName, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        sos.createNewServerSnapshot(os, snapName);

        ServerProcessMsg scf = new ServerProcessMsg();
        scf.setMsg("succ");

        return scf;
    }

    /**
     * 获取控制台
     *
     * @param serverId
     * @param session
     */
    @RequestMapping("/showVNCConsole")
    public ServerProcessMsg showVNCConsole(String serverId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        VNCConsole vc = sos.getVNCConsole(os);

        ServerProcessMsg scf = new ServerProcessMsg();
        scf.setMsg(vc.getURL());

        return scf;
    }

    /**
     * 编辑云主机
     *
     * @param session
     */
    @RequestMapping("/editServer")
    public ServerProcessMsg editServer(String serverId, String newServerName, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        sos.editServer(os, newServerName);

        ServerProcessMsg scf = new ServerProcessMsg();
        scf.setMsg("succ");

        return scf;
    }

    /**
     * 调整云主机大小
     *
     * @param serverId
     * @param flavorId
     * @param session
     * @throws Exception
     */
    @RequestMapping("/resizeServer")
    public ServerProcessMsg resizeServer(String serverId, String flavorId, HttpSession session) throws Exception {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);
        ServerProcessMsg scf = new ServerProcessMsg();

        ActionResponse act = sos.resize(os, flavorId);

        if (act.isSuccess()) {
            String state = os.compute().servers().get(serverId).getTaskState();
            while (null != state) {
                Thread.sleep(1000);
                state = os.compute().servers().get(serverId).getTaskState();
            }

            sos.comfirmResize(os);

            Thread.sleep(1000);

            scf.setMsg("succ");
        } else {
            scf.setMsg(act.getFault());
        }

        return scf;
    }

    /**
     * 编辑安全组
     *
     * @param serverId
     * @param newSecGroupNames
     * @param session
     * @throws Exception
     */
    @RequestMapping("/editSecurityGroups")
    public ServerProcessMsg editSecurityGroups(String serverId, String newSecGroupNames, HttpSession session) throws Exception {
//		String serverId="0a69f4bf-54ed-469c-b1c8-fcbacbbd73dd";
//		String newSecGroupNames="[]";

        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        ObjectMapper mapper = new ObjectMapper();
        List<String> newSecs = mapper.readValue(newSecGroupNames, new TypeReference<List<String>>() {
        });

        List<String> oldSecs = new ArrayList<>();
        List<? extends SecGroupExtension> old = securityGroupOS.ListServerGroup(os, serverId);
        for (SecGroupExtension se : old) {
            oldSecs.add(se.getName());
        }

        List<String> newSecsCopy = new ArrayList<>(newSecs);
        newSecs.removeAll(oldSecs);//add
        oldSecs.removeAll(newSecsCopy);//remove

        for (String str : newSecs) {
            sos.addSecurityGroups(os, str);
        }
        for (String str : oldSecs) {
            sos.removeSecurityGroups(os, str);
        }

        ServerProcessMsg scf = new ServerProcessMsg();
        scf.setMsg("succ");

        return scf;
    }

    /**
     * 绑定浮动IP
     *
     * @param serverId
     * @param ipAddress
     * @param session
     */
    @RequestMapping("/addFloatingIP")
    public ServerProcessMsg addFloatingIP(String serverId, String ipAddress, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        sos.addFloatingIP(os, ipAddress);

        ServerProcessMsg scf = new ServerProcessMsg();
        scf.setMsg("succ");

        return scf;
    }

    /**
     * 重建云主机
     *
     * @param serverId
     * @param imageId
     * @param session
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping("/reBuild")
    public ServerProcessMsg reBuild(String serverId, String imageId, HttpSession session) throws Exception {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);
        ServerProcessMsg scf = new ServerProcessMsg();

        ActionResponse act = sos.reBuildServer(os, imageId);

        if (act.isSuccess()) {
            scf.setMsg("succ");
            String state = os.compute().servers().get(serverId).getTaskState();
            while (null != state) {
                Thread.sleep(1000);
                state = os.compute().servers().get(serverId).getTaskState();
            }
        } else {
            scf.setMsg(act.getFault());
        }

        return scf;
    }

    /**
     * 返回云主机概况
     *
     * @param serverId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/serverOverviewShow")
    public ServerOverView serverOverviewShow(String serverId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        ServerOverView sow = new ServerOverView();

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

        return sow;
    }

    /**
     * 显示控制台日志
     *
     * @param serverId
     * @param numLines
     * @param session
     * @throws IOException
     */
    @RequestMapping("/ConsoleLogShow")
    public String consoleLogShow(String serverId, int numLines, HttpSession session) throws IOException {
        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String tokenId = tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
        String tenantId = tkn.getTenant().getId();

        return ConsoleLog.getConsoleLog(tenantId, tokenId, serverId, numLines);
    }

    /**
     * 列出对server的操作日志
     *
     * @param serverId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/listActionForServer")
    public String listActionForServer(String serverId, HttpSession session) throws IOException {
        Access ac = (Access) session.getAttribute("Access");
        KeystoneToken tkn = (KeystoneToken) ac.getToken();
        String tokenId = tkn.getId();
//		OSClientV2 os=OSFactory.clientFromAccess(ac);
        String tenantId = tkn.getTenant().getId();

        return serverOS.listActionsForServer(tenantId, tokenId, serverId);
    }

    /**
     * 向网页传输所有租户的实例列表
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/listAllTenantServers")
    public List<AllServerListEle> listAllTenantServers(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);

        List<? extends Server> servers = serverOS.listAllTenantServes(os);
        List<AllServerListEle> lse = new ArrayList<>();
        for (Server s : servers) {

            AllServerListEle sle = new AllServerListEle();
            ServerOS so = new ServerOS(s);

            String tenantId = so.getTenantId();
            sle.setTenanantName(os.identity().tenants().get(tenantId).getName());
            sle.setHost(so.getHost());
            sle.setServerId(so.getServerId());
            sle.setServerName(so.getServerName());
            sle.setImageName(so.getImageName());
            sle.setIpAddr(new ArrayList<>(so.getIPAddrs().values()));

            sle.setVcpus(so.getFlavor().getVcpus());
            sle.setRam(so.getFlavor().getRam());
            sle.setDisk(so.getFlavor().getDisk());

            sle.setStatus(so.getStatus());
            sle.setTimeFromCreated(so.getTimeFromCreated());

            lse.add(sle);
        }

        return lse;
    }


    /**
     * 迁移云主机
     *
     * @param serverId
     * @param session
     * @throws Exception
     */
    @RequestMapping("migrate")
    public String migrate(String serverId, HttpSession session) throws Exception {
//		String serverId="6f71fa1d-60d1-41d9-9b3c-45fc28791676";

        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        ActionResponse ar = sos.migrate(os);

        String state = os.compute().servers().get(serverId).getTaskState();
        while (null != state) {
            Thread.sleep(1000);
            state = os.compute().servers().get(serverId).getTaskState();
        }

        String msg;
        if (ar.getFault() == null) {
            msg = "succ";
        } else {
            msg = ar.getFault();
        }

        return msg;
    }

    /**
     * 热迁移云主机
     *
     * @param serverId
     * @param host
     * @param enabled
     * @param blocked
     * @param session
     * @throws Exception
     */
    @RequestMapping("/liveMigrate")
    public String liveMigrate(String serverId, String host,boolean enabled,boolean blocked, HttpSession session) throws Exception {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        Server s = os.compute().servers().get(serverId);
        ServerOS sos = new ServerOS(s);

        ActionResponse ar = sos.liveMigrate(os, host, enabled, blocked);

        String state = os.compute().servers().get(serverId).getTaskState();
        while (null != state) {
            Thread.sleep(1000);
            state = os.compute().servers().get(serverId).getTaskState();
        }

        String msg;
        if (ar.getFault() == null) {
            msg = "succ";
        } else {
            msg = ar.getFault();
        }

        return msg;
    }

}
