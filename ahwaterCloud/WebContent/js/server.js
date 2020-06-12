/**
 * Created by zhongyue on 2016/12/14.
 */

app.controller('shiliCtrl',["$scope","$http","$location",function($scope,$http,$location){
		//获取实例列表   
    $scope.getserver=function(){
    	$http.get('/ahwaterCloud/ctr/listAllServers').success(function(shiliList){
        $scope.shiliData=shiliList;
        $scope.serverLength=shiliList.length;
        $('.cloudlist>table>tbody>tr:first-child').hide();
    	})	
    }
		$scope.getserver();
		
    $('#myTab a').click(function (e) {
        e.preventDefault()
        $(this).tab('show')
    })
    $scope.percent20=function(){
        $scope.percentage=20;
        $('div.actionBtn .progress>div').css({"width":"20%"})
    }
    $scope.percent40=function(){
        $scope.percentage=40;
        $('div.actionBtn .progress>div').css({"width":"40%"})
    }
    $scope.percent60=function(){
        $scope.percentage=60;
        $('div.actionBtn .progress>div').css({"width":"60%"})
    }
    $scope.percent80=function(){
        $scope.percentage=80;
        $('div.actionBtn .progress>div').css({"width":"80%"})
    }
    $scope.percent100=function(){
        $scope.percentage=100;
        $('div.actionBtn .progress>div').css({"width":"100%"})
    }
    
    //创建云主机
    $scope.createHost=function(){
        //获取可用域
        //$http.get('url').success(function(FieldList){
        //    $scope.fieldList=FieldList;
        //    if(FieldList.length==0){
                $scope.fieldList=['nova']
        //    }
        //})

        //获取云主机类型
        $http.get('/ahwaterCloud/ctr/flavorList').success(function(TypeList){
            $scope.typeList=TypeList;
            if(TypeList.length==0){
                $scope.typeList=['暂无云主机类型']
            }
            $.ajax({
				type:"post",
				url:"/ahwaterCloud/ctr/flavorDetails",
				dataType:"json",
				data:{flavorId:TypeList[0].flavorId},
				success:function(data){
					console.dir(data)
					var html=`
					<caption>方案详情</caption>
	        <tr>
	          <th>名称</th>
	          <td>${data[0].name}</td>
	        </tr>
	        <tr>
	          <th>虚拟内核</th>
	          <td>${data[0].vcpus}核</td>
	        </tr>
	        <tr>
	          <th>根磁盘</th>
	          <td>${data[0].disk}GB</td>
	        </tr>
	        <tr>
	          <th>临时磁盘</th>
	          <td>${data[0].ephemeral}GB</td>
	        </tr>
	        <tr>
	          <th>磁盘总计</th>
	          <td>${data[0].disk+data[0].ephemeral}GB</td>
	        </tr>
	        <tr>
	          <th>内存</th>
	          <td>${data[0].ram}MB</td>
	        </tr>`;
        	$('.detail-right table').html(html);
				},
				error:function(){
					$('.detail-right table').html("初始化数据失败");
				}
			})
        })
        //$scope.typeList=['云主机类型1','云主机类型2','云主机类型3','云主机类型4']
        ////获取启动源
        //$http.get('url').success(function(SourceList){
        //    $scope.sourceList=SourceList;
        //    if(SourceList.length==0){
                $scope.sourceList=['镜像','快照']
        //    }
        //})
        //
        ////获取镜像列表
        $http.get('/ahwaterCloud/cont/listImageNameId').success(function(ImageList){
            $scope.imageList=ImageList;
            if(ImageList.length==0){
                $scope.imageList=['无可用镜像']
            }
        })
        //
        ////获取快照列表
        //$http.get('url').success(function(SnapshotList){
        //    $scope.snapshotList=SnapshotList;
        //    if(SnapshotList.length==0){
                $scope.snapshotList=['无可用快照']
        //    }
        //})
        //
        ////获取云硬盘列表
        //$http.get('url').success(function(DiskList){
        //    $scope.diskList=DiskList;
        //    if(DiskList.length==0){
                $scope.diskList=['无可用云硬盘']
        //    }
        //})
        //获取可用网络
        $http.get('/ahwaterCloud/cont/listNetworkNameId').success(function(IpList){
            $scope.ipList=IpList;
            if(IpList.length==0){
                $scope.ipList=['暂无空闲ip']
            }
        })
        
        //获取密钥对
        $http.get('/ahwaterCloud/ctr/keypairsList').success(function(KeyList){
            $scope.keyList=KeyList;
            if(KeyList.length==0){
                $scope.keyList=['无可用密钥对']
            }
        })
        
        $http.get('/ahwaterCloud/ctr/securityGroupsList').success(function(SecurityList){
        	$scope.securityList=SecurityList
        	if(SecurityList.length==0){
                $scope.securityList=['无安全组']
            }
        })
        
        
        //初始化信息
        $("#goAction").attr("disabled",true)
        
        
    }
    
    //启动源onchange事件
    $('.detail-left form .imageBox').show().nextAll().hide();
    $('#startSrc').change(function(){
        var option=this.options[this.options.selectedIndex];
        var k=parseInt($(option).attr('tag'));
        if(k==0){
            $('.detail-left form .imageBox').show().nextAll().hide();
        }else if(k==1){
            $('.detail-left form .quickShotBox').show().nextAll().hide();
            $('.detail-left form .quickShotBox').prev().hide();
        }
    })
		
		//云主机类型的Onchange事件
		$('#serverType').change(function(){
			$.ajax({
				type:"post",
				url:"/ahwaterCloud/ctr/flavorDetails",
				dataType:"json",
				data:{flavorId:$(this).val()},
				success:function(data){
					var html=`
					<caption>方案详情</caption>
	        <tr>
	          <th>名称</th>
	          <td>${data[0].name}</td>
	        </tr>
	        <tr>
	          <th>虚拟内核</th>
	          <td>${data[0].vcpus}核</td>
	        </tr>
	        <tr>
	          <th>根磁盘</th>
	          <td>${data[0].disk}GB</td>
	        </tr>
	        <tr>
	          <th>临时磁盘</th>
	          <td>${data[0].ephemeral}GB</td>
	        </tr>
	        <tr>
	          <th>磁盘总计</th>
	          <td>${data[0].disk+data[0].ephemeral}GB</td>
	        </tr>
	        <tr>
	          <th>内存</th>
	          <td>${data[0].ram}MB</td>
	        </tr>`;
        	$('.detail-right table').html(html);
				},
				error:function(){
					$('.detail-right table').html("获取数据失败");
				}
			})
		})
		
		//显示操作系统信息
		$scope.showtoolBar=function($event){
			var e=$event.target;
			$(e).tooltip('show');
		}
			
		//开始创建
    $scope.goAction=function(){
    	$("#startCloud").modal('hide');
    	/////获取选择的网络
    	var netobj=document.getElementsByName('netWorkItem')
    	var netWorkArr=[];
    	for(j in netobj){
    		if(netobj[j].checked){
    			netWorkArr.push(netobj[j].value)
    		}
    	} 	
    	var newCnt=parseInt($("#serverCount").val());
    	console.log(newCnt);
    		var newserver={"serverName":$("#serverName").val(),
										"imageName":$("#imgName option:selected").text(),
										"ipAddr":['分配中'],
										"vcpus":'-',
										"ram":'-',
										"disk":'-',
										"status":"创建中",
										"timeFromCreated":"0分钟"};		  	
    	for(var i=0;i<newCnt;i++){
    		$scope.shiliData.push(newserver);
    	}
    	
    	var newNetwork=JSON.stringify(netWorkArr);
    	//获取选择的安全组
    	var SecurityVal=$("#securityGroup input[name='securityItem']:checked").val();   	
    	var Security=SecurityVal===undefined?null:SecurityVal;  	
    	//获取选择的密钥对
    	var keycode=$("#codeKey").val()=="无可用密钥对"?'':$("#codeKey").val();  	   	  	
        $.ajax({
            url:'/ahwaterCloud/ctr/createServer',
            type:'POST',
            dataType:'json',
            data:{
            	HostField:$('#availableField').val(),
            	serverName:$('#serverName').val(),
               "flavorId":$('#serverType').val(),
               "number":$('#serverCount').val(),
               HostStartSrc:$('#startSrc').val(),
               imageId:$('#imgName').val(),
               HostShot:$('#quickShot').val(),
               keypairsName:keycode,
               securityGroupName:Security,
               networksStr:newNetwork
           },
           success:function(data){
            	console.log(data);
            	var succCnt=0;
            	var failedArr=[];            	
            	for(var i=0;i<data.length;i++){
            		if(data[i].msg=='succ'){
            			//$scope.shiliData.push(data[i]);            			
            			succCnt++;
            		}
            		else{
            			failedArr.push(data[i]);
            		}
            	}
            	$scope.getserver();
            	new $.flavr({ 
            		content : '成功创建'+succCnt+'个实例,'+'失败:'+failedArr.length+'个',               
                buttons:{
                	确定:{
                		//action:function(){location.reload()}
                	}
                }            
            	});	           	        
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
      }

		/*创建快照*/
		$scope.createFastShot=function($event){
			var e=$event.target;
			//console.log($(e));
			var serverid=$(e).parent().parent().children('.mysid').children('input').attr('sid');
			//console.log(serverid);
			new $.flavr({
			    content: '快照是保存运行中实例的磁盘状态的一个镜像',
			    dialog: 'prompt',
			    prompt: { placeholder: '请输入快照名称'},
			    buttons:{ //定制按钮
			    	创建:{
			    		style:'danger',
			    		action:function( $container, $prompt ){		    			
				        if($prompt.val()==''){ //如果输入为空
				        	this.shake();//晃动框提示
				        	return false;
				        }
				        else{ //否则
				        	$.ajax({
				        		type:"POST",
				        		url:"/ahwaterCloud/ctr/createSnapShot",
				        		dataType:'json',
				        		data:{
				        			serverId:serverid,
				        			snapName:$prompt.val()
				        		},
				        		success:function(data){ //请求成功
				        			//创建成功or失败
				        			if(data.msg=='succ'){
				        				new $.flavr({
				        				content:'创建成功',
				        				autoclose:true,
				        				timeout:3000,
				        				buttons:{
				        					确定:{}
				        				}
				        				})
				        			}
				        			else{
				        				new $.flavr({
				        				content:'创建失败',
				        				autoclose:true,
				        				timeout:3000,
				        				buttons:{
				        					确定:{}
				        				}
				        				})
				        			}
				        		},
				        		error:function(){  //请求失败
				        			new $.flavr({
				        				content:'请求失败',
				        				autoclose:true,
				        				timeout:3000,
				        				buttons:{
				        					确定:{}
				        				}
				        			})
				        		}
				        	});
				        	return true
				       }
				    	}
			    	},
			    	取消:{}
			    }
			    
			});
		}
		
		/*暂停实例*/
		$scope.pauseServer=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).parent().parent().children('.mysid').children('input').attr('sid');
			console.log(serverid);
			var html=`暂停中 <b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().prev().prev().children('.ecs-status').html(html);
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"pause"
				},
				success:function(data){
					if(data.msg=='succ'){
						var html=`<i></i><span>暂停</span>`;
						$(e).parent().prev().prev().attr('class','暂停')
						$(e).parent().prev().prev().children('.ecs-status').html(html);					
						$scope.getserver()
					}
					else{
						new $.flavr({
					    content:'暂停出现错误,'+data.msg,
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
		
		/*进入实例控制台*/
		$scope.VNCConsole=function($event){		
			var e=$event.target;
			var serverid=$(e).parent().parent().parent().parent().parent().parent().children('.mysid').children('input').attr('sid');
			console.log(serverid);
			$.ajax({
				url:'/ahwaterCloud/ctr/showVNCConsole',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
				},
				success:function(data){				
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
		
		//挂起实例
		$scope.suspendEcs=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			console.log(serverid);
			var html=`挂起中<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"suspend"
				},
				success:function(data){
					if(data.msg=='succ'){
						var html=`<i></i><span>挂起</span>`;
						$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','挂起')
						$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);					
						
						$scope.getserver();
					}
					else{
						new $.flavr({
					    content:'挂起出现错误,'+data.msg,
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
		
		//恢复实例
		$scope.resumeEcs=function($event,action){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			console.log(serverid);
			var html=`恢复中<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().prev().prev().children('.ecs-status').html(html);
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
						var html=`<i></i><span>运行中</span>`;
						$(e).parent().prev().prev().attr('class','运行中')
						$(e).parent().prev().prev().children('.ecs-status').html(html);
						
						$scope.getserver();
					}
					else{
						new $.flavr({
					    content:'出现错误,'+data.msg,
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

		//废弃
		$scope.shelveEcs=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			console.log(serverid);
			var html=`搁置中<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"shelve"
				},
				success:function(data){
					if(data.msg=='succ'){
						var html=`<i></i><span>卸载搁置</span>`;
						$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','卸载搁置')
						$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
						
						$scope.getserver();
					}
					else{
						new $.flavr({
					    content:'搁置出现错误,'+data.msg,
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
		
		//锁定实例		
		$scope.lockEcs=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			console.log(serverid);
			var html=`锁定中<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"lock"
				},
				success:function(data){
					if(data.msg=='succ'){
						var html=`<i></i><span>运行中</span>`;
						$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','运行中')
						$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
						new $.flavr({
					    content:'已锁定',
					    modal:false,
					    autoclose:true,
					    timeout:3000,
					    buttons:{
					       	确定:{}
					    }
				  	})
						$scope.getserver();
					}
					else{
						new $.flavr({
					    content:'锁定出现错误,'+data.msg,
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
		
		//解锁实例
		$scope.unlockEcs=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			console.log(serverid);
			var html=`解除锁定<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"unLock"
				},
				success:function(data){
					if(data.msg=='succ'){
						var html=`<i></i><span>运行中</span>`;
						$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','运行中')
						$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
						$scope.getserver();
					}
					else{
						new $.flavr({
					    content:'出现错误,'+data.msg,
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
		
		//软重启实例
		$scope.softRebootEcs=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			var html=`正在重启<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
			console.log(serverid);		
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"softReBootServer"
				},
				success:function(data){
					if(data.msg=='succ'){
						var html=`<i></i><span>运行中</span>`;
						$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','运行中')
						$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
						$scope.getserver();
						//location.reload();
						
					}
					else{
						new $.flavr({
					    content:'出现错误,'+data.msg,
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
		
		//硬重启实例
		$scope.hardRebootEcs=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			var html=`正在启动<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
			console.log(serverid);	
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"hardRebootServer"
				},
				success:function(data){
					if(data.msg=='succ'){
						var html=`<i></i><span>运行中</span>`;
						$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','运行中')
						$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
						//location.reload();
						$scope.getserver();
					}
					else{
						new $.flavr({
					    content:'出现错误,'+data.msg,
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
		
		//关闭实例
		$scope.stopEcs=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			console.log(serverid);
			var html=`停止中<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
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
						var html=`<i></i><span>关机</span>`;
						$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','关机')
						$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
						
						$scope.getserver();
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
		
		//删除实例
		$scope.deleteEcs=function($event){
			var e=$event.target;		
			var serverid=$(e).attr('sid');
			new $.flavr({
				content:'确定删除该实例?',
				dialog:'confirm',
				buttons:{
	       	确定:{
	       		style:'danger',
	       		action:function(){
	       			var html=`正在删除<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
							$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
							$.ajax({
								url:'/ahwaterCloud/ctr/serverAction',
								type:'get',
								dataType:'json',
								data:{
									serverId:serverid,
									actionName:"deleteServer"
								},
								success:function(data){
									if(data.msg=='succ'){						
										location.reload();
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
	       	},
	       	取消:{}
				}
			})
			
		}
			
		//取消废弃
		$scope.unShelveEcs=function($event){
			var e=$event.target;
			console.log($(e));
			var serverid=$(e).attr('sid');
			var html=`正在重新初始化<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().prev().prev().children('.ecs-status').html(html);
			console.log(serverid);		
			$.ajax({
				url:'/ahwaterCloud/ctr/serverAction',
				type:'get',
				dataType:'json',
				data:{
					serverId:serverid,
					actionName:"unShelve"
				},
				success:function(data){
					if(data.msg=='succ'){
						
						var html=`<i></i><span>运行中</span>`;
						$(e).parent().prev().prev().attr('class','运行中')
						$(e).parent().prev().prev().children('.ecs-status').html(html);
						$scope.getserver();
						
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
				
		//编辑云主机
		$scope.editEcs=function($event){
			var e=$event.target;
			var serid=$(e).attr('sid')
			var sername=$(e).attr("sname")
			var html=`
				<form>
					<div class="form-row">
					<input class="form-control" style="color:#666" name="newServerName" value=${sername} type="text" />
					<input name="serverId" type="hidden" value=${serid} />
					</div>					
				</form>
				`;
		
			new $.flavr({
				content : '修改云主机名称', 
				dialog : 'form', 
				form : { 
					content: html, 
					method:'post'
				}, 
				buttons:{
					保存:{
						style:'danger',
						action:function( $container, $form ){ 
									console.log($form.serialize());									
									$.ajax({
										type:"POST",
										url:"/ahwaterCloud/ctr/editServer",
										dataType:'json',
										data:$form.serialize(),
										success:function(data){
											if(data.msg=='succ'){
												$(e).parent().parent().parent().parent().parent().siblings('.myServerName').children('span').children('a').html(sername);
												$scope.getserver();
											}else{
												new $.flavr({
													content:'修改失败,'+data.msg,
													buttons:{确定:{}}
												})
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
									});
									return true;
								}
					},
					取消:{}
				}
			});
			
		}
		
		//调整云主机大小
		$scope.adjustEcs=function($event){
			var e=$event.target;
			$("#adjustEcsModal").modal('show');
			$http.get('/ahwaterCloud/ctr/flavorList').success(function(typeList){
				$scope.typeChangeList=typeList;
			})
			console.log($(e).attr('fname'))
			$("#oldServertype").html('当前使用类型:&nbsp;&nbsp;&nbsp;'+$(e).attr('fname'));	
			$scope.adjustAction=function(){
				$("#adjustEcsModal").modal('hide');
				var html=`正在调整<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
				$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
				$.ajax({
					type:"POST",
					url:"/ahwaterCloud/ctr/resizeServer",
					dataType:'json',
					data:{
						serverId:$(e).attr('sid'),
						flavorId:$('#typeChangeBox').val()
					},
					success:function(data){
						if(data.msg=='succ'){
							var html=`<i></i><span>运行中</span>`;
							$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','运行中')
							$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
							$scope.getserver();
						}else{
							new $.flavr({
						    content:'操作失败,提示信息:'+data.msg,
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
				});
			}
		}

		//重建云主机
		$scope.rebuiltEcs=function($event){
			var e=$event.target;
			$("#rebuiltEcsModal").modal('show');
			$http.get('/ahwaterCloud/cont/listImageNameId').success(function(ImageList){
				if(ImageList.length==0){
            $scope.imageChangeList=['无可用镜像']
        }
        $scope.imageChangeList=ImageList;
      })
			$scope.rebuiltAction=function(){
				$("#rebuiltEcsModal").modal('hide');
				var html=`开始重建<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
				$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
				$.ajax({
					type:"POST",
					url:"/ahwaterCloud/ctr/reBuild",
					dataType:'json',
					data:{
						serverId:$(e).attr('sid'),
						imageId:$('#imageChangeBox').val()
					},
					success:function(data){
						if(data.msg=='succ'){
							var html=`<i></i><span>运行中</span>`;
							$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','运行中')
							$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
							$scope.getserver();
						}else{
							new $.flavr({
						    content:'操作失败,提示信息:'+data.msg,
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
				});
			}
		}
		
		//编辑安全组
		$scope.editSeGroup=function($event){
			var e=$event.target;
			$("#editSeGroupModal").modal('show');			
			$http({
				method :'POST',
				url:'/ahwaterCloud/contr/ListServerGroups',
				params :{serverId:$(e).attr('sid')},
				headers : {'Content-Type':'application/json'}
			}).success(function(ServerGroups){
				if(ServerGroups.length==0){
						$scope.allGroupList=$scope.existGroupList=[];
					}else{
						$scope.existGroupList=ServerGroups[0];
						$scope.allGroupList=ServerGroups[1];					
					}					
			})
			console.log($(e).attr('sid'))
			$scope.editSeGroupAction=function(){
				$("#editSeGroupModal").modal('hide');
				console.log($(e).attr('sid'))
				var seGroupArr=[];
				var inputs=$("#secGroupBox input");
				for(var i=0;i<inputs.length;i++){
					if($(inputs[i]).prop('checked')){
						seGroupArr.push($(inputs[i]).val())
					}
				}
				console.log(seGroupArr);
				$.ajax({
					type:'get',
					url:'/ahwaterCloud/ctr/editSecurityGroups',
					dataType:'json',
					data:{
						serverId:$(e).attr('sid'),
						newSecGroupNames:JSON.stringify(seGroupArr)
					},
					success:function(data){
						console.log(data);
						if(data.msg=='succ'){
							new $.flavr({
						    content:'修改成功',
						    autoclose:true,
						    timeout:3000,
						    buttons:{
						       	确定:{}
						    }
						  })
						}else{
							new $.flavr({
						    content:'修改失败,提示信息:'+data.msg,
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
		}
		
		//绑定浮动IP
		$scope.bindFloatIP=function($event){
			var e=$event.target;
			$("#bindFloatIPModal").modal('show');
			$http.get('/ahwaterCloud/contr/ListAllFloatingIp').success(function(floatIPList){
				if(floatIPList.length==0){
					$scope.IPListBox=['没有分配浮动IP']
				}else{
					$scope.IPListBox=floatIPList;
				}
			})
			$scope.bindIPAction=function(){
				$("#bindFloatIPModal").modal('hide');
				$.ajax({
					type:"post",
					url:"url",
					dataType:'json',
					data:{
						serverId:$(e).attr("sid"),
						ipId:$("#avaIPBox").val()
					},
					success:function(data){
						console.log(data)
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
				});
			}
		}
		
		//进入详情页
		$scope.serverDetail=function($event){
			var e=$event.target;
			$location.url('/server/'+$(e).attr('sid'))
		}
		
		
		//批量选择
		$scope.cnt=0;
    $('.actionBtn>button.deleteBatch').attr('disabled',true);
    $('.cloudlist>table>thead>tr>th>input').click(function(){
        $scope.canAction=false;
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.cnt=$scope.serverLength;
            $('.actionBtn>button.deleteBatch').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.cnt=0
            $('.actionBtn>button.deleteBatch').attr('disabled',true);
        }
    })
    //单个选择
    $scope.choose=function($event){
        var input=$event.target;
        if($(input).prop('checked')==true){
            $(input).prop('checked',true);
            $scope.cnt++;
            if($scope.cnt==$scope.serverLength){
                $('.cloudlist>table>thead>tr>th>input').prop('checked',true);
            }
        }else{
            $(input).prop('checked',false);
            $scope.cnt--;
            $('.cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.cnt>0){$('.actionBtn>button.deleteBatch').attr('disabled',false);}
        else{$('.actionBtn>button.deleteBatch').attr('disabled',true);}
    }
    
    //批量删除
    $scope.deleteBatch=function(){
    	var inputs=$('.cloudlist>table>tbody input[type="checkbox"]');		
			var delServerId=[];
			for(k in inputs){
				if(inputs[k].checked){
					delServerId.push($(inputs[k]).attr('sid'))
				}
			}
			new $.flavr({
				content:'确定删除选中的'+delServerId.length+'个实例？',
				dialog:'confirm',
				buttons:{
					确定:{
						style:'danger',
						action:function(){
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/ctr/serverBatchDelete",
								dataType:'json',
								data:{
									serverIdStr:JSON.stringify(delServerId)
									},
								success:function(data){
									console.log(data)
									if(data.msg=='succ'){
										location.reload()
									}
								},
								error:function(){
									new $.flavr({
										content:'请求失败',
										autoclose:true,
										timeout:3000,
										buttons:{确定:{}}
									})
								}
							});
						}
					},
					取消:{}
				}
			});
    }

		//TODO:搜索实例
		$scope.searchServer=function($event){
			var e=$event.target;
			var txt=$(e).prev().val();
			if(txt==''){
				$(e).prev()[0].focus();
			}
			else{
				$http({
					method:'Get',
					params:{
						searchTxt:txt
					},
					url:'url',
					headers : {'Content-Type':'application/json'}
				}).success(function(dataList){
					$scope.shiliData=dataList;
				})
			}
		}
	

}])
		//检查输入
		function CheckServerName(e){
			if($(e).val()==""){
				$(e).parent().addClass("has-error");				
			}else{
				$(e).parent().removeClass("has-error");
				$("#goAction").attr("disabled",false)
			}
		}

