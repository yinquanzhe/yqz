app.controller('aggrCtrl',['$scope','$http',function($scope,$http){
	
	var url='/ahwaterCloud/cont/listAvailabilityZone';
	
	$http({
		method:'GET',
		url:'/ahwaterCloud/cont/listAvailabilityZone',
		params:{tenantId:$('#dropdownTenant').attr('tid')},
		headers:{'Content-Type':'application/json'}
	}).success(function(aggrList){
		if(aggrList.length==0){
			$('.cloudlist table>tbody>tr:first-child td').html('暂无数据')
		}else{
			$('.cloudlist table>tbody>tr:first-child').hide()
			$scope.aggrData=aggrList;
		}
	})
		
}])