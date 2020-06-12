package net.ahwater.tender.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.ahwater.tender.db.entity.BeanPushLog;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Reeye
 * @since 2018-11-01
 */
@CacheNamespace(flushInterval = 1000L * 3600)
public interface PushLogMapper extends BaseMapper<BeanPushLog> {

    int pushCountToday(@Param("userId") Integer userId);

    int updateStatusByIdsIn(@Param("status") Integer status, @Param("ids") List<Integer> ids);

    int deletePushLogByWsidIn(@Param("wsids") List<Integer> wsids);

}
