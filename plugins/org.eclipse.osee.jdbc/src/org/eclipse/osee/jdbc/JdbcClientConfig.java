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

import static org.eclipse.osee.jdbc.JdbcConstants.DB_PASSWORD_KEY;
import static org.eclipse.osee.jdbc.JdbcConstants.DB_USERNAME_KEY;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC__CONNECTION_APPEND_PROPS_TO_URI;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC__CONNECTION_DRIVER;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC__CONNECTION_PASSWORD;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC__CONNECTION_URI;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC__CONNECTION_USERNAME;
import static org.eclipse.osee.jdbc.JdbcConstants.DEFAULT_JDBC__IS_PRODUCTION_DB;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__CONNECTION_APPEND_PROPS_TO_URI;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__CONNECTION_DRIVER;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__CONNECTION_PASSWORD;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__CONNECTION_URI;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__CONNECTION_USERNAME;
import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__IS_PRODUCTION_DB;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.get;
import static org.eclipse.osee.jdbc.internal.JdbcUtil.getBoolean;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.eclipse.osee.jdbc.internal.JdbcUtil;

/**
 * @author Roberto E. Escobar
 */
public class JdbcClientConfig {

   private String dbDriver;
   private String dbUri;
   private boolean production;
   private boolean dbAppendPropsToUri;
   private final Properties dbProps = new Properties();
   private final JdbcPoolConfig poolConfig = new JdbcPoolConfig();

   JdbcClientConfig() {
      super();
      reset();
   }

   public boolean isProduction() {
      return production;
   }

   public String getDbDriver() {
      return dbDriver;
   }

   public String getDbUri() {
      return dbUri;
   }

   public boolean isDbAppendPropsToUri() {
      return dbAppendPropsToUri;
   }

   public String getDbUsername() {
      return (String) dbProps.get(DB_USERNAME_KEY);
   }

   public String getDbPassword() {
      return (String) dbProps.get(DB_PASSWORD_KEY);
   }

   public Properties getDbProps() {
      Properties unmodifiable = new Properties();
      unmodifiable.putAll(dbProps);
      return unmodifiable;
   }

   public JdbcPoolConfig getPoolConfig() {
      return poolConfig;
   }

   void setProduction(boolean production) {
      this.production = production;
   }

   void setDbDriver(String dbDriver) {
      this.dbDriver = dbDriver;
   }

   void setDbUri(String dbUri) {
      this.dbUri = dbUri;
   }

   void setDbAppendPropsToUri(boolean dbAppendPropsToUri) {
      this.dbAppendPropsToUri = dbAppendPropsToUri;
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
         dbProps.put(key, value);
      }
   }

   void removeDbParam(String key) {
      dbProps.remove(key);
   }

   private void reset() {
      dbProps.clear();
      setProduction(DEFAULT_JDBC__IS_PRODUCTION_DB);
      setDbDriver(DEFAULT_JDBC__CONNECTION_DRIVER);
      setDbUri(DEFAULT_JDBC__CONNECTION_URI);
      setDbUsername(DEFAULT_JDBC__CONNECTION_USERNAME);
      setDbPassword(DEFAULT_JDBC__CONNECTION_PASSWORD);
      setDbAppendPropsToUri(DEFAULT_JDBC__CONNECTION_APPEND_PROPS_TO_URI);

      getPoolConfig().reset();
   }

   void readProperties(Map<String, Object> src) {
      setProduction(getBoolean(src, JDBC__IS_PRODUCTION_DB, DEFAULT_JDBC__IS_PRODUCTION_DB));
      setDbDriver(get(src, JDBC__CONNECTION_DRIVER, DEFAULT_JDBC__CONNECTION_DRIVER));
      setDbUri(get(src, JDBC__CONNECTION_URI, DEFAULT_JDBC__CONNECTION_URI));
      setDbUsername(get(src, JDBC__CONNECTION_USERNAME, DEFAULT_JDBC__CONNECTION_USERNAME));
      setDbPassword(get(src, JDBC__CONNECTION_PASSWORD, DEFAULT_JDBC__CONNECTION_PASSWORD));
      setDbAppendPropsToUri(getBoolean(src, JDBC__CONNECTION_APPEND_PROPS_TO_URI,
         DEFAULT_JDBC__CONNECTION_APPEND_PROPS_TO_URI));

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

      getPoolConfig().readProperties(src);
   }

   protected JdbcClientConfig copy() {
      JdbcClientConfig data = new JdbcClientConfig();
      data.production = this.production;
      data.dbDriver = this.dbDriver;
      data.dbUri = this.dbUri;
      data.dbAppendPropsToUri = this.dbAppendPropsToUri;
      data.poolConfig.copy(this.poolConfig);
      data.dbProps.clear();
      data.dbProps.putAll(this.dbProps);
      return data;
   }

   protected void copy(JdbcClientConfig other) {
      this.production = other.production;
      this.dbDriver = other.dbDriver;
      this.dbUri = other.dbUri;
      this.dbAppendPropsToUri = other.dbAppendPropsToUri;
      this.poolConfig.copy(other.poolConfig);
      this.dbProps.clear();
      this.dbProps.putAll(other.dbProps);
   }

   @Override
   public String toString() {
      return "JdbcClientConfig [dbDriver=" + dbDriver + ", dbUri=" + dbUri + ", production=" + production + ", dbAppendPropsToUri=" + dbAppendPropsToUri + ", dbProps=" + dbProps + ", poolConfig=" + poolConfig + "]";
   }

}
