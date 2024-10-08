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
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.server.ide.api.SessionEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class InternalClientSessionManager {
   public static final String STATUS_ID = "Session Manager";
   private static final InternalClientSessionManager instance = new InternalClientSessionManager();

   private OseeSessionGrant oseeSessionGrant;
   private IdeClientSession oseeSession;
   private SessionEndpoint sessionEndpoint;
   private final UserService userService;

   private InternalClientSessionManager() {
      userService = OsgiUtil.getService(getClass(), OseeClient.class).userService();
   }

   public static InternalClientSessionManager getInstance() {
      if (instance.sessionEndpoint == null) {
         instance.sessionEndpoint =
            OsgiUtil.getService(InternalClientSessionManager.class, OseeClient.class).getSessionEndpoint();
      }
      return instance;
   }

   public boolean isSessionValid() {
      synchronized (this) {
         return oseeSession != null;
      }
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
      try {
         session.setClientName(InetAddress.getLocalHost().getHostName());
      } catch (Exception ex) {
         session.setClientName(HttpServer.getServerAddressForExternalCommunication());
      }
      session.setClientAddress(HttpServer.getServerAddressForExternalCommunication());
      session.setUserId("N/A");
      session.setClientPort(String.valueOf(HttpServer.getDefaultServicePort()));
      session.setClientVersion(OseeCodeVersion.getVersion());
      session.setAuthenticationProtocol("N/A");
      return session;
   }

   public synchronized void authenticate(ICredentialProvider credentialProvider) {
      if (!isSessionValid()) {
         try {
            userService.setUserLoading(true);
            OseeCredential credential = credentialProvider.getCredential();
            clearData();

            /**
             * This gets us through initial dbinit; after that credential that isn't OSEE System (eg Joe Smith) should
             * authenticate normally. After initial db creation, you can move this to only if oseeSessionGrant == null
             * and you'll get Joe Smith on next db init. BUT, it doesn't work if database is deleted. Somehow have to
             * re-authenticate in AtsDbConfigBaseIde so get Joe Smith. releaseUser doesn't seem to do that.
             */
            if (OseeProperties.isInDbInit() && credential.getUserName().equals("OSEE System")) {
               oseeSessionGrant = new OseeSessionGrant();
               oseeSessionGrant.setAuthenticationProtocol(credential.getAuthenticationProtocol());
               oseeSessionGrant.setSessionId("-1");
               oseeSessionGrant.setUserToken(SystemUser.OseeSystem);
            } else {
               oseeSessionGrant = getSessionEndpoint().createIdeClientSession(credential);
            }
            if (oseeSessionGrant == null) {
               return;
            }
            oseeSession = new IdeClientSession();
            oseeSession.setId(oseeSessionGrant.getSessionId());
            oseeSession.setClientName(InetAddress.getLocalHost().getHostName());
            oseeSession.setClientAddress(HttpServer.getServerAddressForExternalCommunication());
            oseeSession.setUserId(oseeSessionGrant.getUserToken().getUserId());
            oseeSession.setClientPort(String.valueOf(HttpServer.getDefaultServicePort()));
            oseeSession.setClientVersion(OseeCodeVersion.getVersion());
            oseeSession.setAuthenticationProtocol(oseeSessionGrant.getAuthenticationProtocol());
         } catch (Exception ex) {
            OseeLog.reportStatus(new BaseStatus(STATUS_ID, Level.SEVERE, ex));
            OseeCoreException.wrapAndThrow(ex);
         } finally {
            userService.setUserLoading(false);
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
                  if (OseeProperties.isInDbInit()) {
                     userName = SystemUser.OseeSystem.getName();
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
         if (!getOseeSessionGrant().getSessionId().equals("-1")) {
            getSessionEndpoint().releaseIdeClientSession(getOseeSessionGrant().getSessionId());
         }
         clearData();
      }
   }

   public List<String> getAuthenticationProtocols() {
      userService.setUserLoading(true);
      List<String> ideClientProtocols = getSessionEndpoint().getIdeClientProtocols();
      userService.setUserLoading(false);
      return ideClientProtocols;
   }

   private void clearData() {
      synchronized (this) {
         this.oseeSession = null;
         this.oseeSessionGrant = null;
      }
   }

   private SessionEndpoint getSessionEndpoint() {
      return sessionEndpoint;
   }
}