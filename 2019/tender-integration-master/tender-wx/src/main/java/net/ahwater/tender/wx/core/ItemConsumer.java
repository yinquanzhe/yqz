package net.ahwater.tender.wx.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.*;
import net.ahwater.tender.db.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Created by Reeye on 2018/5/28 17:20
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
@Slf4j
public class ItemConsumer {

    @Autowired
    private PushLogMapper pushLogMapper;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserWebsiteMapper userWebsiteMapper;

    @Autowired
    private KeywordGroupMapper keywordGroupMapper;

    @Autowired
    private ExcludedWordMapper excludedWordMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private KeywordGroupUnitMapper keywordGroupUnitMapper;

    @Autowired
    private PushScheduler pushScheduler;

    @JmsListener(destination = Constants.ITEM_TOPIC, containerFactory = "jmsListenerContainerFactory4ItemTopic")
    public void receiveMsg(final ObjectMessage msg, Session session) {
        try {
            @SuppressWarnings("unchecked")
            final BeanItem item = (BeanItem) msg.getObject();
            log.debug("Topic消息正文: " + item.toString());

            // TODO 这里需要优化, 可以加个用户-网站-关键词-排除词的缓存, 避免每次都要查询计算
            /* 匹配用户订阅的网站和设置的关键词, 生成推送记录数据 */
            // 1. 获取订阅了这个网站的用户集合
            List<BeanUserWebsite> userWebsites = userWebsiteMapper.selectList(
                    new QueryWrapper<BeanUserWebsite>()
                            .apply("website_id=(select website_id from t_module where id={0})", item.getModuleId()));
            if (userWebsites.size() > 0) {
                // 用户集合
                List<BeanUser> users = userMapper.selectBatchIds(userWebsites
                        .stream()
                        .map(BeanUserWebsite::getUserId)
                        .collect(Collectors.toList()))
                        .stream()
                        .filter(BeanUser::isEnabled)
                        .collect(Collectors.toList());  // 这是个人用户
                users.addAll(userMapper.selectList(new QueryWrapper<BeanUser>().lambda().ne(BeanUser::getUnitId, 0)));  // 加入企业用户
                log.debug("Item:  id>{} title>{}, 关注用户 数量为: {}", item.getId(), item.getTitle(), users.size());
                // 2. 获取这些用户的各种关键词等
                for (BeanUser user : users) {
                    final List<BeanKeywordGroup> keywordGroups;    // 用户下的分组
                    if (user.getUnitId() == 0) {    // 个人用户
                        keywordGroups = keywordGroupMapper.selectList(new QueryWrapper<BeanKeywordGroup>()
                                .lambda()
                                .eq(BeanKeywordGroup::getCreatedUserId, user.getId()));
                    } else {    // 企业用户
                        BeanUnit unit = unitMapper.selectById(user.getUnitId());
                        if (unit.getParentId() == 0) { // 一级部门
                            keywordGroups = keywordGroupMapper.selectList(new QueryWrapper<BeanKeywordGroup>()
                                    .lambda()
                                    .eq(BeanKeywordGroup::getUnitId, unit.getId()));
                        } else {    // 子部门
                            List<BeanKeywordGroupUnit> keywordGroupUnits = keywordGroupUnitMapper.selectList(new QueryWrapper<BeanKeywordGroupUnit>()
                                .lambda()
                                .eq(BeanKeywordGroupUnit::getUnitId, unit.getId()));
                            keywordGroups = keywordGroupMapper.selectBatchIds(
                                    keywordGroupUnits.stream()
                                            .map(BeanKeywordGroupUnit::getKeywordGroupId)
                                            .collect(Collectors.toList()));
                        }
                    }
                    log.debug("user [id:{}]  keywordGroups count: {}", user.getId(), keywordGroups.size());

                    // 用户所有分组下的关键词
                    List<BeanKeyword> keywords = keywordMapper.selectList(new QueryWrapper<BeanKeyword>()
                            .lambda()
                            .in(BeanKeyword::getGroupId, keywordGroups.stream()
                                    .map(BeanKeywordGroup::getId)
                                    .collect(Collectors.toList())));
                    log.debug("user [id:{}] keywords count: {}", user.getId(), keywords.size());
                    // 关键词分配到各分组下
                    keywordGroups.forEach(e -> e.setKeywords(keywords.stream()
                            .filter(kw -> kw.getGroupId().equals(e.getId()))
                            .collect(Collectors.toList())));

                    List<BeanExcludedWord> words = excludedWordMapper.selectList(new QueryWrapper<BeanExcludedWord>()
                            .lambda()
                            .eq(BeanExcludedWord::getUserId, user.getId()));
                    List<BeanExcludedWord> excluded = words.stream().filter(e -> e.getType() == 2).collect(Collectors.toList());    // 排除词
                    List<BeanExcludedWord> confused = words.stream().filter(e -> e.getType() == 1).collect(Collectors.toList());    // 混淆词

                    boolean flag = true; // 推送标识,  true代表需要推送
                    String title = item.getTitle(); // 用于暂存标题
                    BeanPushLog pushLog = new BeanPushLog();

                    group: for (BeanKeywordGroup group : keywordGroups) {
                        if (group.getNoExcluded()) { // 开启了排除词过滤
                            flag = excluded.stream().noneMatch(e -> item.getTitle().contains(e.getWord()));
                            log.debug("title [{}] contains excludedWords: {}", item.getTitle(), flag);
                        }
                        if (flag) { // 通过了排除词过滤
                            if (group.getNoConfused()) { // 开启了混淆词过滤
                                if (confused != null && confused.size() >0) {
                                    for (BeanExcludedWord confusedWord : confused) {
                                        title = title.replaceAll(Matcher.quoteReplacement(confusedWord.getWord()), "");
                                    }
                                    log.debug("title that after remove confusedWord: {}", title);
                                }
                            }
                            List<BeanKeyword> keywordList = group.getKeywords();
                            for (BeanKeyword keyword : keywordList) {
                                if (keyword.getName() != null) {
                                    String[] kws = keyword.getName().split(",");
                                    boolean temp = true;
                                    for (String kw : kws) {
                                        if (!title.contains(kw)) {
                                            temp = false;
                                        }
                                    }
                                    flag = temp;
                                    if (flag) {
                                        pushLog.setKeywordId(keyword.getId());
                                        break group;
                                    }
                                }
                            }
                        }
                    }
                   if (flag) {
                        // 生成推送记录
                        pushLog.setStatus(1);
                        pushLog.setUserId(user.getId());
                        pushLog.setItemId(item.getId());
                        pushLog.setCreateTime(new Date());
                        log.debug("add one new push log: {}", pushLogMapper.insert(pushLog));
                    }
                }
            } else {
                log.debug("userWebsites.size() = 0");
            }
            msg.acknowledge();
        } catch (Exception e) {
            log.error("Reeye>>" + e.getMessage());
            try {
                session.recover();
            } catch (JMSException e1) {
                log.error(e1.getMessage());
            }
        }
    }

    @JmsListener(destination = Constants.GRAB_DONE, containerFactory = "jmsListenerContainerFactory4GrabTopic")
    public void receiveMsg1(final TextMessage msg, Session session) {
        try {
            log.debug("Topic[{}] 收到消息: {}", Constants.GRAB_DONE, msg.getText());
            List<BeanUser> users = userMapper.selectList(new QueryWrapper<BeanUser>().lambda().eq(BeanUser::getPushTime, -1));
            log.debug("待实时推送的UserIds: {}", users.stream().map(BeanUser::getId).collect(Collectors.toList()));
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
            for (BeanUser user : users) {
                fixedThreadPool.execute(pushScheduler.new PushJob(user));
            }
            msg.acknowledge();
        } catch (JMSException e) {
            log.error("Reeye>>" + e.getMessage());
            try {
                session.recover();
            } catch (JMSException e1) {
                log.error(e1.getMessage());
            }
        }
    }

}
