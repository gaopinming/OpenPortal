package Portal.Utils;

public class WR {

	public static String Getbyte2HexString(byte[] b) {
		
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex);
		}
		return("["+sb.toString()+"]");
	}
	
	
	public static void space(){
		System.out.println("###############################################################################################################################################################################");
	}

}
