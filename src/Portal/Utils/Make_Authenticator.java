package Portal.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Make_Authenticator {
	/**
	 * 生成Request Authenticator结果
	 * 	    以字节流Ver + Type + PAP/CHAP + Rsvd + SerialNo + ReqID + UserIP + UserPort + ErrCode + AttrNum 
	 *          + 16个字节的0 + request attributes + secret作为MD5的输入，
	 *          得到的MD5输出就是请求报文的验证字Request  Authenticator的内容
	 * @param Buff 基础16字节包
	 * @param Attrs request attributes字段包
	 * @param Secret Secret字段
	 * @return Request Authenticator 16字节
	 */
	public static byte[] MK_Authen(byte[] Buff,byte[] Attrs,String sharedSecret){
		
		byte[] Secret = sharedSecret.getBytes();
		byte Authen[]=new byte[16];
		//初始化buf byte[]
		byte[] buf=new byte[Buff.length+16+Attrs.length+Secret.length];
		//给buf byte[] 传值      
		
		for(int i=0;i<Buff.length;i++){
			buf[i]=Buff[i];
		}
		
		for(int i=0;i<16;i++){
			buf[Buff.length+i]=(byte) 0;
		}
		
		if(Attrs.length>0){
			for(int i=0;i<Attrs.length;i++){
				buf[Buff.length+16+i]=Attrs[i];
			}
			
			for(int i=0;i<Secret.length;i++){
				buf[Buff.length+16+Attrs.length+i]=Secret[i];
			}
		}else{
			for(int i=0;i<Secret.length;i++){
				buf[Buff.length+16+i]=Secret[i];
			}
		}
		
		
		
		//生成Chap-Password
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			 md.update(buf);
			 Authen = md.digest(); 
			 System.out.println("生成Request Authenticator :" + WR.Getbyte2HexString(Authen));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			System.out.println("生成Request Authenticator出错！");
		}
		return Authen;
	}
	
}

