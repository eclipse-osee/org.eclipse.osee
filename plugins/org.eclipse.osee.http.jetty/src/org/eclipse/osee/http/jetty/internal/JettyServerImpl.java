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
package org.eclipse.osee.http.jetty.internal;

import java.io.File;
import javax.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.osee.http.jetty.JettyConfig;
import org.eclipse.osee.http.jetty.JettyConstants;
import org.eclipse.osee.http.jetty.JettyException;
import org.eclipse.osee.http.jetty.JettyServer;
import org.osgi.framework.Constants;

/**
 * @author Roberto E. Escobar
 */
public final class JettyServerImpl extends JettyServer {

   private final JettyConfig config;
   private final Server server;
   private final ServletContextHandler httpContext;
   private final String httpPort;
   private final String httpsPort;
   private final File contextWorkDir;

   public JettyServerImpl(JettyConfig config, Server server, ServletContextHandler httpContext, String httpPort, String httpsPort, File contextWorkDir) {
      super();
      this.config = config;
      this.server = server;
      this.httpContext = httpContext;
      this.httpPort = httpPort;
      this.httpsPort = httpsPort;
      this.contextWorkDir = contextWorkDir;
   }

   @Override
   public JettyConfig getConfig() {
      return config;
   }

   @Override
   public void start() {
      try {
         server.start();
      } catch (Exception ex) {
         throw JettyException.newJettyException(ex, "Error starting jetty server - [%s]", config);
      }
   }

   @Override
   public void stop() {
      try {
         server.stop();
      } catch (Exception ex) {
         throw JettyException.newJettyException(ex, "Error stopping jetty server - [%s]", config);
      } finally {
         deleteDirectory(contextWorkDir);
      }
   }

   private static boolean deleteDirectory(File directory) {
      if (directory.exists() && directory.isDirectory()) {
         File[] files = directory.listFiles();
         for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
               deleteDirectory(files[i]);
            } else {
               files[i].delete();
            }
         }
      }
      return directory.delete();
   }

   @Override
   public JettyServer addServlet(String context, Servlet servlet) {
      String contextToUse = JettyUtil.normalizeContext(context);
      ServletHolder holder = newServletHolder(config, servlet, httpPort, httpsPort);
      httpContext.addServlet(holder, contextToUse);
      return this;
   }

   private ServletHolder newServletHolder(JettyConfig config, Servlet servlet, String httpPort, String httpsPort) {
      ServletHolder holder = new ServletHolder(servlet);
      holder.setInitOrder(0);
      holder.setInitParameter(Constants.SERVICE_VENDOR, JettyConstants.SERVICE_VENDOR);
      holder.setInitParameter(Constants.SERVICE_DESCRIPTION, JettyConstants.SERVICE_DESCRIPTION);

      if (httpPort != null) {
         holder.setInitParameter("http.port", httpPort);
      }

      if (httpsPort != null) {
         holder.setInitParameter("https.port", httpsPort);
      }
      String otherInfo = config.getOtherInfo();
      if (otherInfo != null) {
         holder.setInitParameter("other.info", otherInfo);
      }
      return holder;
   }

}