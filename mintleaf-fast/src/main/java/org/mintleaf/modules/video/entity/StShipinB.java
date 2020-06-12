package org.mintleaf.modules.video.entity;
import org.beetl.sql.core.annotatoin.Table;

import java.math.*;

/* 
* 
* gen by beetlsql 2018-07-07
*/
@Table(name="HWSWJ.ST_SHIPIN_B")
public class StShipinB  {
	
	private String id ;
	private BigDecimal e ;
	private BigDecimal n ;
	private String name ;
	private String newvideoId ;
	private String pass ;
	private String show ;
	private Long slevel ;
	private String sszz ;
	private String swz ;
	private String swzId ;
	private String turn ;
	private String videoId ;
	
	public StShipinB() {
	}
	
	public String getId(){
		return  id;
	}
	public void setId(String id ){
		this.id = id;
	}
	
	public BigDecimal getE(){
		return  e;
	}
	public void setE(BigDecimal e ){
		this.e = e;
	}
	
	public BigDecimal getN(){
		return  n;
	}
	public void setN(BigDecimal n ){
		this.n = n;
	}
	
	public String getName(){
		return  name;
	}
	public void setName(String name ){
		this.name = name;
	}
	
	public String getNewvideoId(){
		return  newvideoId;
	}
	public void setNewvideoId(String newvideoId ){
		this.newvideoId = newvideoId;
	}
	
	public String getPass(){
		return  pass;
	}
	public void setPass(String pass ){
		this.pass = pass;
	}
	
	public String getShow(){
		return  show;
	}
	public void setShow(String show ){
		this.show = show;
	}
	
	public Long getSlevel(){
		return  slevel;
	}
	public void setSlevel(Long slevel ){
		this.slevel = slevel;
	}
	
	public String getSszz(){
		return  sszz;
	}
	public void setSszz(String sszz ){
		this.sszz = sszz;
	}
	
	public String getSwz(){
		return  swz;
	}
	public void setSwz(String swz ){
		this.swz = swz;
	}
	
	public String getSwzId(){
		return  swzId;
	}
	public void setSwzId(String swzId ){
		this.swzId = swzId;
	}
	
	public String getTurn(){
		return  turn;
	}
	public void setTurn(String turn ){
		this.turn = turn;
	}
	
	public String getVideoId(){
		return  videoId;
	}
	public void setVideoId(String videoId ){
		this.videoId = videoId;
	}
	

}
