package Portal.Web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import Portal.Server.Action;

public class Login extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try {
			HttpSession session=request.getSession();
			String username=(String)session.getAttribute("username");
			String password=(String)session.getAttribute("password");
		    String ip=(String)session.getAttribute("ip");
		    if((ip.equals("")||ip==null)||(username.equals("")||username==null)||(password.equals("")||password==null)){
		    	request.setAttribute("msg", "非法访问！");
		    	request.getRequestDispatcher("/index.jsp").forward(request, response);
		    	return;
		    }else{
		    	request.setAttribute("msg", "请不要重复刷新！");
		    	request.getRequestDispatcher("/index.jsp").forward(request, response);
		    	return;
		    }
			
		} catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("msg", "请重新登录！");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
		}
		
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 校验验证码
		 * 1. 从session中获取正确的验证码
		 * 2. 从表单中获取用户填写的验证码
		 * 3. 进行比较！
		 * 4. 如果相同，向下运行，否则保存错误信息到request域，转发到login.jsp
		 */
		request.setCharacterEncoding("utf-8");
		String sessionCode = (String) request.getSession().getAttribute("session_vcode");
		String paramCode = request.getParameter("vcode");
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		String ip=request.getRemoteAddr();//获取客户端的ip
		
		if((username.equals(""))||(username==null)) {
			request.setAttribute("msg", "用户名不能为空！");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
		}
		if((password.equals(""))||(password==null)) {
			request.setAttribute("msg", "密码不能为空！");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
		}
		
		if(!paramCode.equalsIgnoreCase(sessionCode)) {
			request.setAttribute("msg", "验证码错误！");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
		}
		
		
		String bas_ip;
		String bas_port;
		String portal_port;
		String sharedSecret;
		String authType;	
		String timeoutSec;	
		String portalVer;
		String cfgPath = request.getRealPath("/");//获取服务器的webroot路径
		FileInputStream fis = null;
		Properties config=new Properties();
		File file = new File(cfgPath+"config.properties");
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("config.properties 配置文件不存在！！");
			request.setAttribute("msg", "config.properties 配置文件不存在！！");
	    	request.getRequestDispatcher("/index.jsp").forward(request, response);
	    	return;
		}
		  
		try {
			config.load(fis);
			bas_ip=config.getProperty("bas_ip");
			bas_port=config.getProperty("bas_port");
			portal_port=config.getProperty("portal_port");
			sharedSecret=config.getProperty("sharedSecret");
			authType=config.getProperty("authType");	
			timeoutSec=config.getProperty("timeoutSec");	
			portalVer=config.getProperty("portalVer");	
//			#chap 0 pap 1
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("config.properties 数据库配置文件读取失败！！");
			request.setAttribute("msg", "config.properties 数据库配置文件读取失败！！");
	    	request.getRequestDispatcher("/index.jsp").forward(request, response);
	    	return;
		}
		System.out.println(config);
		
		
		int info=new Action().Method("Login",username, password, ip, bas_ip, bas_port, portalVer, authType, timeoutSec, sharedSecret);
		if(info==0 || info==22){
			Cookie cookie=new Cookie("uname", username);
			cookie.setMaxAge(60*60*24);
			response.addCookie(cookie);
			HttpSession session=request.getSession();
			session.setAttribute("username", username);
			session.setAttribute("password", password);
			session.setAttribute("ip", ip);
			request.setAttribute("msg", "登录成功！");
//			request.getRequestDispatcher("/index.jsp").forward(request, response);
			String path = request.getContextPath();
			response.sendRedirect(response.encodeUrl(path+"/loginSucc.jsp"));
		}
		else{
			if(info==01){
				request.setAttribute("msg", "请求Challenge超时！");
			}else if(info==02){
				request.setAttribute("msg", "用户认证请求超时！");
			}else if(info==11){
				request.setAttribute("msg", "请求Challenge被拒绝!");
			}else if(info==12){
				request.setAttribute("msg", "请求的Challenge链接已建立!");
			}else if(info==13){
				request.setAttribute("msg", "有一个用户正在认证过程中，请稍后再试!");
			}else if(info==14){
				request.setAttribute("msg", "用户请求Challenge失败（发生错误）");
			}else if(info==21){
				request.setAttribute("msg", "用户认证请求被拒绝");
			}else if(info==22){
				request.setAttribute("msg", "用户链接已建立");
			}else if(info==23){
				request.setAttribute("msg", "有一个用户正在认证过程中，请稍后再试");
			}else if(info==24){
				request.setAttribute("msg", "用户认证失败（发生错误）");
			}else if(info==55){
				request.setAttribute("msg", "暂时不支持该Portal 2   ！！");
			}else if(info==66){
				request.setAttribute("msg", "暂时不支持PAP认证方式  ！！");
			}else if(info==99){
				request.setAttribute("msg", "未知错误！！");
			}
			
			RequestDispatcher qr=request.getRequestDispatcher("/index.jsp");
			qr.forward(request, response);
		}
	}

}
