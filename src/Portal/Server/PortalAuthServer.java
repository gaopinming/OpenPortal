package Portal.Server;

import Portal.Action.Req_Login;
import Portal.Action.Req_Quit;

public class PortalAuthServer extends Thread{

	public PortalAuthServer() {
	}

	public void run() {
		new Req_Login();
		Req_Login.login("leeson", "iwsiqh", "27.103.192.200", "27.103.192.100", "2000", "1", "0", "3", "LeeSon");
		new Req_Quit();
		Req_Quit.Req_Quit("27.103.192.200", "27.103.192.100", "2000", "1", "0", "3", "LeeSon");
	}

	public static void openServer(){
		
			new PortalAuthServer().start();
		
	}

}
