package net.ahwater.ahwaterCloud.entity.compute;

import java.util.Date;


public class KeyPairDetailInfo {
	String keypairName;
	String id;
	String fingerPrint;
	String timeCreated;
	String userID;
	String publicKey;
	
	
	public String getKeypairName() {
		return keypairName;
	}
	public void setKeypairName(String keypairName) {
		this.keypairName = keypairName;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFingerPrint() {
		return fingerPrint;
	}
	public void setFingerPrint(String fingerPrint) {
		this.fingerPrint = fingerPrint;
	}
	public String getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(String timeCreated) {
		this.timeCreated = timeCreated;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
}
