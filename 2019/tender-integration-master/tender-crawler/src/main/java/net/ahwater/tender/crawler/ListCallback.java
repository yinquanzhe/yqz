package net.ahwater.tender.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.*;
import net.ahwater.tender.db.mapper.GrabErrorMapper;
import net.ahwater.tender.db.util.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yyc on 2018/2/1 19:29.
 * Nothing is true but improving yourself.
 */
@Slf4j
public class ListCallback implements Callable<List<BeanItem>> {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Pattern pattern = Pattern.compile("((\\d{2}|\\d{4})[-/年])?\\d{1,2}[-/月]\\d{1,2}");

    private GrabErrorMapper grabErrorMapper;
    private BeanModule module;
    private BeanWebsite website;
    private List<BeanParam> params;

    // 标识区分测试还是真实抓取
    private boolean isTest;

    public ListCallback(BeanModule module, BeanWebsite website, List<BeanParam> params, GrabErrorMapper grabErrorMapper, boolean isTest) {
        this.module = module;
        this.website = website;
        this.params = params;
        this.grabErrorMapper = grabErrorMapper;
        this.isTest = isTest;
    }

    @SuppressWarnings({"Duplicates"})
    @Override
    public List<BeanItem> call() {
        List<BeanItem> items = new CopyOnWriteArrayList<>();
        AspParam aspParam = new AspParam();
        RestTemplate restTemplate = new RestTemplate(Collections.singletonList(new StringHttpMessageConverter(Charset.forName("UTF-8"))));
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);    //关闭css

