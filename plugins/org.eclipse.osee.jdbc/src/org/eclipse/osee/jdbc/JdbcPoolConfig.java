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

import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__CONFIG_FILE_URI;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__ENABLED;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__LIFO;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__LOG_ABANDONED;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MAX_ACTIVE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MAX_IDLE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MAX_IDLE_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MAX_WAIT_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MAX_WAIT_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MIN_EVICTABLE_IDLE_TIME_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MIN_IDLE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__MIN_IDLE_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__NUM_TESTS_PER_EVICTION_RUN;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__PREPARED_STATEMENTS_ALLOWED;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__REMOVE_ABANDONED;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__REMOVE_ABANDONED_TIMEOUT;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__TEST_ON_BORROW;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__TEST_ON_RETURN;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__TEST_WHILE_IDLE;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__TIME_BETWEEN_EVICTION_RUNS_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__VALIDATION_QUERY_TIMEOUT_SECS;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC_POOL__WHEN_EXHAUSTED_ACTION;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__CONFIG_FILE_URI;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__ENABLED;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__LIFO;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__LOG_ABANDONED;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_ACTIVE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_IDLE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_IDLE_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_WAIT_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MAX_WAIT_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MIN_EVICTABLE_IDLE_TIME_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MIN_IDLE_CONNECTIONS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__MIN_IDLE_PREPARED_STATEMENTS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__NUM_TESTS_PER_EVICTION_RUN;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__PREPARED_STATEMENTS_ALLOWED;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__REMOVE_ABANDONED;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__REMOVE_ABANDONED_TIMEOUT;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__TEST_ON_BORROW;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__TEST_ON_RETURN;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__TEST_WHILE_IDLE;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__TIME_BETWEEN_EVICTION_RUNS_MILLIS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__VALIDATION_QUERY_TIMEOUT_SECS;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC_POOL__WHEN_EXHAUSTED_ACTION;
import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.get;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.getBoolean;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.getExhaustedAction;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.getInt;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.getLong;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcConstants.PoolExhaustedAction;

/**
 * @author Roberto E. Escobar
 */
public class JdbcPoolConfig {

   private boolean poolEnabled;
   private String poolConfigUri;
   private boolean poolPreparedStatementsAllowed;
   private int poolValidationQueryTimeoutSecs;
   private int poolMaxActiveConnections;
   private int poolMaxIdleConnections;
   private int poolMinIdleConnections;
   private long poolMaxWaitForConnection;
   private PoolExhaustedAction poolExhaustedAction;
   private boolean poolTestOnBorrowEnabled;
   private boolean poolTestOnReturnEnabled;
   private boolean poolTestWhileIdeEnabled;
   private long poolTimeBetweenEvictionCheckMillis;
   private int poolNumberTestsPerEvictionRun;
   private long poolMinEvictableIdleTimeMillis;
   private boolean poolLifo;
   private long poolSoftMinEvictableTimeoutMillis;
   private int poolMaxTotalPreparedStatements;
   private int poolMaxActivePreparedStatements;
   private int poolMaxIdlePreparedStatements;
   private int poolMinIdlePreparedStatements;
   private long poolMaxWaitPreparedStatements;
   private boolean poolAbandonedLoggingEnabled;
   private boolean poolAbandonedRemovalEnabled;
   private int poolAbandonedRemovalTimeout;

   JdbcPoolConfig() {
      super();
      reset();
   }

