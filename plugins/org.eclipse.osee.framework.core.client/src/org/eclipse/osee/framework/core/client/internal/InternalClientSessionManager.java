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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.GuestCredentialProvider;
import org.eclipse.osee.framework.core.client.ICredentialProvider;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.data.OseeClientInfo;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;

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

   public IDatabaseInfo getDatabaseInfo() throws OseeAuthenticationRequiredException {
      return getOseeSessionGrant().getDatabaseInfo();
   }

   public OseeSessionGrant getOseeSessionGrant() throws OseeAuthenticationRequiredException {
      ensureSessionCreated();
      if (isSessionValid()) {
         return oseeSessionGrant;
      }
      throw new OseeAuthenticationRequiredException("Session is invalid - authentication is required");
   }

   public IdeClientSession getOseeSession() throws OseeAuthenticationRequiredException {
      ensureSessionCreated();
      if (isSessionValid()) {
         return oseeSession;
      }
      throw new OseeAuthenticationRequiredException("Session is invalid - authentication is required");
   }

   public void authenticateAsGuest() throws OseeCoreException {
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

   public synchronized void authenticate(ICredentialProvider credentialProvider) throws OseeCoreException {
      if (!isSessionValid()) {
         try {
            OseeCredential credential = credentialProvider.getCredential();
            clearData();
            oseeSessionGrant = internalAcquireSession(credential);
            if (oseeSessionGrant == null) {
               return;
            }
            oseeSession = new IdeClientSession();
            oseeSession.setId("Invalid");
            oseeSession.setClientName(clientInfo.getClientMachineName());
            oseeSession.setClientAddress(clientInfo.getClientAddress());
            oseeSession.setUserId(oseeSessionGrant.getUserToken().getUserId());
            oseeSession.setClientPort(String.valueOf(clientInfo.getPort()));
            oseeSession.setClientVersion(clientInfo.getVersion());
            oseeSession.setAuthenticationProtocol(oseeSessionGrant.getAuthenticationProtocol());
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
                  credential.setDomain("");
                  credential.setPassword("");
                  return credential;
               }
            });
         } catch (Exception ex) {
            OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
            try {
               authenticateAsGuest();
            } catch (Exception ex1) {
               OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex1);
            }
         }
      }
   }

   public void releaseSession() throws OseeCoreException {
      if (isSessionValid()) {
         internalReleaseSession(getOseeSessionGrant().getSessionId());
      }
   }

   private void internalReleaseSession(String sessionId) throws OseeCoreException {
      Map<String, String> parameters = new HashMap<>();
      parameters.put("operation", "release");
      parameters.put("sessionId", sessionId);
      try {
         String url =
            HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.SESSION_CONTEXT, parameters);
         if (Strings.isValid(url)) {
            String reponse = HttpProcessor.post(new URL(url));
            OseeLog.log(CoreClientActivator.class, Level.INFO, reponse);
            clearData();
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   public List<String> getAuthenticationProtocols() {
      List<String> toReturn = new ArrayList<>();
      try {
         Map<String, String> parameters = new HashMap<>();
         String url =
            HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.SESSION_CONTEXT, parameters);
         if (Strings.isValid(url)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            AcquireResult result = HttpProcessor.acquire(new URL(url), outputStream);
            if (result.getCode() == HttpURLConnection.HTTP_OK) {
               String protocols = outputStream.toString("UTF-8");
               if (Strings.isValid(protocols)) {
                  String[] results = protocols.split("[\\[\\]\\s,]+");
                  for (String entry : results) {
                     if (Strings.isValid(entry)) {
                        toReturn.add(entry);
                     }
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

   private void clearData() {
      this.oseeSession = null;
      this.oseeSessionGrant = null;
   }

   private OseeSessionGrant internalAcquireSession(OseeCredential credential) throws OseeCoreException, MalformedURLException {
      OseeSessionGrant session = null;
      Map<String, String> parameters = new HashMap<>();
      parameters.put("operation", "create");
      String url =
         HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.SESSION_CONTEXT, parameters);

      if (Strings.isValid(url)) {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result =
            HttpProcessor.post(new URL(url), asInputStream(credential), "text/xml", "UTF-8", outputStream);
         if (result.getCode() == HttpURLConnection.HTTP_ACCEPTED) {
            session = fromEncryptedBytes(outputStream.toByteArray());
         } else {
            throw new OseeCoreException("Error during create session request - code [%s]\n%s", result.getCode(),
               outputStream.toString());
         }
      }
      return session;
   }

   private static ByteArrayInputStream asInputStream(OseeCredential credential) throws OseeCoreException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      credential.write(outputStream);

      //TODO ENCRYPT DATA

      return new ByteArrayInputStream(outputStream.toByteArray());
   }

   private static OseeSessionGrant fromEncryptedBytes(byte[] rawData) throws OseeCoreException {
      OseeSessionGrant session = null;
      InputStream inputStream = null;
      try {
         //TODO DECRYPT DATA
         inputStream = new ByteArrayInputStream(rawData);
         session = OseeSessionGrant.fromXml(inputStream);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
         }
      }
      return session;
   }

}
