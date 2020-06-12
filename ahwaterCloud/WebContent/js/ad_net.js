app.controller('adnetCtrl',['$scope','$http',function($scope,$http){
	
	$http.get('/ahwaterCloud/cont/adminListAllNetworks').success(function(NetList){
		$scope.adnetData=NetList;
		$('.cloudlist>table>tbody>tr:first-child').hide();
	})
	
	//搜索网络
	
	
}])

