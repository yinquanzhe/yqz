app.controller('tenantCtrl',['$scope','$http',function($scope,$http){

	//获取列表
	$scope.getTenant=function(){
		$http.get('/ahwaterCloud/cont/listAllTenant').success(function(TeanntList){
			$scope.TeanntListData=TeanntList
			$scope.TeanntListLength=TeanntList.length;
			if(TeanntList.length==0){
				$('.cloudlist>table>tbody>tr:first-child').html("暂无数据");
			}else{
				$('.cloudlist>table>tbody>tr:first-child').hide();
			}
		})
	}
	$scope.getTenant();	
	
	
	//创建租户
	$scope.createTenant=function(){
		$('#createTenantModal').modal('show')
		$("#createTenantTab a").on('click',function(e){
			e.preventDefault();
			$(this).tab('show')
		})
		$.ajax({
			type:"get",
			url:"/ahwaterCloud/contr/ListAllUser",
			dataType:'json',
			success:function(userList){
				var html;
				for(var i=0;i<userList.length;i++){
					html=`<li  class="list-group-item" >${userList[i].userName}
									<button  uid=${userList[i].useerId} tname=${userList[i].userName} type="button"></button>
								</li>`
					$('#tenantUsersBox .allUsers').append(html)
				}
				$('#tenantUsersBox .allUsers button').click(function(){
					$(this).toggleClass('selc');
				})
				
			}
		});
		$scope.createTenantAction=function(){
			$('#createTenantModal').modal('hide')
			$.ajax({
				type:"get",
				url:"/ahwaterCloud/cont/createTenant",
				dataType:'json',
				data:{
					tenantName:$('#tntName').val(),
					tenantDescription:$('#tntDesc').val(),
					tenantEnable:$('#tntActive').prop('checked')
				},
				success:function(data){
					if(data=='succ'){
						$scope.getTenant();
					}
					else{
						new $.flavr({
							content:'创建失败，'+data
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
	
	//删除
	$scope.delTenant=function($event){
		var e=$event.target;
		new $.flavr({
			content:'确定删除该项目?',
			dialog:'confirm',
			buttons:{
       	确定:{
       		style:'danger',
       		action:function(){				
							$.ajax({
								url:'/ahwaterCloud/cont/deleteTenant',
								type:'POST',
								dataType:'json',
								data:{
									"tenantId":$(e).attr('tid')
								},
								success:function(data){
									if(data=='succ'){
										$scope.getTenant();
										new $.flavr({
											content:'已删除',
											modal:false,
											autoclose:true,
											timeout:2500,
											buttons:{确定:{}}
										})
									}else{
										new $.flavr({
											content:'删除失败，'+data,
											buttons:{确定:{}}
										})
									}
								},
								error:function(){
									new $.flavr({
								    content:'请求失败',
								    autoclose:true,
								    timeout:2500,
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
	
	//全选与取消全选
	$scope.Tenantcnt=0;
  $('.tenantBox .actionBtn>button.deleteBatch').attr('disabled',true);
  $('.tenantBox .cloudlist>table>thead>tr>th>input').click(function(){       
      var inputs=$(this).parent().parent().parent().next().children().children().children('input');
      if($(this).prop('checked')==true){
          $(this).prop('checked',true);
          inputs.prop('checked',true);
          $scope.Tenantcnt=$scope.TeanntListLength;
          $('.tenantBox .actionBtn>button.deleteBatch').attr('disabled',false);
      }else{
          inputs.prop('checked',false);
          $scope.Tenantcnt=0
          $('  .actionBtn>button.deleteBatch').attr('disabled',true);
      }
  })
	
	//单个选择
	$scope.Typechoose=function($event){
    var input=$event.target;
    if($(input).prop('checked')==true){
        $(input).prop('checked',true);
        $scope.Tenantcnt++;
        if($scope.Tenantcnt==$scope.TeanntListLength){
          $('.tenantBox .cloudlist>table>thead>tr>th>input').prop('checked',true);
        }
    }else{
        $(input).prop('checked',false);
        $scope.Tenantcnt--;
        $('.tenantBox .cloudlist>table>thead>tr>th>input').prop('checked',false);
    }
    if($scope.Tenantcnt>0){$('.actionBtn>button.deleteBatch').attr('disabled',false);}
    else{$('.tenantBox .actionBtn>button.deleteBatch').attr('disabled',true);}
  }
	
	//批量删除
	$scope.removeTenantBatch=function(){
		var inputs=$('.tenantBox .cloudlist>table>tbody input[type="checkbox"]');		
			var tenantIdArr=[];
			for(k in inputs){
				if(inputs[k].checked){
					tenantIdArr.push($(inputs[k]).attr('tid'))
				}
			}
			new $.flavr({
				content:'确定删除选中的'+tenantIdArr.length+'个项目？',
				dialog:'confirm',
				buttons:{
					确定:{
						style:'danger',
						action:function(){
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/cont/deleteMultiTenants",
								dataType:'json',
								data:{
									tenantIds:JSON.stringify(tenantIdArr)
									},
								success:function(data){
									var succCnt=0;
									for(var i=0;i<data.length;i++){
										if(data[i]=='succ'){
											succCnt++;
										}
									}
									if(succCnt==data.length){
										$scope.getTenant();
										new $.flavr({
											content:'已删除',
											modal:false,
											autoclose:true,
											timeout:2500,
											buttons:{确定:{}}
										})
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
				
	//更改成员
	$scope.managerUser=function($event){
		var e=$event.target
		
		$("#managerUserModal").modal('show')
		$http.get('/ahwaterCloud/contr/ListAllUser').success(function(userList){
			$scope.tntUserData=userList	
			$http.get('/ahwaterCloud/ctr/listRoles').success(function(roles){
				$scope.tntRoleData=roles;
				//获取当前租户所有的成员
				$http({
					method:'get',
					url:'/ahwaterCloud/cont/listUserIdRoleId',
					params:{
						tenantId:$(e).attr('tid')
					},
					headers:{'Content-Type':'application/json'}
				}).success(function(tntUserRole){
					$scope.tntUserRole=tntUserRole;
					var btns=$('#managerUserModal .list-group li button')
					for(let i=0;i<tntUserRole.length;i++){
						for(let j=0;j<btns.length;j++){
							if($(btns[j]).attr('uid')==tntUserRole[i].userId){
								$(btns[j]).addClass('selc'); //按钮变红
								console.log(tntUserRole[i].roleIds[0])
								$(btns[j]).next().val(tntUserRole[i].roleIds[0]); //默认选中项
							}
						}
					}
				})
			})
		})
		
		
		
		
		$scope.checkUserBtn=function($event){
			var e=$event.target;
			$(e).toggleClass('selc')
		}
		
		$scope.managerUserAction=function(){
			
			var btns=$('#managerUserModal .accessControl li button.selc')
			var newUsersArr=[];			
			for(var i=0;i<btns.length;i++){
				var newUserObj={};
				newUserObj.userId=$(btns[i]).attr('uid');
				newUserObj.roleId=$(btns[i]).next().val()+',';
				newUsersArr.push(newUserObj)
			}
			
			$("#managerUserModal").modal('hide')
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/cont/addDeleteUsers",
				dataType:'json',
				data:{
					"tenantId":$(e).attr('tid'),					
					"tenantIdListStr":JSON.stringify(newUsersArr)
				},
				success:function(data){
					if(data=='succ'){
						$scope.getTenant()
						new $.flavr({
							content:'修改成功',
							modal:false,
							autoclose:true,
							timeout:2500,
							buttons:{确定:{}}
						})
					}else{
						new $.flavr({
							content:'修改失败，'+data,
							buttons:{确定:{}}
						})
					}
				},
				error:function(){
					new $.flavr({
						content:"请求失败",
						autoclose:true,
						timeout:2500,
						buttons:{确定:{}}
					})
				}
			});
		}
	}		

	//编辑名称和描述
	$scope.editTenant=function($event){
		var e=$event.target;
		var tntid=$(e).attr('tid');
		var name=$(e).attr('tname')
		var desc=$(e).attr('tdesc')
		var html=`
		<form>
			<div class="form-row">
			<input class="form-control" style="color:#333" name="newName" type="text" placeholder="请输入新的名称" value=${name} />
			<input name="tenantId" type="hidden" value=${tntid} />
			</div>
			<div class="form-row">
				<textarea class="form-control" name="newDescription" style="resize: none;color:#333;padding-left:15px" rows="3" cols="30" placeholder="新的描述内容">${desc}</textarea>
			</div>
		</form>
		`;
		
		new $.flavr({
			title : '编辑', 
			content : '修改项目名称和描述', 
			dialog : 'form', 
			form : { 
				content: html, 
				method:'post'
			},  
			buttons:{
				保存:{
					style:'danger',
					action:function( $container, $form ){ 
								$.ajax({
									type:"POST",
									url:"/ahwaterCloud/cont/updateTenantNameDes",
									dataType:'json',
									data:$form.serialize(),
									success:function(data){
										if(data=='succ'){
											$scope.getTenant();
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

	//修改配额
	$scope.changeQuota=function($event){
		var e=$event.target;
		$('#changeTntQuotaModal').modal('show')
		$http({
			method:'get',
			url:'/ahwaterCloud/contr//GetTenantQuota',
			params:{
				tenantId:$(e).attr('tid')
			},
			headers:{'ContentType':'application/json'}
		}).success(function(quotaData){
			$scope.quotaData=quotaData;
			$('#metaCount').val($scope.quotaData.maxImageMeta) //元数据条目
			$('#fileLimit').val($scope.quotaData.maxPersonality) //注入的文件
			$('#fileByte').val($scope.quotaData.maxPersonalitySize) //注入的文件字节数
			$('#serverGroupMember').val($scope.quotaData.maxServerGroupMembers) //主机组成员
			$('#serverGroup').val($scope.quotaData.maxServerGroups) //主机组
			
			$('#cpuLimit').val($scope.quotaData.maxTotalCores) //虚拟内核
			$('#ipsCount').val($scope.quotaData.maxTotalFloatingIps) //浮动IP
			$('#serverLimit').val($scope.quotaData.maxTotalInstances) //云主机
			$('#memoryLimit').val($scope.quotaData.maxTotalRAMSize) //内存
			$('#keyLimit').val($scope.quotaData.maxTotalKeypairs) //密钥对
					
			$('#volShotLimit').val($scope.quotaData.maxGigabytes) //云硬盘和快照总大小
			$('#snapShotLimit').val($scope.quotaData.maxSnapShots) //云硬盘快照
			$('#volLimit').val($scope.quotaData.maxVolumes) //云硬盘
			$('#ruleLimit').val($scope.quotaData.maxSecurityGroupRules) //安全组规则
			$('#seGroupLimit').val($scope.quotaData.maxSecurityGroups) //安全组
			$('#subnetLimit').val($scope.quotaData.maxSubnet) //子网数
			$('#routeLimit').val($scope.quotaData.maxRouter) //路由数
			$('#portLimit').val($scope.quotaData.maxPort) //端口
			$('#netLimit').val($scope.quotaData.maxNetwork) //网络
		})

		$scope.changeTntQuotaAction=function(){
			$('#changeTntQuotaModal').modal('hide');
			$.ajax({
				type:"post",
				url:"/ahwaterCloud/contr/UpdateTenantQuota",
				dataType:'json',
				data:{
					tenantId:$(e).attr('tid'),
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
						$scope.getTenant();
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