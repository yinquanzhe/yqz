#!/usr/bin/python
#-*- coding: UTF-8 -*-

#+++++++++++++++++++++++++++++++++++++++++++++++++++#
#Name        : tempDataToDB.py						#
#Author      : @ruiy								#
#Version     : 1.5									#
#Copyright   : 2016 ~ 2017 ahwater.net Corporation.`#
#Description : cloud host,disk data temp to DB.		#
#+++++++++++++++++++++++++++++++++++++++++++++++++++#

import os
import sys
import json
import commands
import MySQLdb
reload(sys)
sys.setdefaultencoding( "utf-8" )

#数据库连接
db = MySQLdb.connect("localhost","wateruser","pass321","waterdb",charset='utf8')
#使用cursor()方法获取操作游标
cursor = db.cursor()

#插入前清空ahwater_cloud_instances
sql = """delete from ahwater_cloud_disks;"""
try:
	cursor.execute(sql)
	db.commit()
except:
	db.rollback()

#print(sys.getfilesystemencoding())
#print(sys.getdefaultencoding())

#curl模拟登陆沃特云管理后台 http://ahwater-cloud-controller/ahwaterCloud/ctr/LoginProcess
os.system('curl -c ck.txt --user-agent Mozilla/4.0 -d "username=admin&userpwd=ahwater&server_id=1&submit=true" http://ahwater-cloud-controller/ahwaterCloud/ctr/LoginProcess > /dev/null 2>&1')
#获取云磁盘
cmd='curl -c ck.txt -b ck.txt --user-agent Mozilla/4.0 http://ahwater-cloud-controller/ahwaterCloud/cont/adminListAllVolumes?tenantId=6a98f9d2278d40cb8f58bf348db66ecc | python -m json.tool > ./cloudDiskList.json'
(status,output) = commands.getstatusoutput("%s" % cmd)

#os.system('cat ./cloudHostList.json | awk -F"disk" \'/disk/{print $0}\'')

#获取数据条数
cmd='lens=`cat ./cloudDiskList.json |awk -F"volumeName" \'/volumeName/{print $0}\' | wc -l`;echo $lens'
(status,output) = commands.getstatusoutput("%s" % cmd)
lens=int(output)
#print(lens)

#读取处理cloudHostList.json文件
fs=open('./cloudDiskList.json')
jsobj=json.load(fs)
for i in range(lens):
	if str(jsobj[i]['volumeAttachment']) == "None":
		tenantNames = "'" + jsobj[i]['tenantName'] + "'";
		#print(tenantNames)
		volumeNames = "'" + jsobj[i]['volumeName'] + "'";
		#serverIds = "'" 'None';
		#serverNames = 'None';
		#print(serverName)
		volumeSizes = "'" + str(jsobj[i]['volumeSize']) + "'";
		volumeStatuss = "'" + jsobj[i]['volumeStatus'] + "'";
		
		sql = """insert into ahwater_cloud_disks(
		tenantName,
		volumeName,
		volumeSize,
		volumeStatus
		) values(
			%(tenantName)s,
			%(volumeName)s,
			%(volumeSize)s,
			%(volumeStatus)s
			)
			"""
		#print(sql)
#try:
		try:
			cursor.execute(sql % dict(
					tenantName = tenantNames,
					volumeName = volumeNames,
					volumeSize = volumeSizes,
					volumeStatus = volumeStatuss
					))
			db.commit()
		except:
			db.rollback()
			
	elif str(jsobj[i]['volumeAttachment']) != "None":
		tenantNames = "'" + jsobj[i]['tenantName'] + "'";
		volumeNames = "'" + jsobj[i]['volumeName'] + "'";
		serverIds = "'" + jsobj[i]['volumeAttachment']['serverId'] + "'";
		serverNames = "'" + jsobj[i]['volumeAttachment']['serverName'] + "'";
		volumeSizes = "'" + str(jsobj[i]['volumeSize']) + "'";
		volumeStatuss = "'" + jsobj[i]['volumeStatus'] + "'";
		sql = """insert into ahwater_cloud_disks(
		tenantName,
		volumeName,
		serverId,
		serverName,
		volumeSize,
		volumeStatus
		) values(
			%(tenantName)s,
			%(volumeName)s,
			%(serverId)s,
			%(serverName)s,
			%(volumeSize)s,
			%(volumeStatus)s
			)
			"""

		try:
			cursor.execute(sql % dict(
					tenantName = tenantNames,
					volumeName = volumeNames,
					serverId = serverIds,
					serverName = serverNames,
					volumeSize = volumeSizes,
					volumeStatus = volumeStatuss
					))
			db.commit()
		except:
			db.rollback()
