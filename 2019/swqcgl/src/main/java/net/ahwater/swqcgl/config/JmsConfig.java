package net.ahwater.swqcgl.config;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * Created by yqz on 2019/1/17
 */
@Configuration
public class JmsConfig {
    @Bean
    public ConnectionFactory connectionFactory(){
        return new ActiveMQConnectionFactory();
    }
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory){
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        return jmsTemplate;
    }
}