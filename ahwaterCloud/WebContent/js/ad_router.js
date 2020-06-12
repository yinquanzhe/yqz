app.controller('adrouterCtrl',['$scope','$http',function($scope,$http){
	
	$http.get('/ahwaterCloud/contr/ListAllRouter').success(function(routerList){
		$scope.AdrouterData=routerList;
		if(routerList.length==0){
			$('.cloudlist>table>tbody>tr:first-child td').html("暂无数据")
		}else{
			$('.cloudlist>table>tbody>tr:first-child').hide();
		}
	})
	
	//编辑路由
	$scope.editAdminRouter=function($event){
		var e=$event.target;
		new $.flavr({ 
		content : '编辑路由', 
		dialog : 'prompt',
		prompt : { 
			placeholder: '输入新的路由名称' 
		}, 
		buttons:{
			提交:{
				style:'danger',
				action:function($container,$prompt){ 	
						console.log($prompt.val());
							$.ajax({
							type:'POST',
							dataType:'json',
							url:'url',
							data:{
								newRouteName:$prompt.val(),
								routeId:$(e).attr('rid')
							},
							success:function(data){
								if(data.errorMsg=="success"){
									$(e).prev().html($prompt.val())
								}
								else{
									new $.flavr({ 
						          content : '修改失败,提示信息：'+data.errorMsg,
						          autoclose : true,
						          timeout : 3500,
						          buttons:{
						            	确定:{}
						          }
				        		});
								}
							},
							error:function(){
								new $.flavr({ 
						          content : '请求失败',
						          autoclose : true,
						          timeout : 2500,
						          buttons:{
						            	确定:{}
						          }
				        		});
							}
						})
						return true;
						
				}
			},
			取消:{}
		}		
	});
	}
	
}])

