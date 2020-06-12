package net.ahwater.tender.db.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import net.ahwater.tender.db.entity.BeanItem;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Reeye
 * @since 2018-11-01
 */
@CacheNamespace(flushInterval = 1000L * 3600)
public interface ItemMapper extends BaseMapper<BeanItem> {

    List<Map<String, Object>> selectItemsPageByUserId(IPage page, @Param("ew") Wrapper wrapper);

    List<Map<String, Object>> selectItemsPage(IPage page);

}
