package net.ahwater.tender.crawler;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanItem;
import net.ahwater.tender.db.util.DateUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Reeye on 2018/12/21 16:26
 * Nothing is true but improving yourself.
 */
@Slf4j
@Component
public class LuceneTemplate {

    @Value("${lucene.index.path}")
    private String luceneIndexPath;

    private SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(); // 中文分词器

    public void generateIndex(List<BeanItem> list) throws Exception {
        Directory dir = FSDirectory.open(Paths.get(luceneIndexPath));
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, iwc);
//        log.debug("清空索引: " + writer.deleteAll());
        log.debug("获取到" + list.size() + "条数据进行索引生成");
        for (BeanItem aList : list) {
            Document doc = new Document();
            doc.add(new StoredField("id", aList.getId()));
            doc.add(new TextField("title", aList.getTitle(), Field.Store.YES));
            doc.add(new SortedNumericDocValuesField("pub_time", Long.parseLong(DateUtils.format("YYYYMMdd", aList.getPubTime()))));
//            doc.add(new TextField("content", list.get(i).getHtmltext(), Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.close();
        log.debug("索引生成成功:" + list.size() + "条");
    }

    public ScoreDoc[] search(String q) throws Exception {
        Directory dir = FSDirectory.open(Paths.get(luceneIndexPath));
        IndexReader reader = DirectoryReader.open(dir);
        final IndexSearcher is = new IndexSearcher(reader);

        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title"}, analyzer);
        Query query = parser.parse(q);

        Sort sort = new Sort(new SortField("title", SortField.Type.SCORE),
                new SortedNumericSortField("pub_time", SortField.Type.LONG, true));

        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, Integer.MAX_VALUE, sort, true, false);
        long end = System.currentTimeMillis();
        reader.close();
        log.debug("匹配[" + q + "]总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
        return hits.scoreDocs;
    }

}
