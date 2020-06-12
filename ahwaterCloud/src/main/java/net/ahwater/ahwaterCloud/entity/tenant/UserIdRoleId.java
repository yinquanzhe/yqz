package net.ahwater.ahwaterCloud.entity.tenant;

import java.util.List;
/**
 * 用户Id和对应的角色的Id
 * @author zdf
 *
 */

public class UserIdRoleId {
	private String userId;
	private List<String> roleIds;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<String> getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}
}
