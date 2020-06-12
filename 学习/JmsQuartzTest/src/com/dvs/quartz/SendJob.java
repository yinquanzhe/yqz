package com.dvs.quartz;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.dvs.activemq.Message;
import com.dvs.activemq.MessageProducer;

public class SendJob extends QuartzJobBean {
    
	private MessageProducer messageProducer;
	
	public SendJob(){
		
	}
	
	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	public void setMessageProducer(
			MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
	
		Message msg = new Message();
		msg.setId(1);
		msg.setName("SendJob");
		msg.setDate(new Date());		
		messageProducer.send(msg);		
	}
}

