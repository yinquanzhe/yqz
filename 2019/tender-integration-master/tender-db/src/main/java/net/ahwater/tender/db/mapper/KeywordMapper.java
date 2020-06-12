package net.ahwater.tender.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.ahwater.tender.db.entity.BeanKeyword;
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
public interface KeywordMapper extends BaseMapper<BeanKeyword> {

    List<Map<String, Object>> selectTop3KeywordByUserId(@Param("userId") Integer userId);

    List<Map<String, Object>> selectCurrentMonthByUserId(@Param("userId") Integer userId);

    List<Map<String, Object>> selectTwoMonthByUserId(@Param("userId") Integer userId);

    List<Integer> selectUserIdByModuleId(@Param("mid") Integer mid);

}
