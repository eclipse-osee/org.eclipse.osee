/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.database.internal.core;

import java.util.Properties;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * @see http://commons.apache.org/proper/commons-dbcp/configuration.html
 * @author Roberto E. Escobar
 */
public final class PoolConfigUtil {

   public static final byte WHEN_EXHAUSTED_FAIL = 0;
   public static final byte WHEN_EXHAUSTED_BLOCK = 1;
   public static final byte WHEN_EXHAUSTED_GROW = 2;

   public static final byte DEFAULT_WHEN_EXHAUSTED_ACTION = WHEN_EXHAUSTED_BLOCK;

   public static final int DEFAULT_MAX_ACTIVE = OseeProperties.getOseeDbConnectionCount(); // default was 8
   public static final int DEFAULT_MAX_IDLE = DEFAULT_MAX_ACTIVE; // default was 8
   public static final int DEFAULT_MIN_IDLE = 0;
   public static final long DEFAULT_MAX_WAIT = -1L;

   public static final boolean DEFAULT_TEST_ON_BORROW = false;
   public static final boolean DEFAULT_TEST_ON_RETURN = false;
   public static final boolean DEFAULT_TEST_WHILE_IDLE = true;

   // The default number of objects to examine per run in the idle object evictor.
   public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = DEFAULT_MAX_ACTIVE;

   public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 1000L; // (1 secs) - default -1L (infinite)
   public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 500L; // (.5 secs) - default - 1000L * 60L * 30L - 30 mins;
   public static final long DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = -1;

   public static final boolean DEFAULT_LIFO = true;

   public static final String DEFAULT_VALIDATION_QUERY = "SELECT 1 FROM DUAL";
   public static final int DEFAULT_VALIDATION_QUERY_TIMEOUT_SECS = 3; // 3 secs 

   public static final boolean DEFAULT_POOL_PREPARED_STATEMENTS = true; // default was false

   public static final int DEFAULT_MAX_TOTAL_STATEMENTS = 20; // default was 8
   public static final int DEFAULT_MAX_ACTIVE_STATEMENTS = DEFAULT_MAX_TOTAL_STATEMENTS; // default was 8
   public static final int DEFAULT_MAX_IDLE_STATEMENTS = DEFAULT_MAX_ACTIVE_STATEMENTS; // default was 8
   public static final int DEFAULT_MIN_IDLE_STATEMENTS = 0;
   public static final long DEFAULT_MAX_WAIT_STATEMENTS = -1L;

   private PoolConfigUtil() {
      // Utility class
   }

   public static boolean isPoolingPreparedStatementsAllowed(Properties props) {
      return getBoolean(props, "poolPreparedStatements", DEFAULT_POOL_PREPARED_STATEMENTS);
   }

   public static String getValidationQuery(Properties props) {
      return get(props, "validationQuery", DEFAULT_VALIDATION_QUERY);
   }

   public static int getValidationQueryTimeoutSecs(Properties props) {
      return getInt(props, "validationQueryTimeoutSecs", DEFAULT_VALIDATION_QUERY_TIMEOUT_SECS);
   }

   public static GenericObjectPool.Config getPoolConfig(Properties props) {
      GenericObjectPool.Config config = new GenericObjectPool.Config();
      //@formatter:off
      config.maxActive = getInt(props, "maxActive", DEFAULT_MAX_ACTIVE);
      config.maxIdle = getInt(props, "maxIdle", DEFAULT_MAX_IDLE);
      config.minIdle = getInt(props, "minIdle", DEFAULT_MIN_IDLE);
      config.maxWait = getLong(props, "maxWait", DEFAULT_MAX_WAIT);
      
      config.whenExhaustedAction= getByte(props, "whenExhaustedAction", DEFAULT_WHEN_EXHAUSTED_ACTION);
      config.testOnBorrow = getBoolean(props, "testOnBorrow", DEFAULT_TEST_ON_BORROW);
      config.testOnReturn = getBoolean(props, "testOnReturn", DEFAULT_TEST_ON_RETURN);
      config.testWhileIdle = getBoolean(props, "testWhileIdle", DEFAULT_TEST_WHILE_IDLE);
      config.timeBetweenEvictionRunsMillis = getLong(props, "timeBetweenEvictionRunsMillis", DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
      config.numTestsPerEvictionRun = getInt(props, "numTestsPerEvictionRun", DEFAULT_NUM_TESTS_PER_EVICTION_RUN);
      config.minEvictableIdleTimeMillis = getLong(props, "minEvictableIdleTimeMillis", DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
      config.lifo = getBoolean(props, "lifo", DEFAULT_LIFO);
      
      config.softMinEvictableIdleTimeMillis = getLong(props, "softMinEvictableIdleTimeMillis", DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
      //@formatter:on

      return config;
   }

   public static GenericKeyedObjectPool.Config getStatementPoolConfig(Properties props) {
      GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();

      //@formatter:off
      config.maxTotal = getInt(props, "maxTotalPreparedStatements", DEFAULT_MAX_TOTAL_STATEMENTS);
      config.maxActive = getInt(props, "maxActivePreparedStatements", DEFAULT_MAX_ACTIVE_STATEMENTS);
      config.maxIdle = getInt(props, "maxIdlePreparedStatements", DEFAULT_MAX_IDLE_STATEMENTS);
      config.minIdle = getInt(props, "minIdlePreparedStatements", DEFAULT_MIN_IDLE_STATEMENTS);
      config.maxWait = getLong(props, "maxWaitPreparedStatements", DEFAULT_MAX_WAIT_STATEMENTS);
      
      // Same as Connection Pool
      config.whenExhaustedAction= getByte(props, "whenExhaustedAction", DEFAULT_WHEN_EXHAUSTED_ACTION);
      config.testOnBorrow = getBoolean(props, "testOnBorrow", DEFAULT_TEST_ON_BORROW);
      config.testOnReturn = getBoolean(props, "testOnReturn", DEFAULT_TEST_ON_RETURN);
      config.testWhileIdle = getBoolean(props, "testWhileIdle", DEFAULT_TEST_WHILE_IDLE);
      config.timeBetweenEvictionRunsMillis = getLong(props, "timeBetweenEvictionRunsMillis", DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
      config.numTestsPerEvictionRun = getInt(props, "numTestsPerEvictionRun", DEFAULT_NUM_TESTS_PER_EVICTION_RUN);
      config.minEvictableIdleTimeMillis = getLong(props, "minEvictableIdleTimeMillis", DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
      config.lifo = getBoolean(props, "lifo", DEFAULT_LIFO);
      //@formatter:on

      return config;
   }

   private static String get(Properties props, String key, String defaultValue) {
      return props.getProperty(key, defaultValue);
   }

   private static int getInt(Properties props, String key, int defaultValue) {
      return Integer.valueOf(get(props, key, String.valueOf(defaultValue)));
   }

   private static long getLong(Properties props, String key, long defaultValue) {
      return Long.valueOf(get(props, key, String.valueOf(defaultValue)));
   }

   private static boolean getBoolean(Properties props, String key, boolean defaultValue) {
      return Boolean.valueOf(get(props, key, String.valueOf(defaultValue)));
   }

   private static byte getByte(Properties props, String key, byte defaultWhenExhaustedAction) {
      return Byte.valueOf(get(props, key, String.valueOf(defaultWhenExhaustedAction)));
   }
}
