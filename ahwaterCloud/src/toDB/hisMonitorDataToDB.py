#!/usr/bin/python
#-*- coding: UTF-8 -*-

#+++++++++++++++++++++++++++++++++++++++++++++++++++#
#Name        : module.py							#
#Author      : @ruiy								#
#Version     : 1.5									#
#Copyright   : 2016 ~ 2017 ahwater.net Corporation.`#
#Description : new function module template.		#
#+++++++++++++++++++++++++++++++++++++++++++++++++++#

from __future__ import print_function
import sys
import libvirt
import os
import subprocess
from xml.etree import ElementTree
import time
import datetime
import MySQLdb
from subprocess import Popen
from subprocess import PIPE
import commands


#1.)建立libVirt 与云平台每计算节点hypervisor-kvm连接
#conn1-->计算节点001
conn1 = libvirt.open('qemu+ssh://root@ahwater-cloud-compute001/system')
if conn1 == None:
	print('Failed to open connection to qemu+ssh://root@ahwater-cloud-compute001/system',file=sys.stderr)
	exit(1)

#conn2-->计算节点002
conn2 = libvirt.open('qemu+ssh://root@ahwater-cloud-compute002/system')
if conn2 == None:
	printf('Failed to open connection to qemu+ssh://root@ahwater-cloud-compute002/system',file=sys.stderr)
	exit(1)

#数据库连接
db = MySQLdb.connect("localhost","wateruser","pass321","waterdb") 
#使用cursor()方法获取操作游标
cursor = db.cursor()

