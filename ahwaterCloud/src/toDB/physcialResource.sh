#!/bin/bash
#+++++++++++++++++++++++++++++++++++++++++++++++++++#
#Name        : physcialResource.sh                  #
#Author      : @ruiy                                #
#Version     : 2.0                                  #
#Copyright   : 2016 ~ 2017 ahwater.net Corporation. #
#Description : hardware resource data collecting.   #
#+++++++++++++++++++++++++++++++++++++++++++++++++++#


#通常默认数据采集程序发布运行在云环境的头节点,如运行在其他可行linux节点，请使用rsh采集头节点数据.
#云环境-头节点主机,监控数据采集
#cpu使用百分比
#controller_cpuuse=`top -b -n2 -p 1 | fgrep "Cpu(s)" | tail -1 | awk -F'id,' -v prefix="$prefix" '{ split($1, vs, ","); v=vs[length(vs)]; sub("%", "", v); printf "%s%.1f%%\n", prefix, 100 - v }'`

#memory使用百分比
#mem=`free -m|grep Mem|awk '{print ($3-$6-$7)/$2}'`

#disk使用百分比
#disk=iostat -x

#=================================================================================
#入库监控字段说明:
#数据库表->ahwater_physcial_monitor
#字段包括:id、create_at、physcial_hostname、cpu_processor、cpu_usage
#				mem_total、mem_used、mem_free、mem_buffer_cache、mem_page_cache
#				mem_swap_total、mem_swap_used、mem_swap_free
#				mem_real_used、mem_usage、root_disk_total、root_disk_used
#				root_disk_free、root_disk_usage
#
#表->ahwater_cinder_monitor
#字段包括:id、create_at、physcial_hostname、vg_total、vg_used、vg_free、vg_usage
#=================================================================================


#一.需要监控的云环境主机列表,sshPorti!=22
cluster=(
		ahwater-cloud-controller
		)

sshPort=2299

for i in ${cluster[*]};
	do
	#other info
	create_at=`date +%Y-%m-%d:%H:%M:%S`
	physcial_hostname=${i}

	#cpu info
	cpu_processor=`rsh -p ${sshPort} ${i} cat /proc/cpuinfo | grep processor | wc -l`
	cpu_usage=`rsh -p ${sshPort} ${i} top -b -n2 -p 1 | fgrep "Cpu(s)" | tail -1 | awk -F'id,' -v prefix="$prefix" '{ split($1, vs, ","); v=vs[length(vs)]; sub("%", "", v); printf "%s%.1f%%\n", prefix, 100 - v }'`

	#mem info
	mem_total=`rsh -p ${sshPort} ${i} free -m | grep Mem | awk -F" " '{print $2}'`
	mem_used=`rsh -p ${sshPort} ${i} free -m | grep Mem | awk -F" " '{print $3}'`
	mem_free=`rsh -p ${sshPort} ${i} free -m | grep Mem | awk -F" " '{print $4}'`
	mem_buffer_cache=`rsh -p ${sshPort} ${i} free -m | grep Mem | awk -F" " '{print $6}'`
	mem_page_cache=`rsh -p ${sshPort} ${i} free -m | grep Mem | awk -F" " '{print $7}'`
	mem_swap_total=`rsh -p ${sshPort} ${i} free -m | grep Swap | awk -F" " '{print $2}'`
	mem_swap_used=`rsh -p ${sshPort} ${i} free -m | grep Swap | awk -F" " '{print $3}'`
	mem_swap_free=`rsh -p ${sshPort} ${i} free -m | grep Swap | awk -F" " '{print $4}'`
	mem_real_used=(${mem_used} - ${mem_buffer_cache} - ${mem_page_cache})
	mem_usage=`rsh -p ${sshPort} ${i} free -m|grep Mem|awk '{print ($3-$6-$7)/$2}'`

	#disk info
	root_disk_total=`rsh -p ${sshPort} ${i} df -lh | grep '/$' | awk -F" " '{print $2}'`
	root_disk_used=`rsh -p ${sshPort} ${i} df -lh | grep '/$' | awk -F" " '{print $3}'`
	root_disk_free=`rsh -p ${sshPort} ${i} df -lh | grep '/$' | awk -F" " '{print $4}'`
	root_disk_usage=`rsh -p ${sshPort} ${i} df -lh | grep '/$' | awk -F" " '{print $5}'`

	#数据入库
	echo "insert into ahwater_physcial_monitor values('','${create_at}','${physcial_hostname}',${cpu_processor},'${cpu_usage}',${mem_total},${mem_used},${mem_free},${mem_buffer_cache},${mem_page_cache},${mem_swap_total},${mem_swap_used},${mem_swap_free},${mem_real_used},'${mem_usage}','${root_disk_total}','${root_disk_used}','${root_disk_free}','${root_disk_usage}')" | mysql -uwateruser -ppass321 waterdb
done

