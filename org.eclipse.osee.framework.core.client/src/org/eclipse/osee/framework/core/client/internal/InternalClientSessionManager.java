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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.GuestCredentialProvider;
import org.eclipse.osee.framework.core.client.ICredentialProvider;
import org.eclipse.osee.framework.core.client.OseeClientSession;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeClientInfo;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
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
   private OseeClientSession oseeSession;

   private InternalClientSessionManager() {
      clearData();
      this.clientInfo = new OseeClientInfo();
      clientInfo.setClientAddress(HttpServer.getLocalServerAddress(), HttpServer.getDefaultServicePort());
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

   public OseeClientSession getOseeSession() throws OseeAuthenticationRequiredException {
      ensureSessionCreated();
      if (isSessionValid()) {
         return oseeSession;
      }
      throw new OseeAuthenticationRequiredException("Session is invalid - authentication is required");
   }

   public void authenticateAsGuest() throws OseeCoreException {
      authenticate(new GuestCredentialProvider());
   }

   public synchronized OseeClientSession authenticate(ICredentialProvider credentialProvider) throws OseeCoreException {
      if (!isSessionValid()) {
         try {
            OseeCredential credential = credentialProvider.getCredential();
            clearData();
            oseeSessionGrant = internalAcquireSession(credential);
            oseeSession =
                  new OseeClientSession(oseeSessionGrant.getSessionId(), clientInfo.getClientMachineName(),
                        oseeSessionGrant.getOseeUserInfo().getUserID(), clientInfo.getClientAddress(),
                        clientInfo.getPort(), clientInfo.getVersion());
         } catch (OseeCoreException ex) {
            OseeLog.reportStatus(new BaseStatus(STATUS_ID, Level.SEVERE, ex));
            throw ex;
         }
         OseeLog.reportStatus(new BaseStatus(STATUS_ID, Level.INFO, "%s", oseeSession));
      }
      return oseeSession;
   }

   public void ensureSessionCreated() {
      if (!isSessionValid()) {
         try {
            authenticate(new BaseCredentialProvider() {
               public OseeCredential getCredential() throws OseeCoreException {
                  OseeCredential credential = super.getCredential();
                  credential.setUserName(System.getProperty("user.name"));
                  credential.setDomain("");
                  credential.setPassword("");
                  credential.setAuthenticationProtocol(OseeClientProperties.getAuthenticationProtocol());
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

   private void internalReleaseSession(String sessionId) throws OseeDataStoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("operation", "release");
      parameters.put("sessionId", sessionId);
      try {
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SESSION_CONTEXT, parameters);
         String reponse = HttpProcessor.post(new URL(url));
         OseeLog.log(CoreClientActivator.class, Level.INFO, reponse);
         oseeSession = null;
         oseeSessionGrant = null;
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public String[] getAuthenticationProtocols() {
      List<String> toReturn = new ArrayList<String>();
      try {
         Map<String, String> parameters = new HashMap<String, String>();
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SESSION_CONTEXT, parameters);
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
      } catch (Exception ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
      }
      return toReturn.toArray(new String[toReturn.size()]);
   }

   private void clearData() {
      this.oseeSession = null;
      this.oseeSessionGrant = null;
   }

   private OseeSessionGrant internalAcquireSession(OseeCredential credential) throws OseeCoreException {
      OseeSessionGrant session = null;
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("operation", "create");
      String url = null;
      try {
         url = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SESSION_CONTEXT, parameters);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result =
               HttpProcessor.post(new URL(url), asInputStream(credential), "text/xml", "UTF-8", outputStream);
         if (result.getCode() == HttpURLConnection.HTTP_ACCEPTED) {
            session = fromEncryptedBytes(outputStream.toByteArray());
         }
      } catch (IOException ex) {
         throw new OseeAuthenticationException(url, ex);
      }
      return session;
   }

   private static ByteArrayInputStream asInputStream(OseeCredential credential) throws OseeWrappedException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      credential.write(outputStream);

      //TODO ENCRYPT DATA

      return new ByteArrayInputStream(outputStream.toByteArray());
   }

   private static OseeSessionGrant fromEncryptedBytes(byte[] rawData) throws OseeWrappedException {
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
               throw new OseeWrappedException(ex);
            }
         }
      }
      return session;
   }
}
