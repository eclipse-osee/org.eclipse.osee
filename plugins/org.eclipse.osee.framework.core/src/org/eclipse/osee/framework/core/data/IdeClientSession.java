/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * @author Donald G. Dunne
 */
public class IdeClientSession {

   private String id;
   private String clientAddress;
   private String clientPort;
   private String userId;
   private String clientVersion;
   private String sessionId;
   private String createdOn;
   private String sessionLog;
   private String authenticationProtocol;
   private String clientName;
   private String useOracleHints;

   public IdeClientSession() {
      // For JAX-RS instantiation
   }

   public IdeClientSession(String clientAddress, String clientPort, String userId, String clientVersion, String sessionId, String createdOn) {
      this.clientAddress = clientAddress;
      this.clientPort = clientPort;
      this.userId = userId;
      this.clientVersion = clientVersion;
      this.sessionId = sessionId;
      this.createdOn = createdOn;
   }

   public String getClientAddress() {
      return clientAddress;
   }

   public String getClientPort() {
      return clientPort;
   }

   public String getUserId() {
      return userId;
   }

   public String getClientVersion() {
      return clientVersion;
   }

   public String getSessionId() {
      return sessionId;
   }

   public String getCreatedOn() {
      return createdOn;
   }

   public String getSessionLog() {
      return sessionLog;
   }

   public void setSessionLog(String sessionLog) {
      this.sessionLog = sessionLog;
   }

   public String getAuthenticationProtocol() {
      return authenticationProtocol;
   }

   public void setAuthenticationProtocol(String authenticationProtocol) {
      this.authenticationProtocol = authenticationProtocol;
   }

   public void setClientAddress(String clientAddress) {
      this.clientAddress = clientAddress;
   }

   public void setClientPort(String clientPort) {
      this.clientPort = clientPort;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public void setClientVersion(String clientVersion) {
      this.clientVersion = clientVersion;
   }

   public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
   }

   public void setCreatedOn(String createdOn) {
      this.createdOn = createdOn;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getClientName() {
      return clientName;
   }

   public void setClientName(String clientName) {
      this.clientName = clientName;
   }

   @Override
   public String toString() {
      return "IdeClientSession [id=" + id + ", clientAddress=" + clientAddress + ", clientPort=" + clientPort + ", userId=" + userId + ", clientVersion=" + clientVersion + ", sessionId=" + sessionId + ", createdOn=" + createdOn + ", sessionLog=" + sessionLog + ", authenticationProtocol=" + authenticationProtocol + ", clientName=" + clientName + "]";
   }

   public String getUseOracleHints() {
      return useOracleHints;
   }

   public void setUseOracleHints(String useOracleHints) {
      this.useOracleHints = useOracleHints;
   }

}