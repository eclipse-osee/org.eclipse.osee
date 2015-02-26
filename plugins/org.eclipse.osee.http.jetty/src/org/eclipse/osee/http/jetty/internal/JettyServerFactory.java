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

import static org.eclipse.osee.http.jetty.JettyException.newJettyException;
import static org.eclipse.osee.http.jetty.internal.JettyUtil.checkNotNullOrEmpty;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.osee.http.jetty.JettyConfig;
import org.eclipse.osee.http.jetty.JettyConstants;
import org.eclipse.osee.http.jetty.JettyLogger;
import org.eclipse.osee.http.jetty.JettyServer;
import org.eclipse.osee.http.jetty.JettySessionManagerFactory;

/**
 * @author Roberto E. Escobar
 */
public class JettyServerFactory {

   public JettyServer newServer(final JettyConfig config, final JettyLogger logger, final JettySessionManagerFactory sessionManagerFactory) {
      if (sessionManagerFactory == null) {
         throw newJettyException("Error sessionManagerFactory cannot be null");
      }
      if (logger == null) {
         throw newJettyException("Error logger cannot be null");
      }
      final Server server = new Server();

      String httpPort = null;
      Connector httpConnector = createHttpConnector(config);
      if (httpConnector != null) {
         server.addConnector(httpConnector);

         int port = httpConnector.getLocalPort();
         if (port == -1) {
            port = httpConnector.getPort();
         }
         httpPort = Integer.toString(port);
      }

      String httpsPort = null;
      Connector httpsConnector = createHttpsConnector(config);
      if (httpsConnector != null) {
         server.addConnector(httpsConnector);
         int port = httpsConnector.getLocalPort();
         if (port == -1) {
            port = httpsConnector.getPort();
         }
         httpsPort = Integer.toString(port);
      }

      ServletContextHandler httpContext = createHttpContext(config);

      File contextWorkDir = createWorkingDirectory(config);
      httpContext.setAttribute(JettyConstants.SERVLET_ATTRIBUTE_KEY__CONTEXT_TEMPDIR, contextWorkDir);

      Map<String, Object> otherProps = config.getOtherProps();
      SessionManager sessionManager = sessionManagerFactory.newSessionManager(logger, server, otherProps);
      sessionManager.setMaxInactiveInterval(config.getContextSessioninactiveinterval());
      httpContext.setSessionHandler(new SessionHandler(sessionManager));

      server.setHandler(httpContext);
      return new JettyServerImpl(config, server, httpContext, httpPort, httpsPort, contextWorkDir);
   }

   private AbstractConnector createHttpConnector(JettyConfig config) {
      AbstractConnector connector = null;
      if (config.isHttpEnabled()) {
         int httpPort = config.getHttpPort();
         if (httpPort > 0) {
            boolean nioEnabled = false;
            if (config.isAutoDetectNioSupport()) {
               nioEnabled = getDefaultNIOEnablement();
            } else {
               nioEnabled = config.isNonBlockinIoEnabled();
            }

            if (nioEnabled) {
               connector = new SelectChannelConnector();
            } else {
               connector = new SocketConnector();
            }
            connector.setPort(httpPort);
            String httpHost = config.getHttpHost();
            if (httpHost != null) {
               connector.setHost(httpHost);
            }

            configureForForwardedRequests(config, connector);

         } else {
            throw newJettyException("http port cannot be less than = 0");
         }
      }
      return connector;
   }

   private Boolean getDefaultNIOEnablement() {
      Properties systemProperties = System.getProperties();
      String javaVendor = systemProperties.getProperty("java.vendor", "");
      if (javaVendor.equals("IBM Corporation")) {
         String javaVersion = systemProperties.getProperty("java.version", "");
         if (javaVersion.startsWith("1.4")) {
            return Boolean.FALSE;
         }
         // Note: no problems currently logged with 1.5
         if (javaVersion.equals("1.6.0")) {
            String jclVersion = systemProperties.getProperty("java.jcl.version", "");
            if (jclVersion.startsWith("2007")) {
               return Boolean.FALSE;
            }
            if (jclVersion.startsWith("2008") && !jclVersion.startsWith("200811") && !jclVersion.startsWith("200812")) {
               return Boolean.FALSE;
            }
         }
      }
      return Boolean.TRUE;
   }

   @SuppressWarnings("deprecation")
   private AbstractConnector createHttpsConnector(JettyConfig config) {
      SslSocketConnector sslConnector = null;
      if (config.isHttpsEnabled()) {
         int httpsPort = config.getHttpsPort();
         if (httpsPort > 0) {

            sslConnector = new SslSocketConnector();
            sslConnector.setPort(httpsPort);

            String httpHost = config.getHttpHost();
            if (httpHost != null) {
               sslConnector.setHost(httpHost);
            }

            configureForForwardedRequests(config, sslConnector);

            // configure SSL
            String keyStore = config.getSslKeystore();
            if (keyStore != null) {
               sslConnector.setKeystore(keyStore);
            }

            String password = config.getSslPassword();
            if (password != null) {
               sslConnector.setPassword(password);
            }

            String keyPassword = config.getSslKeypassword();
            if (keyPassword != null) {
               sslConnector.setKeyPassword(keyPassword);
            }

            sslConnector.setNeedClientAuth(config.isSslNeedClientAuth());
            sslConnector.setWantClientAuth(config.isSslWantClientAuth());

            String protocol = config.getSslProtocol();
            if (protocol != null) {
               sslConnector.setProtocol(protocol);
            }

            String keystoreType = config.getSslKeystoretype();
            if (keystoreType != null) {
               sslConnector.setKeystoreType(keystoreType);
            }
         } else {
            throw newJettyException("https port cannot be less than = 0");
         }
      }
      return sslConnector;
   }

   private void configureForForwardedRequests(JettyConfig config, AbstractConnector connector) {
      if (connector != null) {
         if (config.hasServerName()) {
            connector.setHostHeader(config.getServerName());
         }
         boolean isForwarded = config.isHttpForwarded() || config.hasServerName();
         connector.setForwarded(isForwarded);
      }
   }

   private ServletContextHandler createHttpContext(JettyConfig config) {
      ServletContextHandler httpContext = new ServletContextHandler();
      MimeTypes mimeTypes = httpContext.getMimeTypes();

      mimeTypes.addMimeMapping("xsd", "application/xml");

      httpContext.setAttribute(JettyConstants.SERVLET_ATTRIBUTE_KEY__INTERNAL_CONTEXT_CLASSLOADER,
         Thread.currentThread().getContextClassLoader());
      httpContext.setClassLoader(this.getClass().getClassLoader());

      String contextPathProperty = config.getContextPath();
      if (contextPathProperty == null) {
         contextPathProperty = "/";
      }
      httpContext.setContextPath(contextPathProperty);
      httpContext.setCompactPath(config.isMultipleSlashToSingle());
      return httpContext;
   }

   private File createWorkingDirectory(JettyConfig config) {
      String workingDirectory = config.getWorkingDirectory();
      checkNotNullOrEmpty(workingDirectory, "working directory");

      File contextWorkDir = new File(workingDirectory);
      contextWorkDir.mkdir();
      return contextWorkDir;
   }

}
