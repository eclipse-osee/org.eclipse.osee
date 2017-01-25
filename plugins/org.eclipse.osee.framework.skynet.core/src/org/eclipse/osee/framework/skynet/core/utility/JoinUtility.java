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
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Random;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

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

   private static IJoinAccessor createAccessor(JdbcClient jdbcClient) {
      return new DatabaseJoinAccessor(jdbcClient);
   }

   public static CharJoinQuery createCharJoinQuery(JdbcClient jdbcClient) {
      return createCharJoinQuery(jdbcClient, null);
   }

   public static CharJoinQuery createCharJoinQuery(JdbcClient jdbcClient, Long expiresIn) {
      Long actualExpiration = getExpiresIn(jdbcClient, expiresIn, EXPIRATION_SECS__CHAR_JOIN_QUERY);
      return new CharJoinQuery(createAccessor(jdbcClient), actualExpiration, getNewQueryId());
   }

   public static IdJoinQuery createIdJoinQuery(JdbcClient jdbcClient) {
      return createIdJoinQuery(jdbcClient, null);
   }

   public static IdJoinQuery createIdJoinQuery(JdbcClient jdbcClient, Long expiresIn) {
      Long actualExpiration = getExpiresIn(jdbcClient, expiresIn, EXPIRATION_SECS__ID_JOIN_QUERY);
      return new IdJoinQuery(createAccessor(jdbcClient), actualExpiration, getNewQueryId());
   }

   public static Id4JoinQuery createId4JoinQuery(JdbcClient jdbcClient) {
      return createId4JoinQuery(jdbcClient, null);
   }

   public static Id4JoinQuery createId4JoinQuery(JdbcClient jdbcClient, Long expiresIn) {
      Long actualExpiration = getExpiresIn(jdbcClient, expiresIn, EXPIRATION_SECS__ARTIFACT_JOIN_QUERY);
      return new Id4JoinQuery(createAccessor(jdbcClient), actualExpiration, getNewQueryId(),
         getMaxArtifactJoinSize(jdbcClient));
   }

   public static TransactionJoinQuery createTransactionJoinQuery(JdbcClient jdbcClient) {
      return createTransactionJoinQuery(jdbcClient, null);
   }

   public static TransactionJoinQuery createTransactionJoinQuery(JdbcClient jdbcClient, Long expiresIn) {
      Long actualExpiration = getExpiresIn(jdbcClient, expiresIn, EXPIRATION_SECS__TX_JOIN_QUERY);
      return new TransactionJoinQuery(createAccessor(jdbcClient), actualExpiration, getNewQueryId());
   }

   ////////////////// Static Legacy Calls /////////////////////////
   private static JdbcClient getJdbcClient() throws OseeDataStoreException {
      return ConnectionHandler.getJdbcClient();
   }

   public static IdJoinQuery createIdJoinQuery() throws OseeDataStoreException {
      return createIdJoinQuery(getJdbcClient());
   }

   public static Id4JoinQuery createId4JoinQuery() {
      JdbcClient jdbcClient = getJdbcClient();
      return createId4JoinQuery(jdbcClient);
   }

   public static TransactionJoinQuery createTransactionJoinQuery() {
      JdbcClient jdbcClient = getJdbcClient();
      return createTransactionJoinQuery(jdbcClient);
   }

   private static int getMaxArtifactJoinSize(JdbcClient jdbcClient) {
      int toReturn = Integer.MAX_VALUE;
      String maxSize = OseeInfo.getValue(jdbcClient, "artifact.join.max.size");
      if (Strings.isNumeric(maxSize)) {
         toReturn = Integer.parseInt(maxSize);
      }
      return toReturn;
   }

   private static Long getExpiresIn(JdbcClient jdbcClient, Long actual, String defaultKey) {
      Long toReturn = DEFAULT_JOIN_EXPIRATION_SECONDS;
      if (actual != null) {
         toReturn = actual;
      } else {
         String expiration = OseeInfo.getValue(jdbcClient, defaultKey);
         if (Strings.isNumeric(expiration)) {
            toReturn = Long.parseLong(expiration);
         }
      }
      return toReturn;
   }
}
