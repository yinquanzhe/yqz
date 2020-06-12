package net.ahwater.tender.wx;

import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.BeanItem;
import net.ahwater.tender.db.entity.BeanUserWebsite;
import net.ahwater.tender.db.mapper.UserWebsiteMapper;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TenderPushApplicationTests {

	@Autowired
	private JmsMessagingTemplate msgTemplate;

	@Autowired
	private UserWebsiteMapper userWebsiteMapper;

	@Test
	public void contextLoads() {
		BeanItem item = new BeanItem();
		item.setId(1);
		item.setUrl("uuuurrrrl");
		item.setTitle("title11111");
		item.setContent("cotnent111");
		item.setPubTime(new Date());
		item.setGrabTime(new Date());
		item.setModuleId(1);

		msgTemplate.convertAndSend("tender.items.queue", item);
	}

	@Test
    public void test0() {
        BeanUserWebsite bean = new BeanUserWebsite().setUserId(7).setWebsiteId(6);
        List<BeanUserWebsite> list = new ArrayList<>();
        list.add(bean);
        System.out.println(userWebsiteMapper.insertAll(list));
    }

}
