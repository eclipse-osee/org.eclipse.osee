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
package org.eclipse.osee.framework.core.client;

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
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeClientInfo;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
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
public class ClientSessionManager {
   public static final String STATUS_ID = "Session Manager";
   private static OseeSessionGrant oseeSessionGrant = null;
   private static OseeClientSession oseeSession = null;
   private static OseeClientInfo clientInfo;
   static {
      clientInfo = new OseeClientInfo();
      clientInfo.setClientAddress(HttpServer.getLocalServerAddress(), HttpServer.getDefaultServicePort());
      clientInfo.setClientVersion(OseeCodeVersion.getVersion());
      try {
         clientInfo.setClientMachineName(InetAddress.getLocalHost().getHostName());
      } catch (Exception ex) {
         clientInfo.setClientMachineName(clientInfo.getClientAddress());
      }
   }

   private ClientSessionManager() {
   }

   public static boolean isSessionValid() {
      return oseeSession != null;
   }

   public static OseeClientSession getSession() throws OseeAuthenticationRequiredException {
      if (isSessionValid()) {
         return oseeSession;
      }
      throw new OseeAuthenticationRequiredException("Session is invalid - authentication is required");
   }

   public static OseeSessionGrant getSessionGrant() throws OseeAuthenticationRequiredException {
      if (oseeSessionGrant != null) {
         return oseeSessionGrant;
      }
      throw new OseeAuthenticationRequiredException("Session is invalid - authentication is required");
   }

   public static String getSessionId() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getSessionId();
   }

   public static String getDataStoreLoginName() throws OseeCoreException {
      return getSessionGrant().getDatabaseInfo().getDatabaseLoginName();
   }

   public static String getDataStoreName() throws OseeCoreException {
      return getSessionGrant().getDatabaseInfo().getDatabaseName();
   }

   public static boolean isProductionDataStore() throws OseeCoreException {
      return getSessionGrant().getDatabaseInfo().isProduction();
   }

   public static String getSQL(String key) throws OseeCoreException {
      String sql = getSessionGrant().getSqlProperties().getProperty(key);
      if (Strings.isValid(sql)) {
         return sql;
      }
      throw new OseeArgumentException(String.format("Invalid sql key [%s]", key));
   }

   public static String[] getAuthenticationProtocols() {
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

   public synchronized static OseeClientSession authenticate(ICredentialProvider credentialProvider) throws OseeCoreException {
      try {
         OseeCredential credential = credentialProvider.getCredential();
         oseeSessionGrant = internalAcquireSession(credential);

         OseeDbConnection.setDatabaseInfo(oseeSessionGrant.getDatabaseInfo());

         oseeSession =
               new OseeClientSession(oseeSessionGrant.getSessionId(), clientInfo.getClientMachineName(),
                     oseeSessionGrant.getOseeUserInfo().getUserID(), clientInfo.getClientAddress(),
                     clientInfo.getPort(), clientInfo.getVersion());
      } catch (OseeCoreException ex) {
         OseeLog.reportStatus(new BaseStatus(STATUS_ID, Level.SEVERE, ex));
         throw ex;
      }
      OseeLog.reportStatus(new BaseStatus(STATUS_ID, Level.INFO, "%s", oseeSession));
      return oseeSession;
   }

   public static OseeClientSession authenticateAsGuest() throws OseeCoreException {
      return authenticate(new GuestCredentialProvider());
   }

   public static void releaseSession() throws OseeDataStoreException {
      if (oseeSessionGrant != null) {
         internalReleaseSession(oseeSessionGrant.getSessionId());
      }
   }

   private static void internalReleaseSession(String sessionId) throws OseeDataStoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("operation", "release");
      parameters.put("sessionId", sessionId);
      try {
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SESSION_CONTEXT, parameters);
         String reponse = HttpProcessor.post(new URL(url));
         OseeLog.log(CoreClientActivator.class, Level.INFO, reponse);
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   private static OseeSessionGrant internalAcquireSession(OseeCredential credential) throws OseeCoreException {
      OseeSessionGrant session = null;
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("operation", "create");
      try {
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SESSION_CONTEXT, parameters);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result =
               HttpProcessor.post(new URL(url), asInputStream(credential), "text/xml", "UTF-8", outputStream);
         if (result.getCode() == HttpURLConnection.HTTP_ACCEPTED) {
            session = fromEncryptedBytes(outputStream.toByteArray());
         }
      } catch (Exception ex) {
         throw new OseeAuthenticationException(ex);
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
