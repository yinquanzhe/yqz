package net.ahwater.ahwaterCloud.summary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.compute.QuotaSet;
import org.openstack4j.model.compute.builder.QuotaSetUpdateBuilder;
import org.openstack4j.model.network.NetQuota;
import org.openstack4j.model.network.builder.NetQuotaBuilder;
import org.openstack4j.model.storage.block.BlockQuotaSet;
import org.openstack4j.model.storage.block.builder.BlockQuotaSetBuilder;

import net.ahwater.ahwaterCloud.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.summary.entity.QuotaEntity;

public class QuotaOs {
	QuotaSet quotaSet;
	BlockQuotaSet blockQuotaSet;
	NetQuota netQuota;
	public QuotaOs(OSClientV2 os,String tenantId) {
		quotaSet=os.compute().quotaSets().get(tenantId);
		blockQuotaSet=os.blockStorage().quotaSets().get(tenantId);
		quotaSet.getCores();
		quotaSet.getRam();
		quotaSet.getInstances();
		quotaSet.getFloatingIps();
		quotaSet.getInjectedFileContentBytes();
		quotaSet.getInjectedFilePathBytes();
		quotaSet.getInjectedFiles();
		quotaSet.getKeyPairs();
		quotaSet.getMetadataItems();
		quotaSet.getSecurityGroups();
		blockQuotaSet.getSnapshots();
		blockQuotaSet.getVolumes();
		blockQuotaSet.getGigabytes();
	}
	/**
	 * 获取租户配额
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	public static QuotaEntity GetTenantQuota(OSClientV2 os,String tenantId)
	{
		os.perspective(Facing.ADMIN);
		QuotaSet quotaSet = os.compute().quotaSets().get(tenantId);
		BlockQuotaSet blockQuotaSet=os.blockStorage().quotaSets().get(tenantId);
		NetQuota netQuota=os.networking().quotas().get(tenantId);
		QuotaEntity limit=new QuotaEntity();
		 	limit.setMaxImageMeta(quotaSet.getMetadataItems());
	        limit.setMaxPersonality(quotaSet.getInjectedFiles());
	        limit.setMaxPersonalitySize(quotaSet.getInjectedFileContentBytes());
	        //limit.setMaxServerGroupMembers(quotaSet.get);
	        //limit.setMaxServerGroups(Integer.parseInt(props.getProperty("maxServerGroups")));
	        //limit.setMaxServerMeta(Integer.parseInt(props.getProperty("maxServerMeta")));
	        limit.setMaxTotalCores(quotaSet.getCores());
	        limit.setMaxTotalInstances(quotaSet.getInstances());
	        limit.setMaxTotalRAMSize(quotaSet.getRam());
	        limit.setMaxTotalKeypairs(quotaSet.getKeyPairs());
	        
	        limit.setMaxGigabytes(blockQuotaSet.getGigabytes());
	        limit.setMaxSnapShots(blockQuotaSet.getSnapshots());
	        limit.setMaxVolumes(blockQuotaSet.getVolumes());
	
	        limit.setMaxTotalFloatingIps(netQuota.getFloatingIP());
	        limit.setMaxSecurityGroupRules(netQuota.getSecurityGroupRule());
	        limit.setMaxSecurityGroups(netQuota.getSecurityGroup());
	        limit.setMaxSubnet(netQuota.getSubnet());
	        limit.setMaxRouter(netQuota.getRouter());
	        limit.setMaxPort(netQuota.getPort());
	        limit.setMaxNetwork(netQuota.getNetwork());
	        return limit;
	}
	/**
	 * 更新租户配额（面向管理员）
	 * @param os
	 * @param tenantId
	 * @param limit
	 */
	public static void UpdateTenantQuota(OSClientV2 os,String tenantId,QuotaEntity limit)
	{
		os.perspective(Facing.ADMIN);
		QuotaSetUpdateBuilder qBuilder=Builders.quotaSet();
		BlockQuotaSetBuilder bBuilder=Builders.blockQuotaSet();
		NetQuotaBuilder nBuilder=Builders.netQuota();
		
		qBuilder.metadataItems(limit.getMaxImageMeta())
					.injectedFiles(limit.getMaxPersonality())
					.injectedFileContentBytes(limit.getMaxPersonalitySize())
					.cores(limit.getMaxTotalCores())
					.keyPairs(limit.getMaxTotalKeypairs())
					.instances(limit.getMaxTotalInstances())
					.ram(limit.getMaxTotalRAMSize());
		bBuilder.gigabytes(limit.getMaxGigabytes())
		            .snapshots(limit.getMaxSnapShots())
		            .volumes(limit.getMaxVolumes());
		nBuilder.floatingIP(limit.getMaxTotalFloatingIps())
					.securityGroup(limit.getMaxSecurityGroups())
					.securityGroupRule(limit.getMaxSecurityGroupRules())
					.subnet(limit.getMaxSubnet())
					.router(limit.getMaxRouter())
					.port(limit.getMaxPort())
					.network(limit.getMaxNetwork());
					
		
		os.compute().quotaSets().updateForTenant(tenantId, qBuilder.build());
		os.blockStorage().quotaSets().updateForTenant(tenantId, bBuilder.build());
		os.networking().quotas().updateForTenant(tenantId, nBuilder.build());
	}
	/**
	 * 获取默认值
	 * @return
	 * @throws IOException
	 */
	public static  QuotaEntity GetLimits() throws IOException
	{
		QuotaEntity limit=new QuotaEntity();
		
		Properties props = new Properties();
        InputStream in = IdentityOS.class.getResourceAsStream("/config/Limits.properties");//默认值配置文件地址
        props.load(in);
        in.close();
        limit.setMaxImageMeta(Integer.parseInt(props.getProperty("maxImageMeta")));
        limit.setMaxPersonality(Integer.parseInt(props.getProperty("maxPersonality")));
        limit.setMaxPersonalitySize(Integer.parseInt(props.getProperty("maxPersonalitySize")));
        limit.setMaxServerGroupMembers(Integer.parseInt(props.getProperty("maxServerGroupMembers")));
        limit.setMaxServerGroups(Integer.parseInt(props.getProperty("maxServerGroups")));
        limit.setMaxServerMeta(Integer.parseInt(props.getProperty("maxServerMeta")));
        limit.setMaxTotalCores(Integer.parseInt(props.getProperty("maxTotalCores")));
        limit.setMaxTotalFloatingIps(Integer.parseInt(props.getProperty("maxTotalFloatingIps")));
        limit.setMaxTotalInstances(Integer.parseInt(props.getProperty("maxTotalInstances")));
        limit.setMaxTotalRAMSize(Integer.parseInt(props.getProperty("maxTotalRAMSize")));
        limit.setMaxGigabytes(Integer.parseInt(props.getProperty("maxGigabytes")));
        limit.setMaxSnapShots(Integer.parseInt(props.getProperty("maxSnapShots")));
        limit.setMaxVolumes(Integer.parseInt(props.getProperty("maxVolumes")));
        limit.setMaxTotalKeypairs(Integer.parseInt(props.getProperty("maxTotalKeypairs")));
        limit.setMaxSecurityGroupRules(Integer.parseInt(props.getProperty("maxSecurityGroupRules")));
        limit.setMaxSecurityGroups(Integer.parseInt(props.getProperty("maxSecurityGroups")));
        limit.setMaxSubnet(Integer.parseInt(props.getProperty("maxSubnet")));
        limit.setMaxRouter(Integer.parseInt(props.getProperty("maxRouter")));
        limit.setMaxPort(Integer.parseInt(props.getProperty("maxPort")));
        limit.setMaxNetwork(Integer.parseInt(props.getProperty("maxNetwork")));
 
        return limit;
	}
	/**
	 * 设置默认值
	 * @param limit 
	 * @throws IOException
	 */
	public static  void SetLimits(QuotaEntity limit) throws IOException
	{
		
		Properties props = new Properties();
		OutputStream out = new FileOutputStream(QuotaOs.class.getResource("/config/Limits.properties").getFile()); 
        
		props.setProperty("maxImageMeta", String.valueOf(limit.getMaxImageMeta()));
		props.setProperty("maxPersonality", String.valueOf(limit.getMaxPersonality()));
		props.setProperty("maxPersonalitySize",String.valueOf(limit.getMaxPersonalitySize()) );
		props.setProperty("maxServerGroupMembers",String.valueOf(limit.getMaxServerGroupMembers()) );
		props.setProperty("maxServerGroups", String.valueOf(limit.getMaxServerGroups()));
		props.setProperty("maxServerMeta", String.valueOf(limit.getMaxServerMeta()));
		props.setProperty("maxTotalCores", String.valueOf(limit.getMaxTotalCores()));
		props.setProperty("maxTotalKeypairs", String.valueOf(limit.getMaxTotalKeypairs()));
		props.setProperty("maxTotalInstances", String.valueOf(limit.getMaxTotalInstances()));
		props.setProperty("maxTotalRAMSize", String.valueOf(limit.getMaxTotalRAMSize()));
		
		props.setProperty("maxGigabytes",String.valueOf(limit.getMaxGigabytes()) );
		props.setProperty("maxSnapShots", String.valueOf(limit.getMaxSnapShots()));
		props.setProperty("maxVolumes", String.valueOf(limit.getMaxVolumes()));
		
		props.setProperty("maxTotalFloatingIps", String.valueOf(limit.getMaxTotalFloatingIps()));
		props.setProperty("maxSecurityGroupRules",String.valueOf(limit.getMaxSecurityGroupRules()) );
		props.setProperty("maxSecurityGroups", String.valueOf(limit.getMaxSecurityGroups()));
		props.setProperty("maxSubnet", String.valueOf(limit.getMaxSubnet()));
		props.setProperty("maxRouter", String.valueOf(limit.getMaxRouter()));
		props.setProperty("maxPort",String.valueOf(limit.getMaxPort()) );
		props.setProperty("maxNetwork", String.valueOf(limit.getMaxNetwork()));
		
        props.store(out,"update");
        out.close();
	}
}
