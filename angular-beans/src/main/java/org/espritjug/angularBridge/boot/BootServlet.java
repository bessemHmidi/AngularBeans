package org.espritjug.angularBridge.boot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

@WebServlet(urlPatterns = "/angular-bridge.js")
public class BootServlet extends HttpServlet {

	// public void init(ServletConfig config) {
	// String path = config.getServletContext().getRealPath("/");
	// System.out.println(path);
	// }

	@Inject
	JavaScriptGenerator generator;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		generator.setHTTPRequest(req);

		resp.setContentType("text/javascript");
		StringWriter stringWriter = new StringWriter();
		generator.getScript(stringWriter);

		byte[] barray = stringWriter.toString().getBytes();

		InputStream is = new ByteArrayInputStream(barray);
		String compressed = getCompressedJavaScript(is);

		resp.getWriter().write(compressed);

		resp.getWriter().flush();
	}

	private String getCompressedJavaScript(InputStream inputStream)
			throws IOException {
		InputStreamReader isr = new InputStreamReader(inputStream);
		JavaScriptCompressor compressor = new JavaScriptCompressor(isr,
				new CompressorErrorReporter());
		inputStream.close();
		int lineBreakPos = 80;
		boolean munge = false;
		boolean warn = false;
		boolean preserveAllSemiColons = true;
		StringWriter out = new StringWriter();
		compressor.compress(out, lineBreakPos, munge, warn,
				preserveAllSemiColons, true);

		
		
		out.flush();
		StringBuffer buffer = out.getBuffer();

		return buffer.toString();
	}

}
