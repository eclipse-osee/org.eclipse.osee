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
package org.eclipse.osee.http.jetty;

import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.Servlet;
import org.eclipse.osee.http.jetty.internal.JettyServerFactory;
import org.eclipse.osee.http.jetty.internal.JettyUtil;
import org.eclipse.osee.http.jetty.internal.session.InMemoryJettySessionManagerFactory;
import org.eclipse.osee.http.jetty.internal.session.JdbcJettySessionManagerFactory;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Roberto E. Escobar
 */
public abstract class JettyServer {

   public abstract JettyConfig getConfig();

   public abstract void start();

   public abstract void stop();

   public abstract JettyServer addServlet(String context, Servlet servlet);

   public static Builder newBuilder() {
      return new Builder();
   }

   public static Builder newBuilder(JettyConfig config) {
      return newBuilder().withConfig(config);
   }

   public static Builder newBuilder(Map<String, Object> properties) {
      return newBuilder().properties(properties);
   }

   public static JettyServer fromConfig(JettyConfig config) {
      return newBuilder(config).build();
   }

   public static JettyServer fromProperties(Map<String, Object> properties) {
      return newBuilder(properties).build();
   }

   ///////////////////////////////////// BUILDER 

   public static class Builder extends JettyConfig {

      private static final JettyServerFactory factory = new JettyServerFactory();

      private JettyLogger logger;
      private boolean loggingEnabled;

      private JettySessionManagerFactory sessionManagerFactory;
      private JdbcClient jdbcClient;

      private Builder() {
         // Builder
      }

      public JettyServer build() {
         JettyConfig config = copy();
         if (config.isHttpEnabled() && config.isRandomHttpPort()) {
            int port = JettyUtil.getRandomPort();
            config.setHttpPort(port);
         }
         if (config.isHttpsEnabled() && config.isRandomHttpsPort()) {
            int port = JettyUtil.getRandomPort();
            config.setHttpsPort(port);
         }

         if (loggingEnabled && logger == null) {
            logger = JettyUtil.newConsoleLogger();
         }
         if (logger == null) {
            logger = JettyUtil.newNoopLogger();
         }

         JettySessionManagerFactory sessionManagerFactoryToUse = sessionManagerFactory;
         if (sessionManagerFactoryToUse == null) {
            if (jdbcClient != null) {
               sessionManagerFactoryToUse = new JdbcJettySessionManagerFactory(jdbcClient);
            } else {
               sessionManagerFactoryToUse = new InMemoryJettySessionManagerFactory();
            }
         }
         return factory.newServer(config, logger, sessionManagerFactoryToUse);
      }

      public Builder properties(Map<String, Object> src) {
         readProperties(src);
         return this;
      }

      public Builder withConfig(JettyConfig config) {
         this.copy(config);
         return this;
      }

      public Builder serverName(String serverName) {
         setServerName(serverName);
         return this;
      }

      public Builder autoDetectNioSupport(boolean autoDetect) {
         setAutoDetectNioSupport(autoDetect);
         return this;
      }

      public Builder contextPath(String contextPath) {
         setContextPath(contextPath);
         return this;
      }

      public Builder contextSessionInactiveInterval(int timeout) {
         setContextSessioninactiveinterval(timeout);
         return this;
      }

      public Builder httpEnabled(boolean httpEnabled) {
         setHttpEnabled(httpEnabled);
         return this;
      }

      public Builder httpHost(String host) {
         setHttpHost(host);
         return this;
      }

      public Builder httpPort(int port) {
         setHttpPort(port);
         return this;
      }

      public Builder httpForwarded(boolean isForwarded) {
         setHttpForwarded(isForwarded);
         return this;
      }

      public Builder httpsEnabled(boolean httpsEnabled) {
         setHttpsEnabled(httpsEnabled);
         return this;
      }

      public Builder httpsHost(String host) {
         setHttpsHost(host);
         return this;
      }

      public Builder httpsPort(int port) {
         setHttpsPort(port);
         return this;
      }

      public Builder httpsForwarded(boolean isForwarded) {
         setHttpsForwarded(isForwarded);
         return this;
      }

      public Builder nonBlockinIoEnabled(boolean enableNio) {
         setNonBlockinIoEnabled(enableNio);
         return this;
      }

      public Builder otherInfo(String otherInfo) {
         setOtherInfo(otherInfo);
         return this;
      }

      public Builder sslKeypassword(String keypassword) {
         setSslKeypassword(keypassword);
         return this;
      }

      public Builder sslKeystore(String keystore) {
         setSslKeystore(keystore);
         return this;
      }

      public Builder sslKeystoretype(String keystoreType) {
         setSslKeystoretype(keystoreType);
         return this;
      }

      public Builder sslNeedClientAuth(boolean needClientAuth) {
         setSslNeedClientAuth(needClientAuth);
         return this;
      }

      public Builder sslPassword(String password) {
         setSslPassword(password);
         return this;
      }

      public Builder sslProtocol(String protocol) {
         setSslProtocol(protocol);
         return this;
      }

      public Builder sslWantClientAuth(boolean wantClientAuth) {
         setSslWantClientAuth(wantClientAuth);
         return this;
      }

      public Builder workingDirectory(String workingDirectory) {
         setWorkingDirectory(workingDirectory);
         return this;
      }

      public Builder useRandomHttpPort(boolean useRandomPort) {
         setUseRandomHttpPort(useRandomPort);
         return this;
      }

      public Builder useRandomHttpsPort(boolean useRandomPort) {
         setUseRandomHttpsPort(useRandomPort);
         return this;
      }

      public Builder logging(boolean loggingEnabled) {
         this.loggingEnabled = loggingEnabled;
         return this;
      }

      public Builder logger(JettyLogger logger) {
         this.logger = logger;
         return this;
      }

      public Builder sessionManagerFactory(JettySessionManagerFactory sessionManagerFactory) {
         this.sessionManagerFactory = sessionManagerFactory;
         return this;
      }

      public Builder jdbcSessionManagerFactory(JdbcClient jdbcClient) {
         this.jdbcClient = jdbcClient;
         return this;
      }

      public Builder replaceMultipleSlashesWithSingle(boolean value) {
         setMultipleSlashToSingle(value);
         return this;
      }

      public Builder extraParam(String key, Object value) {
         addProp(key, value);
         return this;
      }

      public Builder extraParams(Map<String, Object> props) {
         for (Entry<String, Object> entry : props.entrySet()) {
            extraParam(entry.getKey(), entry.getValue());
         }
         return this;
      }

   }

}
