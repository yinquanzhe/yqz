app.controller('volumeCtrl',['$scope','$http',function($scope,$http){
	//获取云硬盘列表
	$scope.getVolume=function(){
		$http.get('/ahwaterCloud/cont/listAllVolumes').success(function(VolumeList){
			$scope.volumeData=VolumeList;
			$scope.volumeLength=VolumeList.length;
			$('#volumeList .cloudlist>table>tbody>tr:first-child').hide();
		})
	}
	$scope.getVolume();
	
	//获取云硬盘快照(需在点击事件里执行)
	$scope.getVolumeShot=function(){
		$http.get('/ahwaterCloud/cont/listAllVolumeSnapshot').success(function(VolumeShotList){
			$scope.volomeShotData=VolumeShotList;
			$scope.volShotLength=VolumeShotList.length;
			$('#snapShotList .cloudlist>table>tbody>tr:first-child').hide();
		})
	}
	//$scope.getVolumeShot();
	
	
	/*更改启动源*/
	$(".volModal .imgBox").hide();
	$(".volModal .shotBox").hide();
	$(".volModal .volBox").hide();
	$("#volSrc").change(function(){
	var option=this.options[this.options.selectedIndex];
	var k=parseInt($(option).attr('tag'));
	if(k==1){
		$(".volModal .imgBox").hide();
		$(".volModal .shotBox").hide();
		$(".volModal .volBox").hide();
		$(".volModal .volTypeBox").show();
	}else if(k==2){
		$(".volModal .imgBox").hide();
		$(".volModal .volTypeBox").hide();
		$(".volModal .volBox").hide();
		$(".volModal .shotBox").show();
	}else if(k==3){
		$(".volModal .shotBox").hide();
		$(".volModal .volBox").hide();
		$(".volModal .volTypeBox").show();
		$(".volModal .imgBox").show();
	}else if(k==4){
		$(".volModal .imgBox").hide();
		$(".volModal .volTypeBox").hide();
		$(".volModal .shotBox").hide();
		$(".volModal .volBox").show();
	}
})
	
	/*添加动画*/
	$scope.createVolume=function(){
		$(".volModal .modal-content").removeClass("fadeOutUp").addClass("pulse")
		$http.get('/ahwaterCloud/cont/listImageNameId').success(function(imglist){
			$scope.imageListData=imglist
		})
		$http.get('/ahwaterCloud/cont/listVolumesNameIdSize').success(function(imgVollist){
			$scope.imageVolData=imgVollist
		})
		$http.get('/ahwaterCloud/cont/listVolumeSnapshotsNameIdSize').success(function(imgShotlist){
			$scope.imageShotData=imgShotlist
		})
	}
	/*取消*/
	$scope.cancelVolBtn=function(){
		$(".volModal .modal-content").removeClass("pulse").addClass("fadeOutUp");
		//$("#createVolModal").modal("hide");
		setTimeout(function(){
			$("#createVolModal").modal("hide");
		},600)
	}
	/*标签页切换*/
	$('#volumeListTab a').click(function(e){
		e.preventDefault();
		//console.log($(e.target).attr('href').slice(1));
		$(this).tab('show');
		if($(e.target).attr('href').slice(1)=='volumeList'){
			$scope.getVolume();
		}else{
			$scope.getVolumeShot();
		}
	})

	/*创建云硬盘*/
	$scope.createVolBtn=function(){
		$(".volModal .modal-content").removeClass("pulse").addClass("fadeOutUp");
		setTimeout(function(){
			$("#createVolModal").modal("hide");
		},600)
		
		var k=$("#volSrc option:selected").attr('tag')
		console.log(k)
		if(k==1){
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/cont/createVolume",
				dataType:'json',
				data:{
					volumeName:$("#volName").val(),
					volumeDescription:$("#volDesc").val(),
					volumSize:$("#volSize").val()
				},
				success:function(data){					
					if(data=="success"){					
	        	$scope.getVolume();
					}
					else{
						new $.flavr({ 
	          content : '创建失败！',         
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
			});
		}else if(k==2){
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/cont/createVolumefromVolumeSnapshot",
				dataType:'json',
				data:{
					volumeName:$("#volName").val(),
					volumeDescription:$("#volDesc").val(),
					volumSize:$("#volSize").val(),
					volumeSnapshotId:$('#quickShot').val()
				},
				success:function(data){					
					if(data=="success"){					
	        	$scope.getVolume();
					}
					else{
						new $.flavr({ 
	          content : '创建失败！',         
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
			});
		}else if(k==3){
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/cont/createVolumefromImage",
				dataType:'json',
				data:{
					volumeName:$("#volName").val(),
					volumeDescription:$("#volDesc").val(),
					volumSize:$("#volSize").val(),
					imageId:$("#volImg").val()
				},
				success:function(data){					
					if(data=="success"){					
	        	$scope.getVolume();
					}
					else{
						new $.flavr({ 
	          content : '创建失败！',         
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
			});
		}else if(k==4){
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/cont/createVolumefromVolume",
				dataType:'json',
				data:{
					volumeName:$("#volName").val(),
					volumeDescription:$("#volDesc").val(),
					volumSize:$("#volSize").val(),
					volumeId:$("#volVol").val()
				},
				success:function(data){					
					if(data=="success"){					
	        	$scope.getVolume();
					}
					else{
						new $.flavr({ 
	          content : '创建失败！',         
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
			});
		}
	}

	/*编辑*/
	$scope.editVol=function($event){
		var e=$event.target;
		var volid=$(e).attr('vid');
		var name=$(e).attr('vname')
		var desc=$(e).attr('vdesc')
		var html=`
		<form>
			<div class="form-row">
			<input class="form-control" style="color:#333" name="volumeName" value=${name} type="text" />
			<input name="volumeId" type="hidden" value=${volid} />
			</div>
			<div class="form-row">
				<textarea class="form-control"  name="volumeDescription" style="resize: none;color:#333;padding-left:15px" rows="3" cols="30" >${desc}</textarea>
			</div>
		</form>
		`;
		
		new $.flavr({
			title : '编辑', 
			content : '修改云硬盘名称和描述', 
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
									url:"/ahwaterCloud/cont/editVolume",
									dataType:'json',
									data:$form.serialize(),
									success:function(data){
										if(data=='success'){
											$scope.getVolume();
										}else{
											new $.flavr({
												content:'修改失败,'+data,
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

	/*创建快照*/
	$scope.createShot=function($event){
		var e=$event.target;
		var html=`
		<form>
			<div class="form-row">
				<input type="hidden" name="volumeId" value=${$(e).attr('vid')} />
				<input class="form-control" style="color:#333" name="volumeSnapshotName" type="text" placeholder="请输入快照名称"/>
			</div>
			<div class="form-row">
				<textarea class="form-control" name="volumeSnapshotDescription" style="resize: none;color:#333;padding-left:15px" rows="3" cols="30" placeholder="快照描述内容"></textarea>
			</div>
		</form>
		`;
		
		new $.flavr({
			title : '创建快照', 
			content : '卷是可以被附属连接到实例的块设备。', 
			dialog : 'form', 
			form : { 
				content: html, 
				method:'post'
			}, 
			buttons:{
				创建:{
					style:'danger',
					action:function( $container, $form ){ 
								$.ajax({
									type:"POST",
									url:"/ahwaterCloud/cont/createVolumeSnapshot",
									dataType:'json',
									data:$form.serialize(),
									success:function(data){
										if(data=='success'){
											new $.flavr({ 
						          content : '创建成功',
						          autoclose : true,
						          timeout : 2500,
						          buttons:{
						            	确定:{}
						          }
				        			});
										}else{
											new $.flavr({ 
						          content : '创建失败,'+data,
						          autoclose : true,
						          timeout : 2500,
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
								});
								return true;
							}
				},
				取消:{}
			}
		});
	}
	
	
	/*删除云硬盘*/
	$scope.deleteShot=function($event){
		var e=$event.target;
		new $.flavr({
				content:'确定删除此硬盘?',
				dialog:'confirm',
				buttons:{
	       	确定:{
	       		style:'danger',
	       		action:function(){
							$.ajax({
								url:'/ahwaterCloud/cont/deleteVolume',
								type:'POST',
								dataType:'json',
								data:{
									volumeId:$(e).attr('vid'),
								},
								success:function(data){
									console.log(data);
									if(data=='succ'){
										$scope.getVolume();
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
	
	
	/*从主机卸载*/
	$scope.removeVolume=function($event){
		var e=$event.target;
		new $.flavr({
				content:'确定将此硬盘从主机卸载?',
				dialog:'confirm',
				buttons:{
	       	确定:{
	       		style:'danger',
	       		action:function(){
							$.ajax({
								url:'/ahwaterCloud/cont/detachVolumeServer',
								type:'POST',
								dataType:'json',
								data:{
									serverId:$(e).attr('sid'),
									attachmentId:$(e).attr('aid')
								},
								success:function(data){
									console.log(data);
									if(data=='success'){
										$scope.getVolume();
										new $.flavr({
									    content:'正在卸载，请稍后查看',
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
		
	/*挂载到主机*/
	$scope.addToServer=function($event){
		var e=$event.target;
		$("#AddtoServerModal").modal('show');
		
		//获取主机列表
		$http.get('/ahwaterCloud/cont/listServerNameId').success(function(cloudServerList){
			$scope.cloudServerData=cloudServerList;
		})
		
		//确定挂载
		$scope.addToServerAction=function(){
			$("#AddtoServerModal").modal('hide');
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/cont/attachVolumeServer",
				dataType:'json',
				data:{
					volumeId:$(e).attr('vid'),
					serverId:$('#cloudServerBox').val()
				},
				success:function(data){
					console.log(data)
					if(data=="success"){
						$scope.getVolume();
						new $.flavr({
							content:'挂载中,请稍后查看',
							autoclose:true,
							timeout:3000,
							buttons:{确定:{}}
						})
					}else{
						new $.flavr({
							content:'挂载失败,'+data,						
							buttons:{确定:{}}
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

		//批量选择		
		$scope.cnt=0;
    $('#volumeList .actionBtn>button.delVolumeBatch').attr('disabled',true);
    $('#volumeList .cloudlist>table>thead>tr>th>input').click(function(){       
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.cnt=$scope.volumeLength;
            $('#volumeList .actionBtn>button.delVolumeBatch').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.cnt=0
            $('#volumeList .actionBtn>button.delVolumeBatch').attr('disabled',true);
        }
    })
    //单个选择
    $scope.choose=function($event){
        var input=$event.target;
        if($(input).prop('checked')==true){
            $(input).prop('checked',true);
            $scope.cnt++;
            if($scope.cnt==$scope.volumeLength){
                $('#volumeList .cloudlist>table>thead>tr>th>input').prop('checked',true);
            }
        }else{
            $(input).prop('checked',false);
            $scope.cnt--;
            $('#volumeList .cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.cnt>0){$('.actionBtn>button.delVolumeBatch').attr('disabled',false);}
        else{$('#volumeList .actionBtn>button.delVolumeBatch').attr('disabled',true);}
    }
    
    //批量删除云硬盘
    $scope.delVolumeBatch=function(){
    	var inputs=$('.cloudlist>table>tbody input[type="checkbox"]');		
			var volumeIdArr=[];
			for(k in inputs){
				if(inputs[k].checked){
					volumeIdArr.push($(inputs[k]).attr('vid'))
				}
			}
			new $.flavr({
				content:'确定删除选中的'+volumeIdArr.length+'个云硬盘？',
				dialog:'confirm',
				buttons:{
					确定:{
						style:'danger',
						action:function(){
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/cont/deleteMultiVolume",
								dataType:'json',
								data:{
									volumeIds:JSON.stringify(volumeIdArr)
									},
								success:function(data){
									var succCnt=0
									for (var i = 0; i < data.length; i++) {
										if(data[i]=='succ'){
											succCnt++
										}
									}
									new $.flavr({
										content:'成功删除'+succCnt+'个,失败'+(data.length-succCnt)+'个',
										autoclose:true,
										timeout:3000,
										buttons:{确定:{}}
									})
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
	
	
	////编辑云硬盘快照
	$scope.editVolShot=function($event){
		var e=$event.target;
		var volid=$(e).attr('vid');
		var name=$(e).attr('vsname')
		var desc=$(e).attr('vsdesc')
		var html=`
		<form>
			<div class="form-row">
			<input class="form-control" value=${name} style="color:#333" name="newVolumeSnapshotName" type="text" placeholder="请输入新的名称"/>
			<input name="volumeSnapshotId" type="hidden" value=${volid} />
			</div>
			<div class="form-row">
				<textarea class="form-control" name="newVolumeSnapshotDescription" style="resize: none;color:#333;padding-left:15px" rows="3" cols="30" placeholder="新的描述内容">${desc}</textarea>
			</div>
		</form>
		`;
		
		new $.flavr({
			title : '编辑', 
			content : '修改云硬盘快照的名称和描述', 
			dialog : 'form', 
			form : { 
				content: html, 
				method:'post'
			}, 
			buttons:{
				提交:{
					style:'danger',
					action:function( $container, $form ){ 
								console.log($form.serialize());									
								$.ajax({
									type:"POST",
									url:"/ahwaterCloud/cont/editVolumeSnapshot",
									dataType:'json',
									data:$form.serialize(),
									success:function(data){
										if(data=='succ'){
											$scope.getVolumeShot();
										}else{
											new $.flavr({
												content:'修改失败,'+data,
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
	
	
	////删除云硬盘快照
	$scope.deleteVolShot=function($event){
		var e=$event.target;
		new $.flavr({
				content:'确定删除此硬盘快照?',
				dialog:'confirm',
				buttons:{
	       	确定:{
	       		style:'danger',
	       		action:function(){
							$.ajax({
								url:'/ahwaterCloud/cont/deleteVolumeSnapshot',
								type:'POST',
								dataType:'json',
								data:{
									volumeSnapshotId:$(e).attr('vid'),
								},
								success:function(data){
									console.log(data);
									if(data=='succ'){
										$scope.getVolumeShot();
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


		////云硬盘快照批量选择
		$scope.Shotcnt=0;
    $('#snapShotList .actionBtn>button').attr('disabled',true);
    $('#snapShotList .cloudlist>table>thead>tr>th>input').click(function(){       
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.Shotcnt=$scope.volShotLength;
            $('#snapShotList .actionBtn>button').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.Shotcnt=0
            $('#snapShotList .actionBtn>button').attr('disabled',true);
        }
    })
    //云硬盘快照单个选择
    $scope.Shotchoose=function($event){
        var input=$event.target;
        if($(input).prop('checked')==true){
            $(input).prop('checked',true);
            $scope.Shotcnt++;
            if($scope.Shotcnt==$scope.volShotLength){
              $('#snapShotList .cloudlist>table>thead>tr>th>input').prop('checked',true);
            }
        }else{
            $(input).prop('checked',false);
            $scope.Shotcnt--;
            $('#snapShotList .cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.Shotcnt>0){$('#snapShotList .actionBtn>button').attr('disabled',false);}
        else{$('#snapShotList .actionBtn>button').attr('disabled',true);}
    }


	////批量删除云硬盘快照
	$scope.delVolShotBatch=function(){
		var inputs=$('.cloudlist>table>tbody input[type="checkbox"]');		
			var volumeShotIdArr=[];
			for(k in inputs){
				if(inputs[k].checked){
					volumeShotIdArr.push($(inputs[k]).attr('vid'))
				}
			}
			new $.flavr({
				content:'确定删除选中的'+volumeShotIdArr.length+'个快照？',
				dialog:'confirm',
				buttons:{
					确定:{
						style:'danger',
						action:function(){
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/cont/deleteMultiVolumeSnapshots",
								dataType:'json',
								data:{
									volumeSnapshotIds:JSON.stringify(volumeShotIdArr)
									},
								success:function(data){
									var succCnt=0;
									for(var i=0;i<data.length;i++){
										if(data[i]=='succ'){
											succCnt++;
										}
									}
									if(succCnt==data.length){
										$scope.getVolumeShot();
									}
									else{
										new $.flavr({
										content:'删除成功'+succCnt+'个,失败'+(data.length-succCnt)+'个',
										autoclose:true,
										timeout:3000,
										buttons:{确定:{}}
									})
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
}])

/*修改大小*/
function changeVolSize(e){
	console.log(e);
	new $.flavr({ 
		content : '扩展云硬盘', 
		dialog : 'prompt',
		prompt : { 
			placeholder: '输入新的卷大小' 
		}, 
		buttons:{
			确定:{
				style:'danger',
				action:function($container,$prompt){ 						
						var size=parseInt($prompt.val())						
						if(isNaN(size)){
							this.shake();
							return false
						}
						else{
							$.ajax({
							type:'POST',
							dataType:'json',
							url:'/ahwaterCloud/cont/extendVolume',
							data:{
								volumeId:$(e).prev().attr('vid'),
								newSize:size
							},
							success:function(data){
								if(data=='success'){
									$(e).prev().html($prompt.val()+"GB");
								}
								else{
									new $.flavr({ 
						          content : '修改失败,'+data,					          
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