#2.)监控指标数据采集
#计算节点001
domains001 = conn1.listAllDomains(1)
if len(domains001) != 0:
	for domain001 in domains001:
		#使用libVirt云主机名称得到nova云主机名称
	    #标记当前虚拟机宿主机主机名physcial_hostname
		#print("ahwater-cloud-compute001")
		Monitor0001 = "\'ahwater-cloud-compute001\'"
		#print(Monitor0001)

		#监控数据获取日期时间created_at
		#print(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
		Monitor0002 = "'" + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S') + "'"
		#print(type(Monitor0002))

		#kvm_name
		#print(domain001.name())
		Monitor0003 = "'" + domain001.name() + "'"
		#print(Monitor0003)

	    #kvm_uuid
		#print(domain001.UUIDString())
		Monitor0004 = "'" + domain001.UUIDString() + "'"
		#print(Monitor0004)

		#nova_hostname
		cmd = "virsh -c qemu+ssh://root@ahwater-cloud-compute001/system dumpxml %s | grep \"<nova:name>\"|awk -F\">\" '{print $2}'|awk -F\"<\" '{print $1}'" % domain001.name()
		(status,output) = commands.getstatusoutput("%s" % cmd)
		Monitor0005 = "'" + output + "'"
		#print(output)
		#print(type(output))
		#Monitor0005 = os.system("%s" % cmd + "> /dev/null 2>&1")
		#Monitor0005 = Popen(["%s" % cmd],stdout=PIPE,stderr=PIPE,stdin=PIPE)

		#print(Monitor0005)
		#Monitor0005 = os.system("%s" % cmd) >> serr
		#抑制数据输出
		#print(os.system("%s" % cmd))

		#Monitor0005 = "'" + str(os.system("%s" % cmd)) + "'"
		#print(type(Monitor0005))
		#os.system("%s" % cmd)
		#print(type(os.system("%s" % cmd)))
		#Monitor0005 = "'" + os.system("%s" % cmd) + "'"
		#print(Monitor0005)

		#cpu_usage_rate
		t1 = time.time()
		c1 = int(domain001.info()[4])
		#print(c1)
		time.sleep(1)
		t2 = time.time()
		c2 = int(domain001.info()[4])
		c_nums = int(domain001.info()[3])
		usage = (c2 - c1) * 100 / ((t2 - t1) * c_nums *1e9)
		#print("%f" % usage)
		Monitor0006 = "'" + str(usage) + "'"
		#print(Monitor0006)

		#network_trafic_infos
		tree = ElementTree.fromstring(domain001.XMLDesc())
		ifaces = tree.findall('devices/interface/target')
		#根据openStack环境特点，通常网卡仅有一块,为了适应软件合规友好性，程序写定仅监控第一块网卡
		"""
		for i in ifaces:
			iface = i.get('dev')
			ifaceinfo = domain001.interfaceStats(iface)
			#网络流控信息说明:rx_bytes:kb/s,rx_packets:kb/s,rx_errs:kb/s,rx_drop:kb/s,tx_bytes:kb/s,tx_packets:kb/s,tx_errs:kb/s,tx_drop:kb/s
			print(iface,ifaceinfo[0]/1024,ifaceinfo[1]/1024,ifaceinfo[2]/1024,ifaceinfo[3]/1024,ifaceinfo[4]/1024,ifaceinfo[5]/1024,ifaceinfo[6]/1024,ifaceinfo[7]/1024)
		"""
		i = ifaces[0]
		iface = i.get('dev')
		ifaceinfo = domain001.interfaceStats(iface)
		#print(iface,ifaceinfo[0]/1024,ifaceinfo[1]/1024,ifaceinfo[2]/1024,ifaceinfo[3]/1024,ifaceinfo[4]/1024,ifaceinfo[5]/1024,ifaceinfo[6]/1024,ifaceinfo[7]/1024)
		#print(iface,ifaceinfo)
		Monitor0007 = "'" + iface + "'"
		#print(Monitor0007)
		Monitor0008 = "'" + str(ifaceinfo[0]) + "'"
		Monitor0009 = "'" + str(ifaceinfo[1]) + "'"
		Monitor0010 = "'" + str(ifaceinfo[2]) + "'"
		Monitor0011 = "'" + str(ifaceinfo[3]) + "'"
		Monitor0012 = "'" + str(ifaceinfo[4]) + "'"
		Monitor0013 = "'" + str(ifaceinfo[5]) + "'"
		Monitor0014 = "'" + str(ifaceinfo[6]) + "'"
		Monitor0015 = "'" + str(ifaceinfo[7]) + "'"

		#根据业务共性规定,仅采集第一块硬盘数据
		#disk_size
		tree = ElementTree.fromstring(domain001.XMLDesc())
		devices = tree.findall('devices/disk/target')
		#仅获取第一块硬盘,系统盘数据
		d = devices[0]
		device = d.get('dev')
		try:
			devinfo = domain001.blockInfo(device)
		except libvirt.libvirtError:
			pass
		#print(device,devinfo[0]/1024/1024/1024,devinfo[1]/1024,devinfo[2]/1024)
		#print(device,devinfo)
		Monitor0016 = "'" + device + "'"
		Monitor0017 = "'" + str(devinfo[0]) + "'"
		Monitor0018 = "'" + str(devinfo[1]) + "'"
		Monitor0019 = "'" + str(devinfo[2]) + "'"
		"""
		for d in devices:
			device = d.get('dev')
			try:
				devinfo = domain001.blockInfo(device)
			except libvirt.libvirtError:
				pass
			#磁盘大小信息格式说明:capacity:Gb,allocation:kb,physical:kb
			print(device,devinfo[0]/1024/1024/1024,devinfo[1]/1024,devinfo[2]/1024)
		"""
		#disk_iops
		tree = ElementTree.fromstring(domain001.XMLDesc())	
		devices = tree.findall('devices/disk/target')
		#获取第一块硬盘,系统盘数据
		d = devices[0]
		device = d.get('dev')
		try:
			devstats = domain001.blockStats(device)
			#print(device,devstats)
			Monitor0020 = "'" + str(devstats[0]) + "'"
			Monitor0021 = "'" + str(devstats[1]) + "'"
			Monitor0022 = "'" + str(devstats[2]) + "'"
			Monitor0023 = "'" + str(devstats[3]) + "'"
			Monitor0024 = "'" + str(devstats[4]) + "'"
		except libvirt.libvirtError:
			pass
		"""
		for d in devices:
			device = d.get('dev')	
			try:
				devstats = domain001.blockStats(device)
				#磁盘I/O返回说明:read_bytes:kb/s,read_requests:kb/s,write_bytes:kb/s,write_requests:kb/s,errors:kb/s
				#print(device,devstats[0]/1024,devstats[1]/1024,devstats[2]/1024,devstats[3]/1024,devstats[4]/1024)
				print(device,devstats)
			except libvirt.libvirtError:
				pass
		"""

		#mem_infos
		domain001.setMemoryStatsPeriod(10)
		meminfo = domain001.memoryStats()
		#数据返回格式说明,添加memballon插件数据返回'swap_out', 'available=>虚拟机设别出的最大内存', 'actual=>设置的最大内存', 'major_fault', 'swap_in', 'unused=>未使用内存量', 'minor_fault', 'rss=>qemu process在宿主机所占内存'
						 #虚拟机实例未添加memballon插件数据返回'actual=>设置的最大内存','rss=>qemu process在宿主机所占内存'
		#根据业务共性规定,返回值为:'actual=>设置的最大内存','rss=>qemu process在宿主机所占内存','available=>虚拟机设别出的最大内存','unused=>未使用内存量'
		#插入数据时根据判断meminfo 字典长度 len = 2则没设置memballon,返回actual/rss字段, or len = 8说明本虚拟机实例设置了memballon,返回actual/rss/available/unused

		if len(meminfo) == 2:
			#print(meminfo['actual'],meminfo['rss'])
			Monitor0025 = "'" + str(meminfo['actual']) + "'"
			Monitor0026 = "'" + str(meminfo['rss']) + "'"
		elif len(meminfo) == 8:
			#print(meminfo['actual'],meminfo['rss'],meminfo['available'],meminfo['unused'])
			Monitor0025 = "'" + str(meminfo['actual']) + "'"
			Monitor0026 = "'" + str(meminfo['rss']) + "'"
			Monitor0027 = "'" + str(meminfo['available']) + "'"
			Monitor0028 = "'" + str(meminfo['unused']) + "'"

			
		#print(len(meminfo))
		#采集监控数据入库
		if len(meminfo) == 2:
			#插入云主机无memballon监控sql语句
			sql = """insert into ahwater_cloud_monitor(
			physcial_hostname,
			create_at,
			kvm_name,
			uuid,
			cloud_hostname,
			vcpu_usage_rate,
			vnet_name,
			vnet_flow_rx_bytes,
			vnet_flow_rx_packets,
			vnet_flow_rx_errs,
			vnet_flow_rx_drop,
			vnet_flow_tx_bytes,
			vnet_flow_tx_packets,
			vnet_flow_tx_errs,
			vnet_flow_tx_drop,
			vdisk_name,
			vdisk_capacity_size,
			vdisk_allocation_size,
			vdisk_physical_size,
			vdisk_read_bytes,
			vdisk_read_requests,
			vdisk_write_bytes,
			vdisk_write_requests,
			vdisk_errors,
			vmem_actual_size,
			vmem_rss_size
			) values(
				%(physcial_hostname)s,
				%(create_at)s,
				%(kvm_name)s,
				%(uuid)s,
				%(cloud_hostname)s,
				%(vcpu_usage_rate)s,
				%(vnet_name)s,
				%(vnet_flow_rx_bytes)s,
				%(vnet_flow_rx_packets)s,
				%(vnet_flow_rx_errs)s,
				%(vnet_flow_rx_drop)s,
				%(vnet_flow_tx_bytes)s,
				%(vnet_flow_tx_packets)s,
				%(vnet_flow_tx_errs)s,
				%(vnet_flow_tx_drop)s,
				%(vdisk_name)s,
				%(vdisk_capacity_size)s,
				%(vdisk_allocation_size)s,
				%(vdisk_physical_size)s,
				%(vdisk_read_bytes)s,
				%(vdisk_read_requests)s,
				%(vdisk_write_bytes)s,
				%(vdisk_write_requests)s,
				%(vdisk_errors)s,
				%(vmem_actual_size)s,
				%(vmem_rss_size)s
				)
			"""
		elif len(meminfo) == 8:
			#插入云主机有memballon监控sql语句
			sql = """insert into ahwater_cloud_monitor values(
			'null',
			%(physcial_hostname)s,
			%(create_at)s,
			%(kvm_name)s,
			%(uuid)s,
			%(cloud_hostname)s,
			%(vcpu_usage_rate)s,
			%(vnet_name)s,
			%(vnet_flow_rx_bytes)s,
			%(vnet_flow_rx_packets)s,
			%(vnet_flow_rx_errs)s,
			%(vnet_flow_rx_drop)s,
			%(vnet_flow_tx_bytes)s,
			%(vnet_flow_tx_packets)s,
			%(vnet_flow_tx_errs)s,
			%(vnet_flow_tx_drop)s,
			%(vdisk_name)s,
			%(vdisk_capacity_size)s,
			%(vdisk_allocation_size)s,
			%(vdisk_physical_size)s,
			%(vdisk_read_bytes)s,
			%(vdisk_read_requests)s,
			%(vdisk_write_bytes)s,
			%(vdisk_write_requests)s,
			%(vdisk_errors)s,
			%(vmem_actual_size)s,
			%(vmem_rss_size)s,
			%(vmem_available_size)s,
			%(vmem_unused_size)s
			)
			"""
		#print(len(meminfo))
		try:
			if len(meminfo) == 2:
				cursor.execute(sql % dict(
							physcial_hostname = Monitor0001,
							create_at = Monitor0002,
							kvm_name = Monitor0003,
							uuid = Monitor0004,
							cloud_hostname = Monitor0005,
							vcpu_usage_rate = Monitor0006,
							vnet_name = Monitor0007,
							vnet_flow_rx_bytes = Monitor0008,
							vnet_flow_rx_packets = Monitor0009,
							vnet_flow_rx_errs = Monitor0010,
							vnet_flow_rx_drop = Monitor0011,
							vnet_flow_tx_bytes = Monitor0012,
							vnet_flow_tx_packets = Monitor0013,
							vnet_flow_tx_errs = Monitor0014,
							vnet_flow_tx_drop = Monitor0015,
							vdisk_name = Monitor0016,
							vdisk_capacity_size = Monitor0017,
							vdisk_allocation_size = Monitor0018,
							vdisk_physical_size = Monitor0019,
							vdisk_read_bytes = Monitor0020,
							vdisk_read_requests = Monitor0021,
							vdisk_write_bytes = Monitor0022,
							vdisk_write_requests = Monitor0023,
							vdisk_errors = Monitor0024,
							vmem_actual_size = Monitor0025,
							vmem_rss_size = Monitor0026
							))
			elif len(meminfo) == 8:
				 cursor.execute(sql % dict(
							"",
							physcial_hostname = Monitor0001,
							create_at = Monitor0002,
							kvm_name = Monitor0003,
							uuid = Monitor0004,
							cloud_hostname = Monitor0005,
							vcpu_usage_rate = Monitor0006,
							vnet_name = Monitor0007,
							vnet_flow_rx_bytes = Monitor0008,
							vnet_flow_rx_packets = Monitor0009,
							vnet_flow_rx_errs = Monitor0010,
							vnet_flow_rx_drop = Monitor0011,
							vnet_flow_tx_bytes = Monitor0012,
							vnet_flow_tx_packets = Monitor0013,
							vnet_flow_tx_errs = Monitor0014,
							vnet_flow_tx_drop = Monitor0015,
							vdisk_name = Monitor0016,
							vdisk_capacity_size = Monitor0017,
							vdisk_allocation_size = Monitor0018,
							vdisk_physical_size = Monitor0019,
							vdisk_read_bytes = Monitor0020,
							vdisk_read_requests = Monitor0021,
							vdisk_write_bytes = Monitor0022,
							vdisk_write_requests = Monitor0023,
							vdisk_errors = Monitor0024,
							vmem_actual_size = Monitor0025,
							vmem_rss_size = Monitor0026,
							vmem_available_size = Monitor0027,
							vmem_unused_size = Monitor0028
							 ))
			#执行sql语句
			
			#提交到数据库
			db.commit()
		except:
			#Rollback in case there any error
			db.rollback()

		#print(meminfo)

		#每条监控数据空格分隔,调试用
		#print()