        if (module.getJsDelay() != null) {
            if (module.getJsDelay() < 1) {
                webClient.getOptions().setJavaScriptEnabled(false);
            } else {
                webClient.getOptions().setJavaScriptEnabled(true);
                webClient.waitForBackgroundJavaScript(1000L * module.getJsDelay());
            }
        }
        for (int i = module.getPageStart(); i <= module.getMaxPage(); i++) {
            String url = (module.getUrl().startsWith("http") ? "" : website.getDomain()) + module.getUrl();
            try {
                List<NameValuePair> kvs = new ArrayList<>();
                if (module.getPageKey().startsWith("{") && module.getPageKey().endsWith("}")) {
                    url = url.replace(module.getPageKey(), i + "");
                } else {
                    kvs.add(new NameValuePair(module.getPageKey(), i + ""));
                }

                if (params != null && params.size() > 0) {
                    for (BeanParam param : params) {
                        if (param.getName().startsWith("{") && param.getName().endsWith("}")) {
                            url = url.replace(param.getName(), param.getVal());
                        } else {
                            kvs.add(new NameValuePair(param.getName(), param.getVal() != null ? URLEncoder.encode(param.getVal(), "UTF-8") : ""));
                        }
                    }
                }

                if (module.getViewstage() == 1) {
                    if (aspParam.viewstage != null) {
                        kvs.add(new NameValuePair("__VIEWSTATE", aspParam.viewstage));
                    }
                    if (aspParam.viewstategenerator != null) {
                        kvs.add(new NameValuePair("__VIEWSTATEGENERATOR", aspParam.viewstategenerator));
                    }
                    if (aspParam.eventvalidation != null) {
                        kvs.add(new NameValuePair("__EVENTVALIDATION", aspParam.eventvalidation));
                    }
                    if (aspParam.eventtarget != null) {
                        kvs.add(new NameValuePair("__EVENTTARGET", aspParam.eventtarget));
                    }
                    if (aspParam.eventargument != null) {
                        kvs.add(new NameValuePair("__EVENTARGUMENT", aspParam.eventargument));
                    }
                    if (aspParam.viewstateencrypted != null) {
                        kvs.add(new NameValuePair("__VIEWSTATEENCRYPTED", aspParam.viewstateencrypted));
                    }
                }

                log.trace("url>>>>" + url);
                log.trace("params>>>>" + kvs.toString());

                if (module.getType() == 1) {    // html抓取方式
                    WebRequest request = new WebRequest(new URL(url), HttpMethod.valueOf(module.getReqMethod().toUpperCase()));
                    request.setRequestParameters(kvs);
                    HtmlPage page = webClient.getPage(request);
                    //                    log.trace(page.asXml());
                    if (module.getViewstage() == 1) {
                        DomElement __VIEWSTATE = page.getElementById("__VIEWSTATE");
                        DomElement __VIEWSTATEGENERATOR = page.getElementById("__VIEWSTATEGENERATOR");
                        DomElement __EVENTVALIDATION = page.getElementById("__EVENTVALIDATION");
                        DomElement __EVENTTARGET = page.getElementById("__EVENTTARGET");
                        DomElement __EVENTARGUMENT = page.getElementById("__EVENTARGUMENT");
                        DomElement __VIEWSTATEENCRYPTED = page.getElementById("__VIEWSTATEENCRYPTED");
                        if (__VIEWSTATE != null) {
                            aspParam.viewstage = __VIEWSTATE.getAttribute("value");
                        }
                        if (__VIEWSTATEGENERATOR != null) {
                            aspParam.viewstategenerator = __VIEWSTATEGENERATOR.getAttribute("value");
                        }
                        if (__EVENTVALIDATION != null) {
                            aspParam.eventvalidation = __EVENTVALIDATION.getAttribute("value");
                        }
                        if (__EVENTTARGET != null) {
                            aspParam.eventtarget = __EVENTTARGET.getAttribute("value");
                        }
                        if (__EVENTARGUMENT != null) {
                            aspParam.eventargument = __EVENTARGUMENT.getAttribute("value");
                        }
                        if (__VIEWSTATEENCRYPTED != null) {
                            aspParam.viewstateencrypted = __VIEWSTATEENCRYPTED.getAttribute("value");
                        }
                    }
                    List<HtmlElement> hrefList = page.getByXPath(module.getUrlXpathInList() == null ? "" : module.getUrlXpathInList());
                    List<HtmlElement> titleList = null;
                    List<HtmlElement> timeList = null;
                    //                List<HtmlElement> rmkList = null;
                    if (module.getTitleXpathInList() != null && module.getTitleXpathInList().length() > 3) {
                        titleList = page.getByXPath(module.getTitleXpathInList());
                    }
                    if (module.getTimeXpathInList() != null && module.getTimeXpathInList().length() > 3) {
                        timeList = page.getByXPath(module.getTimeXpathInList());
                    }
                    //                if (module.getItemrmkxpath() != null && module.getItemrmkxpath().length() > 3) {
                    //                    rmkList = page.getByXPath(module.getItemrmkxpath());
                    //                }
                    if (hrefList != null) {
                        for (int j = 0; j < hrefList.size(); j++) {
                            BeanItem item = new BeanItem();
                            item.setModuleId(module.getId());
                            item.setGrabTime(new Date());
                            String href = hrefList.get(j).getAttribute(module.getUrlAttrInList());
                            if (!href.startsWith("http")) {
                                // TODO 如果网页地址为http://domain.com/news/list.html 且href为1001.html这种相对路径, 这里直接用"/"拼接 可能会出现问题
                                href = website.getDomain() + (href.startsWith("/") ? "" : "/") + href;
                            }
                            item.setUrl(href);
                            if (titleList != null && titleList.size() > j) {
                                String title = module.getTitleAttrInList() != null ?
                                        module.getTitleAttrInList().equals("text") ? titleList.get(j).asText() : titleList.get(j).getAttribute(module.getTitleAttrInList()) : null;
                                if (title != null) {
                                    title = title.replaceAll("<.*?</?.*?>", "");
                                }
                                item.setTitle(title);
                            }
                            if (timeList != null && timeList.size() > j) {
                                String time = module.getTimeAttrInList() != null ?
                                        module.getTimeAttrInList().equals("text") ? timeList.get(j).asText() : timeList.get(j).getAttribute(module.getTimeAttrInList()) : null;
                                if (time != null) {
                                    Matcher matcher = pattern.matcher(time);
                                    if (matcher.find()) {
                                        String s = matcher.group(0);
                                        s = s.replaceAll("\\D", "-");
                                        if (s.length() == 10) {
                                            item.setPubTime(sdf.parse(s));
                                        } else if (s.length() == 8) {
                                            item.setPubTime(sdf.parse("20" + s));
                                        } else {
                                            Date t = sdf.parse(LocalDate.now().getYear() + "-" + s);
                                            if (t.after(new Date())) {
                                                t = sdf.parse(LocalDate.now().getYear() - 1 + "-" + s);
                                            }
                                            item.setPubTime(t);
                                        }
                                        //                                    log.trace("{}==={}===={}", item.getTitle(), s, item.getPubtime());
                                    }
                                }
                            }

                            //                        if (rmkList != null && rmkList.size() > j) {
                            //                            String remark = module.getUrlAttrInList() != null ?
                            //                                    module.getItemrmkattrkey().equals("text") ? rmkList.get(j).asText() : rmkList.get(j).getAttribute(module.getUrlAttrInList()) : null;
                            //                            item.setRemark(remark);
                            //                        }
                            // 发布时间在10天内的信息 才会入库
                            if (item.getPubTime() != null && (isTest || isWithin10Days(item.getPubTime(), new Date()))) {
                                log.trace("采集到有效信息: " + item.toString());
                                items.add(item);
                            } else {
                                log.trace("采集到失效信息: " + item.toString());
                            }
                        }
                    } else {
                        log.error("Reeye>>>>内容采集失败!!");
                    }
                } else if (module.getType() == 2) {                        // json方式
                    String json = null;
                    if (module.getReqMethod() != null) {
                        if (module.getReqMethod().toUpperCase().equals("GET")) {
                            if (!url.contains("?")) {
                                url += "?";
                            } else {
                                for (int j = 0; j < kvs.size(); j++) {
                                    url += (kvs.get(i).getName() + "=" + kvs.get(i).getValue() + "&");
                                }
                            }
                            log.trace("JSON方式抓取: GET最终url>>>{}", url);
                            json = restTemplate.getForEntity(url, String.class).getBody();
                        } else if (module.getReqMethod().toUpperCase().equals("POST")) {
                            HttpHeaders headers = new HttpHeaders();
                            MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded;charset=UTF-8");
                            headers.setContentType(type);
                            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                            for (NameValuePair kv : kvs) {
                                map.add(kv.getName(), kv.getValue());
                            }
                            HttpEntity<MultiValueMap<String, String>> request1 = new HttpEntity<>(map, headers);
                            json = restTemplate.postForEntity(url, request1, String.class).getBody();
                        }
                    }
                    //                log.trace("JSON方式抓取, 获取的JSON>>{}", json);
                    if (json != null) {
                        items.addAll(getListFromJson(json, module));
                    }
                } else if (module.getType() == 3) { // 文本方式
                    String text = null;
                    if (module.getReqMethod() != null) {
                        if (module.getReqMethod().toUpperCase().equals("GET")) {
                            if (!url.contains("?")) {
                                url += "?";
                            } else {
                                for (int j = 0; j < kvs.size(); j++) {
                                    url += (kvs.get(i).getName() + "=" + kvs.get(i).getValue());
                                }
                            }
                            log.debug("文本方式抓取: GET最终url>>>{}", url);
                            text = restTemplate.getForEntity(url, String.class).getBody();
                        } else if (module.getReqMethod().toUpperCase().equals("POST")) {
                            HttpHeaders headers = new HttpHeaders();
                            MediaType type = MediaType.parseMediaType("text/plain;charset=UTF-8");
                            headers.setContentType(type);
                            String body = "";
                            for (NameValuePair kv : kvs) {
                                body += kv.getName() + "=" + kv.getValue() + "\n";
                            }
                            HttpEntity<String> request1 = new HttpEntity<>(body, headers);
                            text = restTemplate.postForEntity(url, request1, String.class).getBody();
                        }
                        if (text != null) {
                            log.trace("文本方式获取到内容: {}", text);
                            items.addAll(getListFromText(StringUtils.unicodeToString(text), module));
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                handleException(e, url);
            }
        }
        return items;
    }

    private void handleException(Exception e, String url) {
        BeanGrabError beanGatherLog = new BeanGrabError();
        beanGatherLog.setModuleId(this.module.getId());
        beanGatherLog.setUrl(url);
        beanGatherLog.setMsg(e.getMessage());
        beanGatherLog.setTime(new Date());
        if (e instanceof FailingHttpStatusCodeException) {
            beanGatherLog.setStatus(((FailingHttpStatusCodeException) e).getStatusCode());
        }
        if (!isTest) {
            grabErrorMapper.insert(beanGatherLog);
        }
        log.error(e.getMessage());
    }

    private List<BeanItem> getListFromText(String text, BeanModule module) throws Exception {
        List<BeanItem> list = new ArrayList<>();
        if (module.getUrlXpathInList() != null && module.getTitleXpathInList() != null && module.getTimeXpathInList() != null) {
            Pattern pattern1 = Pattern.compile(module.getUrlXpathInList());
            Pattern pattern2 = Pattern.compile(module.getTitleXpathInList());
            Pattern pattern3 = Pattern.compile(module.getTimeXpathInList());
            Matcher matcher1 = pattern1.matcher(text);
            Matcher matcher2 = pattern2.matcher(text);
            Matcher matcher3 = pattern3.matcher(text);
            while (matcher1.find() && matcher2.find() && matcher3.find()) {
                BeanItem item = new BeanItem();
                String urlStr = matcher1.group(1);
                String titleStr = matcher2.group(1);
                String timeStr = matcher3.group(1);
                if (!StringUtils.isEmpty(module.getUrlAttrInList())) {     // URL属性不为空
                    item.setUrl(module.getUrlAttrInList().replaceAll("\\{ID}", urlStr));  // URL属性值中替换掉 {ID}
                } else {
                    item.setUrl(urlStr);
                }
                item.setTitle(titleStr);
                //noinspection Duplicates
                if (timeStr != null) {
                    Matcher timeMatcher = pattern.matcher(timeStr);
                    if (timeMatcher.find()) {
                        String s = timeMatcher.group(0);
                        s = s.replaceAll("\\D", "-");
                        if (s.length() == 10) {
                            item.setPubTime(sdf.parse(s));
                        } else if (s.length() == 8) {
                            item.setPubTime(sdf.parse("20" + s));
                        } else {
                            Date t = sdf.parse(LocalDate.now().getYear() + "-" + s);
                            if (t.after(new Date())) {
                                t = sdf.parse(LocalDate.now().getYear() - 1 + "-" + s);
                            }
                            item.setPubTime(t);
                        }
                    }
                }
                if (item.getUrl() != null && item.getPubTime() != null && (isTest || isWithin10Days(item.getPubTime(), new Date()))) {
                    item.setModuleId(module.getId());
                    item.setGrabTime(new Date());
                    list.add(item);
                    log.trace("采集成功: " + item.toString());
                } else {
                    log.trace("采集到失效信息: " + item.toString());
                }
            }
        }
        return list;
    }

    @SuppressWarnings("all")
    private List<BeanItem> getListFromJson(String json, BeanModule module) {
        List<BeanItem> list = new ArrayList<>();
        ObjectMapper om = new ObjectMapper();
        List<Map<String, Object>> arr = new ArrayList<>();
        try {
            if (json.startsWith("{")) {
                LinkedHashMap<String, Object> map = om.readValue(json, LinkedHashMap.class);
                String[] keys = module.getUrlXpathInList().split("\\.");
                for (int i = 0; i < keys.length; i++) {
                    if (i == keys.length - 1) {
                        List<Map<String, Object>> temp = (List<Map<String, Object>>) map.get(keys[i]);
                        arr.addAll(temp);
                    } else {
                        map = (LinkedHashMap<String, Object>) map.get(keys[i]);
                    }
                }
            } else {
                if (module.getUrlXpathInList() == null || module.getUrlXpathInList().trim().equals("")) {
                    arr.addAll(om.readValue(json, ArrayList.class));
                }
            }
            for (int i = 0; i < arr.size(); i++) {
                BeanItem item = new BeanItem();
                item.setModuleId(module.getId());
                item.setGrabTime(new Date());
                // 获取url
                if (module.getUrlXpathInList() == null || !module.getUrlXpathInList().startsWith("//")) {
                    LinkedHashMap<String, Object> m1 = (LinkedHashMap<String, Object>) arr.get(i);
                    if (module.getUrlAttrInList() != null) {
                        String[] urlKeys = module.getUrlAttrInList().split("\\.");
                        for (int j = 0; j < urlKeys.length; j++) {
                            if (j == urlKeys.length - 1) {
                                if (m1.containsKey(urlKeys[j])) {
                                    String url = m1.get(urlKeys[j]).toString();
                                    if (!url.startsWith("http")) {
                                        url = website.getDomain() + (url.startsWith("/") ? "" : "/") + url;
                                    }
                                    item.setUrl(url);
                                }
                            } else {
                                m1 = (LinkedHashMap<String, Object>) m1.get(urlKeys[j]);
                            }
                        }
                    }
                }

                // title
                if (module.getTitleXpathInList() == null || !module.getTitleXpathInList().startsWith("//")) {
                    LinkedHashMap<String, Object> m2 = (LinkedHashMap<String, Object>) arr.get(i);
                    if (module.getTitleAttrInList() != null) {
                        String[] titleKeys = module.getTitleAttrInList().split("\\.");
                        for (int j = 0; j < titleKeys.length; j++) {
                            if (j == titleKeys.length - 1) {
                                if (m2.containsKey(titleKeys[j])) {
                                    item.setTitle(m2.get(titleKeys[j]).toString());
                                }
                            } else {
                                m2 = (LinkedHashMap<String, Object>) m2.get(titleKeys[j]);
                            }
                        }
                    }
                }

                // time
                if (module.getTimeXpathInList() == null || !module.getTimeXpathInList().startsWith("//")) {
                    LinkedHashMap<String, Object> m3 = (LinkedHashMap<String, Object>) arr.get(i);
                    if (module.getTimeAttrInList() != null) {
                        String[] timeKeys = module.getTimeAttrInList().split("\\.");
                        for (int j = 0; j < timeKeys.length; j++) {
                            if (j == timeKeys.length - 1) {
                                if (m3.containsKey(timeKeys[j])) {
                                    String str = m3.get(timeKeys[j]).toString();
                                    if (str != null) {
                                        Matcher matcher = pattern.matcher(str);
                                        if (matcher.find()) {
                                            String s = matcher.group(0);
                                            s = s.replaceAll("\\D", "-");
                                            if (s.length() == 10) {
                                                item.setPubTime(sdf.parse(s));
                                            } else if (s.length() == 8) {
                                                item.setPubTime(sdf.parse("20" + s));
                                            } else {
                                                Date t = sdf.parse(LocalDate.now().getYear() + "-" + s);
                                                if (t.after(new Date())) {
                                                    t = sdf.parse(LocalDate.now().getYear() - 1 + "-" + s);
                                                }
                                                item.setPubTime(t);
                                            }
                                        }
                                    }
                                }
                            } else {
                                m3 = (LinkedHashMap<String, Object>) m3.get(timeKeys[j]);
                            }
                        }
                    }
                }

//                // remark
//                if (module.getItemrmkxpath() == null || !module.getItemrmkxpath().startsWith("//")) {
//                    LinkedHashMap<String, Object> m4 = (LinkedHashMap<String, Object>) arr.get(i);
//                    if (module.getItemrmkattrkey() != null) {
//                        String[] remarkKeys = module.getItemrmkattrkey().split("\\.");
//                        for (int j = 0; j < remarkKeys.length; j++) {
//                            if (j == remarkKeys.length - 1) {
//                                if (m4.containsKey(remarkKeys[j])) {
//                                    item.setRemark(m4.get(remarkKeys[j]).toString());
//                                }
//                            } else {
//                                m4 = (LinkedHashMap<String, Object>) m4.get(remarkKeys[j]);
//                            }
//                        }
//                    }
//                }
//
//                // content
//                if (module.getItemcontentxpath() != null && !module.getItemcontentxpath().startsWith("//")) {
//                    LinkedHashMap<String, Object> m5 = (LinkedHashMap<String, Object>) arr.get(i);
//                    String[] contentKeys = module.getItemcontentxpath().split("\\.");
//                    for (int j = 0; j < contentKeys.length; j++) {
//                        if (j == contentKeys.length - 1) {
//                            if (m5.containsKey(contentKeys[j])) {
//                                item.setHtmltext(m5.get(contentKeys[j]).toString());
//                            }
//
//                        } else {
//                            m5 = (LinkedHashMap<String, Object>) m5.get(contentKeys[j]);
//                        }
//                    }
//                }

                if (item.getUrl() != null && item.getPubTime() != null && (isTest || isWithin10Days(item.getPubTime(), new Date()))) {
                    log.trace("采集到有效信息: " + item.toString());
                    list.add(item);
                } else {
                    log.trace("采集到失效信息: " + item.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    class AspParam {
        String viewstage;
        String viewstategenerator;
        String eventvalidation;
        String eventtarget;
        String eventargument;
        String viewstateencrypted;
    }

    private static boolean isWithin10Days(Date addtime, Date now) {
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(now); //把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -10);  //设置为10天前
        Date before7days = calendar.getTime();   //得到10天前的时间
        return before7days.getTime() < addtime.getTime();
    }

}