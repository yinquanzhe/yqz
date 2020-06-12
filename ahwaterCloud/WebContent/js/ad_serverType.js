app.controller('serverTypeCtrl',['$scope','$http',function($scope,$http){

	//获取列表
	$scope.getServerType=function(){
		$http.get('/ahwaterCloud/ctr/listAllFlavors').success(function(serverTypeList){
			$scope.serverTypeData=serverTypeList
			$scope.serverTypeLength=serverTypeList.length;
			if(serverTypeList.length==0){
				$('.cloudlist>table>tbody>tr:first-child').html("暂无数据");
			}else{
				$('.cloudlist>table>tbody>tr:first-child').hide();
			}
		})
	}
	$scope.getServerType();	
	
	//删除
	$scope.delAdminServerType=function($event){
		var e=$event.target;
		new $.flavr({
			content:'确定删除该资源模板?',
			dialog:'confirm',
			buttons:{
       	确定:{
       		style:'danger',
       		action:function(){				
							$.ajax({
								url:'/ahwaterCloud/ctr/deleteFlavor',
								type:'get',
								dataType:'text',
								data:{
									"flavorId":$(e).attr('tid')
								},
								success:function(data){
									if(data=='succ'){
										$scope.getServerType();
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
	
	//全选与取消全选
	$scope.Typecnt=0;
  $('.actionBtn>button:last-child').attr('disabled',true);
  $('.cloudlist>table>thead>tr>th>input').click(function(){       
      var inputs=$(this).parent().parent().parent().next().children().children().children('input');
      if($(this).prop('checked')==true){
          $(this).prop('checked',true);
          inputs.prop('checked',true);
          $scope.Typecnt=$scope.serverTypeLength;
          $('.actionBtn>button:last-child').attr('disabled',false);
      }else{
          inputs.prop('checked',false);
          $scope.Typecnt=0
          $('.actionBtn>button:last-child').attr('disabled',true);
      }
  })
	
	//单个选择
	$scope.Typechoose=function($event){
    var input=$event.target;
    if($(input).prop('checked')==true){
        $(input).prop('checked',true);
        $scope.Typecnt++;
        if($scope.Typecnt==$scope.serverTypeLength){
          $('.cloudlist>table>thead>tr>th>input').prop('checked',true);
        }
    }else{
        $(input).prop('checked',false);
        $scope.Typecnt--;
        $('.cloudlist>table>thead>tr>th>input').prop('checked',false);
    }
    if($scope.Typecnt>0){$('.actionBtn>button:last-child').attr('disabled',false);}
    else{$('.actionBtn>button:last-child').attr('disabled',true);}
  }
	
	//批量删除
	$scope.removeServerTypeBatch=function(){
		var inputs=$('.cloudlist>table>tbody input[type="checkbox"]');		
			var serverTypeIdArr=[];
			for(k in inputs){
				if(inputs[k].checked){
					serverTypeIdArr.push($(inputs[k]).attr('tid'))
				}
			}
			new $.flavr({
				content:'确定删除选中的'+serverTypeIdArr.length+'个模板？',
				dialog:'confirm',
				buttons:{
					确定:{
						style:'danger',
						action:function(){
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/ctr/patchDeleteFlavor",
								dataType:'text',
								data:{
									flavorIdListStr:JSON.stringify(serverTypeIdArr)
									},
								success:function(data){														
									if(data=='succ'){
										$scope.getServerType();
										$('.actionBtn>button:last-child').attr('disabled',true)
									}
									else{
										new $.flavr({
										content:'删除失败,'+data,
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
	
	$scope.clearItem=function(){
		$('#serverTypeControl .allAccess').html('')
	}
	$scope.clearNewItem=function(){
		$('#newaccessControl .allAccess').html('')
	}
	
	//新建云主机类型
	$scope.createAdminServerType=function(){
		$("#createServerTypeModal").modal('show');
		$('#serverTypeControl .allAccess button').removeClass('selc');
		$('#createServerTypeModal #basicConfig').addClass('active').siblings().removeClass('active');
		$('#createServerTypeTab li:first-child').addClass('active').siblings().removeClass('active');
		$('#serverTypeName').val('');
		$('#serverTypeRam').val('');
		$('#serverTypeCPU').val('');
		$('#serverTypeDisk').val('');
		$("#createServerTypeTab a").on('click',function(e){
			e.preventDefault();
			$(this).tab('show');
		})
		
		$.ajax({
			type:"get",
			url:"/ahwaterCloud/ctr/listAllTenants",
			dataType:'json',
			success:function(controlList){
				var html;
				for(var i=0;i<controlList.length;i++){
					html=`<li  class="list-group-item" >${controlList[i].tennatName}
									<button  tid=${controlList[i].tenantId} tname=${controlList[i].tennatName} type="button"></button>
								</li>`
					$('#serverTypeControl .allAccess').append(html)
				}
				$('#serverTypeControl .allAccess button').click(function(){
					$(this).toggleClass('selc');
				})
				
			}
		});
						
		$scope.createServerTypeAction=function(){
			
			var btns=$('#serverTypeControl .allAccess li button.selc')
			
			
			var tenastArr=[];
			
			for(var i=0;i<btns.length;i++){
				tenastArr.push($(btns[i]).attr('tid'))
			}
			
			if(tenastArr.length==0){
				tenastArr="";
			}
			console.log(tenastArr);
			
			$("#createServerTypeModal").modal('hide')
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/ctr/createFlavor",
				dataType:'text',
				data:{
					"flavorName":$('#serverTypeName').val(),
					"ram":$('#serverTypeRam').val(),
					"vcpus":$('#serverTypeCPU').val(),
					"disk":$('#serverTypeDisk').val(),
					"swap":$('#serverTypeSDisk').val(),
					"ephemeral":$('#serverTypeTDisk').val(),
					"tenantIdListStr":tenastArr.length==0?tenastArr:JSON.stringify(tenastArr)
				},
				success:function(data){
					if(data=='succ'){
						$('#serverTypeControl .allAccess').html('')
						$scope.getServerType();	
					}else{
						new $.flavr({
							content:'创建失败，'+data,
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
	
	//编辑模板
	$scope.editServerType=function($event){
		$('#newaccessControl .allAccess button').removeClass('selc')
		$('#editTypeModal #bsConfig').addClass('active').siblings().removeClass('active')
		$('#editTypeModal #editServerTypeTab li:first-child').addClass('active').siblings().removeClass('active')
		var e=$event.target
		var ram=$(e).parent().prev().prev().prev().prev().prev().prev().html()
		$("#editTypeModal").modal('show')
		if(ram.indexOf('GB')!=-1){
			ram=parseFloat(ram)*1024
		}else{
			ram=parseFloat(ram)
		}
		$('#newserverTypeName').val($(e).parent().prev().prev().prev().prev().prev().prev().prev().prev().html());
		$('#newserverTypeRam').val(ram)
		$('#newserverTypeCPU').val(parseFloat($(e).parent().prev().prev().prev().prev().prev().prev().prev().html()))
		$('#newserverTypeDisk').val(parseFloat($(e).parent().prev().prev().prev().prev().prev().html()))
		$('#newserverTypeSDisk').val(parseFloat($(e).parent().prev().prev().prev().html()))
		$('#newserverTypeTDisk').val(parseFloat($(e).parent().prev().prev().prev().prev().html()))
		
		
		$("#editServerTypeTab a").on('click',function(e){
			e.preventDefault();
			$(this).tab('show')
		})
		
		$.ajax({
			type:"get",
			url:"/ahwaterCloud/ctr/listAllTenants",
			dataType:'json',
			success:function(controlList){
				var html;
				for(var i=0;i<controlList.length;i++){
					html=`<li  class="list-group-item" >${controlList[i].tennatName}
									<button  tid=${controlList[i].tenantId} tname=${controlList[i].tennatName} type="button"></button>
								</li>`
					$('#newaccessControl .allAccess').append(html)
				}
				$('#newaccessControl .allAccess button').click(function(){
					$(this).toggleClass('selc');
				})
				//获取能用该模板的租户列表
				$.ajax({
					type:'post',
					url:'/ahwaterCloud/ctr/haveTenantList',
					dataType:'json',
					data:{
						"flavorId":$(e).attr('tid')
					},
					success:function(defTenantList){
						var btns=$('#newaccessControl .allAccess button')
						for(var i=0;i<defTenantList.length;i++){
							for(var j=0;j<btns.length;j++){
								if(defTenantList[i].tenantId==$(btns[j]).attr('tid')){
									$(btns[j]).addClass('selc')
								}
							}
						}
					}
				})
				
			}
		});				

		$scope.editServerTypeAction=function(){
			var btns=$('#newaccessControl .allAccess li button.selc')
		
			var newtenastArr=[];
			
			for(var i=0;i<btns.length;i++){
				newtenastArr.push($(btns[i]).attr('tid'))
			}
		
			$("#editTypeModal").modal('hide')
			$.ajax({
				type:"POST",
				url:"/ahwaterCloud/ctr/editFlavor",
				dataType:'text',
				data:{
					"flavorId":$(e).attr('tid'),
					"flavorName":$('#newserverTypeName').val(),
					"ram":$('#newserverTypeRam').val(),
					"vcpus":$('#newserverTypeCPU').val(),
					"disk":$('#newserverTypeDisk').val(),
					"swap":$('#newserverTypeSDisk').val(),
					"ephemeral":$('#newserverTypeTDisk').val(),
					"tenantIdListStr":JSON.stringify(newtenastArr)
				},
				success:function(data){
					if(data=='succ'){
						$('#newaccessControl .allAccess').html('')
						$scope.getServerType();
						new $.flavr({
							content:'保存成功',
							modal:false,
							autoclose:true,
							timeout:3000,
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
	
	
	
}])