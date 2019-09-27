/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.Properties;
import org.eclipse.osee.framework.jdk.core.util.Lib;

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

   private String oseeUserEmail;
   private String oseeUserName;
   private String oseeUserId;
   private boolean isOseeUserActive;
   private String oseeApplicationServerDataPath;
   private String oseeAuthenticationProtocol;

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

   public UserToken getUserToken() {
      return getGrantedUserToken();
   }

   public String getAuthenticationProtocol() {
      return oseeAuthenticationProtocol;
   }

   public void setAuthenticationProtocol(String protocol) {
      this.oseeAuthenticationProtocol = protocol;
   }

   private UserToken getGrantedUserToken() {
      return UserToken.create(Lib.generateArtifactIdAsInt(), oseeUserName, oseeUserEmail, oseeUserId, isOseeUserActive);
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

   public String getOseeUserEmail() {
      return oseeUserEmail;
   }

   public void setOseeUserEmail(String oseeUserEmail) {
      this.oseeUserEmail = oseeUserEmail;
   }

   public String getOseeUserName() {
      return oseeUserName;
   }

   public void setOseeUserName(String oseeUserName) {
      this.oseeUserName = oseeUserName;
   }

   public String getOseeUserId() {
      return oseeUserId;
   }

   public void setOseeUserId(String oseeUserId) {
      this.oseeUserId = oseeUserId;
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

   public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
   }

   public boolean isDbIsProduction() {
      return dbIsProduction;
   }

   public void setDbIsProduction(boolean dbIsProduction) {
      this.dbIsProduction = dbIsProduction;
   }

   public boolean isOseeUserActive() {
      return isOseeUserActive;
   }

   public void setOseeUserActive(boolean isOseeUserActive) {
      this.isOseeUserActive = isOseeUserActive;
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
