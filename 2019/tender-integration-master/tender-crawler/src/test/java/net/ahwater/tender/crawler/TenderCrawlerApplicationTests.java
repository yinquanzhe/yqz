package net.ahwater.tender.crawler;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.BeanGrabLog;
import net.ahwater.tender.db.entity.BeanItem;
import net.ahwater.tender.db.mapper.GrabLogMapper;
import net.ahwater.tender.db.mapper.ItemMapper;
import net.ahwater.tender.db.mapper.UserMapper;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TenderCrawlerApplicationTests {

//    @Autowired
//    private Crawler crawler;

    @Autowired
    private UserMapper userMapper;

    @Value("${lucene.index.path}")
    private String luceneIndexPath;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private GrabLogMapper grabLogMapper;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private LuceneTemplate lucene;

    @Test
    public void contextLoads() throws Exception {
//        crawler.run();
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);    //关闭css
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
//        webClient.waitForBackgroundJavaScript(1000L * 3);

        WebRequest request = new WebRequest(new URL("http://www.iwencai.com/yike/detail/auid/3d1b67f25888ee61"),
                HttpMethod.valueOf("GET"));
        HtmlPage page = webClient.getPage(request);
        System.out.println(page.asXml());
    }

    @Test
    public void test0() {
//        System.out.println(userMapper.selectList(null));
        BeanItem item = itemMapper.selectById(16);
        jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(Constants.ITEM_TOPIC), item);
        System.out.println("Done!");
        try {
            Thread.sleep(1000L * 5);
            jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(Constants.GRAB_DONE), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done Done!");
    }

    public static void main(String[] args) throws Exception {
//        int count = 0;
//        int max = 100;
//        for (int i = 1; i <= max; i++) {
//            for (int j = i; j <= max; j++) {
//                double c = Math.sqrt(i * i + j * j);
//                if ((c + "").matches("^(100|\\d{1,2})\\.0$")) {
//                    System.out.printf("%d-%d-%.0f\n", i, j, c);
//                    count++;
//                }
//                for (int k = 1; k <= max; k++) {
//                    if (i * i + j * j == k * k) {
//                        System.out.printf("%d-%d-%d\n", i, j, k);
//                        count++;
//                    }
//                }
//            }
//        }
//        System.err.println(count);
//        String s1 = "abc";
//        String s2 = new String("abc");
//        System.out.println(s1.hashCode());
//        System.out.println(s2.hashCode());
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E").format(new Date()));
    }

//    @Autowired
//    private UserMappersitory userMappersitory;
//
//    @Test
//    public void test0() {
//        User u = new User();
//        u.setAge(11);
//        u.setName("韩飞非");
//        u.setSex(true);
//        User user = userMappersitory.insert(u);
//        System.out.println("新增: " + user);
//        List<User> users = userMappersitory.findAll();
//        System.out.println("查找所有: " + users);
//
////        userMappersitory.deleteById(2L);
////        System.err.println("删除成功！");
//    }


//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    public void test1() {
//        User u = new User();
//        u.setId(3L);
//        u.setName("Tom");
//        userRepository.save(u);
//        userRepository.findAll();
//        userRepository.deleteById(3L);
//    }

    @Test
    public void test1() {
//        System.out.println(itemMapper.selectList(new QueryWrapper<BeanItem>().orderByDesc("id").last("limit " + 2)));
        BeanGrabLog grabLog = new BeanGrabLog().setId(1).setStartTime(new Date()).setModuleCount(2).setEndTime(new Date()).setItemCount(20);
        int res = grabLogMapper.update(grabLog, new UpdateWrapper<BeanGrabLog>()
                .lambda()
                .set(BeanGrabLog::getEndTime, grabLog.getEndTime())
                .set(BeanGrabLog::getItemCount, grabLog.getItemCount())
                .eq(BeanGrabLog::getId, grabLog.getId()));
        System.out.println(res);
    }

    @Test
    public void testLucene() throws Exception {
//        List<BeanItem> list = itemMapper.selectList(null);
//        List<BeanItem> list = new ArrayList<>();
//        list.add(new BeanItem().setId(1).setTitle("测试一下").setPubTime(new Date()));
//        lucene.generateIndex(list);

        Directory dir = FSDirectory.open(Paths.get(luceneIndexPath));
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(); //中文分词器
//        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//        IndexWriter writer = new IndexWriter(dir, iwc);
//
//
//        Document doc2 = new Document();
//        doc2.add(new StoredField("id",2));
//        doc2.add(new TextField("title", "测试一下2", Field.Store.YES));
//        doc2.add(new SortedNumericDocValuesField("pub_time", 200L));
//        writer.addDocument(doc2);
//
//        Document doc = new Document();
//        doc.add(new StoredField("id",1));
//        doc.add(new TextField("title", "测试一下", Field.Store.YES));
//        doc.add(new SortedNumericDocValuesField("pub_time", 100L));
//        writer.addDocument(doc);
//
//        writer.close();

        String q = "测试";
        IndexReader reader = DirectoryReader.open(dir);
        final IndexSearcher is = new IndexSearcher(reader);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title"}, analyzer);
        Query query = parser.parse(q);

        Sort sort = new Sort(new SortField("title", SortField.Type.SCORE),
                new SortedNumericSortField("pub_time", SortField.Type.LONG, true));

        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, Integer.MAX_VALUE, sort, true, false);
        long end = System.currentTimeMillis();
        log.debug("匹配[" + q + "]总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            System.out.println(hits.scoreDocs[i]);
            Document document = is.doc(hits.scoreDocs[i].doc);
            System.err.println(document.get("title"));
        }
        reader.close();
    }

}
