<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>图片管理界面</title>
<script src="http://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://unpkg.com/qiniu-js@2.2.1/dist/qiniu.min.js"></script>
</head>
<body>
	<script>
		$(function($) {
			$.get("http://localhost:8080/qiniu/images", function(list) {

				var table = $("<table border=\"1\">");

				$(list).each(function(i, items) {
					if (i == 0) {
						var tr = $("<tr></tr>");
						tr.appendTo(table);
						
						var td = $("<td></td>");
						td.appendTo(tr);
						
						for (key in items) {
							var td = $("<td>" + key + "</td>");
							td.appendTo(tr);
						}
					}

					var tr = $("<tr></tr>");
					tr.appendTo(table);
					
					for (key in items) {						
						var td = $("");
						if(key == "key") {
							td = $("<td></td>");
							var fileName = items[key];
							
							var button = $("<input type=\"button\" value=\"删除\" />").click(fileName, function(){
								$.ajax({
								    type: "delete",  
								    url: "http://localhost:8080/qiniu/images/" + fileName,  
								    success: function(result){
								    	window.location.reload();
								    }
								});
							});
							button.appendTo(td);
							td.appendTo(tr);
							
							td = $("<td id=\"" +fileName+ "\"></td>");
							td.appendTo(tr);	
							$.get("http://localhost:8080/qiniu/images/" + fileName, (fileName, function(publicUrl){
								//alert(td);
								var a = $("<a href=\"" + publicUrl + "\">" + fileName + "</a>");
								a.appendTo($("#" + fileName));
							}));
						} else{
							td = $("<td>" + items[key] + "</td>");
							td.appendTo(tr);
						}
					}
					
				});

				table.append("</table>");

				$("html").append(table);
			});
		})(jQuery);
	</script>
</body>
</html>