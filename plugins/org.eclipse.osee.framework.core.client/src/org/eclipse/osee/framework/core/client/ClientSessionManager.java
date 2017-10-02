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

import java.util.List;
import java.util.Properties;
import org.eclipse.osee.framework.core.client.internal.InternalClientSessionManager;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ClientSessionManager {

   private ClientSessionManager() {
      //
   }

   public static final String getStatusId() {
      return InternalClientSessionManager.STATUS_ID;
   }

   public static boolean isSessionValid() {
      return InternalClientSessionManager.getInstance().isSessionValid();
   }

   public static void ensureSessionCreated() {
      InternalClientSessionManager.getInstance().ensureSessionCreated();
   }

   private static OseeSessionGrant getSessionGrant() throws OseeAuthenticationRequiredException {
      return InternalClientSessionManager.getInstance().getOseeSessionGrant();
   }

   public static IdeClientSession getSession() throws OseeAuthenticationRequiredException {
      return InternalClientSessionManager.getInstance().getOseeSession();
   }

   public static boolean isUserCreationRequired() throws OseeAuthenticationRequiredException {
      return getSessionGrant().isCreationRequired();
   }

   public static void clearUserCreationRequired() throws OseeAuthenticationRequiredException {
      getSessionGrant().setCreationRequired(false);
   }

   public static UserToken getCurrentUserToken() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getUserToken();
   }

   public static String getSessionId() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getSessionId();
   }

   public static String getDataStoreLoginName() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getDbLogin();
   }

   public static String getDataStoreDriver() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getDbDriver();
   }

   public static String getDataStoreName() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getDbDatabaseName();
   }

   public static String getDataStorePath() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getDataStorePath();
   }

   public static boolean isProductionDataStore() throws OseeAuthenticationRequiredException {
      return getSessionGrant().isDbIsProduction();
   }

   public static boolean useOracleHints() {
      return Strings.isValid(getSessionGrant().getUseOracleHints()) ? Boolean.valueOf(
         getSessionGrant().getUseOracleHints()) : false;
   }

   public static Properties getSqlProperties() {
      return getSessionGrant().getSqlProperties();
   }

   public static List<String> getAuthenticationProtocols() {
      return InternalClientSessionManager.getInstance().getAuthenticationProtocols();
   }

   public static void authenticateAsGuest() {
      InternalClientSessionManager.getInstance().authenticateAsGuest();
   }

   public static void authenticate(ICredentialProvider credentialProvider) {
      InternalClientSessionManager.getInstance().authenticate(credentialProvider);
   }

   public static void releaseSession() {
      InternalClientSessionManager.getInstance().releaseSession();
   }

   public static String getDatabaseInfo() throws OseeAuthenticationRequiredException {
      return getSessionGrant().toString();
   }

   public static IdeClientSession getSafeSession() {
      return InternalClientSessionManager.getInstance().getSafeSession();
   }

}
