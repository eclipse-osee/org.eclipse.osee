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
package org.eclipse.osee.framework.database.core;

import java.util.Random;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.internal.ServiceUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class JoinUtility {

   private static final Long DEFAULT_JOIN_EXPIRATION_SECONDS = 3L * 60L * 60L; // 3 hours

   private static final String EXPIRATION_SECS__ARTIFACT_JOIN_QUERY = "artifact.join.expiration.secs";
   private static final String EXPIRATION_SECS__CHAR_JOIN_QUERY = "char.join.expiration.secs";
   private static final String EXPIRATION_SECS__ID_JOIN_QUERY = "id.join.expiration.secs";
   private static final String EXPIRATION_SECS__TX_JOIN_QUERY = "tx.join.expiration.secs";

   private static final Random random = new Random();

   private JoinUtility() {
      // Utility Class
   }

   private static int getNewQueryId() {
      return random.nextInt();
   }

   private static IJoinAccessor createAccessor(IOseeDatabaseService service) {
      return new DatabaseJoinAccessor(service);
   }

   public static CharJoinQuery createCharJoinQuery(IOseeDatabaseService service) {
      return createCharJoinQuery(service, null);
   }

   public static CharJoinQuery createCharJoinQuery(IOseeDatabaseService service, Long expiresIn) {
      Long actualExpiration = getExpiresIn(service, expiresIn, EXPIRATION_SECS__CHAR_JOIN_QUERY);
      return new CharJoinQuery(createAccessor(service), actualExpiration, getNewQueryId());
   }

   public static IdJoinQuery createIdJoinQuery(IOseeDatabaseService service) {
      return createIdJoinQuery(service, null);
   }

   public static IdJoinQuery createIdJoinQuery(IOseeDatabaseService service, Long expiresIn) {
      Long actualExpiration = getExpiresIn(service, expiresIn, EXPIRATION_SECS__ID_JOIN_QUERY);
      return new IdJoinQuery(createAccessor(service), actualExpiration, getNewQueryId());
   }

   public static ArtifactJoinQuery createArtifactJoinQuery(IOseeDatabaseService service) {
      return createArtifactJoinQuery(service, null);
   }

   public static ArtifactJoinQuery createArtifactJoinQuery(IOseeDatabaseService service, Long expiresIn) {
      Long actualExpiration = getExpiresIn(service, expiresIn, EXPIRATION_SECS__ARTIFACT_JOIN_QUERY);
      return new ArtifactJoinQuery(createAccessor(service), actualExpiration, getNewQueryId(),
         getMaxArtifactJoinSize(service));
   }

   public static TransactionJoinQuery createTransactionJoinQuery(IOseeDatabaseService service) {
      return createTransactionJoinQuery(service, null);
   }

   public static TransactionJoinQuery createTransactionJoinQuery(IOseeDatabaseService service, Long expiresIn) {
      Long actualExpiration = getExpiresIn(service, expiresIn, EXPIRATION_SECS__TX_JOIN_QUERY);
      return new TransactionJoinQuery(createAccessor(service), actualExpiration, getNewQueryId());
   }

   ////////////////// Static Legacy Calls /////////////////////////
   private static IOseeDatabaseService getDatabase() throws OseeDataStoreException {
      return ServiceUtil.getDatabaseService();
   }

   public static IdJoinQuery createIdJoinQuery() throws OseeDataStoreException {
      return createIdJoinQuery(getDatabase());
   }

   public static ArtifactJoinQuery createArtifactJoinQuery() {
      IOseeDatabaseService service = getDatabase();
      return createArtifactJoinQuery(service);
   }

   public static TransactionJoinQuery createTransactionJoinQuery() {
      IOseeDatabaseService service = getDatabase();
      return createTransactionJoinQuery(service);
   }

   private static int getMaxArtifactJoinSize(IOseeDatabaseService service) {
      int toReturn = Integer.MAX_VALUE;
      String maxSize = OseeInfo.getValue(service, "artifact.join.max.size");
      if (Strings.isNumeric(maxSize)) {
         toReturn = Integer.parseInt(maxSize);
      }
      return toReturn;
   }

   private static Long getExpiresIn(IOseeDatabaseService service, Long actual, String defaultKey) {
      Long toReturn = DEFAULT_JOIN_EXPIRATION_SECONDS;
      if (actual != null) {
         toReturn = actual;
      } else {
         String expiration = OseeInfo.getValue(service, defaultKey);
         if (Strings.isNumeric(expiration)) {
            toReturn = Long.parseLong(expiration);
         }
      }
      return toReturn;
   }
}
