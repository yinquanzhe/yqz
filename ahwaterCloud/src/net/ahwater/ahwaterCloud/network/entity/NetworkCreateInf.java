package net.ahwater.ahwaterCloud.network.entity;
/**
 * 用于显示网络的名字，是否共享， 管理员状态
 * @author zdf
 *
 */
public class NetworkCreateInf {
	private String networkName;
	private boolean networkIsShared;
	private boolean networkIsAdminStateUp;
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public boolean isNetworkIsShared() {
		return networkIsShared;
	}
	public void setNetworkIsShared(boolean networkIsShared) {
		this.networkIsShared = networkIsShared;
	}
	public boolean isNetworkIsAdminStateUp() {
		return networkIsAdminStateUp;
	}
	public void setNetworkIsAdminStateUp(boolean networkIsAdminStateUp) {
		this.networkIsAdminStateUp = networkIsAdminStateUp;
	}
}