#二.需要监控的云环境主机列表,默认sshPort=22
cluster=(
		ahwater-cloud-compute001
		ahwater-cloud-compute002
		ahwater-cloud-bstorage
		)

#监控指标数据采集及入库
for i in ${cluster[*]};
	#do echo $i;
	do
	
	#other info
	create_at=`date +%Y-%m-%d:%H:%M:%S`
	physcial_hostname=${i}

	#cpu info
	cpu_processor=`rsh ${i} cat /proc/cpuinfo | grep processor | wc -l`
	cpu_usage=`rsh ${i} top -b -n2 -p 1 | fgrep "Cpu(s)" | tail -1 | awk -F'id,' -v prefix="$prefix" '{ split($1, vs, ","); v=vs[length(vs)]; sub("%", "", v); printf "%s%.1f%%\n", prefix, 100 - v }'`

	#mem info
	mem_total=`rsh ${i} free -m | grep Mem | awk -F" " '{print $2}'`
	mem_used=`rsh ${i} free -m | grep Mem | awk -F" " '{print $3}'`
	mem_free=`rsh ${i} free -m | grep Mem | awk -F" " '{print $4}'`
	mem_buffer_cache=`rsh ${i} free -m | grep Mem | awk -F" " '{print $6}'`
	mem_page_cache=`rsh ${i} free -m | grep Mem | awk -F" " '{print $7}'`
	mem_swap_total=`rsh ${i} free -m | grep Swap | awk -F" " '{print $2}'`
	mem_swap_used=`rsh ${i} free -m | grep Swap | awk -F" " '{print $3}'`
	mem_swap_free=`rsh ${i} free -m | grep Swap | awk -F" " '{print $4}'`
	mem_real_used=(${mem_used} - ${mem_buffer_cache} - ${mem_page_cache})
	mem_usage=`rsh ${i} free -m|grep Mem|awk '{print ($3-$6-$7)/$2}'`

	#disk info
	root_disk_total=`rsh ${i} df -lh | grep '/$' | awk -F" " '{print $2}'`
	root_disk_used=`rsh ${i} df -lh | grep '/$' | awk -F" " '{print $3}'`
	root_disk_free=`rsh ${i} df -lh | grep '/$' | awk -F" " '{print $4}'`
	root_disk_usage=`rsh ${i} df -lh | grep '/$' | awk -F" " '{print $5}'`

	#数据入库
	echo "insert into ahwater_physcial_monitor values('','${create_at}','${physcial_hostname}',${cpu_processor},'${cpu_usage}',${mem_total},${mem_used},${mem_free},${mem_buffer_cache},${mem_page_cache},${mem_swap_total},${mem_swap_used},${mem_swap_free},${mem_real_used},'${mem_usage}','${root_disk_total}','${root_disk_used}','${root_disk_free}','${root_disk_usage}')" | mysql -uwateruser -ppass321 waterdb
done

#三.openStack cinder service component vg monitor
cluster=(
		ahwater-cloud-bstorage
		)
cinder_vg_name=cinder-volumes
#监控数据采集及入库
for i in ${cluster[*]};
	do
	#aided info
	create_at=`date +%Y-%m-%d:%H:%M:%S`
    physcial_hostname=${i}

	#volume group info
	vg_total=`rsh ahwater-cloud-bstorage vgdisplay ${cinder_vg_name} | grep "VG Size" | awk -F"VG Size" '{print $2}'`
	vg_used=`rsh ahwater-cloud-bstorage vgdisplay ${cinder_vg_name} | grep Alloc | awk -F"/" '{print $3}'`
	vg_free=`rsh ahwater-cloud-bstorage vgdisplay ${cinder_vg_name} | grep Free | awk -F"/" '{print $3}'`
	#避免单位不一致问题计算usage% 使用PESize * PECount
	#pe_size=`rsh ahwater-cloud-bstorage vgdisplay ${cinder_vg_name} | grep "PE Size" | awk -F" " '{print $3}'`
	total_pe=`rsh ahwater-cloud-bstorage vgdisplay ${cinder_vg_name} | grep " Total PE"  | awk -F" " '{print $3}'`
	alloc_pe=`rsh ahwater-cloud-bstorage vgdisplay ${cinder_vg_name} | grep "Alloc PE" | awk -F"/" '{print $2}' | awk -F" " '{print $2}'`
	#vg_usage=`expr ${alloc_pe} / ${total_pe}`
	#vg_usage=`awk 'begin{printf "%.5f\n",('${alloc_pe}'/'${total_pe}')}'`
	#echo $vg_usage
	vg_usage=`echo "scale=6;${alloc_pe}/${total_pe}" | bc`
	echo "insert into ahwater_cinder_monitor values('','${create_at}','${physcial_hostname}','${vg_total}','${vg_used}','${vg_free}','${vg_usage}')" | mysql -uwateruser -ppass321 waterdb

done
