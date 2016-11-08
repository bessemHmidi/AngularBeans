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

package angularBeans.boot;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import angularBeans.realtime.GlobalConnectionHolder;
import angularBeans.util.CommonUtils;

/**
 * Returns a generated script for resource "/angularBeans.js". the script will be lazily generated
 * based on the registered beans in the {@link BeanRegistry} class.
 * 
 * @author Bessem Hmidi
 * @author Aymen Naili
 */

@WebServlet(urlPatterns = "/angular-beans.js")
public final class BootServlet extends HttpServlet {

   @Inject
   ModuleGenerator generator;

   @Inject
   Logger log;

   @Inject
   GlobalConnectionHolder globalConnectionHolder;

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

      String sessionId = req.getSession().getId();

      globalConnectionHolder.removeConnection(sessionId);

      generator.setContextPath(CommonUtils.getContextPath(req));
      StringBuilder script = generator.generateScript();

      resp.setContentType("text/javascript");
      resp.getWriter().write(script.toString());
      resp.getWriter().flush();
   }

   /**
    * 
    */
   private static final long serialVersionUID = 7758329463070440974L;
}
