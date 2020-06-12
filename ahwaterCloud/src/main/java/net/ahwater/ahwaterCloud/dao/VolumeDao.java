package net.ahwater.ahwaterCloud.dao;

import net.ahwater.ahwaterCloud.entity.compute.AllServerListEle;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by TECHMAN on 2018/3/5.
 */
@Repository
public interface VolumeDao {

    @Select("select * from ahwater_cloud_instances where tenanantName=#{tenanantName}")
    List<AllServerListEle> selectByTenanantName(@Param("tenanantName") String tenantName);
}
