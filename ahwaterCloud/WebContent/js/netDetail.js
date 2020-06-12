app.controller('netDetailCtrl',["$scope","$http","$routeParams",function($scope,$http,$routeParams){
	$('.loading-wrap').show().siblings().css({
		visibility:'hidden'
	})	
	var netid=$routeParams.netId;
	//获取网络概况
	$scope.getNetInfo=function(){
		$http({
			method:'GET',
			url:'/ahwaterCloud/cont/networkDetail',
			params:{networkId:netid},
			header:{'Content-Type':'application/json'}
		}).success(function(data){			
			$('.loading-wrap').hide().siblings().css({
				visibility:'visible'
			});
			$scope.netName=data.networkName;			
			$scope.net=data;
		})
		//获取子网列表
		$http({
			method:'GET',
			url:'/ahwaterCloud/cont/listSubnet',
			params:{networkId:netid},
			header:{'Content-Type':'application/json'}
		}).success(function(data){					
			$scope.subNetTable=data;
			if(data.length==0){
				$('#netInfo .cloudlist>table>tbody>tr:first-child td').html("暂无数据")
			}else{
				$('#netInfo .cloudlist>table>tbody>tr:first-child').hide();
			}
		})
	}
	$scope.getNetInfo();
	
		
	$scope.getnetInfoBtn=function(){		
		$scope.getNetInfo();
	}
	
	//获取端口
	$scope.getPort=function(){		
		$http({
			method:'GET',
			url:'/ahwaterCloud/cont/listPorts',
			params:{networkId:netid},
			header:{'Content-Type':'application/json'}
		}).success(function(data){					
			if(data.length==0){
				$('#netPort .cloudlist>table>tbody>tr:first-child td').html("暂无数据")
			}else{
				$('#netPort .cloudlist>table>tbody>tr:first-child').hide();
				for(var i=0;i<data.length;i++){
					if(data[i].portName==""){
						data[i].portName=data[i].portId.slice(0,8)
					}
				}
				$scope.interfaceTable=data;
			}
		})
	}
	
	//选项卡切换
	$('#netInfoTab a').click(function(e){
	  e.preventDefault()
	  $(this).tab('show')
	})
		
	//弹出子网详情
	$scope.toggleDetailSubNet=function($event){
		var e=$event.target;	
		$(e).parent().parent().parent().siblings().children('.imageAllName').children('span').children('.imageDetailBox').removeClass('showed');
		$http({
			url:'/ahwaterCloud/cont/subnetDetail',
			method:'get',
			params:{subnetId:$(e).attr('nid')},
			headers:{'Content-Type':'application/json'}
		}).success(function(subNetdetail){
			$(e).siblings('.imageDetailBox').toggleClass('showed');
			$scope.subNetdetailData=subNetdetail;
		})
	}

	
	//关闭弹框
	$scope.DetailClose=function(){
		$('.imageDetailBox').removeClass('showed');
	}
	
	//编辑端口
	$scope.editPort=function($event){  	
   	var e=$event.target;
   	var pid=$(e).attr('pid')
			new $.flavr({ 
				content : '编辑端口', 
				dialog : 'prompt',
				prompt : { 
					placeholder: '输入新的端口名称' 
				}, 
				buttons:{
					确定:{
						style:'danger',
						action:function($container,$prompt){ 						
								var name=$prompt.val();						
									$.ajax({
									type:'POST',
									dataType:'json',
									url:'/ahwaterCloud/cont/updatePortName',
									data:{
										portId:$(e).attr('pid'),
										newName:name
									},
									success:function(data){
										if(data=='succ'){		
						        	if(name==''){
						        		$(e).next().html(pid.slice(0,8));
						        	}else{
						        		$(e).next().html(name);
						        	}
										}
										else{
											new $.flavr({ 
								          content : '修改失败,提示信息:'+data,					          
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
