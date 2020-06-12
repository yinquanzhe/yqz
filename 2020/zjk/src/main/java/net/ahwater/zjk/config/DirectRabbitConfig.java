package net.ahwater.zjk.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig{

    public final static String QUEUE_NAME = "TestDirectQueue";
    public final static String EXCHANGE_NAME = "TestDirectExchange";
    public final static String ROUTING_KEY = "TestDirectRouting";

    @Bean
    public Queue testDirectQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange testDirectExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindingDirect(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

}