<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
 /**
     * @Author      ： zhangMengchu
     * @Date        : 2018/7/4-16:54
     * @Description: .m3u8直播
     */
	  
	  String videoId= request.getParameter("videoId");
		
%>
<html>

	<head>
		<meta charset="UTF-8">
		<title>ckplayer</title>
		<script type="text/javascript" src="../../plugins/ckplayer/ckplayer.js"></script>
		<script type="text/javascript" src="../../plugins/jquery/jquery-3.2.1.min.js"></script>
		<style type="text/css">
		body{
			margin:0;
			padding:0px;
			font-family:"Microsoft YaHei",YaHei,"微软雅黑",SimHei,"黑体";
			font-size:14px;
			}
		</style>

	</head>
	<script type="text/javascript">
	$(document).ready(function () {
	var videoId="<%=videoId%>";
	
	if (videoId==null){
		
         videoId="41010000001320000139%40016%240";
		 
				}
	var url="http://10.4.234.183:7086/live/cameraid/"+videoId+"/substream/1.m3u8"
		
	var h = document.documentElement.clientHeight;
	
	$(".video").height(h);
	
	var videoObject = {
		container: '.video',//“#”代表容器的ID，“.”或“”代表容器的class
		variable: 'player',//该属性必需设置，值等于下面的new chplayer()的对象
		autoplay:true,//自动播放
		live:true,
		loaded:'loadedHandler', //当播放器加载后执行的函数	
		video:url//视频地址
	};
	
	var player=new ckplayer(videoObject);
	function loadedHandler() {
		alert("");
		player.addListener('error', errorHandler); //监听元数据
	}
 
	function errorHandler(e) {
		//出错了
		alert("");
		console.log(e);
	}
	});
	
	</script>
	<body>
	
	<div class="video" style="width: 100%;height: 500px"></div>
	
	</body>
</html>