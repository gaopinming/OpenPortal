package Portal.Server;

import Portal.Server.PortalAuthServer;

public class StartServer {

	public static void main(String[] args) {
		// TODO Auto-generated mewhile(true){
		   for(int i=0;i<=100000;i++){
		    	new Thread() {
	    			public void run() {
	    				try {
	    					PortalAuthServer.openServer();
	    				} catch (Exception e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	    			};

	    		}.start();
		   }
		   
		   for(int i=0;i<=100000;i++){
		    	new Thread() {
	    			public void run() {
	    				try {
	    					PortalAuthServer.openServer();
	    				} catch (Exception e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	    			};

	    		}.start();
		   }
		   
		   for(int i=0;i<=100000;i++){
		    	new Thread() {
	    			public void run() {
	    				try {
	    					PortalAuthServer.openServer();
	    				} catch (Exception e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	    			};

	    		}.start();
		   }
		   
		   for(int i=0;i<=100000;i++){
		    	new Thread() {
	    			public void run() {
	    				try {
	    					PortalAuthServer.openServer();
	    				} catch (Exception e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	    			};

	    		}.start();
		   }
		   
		   for(int i=0;i<=100000;i++){
		    	new Thread() {
	    			public void run() {
	    				try {
	    					PortalAuthServer.openServer();
	    				} catch (Exception e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	    			};

	    		}.start();
		   }
		   
		  
        	
        
		
	}

}
