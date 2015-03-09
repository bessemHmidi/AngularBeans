package angularBeans.boot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import angularBeans.LobSource;
import angularBeans.LobWrapper;

@WebServlet(urlPatterns = "/lob/*")

public class DataServlet extends HttpServlet {

	@Inject
	private ByteArrayCache cache;

	public DataServlet() {

		
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse response) {

		String requestURI = req.getRequestURI();

		int index = (requestURI.indexOf("/lob")) + 5;
		String resourceId = requestURI.substring(index);

		if(cache.getCache().containsKey(resourceId)){
		LobSource lobSource=cache.getCache().get(resourceId);
		Method m=lobSource.getGetter();
		Object container=lobSource.getContainer();

		OutputStream o;
		try {
			
			Object result=m.invoke(container);
			
			byte[] data =((LobWrapper) result).getData();
			
			o = response.getOutputStream();
if(data==null){
			data="default".getBytes();	
			}
			o.write(data);
			o.flush();
			o.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}}
	

}
