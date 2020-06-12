package net.ahwater.ahwaterCloud.entity.identityV3;

import java.util.List;

public class DefaultAndTenantList {
	String defaultName;
	String defaultId;
	List<TenantNameId> tenantNames;
	public String getDefaultName() {
		return defaultName;
	}
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}
	public String getDefaultId() {
		return defaultId;
	}
	public void setDefaultId(String defaultId) {
		this.defaultId = defaultId;
	}
	public List<TenantNameId> getTenantNames() {
		return tenantNames;
	}
	public void setTenantNames(List<TenantNameId> tenantNames) {
		this.tenantNames = tenantNames;
	}
	
}
