<%
String url = request.getRequestURL().toString();
String host = request.getServerName();
String port = String.valueOf(request.getServerPort());
%>
<div id="header">
	<a href="workbench.jsp" style="margin-left: 10px;" class="<%=url.indexOf("workbench.jsp") == -1 ? "ui-corner-top" : "current ui-corner-top"%>">Browse</a><a
	href="import.jsp" class="<%=url.indexOf("import.jsp") == -1 ? "ui-corner-top" : "current ui-corner-top"%>">Import</a><a
	href="search.jsp" class="<%=url.indexOf("search.jsp") == -1 ? "ui-corner-top" : "current ui-corner-top"%>">Search</a><a
	href="admin.jsp" class="<%=url.indexOf("admin.jsp") == -1 ? "ui-corner-top" : "current ui-corner-top"%>">Admin</a><a
	href="http://<%=host%>:<%=port%>/TPEN" class="ui-corner-top" target="_blank">Transcribe in TPen</a>
</div>