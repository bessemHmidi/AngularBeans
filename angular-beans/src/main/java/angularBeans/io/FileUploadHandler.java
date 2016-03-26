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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import angularBeans.context.NGSessionScoped;

/**
 * 
 * @author Bessem Hmidi handler for uload's actions an upload url is bound to a
 *         specific action (method call)
 */

@NGSessionScoped
public class FileUploadHandler implements Serializable {

	private final Map<String, Call> uploadsActions = new HashMap<>();

	public Map<String, Call> getUploadsActions() {
		return uploadsActions;
	}

	public void handleUploads(List<Upload> uploads, String path) {

		Call call = uploadsActions.get(path);

		try {
			call.getMethod().invoke(call.getObject(), uploads);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			e.printStackTrace();
		}

	}

}
