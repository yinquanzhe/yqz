
app.controller('adimgCtrl',["$scope","$http",function($scope,$http){
		var selectTenant=$('#dropdownTenant').attr('tid')
   	
   	$http({
				method :'POST',
				url:'/ahwaterCloud/cont/adminListAllImages',
				params :{tenantId:selectTenant},
				headers : {'Content-Type':'application/json'}
			}).success(function(imgListAll){
    		$scope.adminimgList=imgListAll
    		$scope.len=imgListAll.length;
    		if(imgListAll.length==0){
    			$('#adImageBox table>tbody>tr:first-child td').html('暂无数据');
    		}else{
    			$('#adImageBox table>tbody>tr:first-child').hide();
    		}
    	})    	
        
   
   
   //显示操作系统信息
		$scope.showtoolBar=function($event){
			var e=$event.target;
			$(e).tooltip('show');
		}
		
    //全选
    $scope.cnt=0;
    $('.classified>button.deleteImg').attr('disabled',true);
    $('.cloudlist>table>thead>tr>th>input').click(function(){
        $scope.canAction=false;
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.cnt=$scope.len;
            $('.classified>button.deleteImg').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.cnt=0
            $('.classified>button.deleteImg').attr('disabled',true);
        }
    })
    //单个选择
    $scope.choose=function($event){
        var input=$event.target;
        if($(input).prop('checked')==true){
            $(input).prop('checked',true);
            $scope.cnt++;
            if($scope.cnt==$scope.len){
                $('.cloudlist>table>thead>tr>th>input').prop('checked',true);
            }
        }else{
            $(input).prop('checked',false);
            $scope.cnt--;
            $('.cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.cnt>0){$('.classified>button.deleteImg').attr('disabled',false);}
        else{$('.classified>button.deleteImg').attr('disabled',true);}
    }
    //批量删除
   $scope.deleteImgBatch=function(){
   	var inputs=$('.cloudlist>table>tbody input[type="checkbox"]');		
			var delImageId=[];
			for(k in inputs){
				if(inputs[k].checked){
					delImageId.push($(inputs[k]).attr('iid'))
				}
			}
			console.log(delImageId);	
			new $.flavr({
				content:'确定删除选中的'+delImageId.length+'个镜像？',
				dialog:'confirm',
				buttons:{
					确定:{
						style:'danger',
						action:function(){
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/cont/deleteMultiImage",
								dataType:'json',
								data:{imageIds:JSON.stringify(delImageId)},
								success:function(data){
									console.log(data)
									$scope.showAllImg();
									var delCnt=0,failArr=[];
									for(var i=0;i<data.length;i++){
										if(data[i]=='succ'){
											delCnt++;
										}else{
											failArr.push(data[i]);
										}
									}
									new $.flavr({
										content:'成功删除'+delCnt+'个镜像，失败'+failArr.length+'个 '+failArr[0],
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
   
   
   //编辑镜像
   $scope.editImage=function($event){  	
   	var e=$event.target;
			new $.flavr({ 
				content : '编辑镜像', 
				dialog : 'prompt',
				prompt : { 
					placeholder: '输入新的名称' 
				}, 
				buttons:{
					保存:{
						style:'danger',
						action:function($container,$prompt){ 						
								var name=$prompt.val();						
								if(name==''){
									this.shake();
									return false
								}
								else{
									$.ajax({
									type:'POST',
									dataType:'json',
									url:'/ahwaterCloud/cont/updateImageName',
									data:{
										imageId:$(e).attr('iid'),
										newName:name+'#'+$(e).attr('ostype')+'#'+$(e).attr('osvesion')+'#'+$(e).attr('osbit')
									},
									success:function(data){
										if(data=='succ'){		
											new $.flavr({ 
								          content : '修改成功',
								          modal:false,
								          autoclose : true,
								          timeout : 3500,
								          buttons:{
								            	确定:{}
								          }
						        	});
						        	$(e).next().html(name);
										}
										else{
											new $.flavr({ 
								          content : '修改失败,提示信息:'+data,					          
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
   
   //删除镜像
   $scope.deleteImg=function($event){
   	var e=$event.target;
   	new $.flavr({
				content:'确定删除该镜像？',
				dialog:'confirm',
				buttons:{
					确定:{
						style:'danger',
						action:function(){
							$.ajax({
								type:"POST",
								url:"/ahwaterCloud/cont/deleteImage",
								dataType:'json',
								data:{imageId:$(e).attr('iid')},
								success:function(data){
									console.log(data)
									if(data=='succ'){
										new $.flavr({
											content:'删除成功',
											autoclose:true,
											timeout:3000,
											buttons:{确定:{action:function(){location.reload()}}}
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


	
	$('.tab-content>div>div').css({
		"width":"45%",
		"float":"left",
		"marginRight":"30px"
		})
	
		
	
	//查看镜像详情
	$scope.togDetailBox=function($event){
		var e=$event.target;	
		$(e).parent().parent().parent().siblings().children('.imageAllName').children('span').children('.imageDetailBox').removeClass('showed');
		$('.imgDetailTab a').click(function (e) {
		  e.preventDefault()
		  $(this).parent().addClass('active').siblings().removeClass('active');
		  $($(this).attr('href')).addClass('active').siblings().removeClass('active');
		})
		$http({
			url:'/ahwaterCloud/cont/imageDetail',
			method:'get',
			params:{imageId:$(e).attr('iid')},
			headers:{'Content-Type':'application/json'}
		}).success(function(imgdetail){
			$(e).siblings('.imageDetailBox').toggleClass('showed');
			$scope.imgDetailData=imgdetail;
		})
	}
	
	$scope.imageDetailClose=function(){
		$('.imageDetailBox').removeClass('showed');
	}
	
}])
