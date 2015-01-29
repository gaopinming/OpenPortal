<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
String portalPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath%>">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <title>用户登录成功</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="ossh portal">
    <meta http-equiv="description" content="ossh portal login">
    <link type="text/css" href="css/index.css" rel="stylesheet" />
</head>
 <%
    String username=(String)session.getAttribute("username");
    String ip=(String)session.getAttribute("ip");
    String message="";
    String msg=(String)request.getAttribute("msg");
    if(msg!=null){
    	message=msg;
    }
    
    if(username==null){
    	request.setAttribute("msg", "您还没有登录，请先登录！");
    	request.getRequestDispatcher("/index.jsp").forward(request, response);
    	return;
    }
    else{
    %>
<body>
    <div id="page-content">
        <div id="login-page">
            <div id="logo">
                <a href="http://127.0.0.1<%=path%>"><img alt="LaterThis" src="images/logo.png" /></a>
            </div>
           <form id="loginForm" action="<%=path%>/LoginOut" method="post">
              <div id="success-login">
              <p>
                        <label style="text-align: center ;"><font color="red"><b><%=message%></b></font></label> <br/>
                    </p>
			        <p>
			          <label for=success-user>您已登录成功，可以连接办公网，请不要关闭该窗口！！欢迎您：</LABEL><span id="success-user" class="id-note"><font color="red"><b><%=username%></b></font></span>
			          <br/>
			           <label for=success-user>IP地址：</LABEL><span id="success-user" class="id-note"><font color="red"><b><%=ip%></b></font></span>
			           <br/>
			        </p>
			        <p>
			          <input id="loginoffSubmit" class="button" type="submit" value="退出" name="logoff" />  
			        </p>
			      </div>
              </form>
                <p id="signup">
                   Copyright &copy; 2014 - 2015 <a href="http://127.0.0.1<%=path%>">PortalServer服务-李硕</a>.  All Rights Reserved.
               </p>
        </div>
    </div>
</body>
<%} %>
</html>