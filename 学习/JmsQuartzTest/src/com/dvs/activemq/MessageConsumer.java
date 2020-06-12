package com.dvs.activemq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageConsumer {
	private Log log = LogFactory.getLog(getClass());

	public void addResource(Message data) {
		log.info("=======================================");
		log.info("receiveing message ...");
		log.info(data.toString());
		log.info("here to invoke our business method...");
		log.info("=======================================");
	}
}
