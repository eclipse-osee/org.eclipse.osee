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

package org.eclipse.osee.jdbc;

import static org.eclipse.osee.jdbc.JdbcConstants.DB_PASSWORD_KEY;
import static org.eclipse.osee.jdbc.JdbcConstants.DB_USERNAME_KEY;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_SERVER__ACCEPT_REMOTE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_SERVER__ALIVE_WAIT_TIMEOUT_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_SERVER__DB_DATA_PATH;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_SERVER__HOST;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_SERVER__IMPL_CLASSNAME;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_SERVER__PORT;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_SERVER__START_UP_WAIT_TIMEOUT_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_SERVER__USE_RANDOM_PORT;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__ACCEPT_REMOTE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__ALIVE_WAIT_TIMEOUT_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__DB_DATA_PATH;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__HOST;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__IMPL_CLASSNAME;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__PORT;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__START_UP_WAIT_TIMEOUT_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__USE_RANDOM_PORT;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.get;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.getBoolean;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.getInt;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.getLong;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.internal.JdbcUtil;

/**
 * @author Roberto E. Escobar
 */
public class JdbcServerConfig {

   private String host;
   private int port;
   private final Properties properties = new Properties();

   private String dbPath;
   private boolean acceptRemoteConnections;
   private boolean useRandomPort;
   private long aliveWaitTimeout;
   private long startUpWaitTimeout;
   private String serverImplClassName;

   JdbcServerConfig() {
      super();
      reset();
   }

   public boolean acceptRemoteConnections() {
      return acceptRemoteConnections;
   }

   public String getDbHost() {
      return host;
   }

   public int getDbPort() {
      return port;
   }

   public String getDbPath() {
      return dbPath;
   }

   public String getDbUsername() {
      return (String) properties.get(DB_USERNAME_KEY);
   }

   public String getDbPassword() {
      return (String) properties.get(DB_PASSWORD_KEY);
   }

   public String getDbName() {
      String dbName = dbPath;
      if (Strings.isValid(dbName)) {
         int index = dbName.lastIndexOf("/");
         if (index > 0) {
            dbName = dbName.substring(index + 1);
         }
      } else {
         dbName = "";
      }
      return dbName;
   }

   public boolean useRandomPort() {
      return useRandomPort;
   }

   public long getAliveWaitTimeout() {
      return aliveWaitTimeout;
   }

   public long getStartUpWaitTimeout() {
      return startUpWaitTimeout;
   }

   public Properties getProperties() {
      Properties unmodifiable = new Properties();
      unmodifiable.putAll(properties);
      return unmodifiable;
   }

   public String getServerImplClassName() {
      return serverImplClassName;
   }

   void setServerImplClassName(String serverImplClassName) {
      this.serverImplClassName = serverImplClassName;
   }

   void setAcceptRemoteConnections(boolean acceptRemoteConnections) {
      this.acceptRemoteConnections = acceptRemoteConnections;
   }

   void setDbAcceptAddress(String host) {
      this.host = host;
   }

   void setDbListenPort(int port) {
      this.port = port;
   }

   void setDbPath(String dataPath) {
      this.dbPath = dataPath;
   }

   void setUseRandomPort(boolean useRandomPort) {
      this.useRandomPort = useRandomPort;
   }

   void setAliveWaitTimeout(long aliveWaitTimeout) {
      this.aliveWaitTimeout = aliveWaitTimeout;
   }

   void setStartUpWaitTimeout(long startUpWaitTimeout) {
      this.startUpWaitTimeout = startUpWaitTimeout;
   }

   void setDbUsername(String value) {
      addDbParam(DB_USERNAME_KEY, value);
   }

   void setDbPassword(String value) {
      addDbParam(DB_PASSWORD_KEY, value);
   }

   void addDbParam(String key, String value) {
      if (value == null) {
         removeDbParam(key);
      } else {
         properties.put(key, value);
      }
   }

   void removeDbParam(String key) {
      properties.remove(key);
   }

