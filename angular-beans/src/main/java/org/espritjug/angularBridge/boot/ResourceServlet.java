package org.espritjug.angularBridge.boot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.espritjug.angularBridge.Util;

@WebServlet(urlPatterns = "/resources/*")
public class ResourceServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestURI = req.getRequestURI();

		int index = (requestURI.indexOf("/resources/")) + 10;

		InputStream is = getServletContext().getResourceAsStream(
				"META-INF" + (requestURI.substring(index)) + ".properties");

		Properties properties = new Properties();
		properties.load(is);

		resp.getWriter().write(Util.getJson(properties));

	}

}
