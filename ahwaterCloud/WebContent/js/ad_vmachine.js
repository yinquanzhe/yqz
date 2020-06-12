app.controller("ad_vmCtrl",["$scope","$http",function($scope,$http){

	//获取饼图信息
	$scope.getpie=function(){
		$http.get('/ahwaterCloud/ctr/getStatistics').success(function(obj){		
		var cpupie=echarts.init(document.getElementById('cpuPie'));
		var cpupieOption={
	    title : {
	        text: 'CPU使用情况',
	        subtext: obj.vcspus+'中的'+obj.vcpusUsed+'已使用',
	        x:'center',
	        textStyle:{
	        	color:"#555",
	        	fontSize:16,
	        	fontWeight:"normal"
	        }
	    },
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    series : [
        {
          name: '虚拟内核',
          type: 'pie',
          radius : '40%',
          data:[
              {value:obj.vcpusUsed, name:'已使用',itemStyle:{normal:{color:"#C14B0A"}}},
              {value:obj.vcspus-obj.vcpusUsed, name:'剩余',itemStyle:{normal:{color:"#428BCA"}}}
          ],
          label:{
            normal:{
              show:false ,
            	position : 'outside'
            }
          }
        }
    	]
		};
		cpupie.setOption(cpupieOption);
		
		var mempie=echarts.init(document.getElementById('memPie'));
		var mempieOption={
	    title : {
	        text: '内存使用情况(GB)',
	        subtext: obj.memory+'GB中的'+obj.memoryUsed+'GB已使用',
	        x:'center',
	        textStyle:{
	        	color:"#555",
	        	fontSize:16,
	        	fontWeight:"normal"
	        }
	    },
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    series : [
        {
          name: '内存',
          type: 'pie',
          radius : '40%',
          data:[
              {value:obj.memoryUsed, name:'已使用',itemStyle:{normal:{color:"#C14B0A"}}},
              {value:(obj.memory-obj.memoryUsed), name:'剩余',itemStyle:{normal:{color:"#428BCA"}}}
          ],
          label:{
            normal:{
              show:false ,
            	position : 'outside'
            }
          }
        }
    	]
		};
		mempie.setOption(mempieOption);
		
		var diskpie=echarts.init(document.getElementById('diskPie'));
		var diskpieOption={
	    title : {
	        text: '磁盘使用情况(TB)',
	        subtext: obj.localDisk+'TB中的'+obj.localDiskUsed+'TB已使用',
	        x:'center',
	        textStyle:{
	        	color:"#555",
	        	fontSize:16,
	        	fontWeight:"normal"
	        }
	    },
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    series : [
        {
          name: '本地磁盘',
          type: 'pie',
          radius : '40%',
          data:[
              {value:obj.localDiskUsed, name:'已使用',itemStyle:{normal:{color:"#C14B0A"}}},
              {value:(obj.localDisk-obj.localDiskUsed).toFixed(2), name:'剩余',itemStyle:{normal:{color:"#428BCA"}}}
          ],
          label:{
            normal:{
              show:false ,
            	position : 'outside'
            }
          }
        }
    	]
		};
		diskpie.setOption(diskpieOption);
	})
	}
	$scope.getpie();
	
	//获取主机列表
	$scope.getvmachine=function(){
		$http.get('/ahwaterCloud/ctr/listHypervisors').success(function(vmachineList){
			$http.get('/ahwaterCloud/ctr/listComputeServices').success(function(vcomputeList){
				if(vmachineList.length==0){
					$('.cmpServicelist>.cloudlist>table>tbody>tr:first-child td').html('暂无数据')
				}else{
					$('.cmpServicelist>.cloudlist>table>tbody>tr:first-child').hide()
					for (var i = 0; i < vmachineList.length; i++) {
						vmachineList[i].status=vcomputeList[i].status;
						vmachineList[i].updated=vcomputeList[i].updated_at;
					}
					$scope.vmachineData=vmachineList
				}
			})
		})
	}
	$scope.getvmachine();
	
	//关闭服务
	$scope.shutDownService=function($event){
		var e=$event.target;
		new $.flavr({
			content : '关闭服务', 
			dialog : 'prompt',
			prompt : { 
				placeholder: '关闭原因' 
			}, 
			buttons:{
				确定:{
					style:'danger',
					action:function($container,$prompt){ 						
						$.ajax({
						type:'POST',
						dataType:'json',
						url:'/ahwaterCloud/ctr/disableComputeService',
						data:{
							host:$(e).attr('vname'),
							reason:$prompt.val()
						},
						success:function(data){
							
							if(data!=''){		
			        	$scope.getvmachine();
			        	$scope.getpie()
							}
							else{
								new $.flavr({ 
					          content : '关闭未成功,请重试',					          
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
		})
	}
	
	//开启服务
	$scope.startService=function($event){
		var e=$event.target;
		$.ajax({
			type:'GET',
			dataType:'json',
			url:'/ahwaterCloud/ctr/enableComputeService',
			data:{
				host:$(e).attr('vname')
			},
			success:function(data){
						
				if(data!=''){		
	      	$scope.getvmachine();
	      	$scope.getpie()
				}
				else{
					new $.flavr({ 
		          content : '开启未成功,请重试',					          
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
	}
	
	//查看已连接的主机
	$scope.toggleConnectDev=function($event){
		var e=$event.target;
		$('.connectDevBox').css({
			width:'100%',					
			display:'block'
		})
		$http({
			url:'/ahwaterCloud/ctr/listHypervisorServers',			
			method:'GET',
			params:{hypervisorsId:$(e).attr('vid')},
			header:{'Content-Type':'application/json'}
		}).success(function(ConnectDevList){
			$scope.connectDevData=ConnectDevList
			$('.connectDevBox>.cloudlist>table>tbody>tr:first-child').hide()
		})
	}
	
	//关闭弹窗
	$scope.disShowBox=function(){
		$('.connectDevBox').css({
			width:'0',			
			display:'none'
		})
	}
}])