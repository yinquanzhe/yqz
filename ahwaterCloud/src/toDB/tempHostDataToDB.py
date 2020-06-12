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
sql = """delete from ahwater_cloud_instances;"""
try:
	cursor.execute(sql)
	db.commit()
except:
	db.rollback()

#print(sys.getfilesystemencoding())
#print(sys.getdefaultencoding())

#curl模拟登陆沃特云管理后台 http://ahwater-cloud-controller/ahwaterCloud/ctr/LoginProcess
os.system('curl -c ck.txt --user-agent Mozilla/4.0 -d "username=admin&userpwd=ahwater&server_id=1&submit=true" http://ahwater-cloud-controller/ahwaterCloud/ctr/LoginProcess > /dev/null 2>&1')
#基于cookies获取整个云平台临时-'云主机列表'数据
#os.system('cloudHostList=`curl -c ck.txt -b ck.txt --user-agent Mozilla/4.0 http://ahwater-cloud-controller/ahwaterCloud/ctr/listAllTenantServers`')
#os.system('a=`curl -c ck.txt -b ck.txt --user-agent Mozilla/4.0 http://ahwater-cloud-controller/ahwaterCloud/ctr/listAllTenantServers | python -m json.tool`')
cmd='curl -c ck.txt -b ck.txt --user-agent Mozilla/4.0 http://ahwater-cloud-controller/ahwaterCloud/ctr/listAllTenantServers | python -m json.tool > ./cloudHostList.json'
(status,output) = commands.getstatusoutput("%s" % cmd)

#os.system('cat ./cloudHostList.json | awk -F"disk" \'/disk/{print $0}\'')

#获取数据条数
cmd='lens=`cat ./cloudHostList.json |awk -F"tenanantName" \'/tenanantName/{print $0}\' | wc -l`;echo $lens'
(status,output) = commands.getstatusoutput("%s" % cmd)
lens=int(output)

#读取处理cloudHostList.json文件
fs=open('./cloudHostList.json')
jsobj=json.load(fs)
#print(jsobj[0])

for i in range(lens):
	tenanantName="'" + jsobj[i]['tenanantName'] + "'"
	host="'" + jsobj[i]['host'] + "'"
	serverId="'" + jsobj[i]['serverId'] + "'"
	serverName="'" + jsobj[i]['serverName'] + "'"
	imageName="'" + jsobj[i]['imageName'] + "'"
	imagePersonalName="'" + jsobj[i]['imagePersonalName'] + "'"
	imageOStype="'" + jsobj[i]['imageOStype'] + "'"
	imageOSVersion="'" + jsobj[i]['imageOSVersion'] + "'"
	#imageOSBit=jsobj[i]['imageOSBit']
	imageOSBit="'" + str(64) + "'"
	ipAddr="'" + jsobj[i]['ipAddr'][0] + "'"
	vcpus="'" + str(jsobj[i]['vcpus']) + "'"
	ram="'" + str(jsobj[i]['ram']) + "'"
	disk="'" + str(jsobj[i]['disk']) + "'"
	status="'" + jsobj[i]['status'] + "'"
	timeFromCreated="'" + jsobj[i]['timeFromCreated'] + "'"

	sql = """insert into ahwater_cloud_instances(tenanantName,
												host,
												serverId,
												serverName,
												imageName,
												imagePersonalName,
												imageOStype,
												imageOSVersion,
												imageOSBit,
												ipAddr,
												vcpus,
												ram,
												disk,
												status,
												timeFromCreated) values(
													%(tenanantName)s,
													%(host)s,
													%(serverId)s,
													%(serverName)s,
													%(imageName)s,
													%(imagePersonalName)s,
													%(imageOStype)s,
													%(imageOSVersion)s,
													%(imageOSBit)s,
													%(ipAddr)s,
													%(vcpus)s,
													%(ram)s,
													%(disk)s,
													%(status)s,
													%(timeFromCreated)s
													)"""
	try:
		#执行sql语句
		cursor.execute(sql % dict(
				   tenanantName = tenanantName,
				   host = host,
				   serverId = serverId,
				   serverName = serverName,
				   imageName = imageName,
				   imagePersonalName = imagePersonalName,
				   imageOStype = imageOStype,
				   imageOSVersion = imageOSVersion,
				   imageOSBit = imageOSBit,
				   ipAddr = ipAddr,
				   vcpus = vcpus,
				   ram = ram,
				   disk = disk,
				   status = status,
				   timeFromCreated = timeFromCreated
				   ))
		#提交到数据库执行
		db.commit()
		# Rollback in case there is any error
	except:
		db.rollback()
#关闭数据库连接
db.close()
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
