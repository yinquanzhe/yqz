package net.ahwater.ahwaterCloud.entity.network;

public class HostRouteInfo {
	String destination;
	String nextHop;
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getNextHop() {
		return nextHop;
	}
	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}
}
