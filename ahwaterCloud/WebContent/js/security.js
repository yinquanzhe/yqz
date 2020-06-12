app.controller('securityCtrl',["$scope","$http",function($scope,$http){
	
	//获取安全组
	$scope.getSeGroup=function(){
		$http.get('/ahwaterCloud/contr/ListAllSecurityGroup').success(function(SecurityGroup){
			$scope.SecurityGroupData=SecurityGroup;
			$scope.slen=SecurityGroup.length;
			if(SecurityGroup.length==0){
				$('.seGroupBox .cloudlist table>tbody>tr:first-child td').html('暂无数据')
			}else{
				$('.seGroupBox .cloudlist table>tbody>tr:first-child').hide()
			}
		})
	}
	$scope.getSeGroup();
	
	//获取密钥对
	$scope.getPairKey=function(){
		$http.get('/ahwaterCloud/contr/ListAllKeyPairs').success(function(KeyPairsList){
			$scope.KeyPairsData=KeyPairsList;
			$scope.klen=KeyPairsList.length;
			if(KeyPairsList.length==0){
				$('.keycodeBox .cloudlist table>tbody>tr:first-child td').html('暂无数据')
			}else{
				$('.keycodeBox .cloudlist table>tbody>tr:first-child').hide()
			}
		})
	}
	$scope.getPairKey();
	
	//获取浮动ip
	$scope.getFloIp=function(){
		$http.get('/ahwaterCloud/contr/ListAllFloatingIp').success(function(FloatIPList){
			$scope.FloatIPData=FloatIPList;
			$scope.flen=FloatIPList.length;
			if(FloatIPList.length==0){
				$('.floatIpBox .cloudlist table>tbody>tr:first-child td').html('暂无数据')
			}else{
				$('.floatIpBox .cloudlist table>tbody>tr:first-child').hide()
			}
		})
	}
	$scope.getFloIp();
	
	//创建密钥对
	$scope.createKeypair=function(){
		new $.flavr({ 
		content : '创建密钥对', 
		dialog : 'prompt',
		prompt : { 
			placeholder: '输入密钥对名称' 
		}, 
		buttons:{
			创建:{
				style:'danger',
				action:function($container,$prompt){ 	
						console.log($prompt.val());
							$.ajax({
							type:'POST',
							dataType:'json',
							url:'/ahwaterCloud/contr/CreateKeyPair',
							data:{
								keypairName:$prompt.val()
							},
							success:function(data){
								if(data.errorMsg=="success"){								
									$scope.getPairKey();
									new $.flavr({ 
					          content : '创建成功',					          
					          buttons:{
					            	确定:{
					            		style:'danger',
					            		action:function(){
					            			/*var win = window.open("","");
														win.document.write(data.privateKey);
														win.document.close();
														win.document.execCommand('Saveas',false,'c:\\'+$prompt.val()+'.pem');
														win.close();*/
					            		}
					            	}
					          }
				        	});
								}
								else{
									new $.flavr({ 
						          content : '创建失败,提示信息：'+data.errorMsg,
						          autoclose : true,
						          timeout : 3500,
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
			},
			取消:{}
		}		
	});
	}
	
	//删除密钥对
	$scope.delKeyPair=function($event){
		var e=$event.target;		
		var keyNameArr=[];
		
		keyNameArr.push($(e).attr("kname"))
		
		console.log(keyNameArr);
		
		new $.flavr({
			content:'确定删除该密钥对？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeleteKeyPair",
							dataType:'json',
							data:{keypairNames:JSON.stringify(keyNameArr)},
							success:function(data){
								if(data=='删除成功 1 个，失败 0 个！'){
									$scope.getPairKey();
								}else{
									new $.flavr({ 
					          content : data,
					          autoclose : true,
					          timeout : 3500,
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
				},
				取消:{}
			}
		});
	}
	
	
	//console.log($(".cloudlist thead input"));
	//密钥对全选与反选
	$scope.kcnt=0;
	$('.keycodeBox .search>button.delKeycode').attr('disabled',true);
	$('.keycodeBox .cloudlist>table>thead>tr>th>input').click(function(){
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.kcnt=$scope.klen
            $('.keycodeBox .search>button.delKeycode').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.kcnt=0
            $('.keycodeBox .search>button.delKeycode').attr('disabled',true);
        }
  })	
	$scope.kchoose=function($event){
        var input=$event.target;
        console.log(input);
        if($(input).prop('checked')==true){
            //$(input).prop('checked',true);
            $scope.kcnt++;
            if($scope.kcnt==$scope.klen){
            	//alert(333);
              $('.keycodeBox .cloudlist>table>thead>tr>th>input').prop("checked",true);
            }
        }else{
            $scope.kcnt--;
            $('.keycodeBox .cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.kcnt>0){$('.keycodeBox .search>button.delKeycode').attr('disabled',false);}
        else{$('.keycodeBox .search>button.delKeycode').attr('disabled',true);}
  }
	
	//批量删除密钥对
	$scope.dkps=function(){
		var inputs=$('.keycodeBox .cloudlist>table>tbody input[type="checkbox"]');		
		var checkKeyName=[];
		for(k in inputs){
			if(inputs[k].checked){
				checkKeyName.push($(inputs[k]).attr('kname'))
			}
		}
		console.log(checkKeyName);	
		new $.flavr({
			content:'确定删除这些密钥对？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeleteKeyPair",
							dataType:'json',
							data:{keypairNames:JSON.stringify(checkKeyName)},
							success:function(data){
								new $.flavr({
									content:data,
									buttons:{
										确定:{
											action:function(){
												$scope.getPairKey()
											}
										}
									}
								})
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
				},
				取消:{}
			}
		});
		
	}
	
	//显示详情
	$scope.showKeyDe=function($event){
		var e=$event.target;
		console.log($(e).html());
		
		$.ajax({
			type:"get",
			url:"/ahwaterCloud/contr/DisplayKeyPairDetails",
			dataType:'json',
			data:{keypairName:$(e).html()},
			success:function(data){
				var html=`
				<div>
					<div class="text-left">
						名称：
						${data.keypairName}
					</div>
					<div class="text-left">
						ID：
						${data.id}
					</div>
					<div class="text-left">
						创建时间：
						${data.timeCreated}
					</div>
					<div class="text-left">
						指纹：
						${data.fingerPrint}
					</div>
					<div class="text-left">
						用户ID：
						${data.userID}
					</div>
					<div class="text-left" style="width:300px;word-break:break-all">
						公钥：
						${data.publicKey}
					</div>			
				</div>
				`;
				new $.flavr({
					content:html,
					dialog:'confirm',
					animateEntrance : "fadeIn",
					animateClosing:"slideOutUp",
					position:"mid",
					buttons:{
						确定:{
							style:'danger'
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
		});		
	}
	
	//导入密钥对
	$scope.importKey=function(){
		var html=`
		<form>
			<div class="form-row">
			<input class="form-control" style="color:#333" name="keypairName" type="text" placeholder="密钥对名称"/>
		</div>
		<div class="form-row">
			<textarea class="form-control" name="publicKey" style="resize: none;color:#333;padding-left:15px" rows="4" cols="30" placeholder="公钥内容"></textarea>
		</div>
		</form>
		`;	
		new $.flavr({
			title : '导入密钥对', 
			content : '这将生成一对密钥对,一个私钥和一个公钥,请妥善保存好您的私钥', 
			animateEntrance : "fadeIn",
			dialog : 'form', 
			form : { 
				content: html, 
				method:'post'
			}, 
			buttons:{
				导入:{
					style:'danger',
					action:function( $container, $form ){ 
								console.log($form.serialize());									
								$.ajax({
									type:"POST",
									url:"/ahwaterCloud/contr/ImportKeyPair",
									dataType:'json',
									data:$form.serialize(),
									success:function(data){
										console.log(data);
										if(data=="success"){
											/*new $.flavr({ 
						          content : '导入成功',					          
						          buttons:{
						            	确定:{
						            		style:'danger',
						            		action:function(){
						            			location.reload(true);
						            		}
						            	}
						          }
				        			});*/
				        			$scope.getPairKey();
										}
										else{
											new $.flavr({ 
						          content : "导入失败,提示信息:"+data,
						          autoclose : true,
						          timeout : 3500,
						          buttons:{
						            	确定:{style:'danger'}
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
								return true;
							}
				},
				取消:{}
			}
		});
	}


	//关联主机
	$scope.bindIPToEcs=function($event){
			var e=$event.target;
			$("#bindIPModal").modal('show')
			$http.get('/ahwaterCloud/ctr/listAllServers').success(function(serverList){
				$scope.newServerData=serverList;
			})
			console.log($scope.newServerData);
			$scope.bindToEcsAction=function(){
				$("#bindIPModal").modal('hide')
				$.ajax({
					type:"POST",
					url:"/ahwaterCloud/contr/AddFloatingIp",
					dataType:'json',
					data:{
						serverId:$(e).attr('sid'),
						floatingIpAddress:$(e).attr('fip'),
					},
					success:function(data){
						if(data.errorMsg=='success'){
						new $.flavr({ 
				          content : '关联成功',
				          autoclose : true,
				          timeout : 3500,
				          buttons:{
				            	确定:{}
				          }
		        		});
						}else{
							new $.flavr({ 
					          content : '关联失败,提示信息:'+data.errorMsg,
					          autoclose : true,
					          timeout : 3500,
					          buttons:{
					            	确定:{}
					          }
			        		});
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
	
	
	//释放浮动IP
	$scope.delFloatIP=function($event){
		var e=$event.target;
		var ipArr=[];
		ipArr.push($(e).attr('fid'));
		new $.flavr({
			content:'确定释放该IP？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeallocateFloatingIp",
							dataType:'json',
							data:{id:JSON.stringify(ipArr)},
							success:function(data){
								console.log(data);
								if(data=='success'){
									$scope.getFloIp();
								}
								else{
									new $.flavr({ 
							          content : '无法释放,提示信息:'+data,
							          autoclose : true,
							          timeout : 3500,
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
				},
				取消:{}
			}
		});
	}

	//浮动IP多选
	$scope.Icnt=0;
	$('.floatIpBox .search>button.delFloatIP').attr('disabled',true);
	$('.floatIpBox .cloudlist>table>thead>tr>th>input').click(function(){
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.Icnt=$scope.klen
            $('.floatIpBox .search>button.delFloatIP').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.Icnt=0
            $('.floatIpBox .search>button.delFloatIP').attr('disabled',true);
        }
  })	
	$scope.Ichoose=function($event){
        var input=$event.target;
        console.log(input);
        if($(input).prop('checked')==true){
            //$(input).prop('checked',true);
            $scope.Icnt++;
            if($scope.Icnt==$scope.flen){
            	//alert(333);
              $('.floatIpBox .cloudlist>table>thead>tr>th>input').prop("checked",true);
            }
        }else{
            $scope.Icnt--;
            $('.floatIpBox .cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.Icnt>0){$('.floatIpBox .search>button.delFloatIP').attr('disabled',false);}
        else{$('.floatIpBox .search>button.delFloatIP').attr('disabled',true);}
  }
	
	//批量释放浮动IP
	$scope.delIPS=function(){
		var inputs=$('.floatIpBox .cloudlist>table>tbody input[type="checkbox"]');		
		var IPSArr=[];
		for(k in inputs){
			if(inputs[k].checked){
				IPSArr.push($(inputs[k]).attr('kname'))
			}
		}
		new $.flavr({
			content:'确定释放这些浮动IP？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeallocateFloatingIp",
							dataType:'json',
							data:{id:JSON.stringify(IPSArr)},
							success:function(data){
								new $.flavr({
									content:data,									
									buttons:{确定:{
										action:function(){
											location.reload();
										}
									}}
								})
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
				},
				取消:{}
			}
		});
	}

	//分配IP
	$scope.devideIP=function(){
		$("#devideIPModal").modal('show')
		$scope.poolData=[]
		$http.get("/ahwaterCloud/contr/GetPoolNames").success(function(PoolList){		
			$scope.poolData=PoolList;	
			console.log($scope.poolData);
		})
		$scope.devideIPAction=function(){
			$("#devideIPModal").modal('hide');
			$.ajax({
				type:'POST',
				url:'/ahwaterCloud/contr/AllocateFloatingIp',
				dataType:'json',
				data:{
					pool:$("#poolBox").val()
				},
				success:function(data){
					console.log(data);
					if(data=='success'){
						$scope.getFloIp();
					}else{
						new $.flavr({ 
		          content : '分配失败,提示信息:'+data,
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
	}
	
	//区块的显示与隐藏
	$scope.slideUpBtn=function($event){
		var e=$event.target;
		if($(e).hasClass('fa-chevron-down')){
			$(e).removeClass('fa-chevron-down').addClass('fa-chevron-up');
			$(e).parent().parent().siblings('.cloudlist').css({
				height:'0'
			})
		}else{
			$(e).removeClass('fa-chevron-up').addClass('fa-chevron-down');
			$(e).parent().parent().siblings('.cloudlist').css({
				height:'auto'
			})
		}
	}
	
	//创建安全组
	$scope.createSeGroup=function(){
		var html=`
		<form>
			<div class="form-row">
			<input class="form-control" style="color:#333" name="securityGroupName" type="text" placeholder="请输入安全组名称"/>
			</div>
			<div class="form-row">
				<textarea class="form-control" name="description" style="resize: none;color:#333;padding-left:15px" rows="3" cols="30" placeholder="描述内容"></textarea>
			</div>
		</form>
		`;
		
		new $.flavr({
			title : '创建安全组', 
			content : '添加一个新的安全组', 
			dialog : 'form', 
			form : { 
				content: html, 
				method:'post'
			}, 
			buttons:{
				创建:{
					style:'danger',
					action:function( $container, $form ){ 
								console.log($form.serialize());									
								$.ajax({
									type:"POST",
									url:"/ahwaterCloud/contr/CreateSecurityGroup",
									dataType:'json',
									data:$form.serialize(),
									success:function(data){
										if(data=='success'){
											$scope.getSeGroup();
										}else{
											new $.flavr({
												content:'创建失败,'+data,
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
	
	
	//编辑安全组
	$scope.editSeGroup=function($event){
		var e=$event.target;
		var Seid=$(e).attr('sid');
		var html=`
		<form>
			<div class="form-row">
			<input class="form-control" style="color:#333" name="securityGroupName" type="text" placeholder="请输入新的名称"/>
			<input name="securityGroupId" type="hidden" value=${Seid} />
			</div>
			<div class="form-row">
				<textarea class="form-control" name="description" style="resize: none;color:#333;padding-left:15px" rows="3" cols="30" placeholder="新的描述内容"></textarea>
			</div>
		</form>
		`;
		
		new $.flavr({
			title : '编辑', 
			content : '修改安全组名称和描述', 
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
									url:"/ahwaterCloud/contr/EditSecurityGroup",
									dataType:'json',
									data:$form.serialize(),
									success:function(data){
										if(data=='success'){
											$scope.getSeGroup();
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
	
	//安全组全选与单选
	$scope.scnt=0;
	$('.seGroupBox .search>button.deleteSeGroup').attr('disabled',true);
	$('.seGroupBox .cloudlist>table>thead>tr>th>input').click(function(){
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.scnt=$scope.slen
            $('.seGroupBox .search>button.deleteSeGroup').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.scnt=0
            $('.seGroupBox .search>button.deleteSeGroup').attr('disabled',true);
        }
  })	
	$scope.schoose=function($event){
        var input=$event.target;      
        if($(input).prop('checked')==true){
            $scope.scnt++;
            if($scope.scnt==$scope.slen){
              $('.seGroupBox .cloudlist>table>thead>tr>th>input').prop("checked",true);
            }
        }else{
            $scope.scnt--;
            $('.seGroupBox .cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.scnt>0){$('.seGroupBox .search>button.deleteSeGroup').attr('disabled',false);}
        else{$('.seGroupBox .search>button.deleteSeGroup').attr('disabled',true);}
  }
	//批量删除安全组
	$scope.delSeBatch=function(){
		var inputs=$('.seGroupBox .cloudlist>table>tbody input[type="checkbox"]');		
		var seNameArr=[];
		for(k in inputs){
			if(inputs[k].checked){
				seNameArr.push($(inputs[k]).attr('kname'))
			}
		}
		console.log(seNameArr);	
		new $.flavr({
			content:'确定删除这些安全组？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeleteSecurityGroup",
							dataType:'json',
							data:{securityGroupId:JSON.stringify(seNameArr)},
							success:function(data){
								new $.flavr({
									content:data,									
									buttons:{确定:{
										action:function(){
											$scope.getSeGroup()
										}
									}}
								})
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
				},
				取消:{}
			}
		});
	}
	
	//删除安全组
	$scope.delSeGroup=function($event){
		var e=$event.target;
		var securityGroupArr=[];
		securityGroupArr.push($(e).attr('sid'))
		new $.flavr({
			content:'确定删除该安全组？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeleteSecurityGroup",
							dataType:'json',
							data:{securityGroupId: JSON.stringify(securityGroupArr)},
							success:function(data){
								if(data=='删除成功 1 个，失败 0 个！'){
									$scope.getSeGroup();
								}
								else{
									new $.flavr({
										content:data,
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
				},
				取消:{}
			}
		});
	}
}])







