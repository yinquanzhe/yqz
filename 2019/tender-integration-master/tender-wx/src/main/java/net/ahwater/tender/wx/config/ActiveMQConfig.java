package net.ahwater.tender.wx.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

/**
 * Created by Reeye on 2018/5/1 17:04
 * Nothing is true but improving yourself.
 */
@Configuration
public class ActiveMQConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        //是否在每次尝试重新发送失败后,增长这个等待时间
        redeliveryPolicy.setUseExponentialBackOff(true);
        //重发次数,默认为6次   这里设置为10次
        redeliveryPolicy.setMaximumRedeliveries(10);
        //重发时间间隔,默认为1秒
        redeliveryPolicy.setInitialRedeliveryDelay(1);
        //第一次失败后重新发送之前等待500毫秒,第二次失败再等待500 * 2毫秒,这里的2就是value
        redeliveryPolicy.setBackOffMultiplier(2);
        //是否避免消息碰撞
        redeliveryPolicy.setUseCollisionAvoidance(false);
        //设置重发最大拖延时间-1 表示没有拖延只有UseExponentialBackOff(true)为true时生效
        redeliveryPolicy.setMaximumRedeliveryDelay(-1);
        return redeliveryPolicy;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, RedeliveryPolicy redeliveryPolicy) {
        if (connectionFactory instanceof ActiveMQConnectionFactory) {
            ActiveMQConnectionFactory activeMQConnectionFactory = (ActiveMQConnectionFactory) connectionFactory;
            activeMQConnectionFactory.setCloseTimeout(1000);
            activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        }
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setSessionCacheSize(100);
        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
        return new JmsTemplate(cachingConnectionFactory);
    }

    @Bean("jmsListenerContainerFactory4Queue")
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory4Queue(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(false);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean("jmsListenerContainerFactory4ItemTopic")
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory4ItemTopic(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        factory.setClientId(appName + "_item");
        factory.setSubscriptionDurable(true);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean("jmsListenerContainerFactory4GrabTopic")
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory4GrabTopic(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        factory.setClientId(appName + "_grab");
        factory.setSubscriptionDurable(false);
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        return factory;
    }

}
