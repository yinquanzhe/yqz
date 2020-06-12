app.controller("overviewCtrl",["$scope","$http",function($scope,$http){
	
	//获取配额信息
	$scope.getoverview=function(){
		
		$http.get('/ahwaterCloud/contr/GetUsageAndLimit').success(function(LimitList){
			$scope.limitData=LimitList;
			
			
		
			var usedServer=$scope.limitData.usedInstances;
			var totalServer=$scope.limitData.maxInstances;
				
		//实例
			var ecsChart=echarts.init(document.getElementById('serverView'));
  		var ecsoption={		
  			title: {
  				text: '实例',
      		subtext:'总共 '+totalServer,
	        x: 'left',
	        textStyle: {
	            fontSize: 16,
	            //fontWeight: 'bolder',
	            color: '#555'          // 主标题文字颜色
	        },
	        subtextStyle: {
	            color: '#333'         // 副标题文字颜色
	        }
    	},    
			series:[
	  	{
	  		
	      name:'实例',
	      type:'pie',
	      radius: ['25%','50%'],
	      avoidLabelOverlap: false,
	      data:[
	          {value:usedServer/totalServer,name:'已用'+usedServer,
	          	itemStyle:{normal:{color:"#2F4554"}}
	          },
	          {value:1-usedServer/totalServer,name:'剩余'+(totalServer-usedServer),
	          	itemStyle:{normal:{color:"#009795"}}
	          }
	      ]
	  	}
			]
	};
  		ecsChart.setOption(ecsoption);
   
  //虚拟内核
  var usedCpu=$scope.limitData.usedCores;
  var totalCpu=$scope.limitData.maxCores;
  var cpuChart=echarts.init(document.getElementById('cpuView'));
  var cpuoption={
    title:{
      text: '虚拟内核',      
      subtext:'总共 '+totalCpu+'个',
      x: 'left',                
      textStyle: {
          fontSize: 16,
         // fontWeight: 'bolder',
          color: '#555'          // 主标题文字颜色
      },
      subtextStyle: {
          color: '#333'          // 副标题文字颜色
      }
    },
		series:[
  	{
      name:'虚拟内核',
      type:'pie',
      radius: ['25%','50%'],
      avoidLabelOverlap: false, 
      data:[
          {value:usedCpu/totalCpu, name:'已用'+usedCpu+'个',
          	itemStyle:{normal:{color:"#D53A35"}}
          },
          {value:1-usedCpu/totalCpu, name:'剩余'+(totalCpu-usedCpu)+'个',
          	itemStyle:{normal:{color:"#009795"}}
          }
      ]
  	}
		]
	};
  cpuChart.setOption(cpuoption);
  
  //内存
  var usedMom=$scope.limitData.usedRAMSize;
  var totalMom=$scope.limitData.maxRAMSize;
  var memChart=echarts.init(document.getElementById('memoryView'));
  var memoption={
    title:{
      text: '内存',
      subtext:'总共 '+totalMom+'M',
      x: 'left',                
      textStyle: {
          fontSize: 16,
         // fontWeight: 'bolder',
          color: '#555'          // 主标题文字颜色
      },
      subtextStyle: {
          color: '#333'          // 副标题文字颜色
      }
    },
		series:[
  	{
      name:'内存',
      type:'pie',
      radius: ['25%','50%'],
      avoidLabelOverlap: false, 
      data:[
          {value:usedMom/totalMom, name:'已用 '+usedMom+'M',
          	itemStyle:{normal:{color:"#D39C0B"}}
          },
          {value:1-usedMom/totalMom, name:'剩余 '+(totalMom-usedMom)+'M',
          	itemStyle:{normal:{color:"#009795"}}
          }
      ]
  	}
		]
	};
  memChart.setOption(memoption);
  
  //浮动IP
  var usedIp=$scope.limitData.usedFloatingIPs;
  var totalIp=$scope.limitData.maxFloatingIPs;
  var ipChart=echarts.init(document.getElementById('ipView'));
  var ipoption={
    title:{
      text: '浮动IP',
      subtext:'总共 '+totalIp+'个',
      x: 'left',                
      textStyle: {
          fontSize: 16,
          //fontWeight: 'bolder',
          color: '#555'          // 主标题文字颜色
      },
      subtextStyle: {
          color: '#333'          // 副标题文字颜色
      }
    },
		series:[
  	{
      name:'浮动IP',
      type:'pie',
      radius: ['25%','50%'],
      avoidLabelOverlap: false, 
      data:[
          {value:usedIp/totalIp, name:'已用 '+usedIp+'个',
          	itemStyle:{normal:{color:"#746F2E"}}
          },
          {value:1-usedIp/totalIp, name:'剩余 '+(totalIp-usedIp)+'个',
          	itemStyle:{normal:{color:"#009795"}}
          }
      ]
  	}
		]
	};
  ipChart.setOption(ipoption);
  
  //安全组
  var usedSe=$scope.limitData.usedSecurityGroups;
  var totalSe=$scope.limitData.maxSecurityGroups;
  var secuChart=echarts.init(document.getElementById('securityView'));
  var secuoption={
    title:{
      text: '安全组',
      subtext:'总共 '+totalSe+'个',
      x: 'left',                
      textStyle: {
          fontSize: 16,
         // fontWeight: 'bolder',
          color: '#555'          // 主标题文字颜色
      },
      subtextStyle: {
          color: '#333'          // 副标题文字颜色
      }
    },
		series:[
  	{
      name:'安全组',
      type:'pie',
      radius: ['25%','50%'],
      avoidLabelOverlap: false, 
      data:[
          {value:usedSe/totalSe, name:'已用 '+usedSe+'个',
          	itemStyle:{normal:{color:"#C14B0A"}}
          },
          {value:1-usedSe/totalSe, name:'剩余 '+(totalSe-usedSe)+'个',
          	itemStyle:{normal:{color:"#009795"}}
          }
      ]
  	}
		]
	};
  secuChart.setOption(secuoption);
  
  //云硬盘
  var usedVol=$scope.limitData.usedVolumes;
  var totalVol=$scope.limitData.maxVolumes;
  var volChart=echarts.init(document.getElementById('volumeView'));
  var voloption={
    title:{
      text: '云硬盘',
      subtext:'总共 '+totalVol+'个',
      x: 'left',                
      textStyle: {
          fontSize: 16,
          //fontWeight: 'bolder',
          color: '#555'          // 主标题文字颜色
      },
      subtextStyle: {
          color: '#333'          // 副标题文字颜色
      }
    },
		series:[
  	{
      name:'云硬盘',
      type:'pie',
      radius: ['25%','50%'],
      avoidLabelOverlap: false, 
      data:[
          {value:usedVol/totalVol, name:'已用 '+usedVol+'个',
          	itemStyle:{normal:{color:"#0A6C93"}}
          },
          {value:1-usedVol/totalVol, name:'剩余 '+(totalVol-usedVol)+'个',
          	itemStyle:{normal:{color:"#009795"}}
          }
      ]
  	}
		]
	};
  volChart.setOption(voloption);
  
  
  //卷存储
  var usedVolSize=$scope.limitData.usedVolumeSize;
  var totalVolSize=$scope.limitData.maxVolumeSize;
  var volSizeChart=echarts.init(document.getElementById('diskView'));
  var volSizeoption={
    title:{
      text: '存储空间',
      subtext:'总共 '+totalVolSize+'GB',
      x: 'left',                
      textStyle: {
          fontSize: 16,
          //fontWeight: 'bolder',
          color: '#555'          // 主标题文字颜色
      },
      subtextStyle: {
          color: '#333'          // 副标题文字颜色
      }
    },
		series:[
  	{
      name:'卷存储',
      type:'pie',
      radius: ['25%','50%'],
      avoidLabelOverlap: false, 
      data:[
          {value:usedVolSize/totalVolSize, name:'已用 '+usedVolSize+'G',
          	itemStyle:{normal:{color:"#A2BC08"}}
          },
          {value:1-usedVolSize/totalVolSize, name:'剩余 '+(totalVolSize-usedVolSize)+'G',
          	itemStyle:{normal:{color:"#009795"}}
          }
      ]
  	}
		]
	};
  volSizeChart.setOption(volSizeoption);
  
  
  })
	}
	
	$scope.getoverview();
	
	
	$scope.getServerView=function(start,end){
		var startTime,endTime;
		startTime=start;
		endTime=end;		
		$http({
			method:'GET',
			url:'/ahwaterCloud/contr/ListServersByTime',
			params:{
				startTime:startTime,
				endTime:endTime
			},
			headers:{'Content-Type':'application/json'}
		}).success(function(serverList){
				$scope.serverList=serverList;
				$('.cloudlist>table>tbody>tr:first-child').hide();
		})
	}
	document.getElementById('endTimeipt').valueAsDate = new Date();
	var date=new Date();
	var year = date.getFullYear(); 
	var m=date.getMonth()+1
	var month =m<10?'0'+m:m;
	var d=date.getDate();
	var day=d<10?'0'+d:d;
	var nowdate=year+'-'+month+'-'+day;
	var firstdate =year+'-'+month+'-01' ;

	document.getElementById('endTimeipt').valueAsDate =date;
	$scope.getServerView(firstdate,nowdate);
	date.setDate(1);

	document.getElementById('startTimeipt').valueAsDate =date;
		
	$scope.searchByTime=function(){
		console.log($("#startTimeipt").val());
		console.log($("#endTimeipt").val());
		$scope.getServerView($("#startTimeipt").val(),$("#endTimeipt").val());
	}
	
	
	//收放饼图
	$scope.togWidget=function($event){
		var e=$event.target;
		if($(e).hasClass('glyphicon-chevron-down')){
			$(e).removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
			$('.orange .widget-body').css({
				height:'0'
			})
		}else{
			$(e).removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
			$('.orange .widget-body').css({
				height:'480px'
			})
		}
	}
	
}])