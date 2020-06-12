package net.ahwater.ahwaterCloud.service.monitor;

import net.ahwater.ahwaterCloud.dao.ServerMonitorDao;
import net.ahwater.ahwaterCloud.entity.monitor.MonitorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;


/**
 * 监控类
 *
 * @author gwh
 */
@Service
public class ServerMonitor {

    @Autowired
    private ServerMonitorDao serverMonitorDao;

    /**
     * 获取云主机列表的实时监控信息
     *
     * @param uuidList
     * @return
     */
    public List<MonitorInfo> queryNewServerMonitorInfo(List<String> uuidList) {
        List<MonitorInfo> lm = new LinkedList<>();
        for (String uuid : uuidList) {
            lm.add(serverMonitorDao.selectOneByUUID(uuid));
        }
        return lm;
    }


    /**
     * 查询特定云主机的监控信息;按日期递增顺序
     *
     * @param uuid
     * @param hours
     * @return
     */
    public List<MonitorInfo> queryOneServerMonitorInfo(String uuid, int hours) {
        return serverMonitorDao.selectByUUIDAndHours(uuid, hours);
    }

}
