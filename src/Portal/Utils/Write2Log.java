package Portal.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Write2Log {
	public static void Wr2Log(String aaa) {
		String path = System.getProperty("user.dir");
		File file = new File(path + "log.txt");
		if (!file.exists()) {

			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			writer.write(aaa + "\r\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
