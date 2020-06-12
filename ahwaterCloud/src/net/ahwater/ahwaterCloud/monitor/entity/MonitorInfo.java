package net.ahwater.ahwaterCloud.monitor.entity;

public class MonitorInfo {
	String uuid;
	String cloud_hostname;
	
	String create_at;
	
	double vcpu_usage_rate;
	
	long vmem_rss_size;
	
	long vdisk_capacity_size;
	long vdisk_allocation_size;
	
	long vdisk_read_bytes;
	long vdisk_write_bytes;
	
	long vnet_flow_rx_bytes;
	long vnet_flow_tx_bytes;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getCloud_hostname() {
		return cloud_hostname;
	}
	public void setCloud_hostname(String cloud_hostname) {
		this.cloud_hostname = cloud_hostname;
	}
	
	
	public String getCreate_at() {
		return create_at;
	}
	public void setCreate_at(String create_at) {
		this.create_at = create_at;
	}
	public double getVcpu_usage_rate() {
		return vcpu_usage_rate;
	}
	public void setVcpu_usage_rate(double vcpu_usage_rate) {
		this.vcpu_usage_rate = vcpu_usage_rate;
	}
	public long getVmem_rss_size() {
		return vmem_rss_size;
	}
	public void setVmem_rss_size(long vmem_rss_size) {
		this.vmem_rss_size = vmem_rss_size;
	}
	public long getVdisk_capacity_size() {
		return vdisk_capacity_size;
	}
	public void setVdisk_capacity_size(long vdisk_capacity_size) {
		this.vdisk_capacity_size = vdisk_capacity_size;
	}
	public long getVdisk_allocation_size() {
		return vdisk_allocation_size;
	}
	public void setVdisk_allocation_size(long vdisk_allocation_size) {
		this.vdisk_allocation_size = vdisk_allocation_size;
	}
	public long getVdisk_read_bytes() {
		return vdisk_read_bytes;
	}
	public void setVdisk_read_bytes(long vdisk_read_bytes) {
		this.vdisk_read_bytes = vdisk_read_bytes;
	}
	public long getVdisk_write_bytes() {
		return vdisk_write_bytes;
	}
	public void setVdisk_write_bytes(long vdisk_write_bytes) {
		this.vdisk_write_bytes = vdisk_write_bytes;
	}
	public long getVnet_flow_rx_bytes() {
		return vnet_flow_rx_bytes;
	}
	public void setVnet_flow_rx_bytes(long vnet_flow_rx_bytes) {
		this.vnet_flow_rx_bytes = vnet_flow_rx_bytes;
	}
	public long getVnet_flow_tx_bytes() {
		return vnet_flow_tx_bytes;
	}
	public void setVnet_flow_tx_bytes(long vnet_flow_tx_bytes) {
		this.vnet_flow_tx_bytes = vnet_flow_tx_bytes;
	}
}
