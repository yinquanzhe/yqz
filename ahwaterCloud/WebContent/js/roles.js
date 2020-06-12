app.controller('roleCtrl',['$scope','$http',function($scope,$http){
		
	//获取角色列表
	$scope.getRolesList=function(){
		$http.get('/ahwaterCloud/ctr/listRoles').success(function(roles){
			$scope.rolesData=roles
			$scope.roleLen=roles.length;
			if(roles.length==0){
				$('.rolesBox table tbody tr:first-child td').html('暂无数据')
			}else{
				$('.rolesBox table tbody tr:first-child').hide()
			}
		})
	}
	$scope.getRolesList();
	
	//删除角色
	$scope.delRole=function($event){
		var e=$event.target;
		new $.flavr({
			content:'确定删除该角色?',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"post",
							url:"/ahwaterCloud/ctr/deleteRole",
							dataType:'text',
							data:{
								roleId:$(e).attr('rid')
							},
							success:function(data){
								if(data=='succ'){
									$scope.getRolesList();
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
	
	//编辑角色
	$scope.editRole=function($event){
		var e=$event.target
		new $.flavr({ 
		content : '编辑角色', 
		dialog : 'prompt',
		prompt : { 
			placeholder: '输入新角色名称' 
		}, 
		buttons:{
			保存:{
				style:'danger',
				action:function($container,$prompt){ 							
							$.ajax({
							type:'POST',
							dataType:'text',
							url:'/ahwaterCloud/ctr/editRole',
							data:{
								newRoleName:$prompt.val(),
								roleId:$(e).attr('rid')
							},
							success:function(data){
								if(data=="succ"){
									$scope.getRolesList();
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
				},
				取消:{}
			}		
		});
	}
	
	//创建角色
	$scope.createRole=function(){
		new $.flavr({ 
		content : '创建新角色', 
		dialog : 'prompt',
		prompt : { 
			placeholder: '输入角色名称' 
		}, 
		buttons:{
			确定:{
				style:'danger',
				action:function($container,$prompt){ 							
							$.ajax({
							type:'POST',
							dataType:'text',
							url:'/ahwaterCloud/ctr/createRole',
							data:{
								"roleName":$prompt.val(),
							},
							success:function(data){
								if(data=="succ"){
									$scope.getRolesList();
								}
								else{
									new $.flavr({ 
					          content : '创建失败,'+data,						          
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
	
	//批量选择		
	$scope.cnt=0;
  $('.roles .actionBtn>button.deleteBatch').attr('disabled',true);
  $('.roles .cloudlist>table>thead>tr>th>input').click(function(){       
      var inputs=$(this).parent().parent().parent().next().children().children().children('input');
      if($(this).prop('checked')==true){
          $(this).prop('checked',true);
          inputs.prop('checked',true);
          $scope.cnt=$scope.roleLen;
          $('.roles .actionBtn>button.deleteBatch').attr('disabled',false);
      }else{
          inputs.prop('checked',false);
          $scope.cnt=0
          $('.roles .actionBtn>button.deleteBatch').attr('disabled',true);
      }
  })
  //单个选择
  $scope.roleChoose=function($event){
      var input=$event.target;
      if($(input).prop('checked')==true){
          $(input).prop('checked',true);
          $scope.cnt++;
          if($scope.cnt==$scope.roleLen){
              $('.roles .cloudlist>table>thead>tr>th>input').prop('checked',true);
          }
      }else{
          $(input).prop('checked',false);
          $scope.cnt--;
          $('.roles .cloudlist>table>thead>tr>th>input').prop('checked',false);
      }
      if($scope.cnt>0){$('.roles .actionBtn>button.deleteBatch').attr('disabled',false);}
      else{$('.roles .actionBtn>button.deleteBatch').attr('disabled',true);}
  }
    
  //批量删除角色
  $scope.delRoleBatch=function(){
  	var inputs=$('.cloudlist>table>tbody input[type="checkbox"]');		
		var roleIdArr=[];
		for(k in inputs){
			if(inputs[k].checked){
				roleIdArr.push($(inputs[k]).attr('rid'))
			}
		}
		new $.flavr({
			content:'确定删除选中的'+roleIdArr.length+'个角色？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/ctr/PatchDeleteRole",
							dataType:'text',
							data:{
								roleIdListStr:JSON.stringify(roleIdArr)
								},
							success:function(data){
								if(data=='succ'){
									$scope.getRolesList();
								}else{
									new $.flavr({
										content:'批量删除失败，请重试',
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