else:
	print('None')

#计算节点002
domains002 = conn2.listAllDomains(1)
if len(domains002) != 0:
	for domain002 in domains002:
		#print("ahwater-cloud-compute002")
		Monitor0001 = "\'ahwater-cloud-compute002\'"
		#print(Monitor0001)
		#print(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
		Monitor0002 = "'" + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S') + "'"
		#print(domain002.name())
		Monitor0003 = "'" + domain002.name() + "'"
		#print(domain002.UUIDString())
		Monitor0004 = "'" + domain002.UUIDString() + "'"
		
		cmd = "virsh -c qemu+ssh://root@ahwater-cloud-compute002/system dumpxml %s | grep \"<nova:name>\"|awk -F\">\" '{print $2}'|awk -F\"<\" '{print $1}'" % domain002.name()
		(status,output) = commands.getstatusoutput("%s" % cmd)
		Monitor0005 = "'" + output + "'"
		#os.system("%s" % cmd)
		#cpu_usage_rate
		t1 = time.time()
		c1 = int(domain002.info()[4])
		time.sleep(1)
		t2 = time.time()
		c2 = int(domain002.info()[4])
		c_nums = int(domain002.info()[3])
		#usage = (c2 - c1) * 100 / ((t2 - t1) * c_nums * 1e9)
		usage = (c2 - c1) * 100 / ((t2 - t1) * c_nums *1e9)
		#print("%f" % usage)
		Monitor0006 = "'" + str(usage) + "'"
		#network_trafic_infos
		tree = ElementTree.fromstring(domain002.XMLDesc())
		ifaces = tree.findall('devices/interface/target')
		i = ifaces[0]
		iface = i.get('dev')
		ifaceinfo = domain002.interfaceStats(iface)
		#print(iface,ifaceinfo[0]/1024,ifaceinfo[1]/1024,ifaceinfo[2]/1024,ifaceinfo[3]/1024,ifaceinfo[4]/1024,ifaceinfo[4]/1024,ifaceinfo[5]/1024,ifaceinfo[6]/1024,ifaceinfo[7]/1024)
		#print(iface,ifaceinfo)
		Monitor0007 = "'" + iface + "'"
		Monitor0008 = "'" + str(ifaceinfo[0]) + "'"
		Monitor0009 = "'" + str(ifaceinfo[1]) + "'"
		Monitor0010 = "'" + str(ifaceinfo[2]) + "'"
		Monitor0011 = "'" + str(ifaceinfo[3]) + "'"
		Monitor0012 = "'" + str(ifaceinfo[4]) + "'"
		Monitor0013 = "'" + str(ifaceinfo[5]) + "'"
		Monitor0014 = "'" + str(ifaceinfo[6]) + "'"
		Monitor0015 = "'" + str(ifaceinfo[7]) + "'"

		#disk_size
		tree = ElementTree.fromstring(domain002.XMLDesc())
		devices = tree.findall('devices/disk/target')
		d = devices[0]
		device = d.get('dev')
		try:
			devinfo = domain002.blockInfo(device)
		except libvirt.libvirtError:
			pass
		#print(device,devinfo[0]/1024/1024/1024,devinfo[1]/1024,devinfo[2]/1024)
		#print(device,devinfo)
		Monitor0016 = "'" + device + "'"
		Monitor0017 = "'" + str(devinfo[0]) + "'"
		Monitor0018 = "'" + str(devinfo[1]) + "'"
		Monitor0019 = "'" + str(devinfo[2]) + "'"

		#disk_iops
		tree = ElementTree.fromstring(domain002.XMLDesc())
		devices = tree.findall('devices/disk/target')
		d = devices[0]
		device = d.get('dev')
		try:
			devstats = domain002.blockStats(device)
			#print(device,devstats)
			Monitor0020 = "'" + str(devstats[0]) + "'"
			Monitor0021 = "'" + str(devstats[1]) + "'"
			Monitor0022 = "'" + str(devstats[2]) + "'"
			Monitor0023 = "'" + str(devstats[3]) + "'"
			Monitor0024 = "'" + str(devstats[4]) + "'"
		except libvirt.libvirtError:
			pass

		#mem_infos
		domain002.setMemoryStatsPeriod(10)
		meminfo = domain002.memoryStats()
		if len(meminfo) == 2:
			#print(meminfo['actual'],meminfo['rss'])
			Monitor0025 = "'" + str(meminfo['actual']) + "'"
			Monitor0026 = "'" + str(meminfo['rss']) + "'"
		elif len(meminfo) == 8:
			#print(meminfo['actual'],meminfo['rss'],meminfo['available'],meminfo['unused'])
			Monitor0025 = "'" + str(meminfo['actual']) + "'"
			Monitor0026 = "'" + str(meminfo['rss']) + "'"
			Monitor0027 = "'" + str(meminfo['available']) + "'"
			Monitor0028 = "'" + str(meminfo['unused']) + "'"
		#分割每个Instance 监控数据在StdOut中输出
		#print()

		#采集监控数据入库
		if len(meminfo) == 2:
			#插入云主机无memballon监控sql语句
			sql = """insert into ahwater_cloud_monitor(
			physcial_hostname,
			create_at,
			kvm_name,
			uuid,
			cloud_hostname,
			vcpu_usage_rate,
			vnet_name,
			vnet_flow_rx_bytes,
			vnet_flow_rx_packets,
			vnet_flow_rx_errs,
			vnet_flow_rx_drop,
			vnet_flow_tx_bytes,
			vnet_flow_tx_packets,
			vnet_flow_tx_errs,
			vnet_flow_tx_drop,
			vdisk_name,
			vdisk_capacity_size,
			vdisk_allocation_size,
			vdisk_physical_size,
			vdisk_read_bytes,
			vdisk_read_requests,
			vdisk_write_bytes,
			vdisk_write_requests,
			vdisk_errors,
			vmem_actual_size,
			vmem_rss_size
			) values(
				%(physcial_hostname)s,
				%(create_at)s,
				%(kvm_name)s,
				%(uuid)s,
				%(cloud_hostname)s,
				%(vcpu_usage_rate)s,
				%(vnet_name)s,
				%(vnet_flow_rx_bytes)s,
				%(vnet_flow_rx_packets)s,
				%(vnet_flow_rx_errs)s,
				%(vnet_flow_rx_drop)s,
				%(vnet_flow_tx_bytes)s,
				%(vnet_flow_tx_packets)s,
				%(vnet_flow_tx_errs)s,
				%(vnet_flow_tx_drop)s,
				%(vdisk_name)s,
				%(vdisk_capacity_size)s,
				%(vdisk_allocation_size)s,
				%(vdisk_physical_size)s,
				%(vdisk_read_bytes)s,
				%(vdisk_read_requests)s,
				%(vdisk_write_bytes)s,
				%(vdisk_write_requests)s,
				%(vdisk_errors)s,
				%(vmem_actual_size)s,
				%(vmem_rss_size)s
				)
			"""
		elif len(meminfo) == 8:
			#插入云主机有memballon监控sql语句
			sql = """insert into ahwater_cloud_monitor values(
			"",
			%(physcial_hostname)s,
			%(create_at)s,
			%(kvm_name)s,
			%(uuid)s,
			%(cloud_hostname)s,
			%(vcpu_usage_rate)s,
			%(vnet_name)s,
			%(vnet_flow_rx_bytes)s,
			%(vnet_flow_rx_packets)s,
			%(vnet_flow_rx_errs)s,
			%(vnet_flow_rx_drop)s,
			%(vnet_flow_tx_bytes)s,
			%(vnet_flow_tx_packets)s,
			%(vnet_flow_tx_errs)s,
			%(vnet_flow_tx_drop)s,
			%(vdisk_name)s,
			%(vdisk_capacity_size)s,
			%(vdisk_allocation_size)s,
			%(vdisk_physical_size)s,
			%(vdisk_read_bytes)s,
			%(vdisk_read_requests)s,
			%(vdisk_write_bytes)s,
			%(vdisk_write_requests)s,
			%(vdisk_errors)s,
			%(vmem_actual_size)s,
			%(vmem_rss_size)s,
			%(vmem_available_size)s,
			%(vmem_unused_size)s
			)
			"""
		try:
			if len(meminfo) == 2:
				cursor.execute(sql % dict(
							physcial_hostname = Monitor0001,
							create_at = Monitor0002,
							kvm_name = Monitor0003,
							uuid = Monitor0004,
							cloud_hostname = Monitor0005,
							vcpu_usage_rate = Monitor0006,
							vnet_name = Monitor0007,
							vnet_flow_rx_bytes = Monitor0008,
							vnet_flow_rx_packets = Monitor0009,
							vnet_flow_rx_errs = Monitor0010,
							vnet_flow_rx_drop = Monitor0011,
							vnet_flow_tx_bytes = Monitor0012,
							vnet_flow_tx_packets = Monitor0013,
							vnet_flow_tx_errs = Monitor0014,
							vnet_flow_tx_drop = Monitor0015,
							vdisk_name = Monitor0016,
							vdisk_capacity_size = Monitor0017,
							vdisk_allocation_size = Monitor0018,
							vdisk_physical_size = Monitor0019,
							vdisk_read_bytes = Monitor0020,
							vdisk_read_requests = Monitor0021,
							vdisk_write_bytes = Monitor0022,
							vdisk_write_requests = Monitor0023,
							vdisk_errors = Monitor0024,
							vmem_actual_size = Monitor0025,
							vmem_rss_size = Monitor0026
							))
			elif len(meminfo) == 8:
				cursor.execute(sql % dict(
						physcial_hostname = Monitor0001,
						create_at = Monitor0002,
						kvm_name = Monitor0003,
						uuid = Monitor0004,
						cloud_hostname = Monitor0005,
						vcpu_usage_rate = Monitor0006,
						vnet_name = Monitor0007,
						vnet_flow_rx_bytes = Monitor0008,
						vnet_flow_rx_packets = Monitor0009,
						vnet_flow_rx_errs = Monitor0010,
						vnet_flow_rx_drop = Monitor0011,
						vnet_flow_tx_bytes = Monitor0012,
						vnet_flow_tx_packets = Monitor0013,
						vnet_flow_tx_errs = Monitor0014,
						vnet_flow_tx_drop = Monitor0015,
						vdisk_name = Monitor0016,
						vdisk_capacity_size = Monitor0017,
						vdisk_allocation_size = Monitor0018,
						vdisk_physical_size = Monitor0019,
						vdisk_read_bytes = Monitor0020,
						vdisk_read_requests = Monitor0021,
						vdisk_write_bytes = Monitor0022,
						vdisk_write_requests = Monitor0023,
						vdisk_errors = Monitor0024,
						vmem_actual_size = Monitor0025,
						vmem_rss_size = Monitor0026,
						vmem_available_size = Monitor0027,
						vmem_unused_size = Monitor0028
						))
			db.commit()
		except:
			db.rollback()

else:
	print(' None')

#关闭libvirt连接
conn1.close()
conn2.close()
#关闭数据库连接
db.close()
exit(0)
