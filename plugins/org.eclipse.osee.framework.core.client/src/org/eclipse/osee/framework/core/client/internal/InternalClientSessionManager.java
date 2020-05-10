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

package org.eclipse.osee.framework.core.client.internal;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.AnonymousCredentialProvider;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
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
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.server.ide.api.SessionEndpoint;
import org.eclipse.osee.orcs.rest.client.OseeClient;

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

   public void authenticateAsAnonymous() {
      authenticate(new AnonymousCredentialProvider());
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
            oseeSessionGrant = getSessionEndpoint().createIdeClientSession(credential);
            if (oseeSessionGrant == null) {
               return;
            } else if (SystemUser.UnAuthenticated.getUserId().equals(oseeSessionGrant.getUserToken().getUserId())) {
               throw new OseeArgumentException("User [%s] is not authenticated.", credential.getUserName());
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
                  }
                  credential.setUserName(userName);
                  credential.setPassword("");
                  return credential;
               }
            });
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            try {
               authenticateAsAnonymous();
            } catch (Exception ex1) {
               OseeLog.log(Activator.class, Level.SEVERE, ex1);
            }
         }
      }
   }

   public void releaseSession() {
      if (isSessionValid()) {
         getSessionEndpoint().releaseIdeClientSession(getOseeSessionGrant().getSessionId());
         clearData();
      }
   }

   public List<String> getAuthenticationProtocols() {
      return getSessionEndpoint().getIdeClientProtocols();
   }

   private void clearData() {
      this.oseeSession = null;
      this.oseeSessionGrant = null;
   }

   private SessionEndpoint getSessionEndpoint() {
      return OsgiUtil.getService(getClass(), OseeClient.class).getSessionEndpoint();
   }
}