package net.ahwater.tender.db;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.ahwater.tender.db.entity.BeanRole;
import net.ahwater.tender.db.entity.BeanUserWebsite;
import net.ahwater.tender.db.mapper.RoleMapper;
import net.ahwater.tender.db.mapper.UserMapper;
import net.ahwater.tender.db.mapper.UserWebsiteMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TenderDbApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserWebsiteMapper userWebsiteMapper;

	@Test
	public void contextLoads() {
//        System.out.println(userMapper.selectById(1));
//        BeanUser u = new BeanUser();
//        u.setRoleId(1);
//        u.setUnitId(1);
//        u.setUsername("你好");
//        System.out.println(userMapper.insert(u));
//        System.out.println(u);
//        System.out.println(userMapper.selectById(1));
        BeanRole role = roleMapper.selectOne(new QueryWrapper<BeanRole>().apply("id=(select role_id from t_user where id={0})", 6));
        System.out.println(role);
    }

	@Test
    public void test0() throws Exception {
//        ObjectMapper om = new ObjectMapper();
//        BeanTime bean = new BeanTime().setId(1).setHour(12345678901234567L);
//        System.out.println(om.writeValueAsString(bean));
    }

    @Test
    public void test1() {
//        System.err.println(keywordDao.selectTwoMonthByUserId(1));
//        BeanItem bean = new BeanItem();
//        bean.setModuleId(1);
//        bean.setUrl("111");
//        bean.setTitle("123");
//        System.out.println(itemMapper.save(bean));
//        System.out.println(bean);
//        System.out.println(userMapper.findAllByPushTimeNot(-1));
//        System.out.println(roleDao.selectByUserId(1));

        List<Integer> list = new ArrayList<>();
        list.add(1);
//        List<BeanKeyword> keywords = keywordDao.selectList(new EntityWrapper<BeanKeyword>().in("user_id", list));
//        System.out.println(keywords.size());
        BeanUserWebsite bean = new BeanUserWebsite().setUserId(7).setWebsiteId(6);
        System.out.println(userWebsiteMapper.insert(bean));
    }

    @Test
    public void test3() {
//        new OracleEntityCreator.Builder(dataSource)              // 初始化, 设置数据源
//                .classPath("C:\\Users\\Reeye\\IdeaProjects\\tender-integration\\tender-db\\src\\main\\java\\net\\ahwater\\tender\\db\\bean")                // 设置文件输出基础目录(默认项目根目录)
//                .packageName("net.ahwater.tender.db.entity")        // 设置package(默认"entity")
//                .intros(Arrays.asList("行政区划", "抓取出错日志", "抓取日志", "抓取的单条信息", "模块配置", "额外参数", "推送日志", "抓取时间", "网站"))  // 实体类简单说明
//                .classNames(Arrays.asList("BeanDivision", "BeanGrabError", "BeanGrabLog", "BeanItem", "BeanModule", "BeanParam", "BeanPushLog", "BeanTime", "BeanWebsite")) // 设置对应类名
//                .tables(Arrays.asList("t_division", "t_grab_error_log", "t_grab_log", "t_item", "t_module", "t_param", "t_push_log", "t_time", "t_website")) // 设置对应表名
//                .noArgsConstructor(false)                        // 设置是否生成无参构造函数(默认true)
//                .allArgsConstructor(false)                       // 设置是否生成包含所有参数的构造函数(默认true)
//                .getter(false)                                    // 设置是否生成getter(默认true)
//                .setter(false)                                    // 设置是否生成setter(默认true)
//                .toString(false)                                 // 设置是否生成toString(默认true)
//                .build()                                         // 初始化creator
//                .create();                                       // 执行创建
    }

    public static void main(String[] args) {

    }

}
