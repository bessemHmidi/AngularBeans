package angularBeans;

import java.io.IOException;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
@WebServlet(urlPatterns="/angularBridgeException")
@SessionScoped
public class ErrorServlet extends HttpServlet{
	 
	Exception e;
	
	public void setException(@Observes Exception exception){
		e=exception;
		System.out.println("DETECTED:" +e.getMessage());
	}
	
	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse response)
			throws ServletException, IOException {
		
	  e.printStackTrace(response.getWriter());
	}

}
