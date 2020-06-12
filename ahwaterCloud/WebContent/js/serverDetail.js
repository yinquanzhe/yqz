app.controller('serverDetailCtrl',["$scope","$http","$routeParams",function($scope,$http,$routeParams){
	
	$('.loading-wrap').show().siblings().css({
		visibility:'hidden'
	})
		
	$('#monitorStartTime').jeDate({
    format:"YYYY-MM-DD",
    isTime:true, 
    minDate:"2014-09-19 00:00:00"
	})
	
	var serverid=$routeParams.serverId;
	//获取概况
	$scope.getServerInfo=function(){
		$http({
			method:'GET',
			url:'/ahwaterCloud/ctr/serverOverviewShow',
			params:{serverId:serverid},
			header:{'Content-Type':'application/json'}
		}).success(function(data){
			$scope.ipArr=[];
			$scope.metaArr=[];
			$scope.volumeList=data.volumes
			for(k in data.ipAddrs){
				$scope.ipArr.push({'name':k,'addr':data.ipAddrs[k]})
			}
			for(k in data.metaData){
				$scope.metaArr.push({'name':k,'meta':data.metaData[k]})
			}
			$('.loading-wrap').hide().siblings().css({
				visibility:'visible'
			});
			$scope.serverName=data.serverName;
			$scope.status=data.status;
			$scope.server=data;			
			var start = {
		    ishmsVal:false,
		    minDate: data.createdTime,
		    maxDate: $.nowDate(0),
		    format:"YYYY-MM-DD hh:mm",
		    zIndex:3000,
			};
			var end = {
			  ishmsVal:false,
		    minDate: data.createdTime,
		    maxDate: $.nowDate(0),
		    format:"YYYY-MM-DD hh:mm",
		    choosefun: function(elem,val){
		      start.maxDate = val; //将结束日的初始值设定为开始日的最大日期
		      var v1=$('#inpstart').val();	    	
		    	var d1=new Date(parseInt(v1.slice(0,4)),parseInt(v1.slice(5,7))-1,parseInt(v1.slice(8,10)),parseInt(v1.slice(11,13)),parseInt(v1.slice(14,16)))
		    	var d2=new Date(parseInt(val.slice(0,4)),parseInt(val.slice(5,7))-1,parseInt(val.slice(8,10)),parseInt(val.slice(11,13)),parseInt(val.slice(14,16)))
		    	var hour=Math.round((d2.getTime()-d1.getTime())/3600000);
		    	$('.chooseTimeBox button').removeClass('active');
		    	$scope.getServerMonitor(serverid,hour,hour);
		    },
		    okfun:function(elem,val){
		    	var v1=$('#inpstart').val();	
		    	var d1=new Date(parseInt(v1.slice(0,4)),parseInt(v1.slice(5,7))-1,parseInt(v1.slice(8,10)),parseInt(v1.slice(11,13)),parseInt(v1.slice(14,16)))
		    	var d2=new Date(parseInt(val.slice(0,4)),parseInt(val.slice(5,7))-1,parseInt(val.slice(8,10)),parseInt(val.slice(11,13)),parseInt(val.slice(14,16)))
		    	var hour=Math.round((d2.getTime()-d1.getTime())/3600000);
		    	$('.chooseTimeBox button').removeClass('active');
		    	$scope.getServerMonitor(serverid,hour,hour);
		    }
			};
			$("#inpstart").jeDate(start);
			$("#inpend").jeDate(end);
		})
		$http({
				method :'POST',
				url:'/ahwaterCloud/contr/ListServerGroups',
				params :{serverId:serverid},
				headers : {'Content-Type':'application/json'}
			}).success(function(ServerGroups){
				if(ServerGroups.length==0){
						$scope.existGroupList=[];
					}else{
						$scope.existGroupList=ServerGroups[0];										
					}					
			})
		
	}
	
	$scope.getServerInfo();
	$scope.getServerInfoBtn=function(){
		$('.serverDetail .actionBox').show()
		$scope.getServerInfo();
	}
	
	//选项卡切换
	$('#serverInfoTab a').click(function(e){
	  e.preventDefault()
	  $(this).tab('show')
	})
		
	//获取监控信息
	$scope.getServerMonitor=function(uuid,hour,interval){
		$http({
			method:'get',
			url:'/ahwaterCloud/ctr/listOneServerMonitor',
			params:{
				uuid:uuid,
				hours:hour
			},
			headers:{'Content-Type':'application/json'}
		}).success(function(monitorList){
			//CPU信息
			var CPUChart=echarts.init(document.getElementById("serverChartCPU"))
			var TimeData=[];
			var cpuPercentData=[];
			var memData=[];
			var netData=[];		
			var diskPercentData=[];
			var diskWriteData=[];
			var diskReadData=[];
			for(let i=0;i<monitorList.length;i++){								
				TimeData.push(monitorList[i].create_at.slice(10,16))
				cpuPercentData.push(monitorList[i].vcpu_usage_rate.toFixed(2))
				memData.push((monitorList[i].vmem_rss_size/1024/1024).toFixed(2))
				netData.push(((monitorList[i].vnet_flow_rx_bytes/1024/1024/1024)+(monitorList[i].vnet_flow_tx_bytes/1024/1024/1024)).toFixed(2));
				diskPercentData.push((monitorList[i].vdisk_allocation_size/monitorList[i].vdisk_capacity_size*100).toFixed(2));
				diskWriteData.push((monitorList[i].vdisk_write_bytes/1024/1024).toFixed(2));
				diskReadData.push((monitorList[i].vdisk_read_bytes/1024/1024).toFixed(2));
			}		
			var cpuoption = {
			    title: {
			        text: 'cpu信息',
			        textStyle:{
			            color:'#555',
			            fontSize:16,
			            fontWeight:'normal'
			        }
			    },
			    tooltip:{
			      trigger: 'axis',
			        formatter: '{b0}<br />{c0}%',   //提示信息
			        axisPointer: {
			            animation: false
			        }
			    },
			    xAxis: {
			        type: 'category',
			        splitLine: {
			            show: false
			        },
			        data:TimeData,
			        axisLabel:{
			            interval:interval  //刻度间隔
			        },
			        boundaryGap:false,
			        name:'时间'
			    },
			    yAxis: {
			        type: 'value',
			        min:0,
			        max:100,
			        splitLine: {
			            show: true
			        },
			        axisLabel:{
			            interval:0
			        },
			        name:'使用率(%)',
			        splitNumber:5,
			        minInterval:1
			    },
			    series: [{
			        name: 'cpu信息',
			        type: 'line',
			        showSymbol: false,
			        hoverAnimation: false,
			        smooth:true,
			        lineStyle:{
			            normal:{
			                color:'#fa0'  //线条颜色
			            }
			         },
			        data: cpuPercentData
			    }]
			};
			CPUChart.setOption(cpuoption);
			
			//磁盘使用
			var DiskPercentChart=echarts.init(document.getElementById("serverChartDiskPercent"));	
			var diskoption = {
		    title: {
			        text: '磁盘使用',
			        textStyle:{
			            color:'#555',
			            fontSize:16,
			            fontWeight:'normal'
			        }
			    },
			    tooltip:{
			      trigger: 'axis',
			        formatter: '{b0}<br />{c0}%',   //提示信息
			        axisPointer: {
			            animation: false
			        }
			    },
			    xAxis: {
			        type: 'category',
			        splitLine: {
			            show: false
			        },
			        data:TimeData,
			        axisLabel:{
			            interval:interval  //刻度间隔
			        },
			        boundaryGap:false,
			        name:'时间'
			    },
			    yAxis: {
			        type: 'value',
			        min:0,
			        max:100,
			        splitLine: {
			            show: true
			        },
			        axisLabel:{
			            interval:0
			        },
			        name:'使用率(%)',
			        splitNumber:5,
			        minInterval:1
			    },
			    series: [{
			        name: '磁盘使用',
			        type: 'line',
			        showSymbol: false,
			        hoverAnimation: false,
			        smooth:true,
			        lineStyle:{
			            normal:{
			                color:'#A94F2F'  //线条颜色
			            }
			         },
			        data: diskPercentData
			    }]
			};
			DiskPercentChart.setOption(diskoption)
			
			//磁盘总写
			var DiskInChart=echarts.init(document.getElementById("serverChartDiskIn"));	
			var diskinoption = {
		    title: {
			        text: '磁盘总写',
			        textStyle:{
			            color:'#555',
			            fontSize:16,
			            fontWeight:'normal'
			        }
			    },
			    tooltip:{
			      trigger: 'axis',
			        formatter: '{b0}<br />{c0}MB',   //提示信息
			        axisPointer: {
			            animation: false
			        }
			    },
			    xAxis: {
			        type: 'category',
			        splitLine: {
			            show: false
			        },
			        data:TimeData,
			        axisLabel:{
			            interval:interval  //刻度间隔
			        },
			        boundaryGap:false,
			        name:'时间'
			    },
			    yAxis: {
			        type: 'value',
			        splitLine: {
			            show: true
			        },
			        name:'磁盘写入量(MB)'
			    },
			    series: [{
			        name: '磁盘总写',
			        type: 'line',
			        showSymbol: false,
			        hoverAnimation: false,
			        smooth:true,
			        lineStyle:{
			            normal:{
			                color:'#61A0A8'  //线条颜色
			            }
			         },
			        data: diskWriteData
			    }]
			};
			DiskInChart.setOption(diskinoption)
			
			//磁盘读
			var DiskOutChart=echarts.init(document.getElementById("serverChartDiskOut"));	
			var diskoutoption = {
		    title: {
			        text: '磁盘总读',
			        textStyle:{
			            color:'#555',
			            fontSize:16,
			            fontWeight:'normal'
			        }
			    },
			    tooltip:{
			      trigger: 'axis',
			        formatter: '{b0}<br />{c0}MB',   //提示信息
			        axisPointer: {
			            animation: false
			        }
			    },
			    xAxis: {
			        type: 'category',
			        splitLine: {
			            show: false
			        },
			        data:TimeData,
			        axisLabel:{
			            interval:interval  //刻度间隔
			        },
			        boundaryGap:false,
			        name:'时间'
			    },
			    yAxis: {
			        type: 'value',
			        splitLine: {
			            show: true
			        },
			        name:'磁盘读出量(MB)'
			    },
			    series: [{
			        name: '磁盘总读',
			        type: 'line',
			        showSymbol: false,
			        hoverAnimation: false,
			        smooth:true,
			        lineStyle:{
			            normal:{
			                color:'#2F4554'  //线条颜色
			            }
			         },
			        data: diskReadData
			    }]
			};
			DiskOutChart.setOption(diskoutoption)
			
			//所占内存
			var MemChart=echarts.init(document.getElementById("serverChartMem"));	
			var memoption = {
		    title: {
			        text: '所占内存',
			        textStyle:{
			            color:'#555',
			            fontSize:16,
			            fontWeight:'normal'
			        }
			    },
			    tooltip:{
			      trigger: 'axis',
			        formatter: '{b0}<br />{c0}GB',   //提示信息
			        axisPointer: {
			            animation: false
			        }
			    },
			    xAxis: {
			        type: 'category',
			        splitLine: {
			            show: false
			        },
			        data:TimeData,
			        axisLabel:{
			            interval:interval  //刻度间隔
			        },
			        boundaryGap:false,
			        name:'时间'
			    },
			    yAxis: {
			        type: 'value',			        
			        splitLine: {
			            show: true
			        },
			        name:'占用内存(GB)'
			    },
			    series: [{
			        name: '所占内存',
			        type: 'line',
			        showSymbol: false,
			        hoverAnimation: false,
			        smooth:true,
			        lineStyle:{
			            normal:{
			                color:'#C23531'  //线条颜色
			            }
			         },
			        data: memData
			    }]
			};
			MemChart.setOption(memoption)
			
			//总流量
			var NetChart=echarts.init(document.getElementById("serverChartNet"));	
			var netoption = {
		    title: {
			        text: '总流量',
			        textStyle:{
			            color:'#555',
			            fontSize:16,
			            fontWeight:'normal'
			        }
			    },
			    tooltip:{
			      trigger: 'axis',
			        formatter: '{b0}<br />{c0}GB',   //提示信息
			        axisPointer: {
			            animation: false
			        }
			    },
			    xAxis: {
			        type: 'category',
			        splitLine: {
			            show: false
			        },
			        data:TimeData,
			        axisLabel:{
			            interval:interval  //刻度间隔
			        },
			        boundaryGap:false,
			        name:'时间'
			    },
			    yAxis: {
			        type: 'value',			        
			        splitLine: {
			            show: true
			        },
			        name:'产生流量(GB)'
			    },
			    series: [{
			        name: '总流量',
			        type: 'line',
			        showSymbol: false,
			        hoverAnimation: false,
			        smooth:true,
			        lineStyle:{
			            normal:{
			                color:'#C23531'  //线条颜色
			            }
			         },
			        data: netData
			    }]
			};
			NetChart.setOption(netoption)
			
		})
	}
	
	
	$scope.getServerMonitor(serverid,12,10) //初始12小时，以下为小时按钮
	$scope.twoHous=function($event){
		var e=$event.target;
		$(e).addClass('active').siblings('button').removeClass('active')
		$scope.getServerMonitor(serverid,2,2)
	}
	$scope.sixHous=function($event){
		var e=$event.target;
		$(e).addClass('active').siblings('button').removeClass('active')
		$scope.getServerMonitor(serverid,6,6)
	}
	$scope.twelveHous=function($event){
		var e=$event.target;
		$(e).addClass('active').siblings('button').removeClass('active')
		$scope.getServerMonitor(serverid,12,10)
	}
	$scope.twofourHous=function($event){
		var e=$event.target;
		$(e).addClass('active').siblings('button').removeClass('active')
		$scope.getServerMonitor(serverid,24,20)
	}
	
	
	
	

	//获取操作记录
	$scope.getRecord=function(){
		$('.serverDetail .actionBox').hide()
		$http({
			method:'GET',
			url:'/ahwaterCloud/ctr/listActionForServer',
			params:{serverId:serverid},
			header:{'Content-Type':'application/json'}
		}).success(function(data){
			console.dir(data);
			for(var i=0;i<data.length;i++){
				data[i].start_time=data[i].start_time.replace('T',' ').slice(0,data[i].start_time.indexOf('.'));
			}
			$('.cloudlist>table>tbody>tr:first-child').hide();
			$scope.recordList=data;
		})
	}
	
	//获取日志
	$scope.getDaily=function(){
		$('.serverDetail .actionBox').hide();
		$http({
			url:'/ahwaterCloud/ctr/ConsoleLogShow',
			method:'GET',
			params:{
				serverId:serverid,
				numLines:parseInt($("#dailyRow").val())
			},
			headers:{'Content-Type':'application/json'}
		}).success(function(dailyList){
			$scope.dailyData=dailyList;
		})
	}
	
	//操作
	$scope.stopServer=function(){	
			var html=`停止中<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$('.serverDetail .serverStatusBox > div.contentBox .serverStatus span').html(html);
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"stop"
				},
				success:function(data){
					if(data.msg=='succ'){
						$scope.getServerInfo();
						$('.serverDetail .serverStatusBox > div.contentBox .serverStatus span').html("关机");
					}
					else{
						new $.flavr({
					    content:'出现错误',
					    autoclose:true,
					    timeout:3000,
					    buttons:{
					       	确定:{}
					    }
				  	})
					}
				},
				error:function(){
					new $.flavr({
				    content:'请求失败',
				    autoclose:true,
				    timeout:3000,
				    buttons:{
				       	确定:{}
				    }
				  })
				}
			})
	}
	
	//一些操作
	$scope.actionServer=function(tipname,action){	
		var html=`${tipname}<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$('.serverDetail .serverStatusBox > div.contentBox .serverStatus span').html(html);
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:action
				},
				success:function(data){
					if(data.msg=='succ'){
						$('.serverDetail .serverStatusBox > div.contentBox .serverStatus span').html("运行中");
						$scope.getServerInfo();
					}
					else{
						new $.flavr({
					    content:'出现错误',
					    autoclose:true,
					    timeout:3000,
					    buttons:{
					       	确定:{}
					    }
				  	})
					}
				},
				error:function(){
					new $.flavr({
				    content:'请求失败',
				    autoclose:true,
				    timeout:3000,
				    buttons:{
				       	确定:{}
				    }
				  })
				}
			})
		}
	
	
	//修改名称
	$scope.editServerName=function($event){
			var e=$event.target;
			new $.flavr({ 
				content : '编辑', 
				dialog : 'prompt',
				prompt : { 
					placeholder: '输入新的名称',
					value:$(e).parent().contents()[1].nodeValue
				}, 
				buttons:{
					确定:{
						style:'danger',
						action:function($container,$prompt){ 						
								var name=$prompt.val();						
								if(name==''){
									this.shake();
									return false
								}
								else{
									$.ajax({
									type:'POST',
									dataType:'json',
									url:'/ahwaterCloud/ctr/editServer',
									data:{
										serverId:serverid,
										newServerName:name
									},
									success:function(data){
										if(data.msg=='succ'){														        	
						        	$(e).parent().contents()[1].nodeValue=name;
										}
										else{
											new $.flavr({ 
								          content : '修改失败,提示信息:'+data.msg,					          
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
								          timeout : 3500,
								          buttons:{
								            	确定:{}
								          }
						        		});
									}
								})
								return true;
								}
						}
					},
					取消:{}
				}		
			});
		}

	//打开控制台
	$scope.vncControl=function(){		
			$.ajax({
				url:'/ahwaterCloud/ctr/showVNCConsole',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
				},
				success:function(data){	
					console.log('ok')
					window.open(data.msg);
				},
				error:function(){
					new $.flavr({
				    content:'请求失败',
				    autoclose:true,
				    timeout:3000,
				    buttons:{
				       	确定:{}
				    }
				  })
				}
			})
	}


}])



		
		