package angularBeans;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

public class Upload {
	private Part part;
	private String id;
	
	public Upload(Part part, String id) {
		super();
		this.part = part;
		this.id = id;
	}
	
	
	public Part getPart() {
		return part;
	}
	
	public byte[] getAsByteArray(){
		 try
		    {
			 InputStream in=part.getInputStream();
			 ByteArrayOutputStream os = new ByteArrayOutputStream();
		        byte[] buffer = new byte[0xFFFF];

		        for (int len; (len = in.read(buffer)) != -1;)
		            os.write(buffer, 0, len);

		        os.flush();

		        os.close();
		        return os.toByteArray();
		    }
		    catch (IOException e)
		    {
		        return null;
		    }
		
	
		
	}
	
	public String getId() {
		return id;
	}

}
