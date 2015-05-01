package angularBeans.demoApp.util;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

public class FileSource {
	
	
	
	public static byte[] open(String fileName){
		byte[] buff = null;
		 try {
				File f=new File("files/"+fileName);

				FileImageOutputStream fios= new FileImageOutputStream(f);
				//response.setContentType("application/pdf");
			    buff=new byte[(int) f.length()];
			   
					fios.readFully(buff);
					fios.close();
					
					
			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		return buff;
		
		
	}

}
