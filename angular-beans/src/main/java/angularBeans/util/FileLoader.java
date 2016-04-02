package angularBeans.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Loader for text files, e.g. .js files
 *
 * @author Michael Kulla <info@michael-kulla.com>
 */
public class FileLoader {

	/**
	 * Reads a text file and returns the content in a single line
	 *
	 * @param fileName File to read
	 * @return File Content as a single line
	 * @throws IOException If an I/O error occurs
	 * @throws NullPointerException If file doesn't exist or can't be found
	 */
	public static String readFile(String fileName) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		StringBuilder content = new StringBuilder();
		try (InputStream fileStream = classLoader.getResourceAsStream(fileName);
				InputStreamReader fileReader = new InputStreamReader(fileStream);
				BufferedReader in = new BufferedReader(fileReader)) {
			String line;
			while ((line = in.readLine()) != null) {
				content.append(line);
			}
		}
		return content.toString();
	}
}
