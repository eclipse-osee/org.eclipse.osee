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
package org.eclipse.osee.framework.core.client.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcClientBuilder;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcServerConfig;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * Temporary Service implementation until client is dynamically configured using ConfigAdmin
 * 
 * @author Roberto E. Escobar
 */
public class ClientJdbcServiceImpl implements JdbcService {

   private final AtomicReference<JdbcClient> clientRef = new AtomicReference<JdbcClient>();

   private final JdbcClient clientProxy = createClientProxy();

   private String id = "N/A";
   private Set<String> bindings;

   public void start(Map<String, Object> props) {
      bindings = new LinkedHashSet<String>();
      String[] values = getBindings(props);
      for (String value : values) {
         bindings.add(value);
      }
      synchronized (clientRef) {
         InternalClientSessionManager instance = InternalClientSessionManager.getInstance();
         OseeSessionGrant sessionGrant = instance.getOseeSessionGrant();
         IDatabaseInfo dbInfo = sessionGrant.getDatabaseInfo();

         JdbcClient jdbcClient = newClient(dbInfo);
         clientRef.set(jdbcClient);

         id = dbInfo.getId();
      }
   }

   private String[] getBindings(Map<String, Object> props) {
      String[] toReturn = new String[0];
      if (props != null && !props.isEmpty()) {
         Object binding = props.get(JdbcConstants.JDBC_SERVICE__OSGI_BINDING);
         if (binding instanceof String) {
            toReturn = new String[] {(String) binding};
         } else if (binding instanceof String[]) {
            toReturn = (String[]) binding;
         }
      }
      return toReturn;
   }

   public void stop(Map<String, Object> props) {
      bindings = null;
   }

   private JdbcClient newClient(IDatabaseInfo dbInfo) {
      JdbcClientBuilder builder = JdbcClientBuilder.newBuilder()//
      .dbDriver(dbInfo.getDriver())//
      .dbUri(dbInfo.getConnectionUrl())//
      .dbUsername(dbInfo.getDatabaseLoginName())//
      .production(dbInfo.isProduction());

      Properties properties = dbInfo.getConnectionProperties();
      if (properties != null && !properties.isEmpty()) {
         for (Entry<Object, Object> entry : properties.entrySet()) {
            builder.dbParam((String) entry.getKey(), (String) entry.getValue());
         }
      }
      return builder.build();
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public JdbcClient getClient() {
      return clientProxy;
   }

   @Override
   public Set<String> getBindings() {
      return bindings;
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

   private JdbcClient createClientProxy() {
      InvocationHandler handler = new JdbcClientInvocationHandler();
      Class<?>[] types = new Class<?>[] {JdbcClient.class};
      return (JdbcClient) Proxy.newProxyInstance(this.getClass().getClassLoader(), types, handler);
   }

   private final class JdbcClientInvocationHandler implements InvocationHandler {

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            JdbcClient client = clientRef.get();
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