package net.ahwater.ahwaterCloud.dao;

import net.ahwater.ahwaterCloud.entity.monitor.MonitorInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by TECHMAN on 2018/3/6.
 */
@Repository
public interface ServerMonitorDao {

    @Select("select * from ( select * from ahwater_cloud_monitor where uuid= #{uuid} )temp order by create_at desc limit 1")
    MonitorInfo selectOneByUUID(String uuid);

    @Select("select * from ahwater_cloud_monitor where uuid=#{uuid} and  create_at > DATE_SUB(NOW(),INTERVAL #{hours} HOUR) order by create_at asc")
    List<MonitorInfo> selectByUUIDAndHours(@Param("uuid") String uuid,@Param("hours") int hours);
}
