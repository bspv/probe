<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>发送消息</title>
    
	<script src="static/js/jquery-2.1.4.min.js"></script>
	<script type="text/javascript">
	function randomNum(_min,_max){   
		var _range = _max - _min;   
		var _rand = Math.random();   
		return (_min + Math.round(_rand * _range));   
	}
	function sendMsg(){
		var _idx = randomNum(1,10);
		var _pageSize = randomNum(20,60);
		var d = new Date();
		var _current = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes() + ':' + d.getSeconds() + ':' + d.getMilliseconds();
		$('#_idx').val(_idx);
		$('#_pageSize').val(_pageSize);
		$('#_current').val(_current);
		$.ajax({
			url : 'sendMsg',
			type : 'post',
			data : {
				'idx':_idx,
				'pageSize':_pageSize,
				'current':_current
			},
			success : function(res, status) {
				var r = $.parseJSON(res);
				if(r.success){
					var _data = $.parseJSON(r.data);
					alert(_data.msgId);
				}else{
					alert(r.message);
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("网络通讯异常");
			}
		});
	}
	</script>
</head>
<body>
	<table>
		<tr>
			<td>页码：</td>
			<td><input type="text" id ="_idx" /></td>
		</tr>
		<tr>
			<td>Size：</td>
			<td><input type="text" id ="_pageSize" /></td>
		</tr>
		<tr>
			<td>时间：</td>
			<td><input type="text" id ="_current" /></td>
		</tr>
		<tr>
			<td colspan=2><input type="button" value="提交" onclick ="javascript:sendMsg()"/></td>
		</tr>
	</table>
</body>
</html>