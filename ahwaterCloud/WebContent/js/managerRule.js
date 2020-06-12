app.controller('magerRuleCtrl',['$scope','$http','$routeParams',function($scope,$http,$routeParams){
	$scope.seName=$routeParams.seName
	$scope.getRules=function(){
		$http({
			url:'/ahwaterCloud/contr/ListGroupRules',
			method:'GET',
			params:{securityGroupId:$routeParams.seId},
			headers:{'Content-Type':'application/json'}
		}).success(function(rulesList){
			$scope.rulesData=rulesList;
			$scope.rlen=rulesList.length;
			$('.cloudlist table tbody tr:first-child').hide();
		})
	}
	$scope.getRules();
	
	
	
	//删除规则
	$scope.delRule=function($event){
		var e=$event.target;
		var ruleArr=[];
		ruleArr.push($(e).attr('rid'))
		new $.flavr({
			content:'确定删除该条规则？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeleteRule",
							dataType:'json',
							data:{ruleId:JSON.stringify(ruleArr)},
							success:function(data){
								if(data=='删除成功 1 个，失败 0 个！'){
									$scope.getRules();
								}else{
									new $.flavr({ 
					          content : '删除失败',
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

	//批量选择
	$scope.kcnt=0;
	$('.rulesBox .search>button.delRuleBtn').attr('disabled',true);
	$('.rulesBox .cloudlist>table>thead>tr>th>input').click(function(){
        var inputs=$(this).parent().parent().parent().next().children().children().children('input');
        if($(this).prop('checked')==true){
            $(this).prop('checked',true);
            inputs.prop('checked',true);
            $scope.kcnt=$scope.rlen
            $('.rulesBox .search>button.delRuleBtn').attr('disabled',false);
        }else{
            inputs.prop('checked',false);
            $scope.kcnt=0
            $('.rulesBox .search>button.delRuleBtn').attr('disabled',true);
        }
  })	
	$scope.choose=function($event){
        var input=$event.target;
        console.log(input);
        if($(input).prop('checked')==true){
            $scope.kcnt++;
            if($scope.kcnt==$scope.rlen){
              $('.rulesBox .cloudlist>table>thead>tr>th>input').prop("checked",true);
            }
        }else{
            $scope.kcnt--;
            $('.rulesBox .cloudlist>table>thead>tr>th>input').prop('checked',false);
        }
        if($scope.kcnt>0){$('.rulesBox .search>button.delRuleBtn').attr('disabled',false);}
        else{$('.rulesBox .search>button.delRuleBtn').attr('disabled',true);}
  }
	
	//批量删除规则
	$scope.deleteRules=function(){
		var inputs=$('.rulesBox .cloudlist>table>tbody input[type="checkbox"]');		
		var checkRuleName=[];
		for(k in inputs){
			if(inputs[k].checked){
				checkRuleName.push($(inputs[k]).attr('rid'))
			}
		}
		console.log(checkRuleName);	
		new $.flavr({
			content:'确定删除选中的这些规则？',
			dialog:'confirm',
			buttons:{
				确定:{
					style:'danger',
					action:function(){
						$.ajax({
							type:"POST",
							url:"/ahwaterCloud/contr/DeleteRule",
							dataType:'json',
							data:{ruleId:JSON.stringify(checkRuleName)},
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
	
	
	//添加规则
	$scope.addRule=function(){
		$('.addRuleModal').show();
		$('#openPort').val('false')
		$('#remote').val('true')
		$('.ipProtocolBox,.portStartBox,.portEndtBox,.icmpTypeBox,.icmpCodeBox,.securityZuneBox,.etherTypeBox').hide();
		$('.directionBox,.openPortBox,.portBox,.remoteBox,.cidrBox').show();
		$scope.hideRuleBox=function(){
			$('.addRuleModal').hide();
		}
		//获取安全组列表
		$http.get('/ahwaterCloud/contr/ListAllSecurityGroup').success(function(SecurityGroup){
			$scope.SecurityGroupData=SecurityGroup;
		})
		
		//规则change
		$('#ruleList').change(function(e){
			var k=$(e.target).children('option:selected').attr('key');
			console.log(k)
			switch (k){
				case '定制TCP规则' :
				case '定制UDP规则' :
					$('.ipProtocolBox,.portStartBox,.portEndtBox,.icmpTypeBox,.icmpCodeBox').hide();
					$('.directionBox,.openPortBox,.portBox,.remoteBox,.cidrBox').show();
					break;
				case '定制ICMP规则':
					$('.ipProtocolBox,.portStartBox,.portEndtBox,.openPortBox,.portBox').hide();
					$('.directionBox,.icmpTypeBox,.icmpCodeBox,.remoteBox,.cidrBox').show();
					break;
				case'其他协议':
					$('.portStartBox,.portEndtBox,.icmpTypeBox,.icmpCodeBox,.openPortBox,.portBox').hide();
					$('.directionBox,.ipProtocolBox,.remoteBox,.cidrBox').show();
					break;
				case '所有TCP协议':
				case '所有UDP协议':
				case '所有ICMP协议':
					$('.portStartBox,.portEndtBox,.ipProtocolBox,.icmpTypeBox,.icmpCodeBox,.openPortBox,.portBox').hide();
					$('.directionBox,.remoteBox,.cidrBox').show();
					break;
				default:
					$('.directionBox,.portStartBox,.portEndtBox,.ipProtocolBox,.icmpTypeBox,.icmpCodeBox,.openPortBox,.portBox').hide();
					$('.remoteBox,.cidrBox').show();
					break;
			}
		})
		//远程change
		$('#remote').change(function(e){
			console.log($(e.target).val())
			var k=$(e.target).val()
			switch (k){
				case 'true':
					$('.cidrBox').show();
					$('.securityZuneBox,.etherTypeBox').hide();
					break;
				case 'false':
					$('.cidrBox').hide();
					$('.securityZuneBox,.etherTypeBox').show();
					break;
			}
		})
		
		//端口 端口范围
		$('#openPort').change(function(e){
			var k=$(e.target).val()
			switch (k){
				case 'false':
					$('.portBox').show();
					$('.portStartBox,.portEndtBox').hide();
					break;
				case 'true':
					$('.portBox').hide();
					$('.portStartBox,.portEndtBox').show();
					break;
			}
		})
		
		//确定添加
		$scope.addRuleAction=function(){
			var k=$("#ruleList").children('option:selected').attr('key');
			$('.addRuleModal').hide();
			var startport,endport;
		  if(k=='SSH'){
				startport=endport='22'
			}else if(k=='SMTP'){
				startport=endport='25'
			}else if(k=='DNS'){
				startport=endport='53'
			}else if(k=='HTTP'){
				startport=endport='80'
			}else if(k=='POP3'){
				startport=endport='110'
			}else if(k=='IMAP'){
				startport=endport='143'
			}else if(k=='LDAP'){
				startport=endport='389'
			}else if(k=='HTTPS'){
				startport=endport='443'
			}else if(k=='SMTPS'){
				startport=endport='465'
			}else if(k=='IMAPS'){
				startport=endport='993'
			}else if(k=='POP3S'){
				startport=endport='995'
			}else if(k=='MYSQL'){
				startport=endport='3306'
			}else if(k=='RDP'){
				startport=endport='3389'
			}else if(k=='MS SQL'){
				startport=endport='1433'
			}else if(k=='其他协议'){
				startport='0'
				endport='65535'
			}else	if($('#openPort').val()=='false'){
				startport=$('#port').val();
				endport=$('#port').val();
			}else{
				startport=$('#portStart').val();
				endport=$('#portEnd').val()
			}
			$.ajax({
				url:'/ahwaterCloud/contr/CreateRule',
				type:'post',
				dataType:'json',
				data:{
					securityGroupId:$routeParams.seId,
					protocol:$('#ruleList').val()=='other'?$('#ipProtocol').val():$('#ruleList').val(),
					direction:$('#derection').val(),
					hasPort:$('#openPort').val(),
					portRangeMin:startport,
					portRangeMax:endport,
					isCustomisedIcmp:$('#ruleList').val()=='定制ICMP规则',
					icmpType:$('#icmpType').val(),
					icmpCode:$('#icmpCode').val(),
					isCIDR:$('#remote').val(),
					remoteIpPrefix:$('#cidr').val(),
					remoteGroupId:$('#securityZune').val(),
					etherType:$('#etherType').val()
				},
				success:function(data){
					if(data=="success"){
						new $.flavr({
							content:'添加成功',
							autoclose:true,
							timeout:2500,
							buttons:{确定:{}}
						})
						$scope.getRules();
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
			})
		}
	}
}])