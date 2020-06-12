package net.ahwater.tender.web;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.BeanItem;
import net.ahwater.tender.db.entity.BeanModule;
import net.ahwater.tender.db.entity.BeanUnit;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.db.mapper.ModuleMapper;
import net.ahwater.tender.db.mapper.UnitMapper;
import net.ahwater.tender.db.mapper.UserMapper;
import net.ahwater.tender.web.util.CacheUtil;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
public class TenderWebApplicationTests {

    @Autowired
    private JmsMessagingTemplate template;

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheUtil util;

    @Autowired
    private UnitMapper unitMapper;

    @Test
    public void contextLoads() {
        BeanModule module = moduleMapper.selectById(434);
        System.out.println(template.convertSendAndReceive(Constants.TEST_QUEUE, module, List.class));
    }

    public static void main(String[] args) {
        int[] arr = new int[]{2, 1, 5, 4, 6, 3};
        System.out.println(Arrays.toString(sort(arr)));
    }

    private static int[] sort(int[] arr) {
        boolean flag = false;
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (i < arr.length - 1) {
                if (arr[i] % 2 == arr[i + 1] % 2) {
                    if (arr[i] > arr[i + 1]) {
                        flag = true;
                        index = i;
                        break;
                    }
                } else {
                    if (arr[i] % 2 == 0) {
                        flag = true;
                        index = i;
                        break;
                    }
                }
            }
        }
        if (flag) {
            int temp = arr[index];
            arr[index] = arr[index + 1];
            arr[index + 1] = temp;
            return sort(arr);
        } else {
            return arr;
        }
    }

    @Test
    public void test0() throws Exception {
        int[] arr = new int[]{0x31, 0x32, 0x33, 0x0D, 0x0A, 0xC4, 0xE3, 0xC4E3};
        System.out.println((char) arr[0]);
        System.out.println((char) arr[1]);
        System.out.println((char) arr[2]);
        System.out.println(arr[0]);
        System.out.println(arr[1]);
        System.out.println(arr[2]);
        System.out.println(arr[5]);
        System.out.println(arr[6]);
        System.out.println(arr[7]);
        System.out.println((int) '你');
        System.out.println(Arrays.toString("你好123".getBytes("utf-8")));
        System.out.println(Arrays.toString("你好123".getBytes("GBK")));
    }

    @Test
    public void test1() {
        BeanItem item = new BeanItem().setId(1).setPubTime(new Date()).setTitle("测试一个合肥").setUrl("http://baid.com");
        template.convertAndSend(new ActiveMQTopic(Constants.ITEM_TOPIC), item);
//        try {
//            Thread.sleep(1000L * 5);
//            template.convertAndSend(new ActiveMQTopic(Constants.GRAB_DONE), "1");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("Done Done!");
    }

    @Test
    public void test2() throws Exception {
//        ValueOperations<String, String> operations = redisTemplate.opsForValue();
//        System.err.println(redisTemplate.getExpire("a"));
//        operations.set("a", "1");
//        System.err.println(redisTemplate.getExpire("a"));
//        String value = util.get("admin", new CacheUtil.A<String>() {
//            @Override
//            public String get() {
//                return userMapper.selectByUserName("admin").toString();
//            }
//        });
//        System.out.println(value);
//        BeanUser user = util.get("admin", new CacheUtil.A<BeanUser>() {
//            @Override
//            public BeanUser get() {
//                return userMapper.selectByUserName("admin");
//            }
//        });
        BeanUser user = util.getIfExistElseSet("admin", () -> userMapper.selectByUserName("admin"));
        System.out.println(user);
        Object obj = redisTemplate.opsForValue().get("admin");
        System.out.println(obj.getClass());
        System.out.println("=================");
        BeanUnit unit = new BeanUnit().setId(8).setName("测试2").setDivisionStr(null);
        System.out.println(unitMapper.updateById(unit));
        int res = unitMapper.update(unit, new UpdateWrapper<BeanUnit>().set("division_str", null).eq("id", 8));
        System.err.println(res);
    }

}
