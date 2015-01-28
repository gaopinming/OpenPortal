package Portal.Action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Portal.Utils.Make_ChapPassword;
import Portal.Utils.WR;

public class Auth_V1 {

	// 创建ErrorInfo包
	byte[] ErrorInfo = new byte[1];
	// 创建ChapPassword包
	byte[] ChapPassword = new byte[16];
	// 创建连接
	DatagramSocket dataSocket;

	public byte[] Action(String Bas_IP, int bas_PORT, int timeout_Sec,
			byte[] buff, String in_username, String in_password, byte[] ReqID,
			byte[] Challenge) {

		byte[] Username = in_username.getBytes();
		byte[] password = in_password.getBytes();
		try {
			ChapPassword = Make_ChapPassword.MK_ChapPwd(ReqID, Challenge,
					password);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] authbuff = new byte[4 + Username.length + ChapPassword.length];
		authbuff[0] = (byte) 1;
		authbuff[1] = (byte) (Username.length + 2);
		for (int i = 0; i < Username.length; i++) {
			authbuff[2 + i] = Username[i];
		}
		authbuff[2 + Username.length] = (byte) 4;
		authbuff[3 + Username.length] = (byte) (ChapPassword.length + 2);
		for (int i = 0; i < ChapPassword.length; i++) {
			authbuff[4 + Username.length + i] = ChapPassword[i];
		}

		// 创建Req_Auth包
		byte[] Req_Auth = new byte[16 + authbuff.length];

		// 给Req_Auth包赋值
		for (int i = 0; i < Req_Auth.length; i++) {
			Req_Auth[i] = buff[i];
		}
		Req_Auth[1] = (byte) 3;
		Req_Auth[14] = (byte) 0;
		short atrAttrNum = 2;
		Req_Auth[15] = (byte) atrAttrNum;
		for (int i = 0; i < authbuff.length; i++) {
			Req_Auth[16 + i] = authbuff[i];
		}

		System.out.println("REQ Auth" + WR.Getbyte2HexString(Req_Auth));

		try {

			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(Req_Auth,
					Req_Auth.length, InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);
		
			// 接收服务器的数据包
			byte[] ACK_Data = new byte[16];
			DatagramPacket receivePacket = new DatagramPacket(ACK_Data,
					ACK_Data.length);
			// 设置请求超时3秒
			dataSocket.setSoTimeout(timeout_Sec * 1000);
			dataSocket.receive(receivePacket);

			byte[] ACK_Auth_Data = new byte[receivePacket.getLength()];
			for (int i = 0; i < receivePacket.getLength(); i++) {
				ACK_Auth_Data[i] = ACK_Data[i];

			}
			System.out
					.println("ACK Auth" + WR.Getbyte2HexString(ACK_Auth_Data));

			if ((int) (ACK_Auth_Data[14] & 0xFF) == 0) {
				System.out.println("认证成功！！");
				System.out.println("准备发送AFF_ACK_AUTH");
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 1) {
				System.out.println("用户认证请求被拒绝");
				ErrorInfo[0] = (byte) 21;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 2) {
				System.out.println("用户链接已建立");
				ErrorInfo[0] = (byte) 22;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 3) {
				System.out.println("有一个用户正在认证过程中，请稍后再试");
				ErrorInfo[0] = (byte) 23;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 4) {
				System.out.println("用户认证失败（发生错误）");
				ErrorInfo[0] = (byte) 24;
				return ErrorInfo;
			}

		} catch (IOException e) {
			System.out.println("用户认证服务器无响应！！！");
			ErrorInfo[0] = (byte) 02;
			return ErrorInfo;
		} finally {
			dataSocket.close();
		}

		// 创建AFF_Ack_Auth包
		byte[] AFF_Ack_Auth_Data = new byte[16];
		// 给AFF_ACK_AUTH包赋值
		for (int i = 0; i < AFF_Ack_Auth_Data.length; i++) {
			AFF_Ack_Auth_Data[i] = buff[i];
		}
		AFF_Ack_Auth_Data[1] = (byte) 7;
		AFF_Ack_Auth_Data[14] = (byte) 0;
		AFF_Ack_Auth_Data[15] = (byte) 0;

		System.out.println("AFF_Ack_Auth"
				+ WR.Getbyte2HexString(AFF_Ack_Auth_Data));

		try {

			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(
					AFF_Ack_Auth_Data, AFF_Ack_Auth_Data.length,
					InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);
			System.out.println("发送AFF_Ack_Auth成功！！");

		} catch (IOException e) {
			System.out.println("发送AFF_Ack_Auth出错！！");
			ErrorInfo[0] = (byte) 20;
			return ErrorInfo;
		} finally {
			dataSocket.close();
		}
		ErrorInfo[0] = (byte) 20;
		return ErrorInfo;

	}

}
