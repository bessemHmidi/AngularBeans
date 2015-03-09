package angularBeans.rest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class MultipartRequestMap extends HashMap<String, List<Object>> {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private String encoding;
    private String tempLocation;

    public MultipartRequestMap(HttpServletRequest request) {
        this(request, System.getProperty("java.io.tmpdir"));
    }

    public MultipartRequestMap(HttpServletRequest request, String tempLocation) {
        super();
        try {
            this.tempLocation = tempLocation;

            this.encoding = request.getCharacterEncoding();
            if (this.encoding == null) {
                try {
                    request.setCharacterEncoding(this.encoding = DEFAULT_ENCODING);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(MultipartRequestMap.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            for (Part part : request.getParts()) {
                String fileName = part.getSubmittedFileName();
                if (fileName != null) {
                    putMulti(part.getName(), getValue(part));
                } else {
                    processFilePart(part, fileName);
                }
            }
        } catch (IOException | ServletException ex) {
            Logger.getLogger(MultipartRequestMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getStringParameter(String name) {
        List<Object> list = get(name);
        return (list != null) ? (String) get(name).get(0) : null;
    }

    public File getFileParameter(String name) {
        List<Object> list = get(name);
        return (list != null) ? (File) get(name).get(0) : null;
    }

    private void processFilePart(Part part, String fileName) throws IOException {
        File tempFile = new File(tempLocation, fileName);
        tempFile.createNewFile();
        tempFile.deleteOnExit();

        try (BufferedInputStream input = new BufferedInputStream(part.getInputStream(), 8192);
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(tempFile), 8192);) {

            byte[] buffer = new byte[8192];
            for (int length = 0; ((length = input.read(buffer)) > 0);) {
                output.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        part.delete();
        putMulti(part.getName(), tempFile);
    }

    private String getValue(Part part) throws IOException {
        BufferedReader reader
                = new BufferedReader(new InputStreamReader(part.getInputStream(), encoding));
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[8192];
        for (int length; (length = reader.read(buffer)) > 0;) {
            value.append(buffer, 0, length);
        }
        return value.toString();
    }

    private <T> void putMulti(final String key, final T value) {
        List<Object> values = (List<Object>) super.get(key);

        if (values == null) {
            values = new ArrayList<>();
            values.add(value);
            put(key, values);
        } else {
            values.add(value);
        }
    }
}