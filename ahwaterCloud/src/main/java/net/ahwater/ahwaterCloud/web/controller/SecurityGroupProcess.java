package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.compute.RuleCreationInfo;
import net.ahwater.ahwaterCloud.entity.compute.RuleInfo;
import net.ahwater.ahwaterCloud.entity.compute.SecurityGroupBriefInfo;
import net.ahwater.ahwaterCloud.service.compute.RuleOS;
import net.ahwater.ahwaterCloud.service.compute.SecurityGroupOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 安全组控制类
 * 定义了对安全组的相关操作
 */
@RestController
@RequestMapping("/contr")
public class SecurityGroupProcess {

    @Autowired
    private SecurityGroupOS securityGroupOS;
    @Autowired
    private RuleOS ruleOS;

    /**
     * 显示所有安全组
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/ListAllSecurityGroup")
    public List<SecurityGroupBriefInfo> ListAllSecurityGroup(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends SecGroupExtension> groups = securityGroupOS.ListAllSecurityGroup(os);
        List<SecurityGroupBriefInfo> groupBriefInfoList = new ArrayList<>();
        for (SecGroupExtension group : groups) {
            SecurityGroupBriefInfo groupBriefInfo = new SecurityGroupBriefInfo();
            SecurityGroupOS groupOs = new SecurityGroupOS(group);
            groupBriefInfo.setSecurityGroupId(groupOs.getId());
            groupBriefInfo.setSecurityGroupName(groupOs.getName());
            groupBriefInfo.setDescription(groupOs.getDescription());
            groupBriefInfoList.add(groupBriefInfo);
        }

        return groupBriefInfoList;
    }

    /**
     * 获取指定主机所属安全组和可用安全组
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/ListServerGroups")
    public List<List<SecurityGroupBriefInfo>> ListServerGroups(String serverId, HttpSession session) throws IOException {
        //String serverId="8ddc300b-8c1c-413f-8d3d-e4cab5570767";
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends SecGroupExtension> usedGroups = securityGroupOS.ListServerGroup(os, serverId);
        List<? extends SecGroupExtension> usableGroups = securityGroupOS.ListAllSecurityGroup(os);
        usableGroups.removeAll(usedGroups);
        List<List<SecurityGroupBriefInfo>> returnList = new ArrayList<>();
        List<SecurityGroupBriefInfo> usedGroupList = new ArrayList<>();
        List<SecurityGroupBriefInfo> usableGroupList = new ArrayList<>();
        process(usedGroups, usedGroupList);
        returnList.add(usedGroupList);
        process(usableGroups, usableGroupList);
        returnList.add(usableGroupList);
        returnList.add(usedGroupList);

        return returnList;
    }

    private void process(List<? extends SecGroupExtension> usedGroups, List<SecurityGroupBriefInfo> usedGroupList) {
        for (SecGroupExtension group : usedGroups) {
            SecurityGroupBriefInfo groupBriefInfo = new SecurityGroupBriefInfo();
            SecurityGroupOS groupOs = new SecurityGroupOS(group);
            groupBriefInfo.setSecurityGroupId(groupOs.getId());
            groupBriefInfo.setSecurityGroupName(groupOs.getName());
            usedGroupList.add(groupBriefInfo);
        }
    }

    /**
     * 创建安全组
     *
     * @param securityGroupName
     * @param description
     * @param session
     */
    @RequestMapping("/CreateSecurityGroup")
    public String CreateSecurityGroup(String securityGroupName, String description, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String errorMsg = "success";
        SecurityGroupOS groupOs = new SecurityGroupOS();
        try {
            groupOs.CreateSecurityGroup(os, securityGroupName, description);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        return errorMsg;
    }

    /**
     * 编辑安全组
     *
     * @param securityGroupId
     * @param securityGroupName
     * @param description
     * @param session
     */
    @RequestMapping("/EditSecurityGroup")
    public String EditSecurityGroup(String securityGroupId, String securityGroupName, String description, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String errorMsg = "success";
        SecurityGroupOS groupOs = new SecurityGroupOS();
        try {
            groupOs.UpdateSecurityGroup(os, securityGroupId, securityGroupName, description);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }

        return errorMsg;
    }

    /**
     * 删除安全组
     *
     * @param securityGroupIds
     * @param session
     * @throws IOException
     */
    @RequestMapping("/DeleteSecurityGroup")
    public String DeleteSecurityGroup(@RequestParam("securityGroupId") String securityGroupIds, HttpSession session) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> securityGroupIdList = mapper.readValue(securityGroupIds, new TypeReference<List<String>>() {
        });
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        SecurityGroupOS groupOs = new SecurityGroupOS();
        int success = 0, failed = 0;
        for (String securityGroupId : securityGroupIdList) {
            try {
                groupOs.DeleteSecurityGroup(os, securityGroupId);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }
        String errorMsg = "删除成功 " + success + " 个，失败 " + failed + " 个！";
        return errorMsg;
    }

    /**
     * 显示此安全组所属规则
     *
     * @param securityGroupId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/ListGroupRules")
    public List<RuleInfo> ListGroupRules(String securityGroupId, HttpSession session) {
        //String securityGroupId="d061dce6-a315-42f5-a6c2-0699b8d307bf";
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return getGroupRules(os, securityGroupId);
    }

    /**
     * 按安全组Id查询该安全组的所有规则
     *
     * @param os
     * @param securityGroupId
     * @return
     */
    private List<RuleInfo> getGroupRules(OSClientV2 os, String securityGroupId) {
        List<? extends SecurityGroupRule> ruleList = os.networking().securitygroup().get(securityGroupId).getRules();
        List<RuleInfo> ruleInfoList = new ArrayList<>();
        for (SecurityGroupRule r : ruleList) {
            RuleOS ros = new RuleOS(r);
            RuleInfo ruleInfo = new RuleInfo();
            ruleInfo.setRuleId(ros.getId());
            ruleInfo.setDirection(ros.getDirection());
            ruleInfo.setEtherType(ros.getEtherType());
            ruleInfo.setProtocol(ros.getProtocol());
            ruleInfo.setPortRange(ros.getPortRangeStr());
            ruleInfo.setCidr(ros.getRemoteIpPrefix());
            String remoteGroupId = ros.getRemoteGroupId();
            if (remoteGroupId != null) {
                SecurityGroup remoteGroup = os.networking().securitygroup().get(remoteGroupId);
                ruleInfo.setRemoteSecurityGroup(remoteGroup.getName());
            } else {
                ruleInfo.setRemoteSecurityGroup(null);
            }
            ruleInfoList.add(ruleInfo);
        }
        return ruleInfoList;
    }

    /**
     * 创建安全组规则
     *
     * @param ruleCreationInfo
     * @param session
     * @throws IOException
     */
    @RequestMapping("/CreateRule")
    public String CreateRule(RuleCreationInfo ruleCreationInfo, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        String errorMsg = "success";
        RuleCreationInfo info = new RuleCreationInfo();
        info.setSecurityGroupId(ruleCreationInfo.getSecurityGroupId());
        info.setDirection(ruleCreationInfo.getDirection());
        info.setProtocol(ruleCreationInfo.getProtocol());
        info.setHasPort(ruleCreationInfo.isHasPort());
        info.setPortRangeMax(ruleCreationInfo.getPortRangeMax());
        info.setPortRangeMin(ruleCreationInfo.getPortRangeMin());
        info.setCIDR(ruleCreationInfo.isCIDR());
        info.setRemoteIpPrefix(ruleCreationInfo.getRemoteIpPrefix());
        info.setRemoteGroupId(ruleCreationInfo.getRemoteGroupId());
        info.setEtherType(ruleCreationInfo.getEtherType());

        try {
            ruleOS.CreateRule(os, info);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        return errorMsg;
    }

    /**
     * 删除规则
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("DeleteRule")
    public String DeleteRule(@RequestParam("ruleId") String ruleIds, HttpSession session) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> ruleIdList = mapper.readValue(ruleIds, new TypeReference<List<String>>() {
        });
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        int success = 0, failed = 0;
        for (String ruleId : ruleIdList) {
            try {
                ruleOS.DeleteRule(os, ruleId);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }
        String errorMsg = "删除成功 " + success + " 个，失败 " + failed + " 个！";

        return errorMsg;
    }
}