   void reset() {
      setPoolEnabled(DEFAULT_JDBC_POOL__ENABLED);
      setPoolConfigUri(DEFAULT_JDBC_POOL__CONFIG_FILE_URI);
      setPoolAbandonedLoggingEnabled(DEFAULT_JDBC_POOL__LOG_ABANDONED);
      setPoolAbandonedRemovalEnabled(DEFAULT_JDBC_POOL__REMOVE_ABANDONED);
      setPoolAbandonedRemovalTimeout(DEFAULT_JDBC_POOL__REMOVE_ABANDONED_TIMEOUT);
      setPoolExhaustedAction(DEFAULT_JDBC_POOL__WHEN_EXHAUSTED_ACTION);
      setPoolLifo(DEFAULT_JDBC_POOL__LIFO);
      setPoolMaxActiveConnections(DEFAULT_JDBC_POOL__MAX_ACTIVE_CONNECTIONS);
      setPoolMaxActivePreparedStatements(DEFAULT_JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS);
      setPoolMaxIdleConnections(DEFAULT_JDBC_POOL__MAX_IDLE_CONNECTIONS);
      setPoolMaxIdlePreparedStatements(DEFAULT_JDBC_POOL__MAX_IDLE_PREPARED_STATEMENTS);
      setPoolMaxTotalPreparedStatements(DEFAULT_JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS);
      setPoolMaxWaitForConnection(DEFAULT_JDBC_POOL__MAX_WAIT_CONNECTIONS);
      setPoolMaxWaitPreparedStatements(DEFAULT_JDBC_POOL__MAX_WAIT_PREPARED_STATEMENTS);
      setPoolMinEvictableIdleTimeMillis(DEFAULT_JDBC_POOL__MIN_EVICTABLE_IDLE_TIME_MILLIS);
      setPoolMinIdleConnections(DEFAULT_JDBC_POOL__MIN_IDLE_CONNECTIONS);
      setPoolMinIdlePreparedStatements(DEFAULT_JDBC_POOL__MIN_IDLE_PREPARED_STATEMENTS);
      setPoolNumberTestsPerEvictionRun(DEFAULT_JDBC_POOL__NUM_TESTS_PER_EVICTION_RUN);
      setPoolPreparedStatementsAllowed(DEFAULT_JDBC_POOL__PREPARED_STATEMENTS_ALLOWED);
      setPoolSoftMinEvictableTimeoutMillis(DEFAULT_JDBC_POOL__SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
      setPoolTestOnBorrowEnabled(DEFAULT_JDBC_POOL__TEST_ON_BORROW);
      setPoolTestOnReturnEnabled(DEFAULT_JDBC_POOL__TEST_ON_RETURN);
      setPoolTestWhileIdeEnabled(DEFAULT_JDBC_POOL__TEST_WHILE_IDLE);
      setPoolTimeBetweenEvictionCheckMillis(DEFAULT_JDBC_POOL__TIME_BETWEEN_EVICTION_RUNS_MILLIS);
      setPoolValidationQueryTimeoutSecs(DEFAULT_JDBC_POOL__VALIDATION_QUERY_TIMEOUT_SECS);
   }

   public boolean isPoolEnabled() {
      return poolEnabled;
   }

   public String getPoolConfigUri() {
      return poolConfigUri;
   }

   public String getPoolConnectionDriver() {
      return JdbcConstants.DEFAULT_JDBC_POOL__CONNECTION_DRIVER;
   }

   public String getPoolConnectionId() {
      return JdbcConstants.DEFAUT_JDBC_POOL__CONNECTION_ID;
   }

   public boolean isPoolPreparedStatementsAllowed() {
      return poolPreparedStatementsAllowed;
   }

   public int getPoolValidationQueryTimeoutSecs() {
      return poolValidationQueryTimeoutSecs;
   }

   public int getPoolMaxActiveConnections() {
      return poolMaxActiveConnections;
   }

   public int getPoolMaxIdleConnections() {
      return poolMaxIdleConnections;
   }

   public int getPoolMinIdleConnections() {
      return poolMinIdleConnections;
   }

   public long getPoolMaxWaitForConnection() {
      return poolMaxWaitForConnection;
   }

   public PoolExhaustedAction getPoolExhaustedAction() {
      return poolExhaustedAction;
   }

   public boolean isPoolTestOnBorrowEnabled() {
      return poolTestOnBorrowEnabled;
   }

   public boolean isPoolTestOnReturnEnabled() {
      return poolTestOnReturnEnabled;
   }

   public boolean isPoolTestWhileIdeEnabled() {
      return poolTestWhileIdeEnabled;
   }

   public long getPoolTimeBetweenEvictionCheckMillis() {
      return poolTimeBetweenEvictionCheckMillis;
   }

   public int getPoolNumberTestsPerEvictionRun() {
      return poolNumberTestsPerEvictionRun;
   }

   public long getPoolMinEvictableIdleTimeMillis() {
      return poolMinEvictableIdleTimeMillis;
   }

   public boolean isPoolLifo() {
      return poolLifo;
   }

   public long getPoolSoftMinEvictableTimeoutMillis() {
      return poolSoftMinEvictableTimeoutMillis;
   }

   public int getPoolMaxTotalPreparedStatements() {
      return poolMaxTotalPreparedStatements;
   }

   public int getPoolMaxActivePreparedStatements() {
      return poolMaxActivePreparedStatements;
   }

   public int getPoolMaxIdlePreparedStatements() {
      return poolMaxIdlePreparedStatements;
   }

   public int getPoolMinIdlePreparedStatements() {
      return poolMinIdlePreparedStatements;
   }

   public long getPoolMaxWaitPreparedStatements() {
      return poolMaxWaitPreparedStatements;
   }

   public boolean isPoolAbandonedLoggingEnabled() {
      return poolAbandonedLoggingEnabled;
   }

   public boolean isPoolAbandonedRemovalEnabled() {
      return poolAbandonedRemovalEnabled;
   }

   public int getPoolAbandonedRemovalTimeout() {
      return poolAbandonedRemovalTimeout;
   }

   void setPoolEnabled(boolean poolEnabled) {
      this.poolEnabled = poolEnabled;
   }

   void setPoolConfigUri(String poolConfigUri) {
      if (poolConfigUri != null && !poolConfigUri.equals(this.poolConfigUri)) {
         readPoolConfig(poolConfigUri);
      }
      this.poolConfigUri = poolConfigUri;
   }

   void setPoolPreparedStatementsAllowed(boolean poolPreparedStatementsAllowed) {
      this.poolPreparedStatementsAllowed = poolPreparedStatementsAllowed;
   }

   void setPoolValidationQueryTimeoutSecs(int poolValidationQueryTimeoutSecs) {
      this.poolValidationQueryTimeoutSecs = poolValidationQueryTimeoutSecs;
   }

   void setPoolMaxActiveConnections(int poolMaxActiveConnections) {
      this.poolMaxActiveConnections = poolMaxActiveConnections;
   }

   void setPoolMaxIdleConnections(int poolMaxIdleConnections) {
      this.poolMaxIdleConnections = poolMaxIdleConnections;
   }

   void setPoolMinIdleConnections(int poolMinIdleConnections) {
      this.poolMinIdleConnections = poolMinIdleConnections;
   }

   void setPoolMaxWaitForConnection(long poolMaxWaitForConnection) {
      this.poolMaxWaitForConnection = poolMaxWaitForConnection;
   }

   void setPoolExhaustedAction(PoolExhaustedAction poolExhaustedAction) {
      this.poolExhaustedAction = poolExhaustedAction;
   }

   void setPoolTestOnBorrowEnabled(boolean poolTestOnBorrowEnabled) {
      this.poolTestOnBorrowEnabled = poolTestOnBorrowEnabled;
   }

   void setPoolTestOnReturnEnabled(boolean poolTestOnReturnEnabled) {
      this.poolTestOnReturnEnabled = poolTestOnReturnEnabled;
   }

   void setPoolTestWhileIdeEnabled(boolean poolTestWhileIdeEnabled) {
      this.poolTestWhileIdeEnabled = poolTestWhileIdeEnabled;
   }

   void setPoolTimeBetweenEvictionCheckMillis(long poolTimeBetweenEvictionCheckMillis) {
      this.poolTimeBetweenEvictionCheckMillis = poolTimeBetweenEvictionCheckMillis;
   }

   void setPoolNumberTestsPerEvictionRun(int poolNumberTestsPerEvictionRun) {
      this.poolNumberTestsPerEvictionRun = poolNumberTestsPerEvictionRun;
   }

   void setPoolMinEvictableIdleTimeMillis(long poolMinEvictableIdleTimeMillis) {
      this.poolMinEvictableIdleTimeMillis = poolMinEvictableIdleTimeMillis;
   }

   void setPoolLifo(boolean poolLifo) {
      this.poolLifo = poolLifo;
   }

   void setPoolSoftMinEvictableTimeoutMillis(long poolSoftMinEvictableTimeoutMillis) {
      this.poolSoftMinEvictableTimeoutMillis = poolSoftMinEvictableTimeoutMillis;
   }

   void setPoolMaxTotalPreparedStatements(int poolMaxTotalPreparedStatements) {
      this.poolMaxTotalPreparedStatements = poolMaxTotalPreparedStatements;
   }

   void setPoolMaxActivePreparedStatements(int poolMaxActivePreparedStatements) {
      this.poolMaxActivePreparedStatements = poolMaxActivePreparedStatements;
   }

   void setPoolMaxIdlePreparedStatements(int poolMaxIdlePreparedStatements) {
      this.poolMaxIdlePreparedStatements = poolMaxIdlePreparedStatements;
   }

   void setPoolMinIdlePreparedStatements(int poolMinIdlePreparedStatements) {
      this.poolMinIdlePreparedStatements = poolMinIdlePreparedStatements;
   }

   void setPoolMaxWaitPreparedStatements(long poolMaxWaitPreparedStatements) {
      this.poolMaxWaitPreparedStatements = poolMaxWaitPreparedStatements;
   }

   void setPoolAbandonedLoggingEnabled(boolean poolAbandonedLoggingEnabled) {
      this.poolAbandonedLoggingEnabled = poolAbandonedLoggingEnabled;
   }

   void setPoolAbandonedRemovalEnabled(boolean poolAbandonedRemovalEnabled) {
      this.poolAbandonedRemovalEnabled = poolAbandonedRemovalEnabled;
   }

   void setPoolAbandonedRemovalTimeout(int poolAbandonedRemovalTimeout) {
      this.poolAbandonedRemovalTimeout = poolAbandonedRemovalTimeout;
   }

   void readProperties(Map<String, Object> src) {
      //@formatter:off
      setPoolEnabled(getBoolean(src, JDBC_POOL__ENABLED, DEFAULT_JDBC_POOL__ENABLED));
      setPoolConfigUri(get(src, JDBC_POOL__CONFIG_FILE_URI, DEFAULT_JDBC_POOL__CONFIG_FILE_URI));
      setPoolAbandonedLoggingEnabled(getBoolean(src, JDBC_POOL__LOG_ABANDONED, DEFAULT_JDBC_POOL__LOG_ABANDONED));
      setPoolAbandonedRemovalEnabled(getBoolean(src, JDBC_POOL__REMOVE_ABANDONED, DEFAULT_JDBC_POOL__REMOVE_ABANDONED));
      setPoolAbandonedRemovalTimeout(getInt(src, JDBC_POOL__REMOVE_ABANDONED_TIMEOUT, DEFAULT_JDBC_POOL__REMOVE_ABANDONED_TIMEOUT));
      setPoolExhaustedAction(getExhaustedAction(src, JDBC_POOL__WHEN_EXHAUSTED_ACTION, DEFAULT_JDBC_POOL__WHEN_EXHAUSTED_ACTION));
      setPoolLifo(getBoolean(src, JDBC_POOL__LIFO, DEFAULT_JDBC_POOL__LIFO));
      setPoolMaxActiveConnections(getInt(src, JDBC_POOL__MAX_ACTIVE_CONNECTIONS, DEFAULT_JDBC_POOL__MAX_ACTIVE_CONNECTIONS));
      setPoolMaxActivePreparedStatements(getInt(src, JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS, DEFAULT_JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS));
      setPoolMaxIdleConnections(getInt(src, JDBC_POOL__MAX_IDLE_CONNECTIONS, DEFAULT_JDBC_POOL__MAX_IDLE_CONNECTIONS));
      setPoolMaxIdlePreparedStatements(getInt(src, JDBC_POOL__MAX_IDLE_PREPARED_STATEMENTS, DEFAULT_JDBC_POOL__MAX_IDLE_PREPARED_STATEMENTS));
      setPoolMaxTotalPreparedStatements(getInt(src, JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS, DEFAULT_JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS));
      setPoolMaxWaitForConnection(getLong(src, JDBC_POOL__MAX_WAIT_CONNECTIONS, DEFAULT_JDBC_POOL__MAX_WAIT_CONNECTIONS));
      setPoolMaxWaitPreparedStatements(getLong(src, JDBC_POOL__MAX_WAIT_PREPARED_STATEMENTS, DEFAULT_JDBC_POOL__MAX_WAIT_PREPARED_STATEMENTS));
      setPoolMinEvictableIdleTimeMillis(getLong(src, JDBC_POOL__MIN_EVICTABLE_IDLE_TIME_MILLIS, DEFAULT_JDBC_POOL__MIN_EVICTABLE_IDLE_TIME_MILLIS));
      setPoolMinIdleConnections(getInt(src, JDBC_POOL__MIN_IDLE_CONNECTIONS, DEFAULT_JDBC_POOL__MIN_IDLE_CONNECTIONS));
      setPoolMinIdlePreparedStatements(getInt(src, JDBC_POOL__MIN_IDLE_PREPARED_STATEMENTS, DEFAULT_JDBC_POOL__MIN_IDLE_PREPARED_STATEMENTS));
      setPoolNumberTestsPerEvictionRun(getInt(src, JDBC_POOL__NUM_TESTS_PER_EVICTION_RUN, DEFAULT_JDBC_POOL__NUM_TESTS_PER_EVICTION_RUN));
      setPoolPreparedStatementsAllowed(getBoolean(src, JDBC_POOL__PREPARED_STATEMENTS_ALLOWED, DEFAULT_JDBC_POOL__PREPARED_STATEMENTS_ALLOWED));
      setPoolSoftMinEvictableTimeoutMillis(getLong(src, JDBC_POOL__SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS, DEFAULT_JDBC_POOL__SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
      setPoolTestOnBorrowEnabled(getBoolean(src, JDBC_POOL__TEST_ON_BORROW, DEFAULT_JDBC_POOL__TEST_ON_BORROW));
      setPoolTestOnReturnEnabled(getBoolean(src, JDBC_POOL__TEST_ON_RETURN, DEFAULT_JDBC_POOL__TEST_ON_RETURN));
      setPoolTestWhileIdeEnabled(getBoolean(src, JDBC_POOL__TEST_WHILE_IDLE, DEFAULT_JDBC_POOL__TEST_WHILE_IDLE));
      setPoolTimeBetweenEvictionCheckMillis(getLong(src, JDBC_POOL__TIME_BETWEEN_EVICTION_RUNS_MILLIS, DEFAULT_JDBC_POOL__TIME_BETWEEN_EVICTION_RUNS_MILLIS));
      setPoolValidationQueryTimeoutSecs(getInt(src, JDBC_POOL__VALIDATION_QUERY_TIMEOUT_SECS, DEFAULT_JDBC_POOL__VALIDATION_QUERY_TIMEOUT_SECS));
      //@formatter:on
   }

   protected JdbcPoolConfig copy() {
      JdbcPoolConfig data = new JdbcPoolConfig();
      data.poolEnabled = this.poolEnabled;
      data.poolConfigUri = this.poolConfigUri;
      data.poolPreparedStatementsAllowed = this.poolPreparedStatementsAllowed;
      data.poolValidationQueryTimeoutSecs = this.poolValidationQueryTimeoutSecs;
      data.poolMaxActiveConnections = this.poolMaxActiveConnections;
      data.poolMaxIdleConnections = this.poolMaxIdleConnections;
      data.poolMinIdleConnections = this.poolMinIdleConnections;
      data.poolMaxWaitForConnection = this.poolMaxWaitForConnection;
      data.poolExhaustedAction = this.poolExhaustedAction;
      data.poolTestOnBorrowEnabled = this.poolTestOnBorrowEnabled;
      data.poolTestOnReturnEnabled = this.poolTestOnReturnEnabled;
      data.poolTestWhileIdeEnabled = this.poolTestWhileIdeEnabled;
      data.poolTimeBetweenEvictionCheckMillis = this.poolTimeBetweenEvictionCheckMillis;
      data.poolNumberTestsPerEvictionRun = this.poolNumberTestsPerEvictionRun;
      data.poolMinEvictableIdleTimeMillis = this.poolMinEvictableIdleTimeMillis;
      data.poolLifo = this.poolLifo;
      data.poolSoftMinEvictableTimeoutMillis = this.poolSoftMinEvictableTimeoutMillis;
      data.poolMaxTotalPreparedStatements = this.poolMaxTotalPreparedStatements;
      data.poolMaxActivePreparedStatements = this.poolMaxActivePreparedStatements;
      data.poolMaxIdlePreparedStatements = this.poolMaxIdlePreparedStatements;
      data.poolMinIdlePreparedStatements = this.poolMinIdlePreparedStatements;
      data.poolMaxWaitPreparedStatements = this.poolMaxWaitPreparedStatements;
      data.poolAbandonedLoggingEnabled = this.poolAbandonedLoggingEnabled;
      data.poolAbandonedRemovalEnabled = this.poolAbandonedRemovalEnabled;
      data.poolAbandonedRemovalTimeout = this.poolAbandonedRemovalTimeout;
      return data;
   }

   protected void copy(JdbcPoolConfig other) {
      this.poolEnabled = other.poolEnabled;
      this.poolConfigUri = other.poolConfigUri;
      this.poolPreparedStatementsAllowed = other.poolPreparedStatementsAllowed;
      this.poolValidationQueryTimeoutSecs = other.poolValidationQueryTimeoutSecs;
      this.poolMaxActiveConnections = other.poolMaxActiveConnections;
      this.poolMaxIdleConnections = other.poolMaxIdleConnections;
      this.poolMinIdleConnections = other.poolMinIdleConnections;
      this.poolMaxWaitForConnection = other.poolMaxWaitForConnection;
      this.poolExhaustedAction = other.poolExhaustedAction;
      this.poolTestOnBorrowEnabled = other.poolTestOnBorrowEnabled;
      this.poolTestOnReturnEnabled = other.poolTestOnReturnEnabled;
      this.poolTestWhileIdeEnabled = other.poolTestWhileIdeEnabled;
      this.poolTimeBetweenEvictionCheckMillis = other.poolTimeBetweenEvictionCheckMillis;
      this.poolNumberTestsPerEvictionRun = other.poolNumberTestsPerEvictionRun;
      this.poolMinEvictableIdleTimeMillis = other.poolMinEvictableIdleTimeMillis;
      this.poolLifo = other.poolLifo;
      this.poolSoftMinEvictableTimeoutMillis = other.poolSoftMinEvictableTimeoutMillis;
      this.poolMaxTotalPreparedStatements = other.poolMaxTotalPreparedStatements;
      this.poolMaxActivePreparedStatements = other.poolMaxActivePreparedStatements;
      this.poolMaxIdlePreparedStatements = other.poolMaxIdlePreparedStatements;
      this.poolMinIdlePreparedStatements = other.poolMinIdlePreparedStatements;
      this.poolMaxWaitPreparedStatements = other.poolMaxWaitPreparedStatements;
      this.poolAbandonedLoggingEnabled = other.poolAbandonedLoggingEnabled;
      this.poolAbandonedRemovalEnabled = other.poolAbandonedRemovalEnabled;
      this.poolAbandonedRemovalTimeout = other.poolAbandonedRemovalTimeout;
   }

   private void readPoolConfig(String configUri) {
      URI uri = null;
      if (Strings.isValid(configUri)) {
         if (!configUri.contains("://")) {
            uri = new File(configUri).toURI();
         } else {
            try {
               uri = new URI(configUri);
            } catch (URISyntaxException ex) {
               throw newJdbcException(ex, "Invalid pool config uri [%s]", configUri);
            }
         }
      }
      loadPoolConfig(uri);
   }

   private void loadPoolConfig(URI uri) {
      if (uri != null) {
         InputStream inputStream = null;
         Properties props = new Properties();
         try {
            URL url = uri.toURL();
            inputStream = new BufferedInputStream(url.openStream());
            props.loadFromXML(inputStream);
         } catch (Exception ex) {
            throw newJdbcException(ex, "Error reading user specified connection pool config uri [%s]", uri);
         } finally {
            Lib.close(inputStream);
         }

         //@formatter:off
         setPoolAbandonedLoggingEnabled(getBooleanObj(props, "logAbandoned", DEFAULT_JDBC_POOL__LOG_ABANDONED));
         setPoolAbandonedRemovalEnabled(getBooleanObj(props, "removeAbandoned", DEFAULT_JDBC_POOL__REMOVE_ABANDONED));
         setPoolAbandonedRemovalTimeout(getIntObj(props, "removeAbandonedTimeout", DEFAULT_JDBC_POOL__REMOVE_ABANDONED_TIMEOUT));
         setPoolExhaustedAction(PoolExhaustedAction.fromByte(getByteObj(props, "whenExhaustedAction", DEFAULT_JDBC_POOL__WHEN_EXHAUSTED_ACTION.asByteValue())));
         setPoolLifo(getBooleanObj(props, "lifo", DEFAULT_JDBC_POOL__LIFO));
         setPoolMaxActiveConnections(getIntObj(props, "maxActive", DEFAULT_JDBC_POOL__MAX_ACTIVE_CONNECTIONS));
         setPoolMaxActivePreparedStatements( getIntObj(props, "maxActivePreparedStatements", DEFAULT_JDBC_POOL__MAX_ACTIVE_PREPARED_STATEMENTS));
         setPoolMaxIdleConnections(getIntObj(props, "maxIdle", DEFAULT_JDBC_POOL__MAX_IDLE_CONNECTIONS));
         setPoolMaxIdlePreparedStatements(getIntObj(props, "maxIdlePreparedStatements", DEFAULT_JDBC_POOL__MAX_IDLE_PREPARED_STATEMENTS));
         setPoolMaxTotalPreparedStatements(getIntObj(props, "maxTotalPreparedStatements", DEFAULT_JDBC_POOL__MAX_TOTAL_PREPARED_STATEMENTS));
         setPoolMaxWaitForConnection(getLongObj(props, "maxWait", DEFAULT_JDBC_POOL__MAX_WAIT_CONNECTIONS));
         setPoolMaxWaitPreparedStatements( getLongObj(props, "maxWaitPreparedStatements", DEFAULT_JDBC_POOL__MAX_WAIT_PREPARED_STATEMENTS));
         setPoolMinEvictableIdleTimeMillis(getLongObj(props, "minEvictableIdleTimeMillis", DEFAULT_JDBC_POOL__MIN_EVICTABLE_IDLE_TIME_MILLIS));
         setPoolMinIdleConnections(getIntObj(props, "minIdle", DEFAULT_JDBC_POOL__MIN_IDLE_CONNECTIONS));
         setPoolMinIdlePreparedStatements(getIntObj(props, "minIdlePreparedStatements", DEFAULT_JDBC_POOL__MIN_IDLE_PREPARED_STATEMENTS));
         setPoolNumberTestsPerEvictionRun(getIntObj(props, "numTestsPerEvictionRun", DEFAULT_JDBC_POOL__NUM_TESTS_PER_EVICTION_RUN));
         setPoolPreparedStatementsAllowed(getBooleanObj(props, "poolPreparedStatements", DEFAULT_JDBC_POOL__PREPARED_STATEMENTS_ALLOWED));
         setPoolSoftMinEvictableTimeoutMillis(getLongObj(props, "softMinEvictableIdleTimeMillis", DEFAULT_JDBC_POOL__SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS));
         setPoolTestOnBorrowEnabled(getBooleanObj(props, "testOnBorrow", DEFAULT_JDBC_POOL__TEST_ON_BORROW));
         setPoolTestOnReturnEnabled(getBooleanObj(props, "testOnReturn", DEFAULT_JDBC_POOL__TEST_ON_RETURN));
         setPoolTestWhileIdeEnabled(getBooleanObj(props, "testWhileIdle", DEFAULT_JDBC_POOL__TEST_WHILE_IDLE));
         setPoolTimeBetweenEvictionCheckMillis(getLongObj(props, "timeBetweenEvictionRunsMillis", DEFAULT_JDBC_POOL__TIME_BETWEEN_EVICTION_RUNS_MILLIS));
         setPoolValidationQueryTimeoutSecs(getIntObj(props, "validationQueryTimeoutSecs", DEFAULT_JDBC_POOL__VALIDATION_QUERY_TIMEOUT_SECS));
         //@formatter:on
      }
   }

   private static String getObj(Properties props, String key, String defaultValue) {
      String value = defaultValue;
      if (props != null) {
         value = props.getProperty(key, defaultValue);
         props.setProperty(key, value);
      }
      return value;
   }

   private static int getIntObj(Properties props, String key, int defaultValue) {
      return Integer.valueOf(getObj(props, key, String.valueOf(defaultValue)));
   }

   private static long getLongObj(Properties props, String key, long defaultValue) {
      return Long.valueOf(getObj(props, key, String.valueOf(defaultValue)));
   }

   private static boolean getBooleanObj(Properties props, String key, boolean defaultValue) {
      return Boolean.valueOf(getObj(props, key, String.valueOf(defaultValue)));
   }

   private static byte getByteObj(Properties props, String key, byte defaultWhenExhaustedAction) {
      return Byte.valueOf(getObj(props, key, String.valueOf(defaultWhenExhaustedAction)));
   }

}
