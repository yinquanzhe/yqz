app.controller('sysCtrl',['$scope','$http',function($scope,$http){
	$scope.getService=function(){
		$http.get('/ahwaterCloud/ctr/listService').success(function(serviceList){
			for (var i = 0; i < serviceList.length; i++) {
				var end=serviceList[i].links.self.lastIndexOf(':')
				serviceList[i].host=serviceList[i].links.self.slice(7,end)
			}
			$scope.serviceData=serviceList;
			$('#adService .cloudlist>table>tbody>tr:first-child').hide();
		})
	}
	$scope.getService()
	
	$scope.getCompService=function(){
		$http.get('/ahwaterCloud/ctr/listSysComputeService').success(function(compServiceList){
			for (var i = 0; i < compServiceList.length; i++) {
				compServiceList[i].updated_at.replace(/T/g,'')
				compServiceList[i].updated_at.slice(0,compServiceList[i].updated_at.indexOf('.'))
			}
			$scope.compServiceData=compServiceList;
			$('#adCompService .cloudlist>table>tbody>tr:first-child').hide();
		})
	}
	
	$('#systemTab a').on('click',function(e){
		e.preventDefault()
		$(this).tab('show')
		if($(this).attr('href').slice(1)=='adService'){
			$scope.getService()
		}else{
			$scope.getCompService()
		}
	})
	
}])