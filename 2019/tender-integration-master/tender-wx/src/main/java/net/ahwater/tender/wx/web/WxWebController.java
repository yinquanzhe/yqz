package net.ahwater.tender.wx.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.*;
import net.ahwater.tender.db.mapper.*;
import net.ahwater.tender.db.util.DateUtils;
import net.ahwater.tender.db.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Reeye on 2018/7/23 10:06
 * Nothing is true but improving yourself.
 */
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "SpringJavaAutowiredFieldsWarningInspection", "Duplicates"})
@Slf4j
@Controller
@RequestMapping("/wx")
public class WxWebController {

    private static final String MY_VIEW = "/my";
    private static final String INDEX_VIEW = "/index";

    @Value("${wechat.mp.server}")
    private String serverUrl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PriceMapper priceMapper;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private WebsiteMapper websiteMapper;

    @Autowired
    private UserWebsiteMapper userWebsiteMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private GrabLogMapper grabLogMapper;

    @Autowired
    private PushLogMapper pushLogMapper;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("serverUrl", serverUrl);
        return "login";
    }

    @GetMapping(MY_VIEW)
    public ModelAndView my(@RequestParam(required = false) String code) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("serverUrl", serverUrl);
        if (!StringUtils.isEmpty(code)) {
            try {
                WxMpOAuth2AccessToken token = wxMpService.oauth2getAccessToken(code);
                String openid = token.getOpenId();
                WxMpUser wxMpUser = wxMpService.getUserService().userInfo(openid);
                BeanUser user = userMapper.selectOne(new QueryWrapper<BeanUser>().lambda().eq(BeanUser::getWxopenid, openid));
                if (user == null) {
                    user = new BeanUser();
                    user.setWxopenid(openid);
                    user.setCreateTime(new Date());
                    user.setPushTime(-1);
                    user.setRoleId(4);
                    int res = userMapper.insert(user);
                    log.info("新增用户{}: {}", res == 1 ? "成功" : "失败", user);
                }
//                user.setKeywords(keywordMapper.selectList(new QueryWrapper<BeanKeyword>().lambda().eq(BeanKeyword::getUserId, user.getId())));
                List<BeanUserWebsite> userWebsites = userWebsiteMapper.selectList(new QueryWrapper<BeanUserWebsite>().lambda().eq(BeanUserWebsite::getUserId, user.getId()));
                user.setWebsites(websiteMapper.selectBatchIds(userWebsites.stream().map(BeanUserWebsite::getWebsiteId).collect(Collectors.toSet())));
                mv.setViewName(MY_VIEW);
                mv.addObject("user", user);
                mv.addObject("openid", openid);
                mv.addObject("nickname", wxMpUser.getNickname());
                mv.addObject("avatar", wxMpUser.getHeadImgUrl());
                if (user.getCreateTime() != null) {
                    mv.addObject("days", Math.abs(DateUtils.differentDays(new Date(), user.getCreateTime())) + 1);
                } else {
                    mv.addObject("days", 1);
                }
                BeanGrabLog grabLog = grabLogMapper.selectOne(new QueryWrapper<BeanGrabLog>().orderByDesc("id").last("limit 1"));
                if (grabLog != null) {
                    mv.addObject("lastTime", DateUtils.formatTime(grabLog.getStartTime()));
                } else {
                    mv.addObject("lastTime", "暂无信息");
                }
                mv.addObject("todayCount", pushLogMapper.pushCountToday(user.getId()));
                List<BeanRole> roles = roleMapper.selectList(new QueryWrapper<BeanRole>().lambda().ne(BeanRole::getType, 1));
                List<BeanPrice> prices = priceMapper.selectList(null);
                prices.forEach(e -> {
                    for (BeanRole role : roles) {
                        if (e.getRoleId().equals(role.getId())) {
                            role.getPrices().add(e);
                        }
                    }
                });
                mv.addObject("role", roleMapper.selectById(user.getRoleId()));
                mv.addObject("roles", roles);
                mv.addObject("websites", websiteMapper.selectList(null));
                log.info("用户[ID:{}]登录[我的]页面", user.getId());
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
//                mv.setViewName("redirect:/error");
                mv.setViewName("redirect:" +
                        wxMpService.oauth2buildAuthorizationUrl(
                                this.serverUrl + "/wx" + MY_VIEW,
                                WxConsts.OAuth2Scope.SNSAPI_BASE,
                                null));
            }
        } else {
            mv.setViewName("redirect:" +
                    wxMpService.oauth2buildAuthorizationUrl(
                            this.serverUrl + "/wx" + MY_VIEW,
                            WxConsts.OAuth2Scope.SNSAPI_BASE,
                            null));
        }
        return mv;
    }

    @GetMapping("/statistics")
    public ModelAndView statistics(@RequestParam String openid) {
        ModelAndView mv = new ModelAndView("statistics");
        mv.addObject("serverUrl", serverUrl);
        BeanUser user = userMapper.selectOne(new QueryWrapper<BeanUser>().lambda().eq(BeanUser::getWxopenid, openid));
        if (user != null) {
            mv.addObject("chart1", keywordMapper.selectCurrentMonthByUserId(user.getId()));
            mv.addObject("chart2", keywordMapper.selectTop3KeywordByUserId(user.getId()));
            mv.addObject("chart3", keywordMapper.selectTwoMonthByUserId(user.getId()));
        }
        return mv;
    }

    @ResponseBody
    @PostMapping("/updateUserPushTime")
    public Integer updateUserPushTime(@RequestParam String openid, @RequestParam Integer hour) {
        if (hour < -1 || hour > 23) {
            return 0;
        }
        BeanUser user = userMapper.selectOne(new QueryWrapper<BeanUser>().lambda().eq(BeanUser::getWxopenid, openid));
        int oldPushTime = user.getPushTime();
        if (oldPushTime == hour) {
            return 1;
        }
        user.setPushTime(hour);
        int res = userMapper.update(user, new UpdateWrapper<BeanUser>().eq("id", user.getId()));
        if (res == 1) {
            jmsMessagingTemplate.convertAndSend(Constants.PUSH_ADD, user);
            if (oldPushTime > 0 && hour < 0) {
                jmsMessagingTemplate.convertAndSend(Constants.PUSH_REMOVE, user);
            }
        }
        return res;
    }

    @Transactional
    @ResponseBody
    @PostMapping("/updateUserWebsite")
    public Integer updateUserWebsite(@RequestParam String openid, @RequestParam(value = "wsids[]") Integer[] wsids) {
        BeanUser user = userMapper.selectOne(new QueryWrapper<BeanUser>().lambda().eq(BeanUser::getWxopenid, openid));
        if (user != null) {
            BeanRole role = roleMapper.selectById(user.getRoleId());
            if (role != null) {
                if (wsids.length > 0 && (wsids.length <= role.getWebsiteCount() || role.getWebsiteCount() == -1)) {
                    List<BeanUserWebsite> userWebsites = userWebsiteMapper.selectList(new QueryWrapper<BeanUserWebsite>()
                            .lambda()
                            .eq(BeanUserWebsite::getUserId, user.getId()));
                    List<Integer> needDeletedWsids = userWebsites.stream()
                            .map(BeanUserWebsite::getWebsiteId)
                            .distinct()
                            .filter(e -> !Arrays.asList(wsids).contains(e))
                            .collect(Collectors.toList());
                    if (needDeletedWsids.size() > 0) {
                        int userWebsiteRes = userWebsiteMapper.delete(new QueryWrapper<BeanUserWebsite>()
                                .lambda()
                                .eq(BeanUserWebsite::getUserId, user.getId())
                                .in(BeanUserWebsite::getWebsiteId, needDeletedWsids));
                        log.debug("删除表[t_user_wesite]中userid[{}] weibsiteid{} 的记录{}条", user.getId(), needDeletedWsids, userWebsiteRes);
                        // 删除t_push_log表对应的记录
                        int pushLogRes = pushLogMapper.deletePushLogByWsidIn(needDeletedWsids);
                        log.debug("删除表[t_push_log]中userid[{}]的记录{}条", user.getId(), pushLogRes);
                    }
                    List<BeanUserWebsite> userWebsiteList = Arrays.stream(wsids)
                            .distinct()
                            .filter(e -> userWebsites.stream().noneMatch(ee -> ee.getWebsiteId().equals(e)))
                            .map(e -> {
                                    BeanUserWebsite website = new BeanUserWebsite();
                                    website.setUserId(user.getId());
                                    website.setWebsiteId(e);
                                    return website;
                            }).collect(Collectors.toList());
                    if (userWebsiteList.size() > 0) {
                        int res = userWebsiteMapper.insertAll(userWebsiteList);
                        log.debug("新增表[t_user_wesite]中userid[{}]的记录{}条", user.getId(), res);
                    }
                    return userWebsiteMapper.selectCount(new QueryWrapper<BeanUserWebsite>().lambda().eq(BeanUserWebsite::getUserId, user.getId()));
                } else {
                    return -1;  // 数量超出规定
                }
            }
        }
        return 0;
    }

    @ResponseBody
    @PostMapping("/insertOrUpdateKeyword")
    public BeanKeyword insertOrUpdateKeyword(BeanKeyword bean, @RequestParam String openid) {
        BeanUser user = userMapper.selectOne(new QueryWrapper<BeanUser>().lambda().eq(BeanUser::getWxopenid, openid));
        BeanRole role = roleMapper.selectOne(new QueryWrapper<BeanRole>().apply("id=(select role_id from t_user where id={0})", bean.getUserId()));
        int count = keywordMapper.selectCount(new QueryWrapper<BeanKeyword>().lambda().eq(BeanKeyword::getUserId, bean.getUserId()));
        if (role.getKeywordCount() == -1 || count < role.getKeywordCount()) {
            bean.setUserId(user.getId())
                    .setName(bean.getName().replaceAll("\\s", "").replaceAll("[，。、；]", ","));
            int res = bean.getId() == null ? keywordMapper.insert(bean) : keywordMapper.updateById(bean);
            return res == 1 ? bean : null;
        } else {
            return null;
        }
    }

    @Transactional
    @ResponseBody
    @PostMapping("/deleteKeyword")
    public Integer deleteKeyword(@RequestParam Integer keywordId, @RequestParam String openid) {
        keywordMapper.delete(new QueryWrapper<BeanKeyword>()
                .eq("id", keywordId)
                .apply("user_id=(select id from t_user where wxopenid={0})", openid));
        return 1;
    }

    @GetMapping(INDEX_VIEW)
    public ModelAndView index(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(required = false, defaultValue = "") String pushCode) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("serverUrl", serverUrl);
        if (!StringUtils.isEmpty(code)) {
            try {
                WxMpOAuth2AccessToken token = wxMpService.oauth2getAccessToken(code);
                String openid = token.getOpenId();
                BeanUser user = userMapper.selectOne(new QueryWrapper<BeanUser>().lambda().eq(BeanUser::getWxopenid, openid));
                if (user == null) {
                    user = new BeanUser();
                    user.setWxopenid(openid);
                    user.setCreateTime(new Date());
                    user.setPushTime(-1);
                    user.setRoleId(4);
                    log.info("新增用户{}: {}", userMapper.insert(user) == 1 ? "成功" : "失败", user);
                }
                mv.setViewName(INDEX_VIEW);
                mv.addObject("user", user);
                mv.addObject("openid", openid);
                log.info("用户[ID:{}]登录[招投标信息]页面", user.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
                mv.setViewName("redirect:" +
                        wxMpService.oauth2buildAuthorizationUrl(
                                this.serverUrl + "/wx" + INDEX_VIEW + "?pushCode=" + pushCode,
                                WxConsts.OAuth2Scope.SNSAPI_BASE,
                                null));
            }
        } else {
            mv.setViewName("redirect:" +
                    wxMpService.oauth2buildAuthorizationUrl(
                            this.serverUrl + "/wx" + INDEX_VIEW + "?pushCode=" + pushCode,
                            WxConsts.OAuth2Scope.SNSAPI_BASE,
                            null));
        }
        return mv;
    }

    @GetMapping("/search")
    public ModelAndView search(
            @RequestParam(value = "code", required = false) String code) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("serverUrl", serverUrl);
        if (!StringUtils.isEmpty(code)) {
            try {
                WxMpOAuth2AccessToken token = wxMpService.oauth2getAccessToken(code);
                String openid = token.getOpenId();
                BeanUser user = userMapper.selectOne(new QueryWrapper<BeanUser>().lambda().eq(BeanUser::getWxopenid, openid));
                if (user == null) {
                    user = new BeanUser();
                    user.setWxopenid(openid);
                    user.setCreateTime(new Date());
                    user.setPushTime(-1);
                    user.setRoleId(4);
                    log.info("新增用户{}: {}", userMapper.insert(user) == 1 ? "成功" : "失败", user);
                }
                mv.setViewName("search");
                mv.addObject("user", user);
                mv.addObject("openid", openid);
                log.debug("用户[ID:{}]登录[招投标搜索]页面", user.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
                mv.setViewName("redirect:" +
                        wxMpService.oauth2buildAuthorizationUrl(
                                this.serverUrl + "/wx/search",
                                WxConsts.OAuth2Scope.SNSAPI_BASE,
                                null));
            }
        } else {
            mv.setViewName("redirect:" +
                    wxMpService.oauth2buildAuthorizationUrl(
                            this.serverUrl + "/wx/search",
                            WxConsts.OAuth2Scope.SNSAPI_BASE,
                            null));
        }
        return mv;
    }

    @ResponseBody
    @PostMapping("/listAllByPage")
    public Page<?> listAllByPage(
            Page page,
            @RequestParam Integer userId,
            @RequestParam(required = false) String pushCode) {
        QueryWrapper<?> qw = new QueryWrapper<>().eq("t1.user_id", userId);
        if (!StringUtils.isEmpty(pushCode)) {
            qw.eq("t1.code", pushCode);
        }
        List<?> items = itemMapper.selectItemsPageByUserId(page, qw);
//        if (items.size() == 0) {
//            items = itemMapper.selectItemsPage(page);
//        }
        //noinspection unchecked
        return page.setRecords(items);
    }

}
