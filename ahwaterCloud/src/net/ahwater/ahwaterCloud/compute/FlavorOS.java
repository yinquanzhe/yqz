package net.ahwater.ahwaterCloud.compute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.FlavorAccess;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.openstack.compute.domain.NovaFlavor;

import net.ahwater.ahwaterCloud.compute.entity.VMManager.TenantListEle;

/**
 * 云资源模板类
 * @author gwh
 *
 */
public class FlavorOS {
	
	private Flavor flavor;
	
	public FlavorOS(){}
	
	public FlavorOS(Flavor fr){
		this.flavor=fr;
	}
	
/**
 * 创建云资源模板
 * @param os
 * @param flavorName
 * @param ram
 * @param vcpus
 * @param disk
 * @param ephemeral
 * @param swap
 * @param isPublic
 * @return
 */
	public String createFlavor(OSClientV2 os,String flavorName,int ram,int vcpus,int disk,int ephemeral,int swap,boolean isPublic){
		String excepStr=null;
		try{
			flavor=os.compute().flavors()
					.create(flavorName, ram, vcpus, disk, ephemeral, swap, 1, isPublic);
		}catch(Exception e){
			excepStr=e.getMessage();
		}
		
		return excepStr;
	}
	
	/**
	 * 增加租户
	 * @param os
	 * @param tenantId
	 */
	public void addTenantAccess(OSClientV2 os,String tenantId){
		 os.compute().flavors().addTenantAccess(flavor.getId(), tenantId);
	}
	
	/**
	 * 减少租户
	 * @param os
	 * @param tenantId
	 */
	public void removeTenantAccess(OSClientV2 os,String tenantId){
		os.compute().flavors().removeTenantAccess(flavor.getId(), tenantId);
	}
	
	/**
	 * 列出类型所属的租户
	 * @param os
	 * @return
	 */
	public List<String> listTenantAccess(OSClientV2 os){
		List<? extends FlavorAccess> lfa=os.compute().flavors().listFlavorAccess(flavor.getId());
		List<String> tenantIds=new ArrayList<>();
		for(FlavorAccess fa:lfa){
			tenantIds.add(fa.getTenantId());
		}
		return tenantIds;
	}
	
	public List<TenantListEle> haveTenantList(OSClientV2 os){
		List<? extends FlavorAccess> lfa=os.compute().flavors().listFlavorAccess(flavor.getId());
		List<TenantListEle> ltle=new ArrayList<>();
		for(FlavorAccess fa:lfa){
			TenantListEle t=new TenantListEle();
			t.setTenantId(fa.getTenantId());
			Tenant tenant=os.identity().tenants().get(fa.getTenantId());
			t.setTennatName(tenant.getName());
			
			ltle.add(t);
		}
		return ltle;
	}
	
	/**
	 * Deleting a Flavor
	 * @param os
	 * @return
	 */
	public ActionResponse deleteFlavor(OSClientV2 os){
		return os.compute().flavors().delete(flavor.getId());
	}
	
	/**
	 * list All public Flavors
	 * @param os
	 * @return
	 */
	public static List<? extends Flavor> listAllFlavors(OSClientV2 os){
		return os.compute().flavors().list();
	}
	
	/**
	 * get a Flavor by ID
	 * @param os
	 * @param flavorId
	 * @return
	 */
	public static Flavor getFlavorById(OSClientV2 os,String flavorId){
		return os.compute().flavors().get(flavorId);
	}
	
	/**
	 * 列出公共和私有的所有flavor
	 * @param os
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<? extends Flavor> listPublicAndNonpublicFlavors(OSClientV2 os){
		Map<String, String> filteringParams=new HashMap<>();
		filteringParams.put("is_public", "1");
		filteringParams.put("sort_key", "vcpus");
		List<NovaFlavor> publicFlavors=(List<NovaFlavor>) os.compute().flavors().list(filteringParams);
		Map<String, String> filteringParams2=new HashMap<>();
		filteringParams2.put("is_public", "0");
		filteringParams2.put("sort_key", "vcpus");
		List<NovaFlavor> nonpublicFlavors=(List<NovaFlavor>) os.compute().flavors().list(filteringParams2);
		nonpublicFlavors.addAll(publicFlavors);
		return nonpublicFlavors;
	}
	
	/**
	 * List Extra Specs For A Flavor
	 * @param os
	 * @return
	 */
	public Map<String,String> listExtraSpecsForAFlavor(OSClientV2 os){
		return os.compute().flavors().listExtraSpecs(flavor.getId());
	}
	
	/**
	 * 列出系统所有租户
	 * @param os
	 * @return
	 */
	public static List<TenantListEle> listAllTenant(OSClientV2 os){
		List<?extends Tenant> lts=os.identity().tenants().list();
		List<TenantListEle> ltle=new ArrayList<>();
		for(Tenant t:lts){
			TenantListEle te=new TenantListEle();
			te.setTenantId(t.getId());
			te.setTennatName(t.getName());
			ltle.add(te);
		}
		return ltle;
	}
}
