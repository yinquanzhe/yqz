package net.ahwater.tender.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.ahwater.tender.db.entity.BeanExcludedWord;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Reeye
 * @since 2019-03-06
 */
@CacheNamespace(flushInterval = 1000L * 3600)
public interface ExcludedWordMapper extends BaseMapper<BeanExcludedWord> {

}
