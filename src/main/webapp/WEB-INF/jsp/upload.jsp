<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>图片上传界面</title>
<script src="http://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://unpkg.com/qiniu-js@2.2.1/dist/qiniu.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#upload1").click(function(){
		var timestamp = Date.parse(new Date());
		$("#timestamp").val(timestamp);
		$.get("http://localhost:8080/qiniu/upToken/" + $("#timestamp").val(), function(token){
			var file1 = $("#localFilePath1").get(0).files[0];
			var reader = new FileReader(); //新建一个FileReader
	        reader.readAsDataURL(f);  // 读取文件base64数据
			var key = $("#timestamp").val();
			var observable = qiniu.upload(file, key, token, { mimeType: ["image/png", "image/jpeg", "image/gif"] }, null);
			var observer = {
				next(res){
					// ...
				},
				error(err){
					// ...
				}, 
				complete(res){
					$.get("http://localhost:8080/qiniu/images/" + $("#timestamp").val(), function(publicUrl){
						$("img").attr("src", publicUrl);
					});
				}
			};
			var subscription = observable.subscribe(observer); // 上传开始
		});
	});
	
	/* $("#upload2").click(function(){
		var timestamp = Date.parse(new Date());
		$("#timestamp").val(timestamp);
		var formData = new FormData();
		formData.append("file", $("#localFilePath1").get(0).files[0]);
		$.ajax({
			url: "http://localhost:8080/qiniu/images",
			type: "post",
			data: formData,
			cache: false,
            contentType: false,
            processData: false,
			success: function( response ) {
			}
		});
	}); */
});
</script>
</head>
<body>
	<form action="http://localhost:8080/qiniu/images" method="post" >
		<input id="localFilePath1" name="file[0]" type="text" />
		<br />
		<input id="localFilePath2" name="file[1]" type="text" />
		<br />
		<input id="localFilePath3" name="file[2]" type="text" />
		<br />
		<br />
		<input type="button" id="upload1" value="客户端上传" />
		<input type="submit" id="upload2" value="服务器上传" />
		<br />
		<br />
		<input type="hidden" id="timestamp" />
		<img id="img" />
	</form>
</body>
</html>