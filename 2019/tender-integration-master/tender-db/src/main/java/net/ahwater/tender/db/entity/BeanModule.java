package net.ahwater.tender.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Reeye
 * @since 2018-11-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_module")
public class BeanModule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 站点ID
     */
    private Integer websiteId;

    /**
     * 名称
     */
    private String name;

    /**
     * URL
     */
    private String url;

    /**
     * 抓取方式 1HTML抓取 2JSON抓取
     */
    private Integer type;

    /**
     * 请求方式
     */
    private String reqMethod;

    /**
     * 分页参数名称
     */
    private String pageKey;

    /**
     * 分页参数起始值
     */
    private Integer pageStart;

    /**
     * 抓取最大页数
     */
    private Integer maxPage;

    /**
     * 抓取最大条数
     */
    private Integer maxItem;

    /**
     * ASP VIEWSTAGE支持
     */
    private Integer viewstage;

    /**
     * JS延迟(0不开启)
     */
    private Integer jsDelay;

    /**
     * 列表页中单条数据的URL获取规则(xpath)
     */
    private String urlXpathInList;

    /**
     * 列表页中单条数据的URL文本所在的属性
     */
    private String urlAttrInList;

    /**
     * 列表页中单条数据的标题获取规则(xpath)
     */
    private String titleXpathInList;

    /**
     * 列表页中单条数据的标题文本所在的属性
     */
    private String titleAttrInList;

    /**
     * 列表页中单条数据的时间获取规则(xpath)
     */
    private String timeXpathInList;

    /**
     * 列表页中单条数据的时间文本所在的属性
     */
    private String timeAttrInList;

    /**
     * 详情页中单条数据的时间获取规则(xpath)
     */
    private String timeXpathInItem;

    /**
     * 详情页中单条数据的时间文本所在的属性
     */
    private String timeAttrInItem;

    /**
     * 详情页中单条数据的正文获取规则(xpath)
     */
    private String contentXpathInItem;

    /**
     * 详情页中单条数据的正文所在的属性
     */
    private String contentAttrInItem;


}
