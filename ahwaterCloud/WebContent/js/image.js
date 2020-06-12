/**
 * Created by zhongyue on 2016/12/14.
 */

app.controller('imgCtrl',["$scope","$http",function($scope,$http){
		var selectTenant=$('#dropdownTenant').attr('tid')
   	
   $scope.getImage=function(){
   		$http({
				method :'POST',
				url:'/ahwaterCloud/cont/listAllImages',
				params :{tenantId:selectTenant},
				headers : {'Content-Type':'application/json'}
			}).success(function(imgListAll){
    	for(var i=0;i<imgListAll.length;i++){
    		if(imgListAll[i].imageOwner==selectTenant){
    			imgListAll[i].showSomeaction=true;
    		}
    	}    	
        $scope.imgListAllData=imgListAll;
        $scope.imgList=$scope.imgListAllData;
        $scope.len=$scope.imgList.length
        
    })
    //$http.get('/ahwaterCloud/cont/listAllImages').success(function(imgListShare){
    //    $scope.imgListShareData=[];
    //})
   $http.get('/ahwaterCloud/cont/listPublicImages').success(function(imgListPub){
        $scope.imgListPubData=imgListPub;
   })
   }
   
   $scope.getImage();
   
   //显示操作系统信息
		$scope.showtoolBar=function($event){
			var e=$event.target;
			$(e).tooltip('show');
		}
		//显示用户所有的
    $scope.showAllImg=function(){
    	$scope.imgList=$scope.imgListAllData;
    	console.dir($scope.imgList.length);
    	$scope.len=$scope.imgList.length
    }
    //显示共享的
    /*$scope.showShareImg=function(){
    	$scope.imgList=$scope.imgListShareData;
    	$scope.len=$scope.imgList.length
    }*/
    //显示共有的
    $scope.showPubImg=function(){
    	$scope.imgList=$scope.imgListPubData;
    	$scope.len=$scope.imgList.length
    }
    //按钮点击切换
    $('.class-group').on('click','a',function(e){
        e.preventDefault();
        $(this).addClass('active').siblings().removeClass('active');
    })
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
										content:'成功删除'+delCnt+'个镜像，失败'+failArr.length+'个 ,'+failArr[0],
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
					确定:{
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
										newName:name
									},
									success:function(data){
										if(data=='succ'){		
											new $.flavr({ 
								          content : '修改成功',
								          autoclose : true,
								          timeout : 3500,
								          buttons:{
								            	确定:{}
								          }
						        	});
						        	$scope.getImage();
						        	$(e).parent().siblings('.imageAllName').children('span').children('a').html(name);
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
											modal:false,
											autoclose:true,
											timeout:2500,
											buttons:{确定:{action:function(){location.reload()}}}
										})
										$scope.getImage()
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

	//创建云硬盘
	$scope.createVolume=function($event){
		var e=$event.target;
		console.log($(e).attr('iname'))
		var imgid=$(e).attr('iid')
		
		var html = `<div class="form-row"> 
									<input style="color:#333" type="text" name="volumeName"  placeholder="请输入云硬盘名称"/> 
								</div>
								<div class="form-row"> 
									<input style="color:#333" type="hidden" name="imageId" value=${imgid}  disabled/> 
								</div>
								<div class="form-row">
									<textarea class="form-control" placeholder="输入描述" name="volumeDescription" rows="3" cols="30" style="resize: none; color: #333; box-shadow:none;border-radius:0;"></textarea>
								</div>
								<div class="form-row">
									<input style="color:#333" type="number" name="volumSize" placeholder="请输入云硬盘大小(GB)"/> 
								</div>`;

    new $.flavr({ 
    	title : '创建云硬盘', 
      content : '将当前镜像('+$(e).attr('iname')+')作为源创建云硬盘', 
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
							type:"GET",
							url:"/ahwaterCloud/cont/createVolumefromImage?"+$form.serialize(),
							dataType:'json',
							data:{},
							success:function(data){
								console.log(data);
								if(data=="success"){
									new $.flavr({ 
				          content : '创建成功',         
				          buttons:{
				            	确定:{}
				          }
				        	});
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
	
	
	$('#myTab a').click(function (e) {
        e.preventDefault()
        $(this).tab('show')
    })
    $scope.percent20=function(){
        $scope.percentage=20;
        $('.progress>div').css({"width":"20%"})
    }
    $scope.percent40=function(){
        $scope.percentage=40;
        $('.progress>div').css({"width":"40%"})
    }
    $scope.percent60=function(){
        $scope.percentage=60;
        $('.progress>div').css({"width":"60%"})
    }
    $scope.percent80=function(){
        $scope.percentage=80;
        $('.progress>div').css({"width":"80%"})
    }
    $scope.percent100=function(){
        $scope.percentage=100;
        $('.progress>div').css({"width":"100%"})
    }
	
	//创建云主机
	$scope.createHost=function($event){
				var e=$event.target;
				console.log($(e).attr('iid'))
				
				
				
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
	          <td>${data[0].ephemeral}GB</td>
	        </tr>
	        <tr>
	          <th>临时磁盘</th>
	          <td>${data[0].swap}GB</td>
	        </tr>
	        <tr>
	          <th>磁盘总计</th>
	          <td>${data[0].disk}GB</td>
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
        
        
        //获取安全组
        $http.get('/ahwaterCloud/ctr/securityGroupsList').success(function(SecurityList){
        	$scope.securityList=SecurityList
        	if(SecurityList.length==0){
                $scope.securityList=['无安全组']
            }
        })
        
        
        ////获取镜像列表
        $http.get('/ahwaterCloud/cont/listImageNameId').success(function(ImageList){
            $scope.imageList=ImageList;
            if(ImageList.length==0){
                $scope.imageList=['无可用镜像']
            }else{
            	$('#imgName').val($(e).attr('iid'))
            }
        })
    }
	
	
	
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
	          <td>${data[0].ephemeral}GB</td>
	        </tr>
	        <tr>
	          <th>临时磁盘</th>
	          <td>${data[0].swap}GB</td>
	        </tr>
	        <tr>
	          <th>磁盘总计</th>
	          <td>${data[0].disk}GB</td>
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
              // HostStartSrc:$('#startSrc').val(),
               imageId:$('#imgName').val(),
              // HostShot:$('#quickShot').val(),
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
            	new $.flavr({ 
            		content : '成功创建'+succCnt+'个实例,'+'失败'+failedArr.length+'个,进入实例查看详细',               
                buttons:{确定:{}}            
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
