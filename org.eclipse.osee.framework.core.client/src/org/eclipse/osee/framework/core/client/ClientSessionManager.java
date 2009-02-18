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

import org.eclipse.osee.framework.core.client.internal.InternalClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeUser;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ClientSessionManager {

   private ClientSessionManager() {
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

   public static OseeClientSession getSession() throws OseeAuthenticationRequiredException {
      return InternalClientSessionManager.getInstance().getOseeSession();
   }

   public static boolean isUserCreationRequired() throws OseeAuthenticationRequiredException {
      return getSessionGrant().isCreationRequired();
   }

   public static void clearUserCreationRequired() throws OseeAuthenticationRequiredException {
      getSessionGrant().setCreationRequired(false);
   }

   public static IOseeUser getCurrentUserInfo() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getOseeUserInfo();
   }

   public static String getSessionId() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getSessionId();
   }

   public static String getDataStoreLoginName() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getDatabaseInfo().getDatabaseLoginName();
   }

   public static String getDataStoreName() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getDatabaseInfo().getDatabaseName();
   }

   public static String getDataStorePath() throws OseeAuthenticationRequiredException {
      return getSessionGrant().getDataStorePath();
   }

   public static boolean isProductionDataStore() throws OseeAuthenticationRequiredException {
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
      return InternalClientSessionManager.getInstance().getAuthenticationProtocols();
   }

   public static void authenticateAsGuest() throws OseeCoreException {
      InternalClientSessionManager.getInstance().authenticateAsGuest();
   }

   public static void authenticate(ICredentialProvider credentialProvider) throws OseeCoreException {
      InternalClientSessionManager.getInstance().authenticate(credentialProvider);
   }

   public static void releaseSession() throws OseeCoreException {
      InternalClientSessionManager.getInstance().releaseSession();
   }

}
