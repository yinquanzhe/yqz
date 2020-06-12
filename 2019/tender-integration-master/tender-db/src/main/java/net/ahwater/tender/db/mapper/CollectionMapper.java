package net.ahwater.tender.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.ahwater.tender.db.entity.BeanCollection;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Reeye
 * @since 2018-11-01
 */
@CacheNamespace(flushInterval = 1000L * 3600)
public interface CollectionMapper extends BaseMapper<BeanCollection> {

}
