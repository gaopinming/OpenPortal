package Portal.Server;

public class PortalAuthServer extends Thread{

	public PortalAuthServer() {
	}

	public void run() {
		new Action().Method("Login","leeson", "iwsiqh", "27.103.192.200", "192.168.0.2", "2000", "1", "0", "3", "LeeSon");
		new Action().Method("LoginOut","leeson", "iwsiqh", "27.103.192.200", "192.168.0.2", "2000", "1", "0", "3", "LeeSon");
		
	}

	public static void openServer(){
		
			new PortalAuthServer().start();
		
	}

}
