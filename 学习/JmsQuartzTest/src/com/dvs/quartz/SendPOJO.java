package com.dvs.quartz;

import java.util.Date;

import com.dvs.activemq.MessageProducer;
import com.dvs.activemq.Message;

public class SendPOJO {
	private MessageProducer messageProducer;

	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	public void setMessageProducer(
			MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}
	
	public void execute() {
		Message msg = new Message();
		msg.setId(2);
		msg.setName("SendPOJO");
		msg.setDate(new Date());		
		messageProducer.send(msg);	
	}	
}
