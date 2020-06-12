package net.ahwater.tender.crawler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.*;
import net.ahwater.tender.db.mapper.*;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Reeye on 2018/5/25 11:25
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
@Slf4j
public class Crawler {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private WebsiteMapper websiteMapper;

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private ParamMapper paramMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private GrabErrorMapper grabErrorMapper;

    @Autowired
    private GrabLogMapper grabLogMapper;

    @Autowired
    private ModuleGrabLogMapper moduleGrabLogMapper;

    @Autowired
    private LuceneTemplate luceneTemplate;

    public void run() {
        try {
            BeanGrabLog grabLog = new BeanGrabLog();
            List<BeanWebsite> websites = websiteMapper.selectList(new QueryWrapper<BeanWebsite>().lambda().eq(BeanWebsite::getStatus, 1));
            List<BeanModule> modules = moduleMapper.selectList(null)
                    .stream()
                    .filter(e -> websites.stream().anyMatch(w -> e.getWebsiteId().equals(w.getId())))   // 过滤掉网站status不为1的
                    .collect(Collectors.toList());
            grabLog.setStartTime(new Date()).setWebsiteCount(websites.size()).setModuleCount(modules.size());
            if (grabLogMapper.insert(grabLog) == 1) {
                log.info("新增抓取日志成功: ID{}", grabLog.getId());
                long startCount = itemMapper.selectCount(null);
                log.debug("成功获取模块数量{}, item数量{}", modules.size(), startCount);

                if (modules.size() > 0) {
                    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
                    final CountDownLatch latch = new CountDownLatch(modules.size());
                    for (BeanModule module : modules) {
                        fixedThreadPool.execute(new MyTask(module, latch, grabLog.getId()));
                    }
                    latch.await();
                    long count = itemMapper.selectCount(null);
                    int newCount = (int) (count - startCount);
                    log.debug("All Done!!! item数量:{}, 新增数量:{}", count, newCount);
                    grabLog.setEndTime(new Date()).setItemCount(newCount);
                    if (newCount >= 0) {
                        push(newCount);
                        List<BeanItem> items = itemMapper.selectList(new QueryWrapper<BeanItem>().orderByDesc("id").last("limit " + newCount));
                        luceneTemplate.generateIndex(items);
                    }
                }
                log.info("更新抓取日志: ID:{} 结果:{}", grabLog.getId(), grabLogMapper.updateById(grabLog));
            } else {
                log.error("新增抓取日志失败: {}", grabLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Nullable
    public List<BeanItem> runModuleTest(BeanModule module) {
        try {
            BeanWebsite website = websiteMapper.selectById(module.getWebsiteId());
            List<BeanParam> params = paramMapper.selectList(new QueryWrapper<BeanParam>().lambda().eq(BeanParam::getModuleId, module.getId()));
            return Executors.newCachedThreadPool().submit(new ListCallback(module, website, params, grabErrorMapper, true)).get();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 推送消息(立即推送)
     *
     * @param count 新抓取的条数
     */
    private void push(int count) {
        if (count <= 0) {
            return;
        }
        Map<String, Object> headers = new HashMap<>();
        headers.put(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000L * 30);
        jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(Constants.GRAB_DONE), count + "", headers);
    }

    class MyTask implements Runnable {

        BeanModule module;
        CountDownLatch latch;
        int grabLogId;

        public MyTask(BeanModule module, CountDownLatch latch, int grabLogId) {
            this.module = module;
            this.latch = latch;
            this.grabLogId = grabLogId;
        }

        @Override
        public void run() {
            try {
                BeanWebsite temp = new BeanWebsite();
                temp.setId(module.getWebsiteId());
                BeanWebsite website = websiteMapper.selectById(module.getWebsiteId());
                if (website != null) {
                    BeanParam temp1 = new BeanParam();
                    temp1.setModuleId(module.getId());
                    List<BeanParam> params = paramMapper.selectList(new QueryWrapper<BeanParam>().lambda().eq(BeanParam::getModuleId, module.getId()));
                    BeanModuleGrabLog moduleGrabLog = new BeanModuleGrabLog().setGrabLogId(grabLogId).setModuleId(module.getId()).setStartTime(new Date());
                    List<BeanItem> list = Executors.newCachedThreadPool().submit(new ListCallback(module, website, params, grabErrorMapper, false)).get();
                    log.debug("[" + website.getName() + "] [" + module.getName() + "] 成功抓取" + (module.getMaxPage() - module.getPageStart()) + "页列表, 共" + list.size() + "条");
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTitle() == null || list.get(i).getPubTime() == null) {
                            list.set(i, Executors.newCachedThreadPool().submit(new ItemCallback(module, list.get(i))).get());
                        }
                    }
                    int count = 0;
                    for (BeanItem item : list) {
                        // TODO 如果url相同, 则不再保存入库  后面可以改成标题+最近时间
                        if (itemMapper.selectCount(new QueryWrapper<BeanItem>().lambda().eq(BeanItem::getUrl, item.getUrl())) < 1) {
                            if (itemMapper.insert(item) == 1) {
                                jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(Constants.ITEM_TOPIC), item);
                                log.debug("成功入库并推送到队列:" + item);
                                count++;
                            } else {
                                log.warn("item:{} 保存失败", item);
                            }
                        }
                    }
                    log.debug("[" + website.getName() + "] [" + module.getName() + "] 成功入库" + count + "条");
                    moduleGrabLogMapper.insert(moduleGrabLog.setEndTime(new Date()).setGrabCount(list.size()).setSavedCount(count));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                latch.countDown();
            }
        }
    }

}
