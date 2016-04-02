package angularBeans.js;

import java.io.IOException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import angularBeans.util.FileLoader;

/**
 *
 * @author Michael Kulla <info@michael-kulla.com>
 */
@Ignore
@RunWith(Arquillian.class)
public class FileLoaderTest {
	
	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class)
				.addClass(FileLoader.class)
				.addAsResource("emptyfile.txt")
				.addAsResource("onelinefile.txt")
				.addAsResource("multilinefile.txt")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}
	
	@Test(expected = NullPointerException.class)
	public void nonExistentFile() throws IOException {
		System.out.println("non-existent file");
		String fileName = "foo";
		FileLoader instance = new FileLoader();
		String result = instance.readFile(fileName);
	}
	
	@Test
	public void readEmptyFile() throws IOException {
		System.out.println("empty file");
		String fileName = "/emptyfile.txt";
		FileLoader instance = new FileLoader();
		String expected = "";
		String actual = instance.readFile(fileName);
		assertEquals(expected, actual);
	}
	
	@Test
	public void readOneLiner() throws IOException {
		System.out.println("file with one line");
		String fileName = "/onelinefile.txt";
		FileLoader instance = new FileLoader();
		String expected = "this is a line";
		String actual = instance.readFile(fileName);
		assertEquals(expected, actual);
	}
	
	@Test
	public void readMoreLines() throws IOException {
		System.out.println("file with two lines");
		String fileName = "/multilinefile.txt";
		FileLoader instance = new FileLoader();
		String expected = "this is a lineand this is another one";
		String actual = instance.readFile(fileName);
		assertEquals(expected, actual);
	}
	
	
}
