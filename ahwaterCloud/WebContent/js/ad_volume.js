app.controller('advolCtrl',['$scope','$http',function($scope,$http){
	//获取云硬盘列表
	$scope.getadminVolume=function(){
		$http({
			url:'/ahwaterCloud/cont/adminListAllVolumes',
			method:'get',
			params:{tenantId:$('#dropdownTenant').attr('tid')},
			headers:{'Content-Type':'application/json'}
		}).success(function(VolumeList){
			$scope.volumeData=VolumeList;
			$scope.volumeLength=VolumeList.length;
			$('#advolumeList .cloudlist>table>tbody>tr:first-child').hide();
		})
	}
	$scope.getadminVolume();
	
	//获取云硬盘快照(需在点击事件里执行)
	$scope.getadminVolumeShot=function(){
		$http({
			url:'/ahwaterCloud/cont/adminListAllVolumeSnapshots',
			method:'get',
			params:{tenantId:$('#dropdownTenant').attr('tid')},
			headers:{'Content-Type':'application/json'}
		}).success(function(VolumeShotList){
			$scope.volomeShotData=VolumeShotList;
			$scope.volShotLength=VolumeShotList.length;
			$('#adsnapShotList .cloudlist>table>tbody>tr:first-child').hide();
		}).error(function(){
			new $.flavr({
				content:'获取列表失败',
				autoclose:true,
				timeout:2500,
				buttons:{确定:{}}
			})
			$('#adsnapShotList .cloudlist>table>tbody>tr:first-child td').html("暂无数据");
		})
	}
	//$scope.getVolumeShot();
	
	/*标签页切换*/
	$('#advolumeListTab a').click(function(e){
		e.preventDefault();
		$(this).tab('show');
		if($(e.target).attr('href').slice(1)=='advolumeList'){
			$scope.getadminVolume();
		}else{
			$scope.getadminVolumeShot();
		}
	})

	/*删除云硬盘*/
	$scope.remAdminVol=function($event){
		var e=$event.target;
		new $.flavr({
				content:'确定删除此云硬盘?',
				dialog:'confirm',
				buttons:{
	       	确定:{
	       		style:'danger',
	       		action:function(){
	       			new $.flavr({
						    content:'删除中，请稍后刷新查看',
						    modal:false,
						    autoclose:true,
						    timeout:2500,
						    buttons:{
						       	确定:{}
						    }
					  	})
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
										$scope.getadminVolume();
										
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

		//批量选择		
		$scope.cnt=0;
    $('#advolumeList .actionBtn>button.delVolumeBatch').attr('disabled',true);
    $('#advolumeList .cloudlist>table>thead>tr>th>input').click(function(){       
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.cnt=$scope.volumeLength;
            $('#advolumeList .actionBtn>button.delVolumeBatch').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.cnt=0
            $('#advolumeList .actionBtn>button.delVolumeBatch').attr('disabled',true);
        }
    })
    //单个选择
    $scope.choose=function($event){
        var input=$event.target;
        if($(input).prop('checked')==true){
            $(input).prop('checked',true);
            $scope.cnt++;
            if($scope.cnt==$scope.volumeLength){
                $('#advolumeList .cloudlist>table>thead>tr>th>input').prop('checked',true);
            }
        }else{
            $(input).prop('checked',false);
            $scope.cnt--;
            $('#advolumeList .cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.cnt>0){$(' #advolumeList .actionBtn>button.delVolumeBatch').attr('disabled',false);}
        else{$('#advolumeList .actionBtn>button.delVolumeBatch').attr('disabled',true);}
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
							/*new $.flavr({
						    content:'删除中，请稍后刷新查看',
						    autoclose:true,
						    timeout:2500,
						    buttons:{
						       	确定:{}
						    }
					  	})*/
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/cont/deleteMultiVolume",
								dataType:'json',
								data:{
									volumeIds:JSON.stringify(volumeIdArr)
									},
								success:function(data){
									var faildCnt=0
									for(let i=0;i<data.length;i++){
										if(data[i]!='succ'){
											faildCnt++
										}
									}
									new $.flavr({
										content:'存在某些无法删除,请检查其状态并重试',
										modal:false,
										buttons:{确定:{action:function(){location.reload()}}}
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
	
	//删除云硬盘快照
	$scope.deleteVolShot=function($event){
		var e=$event.target;
		new $.flavr({
				content:'确定删除此硬盘快照?',
				dialog:'confirm',
				buttons:{
	       	确定:{
	       		style:'danger',
	       		action:function(){
	       			new $.flavr({
						    content:'删除中，请稍后刷新查看',
						    autoclose:true,
						    timeout:2000,
						    buttons:{
						       	确定:{}
						    }
					  	})
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
										$scope.getadminVolumeShot();
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
    $('#adsnapShotList .actionBtn>button').attr('disabled',true);
    $('#adsnapShotList .cloudlist>table>thead>tr>th>input').click(function(){       
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
              $('#adsnapShotList .cloudlist>table>thead>tr>th>input').prop('checked',true);
            }
        }else{
            $(input).prop('checked',false);
            $scope.Shotcnt--;
            $('#adsnapShotList .cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.Shotcnt>0){$('#adsnapShotList .actionBtn>button').attr('disabled',false);}
        else{$('#adsnapShotList .actionBtn>button').attr('disabled',true);}
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
							new $.flavr({
						    content:'删除中，请稍后刷新查看',
						    autoclose:true,
						    timeout:2000,
						    buttons:{
						       	确定:{}
						    }
					  	})
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/cont/deleteMultiVolumeSnapshots",
								dataType:'json',
								data:{
									volumeSnapshotIds:JSON.stringify(volumeShotIdArr)
									},
								success:function(data){								
									if(data=='succ'){
										location.reload()
									}
									else{
										new $.flavr({
										content:'成功删除'+succCnt+'个,失败'+(data.length-succCnt)+'个',
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

	//更改云硬盘状态
	$scope.changeVolState=function($event){
		var e=$event.target;
		$('#changeStateModal').modal('show');
		$scope.changeStateAction=function(){
			$('#changeStateModal').modal('hide');
			new $.flavr({
		    content:'更新中，请稍后查看',
		    modal:false,
		    autoclose:true,
		    timeout:2500,
		    buttons:{
		       	确定:{}
		    }
	  	})
			$.ajax({
				type:"get",
				url:"/ahwaterCloud/cont//resetVolumeState",
				dataType:'json',
				data:{volumeId:$(e).attr('vid'),newState:$('#stateVolBox').val()},
				success:function(data){
					
					if(data=='succ'){
						$scope.getadminVolume()
					}else{
						new $.flavr({
							content:'更改失败,'+data,
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
	
	////更改云硬盘快照状态
	$scope.changeVolShotState=function($event){
		var e=$event.target;
		$('#changeShotStateModal').modal('show');
		$scope.changeShotStateAction=function(){
			$('#changeShotStateModal').modal('hide');
			new $.flavr({
		    content:'更新中，请稍后查看',
		    autoclose:true,
		    timeout:2500,
		    buttons:{
		       	确定:{}
		    }
	  	})
			$.ajax({
				type:"get",
				url:"url",
				dataType:'json',
				data:{volumeShotId:$(e).attr('vid'),newState:$('#stateShotBox').val()},
				success:function(data){
					if(data=='succ'){
						$scope.getadminVolum()
					}else{
						new $.flavr({
							content:'更改失败,'+data,
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
	
	
/*	//迁移卷
	$scope.migrateVol=function($event){
		var e=$event.target;
			
		$("#migrateVolModal").modal('show');
		//获取主机列表
		$http.get('/ahwaterCloud/ctr/listComputeServices').success(function(hostList){
			$scope.hostVolData=hostList;
		})
		
		$("#currentVolHost").html('当前所在主机:&nbsp;&nbsp;&nbsp;'+$(e).attr('hname'));	
		$scope.migrateVolAction=function(){
			$("#migrateVolModal").modal('hide');
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/ctr/liveMigrate",
				dataType:'json',
				data:{
					volumeId:$(e).attr('vid'),
					host:$('#hostVolBox').val(),
					isCopy:$('#iscopy').prop('checked'),
				},
				success:function(data){
					if(data=='succ'){
						new $.flavr({
					    content:'迁移成功',
					    autoclose:true,
					    timeout:2500,
					    buttons:{
					       	确定:{}
					    }
					  })
						$scope.getserver();
					}else{
						new $.flavr({
					    content:'操作失败,'+data,
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
*/	
}])