   private void reset() {
      properties.clear();
      setServerImplClassName(DEFAULT_JDBC_SERVER__IMPL_CLASSNAME);
      setAcceptRemoteConnections(DEFAULT_JDBC_SERVER__ACCEPT_REMOTE_CONNECTIONS);
      setDbAcceptAddress(DEFAULT_JDBC_SERVER__HOST);
      setDbListenPort(DEFAULT_JDBC_SERVER__PORT);
      setDbPath(DEFAULT_JDBC_SERVER__DB_DATA_PATH);
      setUseRandomPort(DEFAULT_JDBC_SERVER__USE_RANDOM_PORT);
      setAliveWaitTimeout(DEFAULT_JDBC_SERVER__ALIVE_WAIT_TIMEOUT_MILLIS);
      setStartUpWaitTimeout(DEFAULT_JDBC_SERVER__START_UP_WAIT_TIMEOUT_MILLIS);
      setDbUsername(JdbcConstants.DEFAULT_JDBC_SERVER__USERNAME);
      setDbPassword(JdbcConstants.DEFAULT_JDBC_SERVER__PASSWORD);
   }

   public JdbcServerConfig copy() {
      JdbcServerConfig data = new JdbcServerConfig();
      data.serverImplClassName = this.serverImplClassName;
      data.acceptRemoteConnections = this.acceptRemoteConnections;
      data.host = this.host;
      data.port = this.port;
      data.dbPath = this.dbPath;
      data.useRandomPort = this.useRandomPort;
      data.aliveWaitTimeout = this.aliveWaitTimeout;
      data.startUpWaitTimeout = this.startUpWaitTimeout;
      data.properties.clear();
      data.properties.putAll(this.properties);
      return data;
   }

   protected void copy(JdbcServerConfig other) {
      this.serverImplClassName = other.serverImplClassName;
      this.acceptRemoteConnections = other.acceptRemoteConnections;
      this.host = other.host;
      this.port = other.port;
      this.dbPath = other.dbPath;
      this.useRandomPort = other.useRandomPort;
      this.aliveWaitTimeout = other.aliveWaitTimeout;
      this.startUpWaitTimeout = other.startUpWaitTimeout;
      this.properties.clear();
      this.properties.putAll(other.properties);
   }

   void readProperties(Map<String, Object> src) {
      // @formatter:off
      setServerImplClassName(get(src, JDBC_SERVER__IMPL_CLASSNAME, DEFAULT_JDBC_SERVER__IMPL_CLASSNAME));
      setAcceptRemoteConnections(getBoolean(src, JDBC_SERVER__ACCEPT_REMOTE_CONNECTIONS, DEFAULT_JDBC_SERVER__ACCEPT_REMOTE_CONNECTIONS));
      setDbAcceptAddress(get(src, JDBC_SERVER__HOST, DEFAULT_JDBC_SERVER__HOST));
      setDbListenPort(getInt(src, JDBC_SERVER__PORT, DEFAULT_JDBC_SERVER__PORT));
      setDbPath(get(src, JDBC_SERVER__DB_DATA_PATH, DEFAULT_JDBC_SERVER__DB_DATA_PATH));
      setUseRandomPort(getBoolean(src, JDBC_SERVER__USE_RANDOM_PORT, DEFAULT_JDBC_SERVER__USE_RANDOM_PORT));
      setAliveWaitTimeout(getLong(src, JDBC_SERVER__ALIVE_WAIT_TIMEOUT_MILLIS, DEFAULT_JDBC_SERVER__ALIVE_WAIT_TIMEOUT_MILLIS));
      setStartUpWaitTimeout(getLong(src, JDBC_SERVER__START_UP_WAIT_TIMEOUT_MILLIS, DEFAULT_JDBC_SERVER__START_UP_WAIT_TIMEOUT_MILLIS));
      setDbUsername(get(src, JdbcConstants.JDBC_SERVER__USERNAME, JdbcConstants.DEFAULT_JDBC_SERVER__USERNAME));
      setDbPassword(get(src, JdbcConstants.JDBC_SERVER__PASSWORD, JdbcConstants.DEFAULT_JDBC_SERVER__PASSWORD));
      // @formatter:on

      for (Entry<String, Object> entry : src.entrySet()) {
         String key = entry.getKey();
         if (JdbcUtil.isValidExtraParam(key)) {
            Object value = entry.getValue();
            if (value != null) {
               addDbParam(key, String.valueOf(value));
            } else {
               removeDbParam(key);
            }
         }
      }
   }

   @Override
   public String toString() {
      return "JdbcServerConfig [host=" + host + ", port=" + port + ", properties=" + properties + ", dbPath=" + dbPath + ", acceptRemoteConnections=" + acceptRemoteConnections + ", useRandomPort=" + useRandomPort + ", aliveWaitTimeout=" + aliveWaitTimeout + ", startUpWaitTimeout=" + startUpWaitTimeout + ", serverImplClassName=" + serverImplClassName + "]";
   }

}