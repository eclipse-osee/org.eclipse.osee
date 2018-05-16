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
package org.eclipse.osee.framework.core.client.internal;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.GuestCredentialProvider;
import org.eclipse.osee.framework.core.client.ICredentialProvider;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.data.OseeClientInfo;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.server.ide.api.SessionEndpoint;
import org.eclipse.osee.jaxrs.client.JaxRsClient;

/**
 * @author Roberto E. Escobar
 */
public class InternalClientSessionManager {
   public static final String STATUS_ID = "Session Manager";
   private static final InternalClientSessionManager instance = new InternalClientSessionManager();

   private final OseeClientInfo clientInfo;
   private OseeSessionGrant oseeSessionGrant;
   private IdeClientSession oseeSession;

   private InternalClientSessionManager() {
      clearData();
      this.clientInfo = new OseeClientInfo();
      clientInfo.setClientAddress(HttpServer.getServerAddressForExternalCommunication(),
         HttpServer.getDefaultServicePort());
      clientInfo.setClientVersion(OseeCodeVersion.getVersion());
      try {
         clientInfo.setClientMachineName(InetAddress.getLocalHost().getHostName());
      } catch (Exception ex) {
         clientInfo.setClientMachineName(clientInfo.getClientAddress());
      }
   }

   public static InternalClientSessionManager getInstance() {
      return instance;
   }

   public boolean isSessionValid() {
      return oseeSession != null;
   }

   public OseeSessionGrant getDatabaseInfo() {
      return getOseeSessionGrant();
   }

   public OseeSessionGrant getOseeSessionGrant() {
      ensureSessionCreated();
      if (isSessionValid()) {
         return oseeSessionGrant;
      }
      throw new OseeAuthenticationRequiredException("Session is invalid - authentication is required");
   }

   public IdeClientSession getOseeSession() {
      ensureSessionCreated();
      if (isSessionValid()) {
         return oseeSession;
      }
      throw new OseeAuthenticationRequiredException("Session is invalid - authentication is required");
   }

   public void authenticateAsGuest() {
      authenticate(new GuestCredentialProvider());
   }

   public IdeClientSession getSafeSession() {
      IdeClientSession session = new IdeClientSession();
      session.setId("Invalid");
      session.setClientName(clientInfo.getClientMachineName());
      session.setClientAddress(clientInfo.getClientAddress());
      session.setUserId("N/A");
      session.setClientPort(String.valueOf(clientInfo.getPort()));
      session.setClientVersion(clientInfo.getVersion());
      session.setAuthenticationProtocol("N/A");
      return session;
   }

   public synchronized void authenticate(ICredentialProvider credentialProvider) {
      if (!isSessionValid()) {
         try {
            OseeCredential credential = credentialProvider.getCredential();
            clearData();
            oseeSessionGrant = internalAcquireSession(credential);
            if (oseeSessionGrant == null) {
               return;
            }
            oseeSession = new IdeClientSession();
            oseeSession.setId(oseeSessionGrant.getSessionId());
            oseeSession.setClientName(clientInfo.getClientMachineName());
            oseeSession.setClientAddress(clientInfo.getClientAddress());
            oseeSession.setUserId(oseeSessionGrant.getUserToken().getUserId());
            oseeSession.setClientPort(String.valueOf(clientInfo.getPort()));
            oseeSession.setClientVersion(clientInfo.getVersion());
            oseeSession.setAuthenticationProtocol(oseeSessionGrant.getAuthenticationProtocol());
            oseeSession.setUseOracleHints(String.valueOf(oseeSessionGrant.getUseOracleHints()));
         } catch (Exception ex) {
            OseeLog.reportStatus(new BaseStatus(STATUS_ID, Level.SEVERE, ex));
            OseeCoreException.wrapAndThrow(ex);
         }
         OseeLog.reportStatus(new BaseStatus(STATUS_ID, Level.INFO, "%s", oseeSession));
      }
   }

   public void ensureSessionCreated() {
      if (!isSessionValid()) {
         try {
            authenticate(new BaseCredentialProvider() {
               @Override
               public OseeCredential getCredential() {
                  OseeCredential credential = super.getCredential();
                  String userName;
                  if (OseeClientProperties.isInDbInit()) {
                     userName = SystemUser.BootStrap.getName();
                  } else {
                     userName = System.getProperty("user.name", SystemUser.Anonymous.getName());
                     if (Strings.isValid(userName)) {
                        userName = userName.toLowerCase();
                     }
                  }
                  credential.setUserName(userName);
                  credential.setPassword("");
                  return credential;
               }
            });
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            try {
               authenticateAsGuest();
            } catch (Exception ex1) {
               OseeLog.log(Activator.class, Level.SEVERE, ex1);
            }
         }
      }
   }

   public void releaseSession() {
      if (isSessionValid()) {
         internalReleaseSession(getOseeSessionGrant().getSessionId());
      }
   }

   private void internalReleaseSession(String sessionId) {
      try {
         SessionEndpoint sessionEp = getSessionEp();
         Response response = sessionEp.releaseIdeClientSession(sessionId);
         if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new OseeCoreException("Unable to Release Session " + response.toString());
         }
         clearData();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public List<String> getAuthenticationProtocols() {
      List<String> toReturn = new ArrayList<>();
      try {
         SessionEndpoint sessionEp = getSessionEp();
         Response response = sessionEp.getIdeClientProtocols();
         @SuppressWarnings("unchecked")
         List<String> protocols = response.readEntity(LinkedList.class);
         toReturn.addAll(protocols);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

   private void clearData() {
      this.oseeSession = null;
      this.oseeSessionGrant = null;
   }

   private OseeSessionGrant internalAcquireSession(OseeCredential credential) {
      SessionEndpoint sessionEp = getSessionEp();
      Response response = sessionEp.createIdeClientSession(credential);
      OseeSessionGrant sessionGrant = response.readEntity(OseeSessionGrant.class);
      return sessionGrant;
   }

   private static SessionEndpoint getSessionEp() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      String atsUri = String.format("%s/ide", appServer);
      JaxRsClient jaxRsClient = JaxRsClient.newBuilder().createThreadSafeProxyClients(true).build();
      return jaxRsClient.target(atsUri).newProxy(SessionEndpoint.class);
   }

}
