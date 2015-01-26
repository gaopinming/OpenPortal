package Portal.Action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Portal.Utils.Make_ChapPassword;
import Portal.Utils.WR;

public class Req_Quit {
	
	public static int Req_Quit(String ip,String basIP,String basPORT,String portalVer,String authType,String timeoutSec,String sharedSecret) {

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
		
		
		/*
		 * 创建随机数SerialNo byte[]
		 */
		short SerialNo_int = (short) (1 + Math.random() * 32767);
		for (int i = 0; i < 2; i++) {
			int offset = (SerialNo.length - 1 - i) * 8;
			SerialNo[i] = (byte) ((SerialNo_int >>> offset) & 0xff);
		}
		
		
		
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
		
		//创建Req_Quit包
		byte[] Req_Quit = new byte[16];
		//给Req_Quit包赋值
		
		Req_Quit[0] = (byte) portal_Ver;
		short atrtype = 5;
		Req_Quit[1] = (byte) atrtype;
		Req_Quit[2] = (byte) auth_Type;
		short atrrsvd = 0;
		Req_Quit[3] = (byte) atrrsvd;
		Req_Quit[4] = SerialNo[0];
		Req_Quit[5] = SerialNo[1];
		Req_Quit[8] = UserIP[0];
		Req_Quit[9] = UserIP[1];
		Req_Quit[10] = UserIP[2];
		Req_Quit[11] = UserIP[3];
		Req_Quit[12] = (byte) 0;
		Req_Quit[13] = (byte) 0;
		short artErrCode=0;
		Req_Quit[14] = (byte) artErrCode;
		Req_Quit[15] = (byte) 0;

		System.out.println("REQ Quit" + WR.Getbyte2HexString(Req_Quit));

		try {

			DatagramSocket dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(Req_Quit,
					Req_Quit.length, InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);

			// 接收服务器的数据包
			byte[] ACK_Data = new byte[16];
			DatagramPacket receivePacket = new DatagramPacket(ACK_Data,
					ACK_Data.length);
			//设置请求超时3秒
			dataSocket.setSoTimeout(timeout_Sec*100);
			dataSocket.receive(receivePacket);
			
			ErrCode[0]=ACK_Data[14];
			
			
			byte[] ACK_Quit_Data = new byte[receivePacket.getLength()];
			for (int i = 0; i < receivePacket.getLength(); i++) {
				ACK_Quit_Data[i] = ACK_Data[i];
				
			}
			dataSocket.close();
			System.out
					.println("ACK Quit" + WR.Getbyte2HexString(ACK_Quit_Data));

		} catch (IOException e) {
			System.out.println("下线请求服务器无响应！！！");
			return 10;
		}
		
		if ((int)(ErrCode[0] & 0xFF)!=0){
			if((int)(ErrCode[0] & 0xFF)==1){
				System.out.println("请求下线被拒绝");
				Ack_Quit_Error(Req_Quit,Bas_IP,bas_PORT);
				return 11;
			}else if((int)(ErrCode[0] & 0xFF)==2){
				System.out.println("请求下线出错");
				Ack_Quit_Error(Req_Quit,Bas_IP,bas_PORT);
				return 12;
			}
			
			
		}else{
			System.out.println("请求下线成功！！");
		}
		return 0;
		}

	private static void Ack_Quit_Error(byte[] Req_Quit,String Bas_IP,int bas_PORT) {
		short artErrCode;
		artErrCode=1;
		Req_Quit[14] = (byte) artErrCode;
		System.out.println("ACK Quit Error" + WR.Getbyte2HexString(Req_Quit));
		
		try {

			DatagramSocket dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(Req_Quit,
					Req_Quit.length, InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);
			dataSocket.close();
		} catch (IOException e) {
			System.out.println("ACK Quit Error发送失败！！！");
		}
	}
}
