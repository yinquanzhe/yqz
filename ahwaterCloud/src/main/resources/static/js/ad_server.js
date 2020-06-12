/**
 * Created by zhongyue on 2017/02/08.
 */

app.controller('adServerCtrl',["$scope","$http","$location",function($scope,$http,$location){
		//获取实例列表   
    $scope.getserver=function(){
    	$http.get('/ahwaterCloud/ctr/listAllTenantServers').success(function(serverList){
        $scope.adminServerData=serverList;
        $scope.serverLength=serverList.length;
        $('.cloudlist>table>tbody>tr:first-child').hide();
    	})	
    }
		$scope.getserver();
		
    
		//显示操作系统信息
		$scope.showtoolBar=function($event){
			var e=$event.target;
			$(e).tooltip('show');
		}
			
		//创建快照
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
				        				content:'创建失败,'+data,
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
		
		//暂停实例
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
					    content:'暂停出现错误',
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
		
		//进入实例控制台
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
			var serverid=$(e).attr('sid');			
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
					    content:'挂起出现错误',
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
					    content:'搁置出现错误',
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
		
		//迁移实例
		$scope.migrateEcs=function($event){
			var e=$event.target;					
			var html=`迁移中<b class="fa fa-spinner fa-pulse fa-fw"></b>`;
			$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);
			$.ajax({
				url:'/ahwaterCloud/ctr/migrate',
				type:'get',
				dataType:'text',
				data:{
					serverId:$(e).attr('sid')
				},
				success:function(data){
					if(data=='succ'){
						var html=`<i></i><span>运行中</span>`;
						$(e).parent().parent().parent().parent().parent().prev().prev().attr('class','运行中')
						$(e).parent().parent().parent().parent().parent().prev().prev().children('.ecs-status').html(html);				
						$scope.getserver();
					}
					else{
						new $.flavr({
					    content:'迁移出现错误,'+data,					   
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
		
		//热迁移实例
		$scope.hotmigrateEcs=function($event){
			var e=$event.target;
			
			$("#migEcsModal").modal('show');
			//获取主机列表
			$http.get('/ahwaterCloud/ctr/listComputeServices').success(function(hostList){
				$scope.hostData=hostList;
			})
			
			$("#currentHost").html('当前所在主机:&nbsp;&nbsp;&nbsp;'+$(e).attr('hname'));	
			$scope.migrateAction=function(){
				$("#migEcsModal").modal('hide');
				//console.log($(e).attr('sid'),$('#hostBox').val(),$('#overCheckbox').prop('checked'),$('#blockCheckbox').prop('checked'))
				$.ajax({
					type:"POST",
					url:"/ahwaterCloud/ctr/liveMigrate",
					dataType:'text',
					data:{
						serverId:$(e).attr('sid'),
						host:$('#hostBox').val(),
						enabled:$('#overCheckbox').prop('checked'),
						blocked:$('#blockCheckbox').prop('checked')
					},
					success:function(data){
						if(data=='succ'){
							new $.flavr({
								modal:false,
						    content:'迁移成功',
						    autoclose:true,
						    timeout:3000,
						    buttons:{
						       	确定:{}
						    }
						  })
							$scope.getserver();
						}else{
							new $.flavr({
						    content:'操作失败,'+data,					   
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
										location.reload()
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
												$(e).parent().parent().parent().parent().parent().prev().prev().prev().prev().prev().prev().children('span').children('a').html(sername);
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
			
		//进入详情页
		$scope.serverDetail=function($event){
			var e=$event.target;
			$location.url('/server/'+$(e).attr('sid'))
		}
	
		//批量选择
		$scope.cnt=0;
		console.log($scope.serverLength);
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

