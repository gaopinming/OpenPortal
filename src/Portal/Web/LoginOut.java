package Portal.Web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import Portal.Server.Action;

public class LoginOut extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setAttribute("msg", "请不要重复刷新！");
		request.getRequestDispatcher("/index.jsp").forward(request, response);
		
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
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
		
		
		
		
		try {
			HttpSession session=request.getSession();
			String ip=(String)session.getAttribute("ip");
			String username=(String)session.getAttribute("username");
			String password=(String)session.getAttribute("password");
			int info=99;
			if(!(ip.equals("")||ip==null)){
				info=new Action().Method("LoginOut",username, password, ip, bas_ip, bas_port, portalVer, authType, timeoutSec, sharedSecret);
			}else{
				session.removeAttribute("username");
				session.removeAttribute("password");
				request.setAttribute("msg", "退出异常，请重新登录后再退出！");
				request.getRequestDispatcher("/index.jsp").forward(request, response);
			}
			
			
			if(info==0){
				session.removeAttribute("username");
				session.removeAttribute("password");
				request.setAttribute("msg", "用户退出登录！");
				request.getRequestDispatcher("/index.jsp").forward(request, response);
			}
			else{
				if(info==10){
					session.removeAttribute("username");
					session.removeAttribute("password");
					request.setAttribute("msg", "请求下线超时!！");
				}else if(info==11){
					request.setAttribute("msg", "请求下线被拒绝!！");
				}else if(info==12){
					request.setAttribute("msg", "请求下线出错!!");
				}else if(info==99){
					session.removeAttribute("username");
					session.removeAttribute("password");
					request.setAttribute("msg", "未知错误！！");
				}
				
				RequestDispatcher qr=request.getRequestDispatcher("/index.jsp");
				qr.forward(request, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("msg", "退出异常，请重新登录后再退出！");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
		
		
	}

}
