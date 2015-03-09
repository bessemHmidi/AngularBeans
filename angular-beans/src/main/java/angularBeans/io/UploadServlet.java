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

import angularBeans.context.NGSessionScopeContext;
import angularBeans.log.NGLogger;
import angularBeans.util.AngularBeansUtil;

@WebServlet(urlPatterns = { "/uploadEndPoint/*" })
@MultipartConfig()
public class UploadServlet extends HttpServlet {

	@Inject
	UploadNotifier uploadNotifier;

	@Inject
	NGLogger logger;

	
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.getSession(true);
		NGSessionScopeContext.setCurrentContext((request.getSession()
				.getAttribute(AngularBeansUtil.NG_SESSION_ATTRIBUTE_NAME)
				.toString()));

		String contextName = request.getContextPath();

		String param = request.getQueryString();

		System.out.println(param);

		response.setContentType("text/html;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet TestServlet</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Angular Beans Uplaud Service"
					+ request.getContextPath() + ": param</h1>");

			// String fileName = "";

			// if(( request.getParts().size())>1){
			//
			// uploadNotifier.fireMultipart(request.getParts());
			// for (Part part : request.getParts()) {
			// // String fileName = part.getSubmittedFileName();
			// // part.write(fileName);
			// }
			//
			// }

			if (request.getParts().size() == 1) {
				for (Part part : request.getParts()) {
					uploadNotifier.fireMultipart(part, param);
					// fileName = part.getSubmittedFileName();
					// part.write(fileName);

				}
			}

			if (request.getParts().size() > 1) {
				List<Upload> uploads = new ArrayList<Upload>();
				for (Part part : request.getParts()) {
					Upload event = new Upload(part, param);
					uploads.add(event);
					// fileName = part.getSubmittedFileName();
					// part.write(fileName);

				}

				uploadNotifier.fireMultipart(uploads);

			}

			// for (Part part : request.getParts()) {
			//
			// fileName = part.getSubmittedFileName();
			// part.write(fileName);
			//
			//
			// }

			// out.println("File uploaded to: /tmp/" + fileName);
			out.println("</body>");
			out.println("</html>");
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