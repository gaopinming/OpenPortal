package Portal.Server;

import Portal.Action.Auth_V1;
import Portal.Action.Auth_V2;
import Portal.Action.Challenge_V1;
import Portal.Action.Challenge_V2;
import Portal.Action.Quit_V1;
import Portal.Action.Quit_V2;
import Portal.Utils.WR;

public class Action {
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

	public Action() {
		/*
		 * 给SerialNo[]赋值 创建随机数SerialNo byte[]
		 */
		short SerialNo_int = (short) (1 + Math.random() * 32767);
		for (int i = 0; i < 2; i++) {
			int offset = (SerialNo.length - 1 - i) * 8;
			SerialNo[i] = (byte) ((SerialNo_int >>> offset) & 0xff);
		}
	}

	public int Method(String Action, String in_username, String in_password,
			String ip, String basIP, String basPORT, String portalVer,
			String authType, String timeoutSec, String sharedSecret) {

		String Bas_IP = basIP;
		int bas_PORT = Integer.parseInt(basPORT);
		int portal_Ver = Integer.parseInt(portalVer);
		int auth_Type = Integer.parseInt(authType);
		int timeout_Sec = Integer.parseInt(timeoutSec);
		byte[] Buff = init(ip, portal_Ver, auth_Type);

		if (auth_Type != 0) {
			System.out.println("暂时不支持PAP认证方式  ！！");
			return 66;
		}
		
		if (portal_Ver == 2) {
			return Portal_V2(Action, in_username, in_password,
					Bas_IP, bas_PORT, timeout_Sec, Buff, sharedSecret);
		}
		
		if (portal_Ver == 1) {
			return Portal_V1(Action, in_username, in_password, Bas_IP, bas_PORT,
					timeout_Sec, Buff);
		}
		
		else{
			return 55;
		}

		
	}

	private int Portal_V2(String Action, String in_username,
			String in_password, String Bas_IP,
			int bas_PORT, int timeout_Sec, byte[] Buff, String sharedSecret) {
		System.out.println("使用Portal V2协议 ！！");
		if (Action.equals("Login")) {
			// 创建Ack_Challenge_V2包
			byte[] Ack_Challenge_V2 = new Challenge_V2().Action(Bas_IP,
					bas_PORT, timeout_Sec, Buff, sharedSecret);
			// 如果出错直接返回错误信息
			if (Ack_Challenge_V2.length == 1) {
				new Quit_V2()
						.Action(1, Bas_IP, bas_PORT, timeout_Sec, Buff, sharedSecret);
				return (int) (Ack_Challenge_V2[0] & 0xFF);
			}

			// 给Buff包赋值
			for (int i = 0; i < Ack_Challenge_V2.length; i++) {
				Buff[i] = Ack_Challenge_V2[i];
			}
			ReqID[0] = Buff[6];
			ReqID[1] = Buff[7];
			for (int i = 0; i < Challenge.length; i++) {
				Challenge[i] = Buff[34 + i];
			}
			System.out.println("获得Challenge：" + WR.Getbyte2HexString(Challenge));
			// 创建Ack_Challenge_V2包
			byte[] Ack_Auth_V2 = new Auth_V2().Action(Bas_IP, bas_PORT,
					timeout_Sec, Buff, in_username, in_password, ReqID,
					Challenge, sharedSecret);
			// 如果出错直接返回错误信息
			if ((int) (Ack_Auth_V2[0] & 0xFF) == 20) {
				return 0;
			} else if ((int) (Ack_Auth_V2[0] & 0xFF) == 22) {
				return 22;
			} else {
				new Quit_V2().Action(2, Bas_IP, bas_PORT, timeout_Sec, Buff, sharedSecret);
				return (int) (Ack_Auth_V2[0] & 0xFF);
			}

		}
		if (Action.equals("LoginOut")) {
			return new Quit_V2().Action(0, Bas_IP, bas_PORT, timeout_Sec, Buff, sharedSecret);
		}

		return 99;
	}

	private int Portal_V1(String Action, String in_username,
			String in_password, String Bas_IP, int bas_PORT, int timeout_Sec,
			byte[] Buff) {
		System.out.println("使用Portal V1协议 ！！");
		if (Action.equals("Login")) {
			// 创建Ack_Challenge_V1包
			byte[] Ack_Challenge_V1 = new Challenge_V1().Action(Bas_IP,
					bas_PORT, timeout_Sec, Buff);
			// 如果出错直接返回错误信息
			if (Ack_Challenge_V1.length == 1) {
				new Quit_V1().Action(1, Bas_IP, bas_PORT, timeout_Sec, Buff);
				return (int) (Ack_Challenge_V1[0] & 0xFF);
			}

			// 给Buff包赋值
			for (int i = 0; i < Ack_Challenge_V1.length; i++) {
				Buff[i] = Ack_Challenge_V1[i];
			}
			ReqID[0] = Buff[6];
			ReqID[1] = Buff[7];
			for (int i = 0; i < Challenge.length; i++) {
				Challenge[i] = Buff[18 + i];
			}
			// 创建Ack_Challenge_V1包
			byte[] Ack_Auth_V1 = new Auth_V1().Action(Bas_IP, bas_PORT,
					timeout_Sec, Buff, in_username, in_password, ReqID,
					Challenge);
			// 如果出错直接返回错误信息
			if ((int) (Ack_Auth_V1[0] & 0xFF) == 20) {
				return 0;
			} else if ((int) (Ack_Auth_V1[0] & 0xFF) == 22) {
				return 22;
			} else {
				new Quit_V1().Action(2, Bas_IP, bas_PORT, timeout_Sec, Buff);
				return (int) (Ack_Auth_V1[0] & 0xFF);
			}
		}
		if (Action.equals("LoginOut")) {
			return new Quit_V1().Action(0, Bas_IP, bas_PORT, timeout_Sec, Buff);
		}
		return 99;
	}

	private byte[] init(String ip, int portal_Ver, int auth_Type) {
		/*
		 * 给UserIP[]赋值 接收客户ip地址 IP地址压缩成4字节,如果要进一步处理的话,就可以转换成一个int了.
		 */
		String[] ips = ip.split("[.]");
		// 将ip地址加入字段UserIP
		for (int i = 0; i < 4; i++) {
			int m = Integer.parseInt(ips[i]);
			byte b = (byte) m;
			UserIP[i] = b;
		}

		Ver[0] = (byte) portal_Ver;
		Type[0] = (byte) 0;
		Mod[0] = (byte) auth_Type;
		Rsvd[0] = (byte) 0;
		UserPort[0] = (byte) 0;
		UserPort[1] = (byte) 0;
		ErrCode[0] = (byte) 0;
		AttrNum[0] = (byte) 0;

		// 创建Buff包
		byte[] Buff = new byte[1024];
		// 给Buff包赋初始值
		Buff[0] = Ver[0];
		Buff[1] = Type[0];
		Buff[2] = Mod[0];
		Buff[3] = Rsvd[0];
		Buff[4] = SerialNo[0];
		Buff[5] = SerialNo[1];
		Buff[8] = UserIP[0];
		Buff[9] = UserIP[1];
		Buff[10] = UserIP[2];
		Buff[11] = UserIP[3];
		Buff[12] = UserPort[0];
		Buff[13] = UserPort[1];
		Buff[14] = ErrCode[0];
		Buff[15] = AttrNum[0];
		return Buff;
	}

}
