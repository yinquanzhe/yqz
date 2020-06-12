app.controller('netCtrl',['$scope','$http',function($scope,$http){
	
	$http.get('/ahwaterCloud/cont/listAllNetwork').success(function(NetList){
		$scope.netData=NetList;
		$('.cloudlist>table>tbody>tr:first-child').hide();
	})
	
	//搜索网络
	
	
}])

