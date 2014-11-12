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
package org.eclipse.osee.jdbc;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class JdbcConstants {

   private JdbcConstants() {
      //Constants
   }

   private static final String EMBEDDED_JDBC_SERVER_CLASSNAME =
      "org.eclipse.osee.jdbc.internal.dbsupport.hsql.HsqlJdbcServer";

   public static final String NAMESPACE = "jdbc";
   public static final String CLIENT_NAMESPACE = qualify("client");
   public static final String SERVER_NAMESPACE = qualify("server");
   private static final String POOL_NAMESPACE = qualifyClient("connection.pool");

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   private static String qualifyPool(String value) {
      return String.format("%s.%s", POOL_NAMESPACE, value);
   }

   private static String qualifyClient(String value) {
      return String.format("%s.%s", CLIENT_NAMESPACE, value);
   }

   private static String qualifyServer(String value) {
      return String.format("%s.%s", SERVER_NAMESPACE, value);
   }

   public static final String DB_USERNAME_KEY = "user";
   public static final String DB_PASSWORD_KEY = "password";

   // @formatter:off
   public static final int JDBC__MAX_VARCHAR_LENGTH = 4000;
   public static final int JDBC__MAX_FETCH_SIZE = 10000;

   public static final int DEFAULT_JDBC__CONNECTION_POOL_SIZE = 10;

   public static final String JDBC__IS_PRODUCTION_DB = qualifyClient("is.production");
   public static final String JDBC__CONNECTION_DRIVER = qualifyClient("driver");
   public static final String JDBC__CONNECTION_URI = qualifyClient("db.uri");
   public static final String JDBC__CONNECTION_APPEND_PROPS_TO_URI = qualifyClient("db.append.props.to.uri");
   public static final String JDBC__CONNECTION_USERNAME = qualifyClient("db.username");
   public static final String JDBC__CONNECTION_PASSWORD = qualifyClient("db.password");
   
   public static final String JDBC_POOL__ENABLED = qualifyPool("enabled");
   public static final String JDBC_POOL__CONFIG_FILE_URI = qualifyPool("config.file.uri");
   public static final String JDBC_POOL__PREPARED_STATEMENTS_ALLOWED = qualifyPool("prepared.statements.allowed");
   public static final String JDBC_POOL__VALIDATION_QUERY_TIMEOUT_SECS = qualifyPool("validation.query.timeout.secs");
   public static final String JDBC_POOL__MAX_ACTIVE_CONNECTIONS = qualifyPool("max.active.connections");
   public static final String JDBC_POOL__MAX_IDLE_CONNECTIONS = qualifyPool("max.idle.connections");
   public static final String JDBC_POOL__MIN_IDLE_CONNECTIONS = qualifyPool("min.idle.connections");
   public static final String JDBC_POOL__MAX_WAIT_CONNECTIONS = qualifyPool("max.wait.for.connections");
   public static final String JDBC_POOL__WHEN_EXHAUSTED_ACTION = qualifyPool("exhausted.action");
   public static final String JDBC_POOL__TEST_ON_BORROW = qualifyPool("test.on.barrow");
   public static final String JDBC_POOL__TEST_ON_RETURN = qualifyPool("test.on.return");
   public static final String JDBC_POOL__TEST_WHILE_IDLE = qualifyPool("test.on.idle");
   public static final String JDBC_POOL__NUM_TESTS_PER_EVICTION_RUN = qualifyPool("number.of.test.per.eviction.run");
   public static final String JDBC_POOL__TIME_BETWEEN_EVICTION_RUNS_MILLIS = qualifyPool("time.between.eviction.runs.millis");
   public static final String JDBC_POOL__MIN_EVICTABLE_IDLE_TIME_MILLIS = qualifyPool("min.evictable.idle.timeout.millis");
   public static final String JDBC_POOL__SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = qualifyPool("soft.min.evictable.timeout.millis");
   public static final String JDBC_POOL__LIFO = qualifyPool("lifo");
   public static final String JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS = qualifyPool("max.total.prepared.statements");
   public static final String JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS = qualifyPool("max.active.prepared.statements");
   public static final String JDBC_POOL__MAX_IDLE_PREPARED_STATEMENTS = qualifyPool("max.idle.prepared.statements");
   public static final String JDBC_POOL__MIN_IDLE_PREPARED_STATEMENTS = qualifyPool("min.idle.prepared.statements");
   public static final String JDBC_POOL__MAX_WAIT_PREPARED_STATEMENTS = qualifyPool("max.wait.for.prepared.statements");
   public static final String JDBC_POOL__LOG_ABANDONED = qualifyPool("abandoned.logging.enabled");
   public static final String JDBC_POOL__REMOVE_ABANDONED = qualifyPool("abandoned.removal.enabled");
   public static final String JDBC_POOL__REMOVE_ABANDONED_TIMEOUT = qualifyPool("abandoned.removal.timeout.secs");

   public static final boolean DEFAULT_JDBC__IS_PRODUCTION_DB = false;
   public static final String DEFAULT_JDBC__CONNECTION_DRIVER = null;
   public static final String DEFAULT_JDBC__CONNECTION_URI = null;
   public static final boolean DEFAULT_JDBC__CONNECTION_APPEND_PROPS_TO_URI = false;
   public static final String DEFAULT_JDBC__CONNECTION_USERNAME = null;
   public static final String DEFAULT_JDBC__CONNECTION_PASSWORD = null;
   
   public static final boolean DEFAULT_JDBC_POOL__ENABLED = true;
   public static final String DEFAULT_JDBC_POOL__CONFIG_FILE_URI = null;
   public static final String DEFAULT_JDBC_POOL__CONNECTION_DRIVER = "org.apache.commons.dbcp.PoolingDriver";
   public static final String DEFAUT_JDBC_POOL__CONNECTION_ID = "jdbc:apache:commons:dbcp:";
   public static final boolean DEFAULT_JDBC_POOL__PREPARED_STATEMENTS_ALLOWED = false; // default was false
   public static final int DEFAULT_JDBC_POOL__VALIDATION_QUERY_TIMEOUT_SECS = 10; // 3 secs 
   public static final int DEFAULT_JDBC_POOL__MAX_ACTIVE_CONNECTIONS = DEFAULT_JDBC__CONNECTION_POOL_SIZE;
   public static final int DEFAULT_JDBC_POOL__MAX_IDLE_CONNECTIONS = DEFAULT_JDBC_POOL__MAX_ACTIVE_CONNECTIONS;
   public static final int DEFAULT_JDBC_POOL__MIN_IDLE_CONNECTIONS = 0;
   public static final long DEFAULT_JDBC_POOL__MAX_WAIT_CONNECTIONS = -1L;
   public static final PoolExhaustedAction DEFAULT_JDBC_POOL__WHEN_EXHAUSTED_ACTION = PoolExhaustedAction.WHEN_EXHAUSTED_FAIL; //WHEN_EXHAUSTED_BLOCK;
   public static final boolean DEFAULT_JDBC_POOL__TEST_ON_BORROW = false;
   public static final boolean DEFAULT_JDBC_POOL__TEST_ON_RETURN = false;
   public static final boolean DEFAULT_JDBC_POOL__TEST_WHILE_IDLE = true;
 
   // The default number of objects to examine per run in the idle object evictor.
   public static final int DEFAULT_JDBC_POOL__NUM_TESTS_PER_EVICTION_RUN = 1; // default was DEFAULT_MAX_ACTIVE
   public static final long DEFAULT_JDBC_POOL__TIME_BETWEEN_EVICTION_RUNS_MILLIS = 5000L; // (5 sec) - default -1L (infinite) 
   public static final long DEFAULT_JDBC_POOL__MIN_EVICTABLE_IDLE_TIME_MILLIS = 60000L; // (60 secs) - default - 1000L * 60L * 30L - 30 mins;
   public static final long DEFAULT_JDBC_POOL__SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = -1;
   public static final boolean DEFAULT_JDBC_POOL__LIFO = true;
   public static final int DEFAULT_JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS = 20; // default was 8
   public static final int DEFAULT_JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS = DEFAULT_JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS; // default was 8
   public static final int DEFAULT_JDBC_POOL__MAX_IDLE_PREPARED_STATEMENTS = DEFAULT_JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS; // default was 8
   public static final int DEFAULT_JDBC_POOL__MIN_IDLE_PREPARED_STATEMENTS = 0;
   public static final long DEFAULT_JDBC_POOL__MAX_WAIT_PREPARED_STATEMENTS = -1L;
   public static final boolean DEFAULT_JDBC_POOL__LOG_ABANDONED = false; // default was false
   public static final boolean DEFAULT_JDBC_POOL__REMOVE_ABANDONED = false; // Flag whether to use abandoned timeout.
   public static final int DEFAULT_JDBC_POOL__REMOVE_ABANDONED_TIMEOUT = 300; // Timeout in seconds before an abandoned connection can be removed.
   // @formatter:on

   public static final String JDBC_SERVER__LOCAL_CONNECTIONS = "127.0.0.1";
   public static final String JDBC_SERVER__REMOTE_CONNECTIONS = "0.0.0.0";

   public static final String JDBC_SERVER__IMPL_CLASSNAME = qualifyServer("impl.classname");
   public static final String JDBC_SERVER__ACCEPT_REMOTE_CONNECTIONS = qualifyServer("accept.remote.connections");
   public static final String JDBC_SERVER__HOST = qualifyServer("host");
   public static final String JDBC_SERVER__PORT = qualifyServer("port");
   public static final String JDBC_SERVER__DB_DATA_PATH = qualifyServer("db.data.path");
   public static final String JDBC_SERVER__USE_RANDOM_PORT = qualifyServer("use.random.path");
   public static final String JDBC_SERVER__ALIVE_WAIT_TIMEOUT_MILLIS = qualifyServer("alive.wait.timeout.millis");
   public static final String JDBC_SERVER__START_UP_WAIT_TIMEOUT_MILLIS = qualifyServer("startup.wait.timeout.millis");
   public static final String JDBC_SERVER__USERNAME = qualifyServer("username");
   public static final String JDBC_SERVER__PASSWORD = qualifyServer("password");

   public static final String DEFAULT_JDBC_SERVER__IMPL_CLASSNAME = EMBEDDED_JDBC_SERVER_CLASSNAME;
   public static final boolean DEFAULT_JDBC_SERVER__ACCEPT_REMOTE_CONNECTIONS = true;
   public static final String DEFAULT_JDBC_SERVER__HOST = null;
   public static final int DEFAULT_JDBC_SERVER__PORT = -1;
   public static final String DEFAULT_JDBC_SERVER__DB_DATA_PATH = null;
   public static final boolean DEFAULT_JDBC_SERVER__USE_RANDOM_PORT = false;
   public static final long DEFAULT_JDBC_SERVER__ALIVE_WAIT_TIMEOUT_MILLIS = 15000L;
   public static final long DEFAULT_JDBC_SERVER__START_UP_WAIT_TIMEOUT_MILLIS = 15000L;
   public static final String DEFAULT_JDBC_SERVER__USERNAME = null;
   public static final String DEFAULT_JDBC_SERVER__PASSWORD = null;

   public static final String JDBC_SERVICE__CONFIGS = qualify("service");
   public static final String JDBC_SERVICE__ID = "service.id";
   public static final String JDBC_SERVICE__OSGI_BINDING = "osgi.binding";

   public static enum PoolExhaustedAction {
      WHEN_EXHAUSTED_FAIL((byte) 0),
      WHEN_EXHAUSTED_BLOCK((byte) 1),
      WHEN_EXHAUSTED_GROW((byte) 2);

      byte byteValue;

      private PoolExhaustedAction(byte byteValue) {
         this.byteValue = byteValue;
      }

      public byte asByteValue() {
         return byteValue;
      }

      public static PoolExhaustedAction fromByte(byte value) {
         PoolExhaustedAction toReturn = DEFAULT_JDBC_POOL__WHEN_EXHAUSTED_ACTION;
         for (PoolExhaustedAction action : PoolExhaustedAction.values()) {
            if (action.asByteValue() == value) {
               toReturn = action;
               break;
            }
         }
         return toReturn;
      }

      public static PoolExhaustedAction fromString(String value) {
         PoolExhaustedAction toReturn = DEFAULT_JDBC_POOL__WHEN_EXHAUSTED_ACTION;
         for (PoolExhaustedAction action : PoolExhaustedAction.values()) {
            if (action.name().equals(value)) {
               toReturn = action;
               break;
            }
         }
         return toReturn;
      }
   }

   public static enum JdbcDriverType {
      oracle_thin("oracle.jdbc.OracleDriver", "jdbc:oracle:thin", "%s@%s:%s:%s"),
      postgresql("org.postgresql.Driver", "jdbc:postgresql", "%s://%s:%s/%s"),
      mysql("com.mysql.jdbc.Driver", "jdbc:mysql", "%s://%s:%s/%s"),
      hsql("org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:hsql", "%s://%s:%s/%s");

      private String driver;
      private String prefix;
      private String uriFormat;

      private JdbcDriverType(String driver, String prefix, String uriFormat) {
         this.driver = driver;
         this.prefix = prefix;
         this.uriFormat = uriFormat;
      }

      public String getDriver() {
         return driver;
      }

      public String getPrefix() {
         return prefix;
      }

      public String getUriFormat() {
         return uriFormat;
      }

      public static JdbcDriverType fromUri(String uri) {
         JdbcDriverType toReturn = null;
         if (Strings.isValid(uri)) {
            for (JdbcDriverType type : JdbcDriverType.values()) {
               if (uri.startsWith(type.getPrefix())) {
                  toReturn = type;
                  break;
               }
            }
         }
         return toReturn;
      }
   }

}