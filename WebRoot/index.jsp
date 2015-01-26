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
    <title>登录</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="ossh portal">
    <meta http-equiv="description" content="ossh portal login">
    <link type="text/css" href="css/index.css" rel="stylesheet" />
	<script type="text/javascript">
function _change() {
	/*
	1. 得到img元素
	2. 修改其src为/day11_3/VerifyCodeServlet
	*/
	var imgEle = document.getElementById("img");
	imgEle.src = "<%=path%>/VCodeServlet?a=" + new Date().getTime();
}
</script>
	
</head>
<% 
  String username=(String)session.getAttribute("username");
  if(username!=null){
  	request.getRequestDispatcher("/loginSucc.jsp").forward(request, response);
  	return;
  }else{
  %>
	<%
    String uname="";
    Cookie[] cs=request.getCookies();
    if(cs!=null){
    	for(Cookie c:cs){
    		if("uname".equals(c.getName())){
    			uname=c.getValue();
    		}
    		
    	}
    }
    
    
    String message="";
    String msg=(String)request.getAttribute("msg");
    if(msg!=null){
    	message=msg;
    }
    %>
<body>
    <div id="page-content">
        <div id="login-page">
            <div id="logo">
                <a href="http://127.0.0.1<%=path%>"><img alt="LaterThis" src="images/logo.png" /></a>
            </div>
           
  
            <form id="loginForm" action="<%=path%>/Login" method="post">
                <div id="normal-login">
                    <p>
                        <label style="text-align: center ;"><font color="red"><b><%=message%></b></font></label> <br/>
                    </p>
                    <p style="padding-top:5px;">
                        <label for="username">用户名</label> <br/>
                        <input id="username" name="username" value="<%=uname%>" />
                    </p>
                    <p>
                        <label for="password">密码</label> <br/> 
                        <input id="password" type="password" name="password" value="" />
                    </p>
                    <p>
                        <label for="password">验证码</label> <br/> 
                        <input id="vcode"  name="vcode" value="" size="4" />        <img id="img" alt="请输入验证码"
			src="<%=path%>/VCodeServlet" /><a href="javascript:_change()">      换一张</a><br />
                    </p>
	                <p>
	                    <input id="loginSubmit" class="button" type="submit" value="登录" name="commit" />
	                    <span style="padding:5px;"></span>
	                    <input class="button" type="reset" value="重置" name="reset" />
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