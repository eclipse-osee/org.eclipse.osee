/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jdbc.internal.osgi;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__ENABLED;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_ACTIVE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_IDLE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__DB_DATA_PATH;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__HOST;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__PORT;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__CONNECTION_USERNAME;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto E. Escobar
 */
@Component(configurationPid = "OseeJdbc", factory = "org.eclipse.osee.jdbc.JdbcService", property = {
   JDBC_SERVER__HOST + "=127.0.0.1",
   JDBC_SERVER__PORT + "=8088",
   JDBC_SERVER__DB_DATA_PATH + "=file:demo/hsql/osee.hsql.db",
   JDBC__CONNECTION_USERNAME + "=public",
   JDBC_POOL__ENABLED + "=true",
   JDBC_POOL__MAX_ACTIVE_CONNECTIONS + "=100",
   JDBC_POOL__MAX_IDLE_CONNECTIONS + "=100"})
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

   @Reference
   void setLogger(Log logger) {
      this.logger = logger;
   }

   @Reference
   void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   @Activate
   void activate(Map<String, Object> props) {
      // update() eventually calls org.hsqldb.server.Server.start() which hangs under certain unknown circumstances so keep that from halting OSGi startup
      executorAdmin.submitAndWait("Start JDBC service", () -> modified(props), 20, TimeUnit.SECONDS);
   }

   @Deactivate
   void stop(Map<String, Object> props) {
      JdbcServer server = getServer();
      if (server != null) {
         server.stop();
      }
   }

   @Modified
   void modified(Map<String, Object> props) {
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
         JdbcClient client = null;
         for (int x = 0; x < 5; x++) {
            client = clientRef.get();
            if (client != null) {
               break;
            } else {
               XConsoleLogger.err("sleeping to get JDBC Client");
               Thread.sleep(1000);
            }
         }
         if (client == null) {
            String msg = "JDBC client not available.  If using an embedded database, it may have hung.";
            XConsoleLogger.err(msg);
            throw new OseeStateException(msg);
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