-- MySQL dump 10.14  Distrib 5.5.52-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: waterdb
-- ------------------------------------------------------
-- Server version	5.5.52-MariaDB-1ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ahwater_cloud_disks`
--

DROP TABLE IF EXISTS `ahwater_cloud_disks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ahwater_cloud_disks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenantName` varchar(255) NOT NULL,
  `volumeName` varchar(255) NOT NULL,
  `serverId` varchar(255) NOT NULL,
  `serverName` varchar(255) NOT NULL,
  `volumeSize` int(11) NOT NULL,
  `volumeStatus` varchar(255) NOT NULL,
  `bootable` int(10) DEFAULT '1',
  `encryption` int(2) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1265 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ahwater_cloud_instances`
--

DROP TABLE IF EXISTS `ahwater_cloud_instances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ahwater_cloud_instances` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenanantName` varchar(255) NOT NULL,
  `host` varchar(200) NOT NULL,
  `serverId` varchar(255) NOT NULL,
  `serverName` varchar(255) NOT NULL,
  `imageName` varchar(255) NOT NULL,
  `imagePersonalName` varchar(255) NOT NULL,
  `imageOStype` varchar(255) DEFAULT 'NULL',
  `imageOSVersion` varchar(255) DEFAULT 'NULL',
  `imageOSBit` varchar(25) DEFAULT 'NULL',
  `ipAddr` varchar(100) NOT NULL,
  `vcpus` int(35) NOT NULL,
  `ram` int(255) NOT NULL,
  `disk` int(200) NOT NULL,
  `status` varchar(100) NOT NULL,
  `timeFromCreated` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27211 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ahwater_cloud_monitor`
--

DROP TABLE IF EXISTS `ahwater_cloud_monitor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ahwater_cloud_monitor` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '监控记录Id标识',
  `physcial_hostname` varchar(255) NOT NULL COMMENT '宿主机标识',
  `create_at` datetime(2) DEFAULT NULL,
  `kvm_name` varchar(255) NOT NULL COMMENT '云主机hypervisor kvm识别名称',
  `uuid` varchar(36) NOT NULL COMMENT '云主机uuid标识',
  `cloud_hostname` varchar(255) NOT NULL COMMENT '云主机openstack nova识别名称',
  `vcpu_usage_rate` float(8,6) NOT NULL COMMENT '云主机虚拟Vcpu使用率',
  `vnet_name` varchar(25) NOT NULL COMMENT '云主机虚拟网卡名称',
  `vnet_flow_rx_bytes` bigint(20) NOT NULL COMMENT '虚拟网卡接收数据流(Rx)bytes',
  `vnet_flow_rx_packets` bigint(20) NOT NULL COMMENT '虚拟网卡接收数据包(Rx)packets',
  `vnet_flow_rx_errs` int(11) NOT NULL COMMENT '虚拟网卡接收错误数据包量',
  `vnet_flow_rx_drop` int(11) NOT NULL COMMENT '虚拟网卡丢包量',
  `vnet_flow_tx_bytes` bigint(20) NOT NULL COMMENT '虚拟网卡发送数据流(Rx)bytes',
  `vnet_flow_tx_packets` bigint(20) NOT NULL COMMENT '虚拟网卡发送数据包(Tx)packets',
  `vnet_flow_tx_errs` int(11) NOT NULL COMMENT '虚拟网卡接收错误数据包量',
  `vnet_flow_tx_drop` int(11) NOT NULL COMMENT '虚拟网卡丢包量',
  `vdisk_name` varchar(5) NOT NULL COMMENT '云主机虚拟磁盘名称',
  `vdisk_capacity_size` bigint(20) NOT NULL COMMENT '虚拟磁盘预分配容量/Gb',
  `vdisk_allocation_size` bigint(20) NOT NULL COMMENT '云主机中虚拟磁盘以分配使用容量/bytes,在capacity容量基础上占用',
  `vdisk_physical_size` bigint(20) NOT NULL COMMENT '云主机中虚拟磁盘所占用宿主物理机磁盘空间',
  `vdisk_read_bytes` bigint(20) NOT NULL COMMENT 'Vdisk_iops->虚拟磁盘I/O读字节量',
  `vdisk_read_requests` bigint(20) NOT NULL COMMENT '云主机虚拟系统盘读请求数',
  `vdisk_write_bytes` bigint(20) NOT NULL COMMENT 'vDisk_iops->虚拟磁盘I/O写字节量',
  `vdisk_write_requests` bigint(20) NOT NULL COMMENT '云主机虚拟系统盘写请求数',
  `vdisk_errors` int(11) NOT NULL COMMENT '虚拟磁盘读写状态值,-1:无错误,0:存在错误',
  `vmem_actual_size` bigint(20) NOT NULL COMMENT '启动虚拟机设置的最大内存/bytes',
  `vmem_rss_size` bigint(20) NOT NULL COMMENT '对应云主机qemu process进程在宿主机所占内存',
  `vmem_available_size` bigint(20) DEFAULT NULL COMMENT '虚拟机识别出的最大内存/bytes',
  `vmem_unused_size` bigint(20) DEFAULT NULL COMMENT '虚拟机未使用内存量,vmem_available/unused字段依赖vm是否启用memballon',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=163625 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `meminfo`
--

DROP TABLE IF EXISTS `meminfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meminfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `physcialHostName` varchar(25) NOT NULL,
  `timestamp` varchar(25) NOT NULL,
  `kvmName` varchar(25) NOT NULL,
  `novaName` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `monitor`
--

DROP TABLE IF EXISTS `monitor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `monitor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `physcial_hostname` varchar(25) NOT NULL,
  `create_at` datetime NOT NULL,
  `kvm_name` varchar(25) NOT NULL,
  `nova_name` varchar(25) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-13 16:17:38
