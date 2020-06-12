package net.ahwater.tender.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanItem;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.db.entity.R;
import net.ahwater.tender.db.mapper.ItemMapper;
import net.ahwater.tender.db.util.StringUtils;
import net.ahwater.tender.web.annotation.CurrentUser;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Reeye on 2018/12/21 16:53
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Api(tags = "招标信息控制类")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/item")
public class ItemController {

    @Value("${lucene.index.path}")
    private String luceneIndexPath;

    @Autowired
    private ItemMapper itemMapper;

    @ApiOperation(value = "分页获取搜索信息", notes = "根据搜索词 分页获取抓取到的招标信息")
    @PreAuthorize("permitAll()")
    @GetMapping
    public R search(Page<BeanItem> page, @RequestParam String q) throws Exception {
        List<BeanItem> items = new ArrayList<>();
        if (!StringUtils.isEmpty(q)) {
            Directory dir = FSDirectory.open(Paths.get(luceneIndexPath));
            IndexReader reader = DirectoryReader.open(dir);
            final IndexSearcher is = new IndexSearcher(reader);

            SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title"}, analyzer);
            Query query = parser.parse(q);
            Sort sort = new Sort(new SortField("title", SortField.Type.SCORE),
                    new SortedNumericSortField("pub_time", SortField.Type.LONG, true));
            long start = System.currentTimeMillis();
            TopDocs hits = is.search(query, Integer.MAX_VALUE, sort, true, false);
            long end = System.currentTimeMillis();
            log.debug("匹配[" + q + "]总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");

            QueryScorer scorer = new QueryScorer(query);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
            Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
            highlighter.setTextFragmenter(fragmenter);

            for (long i = page.getSize() * (page.getCurrent() - 1); i < hits.scoreDocs.length && i < page.getSize() * page.getCurrent(); i++) {
                Document document = is.doc(hits.scoreDocs[(int) i].doc);
                BeanItem item = new BeanItem();
                item.setId(Integer.parseInt(document.get("id")));
                String titile = document.get("title");
                TokenStream tokenStream = analyzer.tokenStream("title", new StringReader(titile));
                item.setTitle(highlighter.getBestFragment(tokenStream, titile));
                items.add(item);
            }
            reader.close();

            List<Integer> ids = items.stream().map(BeanItem::getId).collect(Collectors.toList());
            if (ids.size() > 0) {
                itemMapper.selectBatchIds(ids)
                        .forEach(e -> {
                            for (BeanItem item : items) {
                                if (item.getId().equals(e.getId())) {
                                    item.setPubTime(e.getPubTime());
                                    item.setUrl(e.getUrl());
                                    item.setContent(e.getContent());
                                }
                            }
                        });
            }
            page.setTotal(hits.totalHits);
        }
        return R.ok(page.setRecords(items));
    }

    @ApiOperation(value = "获取推送分页信息", notes = "根据地区码, 搜索词 分页获取用户的推送信息")
    @GetMapping("/listAllByPage")
    public R listAllByPage(
            @CurrentUser BeanUser user,
            Page page,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String dcode) {
        QueryWrapper<?> qw = new QueryWrapper<>().eq("t1.user_id", user.getId());
        if (!StringUtils.isEmpty(q)) {
            qw.like("t2.title", q);
        }
        if (!StringUtils.isEmpty(dcode)) {
            dcode = dcode.replaceAll("(00)+$", "");
            qw.likeRight("t6.code", dcode);
        }
        List<?> items = itemMapper.selectItemsPageByUserId(page, qw);
//        if (items.size() == 0) {
//            items = itemMapper.selectItemsPage(page);
//        }
        //noinspection unchecked
        return R.ok(page.setRecords(items));
    }

}
