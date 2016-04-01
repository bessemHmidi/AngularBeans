package angularBeans.js;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Loader for text files, e.g. .js files
 *
 * @author Michael Kulla <info@michael-kulla.com>
 */
class FileLoader {

	/**
	 * Reads a text file and returns the content in a single line
	 *
	 * @param fileName File to read
	 * @return File Content as a single line
	 * @throws IOException If an I/O error occurs
	 */
	public String readFile(String fileName) throws IOException {

		StringBuilder content = new StringBuilder();
		try (InputStream fileStream = getClass().getResourceAsStream(fileName);
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
