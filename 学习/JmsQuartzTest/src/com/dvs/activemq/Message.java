package com.dvs.activemq;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import java.util.Date;
public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8259746411506015007L;

	long id;

	String name;

	Date date;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
