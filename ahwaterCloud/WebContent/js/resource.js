app.controller('resCtrl',['$scope','$http',function($scope,$http){
	
	$scope.getLimit=function(){
		$http.get('/ahwaterCloud/contr/GetLimits').success(function(limitList){		
			$scope.limitList=limitList;
			$('.cloudlist table>tbody>tr:first-child').hide()
		})
	}
	$scope.getLimit()
	
	$scope.editRes=function(){
		$('#updateResModal').modal('show')
		$('#metaCount').val($scope.limitList.maxImageMeta) //元数据条目
		$('#fileLimit').val($scope.limitList.maxPersonality) //注入的文件
		$('#fileByte').val($scope.limitList.maxPersonalitySize) //注入的文件字节数
		$('#serverGroupMember').val($scope.limitList.maxServerGroupMembers) //主机组成员
		$('#serverGroup').val($scope.limitList.maxServerGroups) //主机组
		
		$('#cpuLimit').val($scope.limitList.maxTotalCores) //虚拟内核
		$('#ipsCount').val($scope.limitList.maxTotalFloatingIps) //浮动IP
		$('#serverLimit').val($scope.limitList.maxTotalInstances) //云主机
		$('#memoryLimit').val($scope.limitList.maxTotalRAMSize) //内存
		$('#keyLimit').val($scope.limitList.maxTotalKeypairs) //密钥对
				
		$('#volShotLimit').val($scope.limitList.maxGigabytes) //云硬盘和快照总大小
		$('#snapShotLimit').val($scope.limitList.maxSnapShots) //云硬盘快照
		$('#volLimit').val($scope.limitList.maxVolumes) //云硬盘
		$('#ruleLimit').val($scope.limitList.maxSecurityGroupRules) //安全组规则
		$('#seGroupLimit').val($scope.limitList.maxSecurityGroups) //安全组
		$('#subnetLimit').val($scope.limitList.maxSubnet) //子网数
		$('#routeLimit').val($scope.limitList.maxRouter) //路由数
		$('#portLimit').val($scope.limitList.maxPort) //端口
		$('#netLimit').val($scope.limitList.maxNetwork) //网络
		
		
		
		$scope.updateResAction=function(){
			$('#updateResModal').modal('hide')
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/contr/SetLimits",
				dataType:'json',
				data:{
					maxImageMeta: parseInt($('#metaCount').val()),
					maxPersonality:parseInt($('#fileLimit').val()),
					maxPersonalitySize:parseInt($('#fileByte').val()),
					maxServerGroupMembers:parseInt($('#serverGroupMember').val()),
					maxServerGroups:parseInt($('#serverGroup').val()),
					maxTotalCores:parseInt($('#cpuLimit').val()),
					maxTotalFloatingIps:parseInt($('#ipsCount').val()),
					maxTotalInstances:parseInt($('#serverLimit').val()),
					maxTotalRAMSize:parseInt($('#memoryLimit').val()),
					maxTotalKeypairs:parseInt($('#keyLimit').val()),
					maxGigabytes:parseInt($('#volShotLimit').val()),
					maxSnapShots:parseInt($('#snapShotLimit').val()),
					maxVolumes:parseInt($('#volLimit').val()),
					maxSecurityGroupRules:parseInt($('#ruleLimit').val()),
					maxSecurityGroups:parseInt($('#seGroupLimit').val()),
					maxSubnet:parseInt($('#subnetLimit').val()),
					maxRouter:parseInt($('#routeLimit').val()),
					maxPort:parseInt($('#portLimit').val()),
					maxNetwork:parseInt($('#netLimit').val())
				},
				success:function(data){
					if(data=='success'){
						$scope.getLimit();
						new $.flavr({
							content:'修改成功',
							modal:false,
							autoclose:true,
							timeout:2500,
							buttons:{确定:{}}
						})
					}
				},
				error:function(){
					new $.flavr({
						content:'请求失败',
						autoclose:true,
						timeout:2500,
						buttons:{确定:{}}
					})
				}
			});
		}
	}
	
}])