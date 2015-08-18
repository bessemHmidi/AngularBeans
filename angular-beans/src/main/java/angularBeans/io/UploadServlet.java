package angularBeans.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.ws.rs.core.MediaType;

import org.ietf.jgss.Oid;

import angularBeans.boot.ModuleGenerator;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.log.NGLogger;
import angularBeans.util.AngularBeansUtil;

@WebServlet(urlPatterns = { "/uploadEndPoint/*" },asyncSupported=true)
@MultipartConfig()
public class UploadServlet extends HttpServlet {

	@Inject
	FileUploadHandler uploadHandler;

	@Inject
	NGLogger logger;

	@Inject
	ModuleGenerator generator;
	
	@Inject
	HttpSession httpSession;
	
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	
		String sessionID=httpSession.getId();
				//request.getSession().getAttribute(AngularBeansUtil.NG_SESSION_ATTRIBUTE_NAME);
		
//		if(sessionID==null){
//		request.getSession(true);
//		request.getSession().setAttribute(AngularBeansUtil.NG_SESSION_ATTRIBUTE_NAME, generator.getUID());		
//		}
		NGSessionScopeContext.setCurrentContext(sessionID);

		String contextName = request.getContextPath();

		String fullURI = request.getRequestURI();
		String urlPrefix = contextName + "/uploadEndPoint/";

		String param = fullURI.substring(fullURI.indexOf(urlPrefix)
				+ urlPrefix.length() - 1);//

		// String param = request.getQueryString();

		response.setContentType(MediaType.APPLICATION_JSON);
		try (PrintWriter out = response.getWriter()) {
			
			
			
			
		
			List<Upload> uploads = new ArrayList<Upload>();
			
			for (Part part : request.getParts()) {
				Upload event = new Upload(part, param);
				uploads.add(event);
				// fileName = part.getSubmittedFileName();
				// part.write(fileName);
			uploadHandler.handleUploads(uploads, param);

			}
			
			out.write(" ");
		}

	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}

}