<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Info</title>
    
	<script src="static/js/jquery-2.1.4.min.js"></script>
	<script type="text/javascript">
	function randomNum(_min,_max){   
		var _range = _max - _min;   
		var _rand = Math.random();   
		return (_min + Math.round(_rand * _range));   
	}
    function findUser(){
        var _idx = randomNum(1,2);
        $('#_idx').val(_idx);
        $.ajax({
            url : 'userAjax',
            type : 'post',
            data : {
                'idx':_idx
            },
            success : function(res, status) {
                var r = $.parseJSON(res);
                if(r.success){
                    alert(JSON.stringify(r.data));
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
	${user.id}<br>
	${user.userName}<br>
	${user.password}<br>
	${user.mobile}<br>
	${user.regTime?string("yyyy-MM-dd HH:mm:ss")}<br>
    <table>
        <tr>
            <td>页码：</td>
            <td><input type="text" id ="_idx" /></td>
        </tr>
        <tr>
            <td colspan=2><input type="button" value="提交" onclick ="javascript:findUser();"/></td>
        </tr>
    </table>
</body>
</html>