#if str(jsobj[1]['volumeAttachment']) == "None":
#	print("NoT!!")
#else:
#	print("True!")
#print(jsobj[0]['volumeAttachment'])
#print(type(jsobj[1]['volumeAttachment']))
#print(jsobj[2]['volumeAttachment'])
#print jsobj[1].has_key('volumeAttachment')
#print(jsobj)
#print(jsobj[1])
#for i in range(lens):
#	if jsobj[i].has_key('volumeAttachment') == "True":
#		print("Ok")
	#print(jsobj[i]['volumeAttachment']['serverId'])
#if jsobj[i]['volumeAttachment'] != '':
#		print("True")
#	else:
#		print("False")
#print(jsobj[3])
#print(jsobj[1][0].keys())

#for i in range(lens):
#tenantName = "'" + jsobj[i]['tenantName'] + "'"
#	volumeName = "'" + jsobj[i]['volumeName'] + "'"
#	volumeSize = "'" + str(jsobj[i]['volumeSize']) + "'"
#	volumeStatus = "'" + jsobj[i]['volumeStatus'] + "'"
#print(jsobj[i])	

#print(jsobj[i]['tenanantName'])
#print(jsobj[37]['tenanantName'])
#print(lens)

#for i in range(int(lens)):
	#tenanantName
	#cmd='cat ./cloudHostList.json | awk -F"tenanantName" \'/tenanantName/{print $0}\''
	#cmd = 'cat ./cloudHostList.json | awk -F"tenanantName" \'/tenanantName/{print $0}\' | awk -F":" \'{print $2}\''
	#os.system('cat ./cloudHostList.json | awk -F"tenanantName" \'/tenanantName/{print $0}\' | awk -F":" \'{print $2}\' >./tmp.txt')
#cmd='for i in `cat ./cloudHostList.json | awk -F"tenanantName" \'/tenanantName/{print $0}\' | awk -F":" \'{print $0}\'|awk -F":" \'{print $2}\'`;do echo $i exit;done'
#(status,output) = commands.getstatusoutput("%s" % cmd)
#print(output)

	#for i in range(lens):
		#os.system('cat ./tmp.txt | cut -c "$i"')
		#cmd='array[$i] = cat ./tmp.txt | cut -c"$i"'
		#(status,output) = commands.getstatusoutput("%s" % cmd)
		#print(output)
		

	#(status,output) = commands.getstatusoutput("%s" % cmd)

	#for i in range(lens):
		#os.system()
	#list = [output]
	#print(type(list))
	#print(list[0])
	#print('\n')
	#print(output)
	#for i in range(int(lens)):
		#da = output
		#print(da)





#db1 = open("./cloudHostList.json")
#db_1 = json.load(db1)
#print(db_1.values())

#print(output.keys())

#print(type(output))
#print(output)
#d=output.encode('utf-8')
#print(d)
#print(output)
#print(type(output))
#data=json.loads(output)
#print(output)
#arr=output.split('tenanantName')
#print arr
#print(output)
#print(type(output))
#print(type(output))
#print(output.split(','))
#print('\n')


#基于cookies获取整个云平台临时-'云磁盘列表'数据
#os.system('curl -c ck.txt -b ck.txt --user-agent Mozilla/4.0 http://ahwater-cloud-controller/ahwaterCloud/cont/adminListAllVolumes?tenantId=6a98f9d2278d40cb8f58bf348db66ecc | python -m json.tool')
