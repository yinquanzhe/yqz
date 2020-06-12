package net.ahwater.ahwaterCloud.tenant;
/**
 * 用于显示租户列表中租户的属性
 * @author zdf
 *
 */
public class TenantElement {
	private String tenantName;
	private String tenantId;
	private String tenantDescription;
	private boolean tenantActive;
	
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getTenantDescription() {
		return tenantDescription;
	}
	public void setTenantDescription(String tenantDescription) {
		this.tenantDescription = tenantDescription;
	}
	
	public boolean isTenantActive() {
		return tenantActive;
	}
	public void setTenantActive(boolean tenantActive) {
		this.tenantActive = tenantActive;
	}
}
