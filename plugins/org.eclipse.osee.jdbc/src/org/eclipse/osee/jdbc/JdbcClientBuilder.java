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

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_SERVER__LOCAL_CONNECTIONS;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcConstants.JdbcDriverType;
import org.eclipse.osee.jdbc.JdbcConstants.PoolExhaustedAction;
import org.eclipse.osee.jdbc.internal.JdbcClientImpl;
import org.eclipse.osee.jdbc.internal.JdbcConnectionFactory;
import org.eclipse.osee.jdbc.internal.JdbcConnectionFactoryManager;
import org.eclipse.osee.jdbc.internal.JdbcConnectionInfo;
import org.eclipse.osee.jdbc.internal.JdbcConnectionProvider;
import org.eclipse.osee.jdbc.internal.JdbcSequenceProvider;
import org.eclipse.osee.jdbc.internal.JdbcUtil;
import org.eclipse.osee.jdbc.internal.PoolFactory;
import org.eclipse.osee.jdbc.internal.PooledConnectionProvider;
import org.eclipse.osee.jdbc.internal.SimpleConnectionProvider;

/**
 * Class used to configure and build JdbcClient instances. JdbcClients for a particular database only needs to be
 * created once. Clients are thread-safe. Database connections are typically managed by a connection pool.
 * 
 * @author Roberto E. Escobar
 */
public final class JdbcClientBuilder extends JdbcClientConfig {

   public static JdbcClientBuilder newBuilder() {
      return new JdbcClientBuilder();
   }

   public static JdbcClientBuilder newBuilder(JdbcClientConfig config) {
      return newBuilder().withConfig(config);
   }

   public static JdbcClientBuilder newBuilder(Map<String, Object> properties) {
      return newBuilder().properties(properties);
   }

   public static JdbcClient fromConfig(JdbcClientConfig config) {
      return newBuilder(config).build();
   }

   public static JdbcClient fromProperties(Map<String, Object> properties) {
      return newBuilder(properties).build();
   }

   public static JdbcClientBuilder hsql(String db, int port) {
      return hsql(db, null, port);
   }

   public static JdbcClientBuilder hsql(String db, String host, int port) {
      return newBuilder().fromType(JdbcDriverType.hsql, db, host, port);
   }

   public static JdbcClientBuilder mysql(String db, String host, int port) {
      return newBuilder().fromType(JdbcDriverType.mysql, db, host, port);
   }

   public static JdbcClientBuilder postgresql(String db, String host, int port) {
      return newBuilder().fromType(JdbcDriverType.postgresql, db, host, port);
   }

   public static JdbcClientBuilder oracle(String db, String host, int port) {
      return newBuilder().fromType(JdbcDriverType.oracle_thin, db, host, port);
   }

   ////////////////////////////////////////////////////////////////////////////////

   private JdbcClientBuilder() {
      // Builder
   }

   public JdbcClient build() {
      JdbcClientConfig cfg = copy();

      if (!Strings.isValid(cfg.getDbDriver())) {
         JdbcDriverType type = JdbcDriverType.fromUri(cfg.getDbUri());
         if (type != null) {
            dbDriver(type.getDriver());
         }
      }

      JdbcConnectionProvider connectionProvider = getConnectionProvider(cfg.getPoolConfig());
      JdbcSequenceProvider sequenceProvider = new JdbcSequenceProvider();
      JdbcConnectionInfo dbInfo =
         JdbcUtil.newConnectionInfo(cfg.getDbDriver(), cfg.getDbUri(), cfg.getDbProps(), cfg.isDbAppendPropsToUri());
      return new JdbcClientImpl(cfg, connectionProvider, sequenceProvider, dbInfo);
   }

   private JdbcConnectionProvider getConnectionProvider(JdbcPoolConfig poolConfig) {
      Map<String, JdbcConnectionFactory> factories = new ConcurrentHashMap<>();
      JdbcConnectionFactoryManager manager = new JdbcConnectionFactoryManager(factories);

      JdbcConnectionProvider connectionProvider;
      if (poolConfig.isPoolEnabled()) {
         PoolFactory poolFactory = new PoolFactory(manager, poolConfig);
         connectionProvider = new PooledConnectionProvider(poolFactory);
      } else {
         connectionProvider = new SimpleConnectionProvider(manager);
      }
      return connectionProvider;
   }

   public JdbcClientBuilder properties(Map<String, Object> src) {
      readProperties(src);
      return this;
   }

   public JdbcClientBuilder withConfig(JdbcClientConfig config) {
      this.copy(config);
      return this;
   }

   public JdbcClientBuilder dbDriver(String driver) {
      setDbDriver(driver);
      return this;
   }

   public JdbcClientBuilder production(boolean production) {
      setProduction(production);
      return this;
   }

   public JdbcClientBuilder dbUri(String format, Object... args) {
      dbUri(String.format(format, args));
      return this;
   }

   public JdbcClientBuilder dbUri(String uri) {
      setDbUri(uri);
      return this;
   }

   public JdbcClientBuilder dbParamsInUri(boolean value) {
      setDbAppendPropsToUri(value);
      return this;
   }

   public JdbcClientBuilder dbUsername(String username) {
      setDbUsername(username);
      return this;
   }

   public JdbcClientBuilder dbPassword(String password) {
      setDbPassword(password);
      return this;
   }

