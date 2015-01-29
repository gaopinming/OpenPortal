package Portal.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Make_ChapPassword {
	public static byte[] MK_ChapPwd(byte[] ReqID,byte[] Challenge,byte[] usp) throws UnsupportedEncodingException{
		
		byte ChapPwd[]=new byte[16];
		//初始化chappassword byte[]
		byte[] buf=new byte[1+usp.length+Challenge.length];
		//给chappassword byte[] 传值      
		/*
		 * Chap_Password的生成：Chap_Password的生成遵循标准的Radious协议中的Chap_Password 生成方法（参见RFC2865）。
		 * 密码加密使用MD5算法，MD5函数的输入为ChapID ＋ Password ＋Challenge 
		 * 其中，ChapID取ReqID的低 8 位，Password的长度不够协议规定的最大长度，其后不需要补零。 
		 */
		buf[0]=ReqID[1];
		
		for(int i=0;i<usp.length;i++){
			buf[1+i]=usp[i];
		}
		
		for(int i=0;i<Challenge.length;i++){
			buf[1+usp.length+i]=Challenge[i];
		}
		
		//生成Chap-Password
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			 md.update(buf);
			 ChapPwd = md.digest(); 
			 System.out.println("生成Chap-Password" + WR.Getbyte2HexString(ChapPwd));
			 Write2Log.Wr2Log("生成Chap-Password" + WR.Getbyte2HexString(ChapPwd));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			System.out.println("生成Chap-Password出错！");
			Write2Log.Wr2Log("生成Chap-Password出错！");
		}
		return ChapPwd;
	}
	
}

