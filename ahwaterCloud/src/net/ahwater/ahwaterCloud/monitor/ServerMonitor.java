package net.ahwater.ahwaterCloud.monitor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import net.ahwater.ahwaterCloud.monitor.entity.MonitorInfo;
import net.ahwater.ahwaterCloud.util.JdbcUtil;

/**
 * 监控类
 * @author gwh
 *
 */
public class ServerMonitor {
	
	/**
	 * 获取云主机列表的实时监控信息
	 * @param uuidList
	 * @return
	 * @throws SQLException
	 */
	public List<MonitorInfo> queryNewServerMonitorInfo(List<String> uuidList) throws SQLException{
		Connection conn = JdbcUtil.getConnection();
		String sql="select * from ( select * from ahwater_cloud_monitor where uuid= ? )temp order by create_at desc limit 1";
		List<MonitorInfo> lm=new ArrayList<>();
		PreparedStatement pstmt=conn.prepareStatement(sql);
		ResultSet rs=null;
		for(String uuid:uuidList){
			pstmt.setString(1,uuid);
			rs=pstmt.executeQuery(); 
			while (rs.next()){
				MonitorInfo m=new MonitorInfo();
				m.setUuid(rs.getString(5));
				m.setCloud_hostname(rs.getString(6));
				m.setCreate_at(rs.getString(3));
				m.setVcpu_usage_rate(rs.getDouble(7));
				m.setVmem_rss_size(rs.getLong(27));
				m.setVdisk_capacity_size(rs.getLong(18));
				m.setVdisk_allocation_size(rs.getLong(19));
				m.setVdisk_read_bytes(rs.getLong(21));
				m.setVdisk_write_bytes(rs.getInt(23));
				m.setVnet_flow_rx_bytes(rs.getLong(9));
				m.setVnet_flow_tx_bytes(rs.getLong(13));
				lm.add(m);
			}
		}
		JdbcUtil.close(conn, pstmt, rs);
		return lm;
	}
	

	/**
	 * 查询特定云主机的监控信息;按日期递增顺序
	 * @param uuid
	 * @return
	 * @throws SQLException
	 */
    public List<MonitorInfo> queryOneServerMonitorInfo(String uuid,int hours) throws SQLException {
    	Connection conn = JdbcUtil.getConnection();
		String sql="select * from ahwater_cloud_monitor where uuid=? and  create_at > DATE_SUB(NOW(),INTERVAL ? HOUR) order by create_at asc"; 
		PreparedStatement pstmt=conn.prepareStatement(sql); 
		pstmt.setString(1,uuid); 
		pstmt.setInt(2, hours);
		ResultSet rs=pstmt.executeQuery(); 
		
		List<MonitorInfo> lm=new ArrayList<>();
		
		while (rs.next()){
			MonitorInfo m=new MonitorInfo();
			m.setUuid(rs.getString(5));
			m.setCloud_hostname(rs.getString(6));
			m.setCreate_at(rs.getString(3));
			m.setVcpu_usage_rate(rs.getDouble(7));
			m.setVmem_rss_size(rs.getLong(27));
			m.setVdisk_capacity_size(rs.getLong(18));
			m.setVdisk_allocation_size(rs.getLong(19));
			m.setVdisk_read_bytes(rs.getLong(21));
			m.setVdisk_write_bytes(rs.getInt(23));
			m.setVnet_flow_rx_bytes(rs.getLong(9));
			m.setVnet_flow_tx_bytes(rs.getLong(13));
			lm.add(m);
		}
		
		JdbcUtil.close(conn, pstmt, rs);
		return lm;
	}
    
}
