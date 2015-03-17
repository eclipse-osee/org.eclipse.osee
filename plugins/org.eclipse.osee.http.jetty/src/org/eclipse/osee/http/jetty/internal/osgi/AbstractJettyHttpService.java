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

import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTPS_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.JETTY__HTTP_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.ORG_OSGI_SERVICE_HTTP_PORT;
import static org.eclipse.osee.http.jetty.JettyConstants.ORG_OSGI_SERVICE_HTTP_PORT_SECURE;
import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.http.jetty.JettyHttpService;
import org.eclipse.osee.http.jetty.JettyConfig;
import org.eclipse.osee.http.jetty.JettyLogger;
import org.eclipse.osee.http.jetty.JettyServer;
import org.eclipse.osee.http.jetty.JettyServer.Builder;
import org.eclipse.osee.http.jetty.internal.JettyUtil;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractJettyHttpService implements JettyHttpService {

   private static final String DIR_PREFIX = "pid_";
   private final AtomicReference<JettyServer> reference = new AtomicReference<JettyServer>();

   private Log logger;
   private File workingDirectory;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(BundleContext bundleContext, Map<String, Object> props) {
      logger.trace("Starting [%s]...", getClass().getSimpleName());

      workingDirectory = new File(bundleContext.getDataFile(""), "jettywork");
      workingDirectory.mkdir();

      update(bundleContext, props);
   }

   public void stop(Map<String, Object> props) {
      logger.trace("Stopping [%s]...", getClass().getSimpleName());
      JettyServer server = reference.get();
      if (server != null) {
         server.stop();
      }
   }

   @Override
   public JettyConfig getConfig() {
      JettyServer jettyServer = reference.get();
      return jettyServer != null ? jettyServer.getConfig() : null;
   }

   public void update(BundleContext bundleContext, Map<String, Object> props) {
      Builder builder = JettyServer.newBuilder(props);
      int httpPort = getPort(bundleContext, props, JETTY__HTTP_PORT, ORG_OSGI_SERVICE_HTTP_PORT);
      if (httpPort > -1) {
         builder.httpPort(httpPort);
      }
      int httpsPort = getPort(bundleContext, props, JETTY__HTTPS_PORT, ORG_OSGI_SERVICE_HTTP_PORT_SECURE);
      if (httpsPort > -1) {
         builder.httpsPort(httpsPort);
      }
      builder.logger(asJettyLogger(logger));

      File contextWorkDir = new File(workingDirectory, getWorkingDirectoryPath(props));
      contextWorkDir.mkdir();
      builder.workingDirectory(contextWorkDir.getAbsolutePath());

      customizeJettyServer(builder, props);

      JettyServer newServer = builder.build();
      JettyServer server = reference.getAndSet(newServer);
      if (server != null) {
         server.stop();
      }
      newServer.addServlet("/*", new JettyHttpServiceServlet());
      newServer.start();
   }

   protected abstract void customizeJettyServer(Builder builder, Map<String, Object> props);

   private String getWorkingDirectoryPath(Map<String, Object> props) {
      return DIR_PREFIX + props.get(Constants.SERVICE_PID).hashCode();
   }

   private int getPort(BundleContext bundleContext, Map<String, Object> props, String key, String osgiKey) {
      int port = -1;
      String httpPort = JettyUtil.get(props, key, null);
      if (!Strings.isNumeric(httpPort)) {
         httpPort = bundleContext.getProperty(osgiKey);
         if (Strings.isNumeric(httpPort)) {
            port = Integer.parseInt(httpPort);
         }
      }
      return port;
   }

   private JettyLogger asJettyLogger(final Log logger) {
      return new JettyLogger() {

         @Override
         public void warn(Throwable th, String msg, Object... args) {
            logger.warn(th, msg, args);
         }

         @Override
         public void error(Throwable th, String msg, Object... args) {
            logger.error(th, msg, args);
         }

         @Override
         public void debug(String msg, Object... args) {
            logger.debug(msg, args);
         }
      };
   }

}
