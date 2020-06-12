package org.mintleaf.modules.video.dao;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import org.mintleaf.modules.video.entity.StShipinB;

@Component
@SqlResource("stShipinB")
public interface StShipinBDao extends BaseMapper<StShipinB> {

    public int deleteSample(@Param("videoId") String videoId);

}
