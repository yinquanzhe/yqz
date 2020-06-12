app.controller('userCtrl',['$scope','$http',function($scope,$http){

	//获取用户列表
	$scope.getUserList=function(){
		$http.get('/ahwaterCloud/contr/ListAllUser').success(function(userList){
			$scope.usersData=userList
			$scope.userLen=userList.length;
			if(userList.length==0){
				$('.cloudlist table>tbody>tr:first-child td').html('暂无数据')
			}else{
				$('.cloudlist table>tbody>tr:first-child').hide()
			}
		})
	}
	$scope.getUserList();
	
	//删除用户
	$scope.delUser=function($event){
		var e=$event.target;
		var userIdArr=[];
		userIdArr.push($(e).attr('uid'))
		new $.flavr({
			content:'确定删除该用户?',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"get",
							url:"/ahwaterCloud/contr/DeleteUser",
							dataType:'json',
							data:{
								userId:JSON.stringify(userIdArr)
							},
							success:function(data){
								if(data=='删除成功 1 个，失败 0 个！'){
									$scope.getUserList();
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
									buttons:{确定:{}}
								})
							}
						});
					}
				},
				取消:{}
			}
		})
	}
	
	//添加用户
	$scope.createUser=function(){
		$('#createUserModal').modal('show')
		$http.get('/ahwaterCloud/ctr/listAllTenants').success(function(teList){
			$scope.teList=teList;
		})
		$http.get('/ahwaterCloud/ctr/listRoles').success(function(roleList){
			$scope.roleList=roleList;
		})
		
		$('#cofUserPwd').on('blur',function(){
			if($(this).val()!=$('#userPwd').val()){
				$(this).parent().addClass('has-error')
				$('#createUserActionBtn').attr('disabled',true)
				$('#createUserActionBtn').html('密码不一致')
			}else{
				$(this).parent().removeClass('has-error')
				$('#createUserActionBtn').attr('disabled',false)
				$('#createUserActionBtn').html('添加')
			}
		})
		
		$scope.createUserAction=function(){
			$('#createUserModal').modal('hide')
			$.ajax({
				type:"post",
				url:"/ahwaterCloud/contr/CreateUser",
				dataType:'json',
				data:{
					userName:$('#userName').val(),
					email:$('#userEmail').val(),
					enabled:$('#userEnabled').prop('checked'),
					"password":$("#userPwd").val(),
					roleName:$('#userRole').val(),
					tenantId:$('#tenantUser').val()
				},
				success:function(data){
					if(data=='success'){
						new $.flavr({
							content:'添加成功',
							modal:false,
							buttons:{确定:{}}
						})
						$scope.getUserList()
					}else{
						new $.flavr({
							content:'添加失败，'+data,
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
	
	//编辑用户
	$scope.editUser=function($event){
		var e=$event.target;
		var uid=$(e).attr('uid');
		$('#newuserName').val($(e).attr('uname'));
		$('#newuserEmail').val($(e).attr('umail'))
		$('#updateUserModal').modal('show')
		$http.get('/ahwaterCloud/ctr/listAllTenants').success(function(teList){
			$scope.teList=teList;
		})
		$scope.updateUserAction=function(){
			$('#updateUserModal').modal('hide')
			$.ajax({
				type:"get",
				url:"/ahwaterCloud/contr/UpdateUserInfo",
				dataType:'json',
				data:{
					"userId":uid,
					userName:$('#newuserName').val(),
					email:$('#newuserEmail').val(),
					enabled:$('#newuserEnabled').prop('checked'),
					"password":$("#newuserPwd").val(),
					mainTenantId:$('#newtenantUser').val()
				},
				success:function(data){
					new $.flavr({
						content:'修改成功',
						modal:false,
						autoclose:true,
						timeout:2500,
						buttons:{确定:{}}
					})
					$scope.getUserList()
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

	//批量选择		
	$scope.cnt=0;
  $('.users .actionBtn>button.deleteBatch').attr('disabled',true);
  $('.users .cloudlist>table>thead>tr>th>input').click(function(){       
      var inputs=$(this).parent().parent().parent().next().children().children().children('input');
      if($(this).prop('checked')==true){
          $(this).prop('checked',true);
          inputs.prop('checked',true);
          $scope.cnt=$scope.userLen;
          $('.users .actionBtn>button.deleteBatch').attr('disabled',false);
      }else{
          inputs.prop('checked',false);
          $scope.cnt=0
          $('.users .actionBtn>button.deleteBatch').attr('disabled',true);
      }
  })
  //单个选择
  $scope.userChoose=function($event){
      var input=$event.target;
      if($(input).prop('checked')==true){
          $(input).prop('checked',true);
          $scope.cnt++;
          if($scope.cnt==$scope.userLen){
              $('.users .cloudlist>table>thead>tr>th>input').prop('checked',true);
          }
      }else{
          $(input).prop('checked',false);
          $scope.cnt--;
          $('.users .cloudlist>table>thead>tr>th>input').prop('checked',false);
      }
      if($scope.cnt>0){$('.users .actionBtn>button.deleteBatch').attr('disabled',false);}
      else{$('.users .actionBtn>button.deleteBatch').attr('disabled',true);}
  }
    
  //批量删除用户
  $scope.removeUserBatch=function(){
  	var inputs=$('.cloudlist>table>tbody input[type="checkbox"]');		
		var userIdArr=[];
		for(k in inputs){
			if(inputs[k].checked){
				userIdArr.push($(inputs[k]).attr('uid'))
			}
		}
		new $.flavr({
			content:'确定删除选中的'+userIdArr.length+'个用户？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeleteUser",
							dataType:'text',
							data:{
								userId:JSON.stringify(userIdArr)
								},
							success:function(data){
								new $.flavr({
									content:data,					
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
	
	
	//创建项目
	$scope.createTenantFromUserList=function(){
		$('#createUserModal').modal('hide');
		$('#createTntFromUserModal').modal('show');
		var userTntName=$('#userTntName').val()
		
		$scope.createTntFromUserAction=function(){
			if($('#userTntName').val()==''){
				$('#userTntName').focus()
			}else if($('#userTntDesc').val()==''){
				$('#userTntDesc').focus()
			}else{
				$.ajax({
					type:"get",
					url:"/ahwaterCloud/cont/createTenant",
					dataType:'json',
					data:{
						tenantName:$('#userTntName').val(),
						tenantDescription:$('#userTntDesc').val(),
						tenantEnable:true
					},
					success:function(data){
						if(data=='succ'){
							$http.get('/ahwaterCloud/ctr/listAllTenants').success(function(newteList){
								$scope.teList=newteList;																								
								/*var ops=$("#tenantUser option");
								for(let j=0;j<ops.length;j++){
									console.log($(ops[j]).attr('txt')+'--'+$('#userTntName').val())
								}*/
								
								$('#createTntFromUserModal').modal('hide');
								$('#createUserModal').modal('show');
							})	
						}
						else{
							$('#createTntFromUserModal').modal('hide');
							$('#createUserModal').modal('hide');
							new $.flavr({
								content:'创建失败，'+data,
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
	}
	
	//取消创建
	$scope.cancleCreateTntFromUser=function(){
		$('#createTntFromUserModal').modal('hide');
		$('#createUserModal').modal('show');
	}

}])