app.controller('routeDetailCtrl',["$scope","$http","$routeParams",function($scope,$http,$routeParams){
	
	$('.loading-wrap').show().siblings().css({
		visibility:'hidden'
	})
		
	var routerid=$routeParams.routerId;
	//获取概况
	$scope.getRouterInfo=function(){
		$http({
			method:'GET',
			url:'/ahwaterCloud/contr/GetRouterDetail',
			params:{routerId:routerid},
			header:{'Content-Type':'application/json'}
		}).success(function(data){			
			$('.loading-wrap').hide().siblings().css({
				visibility:'visible'
			});
			$scope.routeName=data.routeName;			
			$scope.route=data;
		})
		//获取静态路由表
		$http({
			method:'GET',
			url:'/ahwaterCloud/contr/GetHostRoute',
			params:{routerId:routerid},
			header:{'Content-Type':'application/json'}
		}).success(function(data){					
			$scope.routeTable=data;
			if(data.length==0){
				$('#routeInfo .cloudlist>table>tbody>tr:first-child td').html("暂无数据")
			}else{
				$('#routeInfo .cloudlist>table>tbody>tr:first-child').hide();
			}
		})
	}
	$scope.getRouterInfo();
	
		
	$scope.getrouteInfoBtn=function(){		
		$scope.getRouterInfo();
	}	
	
	//选项卡切换
	$('#routeInfoTab a').click(function(e){
	  e.preventDefault()
	  $(this).tab('show')
	})
		
	//修改名称
	$scope.editRouter=function($event){
		var e=$event.target;
		new $.flavr({ 
		content : '编辑路由', 
		dialog : 'prompt',
		prompt : { 
			placeholder: '输入新的路由名称' 
		}, 
		buttons:{
			保存:{
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
