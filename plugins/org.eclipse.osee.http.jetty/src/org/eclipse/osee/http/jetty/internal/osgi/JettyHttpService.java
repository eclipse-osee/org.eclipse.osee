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
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.http.jetty.JettyConstants;
import org.eclipse.osee.http.jetty.JettyConstants.SessionManagerType;
import org.eclipse.osee.http.jetty.JettyLogger;
import org.eclipse.osee.http.jetty.JettyServer;
import org.eclipse.osee.http.jetty.JettyServer.Builder;
import org.eclipse.osee.http.jetty.internal.JettyUtil;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public class JettyHttpService {

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

      String sessionManager =
         getSessionManager(props, JettyConstants.JETTY_SESSION_MANAGER_FACTORY,
            JettyConstants.DEFAULT_JETTY_SESSION_MANAGER_FACTORY);
      SessionManagerType type = SessionManagerType.fromString(sessionManager);
      switch (type) {
         case JDBC:
            JdbcService jdbcService = getJdbcService(bundleContext, "jetty.jdbc.service");
            builder.jdbcSessionManagerFactory(jdbcService.getClient());
            break;
         case UNKNOWN:
            logger.warn("JettySessionManagerFactory [%s] was %s - defaulting to IN_MEMORY - ", sessionManager, type);
            break;
         default:
            // do nothing - default is in-memory
            break;
      }
      JettyServer newServer = builder.build();
      JettyServer server = reference.getAndSet(newServer);
      if (server != null) {
         server.stop();
      }
      newServer.addServlet("/*", new JettyHttpServiceServlet());
      newServer.start();
   }

   private String getSessionManager(Map<String, Object> props, String key, String defaultValue) {
      String value = JettyUtil.get(props, key, defaultValue);
      return Strings.isValid(value) ? value.toLowerCase() : value;
   }

   private JdbcService getJdbcService(BundleContext context, String jdbcServiceBinding) {
      JdbcService toReturn = null;
      try {
         String filter = String.format("(osgi.binding=%s)", jdbcServiceBinding);
         Collection<ServiceReference<JdbcService>> references = context.getServiceReferences(JdbcService.class, filter);
         ServiceReference<JdbcService> reference = null;
         if (!references.isEmpty()) {
            reference = references.iterator().next();
         }
         if (reference != null) {
            toReturn = context.getService(reference);
         }
      } catch (InvalidSyntaxException ex) {
         throw new OseeCoreException(ex, "Error finding JdbcService reference with osgi.binding=%s", jdbcServiceBinding);
      }
      return toReturn;
   }

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