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

package org.eclipse.osee.framework.core.client.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map.Entry;
import java.util.Properties;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientBuilder;
import org.eclipse.osee.jdbc.JdbcServerConfig;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * Temporary Service implementation until client is dynamically configured using ConfigAdmin
 *
 * @author Roberto E. Escobar
 */
public class ClientJdbcServiceImpl implements JdbcService {

   private final JdbcClientExtended clientProxy = createClientProxy();

   @Override
   public String getId() {
      return clientProxy.getId();
   }

   @Override
   public JdbcClient getClient() {
      return clientProxy;
   }

   @Override
   public boolean hasServer() {
      return false;
   }

   @Override
   public JdbcServerConfig getServerConfig() {
      return null;
   }

   @Override
   public boolean isServerAlive(long waitTime) {
      return false;
   }

   private JdbcClientExtended createClientProxy() {
      InvocationHandler handler = new JdbcClientInvocationHandler();
      Class<?>[] types = new Class<?>[] {JdbcClientExtended.class};
      return (JdbcClientExtended) Proxy.newProxyInstance(this.getClass().getClassLoader(), types, handler);
   }

   private interface JdbcClientExtended extends JdbcClient {
      String getId();
   }

   private final class JdbcClientInvocationHandler implements InvocationHandler {

      private volatile JdbcClient proxiedClient;

      public String getId() {
         OseeSessionGrant dbInfo = getDbInfo();
         return dbInfo != null ? dbInfo.getDbId() : "N/A";
      }

      private boolean isGetId(Method method) {
         return "getid".equalsIgnoreCase(method.getName());
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            Object toReturn;
            if (isGetId(method)) {
               toReturn = getId();
            } else {
               JdbcClient client = getProxiedClient();
               toReturn = method.invoke(client, args);
            }
            return toReturn;
         } catch (Throwable ex) {
            Throwable cause = ex.getCause();
            if (cause == null) {
               cause = ex;
            }
            throw cause;
         }
      }

      private JdbcClient getProxiedClient() {
         if (proxiedClient == null) {
            OseeSessionGrant dbInfo = getDbInfo();
            if (dbInfo != null) {
               proxiedClient = newClient(dbInfo);
            }
         }
         return proxiedClient;
      }

      private OseeSessionGrant getDbInfo() {
         OseeSessionGrant sessionGrant = InternalClientSessionManager.getInstance().getOseeSessionGrant();
         return sessionGrant;
      }

      private JdbcClient newClient(OseeSessionGrant sessionGrant) {
         JdbcClientBuilder builder = JdbcClientBuilder.newBuilder()//
            .dbDriver(sessionGrant.getDbDriver())//
            .dbUri(sessionGrant.getDbUrl())//
            .dbUsername(sessionGrant.getDbLogin())//
            .production(sessionGrant.isDbIsProduction());

         Properties properties = sessionGrant.getDbConnectionProperties();
         if (properties != null && !properties.isEmpty()) {
            for (Entry<Object, Object> entry : properties.entrySet()) {
               builder.dbParam((String) entry.getKey(), (String) entry.getValue());
            }
         }
         return builder.build();
      }
   }

}