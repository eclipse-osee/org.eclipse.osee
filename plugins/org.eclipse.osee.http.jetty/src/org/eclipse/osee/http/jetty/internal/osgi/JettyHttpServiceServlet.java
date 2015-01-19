/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty.internal.osgi;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.eclipse.equinox.http.servlet.HttpServiceServlet;
import org.eclipse.osee.http.jetty.JettyConstants;

/**
 * Based on org.eclipse.equinox.http.jetty.internal.HttpServerManager.InternalHttpServiceServlet
 * 
 * @author Roberto E. Escobar
 */
public class JettyHttpServiceServlet implements Servlet {

   private final Servlet httpServiceServlet = new HttpServiceServlet();
   private ClassLoader contextLoader;

   @Override
   public void init(ServletConfig config) throws ServletException {
      ServletContext context = config.getServletContext();
      contextLoader =
         (ClassLoader) context.getAttribute(JettyConstants.SERVLET_ATTRIBUTE_KEY__INTERNAL_CONTEXT_CLASSLOADER);

      Thread thread = Thread.currentThread();
      ClassLoader current = thread.getContextClassLoader();
      thread.setContextClassLoader(contextLoader);
      try {
         httpServiceServlet.init(config);
      } finally {
         thread.setContextClassLoader(current);
      }
   }

   @Override
   public void destroy() {
      Thread thread = Thread.currentThread();
      ClassLoader current = thread.getContextClassLoader();
      thread.setContextClassLoader(contextLoader);
      try {
         httpServiceServlet.destroy();
      } finally {
         thread.setContextClassLoader(current);
      }
      contextLoader = null;
   }

   @Override
   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
      Thread thread = Thread.currentThread();
      ClassLoader current = thread.getContextClassLoader();
      thread.setContextClassLoader(contextLoader);
      try {
         httpServiceServlet.service(req, res);
      } finally {
         thread.setContextClassLoader(current);
      }
   }

   @Override
   public ServletConfig getServletConfig() {
      return httpServiceServlet.getServletConfig();
   }

   @Override
   public String getServletInfo() {
      return httpServiceServlet.getServletInfo();
   }
}