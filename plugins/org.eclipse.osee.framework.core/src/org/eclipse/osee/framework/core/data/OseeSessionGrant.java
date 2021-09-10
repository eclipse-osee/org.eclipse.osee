/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.Properties;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class OseeSessionGrant {

   private String sessionId;
   private String dbDriver;
   private String dbUrl;
   private String dbLogin;
   private String dbDatabaseName;
   private boolean dbIsProduction;
   private String dbDatabasePath;
   private String dbId;

   private String oseeApplicationServerDataPath;
   private String oseeAuthenticationProtocol;
   private UserToken userToken;

   private Properties sqlProperties;
   private Properties dbConnectionProperties;
   private String useOracleHints;

   public OseeSessionGrant() {
      super();
   }

   public OseeSessionGrant(String sessionId) {
      super();
      this.sessionId = sessionId;
   }

   public String getSessionId() {
      return sessionId;
   }

   public void setSqlProperties(Properties sqlProperties) {
      this.sqlProperties = sqlProperties;
   }

   public Properties getSqlProperties() {
      return this.sqlProperties;
   }

   public void setDataStorePath(String oseeApplicationServerData) {
      this.oseeApplicationServerDataPath = oseeApplicationServerData;
   }

   public String getDataStorePath() {
      return oseeApplicationServerDataPath;
   }

   public String getAuthenticationProtocol() {
      return oseeAuthenticationProtocol;
   }

   public void setAuthenticationProtocol(String protocol) {
      this.oseeAuthenticationProtocol = protocol;
   }

   public String getDbDriver() {
      return dbDriver;
   }

   public void setDbDriver(String dbDriver) {
      this.dbDriver = dbDriver;
   }

   public String getDbLogin() {
      return dbLogin;
   }

   public void setDbLogin(String dbLogin) {
      this.dbLogin = dbLogin;
   }

   public String getDbDatabaseName() {
      return dbDatabaseName;
   }

   public void setDbDatabaseName(String dbDatabaseName) {
      this.dbDatabaseName = dbDatabaseName;
   }

   public String getDbDatabasePath() {
      return dbDatabasePath;
   }

   public void setDbDatabasePath(String dbDatabasePath) {
      this.dbDatabasePath = dbDatabasePath;
   }

   public String getDbId() {
      return dbId;
   }

   public void setDbId(String dbId) {
      this.dbId = dbId;
   }

   public String getOseeApplicationServerDataPath() {
      return oseeApplicationServerDataPath;
   }

   public void setOseeApplicationServerDataPath(String oseeApplicationServerDataPath) {
      this.oseeApplicationServerDataPath = oseeApplicationServerDataPath;
   }

   public String getOseeAuthenticationProtocol() {
      return oseeAuthenticationProtocol;
   }

   public void setOseeAuthenticationProtocol(String oseeAuthenticationProtocol) {
      this.oseeAuthenticationProtocol = oseeAuthenticationProtocol;
   }

   public UserToken getUserToken() {
      return userToken;
   }

   public void setUserToken(UserToken userToken) {
      this.userToken = userToken;
   }

   public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
   }

   public boolean isDbIsProduction() {
      return dbIsProduction;
   }

   public void setDbIsProduction(boolean dbIsProduction) {
      this.dbIsProduction = dbIsProduction;
   }

   public void setDbUrl(String dbUrl) {
      this.dbUrl = dbUrl;
   }

   public Properties getDbConnectionProperties() {
      return dbConnectionProperties;
   }

   public void setDbConnectionProperties(Properties dbConnectionProperties) {
      this.dbConnectionProperties = dbConnectionProperties;
   }

   public String getDbUrl() {
      return dbUrl;
   }

   public void setUseOracleHints(String useOracleHints) {
      this.useOracleHints = useOracleHints;
   }

   public String getUseOracleHints() {
      return useOracleHints;
   }

}
