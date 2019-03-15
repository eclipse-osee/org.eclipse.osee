/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc.internal.osgi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientBuilder;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcConstants.JdbcDriverType;
import org.eclipse.osee.jdbc.JdbcLogger;
import org.eclipse.osee.jdbc.JdbcServer;
import org.eclipse.osee.jdbc.JdbcServerBuilder;
import org.eclipse.osee.jdbc.JdbcServerConfig;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.internal.JdbcUtil;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class JdbcServiceImpl implements JdbcService {

   private final AtomicReference<JdbcServer> serverRef = new AtomicReference<>();
   private final AtomicReference<JdbcClient> clientRef = new AtomicReference<>();

   private final JdbcClient clientProxy = createClientProxy();

   private Log logger;
   private ExecutorAdmin executorAdmin;
   private volatile Map<String, Object> config;

   private JdbcServer getServer() {
      return serverRef.get();
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void start(Map<String, Object> props) {
      // update() eventually calls org.hsqldb.server.Server.start() which hangs under certain unknown circumstances so keep that from halting OSGi startup
      executorAdmin.submitAndWait("Start JDBC service", () -> update(props), 20, TimeUnit.SECONDS);
   }

   public void stop(Map<String, Object> props) {
      JdbcServer server = getServer();
      if (server != null) {
         server.stop();
      }
   }

   public void update(Map<String, Object> props) {
      this.config = props;
      synchronized (clientRef) {
         JdbcServer server = newServer(props);

         JdbcClientBuilder builder = JdbcClientBuilder.newBuilder(props);
         if (hasServerConfig(props)) {
            JdbcServerConfig serverConfig = server.getConfig();
            if (!Strings.isValid(builder.getDbUri())) {
               builder = builder.fromType(JdbcDriverType.hsql, serverConfig.getDbName(), serverConfig.getDbPort());
            }

            if (!Strings.isValid(builder.getDbUsername())) {
               String serverUsername = serverConfig.getDbUsername();
               if (Strings.isValid(serverUsername)) {
                  builder.dbUsername(serverUsername);
               }
            }

            if (!Strings.isValid(builder.getDbPassword())) {
               String serverPassword = serverConfig.getDbPassword();
               if (Strings.isValid(serverPassword)) {
                  builder.dbPassword(serverPassword);
               }
            }
         }
         clientRef.set(builder.build());
      }
      logger.trace("Configured - [%s - %s] - uri[%s] bindings%s", getClass().getSimpleName(), //
         getId(), getClient().getConfig().getDbUri(), getBindings());
   }

   private JdbcServer newServer(Map<String, Object> props) {
      JdbcServer newServer = null;
      if (hasServerConfig(props)) {
         JdbcServerBuilder builder = JdbcServerBuilder.newBuilder(props)//
            .logger(asJdbcLogger(logger));

         if (!Strings.isValid(builder.getDbUsername())) {
            String username = JdbcUtil.get(props, JdbcConstants.JDBC__CONNECTION_USERNAME, null);
            if (Strings.isValid(username)) {
               builder.dbUsername(username);
            }
         }
         if (!Strings.isValid(builder.getDbPassword())) {
            String password = JdbcUtil.get(props, JdbcConstants.JDBC__CONNECTION_PASSWORD, null);
            if (Strings.isValid(password)) {
               builder.dbPassword(password);
            }
         }
         newServer = builder.build();
      }

      JdbcServer oldServer = serverRef.getAndSet(newServer);
      if (oldServer != null) {
         oldServer.stop();
      }
      if (newServer != null) {
         newServer.start();
      }
      return newServer;
   }

   @Override
   public String getId() {
      return JdbcUtil.getServiceId(config);
   }

   @Override
   public JdbcClient getClient() {
      return clientProxy;
   }

   @Override
   public Set<String> getBindings() {
      return JdbcUtil.getBindings(config);
   }

   @Override
   public boolean hasServer() {
      return getServer() != null;
   }

   @Override
   public JdbcServerConfig getServerConfig() {
      JdbcServer server = getServer();
      return server != null ? server.getConfig() : null;
   }

   @Override
   public boolean isServerAlive(long waitTime) {
      JdbcServer server = getServer();
      return server != null ? server.isAlive(waitTime) : false;
   }

   private static boolean hasServerConfig(Map<String, Object> props) {
      boolean result = false;
      for (String key : props.keySet()) {
         if (key.startsWith(JdbcConstants.SERVER_NAMESPACE)) {
            result = true;
            break;
         }
      }
      return result;
   }

   private static JdbcLogger asJdbcLogger(final Log logger) {
      return new JdbcLogger() {
         @Override
         public void info(String msg, Object... data) {
            logger.info(msg, data);
         }

         @Override
         public void error(Throwable ex, String msg, Object... data) {
            logger.error(ex, msg, data);
         }

         @Override
         public void error(String msg, Object... data) {
            logger.error(msg, data);
         }

         @Override
         public void debug(String msg, Object... data) {
            logger.debug(msg, data);
         }
      };
   }

   private JdbcClient createClientProxy() {
      InvocationHandler handler = new JdbcClientInvocationHandler();
      Class<?>[] types = new Class<?>[] {JdbcClient.class};
      return (JdbcClient) Proxy.newProxyInstance(this.getClass().getClassLoader(), types, handler);
   }

   private final class JdbcClientInvocationHandler implements InvocationHandler {

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         JdbcClient client = clientRef.get();
         if (client == null) {
            throw new OseeStateException(
               "JDBC client not available.  If using an embedded database, it may have hung.");
         }
         try {

            return method.invoke(client, args);
         } catch (Throwable ex) {
            Throwable cause = ex.getCause();
            if (cause == null) {
               cause = ex;
            }
            throw cause;
         }
      }
   }
}