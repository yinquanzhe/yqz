package com.dvs.activemq;

import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;


/**
 * 订单JMS消息发送类.
 *
 * @author suwei
 */
public class MessageProducer {
	private Log log = LogFactory.getLog(getClass());
	private JmsTemplate template;

	private Queue destination;

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public void setDestination(Queue destination) {
		this.destination = destination;
	}

	public void send(Message data) {
		log.debug("=======================================");
		log.debug("do send ......");
		long l1=System.currentTimeMillis();
		
		template.convertAndSend(this.destination, data);
		
		log.debug("send time:"+(System.currentTimeMillis()-l1)/1000+"s");
		log.debug("=======================================");
	}

}