   public JdbcClientBuilder dbParam(String key, String value) {
      addDbParam(key, value);
      return this;
   }

   public JdbcClientBuilder poolAbandonedLoggingEnabled(boolean value) {
      getPoolConfig().setPoolAbandonedLoggingEnabled(value);
      return this;
   }

   public JdbcClientBuilder poolAbandonedRemovalEnabled(boolean value) {
      getPoolConfig().setPoolAbandonedRemovalEnabled(value);
      return this;
   }

   public JdbcClientBuilder poolAbandonedRemovalTimeout(int value) {
      getPoolConfig().setPoolAbandonedRemovalTimeout(value);
      return this;
   }

   public JdbcClientBuilder poolEnabled(boolean value) {
      getPoolConfig().setPoolEnabled(value);
      return this;
   }

   public JdbcClientBuilder poolExhaustedAction(PoolExhaustedAction value) {
      getPoolConfig().setPoolExhaustedAction(value);
      return this;
   }

   public JdbcClientBuilder poolLifo(boolean value) {
      getPoolConfig().setPoolLifo(value);
      return this;
   }

   public JdbcClientBuilder poolMaxActiveConnections(int value) {
      getPoolConfig().setPoolMaxActiveConnections(value);
      return this;
   }

   public JdbcClientBuilder poolMaxActivePreparedStatements(int value) {
      getPoolConfig().setPoolMaxActivePreparedStatements(value);
      return this;
   }

   public JdbcClientBuilder poolMaxIdleConnections(int value) {
      getPoolConfig().setPoolMaxIdleConnections(value);
      return this;
   }

   public JdbcClientBuilder poolMaxIdlePreparedStatements(int value) {
      getPoolConfig().setPoolMaxIdlePreparedStatements(value);
      return this;
   }

   public JdbcClientBuilder poolMaxTotalPreparedStatements(int value) {
      getPoolConfig().setPoolMaxTotalPreparedStatements(value);
      return this;
   }

   public JdbcClientBuilder poolMaxWaitForConnection(long value) {
      getPoolConfig().setPoolMaxWaitForConnection(value);
      return this;
   }

   public JdbcClientBuilder poolMaxWaitPreparedStatements(long value) {
      getPoolConfig().setPoolMaxWaitPreparedStatements(value);
      return this;
   }

   public JdbcClientBuilder poolMinEvictableIdleTimeMillis(long value) {
      getPoolConfig().setPoolMinEvictableIdleTimeMillis(value);
      return this;
   }

   public JdbcClientBuilder poolMinIdleConnections(int value) {
      getPoolConfig().setPoolMinIdleConnections(value);
      return this;
   }

   public JdbcClientBuilder poolMinIdlePreparedStatements(int value) {
      getPoolConfig().setPoolMinIdlePreparedStatements(value);
      return this;
   }

   public JdbcClientBuilder poolNumberTestsPerEvictionRun(int value) {
      getPoolConfig().setPoolNumberTestsPerEvictionRun(value);
      return this;
   }

   public JdbcClientBuilder poolPreparedStatementsAllowed(boolean value) {
      getPoolConfig().setPoolPreparedStatementsAllowed(value);
      return this;
   }

   public JdbcClientBuilder poolSoftMinEvictableTimeoutMillis(long value) {
      getPoolConfig().setPoolSoftMinEvictableTimeoutMillis(value);
      return this;
   }

   public JdbcClientBuilder poolTestOnBorrowEnabled(boolean value) {
      getPoolConfig().setPoolTestOnBorrowEnabled(value);
      return this;
   }

   public JdbcClientBuilder poolTestOnReturnEnabled(boolean value) {
      getPoolConfig().setPoolTestOnReturnEnabled(value);
      return this;
   }

   public JdbcClientBuilder poolTestWhileIdeEnabled(boolean value) {
      getPoolConfig().setPoolTestWhileIdeEnabled(value);
      return this;
   }

   public JdbcClientBuilder poolTimeBetweenEvictionCheckMillis(long value) {
      getPoolConfig().setPoolTimeBetweenEvictionCheckMillis(value);
      return this;
   }

   public JdbcClientBuilder poolValidationQueryTimeoutSecs(int value) {
      getPoolConfig().setPoolValidationQueryTimeoutSecs(value);
      return this;
   }

   public JdbcClientBuilder fromType(JdbcDriverType type, String db, int port) {
      return fromType(type, db, null, port);
   }

   public JdbcClientBuilder fromType(JdbcDriverType type, String db, String host, int port) {
      dbDriver(type.getDriver());

      String dbHost = host;
      if (JdbcDriverType.hsql == type && !Strings.isValid(dbHost)) {
         dbHost = JDBC_SERVER__LOCAL_CONNECTIONS;
      }
      dbUri(type.getUriFormat(), type.getPrefix(), dbHost, port, db);

      switch (type) {
         case hsql:
            dbParamsInUri(true);
            if (!Strings.isValid(getDbUsername())) {
               dbUsername("public");
            }
            break;
         case oracle_thin:
            if (!getDbProps().containsKey("SetBigStringTryClob")) {
               dbParam("SetBigStringTryClob", "true");
            }
            if (!getDbProps().containsKey("includeSynonyms")) {
               dbParam("includeSynonyms", "true");
            }
            break;
         default:
            break;
      }
      return this;
   }

}