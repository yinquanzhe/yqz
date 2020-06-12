package net.ahwater.tender.crawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanItem;
import net.ahwater.tender.db.entity.BeanModule;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yyc on 2018/2/1 19:34.
 * Nothing is true but improving yourself.
 */
@Slf4j
public class ItemCallback implements Callable<BeanItem> {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private BeanModule module;
    private BeanItem item;

    public ItemCallback(BeanModule module, BeanItem item) {
        this.module = module;
        this.item = item;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public BeanItem call() throws Exception {
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
        WebRequest request = new WebRequest(new URL(item.getUrl()), HttpMethod.GET);
        HtmlPage page = webClient.getPage(request);

        if (item.getTitle() == null || item.getTitle().length() == 0) {
            if (module.getTitleXpathInList() != null && module.getTitleXpathInList().startsWith("//")) {
                List<HtmlElement> titleEles = page.getByXPath(module.getTitleXpathInList());
                if (titleEles != null && titleEles.size() > 0) {
                    item.setTitle(module.getTitleAttrInList().equals("text") ? titleEles.get(0).getTextContent() : titleEles.get(0).getAttribute(module.getTitleAttrInList()));
                } else {
                    log.error("标题采集失败: {}", item.getUrl());
                }
            }
        }

        if (item.getPubTime() == null) {
            if (module.getTimeXpathInItem() != null && module.getTimeXpathInItem().startsWith("//")) {
                List<HtmlElement> timeEles = page.getByXPath(module.getTimeXpathInItem());
                if (timeEles != null && timeEles.size() > 0) {
                    String timeReg = "((\\d{2}|\\d{4})[-/年])?\\d{1,2}[-/月]\\d{1,2}";
                    String timerStr = module.getTimeAttrInList().equals("text") ? timeEles.get(0).asText() : timeEles.get(0).getAttribute(module.getTimeAttrInList());
                    if (timerStr != null) {
                        Matcher matcher = Pattern.compile(timeReg).matcher(timerStr);
                        if (matcher.find()) {
                            String str = matcher.group(0);
                            str = str.replaceAll("\\D", "-");
                            item.setPubTime(sdf.parse(str));
                        }
                    }
                } else {
                    log.error("时间采集失败: {}", item.getUrl());
                }
            }
        }

        if (item.getContent() == null || item.getContent().length() == 0) {
            if (module.getContentXpathInItem() != null && module.getContentXpathInItem().startsWith("//")) {
                List<HtmlElement> contentEles = page.getByXPath(module.getContentXpathInItem());
                if (contentEles != null && contentEles.size() > 0) {
                    item.setContent(contentEles.get(0).asXml());
                } else {
                    log.error("内容采集失败: {}", item.getUrl());
                }
            }
        }
        return item;
    }

}
