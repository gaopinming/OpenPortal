package Portal.Action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Portal.Utils.Make_ChapPassword;
import Portal.Utils.WR;

public class Req_Login {
	public static int login(String in_username,String in_password,String ip,String basIP,String basPORT,String portalVer,String authType,String timeoutSec,String sharedSecret) {

		String Bas_IP=basIP;
		int bas_PORT=Integer.parseInt(basPORT);
		int portal_Ver=Integer.parseInt(portalVer);
		int auth_Type=Integer.parseInt(authType);
		int timeout_Sec=Integer.parseInt(timeoutSec);
		

		// 构建portal协议中的字段包
		byte[] Ver = new byte[1];
		byte[] Type = new byte[1];
		byte[] Mod = new byte[1];
		byte[] Rsvd = new byte[1];
		byte[] SerialNo = new byte[2];
		byte[] ReqID = new byte[2];
		byte[] UserIP = new byte[4];
		byte[] UserPort = new byte[2];
		byte[] ErrCode = new byte[1];
		byte[] AttrNum = new byte[1];
		
		byte[] Challenge = new byte[16];
		byte[] ChapPassword = new byte[16];
		/*
		 * 创建随机数SerialNo byte[]
		 */
		short SerialNo_int = (short) (1 + Math.random() * 32767);
		for (int i = 0; i < 2; i++) {
			int offset = (SerialNo.length - 1 - i) * 8;
			SerialNo[i] = (byte) ((SerialNo_int >>> offset) & 0xff);
		}
		
		/*
		 * 接收客户ip地址
		 */
//		String ip = null;
//		try {
//			ip = InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		/*
		 * IP地址压缩成4字节,如果要进一步处理的话,就可以转换成一个int了.
		 */
		String[] ips = ip.split("[.]");
		// 将ip地址加入字段UserIP
		for (int i = 0; i < 4; i++) {
			int m = Integer.parseInt(ips[i]);
			byte b = (byte) m;
			UserIP[i] = b;
		}
		
		//创建Req_Challenge包
		byte[] Req_Challenge = new byte[16];
		//给Req_Challenge包赋值
		Req_Challenge[0] = (byte) portal_Ver;
		short atrtype = 1;
		Req_Challenge[1] = (byte) atrtype;
		Req_Challenge[2] = (byte) auth_Type;
		short atrrsvd = 0;
		Req_Challenge[3] = (byte) atrrsvd;
		Req_Challenge[4] = SerialNo[0];
		Req_Challenge[5] = SerialNo[1];
		Req_Challenge[8] = UserIP[0];
		Req_Challenge[9] = UserIP[1];
		Req_Challenge[10] = UserIP[2];
		Req_Challenge[11] = UserIP[3];
		short artErrCode=0;
		Req_Challenge[14] = (byte) artErrCode;

		System.out.println("REQ Challenge" + WR.Getbyte2HexString(Req_Challenge));

		try {

			DatagramSocket dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(Req_Challenge,
					Req_Challenge.length, InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);

			// 接收服务器的数据包
			byte[] ACK_Data = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(ACK_Data,
					ACK_Data.length);
			//设置请求超时3秒
			dataSocket.setSoTimeout(timeout_Sec*1000);
			dataSocket.receive(receivePacket);
			
			ReqID[0]=ACK_Data[6];
			ReqID[1]=ACK_Data[7];
			for(int i=0;i<Challenge.length;i++){
				Challenge[i]=ACK_Data[18+i];
			}
			
			byte[] ACK_Challenge_Data = new byte[receivePacket.getLength()];
			for (int i = 0; i < receivePacket.getLength(); i++) {
				ACK_Challenge_Data[i] = ACK_Data[i];
			}
			dataSocket.close();
			System.out
					.println("ACK Challenge" + WR.Getbyte2HexString(ACK_Challenge_Data));

		} catch (IOException e) {
			System.out.println("Challenge挑战请求服务器无响应！！！");
			return 01;
		}
		
		if ((int)(ErrCode[0] & 0xFF)==0){
			System.out.println("请求Challenge成功！！");
			System.out.println("准备发送REQ Auth");
		}else if((int)(ErrCode[0] & 0xFF)==1){
			System.out.println("请求Challenge被拒绝");
			return 11;
		}else if((int)(ErrCode[0] & 0xFF)==2){
			System.out.println("此链接已建立");
			return 12;
		}else if((int)(ErrCode[0] & 0xFF)==3){
			System.out.println("有一个用户正在认证过程中，请稍后再试");
			return 13;
		}else if((int)(ErrCode[0] & 0xFF)==4){
			System.out.println("用户请求Challenge失败（发生错误）");
			return 14;
		}
		
		
		
		
		
		
		
		
		
		
		
		byte[] Username=in_username.getBytes();
		byte[] password=in_password.getBytes();
		try {
			ChapPassword=Make_ChapPassword.MK_ChapPwd(ReqID, Challenge, password);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		byte[] authbuff=new byte[4+Username.length+ChapPassword.length];
		authbuff[0] = (byte) 1;
		authbuff[1] = (byte) (Username.length+2);
		for(int i=0;i<Username.length;i++){
			authbuff[2+i]=Username[i];
		}
		authbuff[2+Username.length]= (byte) 4;
		authbuff[3+Username.length]= (byte) (ChapPassword.length+2);
		for(int i=0;i<ChapPassword.length;i++){
			authbuff[4+Username.length+i]=ChapPassword[i];
		}
		
		
		
		//创建Req_Auth包
				byte[] Req_Auth = new byte[16+authbuff.length];
				//给Req_Auth包赋值
				Req_Auth[0] = (byte) portal_Ver;
				atrtype = 3;
				Req_Auth[1] = (byte) atrtype;
				Req_Auth[2] = (byte) auth_Type;
				atrrsvd = 0;
				Req_Auth[3] = (byte) atrrsvd;
				Req_Auth[4] = SerialNo[0];
				Req_Auth[5] = SerialNo[1];
				Req_Auth[6] = ReqID[0];
				Req_Auth[7] = ReqID[1];
				Req_Auth[8] = UserIP[0];
				Req_Auth[9] = UserIP[1];
				Req_Auth[10] = UserIP[2];
				Req_Auth[11] = UserIP[3];
				artErrCode=0;
				Req_Auth[14] = (byte) artErrCode;
				short atrAttrNum=2;
				Req_Auth[15] = (byte) atrAttrNum;
				for(int i=0;i<authbuff.length;i++){
					Req_Auth[16+i]=authbuff[i];
				}

				System.out.println("REQ Auth" + WR.Getbyte2HexString(Req_Auth));
				
				
				
				try {

					DatagramSocket dataSocket = new DatagramSocket();
					// 创建发送数据包并发送给服务器

					DatagramPacket requestPacket = new DatagramPacket(Req_Auth,
							Req_Auth.length, InetAddress.getByName(Bas_IP), bas_PORT);
					dataSocket.send(requestPacket);

					// 接收服务器的数据包
					byte[] ACK_Data = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(ACK_Data,
							ACK_Data.length);
					//设置请求超时3秒
					dataSocket.setSoTimeout(timeout_Sec*1000);
					dataSocket.receive(receivePacket);
					
					ErrCode[0]=ACK_Data[14];
					
					byte[] ACK_Auth_Data = new byte[receivePacket.getLength()];
					for (int i = 0; i < receivePacket.getLength(); i++) {
						ACK_Auth_Data[i] = ACK_Data[i];
						
					}
					dataSocket.close();
					System.out
							.println("ACK Auth" + WR.Getbyte2HexString(ACK_Auth_Data));

				} catch (IOException e) {
					System.out.println("用户认证服务器无响应！！！");
					return 02;
				}
				
				if ((int)(ErrCode[0] & 0xFF)==0){
					System.out.println("认证成功！！");
					System.out.println("准备发送AFF_ACK_AUTH");
				} else if((int)(ErrCode[0] & 0xFF)==1){
					System.out.println("用户认证请求被拒绝");
					return 21;
				}else if((int)(ErrCode[0] & 0xFF)==2){
					System.out.println("用户链接已建立");
					return 22;
				}else if((int)(ErrCode[0] & 0xFF)==3){
					System.out.println("有一个用户正在认证过程中，请稍后再试");
					return 23;
				}else if((int)(ErrCode[0] & 0xFF)==4){
					System.out.println("用户认证失败（发生错误）");
					return 24;
				}
				

				
				//创建AFF_Ack_Auth包
				byte[] AFF_Ack_Auth_Data = new byte[16];
				//给AFF_ACK_AUTH包赋值
				AFF_Ack_Auth_Data[0] = (byte) portal_Ver;
				atrtype = 7;
				AFF_Ack_Auth_Data[1] = (byte) atrtype;
				AFF_Ack_Auth_Data[2] = (byte) auth_Type;
				atrrsvd = 0;
				AFF_Ack_Auth_Data[3] = (byte) atrrsvd;
				AFF_Ack_Auth_Data[4] = SerialNo[0];
				AFF_Ack_Auth_Data[5] = SerialNo[1];
				AFF_Ack_Auth_Data[6] = ReqID[0];
				AFF_Ack_Auth_Data[7] = ReqID[1];
				AFF_Ack_Auth_Data[8] = UserIP[0];
				AFF_Ack_Auth_Data[9] = UserIP[1];
				AFF_Ack_Auth_Data[10] = UserIP[2];
				AFF_Ack_Auth_Data[11] = UserIP[3];
				artErrCode=0;
				AFF_Ack_Auth_Data[14] = (byte) artErrCode;
				atrAttrNum=0;
				AFF_Ack_Auth_Data[15] = (byte) atrAttrNum;

				System.out.println("AFF_Ack_Auth" + WR.Getbyte2HexString(AFF_Ack_Auth_Data));
					
				try {

					DatagramSocket dataSocket = new DatagramSocket();
					// 创建发送数据包并发送给服务器

					DatagramPacket requestPacket = new DatagramPacket(AFF_Ack_Auth_Data,
							AFF_Ack_Auth_Data.length, InetAddress.getByName(Bas_IP), bas_PORT);
					dataSocket.send(requestPacket);
					dataSocket.close();

					} catch (IOException e) {
					System.out.println("发送AFF_Ack_Auth出错！！");
					return 0;
				}	
					
					
				return 0;	
	}
}
