app.controller('ad_oviewCtrl',["$scope","$http",function($scope,$http){
				
	//获取概况信息
	$scope.getOviewByTime=function(start,end){
		var startTime,endTime;
		startTime=start;
		endTime=end;		
		$http({
			method:'GET',
			url:'/ahwaterCloud/contr/ListTenantUsages',
			params:{
				startTime:startTime,
				endTime:endTime
			},
			headers:{'Content-Type':'application/json'}
		}).success(function(oviewList){
				
				for (let j = 0; j < oviewList.length; j++) {
					
					for(let i=0; i<oviewList.length-1-j;i++ ){
						if(oviewList[i+1].vcpu > oviewList[i].vcpu){
							var temp1=oviewList[i+1];
							//var temp2=oviewList[i];
							oviewList[i+1]=oviewList[i];
							oviewList[i]=temp1;
						}	
					}
				}
				console.log(oviewList)
				$scope.oviewListData=oviewList;
				if(oviewList.length==0){
					$('.cloudlist>table>tbody>tr:first-child td').html("暂无数据")
				}else{
					$('.cloudlist>table>tbody>tr:first-child').hide();
				}
		})
	}
	document.getElementById('endTimeov').valueAsDate = new Date();
	var date=new Date();
	var year = date.getFullYear(); 
	var m=date.getMonth()+1
	var month =m<10?'0'+m:m;
	var d=date.getDate();
	var day=d<10?'0'+d:d;
	var nowdate=year+'-'+month+'-'+day;
	var firstdate =year+'-'+month+'-01' ;

	document.getElementById('endTimeov').valueAsDate =date;
	$scope.getOviewByTime(firstdate,nowdate);
	date.setDate(1);

	document.getElementById('startTimeov').valueAsDate =date;
		
	$scope.getOviewByTimeBtn=function(){
		console.log($("#startTimeov").val());
		console.log($("#endTimeov").val());
		$scope.getOviewByTime($("#startTimeov").val(),$("#endTimeov").val());
	}	
			
}])
