package net.ahwater.tender.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.ahwater.tender.db.entity.BeanKeywordGroupUnit;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Reeye
 * @since 2019-03-12
 */
@CacheNamespace(flushInterval = 1000L * 3600)
public interface KeywordGroupUnitMapper extends BaseMapper<BeanKeywordGroupUnit> {

}
