app.controller('monitorCtrl',['$scope','$http','$interval',function($scope,$http,$interval){
	$scope.getMonitor=function(){
		console.log('开始获取')
		$http({
			method:'get',
			url:'/ahwaterCloud/ctr/listNewMonitor',
			headers:{'Content-Type':'application/json'}
		}).success(function(monitorList){
			if(monitorList.length==0){
				$('.monitor .cloudlist table tbody tr:first-child td').html('暂无监控信息')
			}else{
				$('.monitor .cloudlist table tbody tr:first-child').hide()
				for(var i=0;i<monitorList.length;i++){
					monitorList[i].vcpu_usage_rate=monitorList[i].vcpu_usage_rate.toFixed(2)
					monitorList[i].vmem_rss_size=(monitorList[i].vmem_rss_size/1024/1024).toFixed(2)
					monitorList[i].diskPercent=(monitorList[i].vdisk_allocation_size/monitorList[i].vdisk_capacity_size*100).toFixed(2)
					monitorList[i].diskRead=(monitorList[i].vdisk_read_bytes/1024/1024).toFixed(2)
					monitorList[i].diskWrite=(monitorList[i].vdisk_write_bytes/1024/1024).toFixed(2)
					monitorList[i].vnet_flow_rx_bytes=(monitorList[i].vnet_flow_rx_bytes/1024/1024/1024).toFixed(2)
					monitorList[i].vnet_flow_tx_bytes=(monitorList[i].vnet_flow_tx_bytes/1024/1024/1024).toFixed(2)
				}
				$scope.monitorData=monitorList
			}
		})
		/*$http.get('/ahwaterCloud/ctr/listAllServers').success(function(serverList){
			
		})*/
		
	}
	$scope.getMonitor();
	
	var timer=$interval(function(){
		$scope.getMonitor();
	},60000*5.5)
	
}])