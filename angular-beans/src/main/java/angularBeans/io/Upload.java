/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */
package angularBeans.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

/**
 * wrap a javax.servlet.http.Part in an upload action
 *
 * @author Bessem Hmidi
 *
 */
public class Upload {

	private final Part part;
	private final String id;

	public Upload(Part part, String id) {
		super();
		this.part = part;
		this.id = id;
	}

	public Part getPart() {
		return part;
	}

	public byte[] getAsByteArray() {
		try {
			InputStream in = part.getInputStream();
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[0xFFFF];
				int len;
				while ((len = in.read(buffer)) != -1) {
					os.write(buffer, 0, len);
				}
				os.flush();
				return os.toByteArray();
			}
		} catch (IOException e) {
			return null;
		}

	}

	public String getId() {
		return id;
	}

}
